package com.example.cms;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class register_student extends AppCompatActivity {

    Button btn_stud_return_login, btnRegister;
    EditText etstname, etaddress, etcontactno, etemail, etrollno, etDob, et_stpass, et_strepass;
    Spinner spgender, etprostud, spYear, spSemester , spDivision;
    DatabaseReference studentRef;
    FirebaseAuth auth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register_student);

        btn_stud_return_login = findViewById(R.id.btn_return_login);
        btnRegister = findViewById(R.id.btn_register_stud);
        etDob = findViewById(R.id.et_st_dob);
        spYear = findViewById(R.id.sp_st_year);
        spSemester = findViewById(R.id.sp_st_year_semester);
        spDivision = findViewById(R.id.sp_st_divison);
        et_stpass = findViewById(R.id.et_st_password);
        et_strepass = findViewById(R.id.et_st_confirm_password);
        spgender = findViewById(R.id.sp_st_gender);
        etstname = findViewById(R.id.et_st_name);
        etaddress = findViewById(R.id.et_st_address);
        etcontactno = findViewById(R.id.et_st_contact);
        etemail = findViewById(R.id.et_st_email);
        etprostud = findViewById(R.id.sp_st_program);
        etrollno = findViewById(R.id.et_st_roll_number);

        studentRef = FirebaseDatabase.getInstance().getReference("Students");
        auth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Registering Student...");

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
                                register_student.this, R.array.first_year_semesters, android.R.layout.simple_spinner_item);
                        break;
                    case 1: // Second Year
                        semesterAdapter = ArrayAdapter.createFromResource(
                                register_student.this, R.array.second_year_semesters, android.R.layout.simple_spinner_item);
                        break;
                    case 2: // Third Year
                        semesterAdapter = ArrayAdapter.createFromResource(
                                register_student.this, R.array.third_year_semesters, android.R.layout.simple_spinner_item);
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

        ArrayAdapter<CharSequence> divAdapter = ArrayAdapter.createFromResource(this , R.array.division_array , android.R.layout.simple_spinner_item);
        divAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDivision.setAdapter(divAdapter);

        // Date Picker for DOB
        etDob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkValidations();
            }
        });

        btn_stud_return_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(register_student.this, login_student.class);
                startActivity(intent);
                finish();
            }
        });

    }

    private void showDatePicker() {

        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            etDob.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        Calendar minDate = Calendar.getInstance();
        minDate.set(2000, Calendar.JANUARY, 1); // January is 0-based
        datePickerDialog.getDatePicker().setMinDate(minDate.getTimeInMillis());

        datePickerDialog.show();
    }

    private String getProgramShortForm(String program) {
        switch (program) {
            case "Bachelor of Computer Application":
                return "BCA";
            case "Bachelor of Business Administration":
                return "BBA";
            case "Bachelor of Commerce":
                return "BCOM";
            default:
                return "UNK"; // Unknown Program
        }
    }

    private String getSemesterShortForm(String semester) {
        return semester.replace("Semester ", "SEM"); // Convert "Semester 1" -> "SEM1"
    }

    private String getDivisionShortForm(String division){
        return division.replace("Division " , "DIV");
    }

    private void checkValidations() {

        String name = etstname.getText().toString().trim();
        String dob = etDob.getText().toString().trim();
        String gender = spgender.getSelectedItem().toString();
        String address = etaddress.getText().toString().trim();
        String contact = etcontactno.getText().toString().trim();
        String email = etemail.getText().toString().trim();
        String program = etprostud.getSelectedItem().toString();
        String year = spYear.getSelectedItem().toString();
        String semester = spSemester.getSelectedItem().toString();
        String division = spDivision.getSelectedItem().toString();
        String rollNumber = etrollno.getText().toString().trim();
        String password = et_stpass.getText().toString().trim();
        String confirmPassword = et_strepass.getText().toString().trim();


        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(dob) || TextUtils.isEmpty(address) || TextUtils.isEmpty(gender) ||
                TextUtils.isEmpty(contact) || TextUtils.isEmpty(email) || TextUtils.isEmpty(rollNumber) || TextUtils.isEmpty(program) ||
                TextUtils.isEmpty(year) || TextUtils.isEmpty(semester) || TextUtils.isEmpty(division) ||
                TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(this, "All fields are required!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(register_student.this, "Password must be at least 6 characters long!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!Patterns.PHONE.matcher(contact).matches() || contact.length() != 10) {
            Toast.makeText(this, "Enter a valid 10-digit contact number!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Enter a valid email address!", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.show();

        studentRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean isDuplicate = false;

                for (DataSnapshot studentSnap : snapshot.getChildren()) {
                    String dbRoll = studentSnap.child("rollNumber").getValue(String.class);
                    String dbProgram = studentSnap.child("program").getValue(String.class);
                    String dbYear = studentSnap.child("year").getValue(String.class);
                    String dbSemester = studentSnap.child("semester").getValue(String.class);
                    String dbDivision = studentSnap.child("division").getValue(String.class);

                    if (rollNumber.equals(dbRoll) &&
                            program.equals(dbProgram) &&
                            year.equals(dbYear) &&
                            semester.equals(dbSemester) &&
                            division.equals(dbDivision)) {
                        isDuplicate = true;
                        break;
                    }
                }

                if (isDuplicate) {
                    progressDialog.dismiss();
                    Toast.makeText(register_student.this, "Roll number already registered in selected division!", Toast.LENGTH_SHORT).show();
                } else {
                    registerNewStudent(name, dob, gender, address, contact, email, program, year, semester, division, rollNumber, password);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
                Toast.makeText(register_student.this, "Error checking roll number!", Toast.LENGTH_SHORT).show();
            }
        });

//        ===========This is final registration -----=--=-=-
//        studentRef.orderByChild("contact").equalTo(contact)
//                .addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        if (snapshot.exists()) {
//                            progressDialog.dismiss();
//                            Toast.makeText(register_student.this, "Contact number already registered!", Toast.LENGTH_SHORT).show();
//                        } else {
//                            // Check if Roll Number already exists
//                            studentRef.orderByChild("rollNumber").equalTo(rollNumber)
//                                    .addListenerForSingleValueEvent(new ValueEventListener() {
//                                        @Override
//                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                            if (snapshot.exists()) {
//                                                progressDialog.dismiss();
//                                                Toast.makeText(register_student.this, "Roll number already registered!", Toast.LENGTH_SHORT).show();
//                                            } else {
//                                                // Register Student
//                                                registerNewStudent(name, dob, gender, address, contact, email, program, year, semester, division , rollNumber, password);
//                                            }
//                                        }
//
//                                        @Override
//                                        public void onCancelled(@NonNull DatabaseError error) {
//                                            progressDialog.dismiss();
//                                            Toast.makeText(register_student.this, "Error checking roll number!", Toast.LENGTH_SHORT).show();
//                                        }
//                                    });
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//                        progressDialog.dismiss();
//                        Toast.makeText(register_student.this, "Error checking contact number!", Toast.LENGTH_SHORT).show();
//                    }
//                });

    }


    private void registerNewStudent(String name, String dob, String gender, String address, String contact,
                                    String email, String program, String year, String semester, String division ,
                                    String rollNumber, String password) {

        String registrationDateTime = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                .format(new Date());

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();
                        if (user != null) {
                            String uid = user.getUid();
                            String studentID = getProgramShortForm(program) + "-" + getSemesterShortForm(semester) + "-" + getDivisionShortForm(division) + "-" + rollNumber;

                            HashMap<String, Object> studentData = new HashMap<>();
                            studentData.put("uid", uid);
                            studentData.put("studentId", studentID);
                            studentData.put("name", name);
                            studentData.put("dob", dob);
                            studentData.put("gender",gender);
                            studentData.put("address", address);
                            studentData.put("contact", contact);
                            studentData.put("email", email);
                            studentData.put("registrationDateTime", registrationDateTime);
                            studentData.put("program", program);
                            studentData.put("year", year);
                            studentData.put("semester", semester);
                            studentData.put("division" , division);
                            studentData.put("rollNumber", rollNumber);
                            studentData.put("password", password);

                            studentRef.child(uid).setValue(studentData)
                                    .addOnSuccessListener(unused -> {
                                        progressDialog.dismiss();
                                        Toast.makeText(register_student.this, "Registration Successful!", Toast.LENGTH_SHORT).show();
                                        clearFields();
                                        startActivity(new Intent(register_student.this, login_student.class));
                                        finish();
                                    })
                                    .addOnFailureListener(e -> {
                                        progressDialog.dismiss();
                                        Toast.makeText(register_student.this, "Registration Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        }
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(register_student.this, "Authentication Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(register_student.this, login_student.class);
        startActivity(intent);
        finish();
    }


    private void clearFields() {
        etstname.setText("");
        etDob.setText("");
        etaddress.setText("");
        etcontactno.setText("");
        etemail.setText("");
        etrollno.setText("");
        et_stpass.setText("");
        et_strepass.setText("");
    }
}


//private void saveStudentData(String name, String dob, String gender, String address, String contact, String email,
//                             String program, String year, String semester, String rollNumber) {
//    String studentId = databaseReference.push().getKey();
//
//    HashMap<String, String> studentData = new HashMap<>();
//    studentData.put("studentId", studentId);
//    studentData.put("name", name);
//    studentData.put("dob", dob);
//    studentData.put("gender", gender);
//    studentData.put("address", address);
//    studentData.put("contact", contact);
//    studentData.put("email", email);
//    studentData.put("program", program);
//    studentData.put("year", year);
//    studentData.put("semester", semester);
//    studentData.put("rollNumber", rollNumber);
//    studentData.put("password", password);
//
//    assert studentId != null;
//    databaseReference.child(studentId).setValue(studentData)
//            .addOnCompleteListener(new OnCompleteListener<Void>() {
//                @Override
//                public void onComplete(@NonNull Task<Void> task) {
//                    if (task.isSuccessful()) {
//                        Toast.makeText(register_student.this, "Student Registered Successfully!", Toast.LENGTH_SHORT).show();
//                        Intent intent = new Intent(register_student.this, login_student.class);
//                        startActivity(intent);
//                        finish();
//                        clearFields();
//                    } else {
//                        Toast.makeText(register_student.this, "Failed to save data!", Toast.LENGTH_SHORT).show();
//                    }
//                }
//            });
//}

//        etdob.setOnClickListener(v -> {
//            Calendar calendar = Calendar.getInstance();
//            DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
//                etdob.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
//            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
//
//            Calendar minDate = Calendar.getInstance();
//            minDate.set(2000, Calendar.JANUARY, 1); // January is 0-based
//            datePickerDialog.getDatePicker().setMinDate(minDate.getTimeInMillis());
//
//            datePickerDialog.show();
//        });

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
//                                register_student.this, R.array.first_year_semesters, android.R.layout.simple_spinner_item);
//                        break;
//                    case 1: // Second Year
//                        semesterAdapter = ArrayAdapter.createFromResource(
//                                register_student.this, R.array.second_year_semesters, android.R.layout.simple_spinner_item);
//                        break;
//                    case 2: // Third Year
//                        semesterAdapter = ArrayAdapter.createFromResource(
//                                register_student.this, R.array.third_year_semesters, android.R.layout.simple_spinner_item);
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

//        btn_stud_register.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                registerStudentdetails();
//            }
//        });


//    private void registerStudentdetails() {
//        String name = etstname.getText().toString().trim();
//        String dob = etdob.getText().toString().trim();
//        String gender = spgender.getSelectedItem().toString();
//        String address = etaddress.getText().toString().trim();
//        String contactno = etcontactno.getText().toString().trim();
//        String emailaddress = etemail.getText().toString().trim();
//        String programstud = etprostud.getSelectedItem().toString();
//        String year = spYear.getSelectedItem().toString();
//        String semester = spSemester.getSelectedItem().toString();
//        String rollno = etrollno.getText().toString().trim();
//        String password = et_stpass.getText().toString().trim();
//        String repassword = et_strepass.getText().toString().trim();
//
//        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(dob) || TextUtils.isEmpty(gender) || TextUtils.isEmpty(address) || TextUtils.isEmpty(contactno) || TextUtils.isEmpty(emailaddress) ||
//                TextUtils.isEmpty(programstud) || TextUtils.isEmpty(year) || TextUtils.isEmpty(semester) || TextUtils.isEmpty(rollno) || TextUtils.isEmpty(password) || TextUtils.isEmpty(repassword)) {
//            Toast.makeText(this, "Please Fill All The Feilds", Toast.LENGTH_SHORT).show();
//        } else if (!password.equals(repassword)) {
//            et_strepass.setError("Password Must Be Same!");
//            et_strepass.requestFocus();
//        } else {
//            registerStudent(name, dob, gender, address, contactno, emailaddress, programstud, year, semester, rollno, password);
//        }
//    }
//
//    private void registerStudent(String name, String dob, String gender, String address, String contactno, String emailaddress, String programstud, String year, String semester, String rollno, String password) {
//        String studid = auth.getUid();
//
//        if (studid != null) {
//            HashMap<String, String> studmap = new HashMap<>();
//            studmap.put("Student's Name", name);
//            studmap.put("Student's Date Of Birth", dob);
//            studmap.put("Student's Gender", gender);
//            studmap.put("Student's Address", address);
//            studmap.put("Student's ContactNo", contactno);
//            studmap.put("Student's Email Address", emailaddress);
//            studmap.put("Student's Program", programstud);
//            studmap.put("Student's Year", year);
//            studmap.put("Student's Semester", semester);
//            studmap.put("Student's Roll No", rollno);
//            studmap.put("Student's Password", password);
//
//            databaseReference.child(studid).setValue(studmap).addOnCompleteListener(task -> {
//                if (task.isSuccessful()) {
//                    Toast.makeText(this, "Registration Successfully", Toast.LENGTH_SHORT).show();
//                    finish();
//                } else {
//                    Toast.makeText(this, "Registration Failed", Toast.LENGTH_SHORT).show();
//                }
//            });
//        } else {
//            Toast.makeText(this, "Authentication Failed", Toast.LENGTH_SHORT).show();
//        }
//    }


//        btn_stud_register.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String name = etstname.getText().toString().trim();
//                String dob = etdob.getText().toString().trim();
//                String gender = spgender.getSelectedItem().toString();
//                String address = etaddress.getText().toString().trim();
//                String contactno = etcontactno.getText().toString().trim();
//                String emailaddress = etemail.getText().toString().trim();
//                String programstud = etprostud.getSelectedItem().toString();
//                String year = spYear.getSelectedItem().toString();
//                String semester = spSemester.getSelectedItem().toString();
//                String rollno = etrollno.getText().toString().trim();
//                String password = et_stpass.getText().toString().trim();
//                String repassword = et_strepass.getText().toString().trim();
//            }
//        });

//if (password.equals(confirmPassword)) {
//        auth.createUserWithEmailAndPassword(email, password)
//        .addOnCompleteListener(task -> {
//        if (task.isSuccessful()) {
//String userId = auth.getCurrentUser().getUid();
//Map<String, String> userData = new HashMap<>();
//                userData.put("Name", name);
//                userData.put("DOB", dob);
//                userData.put("Gender", gender);
//                userData.put("Address", address);
//                userData.put("Contact", contact);
//                userData.put("Email", email);
//                userData.put("Program", program);
//                userData.put("Year", year);
//                userData.put("Semester", semester);
//                userData.put("RollNumber", rollNumber);
//
//                dbRef.child(userId).setValue(userData)
//                    .addOnCompleteListener(dbTask -> {
//        if (dbTask.isSuccessful()) {
//        Toast.makeText(getApplicationContext(), "Registration Successful", Toast.LENGTH_SHORT).show();
//                        }
//                                });
//                                }
//                                });
//                                }


//        Calendar calendar = Calendar.getInstance();
//        int year = calendar.get(Calendar.YEAR);
//        int month = calendar.get(Calendar.MONTH);
//        int day = calendar.get(Calendar.DAY_OF_MONTH);
//
//        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
//            @Override
//            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
//                etDob.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
//            }
//        }, year, month, day);
//
//        // Set the minimum date to today's date (restrict past dates)
//        datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
//
//        datePickerDialog.show();