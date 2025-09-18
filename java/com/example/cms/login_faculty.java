package com.example.cms;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class login_faculty extends AppCompatActivity {

    EditText etFacultyemail, etPassword;
    Button btnRegister, btnLogin;
    FirebaseAuth auth;
    DatabaseReference facultyRef;
    private ProgressDialog progressDialog;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_faculty);

        etFacultyemail = findViewById(R.id.etFacultyEmail);
        etPassword = findViewById(R.id.etFacultyPassword);
        btnRegister = findViewById(R.id.btn_reg_faculty);
        btnLogin = findViewById(R.id.btn_login_faculty);

        auth = FirebaseAuth.getInstance();
        facultyRef = FirebaseDatabase.getInstance().getReference("Faculties");

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Logging in...");

        btnLogin.setOnClickListener(v -> loginFaculty());
        btnRegister.setOnClickListener(v -> startActivity(new Intent(login_faculty.this, register_faculty.class)));
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(login_faculty.this, selection_user.class);
        startActivity(intent);
        finish();
    }

    private void loginFaculty() {
        String email = etFacultyemail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "All fields are required!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Enter a valid email address!", Toast.LENGTH_SHORT).show();
            return;
        }
        progressDialog.show();

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();
                        if (user != null) {
                            Toast.makeText(this, "Log In Successful", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(login_faculty.this, home_faculty.class);
                            startActivity(intent);
                            finish();
                        }
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(login_faculty.this, "Login Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

}

//    private void loginFaculty() {
//        String facultyId = facultyid.getText().toString().trim();
//        String password = fcpassword.getText().toString().trim();
//
////        if (TextUtils.isEmpty(facultyId) || TextUtils.isEmpty(password)) {
////            Toast.makeText(this, "All fields are required!", Toast.LENGTH_SHORT).show();
////            return;
////        }
//
////        databaseReference.child(facultyId).get().addOnCompleteListener(task -> {
////            if (task.isSuccessful() && task.getResult().exists()) {
////                DataSnapshot snapshot = task.getResult();
////                String storedPassword = snapshot.child("password").getValue(String.class);
////
////                if (storedPassword != null && storedPassword.equals(password)) {
////                    Toast.makeText(login_faculty.this, "Login Successful!", Toast.LENGTH_SHORT).show();
////                    startActivity(new Intent(login_faculty.this, home_faculty.class));
////                } else {
////                    Toast.makeText(login_faculty.this, "Invalid Password!", Toast.LENGTH_SHORT).show();
////                }
////            } else {
////                Toast.makeText(login_faculty.this, "Faculty ID not found!", Toast.LENGTH_SHORT).show();
////            }
////        });
//
//        DatabaseReference facultyRef = FirebaseDatabase.getInstance().getReference("Faculties");
//
//        facultyRef.orderByChild("facultyId").equalTo(facultyId)
//                .addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        if (dataSnapshot.exists()) {
//                            for (DataSnapshot facultySnapshot : dataSnapshot.getChildren()) {
//                                String storedPassword = facultySnapshot.child("password").getValue(String.class);
//
//                                if (storedPassword != null && storedPassword.equals(password)) {
//                                    // Save facultyID in SharedPreferences
//                                    SharedPreferences sharedPreferences = getSharedPreferences("FacultyLoginPrefs", MODE_PRIVATE);
//                                    SharedPreferences.Editor editor = sharedPreferences.edit();
//                                    editor.putString("facultyId", facultyId);
//                                    editor.apply();
//
//                                    // Redirect to faculty home screen
//                                    Toast.makeText(login_faculty.this, "Login Successful", Toast.LENGTH_SHORT).show();
//                                    startActivity(new Intent(login_faculty.this, home_faculty.class));
//                                    finish();
//                                } else {
//                                    Toast.makeText(login_faculty.this, "Incorrect Password!", Toast.LENGTH_SHORT).show();
//                                }
//                            }
//                        } else {
//                            Toast.makeText(login_faculty.this, "Faculty not found!", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//                        Toast.makeText(login_faculty.this, "Database Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                });
//    }
