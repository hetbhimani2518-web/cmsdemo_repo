package com.example.cms;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class admin_fac_event extends AppCompatActivity {

    private EditText etEventTitle, etEventContent;
    private DatabaseReference eventsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_fac_event);

        etEventTitle = findViewById(R.id.etEventTitle_fc);
        etEventContent = findViewById(R.id.etEventContent_fc);
        Button btnSendEvent = findViewById(R.id.btnSendEvent);

        eventsRef = FirebaseDatabase.getInstance().getReference("AdminToFacultyEvents");

        btnSendEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEvent();
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
            Intent intent = new Intent(admin_fac_event.this, admin_events.class);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(admin_fac_event.this, admin_events.class);
        startActivity(intent);
        finish();
    }

    private void clearFields() {
        etEventTitle.setText("");
        etEventContent.setText("");
    }

    private void sendEvent() {
        String title = etEventTitle.getText().toString().trim();
        String content = etEventContent.getText().toString().trim();
        String date = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());

        if (title.isEmpty() || content.isEmpty()) {
            Toast.makeText(this, "Please enter both title and content", Toast.LENGTH_SHORT).show();
            return;
        }

        HashMap<String, String> eventData = new HashMap<>();
        eventData.put("title", title);
        eventData.put("content", content);
        eventData.put("date", date);

        eventsRef.push().setValue(eventData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(admin_fac_event.this, "Event Sent To Faculty Successfully", Toast.LENGTH_SHORT).show();
                    clearFields();
                })
                .addOnFailureListener(e -> Toast.makeText(admin_fac_event.this, "Failed to send event", Toast.LENGTH_SHORT).show());
    }
}