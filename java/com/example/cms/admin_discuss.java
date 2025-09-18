package com.example.cms;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class admin_discuss extends AppCompatActivity {

    private Spinner spinnerFaculty;
    private RecyclerView recyclerView;
    private EditText etMessage;
    private Button btnSend;

    String selectedFacultyId = "";
    String adminId = "";

    private DatabaseReference databaseReference;
    FirebaseAuth mAuth;

    private List<ChatModelStudent_FacultyDetails> facultyModelList = new ArrayList<>();
    private List<admin_chat_model> chatList;
    private admin_chat_adapter chatAdapter;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_discuss);

        spinnerFaculty = findViewById(R.id.spinnerFaculty);
        recyclerView = findViewById(R.id.recyclerViewChatAdmin);
        etMessage = findViewById(R.id.etMessage);
        btnSend = findViewById(R.id.btnSend);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        adminId = mAuth.getCurrentUser().getUid();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatList = new ArrayList<>();
        chatAdapter = new admin_chat_adapter(chatList, adminId);
        recyclerView.setAdapter(chatAdapter);

        loadFaculties();

        spinnerFaculty.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                ChatModelStudent_FacultyDetails selectedFaculty = facultyModelList.get(position);
                selectedFacultyId = selectedFaculty.getUid();
                loadMessagesWithFaculty(selectedFacultyId);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        btnSend.setOnClickListener(v -> {
            String messageText = etMessage.getText().toString().trim();
            if (messageText.isEmpty() || selectedFacultyId.isEmpty()) {
                Toast.makeText(admin_discuss.this, "Please enter message and select faculty", Toast.LENGTH_SHORT).show();
                return;
            }
            sendMessageToFaculty(selectedFacultyId, messageText);

        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(admin_discuss.this, home_admin.class);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadFaculties() {
        databaseReference.child("Faculties").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                facultyModelList.clear();

                facultyModelList.add(new ChatModelStudent_FacultyDetails("Select Faculty", "", ""));

                for (DataSnapshot facultySnap : snapshot.getChildren()) {
                    String uid = facultySnap.getKey();
                    String name = facultySnap.child("name").getValue(String.class);
                    String designation = facultySnap.child("designation").getValue(String.class);

                    if (name != null && designation != null && uid != null) {
                        facultyModelList.add(new ChatModelStudent_FacultyDetails(name, designation, uid));
                    }
                }

                ArrayAdapter<ChatModelStudent_FacultyDetails> adapter = new ArrayAdapter<>(
                        admin_discuss.this,
                        android.R.layout.simple_spinner_dropdown_item,
                        facultyModelList
                );
                spinnerFaculty.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(admin_discuss.this, "Failed to load faculties", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadMessagesWithFaculty(String facultyId) {
        String chatPath = adminId + "_" + facultyId;

        DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference()
                .child("AdminChats")
                .child(chatPath);

        chatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatList.clear();

                if (snapshot.hasChild("AdminToFaculty")) {
                    for (DataSnapshot snap : snapshot.child("AdminToFaculty").getChildren()) {
                        admin_chat_model model = snap.getValue(admin_chat_model.class);
//                        if (model != null) {
//                            model.setSender("student");
//                            chatList.add(model);
//                        }
                        chatList.add(model);
                    }
                }

                if (snapshot.hasChild("FacultyToAdmin")) {
                    for (DataSnapshot snap : snapshot.child("FacultyToAdmin").getChildren()) {
                        admin_chat_model model = snap.getValue(admin_chat_model.class);
//                        if (model != null) {
//                            model.setSender("student");
//                            chatList.add(model);
//                        }
                        // Update readStatus to true if unread
                        if (!model.isReadStatus()) {
                            snap.getRef().child("readStatus").setValue(true);
                        }
                        chatList.add(model);
                    }
                }

                Collections.sort(chatList, new Comparator<admin_chat_model>() {
                    @Override
                    public int compare(admin_chat_model o1, admin_chat_model o2) {
                        return o1.getTimestamp().compareTo(o2.getTimestamp());
                    }
                });

                chatAdapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(chatList.size() - 1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(admin_discuss.this, "Failed to load chat", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendMessageToFaculty(String facultyId, String messageText) {
        if (messageText.isEmpty()) {
            Toast.makeText(this, "Message can't be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        String chatPath = adminId + "_" + facultyId;
        String timeStamp = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date());
        boolean readStatus = false;

        DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference()
                .child("AdminChats")
                .child(chatPath)
                .child("AdminToFaculty");

        String messageId = chatRef.push().getKey();
        if (messageId == null) return;

        admin_chat_model message = new admin_chat_model(messageId, adminId, selectedFacultyId, messageText, timeStamp , readStatus);

        chatRef.child(messageId).setValue(message)
                .addOnSuccessListener(unused -> {
                    etMessage.setText("");
//                    loadMessagesWithFaculty(selectedFacultyId);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(admin_discuss.this, "Failed to send message", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(admin_discuss.this, home_admin.class);
        startActivity(intent);
        finish();
    }
}