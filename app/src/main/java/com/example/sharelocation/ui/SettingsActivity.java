package com.example.sharelocation.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sharelocation.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        toolbar = findViewById(R.id.settingToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Settings");
        fAuth = FirebaseAuth.getInstance();
        user = fAuth.getCurrentUser();
        deletedRooms = new ArrayList<>();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        deleteButton = findViewById(R.id.deletAccount);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deletImage();

                deletAccount();
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

    public void deletAccount() {
        userRoomsRef = FirebaseDatabase.getInstance().getReference("userRooms");
        userRoomsRef.child(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    DataSnapshot snapshot = task.getResult();

                    int i = 0;
                    for (int j = 0; j < snapshot.getChildrenCount(); j++) {
                        i++;
                        String id = snapshot.child(String.valueOf(i)).getValue(String.class);
                        //  Toast.makeText(getApplicationContext(),id , Toast.LENGTH_SHORT).show();
                        deletedRooms.add(id);

                    }
                    deleteMemberFromRoom();
                }
            }
        });
    }

    public void deleteMemberFromRoom() {
        roomMembers = FirebaseDatabase.getInstance().getReference("roomMembers");
        for (int i = 0; i < deletedRooms.size(); i++) {
            int finalI = i;

            roomMembers.child(deletedRooms.get(i)).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (task.isSuccessful()) {
                        DataSnapshot snapshot = task.getResult();
                        for (int j = 0; j < snapshot.getChildrenCount(); j++) {
                            String id = snapshot.child(String.valueOf(j + 1)).getValue(String.class);
                            String key = snapshot.getKey();
                            if (user.getUid().equals(id)) {
                                // Log.e("deletedRoom", id);
                                int finalJ = j;
                                int finalJ1 = j;
                                roomMembers.child(deletedRooms.get(finalI)).child(String.valueOf((j + 1))).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            roomdeleted++;

                                            for (int n = finalJ1; n < snapshot.getChildrenCount(); n++) {
                                                String nextItem = snapshot.child(String.valueOf(n + 2)).getValue(String.class);
                                                roomMembers.child(deletedRooms.get(finalI)).child(String.valueOf((n + 1))).setValue(nextItem);
                                            }
                                            Log.e("deletedRoom", id + "      :      " + (snapshot.getChildrenCount()));
                                            roomMembers.child(deletedRooms.get(finalI)).child(String.valueOf((snapshot.getChildrenCount()-1))).removeValue();

                                        }
                                    }
                                });
                                break;
                            }
                        }
                    }
                }
            });
        }
        deleteUserRooms();

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
                                /*
                                user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(SettingsActivity.this, "User Deleted Successfly", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                                 */
                            }
                        }
                    });
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