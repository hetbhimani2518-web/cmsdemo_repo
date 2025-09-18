package com.example.cms;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

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
import java.util.List;

public class alerts_faculty extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AlertChatAdapterFaculty alertAdapter;
    private List<AlertChatModelFaculty> alertList;
    private DatabaseReference chatRef;
    TextView noNewMessages;
    private String facultyId;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_alerts_faculty);

        noNewMessages = findViewById(R.id.tvNoChatAlertFaculty);
        recyclerView = findViewById(R.id.rvChatAlertsFaculty);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        alertList = new ArrayList<>();
        alertAdapter = new AlertChatAdapterFaculty(alertList);
        recyclerView.setAdapter(alertAdapter);

        facultyId = FirebaseAuth.getInstance().getCurrentUser().getUid();
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
            Intent intent = new Intent(alerts_faculty.this, home_faculty.class);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(alerts_faculty.this, home_faculty.class);
        startActivity(intent);
        finish();
    }
//    private void loadChatAlerts() {
//        chatsRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                alertList.clear();
//                for (DataSnapshot chatSnapshot : snapshot.getChildren()) {
//                    if (chatSnapshot.getKey().contains(facultyId)) {
//                        for (DataSnapshot messageSnapshot : chatSnapshot.child("StudentToFaculty").getChildren()) {
//                            String senderId = messageSnapshot.child("senderId").getValue(String.class);
//                            String timestamp = messageSnapshot.child("timestamp").getValue(String.class);
//                            boolean readStatus = messageSnapshot.child("readStatus").getValue(Boolean.class);
//
//                            studentsRef.child(senderId).addListenerForSingleValueEvent(new ValueEventListener() {
//                                @Override
//                                public void onDataChange(@NonNull DataSnapshot studentSnapshot) {
//                                    String studentName = studentSnapshot.child("name").getValue(String.class);
//                                    String studentId = studentSnapshot.child("studentId").getValue(String.class);
//
//                                    int unreadCount = readStatus ? 0 : 1;
//                                    alertList.add(new AlertChatModelFaculty(studentName, studentId, timestamp, unreadCount));
//                                    alertAdapter.notifyDataSetChanged();
//                                }
//
//                                @Override
//                                public void onCancelled(@NonNull DatabaseError error) {}
//                            });
//                        }
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {}
//        });
//    }

    private void fetchUnreadMessages() {
        chatRef.orderByKey().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                alertList.clear();
                for (DataSnapshot chatSnapshot : snapshot.getChildren()) {
                    String chatKey = chatSnapshot.getKey();
                    if (chatKey != null && chatKey.contains("_")) {
                        String[] keyParts = chatKey.split("_");
                        if (keyParts.length == 2 && keyParts[1].equals(facultyId)) { // Check if facultyId matches
                            String studentUId = keyParts[0]; // Extract student ID
                            DatabaseReference studentToFacultyRef = chatSnapshot.child("StudentToFaculty").getRef();

                            studentToFacultyRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot facultySnapshot) {
                                    int unreadCount = 0;
                                    String timestamp = "N/A";
                                    for (DataSnapshot msgSnapshot : facultySnapshot.getChildren()) {
                                        Boolean readStatus = msgSnapshot.child("readStatus").getValue(Boolean.class);
                                        if (readStatus != null && !readStatus) {
                                            unreadCount++;
                                            timestamp = msgSnapshot.child("timestamp").getValue(String.class);
                                        }
                                    }
                                    if (unreadCount > 0) {
                                        fetchStudentName(studentUId, timestamp, unreadCount);
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
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", error.getMessage());
            }
        });

    }

    private void fetchStudentName(String studentUId, String timestamp, int unreadCount) {
        DatabaseReference studentsRef = FirebaseDatabase.getInstance().getReference("Students").child(studentUId);
//        studentsRef.child("name").addListenerForSingleValueEvent(new ValueEventListener() {
        studentsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String studentName = snapshot.child("name").getValue(String.class);
                String studentId = snapshot.child("studentId").getValue(String.class);
                alertList.add(new AlertChatModelFaculty(studentName, studentId, timestamp, unreadCount));
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
            recyclerView.setVisibility(View.GONE);
        } else {
            noNewMessages.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            alertAdapter.notifyDataSetChanged();
        }
    }

}