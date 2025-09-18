package com.example.cms;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class profile_update_faculty extends AppCompatActivity {

    private EditText etFacultyName, etFacultyID, etFacultyContact, etFacultyGender, etFacultyDOB, etFacultyAddress, etFacultySpecial;
    private TextView tvFacultyEmail, tvFacultyDesignation;
    Button btnUpdateFProfile;
    private String currentName, currentContact, currentAddress, currentFID, currentDob, currentGender, currentSpecial;
    private ProgressDialog progressDialog;
    private String storedPassword;


    private FirebaseAuth auth;
    FirebaseUser currentUser;
    private DatabaseReference facultyRef, databaseRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile_update_faculty);

        etFacultyAddress = findViewById(R.id.etUPFacultyAddress);
        etFacultyContact = findViewById(R.id.etUPFacultyContact);
        etFacultyDOB = findViewById(R.id.etUPFacultyDOB);
        etFacultyID = findViewById(R.id.etUPFacultyID);
        etFacultyName = findViewById(R.id.etUPFacultyName);
        etFacultySpecial = findViewById(R.id.etUPSpecialSub);
        etFacultyGender = findViewById(R.id.etUPFacultyGender);
        tvFacultyEmail = findViewById(R.id.tvFacultyEmailUp);
        tvFacultyDesignation = findViewById(R.id.tvFacultyDesgUp);
        btnUpdateFProfile = findViewById(R.id.btnUpdateProfile);

        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        databaseRef = FirebaseDatabase.getInstance().getReference("Faculties");
        facultyRef = databaseRef.child(currentUser.getUid());

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Updating Details...");

        fetchFacultyDetails();

        btnUpdateFProfile.setOnClickListener(new View.OnClickListener() {
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
            Intent intent = new Intent(profile_update_faculty.this, home_faculty.class);
            intent.putExtra("openDrawer", true); // Send an extra flag to open the drawer
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


    private void fetchFacultyDetails() {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(profile_update_faculty.this, login_faculty.class));
            finish();
            return;
        }

        facultyRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String facultyEmail = snapshot.child("email").getValue(String.class);
                    String facultyDesg = snapshot.child("designation").getValue(String.class);

                    storedPassword = snapshot.child("password").getValue(String.class);
                    tvFacultyEmail.setText(facultyEmail);
                    tvFacultyDesignation.setText(facultyDesg);

                    currentName = snapshot.child("name").getValue(String.class);
                    currentGender = snapshot.child("gender").getValue(String.class);
                    currentContact = snapshot.child("contact").getValue(String.class);
                    currentAddress = snapshot.child("address").getValue(String.class);
                    currentDob = snapshot.child("dob").getValue(String.class);
                    currentFID = snapshot.child("facultyID").getValue(String.class);
                    currentSpecial = snapshot.child("specialization").getValue(String.class);

                    etFacultyName.setText(currentName);
                    etFacultyGender.setText(currentGender);
                    etFacultyContact.setText(currentContact);
                    etFacultyAddress.setText(currentAddress);
                    etFacultyDOB.setText(currentDob);
                    etFacultyID.setText(currentFID);
                    etFacultySpecial.setText(currentSpecial);


                } else {
                    Toast.makeText(profile_update_faculty.this, "Faculty details not found!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(profile_update_faculty.this, "Database Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void validateAndProceed() {
        String name = etFacultyName.getText().toString().trim();
        String gender = etFacultyGender.getText().toString().trim();
        String newContact = etFacultyContact.getText().toString().trim();
        String address = etFacultyAddress.getText().toString().trim();
        String fid = etFacultyID.getText().toString().trim();
        String dob = etFacultyDOB.getText().toString().trim();
        String special = etFacultySpecial.getText().toString().trim();

        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate fields
        if (name.isEmpty() || gender.isEmpty() || newContact.isEmpty() || address.isEmpty() || dob.isEmpty() || fid.isEmpty() || special.isEmpty()) {
            Toast.makeText(this, "All fields are required!", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.show();

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
                    Toast.makeText(profile_update_faculty.this, "Contact Number already exists!", Toast.LENGTH_SHORT).show();
                } else {
                    progressDialog.dismiss();
                    showPasswordDialog(name, fid, newContact, address, dob, gender, special);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
                Toast.makeText(profile_update_faculty.this, "Error checking contact number!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showPasswordDialog(String name, String fid, String newContact, String address, String dob, String gender, String special) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Password To Update Details");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        input.setHint("Enter Your Current Password");
        builder.setView(input);

        builder.setPositiveButton("Confirm", (dialog, which) -> {
            String enteredPassword = input.getText().toString().trim();
            if (enteredPassword.equals(storedPassword)) {
                updateStudentProfile(name, fid, newContact, address, dob, gender, special);
            } else {
                Toast.makeText(profile_update_faculty.this, "Incorrect Password!", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void updateStudentProfile(String name, String fid, String newContact, String address, String dob, String gender, String special) {
        progressDialog.show();

        Map<String, Object> facultyUpdates = new HashMap<>();
        facultyUpdates.put("name", name);
        facultyUpdates.put("facultyID", fid);
        facultyUpdates.put("contact", newContact);
        facultyUpdates.put("address", address);
        facultyUpdates.put("dob", dob);
        facultyUpdates.put("gender", gender);
        facultyUpdates.put("specialization", special);

        facultyRef.updateChildren(facultyUpdates).addOnCompleteListener(task -> {
            progressDialog.dismiss();
            if (task.isSuccessful()) {
                Toast.makeText(profile_update_faculty.this, "Profile updated successfully", Toast.LENGTH_LONG).show();
                startActivity(new Intent(this, profile_faculty.class));
                finish();
            } else {
                Toast.makeText(profile_update_faculty.this, "Profile update failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

}