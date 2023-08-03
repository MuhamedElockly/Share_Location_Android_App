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
    private boolean jobCanceled = false;

    @Override
    public void onCreate() {
        super.onCreate();
        fAuth = FirebaseAuth.getInstance();
        user = fAuth.getCurrentUser();
        userRef = FirebaseDatabase.getInstance().getReference("users");
    }
int n=3000;
    private void doBackground(JobParameters parameters) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 20; i++) {
                    if (jobCanceled) {
                        return;
                    }
                    Log.e("startService", "started");
                    // Toast.makeText(this, "Service Started", Toast.LENGTH_SHORT).show();
                    userRef.child(user.getUid()).child("name").setValue("Mohamed" + i);
                    try {
                        Thread.sleep(n);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                jobFinished(parameters, false);
            }
        }).start();
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        doBackground(params);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.e("startService", "stoped");
        Toast.makeText(this, "Service Stoped", Toast.LENGTH_SHORT).show();
        jobCanceled = true;
        return true;
    }
}
