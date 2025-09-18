package com.example.cms;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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

import java.util.HashMap;

public class home_student extends AppCompatActivity {

    CardView noticeCard, discuCard, assignmentCard, alertCard, eventsCard, timetableCard;
    DrawerLayout drawerLayout;
    ImageView menuIcon;
    NavigationView navigationView;
    TextView tvStudentName, tvStudentProgram, tvStudentSemesterDiv;
    DatabaseReference studentRef;
    FirebaseAuth auth;
    String studentSemester, studentProgram;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home_student);

        TextView marqueeTextView = findViewById(R.id.marqueeTextView);
        String[] messages = {
                "📢 Welcome to the Student Portal!",
                "Stay updated with the latest notices.",
                "📅 Don't forget to check your assignments!",
                "🎯 Stay focused and keep learning.",
                "🏆 Best of luck for your semester!",
                "📅 Check Your Schedule & Never Miss a Class!",
                "📖 Be Curious. Keep Learning. Grow Daily.",
                "⚠️ Never miss to view alerts about assignment and notices.",
                "📝 Assignments? Deadlines? Stay Sharp!",
                "🎯 Dream Big. Work Hard. Achieve More.",
                "💡 Smart Work + Consistency = Success!",
                "🚀 Your Future Begins with Every Lesson!"
        };

//      Join them with separators
        StringBuilder marqueeBuilder = new StringBuilder();
        for (String message : messages) {
            marqueeBuilder.append("   ✦ ").append(message);
        }
//        .append("   ✦ ")

        marqueeTextView.setText(marqueeBuilder.toString());
        marqueeTextView.setSelected(true); // Important to start marquee


        noticeCard = findViewById(R.id.st_notice_card);
        discuCard = findViewById(R.id.st_discussion_card);
        assignmentCard = findViewById(R.id.assignment_card);
        alertCard = findViewById(R.id.alerts_card);
        eventsCard = findViewById(R.id.events_card);
        timetableCard = findViewById(R.id.timetable_card);

        noticeCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(home_student.this, student_notice_selection.class);
                startActivity(intent);
                finish();
            }
        });

        eventsCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(home_student.this, student_events_admin.class);
                startActivity(intent);
                finish();
            }
        });

        discuCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(home_student.this, discussion_student.class);
                startActivity(intent);
                finish();
            }
        });

        assignmentCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(home_student.this, assignment_student.class);
                startActivity(intent);
                finish();
            }
        });

        alertCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(home_student.this, alerts_student.class);
                startActivity(intent);
                finish();
            }
        });
        timetableCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (studentProgram != null && studentSemester != null) {
                    openCorrectTimetable(studentProgram, studentSemester);
                } else {
                    Toast.makeText(home_student.this, "Data not found.", Toast.LENGTH_SHORT).show();
                }

            }
        });

        drawerLayout = findViewById(R.id.drawer_layout);
        menuIcon = findViewById(R.id.menu_icon_st);
        navigationView = findViewById(R.id.navigation_view_st);

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
                    Toast.makeText(home_student.this, "Home Navigation Icon Clicked", Toast.LENGTH_SHORT).show();
                    if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                        drawerLayout.closeDrawer(GravityCompat.START);
                    } else {
                        home_student.super.onBackPressed();
                    }
                } else if (id == R.id.nav_notice) {
                    Toast.makeText(home_student.this, "Notices Has Opened", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(home_student.this, student_notice_selection.class);
                    startActivity(intent);
                    finish();
                } else if (id == R.id.nav_event) {
                    Toast.makeText(home_student.this, "Events Has Opened", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(home_student.this, student_events_admin.class);
                    startActivity(intent);
                    finish();
                } else if (id == R.id.nav_assignment) {
                    Toast.makeText(home_student.this, "Assignment Has Opened", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(home_student.this, assignment_student.class);
                    startActivity(intent);
                    finish();
                } else if (id == R.id.nav_alerts) {
                    Toast.makeText(home_student.this, "Alerts Has Opened", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(home_student.this, alerts_student.class);
                    startActivity(intent);
                    finish();
                } else if (id == R.id.nav_profile) {
                    Toast.makeText(home_student.this, "Student Profile Has Opened", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(home_student.this, profile_student.class);
                    startActivity(intent);
                    finish();
                } else if (id == R.id.nav_profile_update) {
                    Toast.makeText(home_student.this, "Student Update Profile Has Opened", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(home_student.this, profile_update_student.class);
                    startActivity(intent);
                    finish();
                } else if (id == R.id.nav_logout) {
                    showLogoutConfirmationDialog();
                }
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        tvStudentName = findViewById(R.id.tv_student_name);
        tvStudentProgram = findViewById(R.id.tv_program);
        tvStudentSemesterDiv = findViewById(R.id.tv_semester_division);

        auth = FirebaseAuth.getInstance();
        studentRef = FirebaseDatabase.getInstance().getReference("Students");

        fetchStudentDetails();

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);

        if (getIntent().getBooleanExtra("openDrawer", false)) {
            drawerLayout.openDrawer(findViewById(R.id.navigation_view_st));
        }
    }

    private void showLogoutConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Logout");
        builder.setMessage("Are you sure you want to log out?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Redirect to Login Screen
                Toast.makeText(home_student.this, "You Have Been Logged Out", Toast.LENGTH_SHORT).show();
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

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void fetchStudentDetails() {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(home_student.this, login_student.class));
            finish();
            return;
        }

        String userId = user.getUid();

        studentRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String studentName = snapshot.child("name").getValue(String.class);
                    studentProgram = snapshot.child("program").getValue(String.class);
                    studentSemester = snapshot.child("semester").getValue(String.class);
                    String studentDivision = snapshot.child("division").getValue(String.class);

                    tvStudentName.setText(studentName);
                    tvStudentProgram.setText(studentProgram);

                    String semesterDivision = studentSemester + " - " + studentDivision;
                    tvStudentSemesterDiv.setText(semesterDivision);
                } else {
                    Toast.makeText(home_student.this, "Student details not found!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(home_student.this, "Database Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openCorrectTimetable(String studentProgram, String studentSemester) {
        Intent intent = null;

        if ("Bachelor of Computer Application".equalsIgnoreCase(studentProgram)) {
            switch (studentSemester) {
                case "Semester 1":
                    intent = new Intent(this, bca_sem1.class);
                    break;
                case "Semester 2":
                    intent = new Intent(this, bca_sem2.class);
                    break;
                case "Semester 3":
                    intent = new Intent(this, bca_sem3.class);
                    break;
                case "Semester 4":
                    intent = new Intent(this, bca_sem4.class);
                    break;
                case "Semester 5":
                    intent = new Intent(this, bca_sem5.class);
                    break;
                case "Semester 6":
                    intent = new Intent(this, bca_sem6.class);
                    break;
            }
        } else if ("Bachelor of Business Administration".equalsIgnoreCase(studentProgram)) {
            Toast.makeText(home_student.this, "Timetable not added for BBA Student!", Toast.LENGTH_SHORT).show();
        }

        if (intent != null) {
            startActivity(intent);
        } else {
            Toast.makeText(home_student.this, "Timetable not found!", Toast.LENGTH_SHORT).show();
        }
    }


}
