package com.example.cms;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class profile_student extends AppCompatActivity {

    private TextView tvStudentName, tvStudentRollNo, tvStudentContact, tvStudentEmail, tvStudentDOB, tvStudentAddress, tvStudentProgram, tvStudentYear, tvStudentSemester;
    private FirebaseAuth auth;
    private DatabaseReference studentRef;
    private Button btn_logout , btn_update;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile_student);

        tvStudentName = findViewById(R.id.tvStudentName);
        tvStudentRollNo = findViewById(R.id.tvStudentID);
        tvStudentContact = findViewById(R.id.tvStudentContact);
        tvStudentEmail = findViewById(R.id.tvStudentEmail);
        tvStudentDOB = findViewById(R.id.tvStudentDOB);
        tvStudentAddress = findViewById(R.id.tvStudentAddress);
        tvStudentProgram = findViewById(R.id.tvStudentProgram);
        tvStudentYear = findViewById(R.id.tvStudentYear);
        tvStudentSemester = findViewById(R.id.tvStudentSemester);
        btn_update = findViewById(R.id.btnupdate);
        btn_logout = findViewById(R.id.btnLogout);

        auth = FirebaseAuth.getInstance();
        studentRef = FirebaseDatabase.getInstance().getReference("Students");

        fetchStudentDetails();

        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(profile_student.this , profile_update_student.class);
                startActivity(intent);
                finish();
            }
        });

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLogoutConfirmationDialog();
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
            Intent intent = new Intent(profile_student.this, home_student.class);
            intent.putExtra("openDrawer", true); // Send an extra flag to open the drawer
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void showLogoutConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Logout");
        builder.setMessage("Are you sure you want to log out?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Redirect to Login Screen
                Toast.makeText(profile_student.this, "You Have Been Logged Out", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), login_student.class);
                startActivity(intent);
                finish();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss(); // Close the dialog
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

