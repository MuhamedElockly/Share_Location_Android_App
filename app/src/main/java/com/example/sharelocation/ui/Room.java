package com.example.sharelocation.ui;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.example.sharelocation.R;
import com.example.sharelocation.databinding.ActivityRoomBinding;
import com.example.sharelocation.pojo.MemebrsModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class Room extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
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
    SwipeRefreshLayout swipeRefreshLayout;
    MemebrsModel deletedMember;
    private String roomAdmin = "";
    private FirebaseAuth fAuth;
    private String roomId;
    private Button invite;
    private Button cancel;
    private EditText memberEmailText;
    private String roomName = "";
    private DatabaseReference userRef;
    private ActionBarDrawerToggle drawerToggle;
    private DatabaseReference roomRef;
    private ArrayList<MemebrsModel> searchArryList;
    private FirebaseUser user;
    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            int posation = viewHolder.getAdapterPosition();
            if (searchArryList.size() != 0) {
                deletedMember = searchArryList.get(posation);
            } else {
                deletedMember = members.get(posation);
            }

            if (!user.getUid().equals(roomAdmin)) {
                return 0;
            } else if (deletedMember.getId().equals(user.getUid())) {
                return 0;
            }

            return super.getSwipeDirs(recyclerView, viewHolder);
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int posation = viewHolder.getAdapterPosition();

            if (searchArryList.size() != 0) {
                deletedMember = searchArryList.get(posation);
            } else {
                deletedMember = members.get(posation);
            }
            if (deletedMember.getId().equals(user.getUid())) {
                return;
            }
            String alertMessage = "This user w'll not find this room again !";
            String alertConfirmMessage = "Are you sure for deleting ?";
            showConfirmationDialoge(alertMessage, alertConfirmMessage, deletedMember.getId());
            Log.d("searchElement", deletedMember.getId());

            Snackbar.make((View) binding.memberRecyclerView, "Deleted", Snackbar.LENGTH_LONG).setAction("Undo Changes", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //  rooms.add(posation, deletedRoom);
                    //  roomAdapter.notifyDataSetChanged();
                }
            }).show();

        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)

                    .addSwipeRightBackgroundColor(ContextCompat.getColor(Room.this, R.color.red)).addSwipeRightActionIcon(R.drawable.delete_icon).addSwipeRightLabel("Delete").setSwipeRightLabelTextSize(TypedValue.COMPLEX_UNIT_SP, 15)

                    .setSwipeRightLabelColor(ContextCompat.getColor(Room.this, R.color.white)).create().decorate();

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }

    };
    private DatabaseReference rooms;

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        } else if (item.getItemId() == R.id.logOutPop) {
            FirebaseAuth fAuth = FirebaseAuth.getInstance();
            FirebaseUser user = fAuth.getCurrentUser();
            String alertMessage = "You w'll not find this room again !";
            String confirmMessage = "Are you sure for exite ?";
            showConfirmationLogOut(alertMessage, confirmMessage, user.getUid());

        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_room);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navView);

        Intent intent = getIntent();
        roomId = intent.getStringExtra("roomId");
        roomName = intent.getStringExtra("roomName");
        getSupportActionBar().setTitle(roomName);

        fAuth = FirebaseAuth.getInstance();
        user = fAuth.getCurrentUser();
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
        drawerLayout.setDrawerListener(drawerToggle);

        navigationView.setNavigationItemSelectedListener(this);
        navigationView.bringToFront();

        usersId = new ArrayList<>();
        members = new ArrayList<>();
        searchArryList = new ArrayList<>();
        recyclerView = binding.memberRecyclerView;

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        fAuth = FirebaseAuth.getInstance();
        user = fAuth.getCurrentUser();

        getRoomAdmin();
        //   membersAdapter = new MembersAdapter(this, members);
        // recyclerView.setAdapter(membersAdapter);


        refresh();

        binding.addMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  pushToFireBase();
                addMember();
            }
        });

        swipeRefreshLayout = findViewById(R.id.swipeToRefresh);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
                swipeRefreshLayout.setRefreshing(false);

            }
        });


        swipeRefreshLayout.setEnabled(false);

        new ItemTouchHelper(simpleCallback).attachToRecyclerView(binding.memberRecyclerView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.room_menu, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                search(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                search(newText);

                return false;
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                searchArryList.clear();

                membersAdapter = new MembersAdapter(Room.this, members, roomAdmin);
                // roomAdapter.notifyDataSetChanged();

                recyclerView.setAdapter(membersAdapter);
                refresh();
                return false;
            }
        });


        return true;
    }

    private void search(String token) {
        searchArryList.clear();

        for (int i = 0; i < members.size(); i++) {
            if (members.get(i).getName().contains(token)) {
                searchArryList.add(members.get(i));
                Log.d("searchElement", token);
            }


        }
        membersAdapter = new MembersAdapter(this, searchArryList, roomAdmin);
        // roomAdapter.notifyDataSetChanged();

        recyclerView.setAdapter(membersAdapter);
    }

    private void getUsers() {
        usersRef = FirebaseDatabase.getInstance().getReference("users");
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                members.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                    //   MemebrsModel memebrsModel = dataSnapshot.getValue(MemebrsModel.class);

                    String userId = dataSnapshot.child("userId").getValue(String.class);
                    String email = dataSnapshot.child("email").getValue(String.class);
                    String tokenId = dataSnapshot.child("tokenId").getValue(String.class);
                    String name = dataSnapshot.child("name").getValue(String.class);
                    String photoUri = dataSnapshot.child("profilePhoto").getValue(String.class);
                    MemebrsModel memebrsModel = new MemebrsModel(name, userId, tokenId, email, photoUri);

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

    private void increseRoomCapacity(String roomId) {
        roomRef = FirebaseDatabase.getInstance().getReference("rooms");
        roomRef.child(roomId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
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

    private void addMember() {


        final AlertDialog.Builder[] builder = {new AlertDialog.Builder(this)};
        View view = LayoutInflater.from(this).inflate(R.layout.add_member, null);
        invite = (Button) view.findViewById(R.id.invite);
        cancel = (Button) view.findViewById(R.id.cancel);
        memberEmailText = view.findViewById(R.id.dialogeMemberEmail);


        builder[0].setView(view);
        final AlertDialog dialog = builder[0].create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
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
                    usersRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            DataSnapshot snapshot = task.getResult();
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
                    });


                }
            }
        });
    }


    private void pushUserToFireBase(String userId) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        String id = database.push().getKey();
        //  RoomModel roomModel1 = new RoomModel(roomName, roomCapacity, id);


        // to add the current roomId for userRooms tree after getting the size
        database.child("userRooms").child(userId).child(roomId).child("id").setValue(roomId).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                // to add a new userId for roomMembers tree after getting the size
                database.child("roomMembers").child(roomId).child(userId).child("id").setValue(userId).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        increseRoomCapacity(roomId);
                        refresh();
                        Toast.makeText(Room.this, "Invitation was sent succesfly", Toast.LENGTH_SHORT).show();
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


                //   String name = ((DataSnapshot) (snapshot.getChildren())).child("id").getValue(String.class);
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String id = String.valueOf(dataSnapshot.child("id").getValue(String.class));
                    //  Log.d("children", roomId);
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

    private void getRoomAdmin() {
        rooms = FirebaseDatabase.getInstance().getReference("rooms");
        rooms.child(roomId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    DataSnapshot snapshot = task.getResult();
                    roomAdmin = snapshot.child("admin").getValue(String.class);
                    membersAdapter = new MembersAdapter(Room.this, members, roomAdmin);
                    recyclerView.setAdapter(membersAdapter);
                    if (user.getUid().equals(roomAdmin)) {
                        binding.addMember.setVisibility(View.VISIBLE);
                    } else {
                        binding.addMember.setVisibility(View.GONE);
                    }
                }
            }
        });
    }

    private void refresh() {
        binding.roomPBar.setVisibility(View.VISIBLE);
        usersId.clear();
        members.clear();
        // getRoomAdmin();
        roomMembersRef = FirebaseDatabase.getInstance().getReference("roomMembers");
        roomMembersRef.child(roomId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    // The data has been retrieved successfully
                    DataSnapshot snapshot = task.getResult();
                    //   String name = ((DataSnapshot) (snapshot.getChildren())).child("id").getValue(String.class);
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        String userId = String.valueOf(dataSnapshot.child("id").getValue(String.class));
                        //  Log.d("children", userId);
                        usersId.add(userId);
                    }
                    getUsers();
                    binding.roomPBar.setVisibility(View.GONE);
                }
            }
        });
        updateNavHeader();
    }

    private void refreshlast() {
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
        updateNavHeader();
    }

    public void updateNavHeader() {

        View headerView = navigationView.getHeaderView(0);

        ImageView headerImage = headerView.findViewById(R.id.headerProfilImage);
        TextView headerText = headerView.findViewById(R.id.headerProfileName);


        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference("users");

        userRef.child(uid).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    // The data has been retrieved successfully
                    DataSnapshot snapshot = task.getResult();
                    String name = snapshot.child("name").getValue(String.class);
                    String email = snapshot.child("email").getValue(String.class);
                    String photoUri = snapshot.child("profilePhoto").getValue(String.class);

                    //profileImageUri = photoUri;
                    Glide.with(getApplicationContext()).load(photoUri).into(headerImage);
                    headerText.setText(name);
                }
            }
        });

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        //   Toast.makeText(Home.this, "Drawer", Toast.LENGTH_SHORT).show();
        // printValues();
        int id = item.getItemId();
        if (id == R.id.logout) {
            logOut();
            // drawerLayout.closeDrawer(GravityCompat.START);
        } else if (id == R.id.settings) {
            Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
            // intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            //   finish();
            startActivity(intent);

        } else if (id == R.id.navHome) {
            Intent intent = new Intent(getApplicationContext(), Home.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            finish();
            startActivity(intent);
        } else if (id == R.id.navProfile) {
            Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
            intent.putExtra("memberName", (String) user.getDisplayName());
            // intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            //   finish();
            startActivity(intent);

        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void logOut() {
        fAuth = FirebaseAuth.getInstance();
        fAuth.signOut();
        Intent intent = new Intent(getApplicationContext(), Welcome.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        finish();
        startActivity(intent);
    }

    private void showConfirmationDialoge(String alertStringMessage, String confirmStringMessage, String userId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.delete_dialoge, null);
        TextView alertMessage = view.findViewById(R.id.bodyMessage1);
        TextView confirmMessage = view.findViewById(R.id.bodyMessage2);
        alertMessage.setText(alertStringMessage);
        confirmMessage.setText(confirmStringMessage);
        Button dialogeCancel = view.findViewById(R.id.deleteCancelButton);
        Button dialogeSure = view.findViewById(R.id.deletSureButton);
        builder.setView(view);
        final AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        dialogeCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();

                membersAdapter.notifyDataSetChanged();
            }
        });
        dialogeSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                deleteUserFromRoom(userId);
                searchArryList.clear();
            }
        });

    }


    private void showConfirmationLogOut(String alertStringMessage, String confirmStringMessage, String userId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.delete_dialoge, null);
        TextView alertMessage = view.findViewById(R.id.bodyMessage1);
        TextView confirmMessage = view.findViewById(R.id.bodyMessage2);
        alertMessage.setText(alertStringMessage);
        confirmMessage.setText(confirmStringMessage);
        Button dialogeCancel = view.findViewById(R.id.deleteCancelButton);
        Button dialogeSure = view.findViewById(R.id.deletSureButton);
        builder.setView(view);
        final AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        dialogeCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                membersAdapter.notifyDataSetChanged();
            }
        });
        dialogeSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                logOutFromRoom(userId);
                searchArryList.clear();
            }
        });

    }

    private void logOutFromRoom(String userId) {
        FirebaseAuth fAuth = FirebaseAuth.getInstance();
        FirebaseUser user = fAuth.getCurrentUser();
        // String userId = user.getUid();
        roomMembersRef = FirebaseDatabase.getInstance().getReference("roomMembers");
        DatabaseReference userRoomsRef = FirebaseDatabase.getInstance().getReference("userRooms");
        userRoomsRef.child(userId).child(roomId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    roomMembersRef.child(roomId).child(userId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                roomMembersRef.child(roomId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            //   String roomAdmin = "";
                                            DataSnapshot roomSnapshot = task.getResult();
                                            //  roomAdmin = roomSnapshot.child("admin").getValue(String.class);

                                            if (user.getUid().equals(roomAdmin)) {
                                                String newAdminId = "empty";
                                                if (roomSnapshot.hasChildren()) {
                                                    for (DataSnapshot dataSnapshot : roomSnapshot.getChildren()) {
                                                        newAdminId = String.valueOf(dataSnapshot.child("id").getValue(String.class));
                                                        if (!newAdminId.equals("empty")) {
                                                            Log.e("newAdmin",newAdminId);
                                                            roomRef=FirebaseDatabase.getInstance().getReference("rooms");
                                                            roomRef.child(roomId).child("admin").setValue(newAdminId);
                                                        }
                                                        break;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                });
                                decreaseRoomCapacityforLogOut(roomId);
                            }
                        }
                    });
                }
            }
        });
    }


    private void deleteUserFromRoom(String userId) {
        FirebaseAuth fAuth = FirebaseAuth.getInstance();
        FirebaseUser user = fAuth.getCurrentUser();
        // String userId = user.getUid();
        roomMembersRef = FirebaseDatabase.getInstance().getReference("roomMembers");
        DatabaseReference userRoomsRef = FirebaseDatabase.getInstance().getReference("userRooms");
        userRoomsRef.child(userId).child(roomId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                roomMembersRef.child(roomId).child(userId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        decreaseRoomCapacity(roomId);
                    }
                });
            }
        });
    }

    private void decreaseRoomCapacityforLogOut(String roomId) {
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
                    Intent intent = new Intent(getApplicationContext(), Home.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    finish();
                    startActivity(intent);
                }
                //  refresh();
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
                        Intent intent = new Intent(getApplicationContext(), Home.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        finish();
                        startActivity(intent);
                    } else {
                        intRoomCapacity--;
                        roomRef.child(roomId).child("roomCapacity").setValue(String.valueOf(intRoomCapacity));
                    }
                }
                refresh();
            }
        });
    }
}



