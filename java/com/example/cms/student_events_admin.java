package com.example.cms;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class student_events_admin extends AppCompatActivity {

    private RecyclerView recyclerView;
    private student_events_adapter_admin adapter;
    private ArrayList<student_events_model_admin> eventList;
    private DatabaseReference eventRef;
    private String studentCourse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_student_events_admin);

        recyclerView = findViewById(R.id.recyclerViewAdminEvents);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        eventList = new ArrayList<>();
        adapter = new student_events_adapter_admin(eventList);
        recyclerView.setAdapter(adapter);

        eventRef = FirebaseDatabase.getInstance().getReference("AdminToStudentsEvents");
        getStudentDetails();


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

    }

    private void getStudentDetails() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference studentRef = FirebaseDatabase.getInstance().getReference("Students").child(uid);

        studentRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    studentCourse = snapshot.child("program").getValue(String.class);
                    fetchEvents();
                } else {
                    Toast.makeText(student_events_admin.this, "Student details not found!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(student_events_admin.this, "Failed to load student details", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchEvents() {
        if (studentCourse == null) {
            Toast.makeText(this, "Course not found!", Toast.LENGTH_SHORT).show();
            return;
        }

        eventRef.orderByChild("course").equalTo(studentCourse)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        eventList.clear();
                        for (DataSnapshot data : snapshot.getChildren()) {
                            student_events_model_admin event = data.getValue(student_events_model_admin.class);
                            if (event != null) {
                                eventList.add(event);
                            }
                        }
                        Collections.reverse(eventList); // Show latest first
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(student_events_admin.this, "Failed to load events", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(student_events_admin.this, home_student.class);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(student_events_admin.this, home_student.class);
        startActivity(intent);
        finish();
    }
}