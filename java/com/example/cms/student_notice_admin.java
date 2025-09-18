package com.example.cms;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

public class student_notice_admin extends AppCompatActivity {

    private RecyclerView recyclerView;
    private student_notice_adapter_admin adapter;
    private ArrayList<student_notice_model_admin> noticeList;
    private DatabaseReference noticeRef;
    private String studentCourse, studentSemester;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_student_notice_admin);

        recyclerView = findViewById(R.id.recyclerViewAdminNotices);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        noticeList = new ArrayList<>();
        adapter = new student_notice_adapter_admin(noticeList);
        recyclerView.setAdapter(adapter);

        noticeRef = FirebaseDatabase.getInstance().getReference("AdminToStudentNotice");

        getStudentDetails();

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
            // Navigate back to home screen and keep the drawer open
            Intent intent = new Intent(student_notice_admin.this, student_notice_selection.class);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(student_notice_admin.this, student_notice_selection.class);
        startActivity(intent);
        finish();
    }

    private void updateLastSeenTimestamp() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String studentUID = user.getUid();

            DatabaseReference alertRef = FirebaseDatabase.getInstance()
                    .getReference("StudentNotice_AdminAlerts")
                    .child(studentUID);

            alertRef.child("lastSeenTimestamp")
                    .setValue(System.currentTimeMillis())
                    .addOnSuccessListener(aVoid ->
                            Log.d("NoticeStudent", "Last seen timestamp updated"))
                    .addOnFailureListener(e ->
                            Log.e("NoticeStudent", "Failed to update last seen timestamp: " + e.getMessage()));
        }
    }

    private void getStudentDetails() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid(); // Get logged-in user's UID
        DatabaseReference studentRef = FirebaseDatabase.getInstance().getReference("Students").child(uid);

        studentRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    studentCourse = snapshot.child("program").getValue(String.class);
                    studentSemester = snapshot.child("semester").getValue(String.class);
                    fetchNotices();
                } else {
                    Toast.makeText(student_notice_admin.this, "Student details not found!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(student_notice_admin.this, "Failed to fetch student details", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchNotices() {
        if (studentCourse == null || studentSemester == null) {
            Toast.makeText(student_notice_admin.this, "Incomplete student info", Toast.LENGTH_SHORT).show();
            return;
        }

        String filterKey = studentCourse + "_" + studentSemester;

        noticeRef.orderByChild("course_semester").equalTo(filterKey)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        noticeList.clear();
                        long currentTime = System.currentTimeMillis();
                        long expiryTime = 30L * 24 * 60 * 60 * 1000; // 30 days

                        for (DataSnapshot data : snapshot.getChildren()) {
                            student_notice_model_admin notice = data.getValue(student_notice_model_admin.class);
                            if (notice != null) {
                                long noticeTime = parseDateToMillis(notice.getDate());
                                if (currentTime - noticeTime <= expiryTime) {
                                    noticeList.add(notice);
                                } else {
                                    data.getRef().removeValue(); // Remove old notice
                                }
                            }
                        }

                        Collections.reverse(noticeList);
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(student_notice_admin.this, "Failed to load notices", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private long parseDateToMillis(String dateString) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date date = sdf.parse(dateString);
            return (date != null) ? date.getTime() : 0;
        } catch (Exception e) {
            return 0;
        }
    }

}