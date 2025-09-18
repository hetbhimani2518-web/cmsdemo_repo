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

public class login_admin extends AppCompatActivity {

    Button btn_ad_register, btn_ad_login;
    EditText etemail, etPassword;
    private FirebaseAuth auth;
    private ProgressDialog progressDialog;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_admin);

        btn_ad_register = findViewById(R.id.btn_reg_ad);
        btn_ad_login = findViewById(R.id.btn_login_ad);
        etemail = findViewById(R.id.etadid);
        etPassword = findViewById(R.id.etadPassword);

        auth = FirebaseAuth.getInstance();
        DatabaseReference adminRef = FirebaseDatabase.getInstance().getReference("Admin");
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Logging in...");

        btn_ad_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(login_admin.this, register_admin.class);
                startActivity(intent);
                finish();
            }
        });

        btn_ad_login.setOnClickListener(v -> loginAdmin());
    }

    public void loginAdmin() {
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
                            Intent intent = new Intent(login_admin.this, home_admin.class);
                            startActivity(intent);
                            finish();
                        }
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(login_admin.this, "Login Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(login_admin.this, selection_user.class);
        startActivity(intent);
        finish();
    }
}