package com.example.cms;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.view.MenuItem;
import android.view.View;
import androidx.cardview.widget.CardView;
import android.os.Bundle;
import android.widget.Toast;


public class admin_users_view extends AppCompatActivity {

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_users_view);

        CardView cardStudents = findViewById(R.id.card_students);
        CardView cardFaculty = findViewById(R.id.card_faculty);

        cardStudents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(admin_users_view.this, admin_stu_view.class);
                startActivity(intent);
            }
        });

        cardFaculty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(admin_users_view.this, admin_fac_view.class);
                startActivity(intent);
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
            Intent intent = new Intent(admin_users_view.this, home_admin.class);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}