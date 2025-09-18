package com.example.cms;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class notice_faculty extends AppCompatActivity {

    TextView tvFcName, tvNoticeDate;
    private EditText etNoticeTitle, etNoticeContent, etAutoDeleteDate;
    private Button btnSendNotice;
    Spinner spYear, spSemester, spProgram, spDivision;
    String facultyID, currentDate, facultyName;
    RecyclerView recyclerNotices;

    DatabaseReference noticesRef, databaseReference;
    FirebaseAuth auth;

    ArrayList<FacultyNoticeModel> noticeList;
    FacultyNoticeAdapter noticeAdapter;

    Calendar calendar;
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_notice_faculty);

        auth = FirebaseAuth.getInstance();
        calendar = Calendar.getInstance();
        noticesRef = FirebaseDatabase.getInstance().getReference("Notices");
        databaseReference = FirebaseDatabase.getInstance().getReference("Faculties");
        fetchFacultyDetails();

        tvFcName = findViewById(R.id.tvFcName);
        tvNoticeDate = findViewById(R.id.tvNoticeDate);
        etNoticeTitle = findViewById(R.id.etNoticeTitle);
        etNoticeContent = findViewById(R.id.etNoticeContent);
        btnSendNotice = findViewById(R.id.btnSendNotice);
        etAutoDeleteDate = findViewById(R.id.etAutoDeleteDate);

        spYear = findViewById(R.id.spYear);
        spSemester = findViewById(R.id.spSemester);
        spProgram = findViewById(R.id.spinnerProgram);
        spDivision = findViewById(R.id.spinnerDivision);

        etAutoDeleteDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setupDatePicker();
            }
        });

        ArrayAdapter<CharSequence> programAdapter = ArrayAdapter.createFromResource(this, R.array.program_array, android.R.layout.simple_spinner_item);
        programAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spProgram.setAdapter(programAdapter);

        ArrayAdapter<CharSequence> yearAdapter = ArrayAdapter.createFromResource(this, R.array.year_semester_array, android.R.layout.simple_spinner_item);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spYear.setAdapter(yearAdapter);

        spYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ArrayAdapter<CharSequence> semesterAdapter;

                switch (position) {
                    case 0: // First Year
                        semesterAdapter = ArrayAdapter.createFromResource(
                                notice_faculty.this, R.array.first_year_semesters, android.R.layout.simple_spinner_item);
                        break;
                    case 1: // Second Year
                        semesterAdapter = ArrayAdapter.createFromResource(
                                notice_faculty.this, R.array.second_year_semesters, android.R.layout.simple_spinner_item);
                        break;
                    case 2: // Third Year
                        semesterAdapter = ArrayAdapter.createFromResource(
                                notice_faculty.this, R.array.third_year_semesters, android.R.layout.simple_spinner_item);
                        break;
                    default:
                        semesterAdapter = null;
                }

                if (semesterAdapter != null) {
                    semesterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spSemester.setAdapter(semesterAdapter);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        ArrayAdapter<CharSequence> divAdapter = ArrayAdapter.createFromResource(this, R.array.division_array, android.R.layout.simple_spinner_item);
        divAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDivision.setAdapter(divAdapter);

        recyclerNotices = findViewById(R.id.recyclerNotices);

        currentDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
        tvNoticeDate.setText("Date: " + currentDate);

        recyclerNotices.setLayoutManager(new LinearLayoutManager(this));
        noticeList = new ArrayList<>();
        noticeAdapter = new FacultyNoticeAdapter(this, noticeList);
        recyclerNotices.setAdapter(noticeAdapter);

        deleteExpiredNotices();

        btnSendNotice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendNotice();
            }
        });

        Intent intent = getIntent();
        if (intent.hasExtra("noticeTitle")) {
            etNoticeTitle.setText(intent.getStringExtra("noticeTitle"));
        }
        if (intent.hasExtra("noticeContent")) {
            etNoticeContent.setText(intent.getStringExtra("noticeContent"));
        }
        if (intent.hasExtra("year")) {
            spYear.setSelection(getSpinnerIndex(spYear, intent.getStringExtra("year")));
        }
        if (intent.hasExtra("semester")) {
            spSemester.setSelection(getSpinnerIndex(spSemester, intent.getStringExtra("semester")));
        }
        if (intent.hasExtra("program")) {
            spProgram.setSelection(getSpinnerIndex(spProgram, intent.getStringExtra("program")));
        }
        if (intent.hasExtra("division")) {
            spDivision.setSelection(getSpinnerIndex(spDivision, intent.getStringExtra("division")));
        }


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private int getSpinnerIndex(Spinner spinner, String value) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(value)) {
                return i;
            }
        }
        return 0;
    }


    private void setupDatePicker() {

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(notice_faculty.this,
                (view, year1, month1, dayOfMonth) -> {
                    calendar.set(year1, month1, dayOfMonth);
                    etAutoDeleteDate.setText(sdf.format(calendar.getTime()));
                }, year, month, day);
        dialog.getDatePicker().setMinDate(System.currentTimeMillis());
        dialog.show();
    }

    private void fetchFacultyDetails() {

        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser == null) {
            // Redirect to login screen if user is not logged in
            Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(notice_faculty.this, login_faculty.class));
            finish();
            return;
        }

        String uid = currentUser.getUid();

        databaseReference.orderByChild("uid").equalTo(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot facultySnapshot : snapshot.getChildren()) {
                        facultyID = facultySnapshot.child("facultyID").getValue(String.class);
                        facultyName = facultySnapshot.child("name").getValue(String.class);

                        tvFcName.setText(facultyName);

                        if (facultyID != null) {
                            fetchNotices(); // Fetch past notices after getting facultyID
                        }
                    }
                } else {
                    Toast.makeText(notice_faculty.this, "Faculty details not found!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(notice_faculty.this, "Database Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

//        databaseReference.orderByChild("uid").equalTo(uid).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if (dataSnapshot.exists()) {
//                    for (DataSnapshot facultySnapshot : dataSnapshot.getChildren()) {
//                        facultyID = facultySnapshot.getKey(); // Get facultyID (entered by faculty)
//                        facultyName = facultySnapshot.child("name").getValue(String.class);
//
//                        // Set data in UI
//                        tvFcName.setText(facultyName);
//                    }
//                } else {
//                    Toast.makeText(notice_faculty.this, "Faculty details not found!", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                Toast.makeText(notice_faculty.this, "Database Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
    }

    private void sendNotice() {

        String program = spProgram.getSelectedItem().toString();
        String year = spYear.getSelectedItem().toString();
        String semester = spSemester.getSelectedItem().toString();
        String division = spDivision.getSelectedItem().toString();
        String title = etNoticeTitle.getText().toString().trim();
        String content = etNoticeContent.getText().toString().trim();
        String autoDeleteDate = etAutoDeleteDate.getText().toString();

        if (title.isEmpty() || content.isEmpty() || autoDeleteDate.isEmpty()) {
            Toast.makeText(this, "All fields are required!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (facultyID == null || facultyName == null) {
            Toast.makeText(this, "Faculty details not found!", Toast.LENGTH_SHORT).show();
            return;
        }

        long timestamp = System.currentTimeMillis();
        String noticeID = noticesRef.push().getKey();

        FacultyNoticeModel notice = new FacultyNoticeModel(title, content, currentDate, facultyName, facultyID, year, semester, program, division, timestamp, noticeID, autoDeleteDate);

        noticesRef.child(noticeID).setValue(notice)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(notice_faculty.this, "Notice Sent To Student Successfully!", Toast.LENGTH_SHORT).show();
                    clearFields();
                    fetchNotices();
                })
                .addOnFailureListener(e -> Toast.makeText(notice_faculty.this, "Failed to send notice!", Toast.LENGTH_SHORT).show());


        // Generate a unique ID for each notice
//        String noticeId = databaseReference.push().getKey();
//        FacultyNoticeModel notice = new FacultyNoticeModel(title, content, date, facultyName, year, semester);
////        databaseReference.child(noticeId).setValue(notice);
////
////        HashMap<String, Object> noticeData = new HashMap<>();
////        noticeData.put("facultyName", faculty);
////        noticeData.put("date", date);
////        noticeData.put("title", title);
////        noticeData.put("content", content);
//
//        databaseReference.child(noticeId).setValue(notice)
//                .addOnSuccessListener(aVoid -> {
//                    Toast.makeText(notice_faculty.this, "Notice Sent Successfully", Toast.LENGTH_SHORT).show();
//                    etNoticeTitle.setText("");
//                    etNoticeContent.setText("");
//                })
//                .addOnFailureListener(e -> Toast.makeText(notice_faculty.this, "Failed to send notice", Toast.LENGTH_SHORT).show());
//
//        noticelist.add(0, notice);
//        noticeAdapter.notifyDataSetChanged();
    }