//    private void fetchStudentData() {
//
//        DatabaseReference studentsRef = FirebaseDatabase.getInstance().getReference("Students");
//
//        studentsRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                boolean found = false;
//
//                for (DataSnapshot programSnapshot : dataSnapshot.getChildren()) { // Loop through programs
//                    for (DataSnapshot studentSnapshot : programSnapshot.getChildren()) { // Loop through students
//
//                        StudentProfileDetailsModel student = studentSnapshot.getValue(StudentProfileDetailsModel.class);
//
//                        if (student != null && student.getContactno() != null && student.getContactno().equals(getLoggedInStudentContact())) {
//                            // Set data in UI
//                            tvStudentName.setText(student.getName());
//                            tvStudentRollNo.setText(student.getRollno());
//                            tvStudentContact.setText(student.getContactno());
//                            tvStudentEmail.setText(student.getEmail());
//                            tvStudentDOB.setText(student.getDob());
//                            tvStudentAddress.setText(student.getAddress());
//                            tvStudentProgram.setText(student.getProgram());
//                            tvStudentYear.setText(student.getYear());
//                            tvStudentSemester.setText(student.getSemester());
//
//                            found = true;
//                            return;
//                        }
//                    }
//                }
//
//                if (!found) {
//                    Toast.makeText(profile_student.this, "Student details not found!", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                Toast.makeText(profile_student.this, "Database Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
//
//
////        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
////            @Override
////            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
////                boolean found = false;
////
////                for (DataSnapshot programSnapshot : dataSnapshot.getChildren()) {  // Loop through programs
////                    for (DataSnapshot studentSnapshot : programSnapshot.getChildren()) { // Loop through students
//////                        String studentContact = studentSnapshot.child("contact").getValue(String.class);
////
////                        StudentProfileDetailsModel student = studentSnapshot.getValue(StudentProfileDetailsModel.class);
////
////
////                        if (student != null && student.getContactno().equals(login_student.loggedInContact)) {
//////
//////                            String studentName = studentSnapshot.child("name").getValue(String.class);
//////                            String studentRollNo = studentSnapshot.child("rollNumber").getValue(String.class);
//////                            String studentContactP = studentSnapshot.child("contact").getValue(String.class);
//////                            String studentEmail = studentSnapshot.child("email").getValue(String.class);
//////                            String studentDOB = studentSnapshot.child("dob").getValue(String.class);
//////                            String studentAddress = studentSnapshot.child("address").getValue(String.class);
//////                            String studentProgram = studentSnapshot.child("program").getValue(String.class);
//////                            String studentYear = studentSnapshot.child("year").getValue(String.class);
//////                            String studentSemester = studentSnapshot.child("semester").getValue(String.class);
////
////
////                            tvStudentName.setText(student.getName());
////                            tvStudentRollNo.setText(student.getRollno());
////                            tvStudentContact.setText(student.getContactno());
////                            tvStudentEmail.setText(student.getEmail());
////                            tvStudentDOB.setText(student.getDob());
////                            tvStudentAddress.setText(student.getAddress());
////                            tvStudentProgram.setText(student.getProgram());
////                            tvStudentYear.setText(student.getYear());
////                            tvStudentSemester.setText(student.getSemester());
////                            found = true;
////                            return;
////                        }
////                    }
////                }if (!found) {
////                    Toast.makeText(profile_student.this, "Student details not found!", Toast.LENGTH_SHORT).show();
////                }
////
////            }
////
////            @Override
////            public void onCancelled(@NonNull DatabaseError databaseError) {
////                Toast.makeText(profile_student.this, "Database Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
////            }
////        });
//    }


    private void fetchStudentDetails() {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(profile_student.this, login_student.class));
            finish();
            return;
        }

        String userId = user.getUid();

        studentRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String studentName = snapshot.child("name").getValue(String.class);
                    String studentRollNo = snapshot.child("rollNumber").getValue(String.class);
                    String studentContact = snapshot.child("contact").getValue(String.class);
                    String studentEmail = snapshot.child("email").getValue(String.class);
                    String studentDOB = snapshot.child("dob").getValue(String.class);
                    String studentAddress = snapshot.child("address").getValue(String.class);
                    String studentProgram = snapshot.child("program").getValue(String.class);
                    String studentYear = snapshot.child("year").getValue(String.class);
                    String studentSemester = snapshot.child("semester").getValue(String.class);

                    tvStudentName.setText(studentName);
                    tvStudentRollNo.setText(studentRollNo);
                    tvStudentContact.setText(studentContact);
                    tvStudentEmail.setText(studentEmail);
                    tvStudentDOB.setText(studentDOB);
                    tvStudentAddress.setText(studentAddress);
                    tvStudentProgram.setText(studentProgram);
                    tvStudentYear.setText(studentYear);
                    tvStudentSemester.setText(studentSemester);
                } else {
                    Toast.makeText(profile_student.this, "Student details not found!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(profile_student.this, "Database Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}



//        FirebaseUser currentUser = mAuth.getCurrentUser();
//
//        if (currentUser != null) {
//            String contactNumber = currentUser.getPhoneNumber();
//            if (contactNumber != null) {
//                studentRef = FirebaseDatabase.getInstance().getReference("Students");
//
//                Query query = studentRef.orderByChild("contact").equalTo(contactNumber);
//                query.addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        if (snapshot.exists()) {
//                            for (DataSnapshot studentSnapshot : snapshot.getChildren()) {
//                                String studentName = studentSnapshot.child("name").getValue(String.class);
//                                String studentRollNo = studentSnapshot.child("rollNumber").getValue(String.class);
//                                String studentContact = studentSnapshot.child("contact").getValue(String.class);
//                                String studentEmail = studentSnapshot.child("email").getValue(String.class);
//                                String studentDOB = studentSnapshot.child("dob").getValue(String.class);
//                                String studentAddress = studentSnapshot.child("address").getValue(String.class);
//                                String studentProgram = studentSnapshot.child("program").getValue(String.class);
//                                String studentYear = studentSnapshot.child("year").getValue(String.class);
//                                String studentSemester = studentSnapshot.child("semester").getValue(String.class);
//
//                                // Set values to TextViews
//                                tvStudentName.setText(studentName);
//                                tvStudentRollNo.setText(studentRollNo);
//                                tvStudentContact.setText(studentContact);
//                                tvStudentEmail.setText(studentEmail);
//                                tvStudentDOB.setText(studentDOB);
//                                tvStudentAddress.setText(studentAddress);
//                                tvStudentProgram.setText(studentProgram);
//                                tvStudentYear.setText(studentYear);
//                                tvStudentSemster.setText(studentSemester);
//
//                            }
//                        } else {
//                            Toast.makeText(profile_student.this, "Student details not found!", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//                        Toast.makeText(profile_student.this, "Database Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
//                        Log.e("FirebaseError", error.getMessage());
//                    }
//                });
//            }else {
//                Toast.makeText(this, "Error: Contact number not found!", Toast.LENGTH_SHORT).show();
//            }
//        }else {
//            Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show();
//        }

//        SharedPreferences sharedPreferences = getSharedPreferences("StudentLoginPrefs", MODE_PRIVATE);
//        String contactNumber = sharedPreferences.getString("contact", null);
//
//        if (contactNumber == null) {
//            Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show();
//            startActivity(new Intent(profile_student.this, login_student.class));
//            finish();
//            return;
//        }
//
//        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Students");
//
//        databaseReference.orderByChild("contact").equalTo(contactNumber)
//                .addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        if (dataSnapshot.exists()) {
//                            for (DataSnapshot studentSnapshot : dataSnapshot.getChildren()) {
//                                String studentName = studentSnapshot.child("name").getValue(String.class);
//                                String studentRollNo = studentSnapshot.child("rollNumber").getValue(String.class);
//                                String studentContact = studentSnapshot.child("contact").getValue(String.class);
//                                String studentEmail = studentSnapshot.child("email").getValue(String.class);
//                                String studentDOB = studentSnapshot.child("dob").getValue(String.class);
//                                String studentAddress = studentSnapshot.child("address").getValue(String.class);
//                                String studentProgram = studentSnapshot.child("program").getValue(String.class);
//                                String studentYear = studentSnapshot.child("year").getValue(String.class);
//                                String studentSemester = studentSnapshot.child("semester").getValue(String.class);
//
//                                tvStudentName.setText(studentName);
//                                tvStudentRollNo.setText(studentRollNo);
//                                tvStudentContact.setText(studentContact);
//                                tvStudentEmail.setText(studentEmail);
//                                tvStudentDOB.setText(studentDOB);
//                                tvStudentAddress.setText(studentAddress);
//                                tvStudentProgram.setText(studentProgram);
//                                tvStudentYear.setText(studentYear);
//                                tvStudentSemster.setText(studentSemester);
//
//                                // Save Student ID, Name, Program, and Semester in SharedPreferences
//                                SharedPreferences.Editor editor = sharedPreferences.edit();
//                                editor.putString("studentName", studentName);
//                                editor.putString("studentProgram", studentProgram);
//                                editor.putString("studentSemester", studentSemester);
//                                editor.putString("studentRollNo", studentRollNo);
//                                editor.putString("studentContact", studentContact);
//                                editor.putString("studentEmail", studentEmail);
//                                editor.putString("studentDOB", studentDOB);
//                                editor.putString("studentAddress", studentAddress);
//                                editor.putString("studentYear", studentYear);
//                                editor.apply();
//                            }
//                        } else {
//                            Toast.makeText(profile_student.this, "Student details not found!", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//                        Toast.makeText(profile_student.this, "Database Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                });


