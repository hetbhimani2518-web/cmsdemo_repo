package com.example.cms;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class alerts_student extends AppCompatActivity {

    RecyclerView recyclerView, recyclerViewAdn, recyclerViewAss, recyclerViewChat;
    DatabaseReference noticesRef, noticeadRef, alertRef, alertadRef, adminRef, facultyRef, assignmentRef, assalertRef, chatRef;
    String studentProgram, studentYear, studentSemester, studentDivision;
    String studentUID;
    TextView noNewMessages;

    private AlertChatAdapterStudent adapter;
    private List<AlertChatModelStudent> alertList = new ArrayList<>();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_alerts_student);

//        Notices
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerViewAdn = findViewById(R.id.recyclerViewNoticeAD);
        recyclerViewAdn.setLayoutManager(new LinearLayoutManager(this));

//        Assignment
        recyclerViewAss = findViewById(R.id.rvAssAlerts);
        recyclerViewAss.setLayoutManager(new LinearLayoutManager(this));

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        studentUID = user.getUid();

        FirebaseDatabase.getInstance().getReference("Students").child(studentUID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        studentProgram = snapshot.child("program").getValue(String.class);
                        studentYear = snapshot.child("year").getValue(String.class);
                        studentSemester = snapshot.child("semester").getValue(String.class);
                        studentDivision = snapshot.child("division").getValue(String.class);

                        fetchNoticeAlerts();
                        fetchNoticeAdminAlert();
                        fetchAssignmentAlerts();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });

