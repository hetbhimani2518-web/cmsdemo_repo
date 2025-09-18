package com.example.cms;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class admin_fac_not extends AppCompatActivity {

    private Spinner spinnerFaculty;
    private EditText etNoticeTitle, etNoticeContent;
    private Button btnSendNotice;
    private DatabaseReference noticeRef, facultyRef;
    private List<String> facultyList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_fac_not);

        spinnerFaculty = findViewById(R.id.spinnerFaculty);
        etNoticeTitle = findViewById(R.id.etNoticeTitle);
        etNoticeContent = findViewById(R.id.etNoticeContent);
        btnSendNotice = findViewById(R.id.btnSendNotice);

        facultyRef = FirebaseDatabase.getInstance().getReference("Faculties");
        noticeRef = FirebaseDatabase.getInstance().getReference("AdminToFacultyNotices");
        facultyList = new ArrayList<>();
        loadFacultyNames();

        btnSendNotice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendNotice();
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
            // Navigate back to home screen and keep the drawer open
            Intent intent = new Intent(admin_fac_not.this, admin_notice_sel.class);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(admin_fac_not.this, admin_notice_sel.class);
        startActivity(intent);
        finish();
    }

    private void clearFields() {
        etNoticeTitle.setText("");
        etNoticeContent.setText("");
    }

    private void loadFacultyNames() {
        facultyRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                facultyList.clear();
                for (DataSnapshot facultySnapshot : snapshot.getChildren()) {
                    String facultyName = facultySnapshot.child("name").getValue(String.class);
                    if (facultyName != null) {
                        facultyList.add(facultyName);
                    }
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(admin_fac_not.this, android.R.layout.simple_spinner_dropdown_item, facultyList);
                spinnerFaculty.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(admin_fac_not.this, "Failed to load faculty", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendNotice() {
        String selectedFaculty = spinnerFaculty.getSelectedItem().toString();
        String title = etNoticeTitle.getText().toString().trim();
        String content = etNoticeContent.getText().toString().trim();
        String date = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());

        if (selectedFaculty.isEmpty() || title.isEmpty() || content.isEmpty()) {
            Toast.makeText(this, "Please enter all details", Toast.LENGTH_SHORT).show();
            return;
        }

        String noticeId = noticeRef.push().getKey();
        HashMap<String, String> noticeData = new HashMap<>();
        noticeData.put("facultyName", selectedFaculty);
        noticeData.put("title", title);
        noticeData.put("content", content);
        noticeData.put("date", date);

        noticeRef.child(noticeId).setValue(noticeData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(admin_fac_not.this, "Notice Sent Successfully", Toast.LENGTH_SHORT).show();
                    clearFields();
                })
                .addOnFailureListener(e -> Toast.makeText(admin_fac_not.this, "Failed to send notice", Toast.LENGTH_SHORT).show());
    }

}