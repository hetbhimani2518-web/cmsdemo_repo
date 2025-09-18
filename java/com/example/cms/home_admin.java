package com.example.cms;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class home_admin extends AppCompatActivity {

    TextView tvadname;
    DatabaseReference adminRef;
    FirebaseAuth auth;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home_admin);

        tvadname = findViewById(R.id.tv_welcome_adname);
        auth = FirebaseAuth.getInstance();
        adminRef = FirebaseDatabase.getInstance().getReference("Admin");

        fetchAdminDetails();

        CardView notice_card = findViewById(R.id.admin_notice_card);
        CardView events_card = findViewById(R.id.admin_event_card);
        CardView users_card = findViewById(R.id.users_card);
        CardView dicuss_card = findViewById(R.id.admin_discussion_card);

        notice_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(home_admin.this,admin_notice_sel.class);
                startActivity(intent);
                finish();
            }
        });

        events_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ievent = new Intent(home_admin.this,admin_events.class);
                startActivity(ievent);
                finish();
            }
        });

        users_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iusers = new Intent(home_admin.this, admin_users_view.class);
                startActivity(iusers);
                finish();
            }
        });

        dicuss_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent idiscuss = new Intent(home_admin.this,admin_discuss.class);
                startActivity(idiscuss);
                finish();
            }
        });

    }

    private void fetchAdminDetails(){
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(home_admin.this, login_admin.class));
            finish();
            return;
        }

        String userId = user.getUid();


        adminRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String adminName = snapshot.child("name").getValue(String.class);

                    tvadname.setText("Welcome!\n" + adminName);

                } else {
                    Toast.makeText(home_admin.this, "Admin details not found!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(home_admin.this, "Database Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}