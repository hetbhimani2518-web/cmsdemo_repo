package com.example.cms;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class admin_notice_sel extends AppCompatActivity {

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_notice_sel);

        CardView cardStudent = findViewById(R.id.card_student);
        CardView cardFaculty = findViewById(R.id.card_faculty);

        cardStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(admin_notice_sel.this, admin_stu_not.class);
                startActivity(intent);
                finish();
            }
        });

        cardFaculty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(admin_notice_sel.this, admin_fac_not.class);
                startActivity(intent);
                finish();
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
            Intent intent = new Intent(admin_notice_sel.this, home_admin.class);
            intent.putExtra("openDrawer", true); // Send an extra flag to open the drawer
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}