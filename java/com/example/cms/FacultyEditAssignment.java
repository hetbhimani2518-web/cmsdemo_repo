package com.example.cms;

import android.annotation.SuppressLint;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class FacultyEditAssignment extends AppCompatActivity {

    private Spinner spYear, spSemester, spProgram, spDivision;
    private EditText etAssTitle, etAssDescription, etAssDeadline, etAssLink, etAutoDeleteDate;
    TextView tvFacultyName, tvCurrentDate;
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    Button btnUpdate;
    Calendar calendar;
    String assignmentId, originalTitle, originalDesc, originalDeadline, originalDrivelink, facultyName, sentDate, autoDeleteion;
    String selectedProgram, selectedYear, selectedSemester, selectedDivision;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_faculty_edit_assignment);

        etAssTitle = findViewById(R.id.etAssignmentETitle);
        etAssDescription = findViewById(R.id.etAssignmentDescription);
        etAssDeadline = findViewById(R.id.etAssignmentEDeadline);
        etAssLink = findViewById(R.id.etAssignmentEDriveLink);
        etAutoDeleteDate = findViewById(R.id.etAssignmentDeletionDate);
        btnUpdate = findViewById(R.id.btnUpdateAssignment);
        spProgram = findViewById(R.id.spinnerProgram);
        spYear = findViewById(R.id.spYear);
        spSemester = findViewById(R.id.spSemester);
        spDivision = findViewById(R.id.spinnerDivision);
        tvFacultyName = findViewById(R.id.tvFcNameAss);
        tvCurrentDate = findViewById(R.id.tvAssignmentDate);

        assignmentId = getIntent().getStringExtra("assignId");
        originalTitle = getIntent().getStringExtra("title");
        originalDesc = getIntent().getStringExtra("description");
        originalDeadline = getIntent().getStringExtra("deadline");
        originalDrivelink = getIntent().getStringExtra("drivelink");
        sentDate = getIntent().getStringExtra("sentDate");
        facultyName = getIntent().getStringExtra("facultyName");
        autoDeleteion = getIntent().getStringExtra("autoDeleteDate");

        calendar = Calendar.getInstance();

        etAssTitle.setText(originalTitle);
        etAssLink.setText(originalDrivelink);
        etAssDescription.setText(originalDesc);
        etAssDeadline.setText(originalDeadline);
        etAutoDeleteDate.setText(autoDeleteion);

        tvFacultyName.setText(facultyName);
        tvCurrentDate.setText("Sent Date" + sentDate);

        selectedProgram = getIntent().getStringExtra("program");
        selectedSemester = getIntent().getStringExtra("semester");
        selectedDivision = getIntent().getStringExtra("division");
        selectedYear = getIntent().getStringExtra("year");

        setupSpinners();

        etAutoDeleteDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setupDatePicker();
            }
        });

        btnUpdate.setOnClickListener(v -> updateAssignemnt());

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Toast.makeText(this, "Notice Details Not Edited !", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(FacultyEditAssignment.this, assignment_faculty.class);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupDatePicker() {

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(FacultyEditAssignment.this,
                (view, year1, month1, dayOfMonth) -> {
                    calendar.set(year1, month1, dayOfMonth);
                    etAutoDeleteDate.setText(sdf.format(calendar.getTime()));
                }, year, month, day);
        dialog.getDatePicker().setMinDate(System.currentTimeMillis());
        dialog.show();
    }

    private void setupSpinners() {
        // Program Spinner
        ArrayAdapter<CharSequence> programAdapter = ArrayAdapter.createFromResource(
                this, R.array.program_array, android.R.layout.simple_spinner_item);
        programAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spProgram.setAdapter(programAdapter);
        setSpinnerSelection(spProgram, selectedProgram);

        // Year Spinner
        ArrayAdapter<CharSequence> yearAdapter = ArrayAdapter.createFromResource(
                this, R.array.year_semester_array, android.R.layout.simple_spinner_item);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spYear.setAdapter(yearAdapter);
        setSpinnerSelection(spYear, selectedYear);

        // Division Spinner
        ArrayAdapter<CharSequence> divisionAdapter = ArrayAdapter.createFromResource(
                this, R.array.division_array, android.R.layout.simple_spinner_item);
        divisionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDivision.setAdapter(divisionAdapter);
        setSpinnerSelection(spDivision, selectedDivision);

        // Semester Spinner based on year
        spYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ArrayAdapter<CharSequence> semesterAdapter = null;

                switch (position) {
                    case 0:
                        semesterAdapter = ArrayAdapter.createFromResource(FacultyEditAssignment.this, R.array.first_year_semesters, android.R.layout.simple_spinner_item);
                        break;
                    case 1:
                        semesterAdapter = ArrayAdapter.createFromResource(FacultyEditAssignment.this, R.array.second_year_semesters, android.R.layout.simple_spinner_item);
                        break;
                    case 2:
                        semesterAdapter = ArrayAdapter.createFromResource(FacultyEditAssignment.this, R.array.third_year_semesters, android.R.layout.simple_spinner_item);
                        break;
                }

                if (semesterAdapter != null) {
                    semesterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spSemester.setAdapter(semesterAdapter);
                    setSpinnerSelection(spSemester, selectedSemester); // Apply selection after adapter set
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void setSpinnerSelection(Spinner spinner, String value) {
        if (value != null) {
            ArrayAdapter adapter = (ArrayAdapter) spinner.getAdapter();
            for (int i = 0; i < adapter.getCount(); i++) {
                if (value.equals(adapter.getItem(i).toString())) {
                    spinner.setSelection(i);
                    break;
                }
            }
        }
    }

    private void updateAssignemnt() {
        String newTitle = etAssTitle.getText().toString().trim();
        String newDesc = etAssDescription.getText().toString().trim();
        String newDeadline = etAssDeadline.getText().toString().trim();
        String newDrivelink = etAssLink.getText().toString().trim();
        String newAutoDeleteDate = etAutoDeleteDate.getText().toString().trim();
        String updatedProgram = spProgram.getSelectedItem().toString();
        String updatedYear = spYear.getSelectedItem().toString();
        String updatedSemester = spSemester.getSelectedItem().toString();
        String updatedDivision = spDivision.getSelectedItem().toString();

        if (newTitle.isEmpty() || newDesc.isEmpty() || newDeadline.isEmpty() || newDrivelink.isEmpty() || newAutoDeleteDate.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> updatedAssignment = new HashMap<>();
        updatedAssignment.put("title", newTitle);
        updatedAssignment.put("description", newDesc);
        updatedAssignment.put("autoDeleteDate", newAutoDeleteDate);
        updatedAssignment.put("deadline", newDeadline);
        updatedAssignment.put("drivelink", newDrivelink);
        updatedAssignment.put("program", updatedProgram);
        updatedAssignment.put("semester", updatedSemester);
        updatedAssignment.put("year", updatedYear);
        updatedAssignment.put("division", updatedDivision);
        updatedAssignment.put("facultyName", facultyName);
        updatedAssignment.put("sentDate", sentDate);
        updatedAssignment.put("timestamp", System.currentTimeMillis());

        DatabaseReference assRef = FirebaseDatabase.getInstance().getReference("Assignments").child(assignmentId);
        assRef.updateChildren(updatedAssignment).addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Assignment updated successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(this, assignment_faculty.class);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}