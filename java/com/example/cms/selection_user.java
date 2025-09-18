package com.example.cms;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class selection_user extends AppCompatActivity {

    CardView card_faculty,card_student , card_admin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_selection_user);

        card_faculty = findViewById(R.id.cardTeacher);
        card_student = findViewById(R.id.cardStudent);
        card_admin = findViewById(R.id.cardAdmin);

        card_faculty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(selection_user.this , login_faculty.class);
                startActivity(intent);
                finish();
            }
        });

        card_student.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(selection_user.this , login_student.class);
                startActivity(intent);
                finish();
            }
        });

        card_admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(selection_user.this , login_admin.class);
                startActivity(intent);
                finish();
            }
        });

    }
}