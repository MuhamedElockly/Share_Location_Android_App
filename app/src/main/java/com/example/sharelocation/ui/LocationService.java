package com.example.sharelocation.ui;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LocationService extends JobService {
    FirebaseUser user;
    FirebaseAuth fAuth;
    DatabaseReference userRef;

    @Override
    public void onCreate() {
        super.onCreate();
        fAuth = FirebaseAuth.getInstance();
        user = fAuth.getCurrentUser();
        userRef = FirebaseDatabase.getInstance().getReference("users");
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        Log.e("startService", "started");
        Toast.makeText(this, "Service Started", Toast.LENGTH_SHORT).show();
        userRef.child(user.getUid()).child("name").setValue("Mohamed");
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.e("startService", "stoped");
        Toast.makeText(this, "Service Stoped", Toast.LENGTH_SHORT).show();
        return false;
    }
}