//    private void updateStudentAlerts(String program, String year, String semester) {
//        DatabaseReference studentRef = FirebaseDatabase.getInstance().getReference("Students");
//
//        studentRef.child(program).orderByChild("year").equalTo(year).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                for (DataSnapshot student : snapshot.getChildren()) {
//                    if (student.child("semester").getValue(String.class).equals(semester)) {
//                        String studentID = student.getKey(); // Get student ID
//                        DatabaseReference alertRef = FirebaseDatabase.getInstance().getReference("NoticeAlerts").child(studentID);
//
//                        alertRef.addListenerForSingleValueEvent(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(@NonNull DataSnapshot alertSnapshot) {
//                                int newNotices = 1;
//                                if (alertSnapshot.exists()) {
//                                    Integer currentCount = alertSnapshot.child("newNotices").getValue(Integer.class);
//                                    if (currentCount != null) newNotices += currentCount;
//                                }
//                                alertRef.child("newNotices").setValue(newNotices);
//                                alertRef.child("lastUpdated").setValue(new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date()));
//                            }
//
//                            @Override
//                            public void onCancelled(@NonNull DatabaseError error) { }
//                        });
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) { }
//        });
//    }

    private void deleteExpiredNotices() {
        noticesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snap : snapshot.getChildren()) {
                    FacultyNoticeModel notice = snap.getValue(FacultyNoticeModel.class);
                    if (notice != null) {
                        try {
                            Date current = sdf.parse(sdf.format(Calendar.getInstance().getTime()));
                            Date deleteDate = sdf.parse(notice.getAutoDeleteDate());

                            if (deleteDate != null && current != null && deleteDate.before(current)) {
                                noticesRef.child(notice.getNoticeId()).removeValue();
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(notice_faculty.this, "Failed to check expired notices!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchNotices() {
        noticeList.clear();

        String uid = auth.getCurrentUser().getUid();

        databaseReference.orderByChild("uid").equalTo(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot facultySnapshot) {
                        if (!facultySnapshot.exists()) return;

                        noticesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot snap : snapshot.getChildren()) {
                                    FacultyNoticeModel notice = snap.getValue(FacultyNoticeModel.class);
                                    if (notice != null && notice.getFacultyName().equals(facultyName)) {
                                        noticeList.add(notice);
                                    }
                                }

                                // Sort by timestamp (latest first)
                                Collections.sort(noticeList, (n1, n2) -> Long.compare(n2.getTimestamp(), n1.getTimestamp()));
                                noticeAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(notice_faculty.this, "Failed to load notices", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }


//        ----------------------Nothing work then use it===================
//        String currentFacultyID = FirebaseAuth.getInstance().getCurrentUser().getUid();
//        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
//        String today = sdf.format(Calendar.getInstance().getTime());
//
//        noticesRef.orderByChild("timestamp")
//                .addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        noticeList.clear();
////                        for (DataSnapshot ds : snapshot.getChildren()) {
////                            FacultyNoticeModel notice = ds.getValue(FacultyNoticeModel.class);
////                            noticeList.add(notice);
////                        }
////                        noticeAdapter.notifyDataSetChanged();
//
//                        for (DataSnapshot noticeSnap : snapshot.getChildren()) {
//                            FacultyNoticeModel notice = noticeSnap.getValue(FacultyNoticeModel.class);
////                            if (notice != null && facultyName.equals(notice.getFacultyName())) {
////                                noticeList.add(0, notice); // Add to front to show latest first
////                            }
//                            if (notice != null && currentFacultyID.equals(notice.getFacultyID())) {
//                                noticeList.add(0, notice); // Latest first
//                            }
//                        }
//                        noticeAdapter.notifyDataSetChanged();
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//                        Toast.makeText(notice_faculty.this, "Error fetching notices!", Toast.LENGTH_SHORT).show();
//                    }
//                });


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Navigate back to home screen and keep the drawer open
            Intent intent = new Intent(notice_faculty.this, faculty_notice_selection.class);
            intent.putExtra("openDrawer", false); // Send an extra flag to open the drawer
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(notice_faculty.this, faculty_notice_selection.class);
        startActivity(intent);
        finish();
    }

    private void clearFields() {
        etNoticeTitle.setText("");
        etNoticeContent.setText("");
        etAutoDeleteDate.setText("");
    }

}