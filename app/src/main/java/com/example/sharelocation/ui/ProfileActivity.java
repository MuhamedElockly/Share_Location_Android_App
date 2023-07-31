package com.example.sharelocation.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.example.sharelocation.R;
import com.example.sharelocation.databinding.ActivityProfileBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ProfileActivity extends AppCompatActivity {
    private ActivityProfileBinding binding;
    private FirebaseAuth fAuth;
    private FirebaseUser user;
    private DatabaseReference userRef;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile);

        setSupportActionBar(binding.profileToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();

        String memberName = intent.getStringExtra("memberName");
        getSupportActionBar().setTitle(memberName);

        fAuth = FirebaseAuth.getInstance();
        user = fAuth.getCurrentUser();
        userId = user.getUid();


        refresh();
        binding.swipeToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
                binding.swipeToRefresh.setRefreshing(false);
            }
        });
        //   binding.swipeToRefresh.setEnabled(false);
        updatePasswardView();


    }

    private void updatePasswardView() {
        binding.changePasswardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.changePassward.setVisibility(View.GONE);
                binding.passwardDialoge.setVisibility(View.VISIBLE);
            }
        });
        binding.cancelPassward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                binding.passwardDialoge.setVisibility(View.GONE);
                binding.changePassward.setVisibility(View.VISIBLE);
            }
        });
    }

    private void refresh() {
        userRef = FirebaseDatabase.getInstance().getReference("users");
        userRef.child(userId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {

                    DataSnapshot snapshot = task.getResult();
                    String profilePhotoUri = snapshot.child("profilePhoto").getValue(String.class);
                    binding.name.setText(snapshot.child("name").getValue(String.class));
                    binding.email.setText(snapshot.child("email").getValue(String.class));
                    //  Log.e("profileName", String.valueOf(snapshot.getValue()));
                    binding.phoneNumber.setText("01000594861");
                    Glide.with(getApplicationContext()).load(profilePhotoUri).into(binding.profilePhoto);

                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}