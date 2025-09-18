package com.example.cms;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class notice_student_first extends AppCompatActivity {

    private RecyclerView recyclerView;
    private String program, year, semester, division;

    private NoticeAdapter noticeAdapter;
    private List<NoticeModel> noticeList;

    Spinner spinnerFacultyFilter;
    List<String> facultyNameList = new ArrayList<>();
    Map<String, String> facultyMap = new HashMap<>(); // Map<facultyName, facultyID>
    String selectedFacultyName = "All";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_notice_student_first);

        recyclerView = findViewById(R.id.recyclerViewNotices);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        noticeList = new ArrayList<>();
        noticeAdapter = new NoticeAdapter(noticeList);
        recyclerView.setAdapter(noticeAdapter);

        spinnerFacultyFilter = findViewById(R.id.spinnerFacultyFilter);
        fetchStudentDetails();

        updateLastSeenTimestamp();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(notice_student_first.this, student_notice_selection.class);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateLastSeenTimestamp() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String studentUID = user.getUid();

            DatabaseReference alertRef = FirebaseDatabase.getInstance()
                    .getReference("StudentNoticeAlerts")
                    .child(studentUID);

            alertRef.child("lastSeenTimestamp")
                    .setValue(System.currentTimeMillis())
                    .addOnSuccessListener(aVoid ->
                            Log.d("NoticeStudent", "Last seen timestamp updated"))
                    .addOnFailureListener(e ->
                            Log.e("NoticeStudent", "Failed to update last seen timestamp: " + e.getMessage()));
        }
    }

    private void fetchStudentDetails() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference studentRef = FirebaseDatabase.getInstance().getReference("Students").child(uid);

        studentRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    program = snapshot.child("program").getValue(String.class);
                    year = snapshot.child("year").getValue(String.class);
                    semester = snapshot.child("semester").getValue(String.class);
                    division = snapshot.child("division").getValue(String.class);

                    loadFacultyFilter(program, year, semester, division);

//                    Used for without filter-----------
//                    fetchFilteredNotices();
                }else {
                    Toast.makeText(notice_student_first.this, "Student details not found!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(notice_student_first.this, "Failed to retrieve student data!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadFacultyFilter(String program, String year, String semester, String division) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Notices");

        facultyNameList.clear();
        facultyMap.clear();
        facultyNameList.add("All");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snap : snapshot.getChildren()) {
                    String noticeProgram = snap.child("program").getValue(String.class);
                    String noticeYear = snap.child("year").getValue(String.class);
                    String noticeSemester = snap.child("semester").getValue(String.class);
                    String noticeDivision = snap.child("division").getValue(String.class);

                    if (program.equals(noticeProgram) && year.equals(noticeYear)
                            && semester.equals(noticeSemester) && division.equals(noticeDivision)) {

                        String facultyName = snap.child("facultyName").getValue(String.class);
                        String facultyID = snap.child("facultyID").getValue(String.class);

                        if (facultyName != null && !facultyMap.containsKey(facultyName)) {
                            facultyMap.put(facultyName, facultyID);
                            facultyNameList.add(facultyName);
                        }
                    }
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(notice_student_first.this,
                        android.R.layout.simple_spinner_item, facultyNameList);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerFacultyFilter.setAdapter(adapter);

                // Default load all
                fetchFilteredNotices(program, year, semester, division, "All");

                spinnerFacultyFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        selectedFacultyName = facultyNameList.get(position);
                        fetchFilteredNotices(program, year, semester, division, selectedFacultyName);
                    }
                    @Override public void onNothingSelected(AdapterView<?> parent) { }
                });
            }

            @Override public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    private void fetchFilteredNotices(String program, String year, String semester, String division, String facultyNameFilter) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Notices");
        List<NoticeModel> filteredList = new ArrayList<>();

        ref.orderByChild("timestamp").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snap : snapshot.getChildren()) {
                    String nProgram = snap.child("program").getValue(String.class);
                    String nYear = snap.child("year").getValue(String.class);
                    String nSemester = snap.child("semester").getValue(String.class);
                    String nDivision = snap.child("division").getValue(String.class);
                    String nFaculty = snap.child("facultyName").getValue(String.class);

                    if (program.equals(nProgram) && year.equals(nYear)
                            && semester.equals(nSemester) && division.equals(nDivision)) {

                        if (facultyNameFilter.equals("All") || facultyNameFilter.equals(nFaculty)) {
                            NoticeModel model = snap.getValue(NoticeModel.class);
                            filteredList.add(model);
                        }
                    }
                }

                // Sort by latest first
                Collections.sort(filteredList, (n1, n2) -> Long.compare(n2.getTimestamp(), n1.getTimestamp()));

                NoticeAdapter adapter = new NoticeAdapter(filteredList);
                recyclerView.setAdapter(adapter);
            }

            @Override public void onCancelled(@NonNull DatabaseError error) { }
        });
    }



