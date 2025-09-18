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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class FacultyEditNotice extends AppCompatActivity {

    EditText etTitle, etContent , etAutoDelete;
    Spinner spProgram, spYear, spSemester, spDivision;
    Button btnUpdate;

    String noticeId, originalTitle, originalContent, facultyName, date , autoDeleteion;
    String selectedProgram, selectedYear, selectedSemester, selectedDivision;

    Calendar calendar;
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_faculty_edit_notice);

        etTitle = findViewById(R.id.etNoticeTitle);
        etContent = findViewById(R.id.etNoticeContent);
        etAutoDelete = findViewById(R.id.etNoticeDeletionDate);
        btnUpdate = findViewById(R.id.btnUpdateNotice);
        spProgram = findViewById(R.id.spinnerProgram);
        spYear = findViewById(R.id.spYear);
        spSemester = findViewById(R.id.spSemester);
        spDivision = findViewById(R.id.spinnerDivision);

        noticeId = getIntent().getStringExtra("noticeId");
        originalTitle = getIntent().getStringExtra("title");
        originalContent = getIntent().getStringExtra("content");
        facultyName = getIntent().getStringExtra("facultyName");
        date = getIntent().getStringExtra("date");
        selectedProgram = getIntent().getStringExtra("program");
        selectedSemester = getIntent().getStringExtra("semester");
        selectedDivision = getIntent().getStringExtra("division");
        selectedYear = getIntent().getStringExtra("year");
        autoDeleteion = getIntent().getStringExtra("autoDeleteDate");

        etTitle.setText(originalTitle);
        etContent.setText(originalContent);
        etAutoDelete.setText(autoDeleteion);

        calendar = Calendar.getInstance();

        setupSpinners();

        etAutoDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setupDatePicker();
            }
        });

//        ArrayAdapter<CharSequence> programAdapter = ArrayAdapter.createFromResource(this, R.array.program_array, android.R.layout.simple_spinner_item);
//        programAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spProgram.setAdapter(programAdapter);
//
//        ArrayAdapter<CharSequence> yearAdapter = ArrayAdapter.createFromResource(this, R.array.year_semester_array, android.R.layout.simple_spinner_item);
//        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spYear.setAdapter(yearAdapter);
//
//        spYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                ArrayAdapter<CharSequence> semesterAdapter;
//
//                switch (position) {
//                    case 0: // First Year
//                        semesterAdapter = ArrayAdapter.createFromResource(
//                                FacultyEditNotice.this, R.array.first_year_semesters, android.R.layout.simple_spinner_item);
//                        break;
//                    case 1: // Second Year
//                        semesterAdapter = ArrayAdapter.createFromResource(
//                                FacultyEditNotice.this, R.array.second_year_semesters, android.R.layout.simple_spinner_item);
//                        break;
//                    case 2: // Third Year
//                        semesterAdapter = ArrayAdapter.createFromResource(
//                                FacultyEditNotice.this, R.array.third_year_semesters, android.R.layout.simple_spinner_item);
//                        break;
//                    default:
//                        semesterAdapter = null;
//                }
//
//                if (semesterAdapter != null) {
//                    semesterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                    spSemester.setAdapter(semesterAdapter);
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//
//            }
//        });
//
//        ArrayAdapter<CharSequence> divAdapter = ArrayAdapter.createFromResource(this , R.array.division_array , android.R.layout.simple_spinner_item);
//        divAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spDivision.setAdapter(divAdapter);

        btnUpdate.setOnClickListener(v -> updateNotice());

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

    }

    private void setupDatePicker() {

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(FacultyEditNotice.this,
                (view, year1, month1, dayOfMonth) -> {
                    calendar.set(year1, month1, dayOfMonth);
                    etAutoDelete.setText(sdf.format(calendar.getTime()));
                }, year, month, day);
        dialog.getDatePicker().setMinDate(System.currentTimeMillis());
        dialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {

            Toast.makeText(this, "Notice Details Not Edited !", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(FacultyEditNotice.this, notice_faculty.class);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
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
                        semesterAdapter = ArrayAdapter.createFromResource(FacultyEditNotice.this, R.array.first_year_semesters, android.R.layout.simple_spinner_item);
                        break;
                    case 1:
                        semesterAdapter = ArrayAdapter.createFromResource(FacultyEditNotice.this, R.array.second_year_semesters, android.R.layout.simple_spinner_item);
                        break;
                    case 2:
                        semesterAdapter = ArrayAdapter.createFromResource(FacultyEditNotice.this, R.array.third_year_semesters, android.R.layout.simple_spinner_item);
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

    private void updateNotice() {
        String newTitle = etTitle.getText().toString().trim();
        String newContent = etContent.getText().toString().trim();
        String newAutoDeletionDate = etAutoDelete.getText().toString().trim();
        String updatedProgram = spProgram.getSelectedItem().toString();
        String updatedYear = spYear.getSelectedItem().toString();
        String updatedSemester = spSemester.getSelectedItem().toString();
        String updatedDivision = spDivision.getSelectedItem().toString();

        if (newTitle.isEmpty() || newContent.isEmpty() || newAutoDeletionDate.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> updatedNotice = new HashMap<>();
        updatedNotice.put("title", newTitle);
        updatedNotice.put("content", newContent);
        updatedNotice.put("autoDeleteDate" , newAutoDeletionDate);
        updatedNotice.put("program", updatedProgram);
        updatedNotice.put("semester", updatedSemester);
        updatedNotice.put("year", updatedYear);
        updatedNotice.put("division", updatedDivision);
        updatedNotice.put("facultyName", facultyName);
        updatedNotice.put("date", date);
        updatedNotice.put("timestamp", System.currentTimeMillis());

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Notices").child(noticeId);
        ref.updateChildren(updatedNotice)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Notice updated successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(this, notice_faculty.class);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}