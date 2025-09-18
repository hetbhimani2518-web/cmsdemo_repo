package com.example.cms;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
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

public class discussion_faculty_chatdisplay extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EditText messageInput;
    private Button sendButton;

    private FirebaseAuth mAuth;
    private String facultyId, studentId;
    private DatabaseReference chatRef;

    private List<ChatModelFaculty> chatList;
    private ChatAdapterFaculty chatAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_discussion_faculty_chatdisplay);

        recyclerView = findViewById(R.id.recyclerView);
        messageInput = findViewById(R.id.editMessage);
        sendButton = findViewById(R.id.btnSend);

        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() == null) {
            Toast.makeText(this, "Faculty Not logged in", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        facultyId = mAuth.getCurrentUser().getUid();
        studentId = getIntent().getStringExtra("studentId");

        if (studentId == null || studentId.isEmpty()) {
            Toast.makeText(this, "Invalid student ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        String chatPath = studentId + "_" + facultyId;

        chatList = new ArrayList<>();
        chatAdapter = new ChatAdapterFaculty(chatList, facultyId);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(chatAdapter);

        chatRef = FirebaseDatabase.getInstance().getReference("Chats").child(chatPath);

        loadMessages();

        sendButton.setOnClickListener(view -> {
            String message = messageInput.getText().toString().trim();
            if (!TextUtils.isEmpty(message)) {
                sendMessageToStudent(message);
                messageInput.setText("");
            }
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
            Intent intent = new Intent(discussion_faculty_chatdisplay.this, discussion_faculty.class);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadMessages() {
        chatList.clear();

        chatRef.child("StudentToFaculty").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snap : snapshot.getChildren()) {
                    ChatModelFaculty chat = snap.getValue(ChatModelFaculty.class);
                    if (chat != null) {
                        chatList.add(chat);
                    }
                }

                chatRef.child("FacultyToStudent").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot snap : snapshot.getChildren()) {
                            ChatModelFaculty chat = snap.getValue(ChatModelFaculty.class);
                            if (chat != null) {
                                chatList.add(chat);
                            }
                        }

                        // Sort by timestamp (Assuming it's in "dd/MM/yyyy HH:mm" format)
                        Collections.sort(chatList, new Comparator<ChatModelFaculty>() {
                            @Override
                            public int compare(ChatModelFaculty o1, ChatModelFaculty o2) {
                                return o1.getTimestamp().compareTo(o2.getTimestamp());
                            }
                        });

                        chatAdapter.notifyDataSetChanged();
                        recyclerView.scrollToPosition(chatList.size() - 1);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(discussion_faculty_chatdisplay.this, "Failed to load messages", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(discussion_faculty_chatdisplay.this, "Failed to load messages", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendMessageToStudent(String messageText) {
        String timeStamp = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date());
        String messageId = chatRef.child("FacultyToStudent").push().getKey();
        if (messageId == null) return;

        boolean readStatus = false;

        ChatModelFaculty chat = new ChatModelFaculty(messageId, messageText, facultyId, studentId, timeStamp , readStatus);

        chatRef.child("FacultyToStudent").child(messageId).setValue(chat).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                loadMessages();
            } else {
                Toast.makeText(this, "Failed to send message", Toast.LENGTH_SHORT).show();
            }
        });
    }
}