package com.example.sharelocation.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.sharelocation.R;
import com.example.sharelocation.databinding.ActivityMemberBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Member extends AppCompatActivity {
    ActivityMemberBinding binding;
    DatabaseReference memberRef;
    Toolbar toolbar;
    SwipeRefreshLayout swipeRefreshLayout;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        binding = DataBindingUtil.setContentView(this, R.layout.activity_member);

        Intent intent = getIntent();
        userId = intent.getStringExtra("userId");
        Toast.makeText(this, userId, Toast.LENGTH_SHORT).show();
        refresh();

        swipeRefreshLayout = binding.swipeToRefresh;
        swipeRefreshLayout.setEnabled(false);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.room_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    private void refresh() {


        memberRef = FirebaseDatabase.getInstance().getReference("users");
        memberRef.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = snapshot.child("name").getValue(String.class);
                binding.profileName.setText(name);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}