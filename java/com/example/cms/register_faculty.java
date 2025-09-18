package com.example.cms;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.HashMap;

public class register_faculty extends AppCompatActivity {

    Button btnReturnLogin, btnRegister;
    EditText etFacultyName, etAddress, etContactNo, etEmail, etFacultyID, etSpecialization, etDob, etPassword, etConfirmPassword;
    Spinner spGender, spDesignation;
    DatabaseReference databaseReference;
    FirebaseAuth auth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register_faculty);


        btnReturnLogin = findViewById(R.id.btn_return_login);
        btnRegister = findViewById(R.id.btn_register_fc);
        etFacultyName = findViewById(R.id.et_fc_name);
        etAddress = findViewById(R.id.et_fc_address);
        etContactNo = findViewById(R.id.et_fc_contact);
        etEmail = findViewById(R.id.et_fc_email);
        etFacultyID = findViewById(R.id.et_faculty_id);
        etSpecialization = findViewById(R.id.et_fc_specialization);
        etDob = findViewById(R.id.et_fc_dob);
        etPassword = findViewById(R.id.et_fc_password);
        etConfirmPassword = findViewById(R.id.et_fc_confirm_password);
        spGender = findViewById(R.id.sp_fc_gender);
        spDesignation = findViewById(R.id.sp_fc_designation);

        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Faculties");

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Registering Faculty...");

        ArrayAdapter<CharSequence> genderAdapter = ArrayAdapter.createFromResource(this, R.array.gender_array, android.R.layout.simple_spinner_item);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spGender.setAdapter(genderAdapter);

        ArrayAdapter<CharSequence> designationAdapter = ArrayAdapter.createFromResource(this, R.array.designation_array, android.R.layout.simple_spinner_item);
        designationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDesignation.setAdapter(designationAdapter);

        etDob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkvalidationFac();
            }
        });

        btnReturnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(register_faculty.this, login_faculty.class);
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
        minDate.set(1975, Calendar.JANUARY, 1); // January is 0-based
        datePickerDialog.getDatePicker().setMinDate(minDate.getTimeInMillis());

        datePickerDialog.show();
    }

    private void checkvalidationFac() {

        String name = etFacultyName.getText().toString().trim();
        String dob = etDob.getText().toString().trim();
        String gender = spGender.getSelectedItem().toString();
        String address = etAddress.getText().toString().trim();
        String contact = etContactNo.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String designation = spDesignation.getSelectedItem().toString();
        String specialization = etSpecialization.getText().toString().trim();
        String facultyID = etFacultyID.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();


        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(dob) || TextUtils.isEmpty(address) ||
                TextUtils.isEmpty(gender) || TextUtils.isEmpty(contact) || TextUtils.isEmpty(email) ||
                TextUtils.isEmpty(designation) || TextUtils.isEmpty(specialization) ||
                TextUtils.isEmpty(facultyID) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(this, "All fields are required!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(register_faculty.this, "Password must be at least 6 characters long!", Toast.LENGTH_SHORT).show();
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

        databaseReference.child(facultyID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Toast.makeText(register_faculty.this, "Faculty ID already exists!", Toast.LENGTH_SHORT).show();
                } else {
                    registerFacultyInAuth(name, dob, gender, address, contact, email, designation, specialization, facultyID, password);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(register_faculty.this, "Database Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
//        databaseReference.orderByChild("facultyId").equalTo(facultyID).addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener(){
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if (snapshot.exists()) {
//                    Toast.makeText(register_faculty.this, "Faculty ID already exists!", Toast.LENGTH_SHORT).show();
//                } else {
//                    // Register the faculty
//                    String uniqueId = databaseReference.push().getKey(); // Generate unique ID for each faculty
//                    Faculty faculty = new Faculty(uniqueId, name, dob , gender , address ,contact , email, designation , specialsub , facultyID , password);
//
//                    if (uniqueId != null) {
//                        databaseReference.child(uniqueId).setValue(faculty).addOnCompleteListener(task -> {
//                            if (task.isSuccessful()) {
//                                Toast.makeText(register_faculty.this, "Registration Successful", Toast.LENGTH_SHORT).show();
//                                Intent intent = new Intent(register_faculty.this , login_faculty.class);
//                                startActivity(intent);
//                                finish();
//                                // Optional: Clear input fields
//                                clearFields();
//                            } else {
//                                Toast.makeText(register_faculty.this, "Registration Failed", Toast.LENGTH_SHORT).show();
//                            }
//                        });
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Toast.makeText(register_faculty.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
//
//            }
//
//        });

//        databaseReference.child(facultyID).get().addOnCompleteListener(task -> {
//            if (task.isSuccessful() && task.getResult().exists()) {
//                Toast.makeText(register_faculty.this, "Faculty ID already exists!", Toast.LENGTH_SHORT).show();
//            }else {
//                HashMap<String, Object> facultyData = new HashMap<>();
//                facultyData.put("facultyId", facultyID);
//                facultyData.put("name", name);
//                facultyData.put("dob", dob);
//                facultyData.put("gender", gender);
//                facultyData.put("address", address);
//                facultyData.put("contact", contact);
//                facultyData.put("email", email);
//                facultyData.put("designation", designation);
//                facultyData.put("specialization", specialsub);
//                facultyData.put("password", password);
//
//                databaseReference.child(facultyID).setValue(facultyData)
//                        .addOnSuccessListener(aVoid -> {
//                            Toast.makeText(register_faculty.this, "Registration Successful!", Toast.LENGTH_SHORT).show();
//                            Intent intent = new Intent(register_faculty.this , login_faculty.class);
//                            startActivity(intent);
//                            finish();
//                        })
//                        .addOnFailureListener(e -> Toast.makeText(register_faculty.this, "Registration Failed!", Toast.LENGTH_SHORT).show());
//            }
//        });
    }

    private void registerFacultyInAuth(String name, String dob, String gender, String address, String contact, String email, String designation, String specialization, String facultyID, String password) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String uid = auth.getCurrentUser().getUid();
                HashMap<String, Object> facultyData = new HashMap<>();
                facultyData.put("uid", uid);
                facultyData.put("facultyID", facultyID);
                facultyData.put("name", name);
                facultyData.put("dob", dob);
                facultyData.put("gender", gender);
                facultyData.put("address", address);
                facultyData.put("contact", contact);
                facultyData.put("email", email);
                facultyData.put("designation", designation);
                facultyData.put("specialization", specialization);
                facultyData.put("password" , password);

                databaseReference.child(uid).setValue(facultyData)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(register_faculty.this, "Registration Successful!", Toast.LENGTH_SHORT).show();
                            clearFields();
                            startActivity(new Intent(register_faculty.this, login_faculty.class));
                            finish();
                        })
                        .addOnFailureListener(e -> Toast.makeText(register_faculty.this, "Registration Failed!", Toast.LENGTH_SHORT).show());
            } else {
                Toast.makeText(register_faculty.this, "Authentication Failed!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(register_faculty.this, login_faculty.class);
        startActivity(intent);
        finish();
    }

    private void clearFields() {
        etFacultyName.setText("");
        etDob.setText("");
        etAddress.setText("");
        etContactNo.setText("");
        etEmail.setText("");
        etFacultyID.setText("");
        etPassword.setText("");
        etConfirmPassword.setText("");
        etSpecialization.setText("");

    }
}