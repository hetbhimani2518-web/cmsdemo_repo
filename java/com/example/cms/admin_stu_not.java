package com.example.cms;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class admin_stu_not extends AppCompatActivity {

    private Spinner spinnerCourse, spinnerSemester ;
    private EditText etNoticeTitle, etNoticeContent ;
    private Button btnSendNotice;
    private DatabaseReference noticeRef;
    private String selectedCourse, selectedSemester , adminName;

    DatabaseReference adminRef;
    FirebaseAuth auth;

    Calendar calendar;
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_stu_not);

        auth = FirebaseAuth.getInstance();
        adminRef = FirebaseDatabase.getInstance().getReference("Admin");

        fetchAdminDetails();

        spinnerCourse = findViewById(R.id.spinnerCourse);
        spinnerSemester = findViewById(R.id.spinnerSemester);
        etNoticeTitle = findViewById(R.id.etNoticeTitle);
        etNoticeContent = findViewById(R.id.etNoticeContent);
        btnSendNotice = findViewById(R.id.btnSendNotice);

        ArrayAdapter<CharSequence> programAdapter = ArrayAdapter.createFromResource(this, R.array.program_array, android.R.layout.simple_spinner_item);
        programAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCourse.setAdapter(programAdapter);

        ArrayAdapter<CharSequence> yearAdapter = ArrayAdapter.createFromResource(this, R.array.all_semester_array, android.R.layout.simple_spinner_item);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSemester.setAdapter(yearAdapter);

        spinnerCourse.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCourse = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        spinnerSemester.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedSemester = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        noticeRef = FirebaseDatabase.getInstance().getReference("AdminToStudentNotice");

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
//
//    private String getProgramShortForm(String program) {
//        switch (program) {
//            case "Bachelor of Computer Application":
//                return "BCA";
//            case "Bachelor of Business Administration":
//                return "BBA";
//            case "Bachelor of Commerce":
//                return "BCOM";
//            default:
//                return "UNK"; // Unknown Program
//        }
//    }
//
//    private String getSemesterShortForm(String semester) {
//        return semester.replace("Semester ", "SEM"); // Convert "Semester 1" -> "SEM1"
//    }

    private void sendNotice() {

        String course = spinnerCourse.getSelectedItem().toString();
        String semester = spinnerSemester.getSelectedItem().toString();
        String title = etNoticeTitle.getText().toString().trim();
        String content = etNoticeContent.getText().toString().trim();

        if (title.isEmpty() || content.isEmpty() ) {
            Toast.makeText(this, "All fields are required!", Toast.LENGTH_SHORT).show();
            return;
        }

        String timestamp = String.valueOf(System.currentTimeMillis());
        String currentDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());

        HashMap<String, String> noticeData = new HashMap<>();
        noticeData.put("course", course);
        noticeData.put("semester", semester);
        noticeData.put("title", title);
        noticeData.put("content", content);
        noticeData.put("date", currentDate);
        noticeData.put("timestamp", timestamp);
        noticeData.put("adminName" , adminName);
        noticeData.put("course_semester", selectedCourse + "_" + selectedSemester);

        noticeRef.push().setValue(noticeData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(admin_stu_not.this, "Notice Sent To Student Successfully", Toast.LENGTH_SHORT).show();
                    clearFields();
                })
                .addOnFailureListener(e -> Toast.makeText(admin_stu_not.this, "Failed to send notice", Toast.LENGTH_SHORT).show());
    }

    private void fetchAdminDetails(){
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(admin_stu_not.this, login_admin.class));
            finish();
            return;
        }

        String userId = user.getUid();

        adminRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    adminName = snapshot.child("name").getValue(String.class);

                } else {
                    Toast.makeText(admin_stu_not.this, "Admin details not found!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(admin_stu_not.this, "Database Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Navigate back to home screen and keep the drawer open
            Intent intent = new Intent(admin_stu_not.this, admin_notice_sel.class);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(admin_stu_not.this, admin_notice_sel.class);
        startActivity(intent);
        finish();
    }

    private void clearFields() {
        etNoticeTitle.setText("");
        etNoticeContent.setText("");
    }

}