package com.example.sharelocation.ui;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sharelocation.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class SettingsActivity extends AppCompatActivity {

    androidx.appcompat.widget.Toolbar toolbar;
    FirebaseAuth fAuth;
    FirebaseUser user;
    DatabaseReference userRoomsRef;
    DatabaseReference roomMembers;
    StorageReference imageRef;
    Button deleteButton;
    ArrayList<String> deletedRooms;
    int roomdeleted = 0;
    ProgressBar progressBar;
    Button dialogeCancel;
    Button dialogeSure;
    String userId;
    Switch locationSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        progressBar = findViewById(R.id.settingPBar);
        toolbar = findViewById(R.id.settingToolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Settings");
        fAuth = FirebaseAuth.getInstance();
        user = fAuth.getCurrentUser();
        userId = user.getUid();
        deletedRooms = new ArrayList<>();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        deleteButton = findViewById(R.id.deletAccount);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirmationDialoge();
            }
        });
        locationSwitch = findViewById(R.id.shareLocationSwitch);
        shareLocation();
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

    private void shareLocation() {
        locationSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (locationSwitch.isChecked()) {
                    //    Toast.makeText(SettingsActivity.this, "Share Location Switch Onnnn", Toast.LENGTH_SHORT).show();
                    ComponentName componentName = new ComponentName(getBaseContext(), LocationService.class);
                    JobInfo jobInfo;
                    if(Build.VERSION.SDK_INT<=Build.VERSION_CODES.N) {
                        jobInfo = new JobInfo.Builder(10, componentName)
                                .setPeriodic(500)
                                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                                .build();
                    }else{
                        jobInfo = new JobInfo.Builder(10, componentName)
                                .setMinimumLatency(500)
                                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                                .build();
                    }
                    JobScheduler jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
                    jobScheduler.schedule(jobInfo);

                } else {
                    //     Toast.makeText(SettingsActivity.this, "Share Location Switch offf", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void deletAccount() {
        deletedRooms.clear();

        userRoomsRef = FirebaseDatabase.getInstance().getReference("userRooms");
        userRoomsRef.child(userId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    DataSnapshot snapshot = task.getResult();


                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        String id = dataSnapshot.child("id").getValue(String.class);

                        deletedRooms.add(id);
                    }


                    deleteMemberFromRoom();

                    deleteUserRooms();

                }
            }
        });
    }

    public void deleteMemberFromRoom() {
        roomMembers = FirebaseDatabase.getInstance().getReference("roomMembers");
        for (int i = 0; i < deletedRooms.size(); i++) {


            roomMembers.child(deletedRooms.get(i)).child(userId).removeValue();
            decreaseRoomCapacity(deletedRooms.get(i));
        }
        //  deleteUserRooms();

    }

    public void printValues() {
        userRoomsRef = FirebaseDatabase.getInstance().getReference("roomMembers");
        userRoomsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String id = dataSnapshot.getValue(String.class);
                    // Toast.makeText(getApplicationContext(),roomModel.getId() , Toast.LENGTH_SHORT).show();
                    Log.e("deletedRoom", id);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public void deleteUserRooms() {
        userRoomsRef = FirebaseDatabase.getInstance().getReference("userRooms");
        userRoomsRef.child(user.getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    userRoomsRef = FirebaseDatabase.getInstance().getReference("users");
                    userRoomsRef.child(user.getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                //  Log.e("deletedRoom", "On Fire");
                                fAuth.signOut();
                                user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {

                                            Toast.makeText(SettingsActivity.this, "User Deleted Successfly", Toast.LENGTH_SHORT).show();
                                            progressBar.setVisibility(View.INVISIBLE);
                                            Intent intent = new Intent(getApplicationContext(), Welcome.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            finish();
                                            startActivity(intent);

                                        } else {
                                            //   Log.e("deletedRoom", "Fail");

                                            Toast.makeText(SettingsActivity.this, "Please Try Again !", Toast.LENGTH_SHORT).show();
                                            progressBar.setVisibility(View.INVISIBLE);
                                        }
                                    }
                                });


                            }
                        }
                    });
                }
            }
        });
    }

    public void showConfirmationDialoge() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.delete_dialoge, null);
        dialogeCancel = view.findViewById(R.id.deleteCancelButton);
        dialogeSure = view.findViewById(R.id.deletSureButton);
        builder.setView(view);
        final AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        dialogeCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        dialogeSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                progressBar.setVisibility(View.VISIBLE);
                deletImage();

                deletAccount();
                //  printValues();

            }
        });
    }

    private void decreaseRoomCapacity(String roomId) {
        DatabaseReference roomRef = FirebaseDatabase.getInstance().getReference("rooms");
        roomRef.child(roomId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    DataSnapshot snapshot = task.getResult();
                    String lastRoomCapcity = snapshot.child("roomCapacity").getValue(String.class);
                    int intRoomCapacity = Integer.parseInt(lastRoomCapcity);
                    if (intRoomCapacity == 1) {
                        roomRef.child(roomId).removeValue();
                    } else {
                        intRoomCapacity--;
                        roomRef.child(roomId).child("roomCapacity").setValue(String.valueOf(intRoomCapacity));

                    }
                }
            }
        });
    }

    public void deletImage() {
        imageRef = FirebaseStorage.getInstance().getReferenceFromUrl(String.valueOf(user.getPhotoUrl()));
        imageRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    //  Toast.makeText((Context) SettingsActivity.this, (CharSequence) user.getPhotoUrl(), Toast.LENGTH_SHORT).show();

                }
            }
        });
    }
}