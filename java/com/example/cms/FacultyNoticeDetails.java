package com.example.cms;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FacultyNoticeDetails extends AppCompatActivity {

    TextView tvTitle, tvDate, tvFaculty, tvContent, tvFilter , tvAutoDelete;
    String noticeId,title, content, date, division , program, semester, year, facultyName , autoDeleteDate;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_faculty_notice_details);

        tvTitle = findViewById(R.id.tvNoticeTitle);
        tvDate = findViewById(R.id.tvNoticeDate);
        tvFaculty = findViewById(R.id.tvFacultyName);
        tvContent = findViewById(R.id.tvNoticeContent);
        tvFilter = findViewById(R.id.tvNoticeFilter);
        tvAutoDelete = findViewById(R.id.tvNoticeAutoDelete);

        Intent intent = getIntent();
        noticeId = intent.getStringExtra("noticeId");
        title = intent.getStringExtra("title");
        content = intent.getStringExtra("content");
        date = intent.getStringExtra("date");
        program = intent.getStringExtra("program");
        semester = intent.getStringExtra("semester");
        year = intent.getStringExtra("year");
        division = intent.getStringExtra("division");
        facultyName = intent.getStringExtra("facultyName");
        autoDeleteDate = intent.getStringExtra("autoDeleteDate");

        tvTitle.setText("Title: " + title);
        tvContent.setText("Content: " + content);
        tvDate.setText("Date: " + date);
        tvFaculty.setText("Faculty: " + facultyName);
        tvFilter.setText("Sent to: " + program + " - " + year + " - " + semester + " - " + division);
        tvAutoDelete.setText("Auto Deletion Date: " + autoDeleteDate);

        findViewById(R.id.btnDeleteNotice).setOnClickListener(view -> showDeleteConfirmation());

        findViewById(R.id.btnEditNotice).setOnClickListener(view -> {
            Intent intent1 = new Intent(FacultyNoticeDetails.this, FacultyEditNotice.class);
            intent1.putExtra("noticeId", noticeId);
            intent1.putExtra("title", title);
            intent1.putExtra("content", content);
            intent1.putExtra("program", program);
            intent1.putExtra("year", year);
            intent1.putExtra("semester", semester);
            intent1.putExtra("division" , division);
            intent1.putExtra("facultyName", facultyName);
            intent1.putExtra("date", date);
            intent1.putExtra("autoDeleteDate" , autoDeleteDate);
            startActivity(intent1);
            finish();
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
            Intent intent = new Intent(FacultyNoticeDetails.this, notice_faculty.class);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDeleteConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Notice")
                .setMessage("Are you sure you want to delete this notice?")
                .setPositiveButton("Yes", (dialog, which) -> deleteNotice())
                .setNegativeButton("No", null)
                .show();
    }

    private void deleteNotice() {
        DatabaseReference noticeRef = FirebaseDatabase.getInstance().getReference("Notices");

        if (noticeId != null && !noticeId.isEmpty()) {
            noticeRef.child(noticeId).removeValue()
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Notice deleted successfully", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(this, notice_faculty.class));
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Failed to delete notice: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(this, "Notice ID not found!", Toast.LENGTH_SHORT).show();
        }
    }
}