package com.example.cms;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

public class faculty_events_admin extends AppCompatActivity {

    private RecyclerView recyclerView;
    private faculty_evets_adapter_admin eventAdapter;
    private ArrayList<faculty_events_model_admin> eventList;
    private DatabaseReference eventsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_faculty_events_admin);

        recyclerView = findViewById(R.id.rvFaculty_AdminEvents);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        eventList = new ArrayList<>();
        eventAdapter = new faculty_evets_adapter_admin(eventList);
        recyclerView.setAdapter(eventAdapter);

        eventsRef = FirebaseDatabase.getInstance().getReference("AdminToFacultyEvents");

        fetchEvents();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

    }

    private void fetchEvents() {
        eventsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                eventList.clear();
                long currentTime = System.currentTimeMillis();
                long expiryTime = 30L * 24 * 60 * 60 * 1000; // 30 days in milliseconds

                for (DataSnapshot data : snapshot.getChildren()) {
                    faculty_events_model_admin event = data.getValue(faculty_events_model_admin.class);
                    if (event != null) {
                        long eventTime = parseDateToMillis(event.getDate());
                        if (currentTime - eventTime <= expiryTime) {
                            eventList.add(event);
                        } else {
                            data.getRef().removeValue(); // Delete expired events
                        }
                    }
                }
                Collections.reverse(eventList); // Latest events on top
                eventAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(faculty_events_admin.this, "Failed to load events", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private long parseDateToMillis(String dateString) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date date = sdf.parse(dateString);
            return (date != null) ? date.getTime() : 0;
        } catch (Exception e) {
            return 0;
        }
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(faculty_events_admin.this, home_faculty.class);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(faculty_events_admin.this, home_faculty.class);
        startActivity(intent);
        finish();
    }

}