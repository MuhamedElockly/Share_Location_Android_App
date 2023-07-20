package com.example.sharelocation.ui;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sharelocation.R;
import com.example.sharelocation.databinding.ActivityRoomBinding;
import com.example.sharelocation.pojo.MemebrsModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Room extends AppCompatActivity {
    Toolbar toolbar;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActivityRoomBinding binding;
    RecyclerView recyclerView;
    DatabaseReference usersRef;
    DatabaseReference roomMembersRef;
    MembersAdapter membersAdapter;
    ArrayList<String> usersId;
    ArrayList<MemebrsModel> members;
    private FirebaseAuth fAuth;
    private String roomId;
    private Button invite;
    private Button cancel;
    private EditText memberEmailText;
    private String roomName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_room);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navView);

        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        usersId = new ArrayList<>();
        members = new ArrayList<>();

        recyclerView = binding.memberRecyclerView;

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        membersAdapter = new MembersAdapter(this, members);
        recyclerView.setAdapter(membersAdapter);

        Intent intent = getIntent();
        roomId = intent.getStringExtra("roomId");
        roomName = intent.getStringExtra("roomName");
        getSupportActionBar().setTitle(roomName);
        refresh();

        binding.addMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  pushToFireBase();
                addMember();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.room_menu, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                return false;
            }
        });
        return true;
    }

    private void pushToFireBase() {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        String id = database.push().getKey();
        MemebrsModel memebrsModel = new MemebrsModel("Mohamed", id, " ", " ");
        database.child("members").child(id).setValue(memebrsModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Added Successfly", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getUsers() {
        usersRef = FirebaseDatabase.getInstance().getReference("users");
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                    //   MemebrsModel memebrsModel = dataSnapshot.getValue(MemebrsModel.class);

                    String userId = dataSnapshot.child("userId").getValue(String.class);
                    String email = dataSnapshot.child("email").getValue(String.class);
                    String tokenId = dataSnapshot.child("tokenId").getValue(String.class);
                    String name = dataSnapshot.child("name").getValue(String.class);
                    MemebrsModel memebrsModel = new MemebrsModel(name, userId, tokenId, email);

                    for (int i = 0; i < usersId.size(); i++) {

                        if (memebrsModel.getId().equals(usersId.get(i))) {
                            // Toast.makeText(getApplicationContext(), usersId.get(0), Toast.LENGTH_SHORT).show();
                            members.add(memebrsModel);
                        }
                    }
                }
                membersAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void addMember() {


        final AlertDialog.Builder[] builder = {new AlertDialog.Builder(this)};
        View view = LayoutInflater.from(this).inflate(R.layout.add_member, null);
        invite = (Button) view.findViewById(R.id.invite);
        cancel = (Button) view.findViewById(R.id.cancel);
        memberEmailText = view.findViewById(R.id.dialogeMemberEmail);


        builder[0].setView(view);
        final AlertDialog dialog = builder[0].create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.LTGRAY));
        dialog.show();
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        invite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String memberEmail = memberEmailText.getText().toString();
                if (memberEmail.isEmpty()) {
                    memberEmailText.setError("This Field Is Required");
                    memberEmailText.requestFocus();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(memberEmail).matches()) {
                    memberEmailText.setError("Email Is Not Falid");
                    memberEmailText.requestFocus();
                } else {
                    // pushToFireBase(roomName, roomCapacity);

                    usersRef = FirebaseDatabase.getInstance().getReference("users");
                    usersRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            boolean emailExist = false;
                            String userId = null;
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                String email = dataSnapshot.child("email").getValue(String.class);

                                if (email.equals(memberEmail)) {
                                    userId = dataSnapshot.child("userId").getValue(String.class);
                                    emailExist = true;
                                    break;
                                }
                            }
                            if (emailExist) {
                                checkUserExistence(userId, dialog);
                            } else {
                                //  Toast.makeText(Room.this, email, Toast.LENGTH_SHORT).show();
                                memberEmailText.setError("Email Is Not Found");
                                memberEmailText.requestFocus();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });


                }
            }
        });
    }


    private void pushUserToFireBase(String userId) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        String id = database.push().getKey();
        //  RoomModel roomModel1 = new RoomModel(roomName, roomCapacity, id);


        database.child("userRooms").child(userId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                DataSnapshot snapshot = task.getResult();
                // To get the size of rooms for a specefic user in userRooms tree
                int i = 1;
                for (int j = 0; j < snapshot.getChildrenCount(); j++) {
                    i++;
                }
                // to add the current roomId for userRooms tree after getting the size
                database.child("userRooms").child(userId).child(String.valueOf(i)).setValue(roomId).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        database.child("roomMembers").child(roomId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DataSnapshot> task) {
                                DataSnapshot snapshot = task.getResult();
                                // To get the size of member for a specefic room in roomMembers tree
                                int i = 1;
                                for (int j = 0; j < snapshot.getChildrenCount(); j++) {
                                    i++;
                                }
                                // to add a new userId for roomMembers tree after getting the size
                                database.child("roomMembers").child(roomId).child(String.valueOf(i)).setValue(userId).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        refresh();
                                        Toast.makeText(Room.this, "Invitation was sent succesfly", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });
                    }
                });
            }
        });
        //  Toast.makeText(getApplicationContext(), id, Toast.LENGTH_SHORT).show();
    }

    private void checkUserExistence(String userId, AlertDialog dialog) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        database.child("roomMembers").child(roomId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                DataSnapshot snapshot = task.getResult();
                boolean userExist = false;
                for (int j = 1; j <= snapshot.getChildrenCount(); j++) {
                    String id = snapshot.child(String.valueOf(j)).getValue(String.class);
                    if (userId.equals(id)) {
                        userExist = true;
                        break;
                    }

                }
                if (userExist) {
                    Toast.makeText(Room.this, "User already exist !", Toast.LENGTH_SHORT).show();
                } else {
                    pushUserToFireBase(userId);
                    dialog.cancel();
                }
            }
        });

    }

    private void refresh() {
        binding.roomPBar.setVisibility(View.VISIBLE);
        usersId.clear();
        members.clear();
        roomMembersRef = FirebaseDatabase.getInstance().getReference("roomMembers");
        roomMembersRef.child(roomId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    // The data has been retrieved successfully
                    DataSnapshot snapshot = task.getResult();
                    String name = snapshot.child("1").getValue(String.class);
                    String email = snapshot.child("email").getValue(String.class);
                    // Toast.makeText(getApplicationContext(),name, Toast.LENGTH_SHORT).show();
                    int i = 0;
                    for (int j = 0; j < snapshot.getChildrenCount(); j++) {
                        i++;
                        String id = snapshot.child(String.valueOf(i)).getValue(String.class);
                        usersId.add(id);
                    }
                    getUsers();
                    binding.roomPBar.setVisibility(View.GONE);
                }
            }
        });
    }
}



