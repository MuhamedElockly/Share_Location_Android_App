package com.example.sharelocation.ui;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.sharelocation.R;
import com.example.sharelocation.databinding.ActivityWelcomeBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;

public class Welcome extends AppCompatActivity {
    ActivityWelcomeBinding binding;
    DatabaseReference database;
    private FirebaseAuth fAuth;
    private FirebaseUser user;
    private DatabaseReference roomRef;
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_welcome);
        fAuth = FirebaseAuth.getInstance();
        user = fAuth.getCurrentUser();
        binding.login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LogIn.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                finish();
                startActivity(intent);
            }
        });
        binding.signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SignUp.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                finish();
                startActivity(intent);
            }
        });


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        database = FirebaseDatabase.getInstance().getReference("roomMembers");
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (user != null) {
            recieveLink();

        }
    }

    private void openNewRoom(String roomId, String roomName) {
        Intent intent = new Intent(Welcome.this, Room.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        //     ((Home) context).finish();
        intent.putExtra("roomId", (String) roomId);
        intent.putExtra("roomName", roomName);
        startActivity(intent);
    }

    private void recieveLink() {

        FirebaseDynamicLinks.getInstance().getDynamicLink(getIntent()).addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
            @Override
            public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                // Get deep link from result (may be null if no link is found)
                Uri deepLink = null;
                if (pendingDynamicLinkData != null) {
                    showProgreesBar();
                    deepLink = pendingDynamicLinkData.getLink();

                    Log.d("recivedLink", deepLink.toString());

                    String referedLink = deepLink.toString();
                    try {
                        referedLink = referedLink.substring(referedLink.lastIndexOf("=") + 1);

                        String roomId = referedLink.substring(0, referedLink.indexOf("@"));
                        String roomName = referedLink.substring(referedLink.lastIndexOf("@") + 1);

                        database.child(roomId).orderByChild("id").equalTo(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    dialog.cancel();
                                    openNewRoom(roomId, roomName);
                                } else {
                                    pushUserToFireBase(roomId, roomName);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                dialog.cancel();
                                Intent intent = new Intent(getApplicationContext(), Home.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                finish();
                                startActivity(intent);
                            }
                        });


                        Log.d("roomId", referedLink);

                    } catch (Exception e) {

                    }
                } else {
                    Intent intent = new Intent(getApplicationContext(), Home.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    finish();
                    startActivity(intent);
                }

            }
        }).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String TAG = "splash";
                Log.e(TAG, "getDynamicLink:onFailure", e);
            }
        });
    }

    private void pushUserToFireBase(String roomId, String roomName) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        String id = database.push().getKey();
        //  RoomModel roomModel1 = new RoomModel(roomName, roomCapacity, id);


        // to add the current roomId for userRooms tree after getting the size
        database.child("userRooms").child(user.getUid()).child(roomId).child("id").setValue(roomId).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                // to add a new userId for roomMembers tree after getting the size
                database.child("roomMembers").child(roomId).child(user.getUid()).child("id").setValue(user.getUid()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        increseRoomCapacity(roomId);
                        //   refresh();
                        openNewRoom(roomId, roomName);
                        Toast.makeText(Welcome.this, "Invitation accepted succesfly", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

        //  Toast.makeText(getApplicationContext(), id, Toast.LENGTH_SHORT).show();
    }

    private void increseRoomCapacity(String roomId) {
        roomRef = FirebaseDatabase.getInstance().getReference("rooms");
        roomRef.child(roomId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                dialog.cancel();
                if (task.isSuccessful()) {
                    DataSnapshot snapshot = task.getResult();
                    String lastRoomCapcity = snapshot.child("roomCapacity").getValue(String.class);
                    int newRoomCapacity = Integer.parseInt(lastRoomCapcity);
                    newRoomCapacity++;
                    roomRef.child(roomId).child("roomCapacity").setValue(String.valueOf(newRoomCapacity));
                }
            }
        });
    }

    private void showProgreesBar() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.load_dialoge, null);
        ProgressBar progressBar = view.findViewById(R.id.profilePbar);

        builder.setView(view);
        dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();


    }


}