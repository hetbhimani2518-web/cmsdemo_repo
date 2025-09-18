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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class home_faculty extends AppCompatActivity {

    TextView fcname, fcid, fcdes, fcspecial;
    CardView cv_notice, cv_events, cv_profile, cv_assignment, cv_alerts, cv_discus;
    FirebaseAuth auth;
    DatabaseReference databaseReference;
    DrawerLayout drawerLayout;
    ImageView menuIcon;
    NavigationView navigationView;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home_faculty);

        TextView marqueeTextView = findViewById(R.id.marqueeTextViewFC);
        String[] messages = {
                "📢 Welcome to the Faculty Portal!",
                "🧑‍🏫 No worry can update Your profile details anytime",
                "📢 View important notices and events sent by Admin",
                "🕒 Send assignment and submission deadlines to students",
                "🎓 Guide students through notices, chats",
                "🚀 Empower learning through timely communication",
                "📢 Stay informed with admin notices and events",
                "💬 Chat directly with admin and students!",
                "🗓️ Share important deadlines with students",
                "🔔 Receive real-time updates from admin!",
                "📝 Post academic announcements smoothly",
                "👨‍🎓 Support students through timely communication!",
                "📚 Foster an engaging learning environment.",
                "📢Notices and Assignments can be deleted automatically - just visit that screen"

        };

//      Join them with separators
        StringBuilder marqueeBuilder = new StringBuilder();
        for (String message : messages) {
            marqueeBuilder.append("   ✦ ").append(message);
        }
//        .append("   ✦ ")

        marqueeTextView.setText(marqueeBuilder.toString());
        marqueeTextView.setSelected(true); // Important to start marquee

        drawerLayout = findViewById(R.id.main);
        menuIcon = findViewById(R.id.menu_icon);
        navigationView = findViewById(R.id.navigation_view);

        menuIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.openDrawer(GravityCompat.START);
                } else {
                    drawerLayout.closeDrawer(GravityCompat.START);
                }
            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.nav_home) {
                    Toast.makeText(home_faculty.this, "Home Navigation Icon Clicked", Toast.LENGTH_SHORT).show();
                    if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                        drawerLayout.closeDrawer(GravityCompat.START);
                    } else {
                        home_faculty.super.onBackPressed();
                    }
                } else if (id == R.id.nav_notice) {
                    Toast.makeText(home_faculty.this, "Notice Navigation Icon Clicked", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(home_faculty.this, faculty_notice_selection.class);
                    startActivity(intent);
                    finish();
                } else if (id == R.id.nav_event) {
                    Toast.makeText(home_faculty.this, "Events Navigation Icon Clicked", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(home_faculty.this, faculty_events_admin.class);
                    startActivity(intent);
                    finish();
                } else if (id == R.id.nav_assignment) {
                    Toast.makeText(home_faculty.this, "Assignment Navigation Icon Clicked", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(home_faculty.this, assignment_faculty.class);
                    startActivity(intent);
                    finish();
                } else if (id == R.id.nav_alerts) {
                    Toast.makeText(home_faculty.this, "Alerts Has Opened", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(home_faculty.this, alerts_faculty.class);
                    startActivity(intent);
                    finish();
                } else if (id == R.id.nav_profile) {
                    Toast.makeText(home_faculty.this, "Faculty Profile Has Opened", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(home_faculty.this, profile_faculty.class);
                    startActivity(intent);
                    finish();
                } else if (id == R.id.nav_profile_update) {
                    Toast.makeText(home_faculty.this, "Faculty Update Profile Has Opened", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(home_faculty.this, profile_update_faculty.class);
                    startActivity(intent);
                    finish();
                } else if (id == R.id.nav_logout) {
                    showLogoutConfirmationDialog();
                }
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Faculties");

        fcname = findViewById(R.id.tv_fc_name);
        fcid = findViewById(R.id.tv_fc_id);
        fcdes = findViewById(R.id.tv_fc_des);
        fcspecial = findViewById(R.id.tv_fc_spe);

        fetchFacultyDetails();

        cv_notice = (CardView) findViewById(R.id.fc_notice_card);
        cv_events = (CardView) findViewById(R.id.fc_event_card);
        cv_profile = (CardView) findViewById(R.id.fc_profile_card);
        cv_assignment = (CardView) findViewById(R.id.fc_assignment_card);
        cv_alerts = (CardView) findViewById(R.id.fc_alerts_card);
        cv_discus = (CardView) findViewById(R.id.fc_discussion_card);

        cv_notice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(home_faculty.this, faculty_notice_selection.class);
                startActivity(intent);
                finish();
            }
        });

        cv_events.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(home_faculty.this, faculty_events_admin.class);
                startActivity(intent);
                finish();
            }
        });

        cv_discus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(home_faculty.this, Faculty_Discussion_selection.class);
                startActivity(intent);
                finish();
            }
        });

        cv_assignment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(home_faculty.this, assignment_faculty.class);
                startActivity(intent);
                finish();
            }
        });

        cv_alerts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(home_faculty.this, alerts_faculty.class);
                startActivity(intent);
                finish();
            }
        });

        cv_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(home_faculty.this, profile_faculty.class);
                startActivity(intent);
                finish();
            }
        });

//        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
//
//        // Check if the intent contains the extra flag to open the drawer
//        if (getIntent().getBooleanExtra("openDrawer", false)) {
//            drawerLayout.openDrawer(findViewById(R.id.navigation_view_st));
//        }

    }

    private void showLogoutConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Logout");
        builder.setMessage("Are you sure you want to log out?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Perform logout
//                FirebaseAuth.getInstance().signOut(); // Logout from Firebase

                // Redirect to Login Screen
                Toast.makeText(home_faculty.this, "You Have Been Logged Out", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(home_faculty.this, login_faculty.class);
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


    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void fetchFacultyDetails() {

        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show();
            return;
        }

        String uid = user.getUid();

        databaseReference.orderByChild("uid").equalTo(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot facultySnapshot : dataSnapshot.getChildren()) {
                        String facultyID = facultySnapshot.child("facultyID").getValue(String.class);
                        String facultyName = facultySnapshot.child("name").getValue(String.class);
                        String facultyDesignation = facultySnapshot.child("designation").getValue(String.class);
                        String facultySpecialization = facultySnapshot.child("specialization").getValue(String.class);

                        // Set data in UI
                        fcname.setText(facultyName);
                        fcid.setText("FacultyID - " + facultyID);
                        fcdes.setText("Designation - " + facultyDesignation);
                        fcspecial.setText("Specialization Subject - " + facultySpecialization);
                    }
                } else {
                    Toast.makeText(home_faculty.this, "Faculty details not found!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(home_faculty.this, "Database Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}