//    =============Used for Without filter======
//    private void fetchFilteredNotices() {
//        DatabaseReference noticesRef = FirebaseDatabase.getInstance().getReference("Notices");
//
//        noticesRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                noticeList.clear();
//
//                for (DataSnapshot noticeSnap : snapshot.getChildren()) {
//                    String noticeProgram = noticeSnap.child("program").getValue(String.class);
//                    String noticeYear = noticeSnap.child("year").getValue(String.class);
//                    String noticeSemester = noticeSnap.child("semester").getValue(String.class);
//                    String noticeDivision = noticeSnap.child("division").getValue(String.class);
//
//                    if (program.equals(noticeProgram) &&
//                            year.equals(noticeYear) &&
//                            semester.equals(noticeSemester) &&
//                            division.equals(noticeDivision)) {
//
//                        String title = noticeSnap.child("title").getValue(String.class);
//                        String content = noticeSnap.child("content").getValue(String.class);
//                        String date = noticeSnap.child("date").getValue(String.class);
//                        String facultyName = noticeSnap.child("facultyName").getValue(String.class);
//
//                        noticeList.add(new NoticeModel(title, content, date, facultyName));
//                    }
//                }
//
//                noticeAdapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {}
//        });
//    }




//    nothing==========
//    private void fetchNotices() {
//        noticeRef = FirebaseDatabase.getInstance().getReference("Notices");
//
//        noticeRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                noticeList.clear();
//                for (DataSnapshot noticeSnapshot : snapshot.getChildren()) {
//                    NoticeModel notice = noticeSnapshot.getValue(NoticeModel.class);
//
//                    // Match Student's Program, Year, Semester with Notice
//                    if (notice != null && notice.getProgram().equals(studentProgram) &&
//                            notice.getYear().equals(studentYear) &&
//                            notice.getSemester().equals(studentSemester)) {
//                        noticeList.add(notice);
//                    }
//                }
//
//                if (noticeList.isEmpty()) {
//                    Toast.makeText(notice_student_first.this, "No notices found for your filters.", Toast.LENGTH_SHORT).show();
//                }
//                noticeAdapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Log.e("FirebaseError", "Failed to fetch notices: " + error.getMessage());
//                Toast.makeText(notice_student_first.this, "Failed to fetch notices!", Toast.LENGTH_SHORT).show();
//            }
//        });
////        noticeRef.child(studentProgram).child(studentId).addValueEventListener(new ValueEventListener() {
////            @Override
////            public void onDataChange(@NonNull DataSnapshot snapshot) {
////                noticeList.clear();
////                for (DataSnapshot noticeSnapshot : snapshot.getChildren()) {
////                    NoticeModel notice = noticeSnapshot.getValue(NoticeModel.class);
////                    if (notice != null) {
////                        noticeList.add(notice);
////                    }
////                }
////                noticeAdapter.notifyDataSetChanged();
////                resetNewNotices();
////            }
////
////            @Override
////            public void onCancelled(@NonNull DatabaseError error) {}
////        });
//
//    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(notice_student_first.this, student_notice_selection.class);
        startActivity(intent);
        finish();
    }

    // Update Alert Activity in Firebase
//    private void resetNewNotices() {
//        alertNoticeRef.child("newNotices").setValue(0);
//    }
//
//    @Override
//    public void onBackPressed() {
//
//    }

}