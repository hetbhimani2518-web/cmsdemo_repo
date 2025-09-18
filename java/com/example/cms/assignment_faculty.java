package com.example.cms;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import androidx.recyclerview.widget.LinearLayoutManager;
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
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class assignment_faculty extends AppCompatActivity {

    private Spinner spYear, spSemester, spProgram, spDivision;
    private EditText etAssignmentTitle, etAssignmentDescription, etAssignmentDeadline, etAssignmentLink, etAutoDeleteDate;
    TextView tvFacultyName, tvCurrentDate;
    RecyclerView rvAssignments;
    Button btnUploadAssignment;

    private String selectedAutoDeleteDate = null;
    private String facultyName = "", facultyID = "", currentDate;
    DatabaseReference assignmentRef, facultyRef;
    FirebaseAuth auth;

    private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    Calendar calendar;

    AssignmentAdapterFaculty adapter;
    List<AssignmentModelFaculty> assignmentList;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_assignment_faculty);

        auth = FirebaseAuth.getInstance();
        tvFacultyName = findViewById(R.id.tvFcNameAss);
        facultyRef = FirebaseDatabase.getInstance().getReference("Faculties");
        loadFacultyInfo();

        tvCurrentDate = findViewById(R.id.tvAssignmentDate);
        currentDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
        tvCurrentDate.setText("Date: " + currentDate);
        calendar = Calendar.getInstance();

        spYear = findViewById(R.id.spYear_ass);
        spSemester = findViewById(R.id.spSemester_ass);
        spProgram = findViewById(R.id.spProgram_ass);
        spDivision = findViewById(R.id.spDivison_ass);

        ArrayAdapter<CharSequence> programAdapter = ArrayAdapter.createFromResource(this, R.array.program_array, android.R.layout.simple_spinner_item);
        programAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spProgram.setAdapter(programAdapter);

        ArrayAdapter<CharSequence> yearAdapter = ArrayAdapter.createFromResource(this, R.array.year_semester_array, android.R.layout.simple_spinner_item);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spYear.setAdapter(yearAdapter);

        spYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ArrayAdapter<CharSequence> semesterAdapter;

                switch (position) {
                    case 0: // First Year
                        semesterAdapter = ArrayAdapter.createFromResource(
                                assignment_faculty.this, R.array.first_year_semesters, android.R.layout.simple_spinner_item);
                        break;
                    case 1: // Second Year
                        semesterAdapter = ArrayAdapter.createFromResource(
                                assignment_faculty.this, R.array.second_year_semesters, android.R.layout.simple_spinner_item);
                        break;
                    case 2: // Third Year
                        semesterAdapter = ArrayAdapter.createFromResource(
                                assignment_faculty.this, R.array.third_year_semesters, android.R.layout.simple_spinner_item);
                        break;
                    default:
                        semesterAdapter = null;
                }

                if (semesterAdapter != null) {
                    semesterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spSemester.setAdapter(semesterAdapter);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        ArrayAdapter<CharSequence> divAdapter = ArrayAdapter.createFromResource(this, R.array.division_array, android.R.layout.simple_spinner_item);
        divAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDivision.setAdapter(divAdapter);

        etAssignmentTitle = findViewById(R.id.etAssignmentTitle);
        etAssignmentDescription = findViewById(R.id.etAssignmentDesc);
        etAssignmentDeadline = findViewById(R.id.etDeadline);
        etAssignmentDeadline.setOnClickListener(v -> showDatePicker(etAssignmentDeadline));
        etAssignmentLink = findViewById(R.id.etDriveLink);

        btnUploadAssignment = findViewById(R.id.btn_upload);
        assignmentRef = FirebaseDatabase.getInstance().getReference("Assignments");
        btnUploadAssignment.setOnClickListener(v -> showAutoDeleteDialog());

        rvAssignments = findViewById(R.id.recyclerViewAssignments);
        rvAssignments.setLayoutManager(new LinearLayoutManager(this));
        assignmentList = new ArrayList<>();
        adapter = new AssignmentAdapterFaculty(this, assignmentList);
        rvAssignments.setAdapter(adapter);

        deleteExpiredNotices();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(assignment_faculty.this, home_faculty.class);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteExpiredNotices() {
        assignmentRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snap : snapshot.getChildren()) {
                    AssignmentModelFaculty assignment = snap.getValue(AssignmentModelFaculty.class);
                    if (assignment != null) {
                        try {
                            Date current = sdf.parse(sdf.format(Calendar.getInstance().getTime()));
                            Date deleteDate = sdf.parse(assignment.getAutoDelete());

                            if (deleteDate != null && current != null && deleteDate.before(current)) {
                                assignmentRef.child(assignment.getAssignmentId()).removeValue();
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(assignment_faculty.this, "Failed to check expired notices!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadFacultyInfo() {
        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser == null) {
            // Redirect to login screen if user is not logged in
            Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(assignment_faculty.this, login_faculty.class));
            finish();
            return;
        }

        String uid = currentUser.getUid();

        facultyRef.orderByChild("uid").equalTo(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot facultySnapshot : snapshot.getChildren()) {
                                facultyID = facultySnapshot.child("facultyID").getValue(String.class);
                                facultyName = facultySnapshot.child("name").getValue(String.class);

                                tvFacultyName.setText(facultyName);

                                if (facultyID != null) {
                                    fetchAssignments(); // Fetch past notices after getting facultyID
                                }
                            }
                        } else {
                            Toast.makeText(assignment_faculty.this, "Faculty details not found!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(assignment_faculty.this, "Failed to load faculty data", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showAutoDeleteDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Auto Delete Date")
                .setMessage("Do you want to keep the deadline and auto-delete date same?")
                .setPositiveButton("Yes, keep same", (dialog, which) -> uploadAssignment(true))
                .setNegativeButton("No", (dialog, which) -> showAutoDeleteDatePicker())
                .show();
    }

    private void showAutoDeleteDatePicker() {
        etAutoDeleteDate = new EditText(this);
        etAutoDeleteDate.setHint("Select Auto Delete Date (dd/MM/yyyy)");
        etAutoDeleteDate.setFocusable(false);

        etAutoDeleteDate.setOnClickListener(v -> showDatePicker(etAutoDeleteDate));

        LinearLayout layout = new LinearLayout(this);
        layout.setPadding(40, 30, 40, 30);
        layout.addView(etAutoDeleteDate);

        new AlertDialog.Builder(this)
                .setTitle("Custom Auto Delete Date")
                .setView(layout)
                .setPositiveButton("Upload", (dialog, which) -> uploadAssignment(false))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showDatePicker(EditText targetEditText) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                assignment_faculty.this,
                (view, year, month, dayOfMonth) -> {
                    String dateStr = dayOfMonth + "/" + (month + 1) + "/" + year;
                    targetEditText.setText(dateStr);
                    if (targetEditText == etAutoDeleteDate) {
                        selectedAutoDeleteDate = dateStr;
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.show();
    }
//        Calendar calendar = Calendar.getInstance();
//        DatePickerDialog dialog = new DatePickerDialog(this,
//                (view, year, month, dayOfMonth) -> {
//                    String dateStr = dayOfMonth + "/" + (month + 1) + "/" + year;
//                    etAssignmentDeadline.setText(dateStr);
//                },
//                calendar.get(Calendar.YEAR),
//                calendar.get(Calendar.MONTH),
//                calendar.get(Calendar.DAY_OF_MONTH));
//
//        dialog.getDatePicker().setMinDate(System.currentTimeMillis());
//        dialog.show();


    private void uploadAssignment(boolean keepSameDate) {
        String title = etAssignmentTitle.getText().toString().trim();
        String desc = etAssignmentDescription.getText().toString().trim();
        String deadline = etAssignmentDeadline.getText().toString().trim();
        String link = etAssignmentLink.getText().toString().trim();
        String program = spProgram.getSelectedItem().toString();
        String year = spYear.getSelectedItem().toString();
        String semester = spSemester.getSelectedItem().toString();
        String division = spDivision.getSelectedItem().toString();

        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(desc) || TextUtils.isEmpty(deadline) || link.isEmpty()) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        String autoDelete = keepSameDate ? deadline : selectedAutoDeleteDate;
        if (autoDelete == null || autoDelete.isEmpty()) {
            Toast.makeText(this, "Select auto-delete date", Toast.LENGTH_SHORT).show();
            return;
        }

        String assignmentId = assignmentRef.push().getKey();
        long timestamp = System.currentTimeMillis();

        AssignmentModelFaculty model = new AssignmentModelFaculty(
                assignmentId, title, desc, deadline, link,
                facultyName, facultyID, currentDate, program, year, semester, division, timestamp, autoDelete
        );

        assignmentRef.child(assignmentId).setValue(model)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Assignment Uploaded Successfully", Toast.LENGTH_SHORT).show();
                    fetchAssignments();
                    showNoticePopup(title, desc, year, semester, program , division);
                    clearFields();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Upload Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void fetchAssignments() {
        assignmentList.clear();

        String uid = auth.getCurrentUser().getUid();

        facultyRef.orderByChild("uid").equalTo(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot facultySnapshot) {
                        if (!facultySnapshot.exists()) return;

                        assignmentRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot snap : snapshot.getChildren()) {
                                    AssignmentModelFaculty assignment = snap.getValue(AssignmentModelFaculty.class);
                                    if (assignment != null && assignment.getFacultyName().equals(facultyName)) {
                                        assignmentList.add(assignment);
                                    }
                                }
                                Collections.sort(assignmentList, (n1, n2) -> Long.compare(n2.getTimestamp(), n1.getTimestamp()));
                                adapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(assignment_faculty.this, "Failed to fetch assignments", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(assignment_faculty.this, "Failed to fetch assignments", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showNoticePopup(String title, String description, String year, String
            semester, String program , String division) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Send Notice?");
        builder.setMessage("Do you want to send a notice regarding this assignment?");

        builder.setPositiveButton("Yes", (dialog, which) -> {
            // Redirect to Faculty Notice Activity with pre-filled details
            Intent intent = new Intent(assignment_faculty.this, notice_faculty.class);
            intent.putExtra("noticeTitle", "New Assignment: " + title);
            intent.putExtra("noticeContent", "Assignment Description: " + description);
            intent.putExtra("year", year);
            intent.putExtra("semester", semester);
            intent.putExtra("program", program);
            intent.putExtra("division", division);
            startActivity(intent);
            finish();
        });

        builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void clearFields() {
        etAssignmentTitle.setText("");
        etAssignmentDescription.setText("");
        etAssignmentDeadline.setText("");
        etAssignmentLink.setText("");
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(assignment_faculty.this, home_faculty.class);
        startActivity(intent);
        finish();
    }
}
