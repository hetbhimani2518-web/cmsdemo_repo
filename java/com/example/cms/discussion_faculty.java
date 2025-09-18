package com.example.cms;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class discussion_faculty extends AppCompatActivity {

    FirebaseUser currentUser;
    String currentFacultyId;
    RecyclerView recyclerView;
    SearchView searchView;

    List<ChatModelFaculty_StudentDetails> fullList = new ArrayList<>();
    ChatAdapterFaculty_StudentDetails adapter = new ChatAdapterFaculty_StudentDetails(fullList, this);
    DatabaseReference chatRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_discussion_faculty);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        currentFacultyId = currentUser.getUid();
        recyclerView = findViewById(R.id.recyclerView);
        searchView = findViewById(R.id.searchView);

//        searchView.clearFocus();

//      Step 1: Start with SearchView Collapsed
//        searchView.setVisibility(View.INVISIBLE);
//        searchView.postDelayed(() -> {
//            searchView.setVisibility(View.VISIBLE);
//            searchView.setTranslationY(-100);
//            searchView.setAlpha(0f);
//            searchView.animate()
//                    .translationY(0)
//                    .alpha(1.0f)
//                    .setDuration(500)
//                    .setInterpolator(new AccelerateDecelerateInterpolator())
//                    .start();
//        }, 200);

        searchView.setVisibility(View.VISIBLE);

        // Step 2: Expand Animation
        searchView.setOnSearchClickListener(view -> {
            searchView.animate()
                    .translationX(0)
                    .alpha(1.0f)
                    .setDuration(300)
                    .start();
        });

        // Step 3: Collapse Animation
        searchView.setOnCloseListener(() -> {
            searchView.animate()
                    .translationX(-50)
                    .alpha(0.0f)
                    .setDuration(300)
                    .start();
            return false;
        });

        // Step 4: Background Change on Focus
        searchView.setOnQueryTextFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                searchView.setBackground(ContextCompat.getDrawable(this, R.drawable.search_view_focused_background));
            } else {
                searchView.setBackground(ContextCompat.getDrawable(this, R.drawable.search_view_background));
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.filter(query);
                if (query.length() < 3) {
                    shakeView(searchView); // Shake effect if search query is invalid
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.filter(newText);
                return false;
            }
        });

        // Step 6: Fade-out Effect on Close
        searchView.setOnCloseListener(() -> {
            searchView.animate()
                    .alpha(0f)
                    .setDuration(300)
                    .withEndAction(() -> searchView.setVisibility(View.GONE))
                    .start();
            return false;
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        chatRef = FirebaseDatabase.getInstance().getReference("Chats");

        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Set<String> studentIds = new HashSet<>();

                for (DataSnapshot chatSnap : snapshot.getChildren()) {
                    String chatKey = chatSnap.getKey();
                    if (chatKey != null && chatKey.endsWith(currentFacultyId)) {
                        String[] ids = chatKey.split("_");
                        if (ids.length == 2) {
                            String studentId = ids[0];
                            studentIds.add(studentId);
                        }
                    }
                }
                loadStudentDetails(studentIds);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
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
            Intent intent = new Intent(discussion_faculty.this, Faculty_Discussion_selection.class);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void shakeView(View view) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "translationX", 0, 10, -10, 10, -10, 5, -5, 0);
        animator.setDuration(500);
        animator.start();
    }

    private void loadStudentDetails(Set<String> studentIds) {
        DatabaseReference studentRef = FirebaseDatabase.getInstance().getReference("Students");

        List<ChatModelFaculty_StudentDetails> studentList = new ArrayList<>();

        for (String studentId : studentIds) {
            studentRef.child(studentId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        ChatModelFaculty_StudentDetails student = snapshot.getValue(ChatModelFaculty_StudentDetails.class);
                        if (student != null) {
                            student.setStudentId(snapshot.getKey()); // Store UID
                            studentList.add(student);
                            adapter.updateList(studentList);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(discussion_faculty.this, Faculty_Discussion_selection.class);
        startActivity(intent);
        finish();
    }
}
