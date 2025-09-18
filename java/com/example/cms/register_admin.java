package com.example.cms;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class register_admin extends AppCompatActivity {

    Button btn_ad_return_login, btnRegister;
    EditText etadname, etemail, et_adpass, et_adrepass;
    DatabaseReference adminRef;
    FirebaseAuth auth;
    private ProgressDialog progressDialog;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register_admin);

        btn_ad_return_login = findViewById(R.id.btn_return_login);
        btnRegister = findViewById(R.id.btn_register_ad);
        etemail = findViewById(R.id.et_ad_email);
        etadname = findViewById(R.id.et_ad_name);
        et_adpass = findViewById(R.id.et_ad_password);
        et_adrepass = findViewById(R.id.et_ad_confirm_password);

        adminRef = FirebaseDatabase.getInstance().getReference("Admin");
        auth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Registering Admin...");

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkValidations();
            }
        });

        btn_ad_return_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(register_admin.this, login_admin.class);
                startActivity(intent);
                finish();
            }
        });

    }

    private void checkValidations() {
        String name = etadname.getText().toString().trim();
        String email = etemail.getText().toString().trim();
        String password = et_adpass.getText().toString().trim();
        String confirmPassword = et_adrepass.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) ||
                TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(this, "All fields are required!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(register_admin.this, "Password must be at least 6 characters long!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Enter a valid email address!", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.show();

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();
                        if (user != null) {
                            String uid = user.getUid();

                            HashMap<String, Object> adminData = new HashMap<>();
                            adminData.put("name", name);
                            adminData.put("email", email);
                            adminData.put("password", password);

                            adminRef.child(uid).setValue(adminData)
                                    .addOnSuccessListener(unused -> {
                                        progressDialog.dismiss();
                                        Toast.makeText(register_admin.this, "Registration Successful!", Toast.LENGTH_SHORT).show();
                                        clearFields();
                                        startActivity(new Intent(register_admin.this, login_admin.class));
                                        finish();
                                    })
                                    .addOnFailureListener(e -> {
                                        progressDialog.dismiss();
                                        Toast.makeText(register_admin.this, "Registration Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        }
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(register_admin.this, "Authentication Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(register_admin.this, login_admin.class);
        startActivity(intent);
        finish();
    }


    private void clearFields() {
        etadname.setText("");
        etemail.setText("");
        et_adpass.setText("");
        et_adrepass.setText("");
    }
}