//        Discussion
        recyclerViewChat = findViewById(R.id.rvChatAlerts);
        recyclerViewChat.setLayoutManager(new LinearLayoutManager(this));
        alertList = new ArrayList<>();
        adapter = new AlertChatAdapterStudent(alertList, this);
        recyclerViewChat.setAdapter(adapter);
        noNewMessages = findViewById(R.id.tvNoChatAlert);

        studentUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        chatRef = FirebaseDatabase.getInstance().getReference("Chats");
        fetchUnreadMessages();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(alerts_student.this, home_student.class);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(alerts_student.this, home_student.class);
        startActivity(intent);
        finish();
    }

    private void fetchUnreadMessages() {
        chatRef.orderByKey().startAt(studentUID + "_").endAt(studentUID + "_\uf8ff")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        alertList.clear();
                        for (DataSnapshot chatSnapshot : snapshot.getChildren()) {
                            String facultyId = chatSnapshot.getKey().split("_")[1];
                            DatabaseReference facultyToStudentRef = chatSnapshot.child("FacultyToStudent").getRef();

                            facultyToStudentRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot facultySnapshot) {
                                    int unreadCount = 0;
                                    String timestamp = "";
                                    for (DataSnapshot msgSnapshot : facultySnapshot.getChildren()) {
                                        Boolean readStatus = msgSnapshot.child("readStatus").getValue(Boolean.class);
                                        if (readStatus != null && !readStatus) {
                                            unreadCount++;
                                            timestamp = msgSnapshot.child("timestamp").getValue(String.class);
                                        }
                                    }
                                    if (unreadCount > 0) {
                                        fetchFacultyName(facultyId, timestamp, unreadCount);
                                    } else {
                                        updateUI();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Log.e("FirebaseError", error.getMessage());
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("FirebaseError", error.getMessage());
                    }
                });
    }

    private void fetchFacultyName(String facultyId, String timestamp, int unreadCount) {
        DatabaseReference facultyRef = FirebaseDatabase.getInstance().getReference("Faculties").child(facultyId);
        facultyRef.child("name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String facultyName = snapshot.getValue(String.class);
                alertList.add(new AlertChatModelStudent(facultyId, facultyName, timestamp, unreadCount));
                updateUI();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", error.getMessage());
            }
        });
    }

    private void updateUI() {
        if (alertList.isEmpty()) {
            noNewMessages.setVisibility(View.VISIBLE);
            recyclerViewChat.setVisibility(View.GONE);
        } else {
            noNewMessages.setVisibility(View.GONE);
            recyclerViewChat.setVisibility(View.VISIBLE);
            adapter.notifyDataSetChanged();
        }
    }

    private void fetchNoticeAlerts() {
        noticesRef = FirebaseDatabase.getInstance().getReference("Notices");
        alertRef = FirebaseDatabase.getInstance().getReference("StudentNoticeAlerts").child(studentUID);
        facultyRef = FirebaseDatabase.getInstance().getReference("Faculties");

        alertRef.child("lastSeenTimestamp")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        long lastSeen = snapshot.exists() ? snapshot.getValue(Long.class) : 0L;
                        fetchNoticesAfterLastSeen(lastSeen);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("AlertsStudent", "Failed to fetch last seen timestamp: " + error.getMessage());
                    }
                });

    }

    private void fetchNoticeAdminAlert() {
        noticeadRef = FirebaseDatabase.getInstance().getReference("AdminToStudentNotice");
        alertadRef = FirebaseDatabase.getInstance().getReference("StudentNotice_AdminAlerts").child(studentUID);
        adminRef = FirebaseDatabase.getInstance().getReference("Admin");

        alertadRef.child("lastSeenTimestamp")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        long lastSeen = snapshot.exists() ? snapshot.getValue(Long.class) : 0L;
                        fetchAdminNoticesAfterLastSeen(lastSeen);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("AlertsStudent", "Failed to fetch last seen timestamp: " + error.getMessage());
                    }
                });

    }

    private void fetchAssignmentAlerts() {
        assignmentRef = FirebaseDatabase.getInstance().getReference("Assignments");
        assalertRef = FirebaseDatabase.getInstance().getReference("StudentAssignmentAlerts").child(studentUID);
        facultyRef = FirebaseDatabase.getInstance().getReference("Faculties");

        assalertRef.child("lastSeenTimestamp")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        long lastSeen = snapshot.exists() ? snapshot.getValue(Long.class) : 0L;
                        fetchAssignmentAfterLastSeen(lastSeen);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("AlertsStudent", "Failed to fetch last seen timestamp: " + error.getMessage());
                    }
                });

    }

    private void fetchNoticesAfterLastSeen(long lastSeen) {
        DatabaseReference noticesRef = FirebaseDatabase.getInstance().getReference("Notices");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        String studentUID = user.getUid();

        // Get student details (program, year, semester, division) to filter relevant notices
        FirebaseDatabase.getInstance().getReference("Students").child(studentUID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()) return;

                        String studentProgram = snapshot.child("program").getValue(String.class);
                        String studentYear = snapshot.child("year").getValue(String.class);
                        String studentSemester = snapshot.child("semester").getValue(String.class);
                        String studentDivision = snapshot.child("division").getValue(String.class);

                        // Fetch all notices
                        noticesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot noticeSnapshot) {
                                Map<String, Integer> facultyNewNoticeMap = new HashMap<>();

                                for (DataSnapshot noticeSnap : noticeSnapshot.getChildren()) {
                                    String program = noticeSnap.child("program").getValue(String.class);
                                    String year = noticeSnap.child("year").getValue(String.class);
                                    String semester = noticeSnap.child("semester").getValue(String.class);
                                    String division = noticeSnap.child("division").getValue(String.class);
                                    String facultyName = noticeSnap.child("facultyName").getValue(String.class);
                                    long timestamp = 0L;

                                    if (noticeSnap.child("timestamp").getValue() instanceof Long) {
                                        timestamp = noticeSnap.child("timestamp").getValue(Long.class);
                                    } else {
                                        try {
                                            timestamp = Long.parseLong(noticeSnap.child("timestamp").getValue(String.class));
                                        } catch (Exception e) {
                                            Log.e("TimestampParse", "Invalid timestamp format");
                                        }
                                    }

                                    // Check if the notice is for this student
                                    if (program != null && program.equals(studentProgram) &&
                                            year != null && year.equals(studentYear) &&
                                            semester != null && semester.equals(studentSemester) &&
                                            division != null && division.equals(studentDivision)) {

                                        // Check if the notice was sent after last seen
                                        if (timestamp > lastSeen) {
                                            int count = facultyNewNoticeMap.containsKey(facultyName) ? facultyNewNoticeMap.get(facultyName) : 0;
                                            facultyNewNoticeMap.put(facultyName, count + 1);
                                        }
                                    }
                                }

                                displayNewNoticeAlerts(facultyNewNoticeMap);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(alerts_student.this, "Error fetching notices", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(alerts_student.this, "Failed to fetch student data", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void fetchAdminNoticesAfterLastSeen(long lastSeen) {
        DatabaseReference noticesadRef = FirebaseDatabase.getInstance().getReference("AdminToStudentNotice");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        String studentUID = user.getUid();

        FirebaseDatabase.getInstance().getReference("Students").child(studentUID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()) return;

                        String studentProgram = snapshot.child("program").getValue(String.class);
                        String studentSemester = snapshot.child("semester").getValue(String.class);

                        // Fetch all notices
                        noticesadRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot noticeSnapshot) {
                                Map<String, Integer> adminNewNoticeMap = new HashMap<>();

                                for (DataSnapshot noticeSnap : noticeSnapshot.getChildren()) {
                                    String program = noticeSnap.child("course").getValue(String.class);
                                    String semester = noticeSnap.child("semester").getValue(String.class);
                                    String adminName = noticeSnap.child("adminName").getValue(String.class);
                                    long timestamp = 0L;

                                    if (noticeSnap.child("timestamp").getValue() instanceof Long) {
                                        timestamp = noticeSnap.child("timestamp").getValue(Long.class);
                                    } else {
                                        try {
                                            timestamp = Long.parseLong(noticeSnap.child("timestamp").getValue(String.class));
                                        } catch (Exception e) {
                                            Log.e("TimestampParse", "Invalid timestamp format");
                                        }
                                    }

                                    // Check if the notice is for this student
                                    if (program != null && program.equals(studentProgram) &&
                                            semester != null && semester.equals(studentSemester)) {

                                        // Check if the notice was sent after last seen
                                        if (timestamp > lastSeen) {
                                            int count = adminNewNoticeMap.containsKey(adminName) ? adminNewNoticeMap.get(adminName) : 0;
                                            adminNewNoticeMap.put(adminName, count + 1);
                                        }
                                    }
                                }

                                displayNewAdminNoticeAlerts(adminNewNoticeMap);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(alerts_student.this, "Error fetching notices", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(alerts_student.this, "Failed to fetch student data", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void fetchAssignmentAfterLastSeen(long lastSeen) {
        DatabaseReference assignmentRef = FirebaseDatabase.getInstance().getReference("Assignments");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        String studentUID = user.getUid();

        // Get student details (program, year, semester, division) to filter relevant notices
        FirebaseDatabase.getInstance().getReference("Students").child(studentUID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()) return;

                        String studentProgram = snapshot.child("program").getValue(String.class);
                        String studentYear = snapshot.child("year").getValue(String.class);
                        String studentSemester = snapshot.child("semester").getValue(String.class);
                        String studentDivision = snapshot.child("division").getValue(String.class);

                        // Fetch all notices
                        assignmentRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot assignmentSnapshot) {
                                Map<String, Integer> facultyNewAssignmentMap = new HashMap<>();

                                for (DataSnapshot assignmentSnap : assignmentSnapshot.getChildren()) {
                                    String program = assignmentSnap.child("program").getValue(String.class);
                                    String year = assignmentSnap.child("year").getValue(String.class);
                                    String semester = assignmentSnap.child("semester").getValue(String.class);
                                    String division = assignmentSnap.child("division").getValue(String.class);
                                    String facultyName = assignmentSnap.child("facultyName").getValue(String.class);
                                    long timestamp = 0L;

                                    if (assignmentSnap.child("timestamp").getValue() instanceof Long) {
                                        timestamp = assignmentSnap.child("timestamp").getValue(Long.class);
                                    } else {
                                        try {
                                            timestamp = Long.parseLong(assignmentSnap.child("timestamp").getValue(String.class));
                                        } catch (Exception e) {
                                            Log.e("TimestampParse", "Invalid timestamp format");
                                        }
                                    }

                                    // Check if the assignment is for this student
                                    if (program != null && program.equals(studentProgram) &&
                                            year != null && year.equals(studentYear) &&
                                            semester != null && semester.equals(studentSemester) &&
                                            division != null && division.equals(studentDivision)) {

                                        // Check if the assignment was sent after last seen
                                        if (timestamp > lastSeen) {
                                            int count = facultyNewAssignmentMap.containsKey(facultyName) ? facultyNewAssignmentMap.get(facultyName) : 0;
                                            facultyNewAssignmentMap.put(facultyName, count + 1);
                                        }
                                    }
                                }

                                displayNewAssignmentAlerts(facultyNewAssignmentMap);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(alerts_student.this, "Error fetching assignments", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(alerts_student.this, "Failed to fetch student data", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void displayNewNoticeAlerts(Map<String, Integer> facultyNewNoticeMap) {
        List<String> alerts = new ArrayList<>();
        if (facultyNewNoticeMap.isEmpty()) {
            alerts.add("✅ No new notice from faculty. You're up to date!");
        } else {
            for (Map.Entry<String, Integer> entry : facultyNewNoticeMap.entrySet()) {
                alerts.add("📣 You got " + entry.getValue() + " new notice(s) from " + entry.getKey());
            }
        }

        AlertNoticeStudentAdapter alertAdapter = new AlertNoticeStudentAdapter(alerts);
        recyclerView.setAdapter(alertAdapter);
    }

    private void displayNewAdminNoticeAlerts(Map<String, Integer> adminNewNoticeMap) {
        List<String> alerts = new ArrayList<>();
        if (adminNewNoticeMap.isEmpty()) {
            alerts.add("✅ No new notice from admin. You're up to date!");
        } else {
            for (Map.Entry<String, Integer> entry : adminNewNoticeMap.entrySet()) {
                alerts.add("📣 You got " + entry.getValue() + " new notice(s) from " + entry.getKey());
            }
        }

        AlertNoticeStudentAdapter alertAdapter = new AlertNoticeStudentAdapter(alerts);
        recyclerViewAdn.setAdapter(alertAdapter);
    }

    private void displayNewAssignmentAlerts(Map<String, Integer> facultyNewAssignmentMap) {
        List<String> alerts = new ArrayList<>();
        if (facultyNewAssignmentMap.isEmpty()) {
            alerts.add("✅ No new assignments. You're up to date!");
        } else {
            for (Map.Entry<String, Integer> entry : facultyNewAssignmentMap.entrySet()) {
                alerts.add("📣 You got " + entry.getValue() + " new assignment(s) from " + entry.getKey());
            }
        }

        AlertAssignmentStudentAdapter alertAdapter = new AlertAssignmentStudentAdapter(alerts);
        recyclerViewAss.setAdapter(alertAdapter);
    }


}


//    private void loadAssignmentAlerts() {
//        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//        if (user == null) return;
//        String studentUID = user.getUid();
//
//        DatabaseReference studentRef = FirebaseDatabase.getInstance().getReference("Students").child(studentUID);
//        studentRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                String program = snapshot.child("program").getValue(String.class);
//                String year = snapshot.child("year").getValue(String.class);
//                String semester = snapshot.child("semester").getValue(String.class);
//                String division = snapshot.child("division").getValue(String.class);
//
//                if (program == null || year == null || semester == null || division == null) return;
//
//                DatabaseReference assignmentRef = FirebaseDatabase.getInstance().getReference("Assignments");
//                assignmentRef.addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        List<String> assignmentAlerts = new ArrayList<>();
//
//                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
//                        Date today = new Date();
//
//                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
//                            String title = dataSnapshot.child("title").getValue(String.class);
//                            String facultyName = dataSnapshot.child("facultyName").getValue(String.class);
//                            String deadlineStr = dataSnapshot.child("deadline").getValue(String.class);
//                            String assProgram = dataSnapshot.child("program").getValue(String.class);
//                            String assYear = dataSnapshot.child("year").getValue(String.class);
//                            String assSemester = dataSnapshot.child("semester").getValue(String.class);
//                            String assDivision = dataSnapshot.child("division").getValue(String.class);
//                            String timestampStr = dataSnapshot.child("timestamp").getValue(String.class);
//
//                            if (assProgram == null || assYear == null || assSemester == null || assDivision == null)
//                                continue;
//
//                            if (assProgram.equals(program) && assYear.equals(year) && assSemester.equals(semester) && assDivision.equals(division)) {
//                                try {
//                                    Date deadline = sdf.parse(deadlineStr);
//
//                                    // ✅ Alert #1: Due in 2 Days
//                                    long diffInMillies = deadline.getTime() - today.getTime();
//                                    long daysDiff = TimeUnit.MILLISECONDS.toDays(diffInMillies);
//
//                                    if (daysDiff <= 2 && daysDiff >= 0) {
//                                        assignmentAlerts.add("⏰ \"" + title + "\" from " + facultyName + " is due in " + daysDiff + " day(s). Deadline: " + deadlineStr);
//                                    }
//
//                                    // ✅ Alert #2: New Assignment Uploaded (uploaded within last 2 days)
//                                    if (timestampStr != null) {
//                                        long uploadMillis = Long.parseLong(timestampStr);
//                                        long timeDiff = today.getTime() - uploadMillis;
//                                        long uploadedDaysAgo = TimeUnit.MILLISECONDS.toDays(timeDiff);
//
//                                        if (uploadedDaysAgo <= 2) {
//                                            assignmentAlerts.add("📚 New Assignment \"" + title + "\" from " + facultyName + ". Deadline: " + deadlineStr);
//                                        }
//                                    }
//
//                                } catch (ParseException | NumberFormatException e) {
//                                    e.printStackTrace();
//                                }
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//                    }
//                });
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//            }
//        });
//    }


//================Below is the perfect notice alert=============
//
//private TextView tvAlertsMessage;
//private DatabaseReference noticeRef;
//private String loggedInUID;
//        private String studentProgram, studentYear, studentSemester, studentDivision;
//private int newNoticesCount = 0;
//
//tvAlertsMessage = findViewById(R.id.tvAlertsMessage);
//
//FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
//        if (currentUser == null) {
//        tvAlertsMessage.setText("User not logged in!");
//            return;
//                    }
//
//loggedInUID = currentUser.getUid();
//fetchStudentDetails();
//
//private void fetchStudentDetails() {
//    DatabaseReference studentRef = FirebaseDatabase.getInstance().getReference("Students");
//    studentRef.child(loggedInUID).addListenerForSingleValueEvent(new ValueEventListener() {
//        @Override
//        public void onDataChange(@NonNull DataSnapshot snapshot) {
//            if (snapshot.exists()) {
//                studentProgram = snapshot.child("program").getValue(String.class);
//                studentYear = snapshot.child("year").getValue(String.class);
//                studentSemester = snapshot.child("semester").getValue(String.class);
//                studentDivision = snapshot.child("division").getValue(String.class);
//
//                fetchNewNoticesCount();
//            } else {
//                tvAlertsMessage.setText("Student details not found!");
//            }
//        }
//
//        @Override
//        public void onCancelled(@NonNull DatabaseError error) {
//            tvAlertsMessage.setText("Error: " + error.getMessage());
//        }
//    });
//}
//
//private void fetchNewNoticesCount() {
//    noticeRef = FirebaseDatabase.getInstance().getReference("Notices");
//
//    noticeRef.addListenerForSingleValueEvent(new ValueEventListener() {
//        @Override
//        public void onDataChange(@NonNull DataSnapshot snapshot) {
//            newNoticesCount = 0;
//            long lastVisitTimestamp = getSharedPreferences("NoticePrefs", MODE_PRIVATE)
//                    .getLong("last_notice_check", 0);
//
//            for (DataSnapshot noticeSnap : snapshot.getChildren()) {
//                String noticeProgram = noticeSnap.child("program").getValue(String.class);
//                String noticeYear = noticeSnap.child("year").getValue(String.class);
//                String noticeSemester = noticeSnap.child("semester").getValue(String.class);
//                String noticeDivision = noticeSnap.child("division").getValue(String.class);
//                Long timestamp = noticeSnap.child("timestamp").getValue(Long.class);
//
//                if (timestamp != null
//                        && noticeProgram.equals(studentProgram)
//                        && noticeYear.equals(studentYear)
//                        && noticeSemester.equals(studentSemester)
//                        && noticeDivision.equals(studentDivision)
//                        && timestamp > lastVisitTimestamp) {
//                    newNoticesCount++;
//                }
//            }
//
//            // Save current timestamp as last visit
//            getSharedPreferences("NoticePrefs", MODE_PRIVATE).edit()
//                    .putLong("last_notice_check", System.currentTimeMillis())
//                    .apply();
//
//            // Display message
//            if (newNoticesCount > 0) {
//                tvAlertsMessage.setText("📣 You have " + newNoticesCount + " new notices since your last visit.");
//            } else {
//                tvAlertsMessage.setText("✅ No new notices. You're up to date!");
//            }
//        }
//
//        @Override
//        public void onCancelled(@NonNull DatabaseError error) {
//            tvAlertsMessage.setText("Failed to load notices.");
//        }
//    });
//}

