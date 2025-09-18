package com.example.cms;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
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

public class admin_fac_view extends AppCompatActivity {

    SearchView searchFaculty;
    RecyclerView recyclerView;
    private admin_fac_view_adapter adapter;
    private List<admin_fac_view_model> facultyList, filteredList;
    private DatabaseReference facultyRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_fac_view);

        searchFaculty = findViewById(R.id.searchFaculty);
        recyclerView = findViewById(R.id.recyclerViewFaculty);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        facultyList = new ArrayList<>();
        filteredList = new ArrayList<>();
        adapter = new admin_fac_view_adapter(filteredList);
        recyclerView.setAdapter(adapter);

        facultyRef = FirebaseDatabase.getInstance().getReference("Faculties");

        fetchFaculty();

        searchFaculty.setVisibility(View.VISIBLE);

        // Step 2: Expand Animation
        searchFaculty.setOnSearchClickListener(view -> {
            searchFaculty.animate()
                    .translationX(0)
                    .alpha(1.0f)
                    .setDuration(300)
                    .start();
        });

        // Step 3: Collapse Animation
        searchFaculty.setOnCloseListener(() -> {
            searchFaculty.animate()
                    .translationX(-50)
                    .alpha(0.0f)
                    .setDuration(300)
                    .start();
            return false;
        });

        // Step 4: Background Change on Focus
        searchFaculty.setOnQueryTextFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                searchFaculty.setBackground(ContextCompat.getDrawable(this, R.drawable.search_view_focused_background));
            } else {
                searchFaculty.setBackground(ContextCompat.getDrawable(this, R.drawable.search_view_background));
            }
        });

        searchFaculty.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterFaculty(query);
                if (query.length() < 3) {
                    shakeView(searchFaculty); // Shake effect if search query is invalid
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterFaculty(newText);
                return false;
            }
        });

        searchFaculty.setOnCloseListener(() -> {
            searchFaculty.animate()
                    .alpha(0f)
                    .setDuration(300)
                    .withEndAction(() -> searchFaculty.setVisibility(View.GONE))
                    .start();
            return false;
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
            Intent intent = new Intent(admin_fac_view.this, admin_users_view.class);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void fetchFaculty() {
        facultyRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                facultyList.clear();
                for (DataSnapshot facultySnapshot : snapshot.getChildren()) {
                    String name = facultySnapshot.child("name").getValue(String.class);
                    String contact = facultySnapshot.child("contact").getValue(String.class);
                    String designation = facultySnapshot.child("designation").getValue(String.class);
                    facultyList.add(new admin_fac_view_model(name ,designation, contact ));
                }
                filteredList.clear();
                filteredList.addAll(facultyList);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(admin_fac_view.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterFaculty(String query) {
        filteredList.clear();
        if (query.isEmpty()) {
            filteredList.addAll(facultyList);
        } else {
            for (admin_fac_view_model faculty : facultyList) {
                if (faculty.getName().toLowerCase().contains(query.toLowerCase()) ||
                        faculty.getDesignation().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(faculty);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void shakeView(View view) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "translationX", 0, 10, -10, 10, -10, 5, -5, 0);
        animator.setDuration(500);
        animator.start();
    }

}