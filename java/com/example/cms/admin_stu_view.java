package com.example.cms;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class admin_stu_view extends AppCompatActivity {

    private Spinner spinnerProgram, spinnerYear, spinnerSemester;
    private admin_stud_view_adapter adapter;
    private List<admin_stud_view_model> studentList;
    private DatabaseReference studentsRef;
    RecyclerView recyclerView;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_stu_view);


        spinnerProgram = findViewById(R.id.spProgram);
        spinnerSemester = findViewById(R.id.spSemester);
        spinnerYear = findViewById(R.id.spYear);
        recyclerView = findViewById(R.id.recyclerStudView);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        studentList = new ArrayList<>();
        adapter = new admin_stud_view_adapter(studentList);
        recyclerView.setAdapter(adapter);

        studentsRef = FirebaseDatabase.getInstance().getReference("Students");

        setupSpinners();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(admin_stu_view.this, admin_users_view.class);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupSpinners() {
        ArrayAdapter<CharSequence> programAdapter = ArrayAdapter.createFromResource(this, R.array.program_array, android.R.layout.simple_spinner_item);
        programAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerProgram.setAdapter(programAdapter);

        ArrayAdapter<CharSequence> yearAdapter = ArrayAdapter.createFromResource(this, R.array.year_semester_array, android.R.layout.simple_spinner_item);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerYear.setAdapter(yearAdapter);

        ArrayAdapter<CharSequence> semesterAdapter = ArrayAdapter.createFromResource(this, R.array.all_semester_array, android.R.layout.simple_spinner_item);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSemester.setAdapter(semesterAdapter);

        AdapterView.OnItemSelectedListener fetchListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                fetchStudents();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        };

        spinnerProgram.setOnItemSelectedListener(fetchListener);
        spinnerYear.setOnItemSelectedListener(fetchListener);
        spinnerSemester.setOnItemSelectedListener(fetchListener);
    }

    private void fetchStudents() {
        String selectedProgram = spinnerProgram.getSelectedItem().toString();
        String selectedYear = spinnerYear.getSelectedItem().toString();
        String selectedSemester = spinnerSemester.getSelectedItem().toString();

        studentsRef.orderByChild("program").equalTo(selectedProgram).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                studentList.clear();
                for (DataSnapshot studentSnapshot : snapshot.getChildren()) {
                    String semester = studentSnapshot.child("semester").getValue(String.class);
                    String year = studentSnapshot.child("year").getValue(String.class);
                    if (year != null && year.equals(selectedYear) && semester != null && semester.equals(selectedSemester)) {
                        String name = studentSnapshot.child("name").getValue(String.class);
                        String studentId = studentSnapshot.child("studentId").getValue(String.class);
                        String contact = studentSnapshot.child("contact").getValue(String.class);
                        studentList.add(new admin_stud_view_model(name, studentId, contact));
                    }
                }
                adapter.notifyDataSetChanged();
                if (studentList.isEmpty()) {
                    Toast.makeText(admin_stu_view.this, "No students found!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(admin_stu_view.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


}