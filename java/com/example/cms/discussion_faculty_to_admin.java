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
import android.widget.SearchView;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class discussion_faculty_to_admin extends AppCompatActivity {

    private Spinner spinnerAdmin;
    private EditText etMessage;
    private Button btnSend;
    RecyclerView recyclerViewFchat;

    String selectedAdminId = "";
    String facultyId = "";

    private DatabaseReference databaseReference;
    FirebaseAuth mAuth;

    private List<ChatModelFaculty_AdminDetails> adminModelList = new ArrayList<>();
    private List<faculty_admin_chat_model> chatList;
    private faculty_admin_chat_adapter chatAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_discussion_faculty_to_admin);

        spinnerAdmin = findViewById(R.id.spinnerAdmin);
        recyclerViewFchat = findViewById(R.id.recyclerViewChatFac);
        etMessage = findViewById(R.id.etMessage);
        btnSend = findViewById(R.id.btnSend);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        facultyId = mAuth.getCurrentUser().getUid();

        recyclerViewFchat.setLayoutManager(new LinearLayoutManager(this));
        chatList = new ArrayList<>();
        chatAdapter = new faculty_admin_chat_adapter(chatList, facultyId);
        recyclerViewFchat.setAdapter(chatAdapter);

        loadAdmin();

        spinnerAdmin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                ChatModelFaculty_AdminDetails selectedAdmin = adminModelList.get(position);
                selectedAdminId = selectedAdmin.getUid();
                loadMessagesWithAdmin(selectedAdminId);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        btnSend.setOnClickListener(v -> {
            String messageText = etMessage.getText().toString().trim();
            if (messageText.isEmpty() || selectedAdminId.isEmpty()) {
                Toast.makeText(discussion_faculty_to_admin.this, "Please enter message and select faculty", Toast.LENGTH_SHORT).show();
                return;
            }
            sendMessageToAdmin(selectedAdminId, messageText);

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
            Intent intent = new Intent(discussion_faculty_to_admin.this, Faculty_Discussion_selection.class);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadAdmin() {
        databaseReference.child("Admin").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                adminModelList.clear();

                adminModelList.add(new ChatModelFaculty_AdminDetails("Select Admin", "" ));

                for (DataSnapshot facultySnap : snapshot.getChildren()) {
                    String uid = facultySnap.getKey();
                    String name = facultySnap.child("name").getValue(String.class);

                    if (name != null && uid != null) {
                        adminModelList.add(new ChatModelFaculty_AdminDetails(name, uid));
                    }
                }

                ArrayAdapter<ChatModelFaculty_AdminDetails> adapter = new ArrayAdapter<>(
                        discussion_faculty_to_admin.this,
                        android.R.layout.simple_spinner_dropdown_item,
                        adminModelList
                );
                spinnerAdmin.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(discussion_faculty_to_admin.this, "Failed to load Admin", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadMessagesWithAdmin(String adminId) {
        String chatPath = adminId + "_" + facultyId;

        DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference()
                .child("AdminChats")
                .child(chatPath);

        chatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatList.clear();

                if (snapshot.hasChild("FacultyToAdmin")) {
                    for (DataSnapshot snap : snapshot.child("FacultyToAdmin").getChildren()) {
                        faculty_admin_chat_model model = snap.getValue(faculty_admin_chat_model.class);
//                        if (model != null) {
//                            model.setSender("student");
//                            chatList.add(model);
//                        }
                        chatList.add(model);
                    }
                }

                if (snapshot.hasChild("AdminToFaculty")) {
                    for (DataSnapshot snap : snapshot.child("AdminToFaculty").getChildren()) {
                        faculty_admin_chat_model model = snap.getValue(faculty_admin_chat_model.class);
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

                Collections.sort(chatList, new Comparator<faculty_admin_chat_model>() {
                    @Override
                    public int compare(faculty_admin_chat_model o1, faculty_admin_chat_model o2) {
                        return o1.getTimestamp().compareTo(o2.getTimestamp());
                    }
                });

                chatAdapter.notifyDataSetChanged();
                recyclerViewFchat.scrollToPosition(chatList.size() - 1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(discussion_faculty_to_admin.this, "Failed to load chat", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendMessageToAdmin(String selectedAdminId, String messageText) {
        if (messageText.isEmpty()) {
            Toast.makeText(this, "Message can't be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        String chatPath = selectedAdminId + "_" + facultyId;
        String timeStamp = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date());
        boolean readStatus = false;

        DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference()
                .child("AdminChats")
                .child(chatPath)
                .child("FacultyToAdmin");

        String messageId = chatRef.push().getKey();
        if (messageId == null) return;

        faculty_admin_chat_model message = new faculty_admin_chat_model(messageId, facultyId, selectedAdminId, messageText, timeStamp, readStatus);

        chatRef.child(messageId).setValue(message)
                .addOnSuccessListener(unused -> {
                    etMessage.setText("");
//                    loadMessagesWithFaculty(selectedFacultyId);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(discussion_faculty_to_admin.this, "Failed to send message", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(discussion_faculty_to_admin.this, Faculty_Discussion_selection.class);
        startActivity(intent);
        finish();
    }
}