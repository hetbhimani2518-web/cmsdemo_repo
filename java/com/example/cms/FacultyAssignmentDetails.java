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

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FacultyAssignmentDetails extends AppCompatActivity {

    TextView tvTitle, tvSentDate, tvFaculty, tvDesc, tvFilter, tvAutoDelete, tvDeadline, tvDrivlink;
    String assignmentId, title, description, deadline, sentdate, division, program, semester, year, facultyName, autoDeleteDate, drivelink;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_faculty_assignment_details);

        tvTitle = findViewById(R.id.tvAssignmentDTitle);
        tvSentDate = findViewById(R.id.tvAssignmentDSentDate);
        tvFaculty = findViewById(R.id.tvFacultyName);
        tvDesc = findViewById(R.id.tvAssignmentDDesc);
        tvFilter = findViewById(R.id.tvAssignmentDFilter);
        tvAutoDelete = findViewById(R.id.tvAssignmentDAutoDelete);
        tvDeadline = findViewById(R.id.tvAssignmentDDeadline);
        tvDrivlink = findViewById(R.id.tvAssignmentDDrivelink);


        Intent intent = getIntent();
        assignmentId = intent.getStringExtra("assignmentID");
        title = intent.getStringExtra("title");
        description = intent.getStringExtra("description");
        deadline = intent.getStringExtra("deadline");
        drivelink = intent.getStringExtra("drivelink");
        sentdate = intent.getStringExtra("sentDate");
        program = intent.getStringExtra("program");
        semester = intent.getStringExtra("semester");
        year = intent.getStringExtra("year");
        division = intent.getStringExtra("division");
        facultyName = intent.getStringExtra("facultyName");
        autoDeleteDate = intent.getStringExtra("autoDeleteDate");

        tvTitle.setText("Title: " + title);
        tvSentDate.setText("Sent Date: " + sentdate);
        tvFaculty.setText("Sent By: " + facultyName);
        tvDesc.setText("Description: " + description);
        tvFilter.setText("Sent To: " + program + " - " + year + " - " + semester + " - " + division);
        tvAutoDelete.setText("Auto Deletion Date: " + autoDeleteDate);
        tvDeadline.setText("Deadline: " + deadline);
        tvDrivlink.setText("DriveLink: " + drivelink);

        findViewById(R.id.btnDeleteassignment).setOnClickListener(view -> showDeleteConfirmation());

        findViewById(R.id.btnEditAssignemnt).setOnClickListener(view -> {
            Intent intent1 = new Intent(FacultyAssignmentDetails.this, FacultyEditAssignment.class);
            intent.putExtra("title", title);
            intent.putExtra("description", description);
            intent.putExtra("deadline", deadline);
            intent.putExtra("drivelink", drivelink);
            intent.putExtra("program", program);
            intent.putExtra("semester", semester);
            intent.putExtra("year", year);
            intent.putExtra("division", division);
            intent.putExtra("facultyName", facultyName);
            intent.putExtra("assignmentID", assignmentId);
            intent.putExtra("sentDate", sentdate);
            intent.putExtra("autoDeleteDate", autoDeleteDate);
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

            Intent intent = new Intent(FacultyAssignmentDetails.this, assignment_faculty.class);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDeleteConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Assignment")
                .setMessage("Are you sure you want to delete this assignment?")
                .setPositiveButton("Yes", (dialog, which) -> deleteAssignment())
                .setNegativeButton("No", null)
                .show();
    }

    private void deleteAssignment() {
        DatabaseReference assignmentRef = FirebaseDatabase.getInstance().getReference("Assignments");

        if (assignmentId != null && !assignmentId.isEmpty()) {
            assignmentRef.child(assignmentId).removeValue()
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Assignment deleted successfully", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(this, assignment_faculty.class));
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Failed to delete assignment: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(this, "Assignment ID not found!", Toast.LENGTH_SHORT).show();
        }
    }
}