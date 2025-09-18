package com.example.cms;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class profile_faculty extends AppCompatActivity {

    TextView fcname, fcid, fcdes, fcspecial, fcdob, fccontact, fcemail;
    FirebaseAuth auth;
    DatabaseReference databaseReference;
    String facultyID;
    private Button btn_logout, btn_update;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile_faculty);

        auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        databaseReference = FirebaseDatabase.getInstance().getReference("Faculties");

        if (currentUser == null) {
            // Redirect to login screen if user is not logged in
            Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(profile_faculty.this, login_faculty.class));
            finish();
            return;
        }

        fcname = findViewById(R.id.tvFacultyPName);
        fcdob = findViewById(R.id.tvFacultyDOB);
        fccontact = findViewById(R.id.tvFacultyContact);
        fcemail = findViewById(R.id.tvFacultyEmail);
        fcdes = findViewById(R.id.tvDesignation);
        fcspecial = findViewById(R.id.tvSpecial);
        fcid = findViewById(R.id.tvFacultyID);

        fetchFacultyDetails();

        btn_update = findViewById(R.id.btnupdate);

        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(profile_faculty.this , profile_update_faculty.class);
                startActivity(intent);
                finish();
            }
        });

        btn_logout = findViewById(R.id.btnLogout);

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
            Intent intent = new Intent(profile_faculty.this, home_faculty.class);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(profile_faculty.this, home_faculty.class);
        startActivity(intent);
        finish();
    }

    private void showLogoutConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Logout");
        builder.setMessage("Are you sure you want to log out?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Redirect to Login Screen
                Toast.makeText(profile_faculty.this, "You Have Been Logged Out", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), login_faculty.class);
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

    private void fetchFacultyDetails() {

        auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser == null) {
            // Redirect to login screen if user is not logged in
            Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(profile_faculty.this, login_faculty.class));
            finish();
            return;
        }

        String uid = currentUser.getUid();

        databaseReference.orderByChild("uid").equalTo(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot facultySnapshot : dataSnapshot.getChildren()) {
                        facultyID = facultySnapshot.getKey(); // Get facultyID (entered by faculty)
                        String facultyName = facultySnapshot.child("name").getValue(String.class);
                        String facultyDesignation = facultySnapshot.child("designation").getValue(String.class);
                        String facultySpecialization = facultySnapshot.child("specialization").getValue(String.class);
                        String facultyDOB = facultySnapshot.child("dob").getValue(String.class);
                        String facultyContact = facultySnapshot.child("contact").getValue(String.class);
                        String facultyEmail = facultySnapshot.child("email").getValue(String.class);
                        String facultyid = facultySnapshot.child("facultyID").getValue(String.class);

                        fcname.setText(facultyName);
                        fcid.setText(facultyid);
                        fcdes.setText(facultyDesignation);
                        fcspecial.setText(facultySpecialization);
                        fcdob.setText(facultyDOB);
                        fccontact.setText(facultyContact);
                        fcemail.setText(facultyEmail);
                    }
                } else {
                    Toast.makeText(profile_faculty.this, "Faculty details not found!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(profile_faculty.this, "Database Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}