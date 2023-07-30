package com.example.sharelocation.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.sharelocation.R;
import com.example.sharelocation.databinding.ActivityProfileBinding;

public class ProfileActivity extends AppCompatActivity {
    private ActivityProfileBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile);

        setSupportActionBar(binding.profileToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();

        String memberName = intent.getStringExtra("memberName");
        getSupportActionBar().setTitle(memberName);

        binding.swipeToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //   refresh();
                binding.swipeToRefresh.setRefreshing(false);
            }
        });
        binding.swipeToRefresh.setEnabled(false);
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