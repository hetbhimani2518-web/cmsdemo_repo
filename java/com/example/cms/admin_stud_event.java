package com.example.cms;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class admin_stud_event extends AppCompatActivity {

    private Spinner spinnerCourse;
    private EditText etEventTitle, etEventDescription;
    private Button btnSendEvent;
    private DatabaseReference eventRef;
    private String selectedCourse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_stud_event);

        spinnerCourse = findViewById(R.id.spinnerCourse);
        etEventTitle = findViewById(R.id.etEventTitle);
        etEventDescription = findViewById(R.id.etEventContent);
        btnSendEvent = findViewById(R.id.btnSendEvent);

        ArrayAdapter<CharSequence> programAdapter = ArrayAdapter.createFromResource(this, R.array.program_array, android.R.layout.simple_spinner_item);
        programAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCourse.setAdapter(programAdapter);

        spinnerCourse.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCourse = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        eventRef = FirebaseDatabase.getInstance().getReference("AdminToStudentsEvents");

        btnSendEvent.setOnClickListener(v -> sendEvent());

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(admin_stud_event.this, admin_events.class);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(admin_stud_event.this, admin_events.class);
        startActivity(intent);
        finish();
    }

    private void clearFields() {
        etEventTitle.setText("");
        etEventDescription.setText("");
    }

    private void sendEvent() {
        String title = etEventTitle.getText().toString().trim();
        String description = etEventDescription.getText().toString().trim();
        String date = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());

        if (title.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        HashMap<String, String> eventData = new HashMap<>();
        eventData.put("course", selectedCourse);
        eventData.put("title", title);
        eventData.put("description", description);
        eventData.put("date", date);

        eventRef.push().setValue(eventData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(admin_stud_event.this, "Event Sent To Student Successfully", Toast.LENGTH_SHORT).show();
                    clearFields();
                })
                .addOnFailureListener(e -> Toast.makeText(admin_stud_event.this, "Failed to send event", Toast.LENGTH_SHORT).show());
    }

}