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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class assignment_student extends AppCompatActivity {

    private RecyclerView recyclerView;
    private String program, year, semester, division;

    private AssignmentAdapterStudent assignmentAdapter;
    private List<AssignmentModelStudent> assignmentList;

    Spinner spinnerFacultyFilter;
    List<String> facultyNameList = new ArrayList<>();
    Map<String, String> facultyMap = new HashMap<>();
    String selectedFacultyName = "All";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_assignment_student);

        recyclerView = findViewById(R.id.recyclerViewNotices);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        assignmentList = new ArrayList<>();
        assignmentAdapter = new AssignmentAdapterStudent(assignmentList);
        recyclerView.setAdapter(assignmentAdapter);

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
            Intent intent = new Intent(assignment_student.this, home_student.class);
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
                    .getReference("StudentAssignmentAlerts")
                    .child(studentUID);

            alertRef.child("lastSeenTimestamp")
                    .setValue(System.currentTimeMillis())
                    .addOnSuccessListener(aVoid ->
                            Log.d("AssignmentStudent", "Last seen timestamp updated"))
                    .addOnFailureListener(e ->
                            Log.e("AssignmentStudent", "Failed to update last seen timestamp: " + e.getMessage()));
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

                } else {
                    Toast.makeText(assignment_student.this, "Student details not found!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(assignment_student.this, "Failed to retrieve student data!", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void loadFacultyFilter(String program, String year, String semester, String division) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Assignments");

        facultyNameList.clear();
        facultyMap.clear();
        facultyNameList.add("All");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snap : snapshot.getChildren()) {
                    String assProgram = snap.child("program").getValue(String.class);
                    String assYear = snap.child("year").getValue(String.class);
                    String assSemester = snap.child("semester").getValue(String.class);
                    String assDivision = snap.child("division").getValue(String.class);

                    if (program.equals(assProgram) && year.equals(assYear)
                            && semester.equals(assSemester) && division.equals(assDivision)) {

                        String facultyName = snap.child("facultyName").getValue(String.class);
                        String facultyID = snap.child("facultyID").getValue(String.class);

                        if (facultyName != null && !facultyMap.containsKey(facultyName)) {
                            facultyMap.put(facultyName, facultyID);
                            facultyNameList.add(facultyName);
                        }
                    }
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(assignment_student.this,
                        android.R.layout.simple_spinner_item, facultyNameList);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerFacultyFilter.setAdapter(adapter);

                // Default load all
                fetchFilteredAssignment(program, year, semester, division, "All");

                spinnerFacultyFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        selectedFacultyName = facultyNameList.get(position);
                        fetchFilteredAssignment(program, year, semester, division, selectedFacultyName);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        Toast.makeText(assignment_student.this, "Nothing Is Selected", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void fetchFilteredAssignment(String program, String year, String semester, String division, String facultyNameFilter) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Assignments");
        List<AssignmentModelStudent> filteredList = new ArrayList<>();

        ref.orderByChild("timestamp").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snap : snapshot.getChildren()) {
                    String nProgram = snap.child("program").getValue(String.class);
                    String nYear = snap.child("year").getValue(String.class);
                    String nSemester = snap.child("semester").getValue(String.class);
                    String nDivision = snap.child("division").getValue(String.class);
                    String nFaculty = snap.child("facultyName").getValue(String.class);

                    if (program.equals(nProgram) && year.equals(nYear)
                            && semester.equals(nSemester) && division.equals(nDivision)) {

                        if (facultyNameFilter.equals("All") || facultyNameFilter.equals(nFaculty)) {
                            AssignmentModelStudent model = snap.getValue(AssignmentModelStudent.class);
                            filteredList.add(model);
                        }
                    }
                }

                // Sort by latest first
                Collections.sort(filteredList, (n1, n2) -> Long.compare(n2.getTimestamp(), n1.getTimestamp()));

                AssignmentAdapterStudent adapter = new AssignmentAdapterStudent(filteredList);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(assignment_student.this, home_student.class);
        startActivity(intent);
        finish();
    }
}