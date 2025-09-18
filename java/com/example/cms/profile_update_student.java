package com.example.cms;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class profile_update_student extends AppCompatActivity {

    private EditText etStudentName, etStudentRollNo, etStudentContact, etStudentGender, etStudentDOB, etStudentAddress;
    TextView tvStudentProgram, tvStudentYear, tvStudentSemester, tvStudentDivision, tvStudentEmail;
    Button btnUpdateProfile;
    private String currentName, currentContact, currentAddress, currentRoll, currentDob, currentGender;
    private ProgressDialog progressDialog;
    private String currentDivision, storedPassword;

    private FirebaseAuth auth;
    FirebaseUser currentUser;
    private DatabaseReference studentRef, databaseRef;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile_update_student);

        etStudentName = findViewById(R.id.etUPStudentName);
        etStudentRollNo = findViewById(R.id.etUPStudentroll);
        etStudentContact = findViewById(R.id.etUPStudentContact);
        etStudentDOB = findViewById(R.id.etUPStudentDOB);
        etStudentAddress = findViewById(R.id.etUPStudentAddress);
        etStudentGender = findViewById(R.id.etUPStudentGender);
        tvStudentProgram = findViewById(R.id.tvStudentProgramUp);
        tvStudentYear = findViewById(R.id.tvStudentYearUp);
        tvStudentSemester = findViewById(R.id.tvStudentSemesterUp);
        tvStudentDivision = findViewById(R.id.tvStudentDivisionUp);
        tvStudentEmail = findViewById(R.id.tvStudentEmailUp);
        btnUpdateProfile = findViewById(R.id.btnUpdateProfile);

        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        databaseRef = FirebaseDatabase.getInstance().getReference("Students");
        studentRef = databaseRef.child(currentUser.getUid());

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Updating Details...");


        fetchStudentDetails();

        btnUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateAndProceed();
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
            Intent intent = new Intent(profile_update_student.this, home_student.class);
            intent.putExtra("openDrawer", true); // Send an extra flag to open the drawer
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void fetchStudentDetails() {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(profile_update_student.this, login_student.class));
            finish();
            return;
        }

        studentRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String studentEmail = snapshot.child("email").getValue(String.class);
                    String studentProgram = snapshot.child("program").getValue(String.class);
                    String studentYear = snapshot.child("year").getValue(String.class);
                    String studentSemester = snapshot.child("semester").getValue(String.class);
                    String studentDivision = snapshot.child("division").getValue(String.class);

                    currentDivision = snapshot.child("division").getValue(String.class);
                    storedPassword = snapshot.child("password").getValue(String.class);

                    tvStudentEmail.setText(studentEmail);
                    tvStudentProgram.setText(studentProgram);
                    tvStudentYear.setText(studentYear);
                    tvStudentSemester.setText(studentSemester);
                    tvStudentDivision.setText(studentDivision);

                    currentName = snapshot.child("name").getValue(String.class);
                    currentGender = snapshot.child("gender").getValue(String.class);
                    currentContact = snapshot.child("contact").getValue(String.class);
                    currentAddress = snapshot.child("address").getValue(String.class);
                    currentDob = snapshot.child("dob").getValue(String.class);
                    currentRoll = snapshot.child("rollNumber").getValue(String.class);

                    etStudentName.setText(currentName);
                    etStudentGender.setText(currentGender);
                    etStudentContact.setText(currentContact);
                    etStudentAddress.setText(currentAddress);
                    etStudentDOB.setText(currentDob);
                    etStudentRollNo.setText(currentRoll);

                } else {
                    Toast.makeText(profile_update_student.this, "Student details not found!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(profile_update_student.this, "Database Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void validateAndProceed() {
        String name = etStudentName.getText().toString().trim();
        String gender = etStudentGender.getText().toString().trim();
        String newContact = etStudentContact.getText().toString().trim();
        String address = etStudentAddress.getText().toString().trim();
        String newRollNo = etStudentRollNo.getText().toString().trim();
        String dob = etStudentDOB.getText().toString().trim();

        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate fields
        if (name.isEmpty() || gender.isEmpty() || newContact.isEmpty() || address.isEmpty() || dob.isEmpty() || newRollNo.isEmpty()) {
            Toast.makeText(this, "All fields are required!", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.show();

        databaseRef.orderByChild("division").equalTo(currentDivision).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean rollExists = false;
                for (DataSnapshot student : snapshot.getChildren()) {
                    String existingRoll = student.child("rollNumber").getValue(String.class);
                    if (existingRoll != null && existingRoll.equals(newRollNo) && !student.getKey().equals(currentUser.getUid())) {
                        rollExists = true;
                        break;
                    }
                }

                if (rollExists) {
                    progressDialog.dismiss();
                    Toast.makeText(profile_update_student.this, "Roll Number already exists in your division!", Toast.LENGTH_SHORT).show();
                } else {
                    // Check if the contact number exists anywhere in the database
                    checkContactNumber(newContact, name, newRollNo, address, dob , gender);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
                Toast.makeText(profile_update_student.this, "Error checking roll number!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkContactNumber(String newContact, String name, String newRollNo, String address, String dob , String gender) {
        databaseRef.orderByChild("contact").equalTo(newContact).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean contactExists = false;
                for (DataSnapshot student : snapshot.getChildren()) {
                    if (!student.getKey().equals(currentUser.getUid())) {
                        contactExists = true;
                        break;
                    }
                }

                if (contactExists) {
                    progressDialog.dismiss();
                    Toast.makeText(profile_update_student.this, "Contact Number already exists!", Toast.LENGTH_SHORT).show();
                } else {
                    progressDialog.dismiss();
                    showPasswordDialog(name, newRollNo, newContact, address, dob , gender);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
                Toast.makeText(profile_update_student.this, "Error checking contact number!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showPasswordDialog(String name, String newRollNo, String newContact, String address, String dob , String gender) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Password To Update Details");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        input.setHint("Enter Your Current Password");
        builder.setView(input);

        builder.setPositiveButton("Confirm", (dialog, which) -> {
            String enteredPassword = input.getText().toString().trim();
            if (enteredPassword.equals(storedPassword)) {
                updateStudentProfile(name, newRollNo, newContact, address, dob , gender);
            } else {
                Toast.makeText(profile_update_student.this, "Incorrect Password!", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void updateStudentProfile(String name, String newRollNo, String newContact, String address, String dob , String gender) {
        progressDialog.show();

        Map<String, Object> studentUpdates = new HashMap<>();
        studentUpdates.put("name", name);
        studentUpdates.put("rollNumber", newRollNo);
        studentUpdates.put("contact", newContact);
        studentUpdates.put("address", address);
        studentUpdates.put("dob", dob);
        studentUpdates.put("gender" , gender);

        studentRef.updateChildren(studentUpdates).addOnCompleteListener(task -> {
            progressDialog.dismiss();
            if (task.isSuccessful()) {
                Toast.makeText(profile_update_student.this, "Profile updated successfully", Toast.LENGTH_LONG).show();
                startActivity(new Intent(this, profile_student.class));
                finish();
            } else {
                Toast.makeText(profile_update_student.this, "Profile update failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
//    private void updateEmail() {
//        String name = etStudentName.getText().toString().trim();
//        String email = etStudentEmail.getText().toString().trim();
//        String password = etStudentPassword.getText().toString().trim();
//        String contact = etStudentContact.getText().toString().trim();
//        String address = etStudentAddress.getText().toString().trim();
//        String rollno = etStudentRollNo.getText().toString().trim();
//        String dob = etStudentDOB.getText().toString().trim();
//
//        FirebaseUser user = auth.getCurrentUser();
//        String userId = user.getUid();
//
//        if (name.isEmpty() || contact.isEmpty() || email.isEmpty() || password.isEmpty() || address.isEmpty() || dob.isEmpty() || rollno.isEmpty()) {
//            Toast.makeText(this, "All fields are required!", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        if (user == null) {
//            Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        progressDialog.show();
//
//        AuthCredential credential = EmailAuthProvider.getCredential(currentEmail, currentPassword);
//        user.reauthenticate(credential).addOnCompleteListener(task -> {
//            if (task.isSuccessful()) {
//
//                user.updateEmail(email).addOnCompleteListener(emailTask -> {
//
//                    if (emailTask.isSuccessful()) {
//
//                        user.updatePassword(password).addOnCompleteListener(passTask -> {
//                            if (passTask.isSuccessful()) {
//
//                                Map<String, Object> studentUpdates = new HashMap<>();
//                                studentUpdates.put("name", name);
//                                studentUpdates.put("rollNumber", rollno);
//                                studentUpdates.put("contact", contact);
//                                studentUpdates.put("dob", dob);
//                                studentUpdates.put("address", address);
//                                studentUpdates.put("email", email); // Update email in database
//
//                                studentUpRef.child(userId).updateChildren(studentUpdates).addOnCompleteListener(dbTask -> {
//                                    progressDialog.dismiss();
//                                    if (dbTask.isSuccessful()) {
//                                        Toast.makeText(this, "Profile updated successfully.", Toast.LENGTH_SHORT).show();
//                                        startActivity(new Intent(this, profile_student.class));
//                                        finish();
//                                    } else {
//                                        Toast.makeText(this, "Database update failed.", Toast.LENGTH_SHORT).show();
//                                    }
//                                });
//
////                                HashMap<String, Object> map = new HashMap<>();
////                                map.put("email", email);
////                                map.put("name", name);
////                                map.put("rollNumber", rollno);
////                                map.put("contact", contact);
////                                map.put("dob", dob);
////                                map.put("address", address);
////                                map.put("password", password);
////
////                                studentUpRef.child(user.getUid()).updateChildren(map).addOnCompleteListener(dbTask -> {
////                                    progressDialog.dismiss();
////                                    if (dbTask.isSuccessful()) {
////                                        Toast.makeText(profile_update_student.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
////                                    } else {
////                                        Toast.makeText(profile_update_student.this, "Database update failed", Toast.LENGTH_SHORT).show();
////                                    }
////                                });
//                            } else {
//                                progressDialog.dismiss();
//                                Toast.makeText(profile_update_student.this, "Failed to update password", Toast.LENGTH_SHORT).show();
//                            }
//                        });
//                    } else {
//                        progressDialog.dismiss();
//                        Toast.makeText(profile_update_student.this, "Failed to update email", Toast.LENGTH_SHORT).show();
//                    }
//                });
//            } else {
//                progressDialog.dismiss();
//                Toast.makeText(profile_update_student.this, "Re-authentication failed", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
