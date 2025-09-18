package com.example.cms;

import android.annotation.SuppressLint;
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

import java.util.ArrayList;
import java.util.List;

public class notice_faculty_admin extends AppCompatActivity {

    private RecyclerView recyclerView;
    private faculty_notice_adapter_admin adapter;
    private ArrayList<faculty_notice_model_admin> noticeList;
    private DatabaseReference noticeRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_notice_faculty_admin);

        recyclerView = findViewById(R.id.rvFaculty_AdminNotices);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        noticeList = new ArrayList<>();
        adapter = new faculty_notice_adapter_admin(noticeList);
        recyclerView.setAdapter(adapter);

        noticeRef = FirebaseDatabase.getInstance().getReference("AdminToFacultyNotices");
        loadNotices();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(notice_faculty_admin.this, faculty_notice_selection.class);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(notice_faculty_admin.this, faculty_notice_selection.class);
        startActivity(intent);
        finish();
    }


    private void loadNotices() {
        noticeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                noticeList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    faculty_notice_model_admin notice = dataSnapshot.getValue(faculty_notice_model_admin.class);
                    noticeList.add(notice);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(notice_faculty_admin.this, "Failed to load notices", Toast.LENGTH_SHORT).show();
            }
        });
    }

}