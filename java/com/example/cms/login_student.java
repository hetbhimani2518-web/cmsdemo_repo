package com.example.cms;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class login_student extends AppCompatActivity {

    Button btn_stud_register, btn_stud_login;
    EditText etemail, etPassword;
    private FirebaseAuth auth;
    private DatabaseReference studentRef;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_student);

        btn_stud_register = findViewById(R.id.btn_reg_stud);
        btn_stud_login = findViewById(R.id.btn_login_stud);
        etemail = findViewById(R.id.etstudid);
        etPassword = findViewById(R.id.etstudPassword);

        auth = FirebaseAuth.getInstance();
        studentRef = FirebaseDatabase.getInstance().getReference("Students");
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Logging in...");

        btn_stud_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(login_student.this, register_student.class);
                startActivity(intent);
                finish();
            }
        });

        btn_stud_login.setOnClickListener(v -> loginStudent());
    }

    public void loginStudent() {
        String email = etemail.getText().toString().trim();
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
                            Intent intent = new Intent(login_student.this, home_student.class);
                            startActivity(intent);
                            finish();
                        }
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(login_student.this, "Login Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(login_student.this, selection_user.class);
        startActivity(intent);
        finish();
    }


//        databaseReference.orderByChild("contact").equalTo(contact)
//                .addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        if (dataSnapshot.exists()) {
//                            for (DataSnapshot studentSnapshot : dataSnapshot.getChildren()) {
//                                String storedPassword = studentSnapshot.child("password").getValue(String.class);
//                                String studentName = studentSnapshot.child("name").getValue(String.class);
//                                String studentID = studentSnapshot.getKey(); // Get unique student ID
//
//                                if (storedPassword != null && storedPassword.equals(password)) {
//                                    // Save login session
//                                    SharedPreferences.Editor editor = getSharedPreferences("StudentLoginPrefs", MODE_PRIVATE).edit();
//                                    editor.putString("contact", contact);
//                                    editor.putString("studentID", studentID);
//                                    editor.putString("studentName", studentName);
//                                    editor.apply();
//
//                                    Toast.makeText(login_student.this, "Login Successful", Toast.LENGTH_SHORT).show();
//                                    startActivity(new Intent(login_student.this, home_student.class));
//                                    finish();
//                                    return;
//                                }
//                            }
//                            Toast.makeText(login_student.this, "Invalid Password!", Toast.LENGTH_SHORT).show();
//                        } else {
//                            Toast.makeText(login_student.this, "Contact Number not found!", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//                        Toast.makeText(login_student.this, "Database Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                });

//        String contact = stcontactlog.getText().toString().trim();
//        String password = stpasslog.getText().toString().trim();
//
//        if (TextUtils.isEmpty(contact) || TextUtils.isEmpty(password)) {
//            Toast.makeText(this, "All fields are required!", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//
//        databaseReference.orderByChild("contact").equalTo(contact).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if (dataSnapshot.exists()) {
//                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                        String email = snapshot.child("email").getValue(String.class);
//
//                        if (email != null) {
//                            FirebaseAuth auth = FirebaseAuth.getInstance();
//                            auth.signInWithEmailAndPassword(email, password)
//                                    .addOnCompleteListener(task -> {
//                                        if (task.isSuccessful()) {
//                                            Toast.makeText(login_student.this, "Login Successful!", Toast.LENGTH_SHORT).show();
//                                            startActivity(new Intent(login_student.this, home_student.class));
//                                            finish();
//                                        } else {
//                                            Toast.makeText(login_student.this, "Invalid password!", Toast.LENGTH_SHORT).show();
//                                        }
//                                    });
//                        }
//                    }
//                } else {
//                    Toast.makeText(login_student.this, "No user found with this contact number!", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Toast.makeText(login_student.this, "Database Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });


}

