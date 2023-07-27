package com.example.sharelocation.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
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
import androidx.core.app.ActivityCompat;
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
import com.example.sharelocation.databinding.ActivityHomeBinding;
import com.example.sharelocation.pojo.RoomModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

public class Home extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private final static int REQUSTE_CODE = 100;
    RoomModel deletedRoom;
    Toolbar toolbar;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActivityHomeBinding binding;
    RecyclerView recyclerView;
    ArrayList<RoomModel> rooms;
    DatabaseReference userRoomsRef;
    DatabaseReference userRef;
    DatabaseReference roomsRef;
    RoomAdapter roomAdapter;
    ArrayList<String> userRooms;
    EditText roomName;
    EditText numberOfMembers;
    SwipeRefreshLayout swipeRefreshLayout;
    ActionBarDrawerToggle drawerToggle;
    FusedLocationProviderClient fusedLocationProviderClient;
    ArrayList<RoomModel> searchArryList;
    private EditText roomNameText;
    private EditText roomCapacityText;
    private Button apply;
    private Button cancel;
    private FirebaseAuth fAuth;
    private FirebaseUser user;
    private LinearLayoutManager mLayoutManager;
    private ImageView headerImage;
    private TextView headerText;
    private String profileImageUri;
    private DatabaseReference roomMembersRef;
    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int posation = viewHolder.getAdapterPosition();

            if (searchArryList.size() != 0) {
                deletedRoom = searchArryList.get(posation);
            } else {
                deletedRoom = rooms.get(posation);
            }

            //  deletedRoom = rooms.get(posation);
            //    rooms.remove(posation);
            //  roomAdapter.notifyDataSetChanged();
            // deleteUserFromRoom(deletedRoom.getId());
            String alertMessage = "You w'll not be able to find this room again !";
            showConfirmationDialoge(alertMessage, deletedRoom.getId());


            Snackbar.make((View) binding.roomRecyclerView, "Deleted", Snackbar.LENGTH_LONG).setAction("Undo Changes", new View.OnClickListener() {
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

                    .addSwipeRightBackgroundColor(ContextCompat.getColor(Home.this, R.color.red)).addSwipeRightActionIcon(R.drawable.delete_icon).addSwipeRightLabel("Delete").setSwipeRightLabelTextSize(TypedValue.COMPLEX_UNIT_SP, 15)

                    .setSwipeRightLabelColor(ContextCompat.getColor(Home.this, R.color.white)).create().decorate();

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_home);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navView);

        fAuth = FirebaseAuth.getInstance();
        user = fAuth.getCurrentUser();
        recyclerView = binding.roomRecyclerView;
        userRoomsRef = FirebaseDatabase.getInstance().getReference("userRooms");
        rooms = new ArrayList<>();
        userRooms = new ArrayList<>();
        searchArryList = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        roomAdapter = new RoomAdapter(this, rooms);
        recyclerView.setAdapter(roomAdapter);


     //   refresh();
         checkRooms();

        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
        drawerLayout.setDrawerListener(drawerToggle);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.bringToFront();


        // refresh();
        binding.addRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //    logOut();
                addRoom();
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


   /*
        swipeRefreshLayout.setOnChildScrollUpCallback(new SwipeRefreshLayout.OnChildScrollUpCallback() {
            private static final String TAG = "scroll";
            @Override
            public boolean canChildScrollUp(SwipeRefreshLayout parent, View child) {
                if (recyclerView != null) {
                    Log.e(TAG, "Scroll : nottttt " );
                    return recyclerView.canScrollVertically(-1);
                } return false;
            }
        });

         */

        //
        //  Toast.makeText(this, scroll+"", Toast.LENGTH_SHORT).show();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        getLastLocation();
        new ItemTouchHelper(simpleCallback).attachToRecyclerView(binding.roomRecyclerView);


    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu, menu);
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

                roomAdapter = new RoomAdapter(Home.this, rooms);
                // roomAdapter.notifyDataSetChanged();

                recyclerView.setAdapter(roomAdapter);
                refresh();
                return false;
            }
        });
        return true;
    }

    private void search(String token) {

        searchArryList.clear();
        for (int i = 0; i < rooms.size(); i++) {
            if (rooms.get(i).getName().contains(token)) {
                searchArryList.add(rooms.get(i));
                Log.d("searchElement", token);
            }


        }
        roomAdapter = new RoomAdapter(this, searchArryList);
        // roomAdapter.notifyDataSetChanged();

        recyclerView.setAdapter(roomAdapter);
        /*
        FirebaseRecyclerOptions<RoomModel> options = new FirebaseRecyclerOptions.Builder<RoomModel>()
                .setQuery(FirebaseDatabase.getInstance().getReference().child("rooms").orderByKey()
                        .orderByChild("name").startAt(token).endAt(token + "\uf8ff"), RoomModel.class).build();



        // Convert the FirebaseRecyclerOptions object to an Object array.
        Object[] objects = options.getSnapshots().toArray();


        // Add the objects from the Object array to the ArrayList object.
        for (Object object : objects) {
            RoomModel room = (RoomModel) object;
            firebaseArrayList.add(room);
        }

        Log.d("searchElement", String.valueOf(options.getSnapshots().size()));


        roomAdapter = new RoomAdapter(this, firebaseArrayList);
        roomAdapter.notifyDataSetChanged();


         */
        //   rooms.clear();
        // rooms.addAll(options);
        //  roomAdapter.notifyDataSetChanged();
        // firebaseList.addAll(options);
        //   roomAdapter=new RoomAdapter(this,options);
    }

    public void printValues() {
        userRoomsRef = FirebaseDatabase.getInstance().getReference("roomMembers");
        userRoomsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    ArrayList<String> temp = new ArrayList<>();
                    String id;
                    id = dataSnapshot.child("1").getValue(String.class);
                    // Toast.makeText(getApplicationContext(),roomModel.getId() , Toast.LENGTH_SHORT).show();
                    Log.e("deletedRoom", id);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void pushToFireBase2(String roomName, String roomCapacity) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        String id = database.push().getKey();
        String roomMembers = "1";
        RoomModel roomModel1 = new RoomModel(roomName, roomCapacity, id, roomMembers, user.getUid());
        String userId = fAuth.getCurrentUser().getUid();
        database.child("rooms").child(id).setValue(roomModel1).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    // to add a new roomId for userRooms tree after getting the size
                    database.child("userRooms").child(userId).child(id).child("id").setValue(id).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            // to add a new userId for roomMembers tree after getting the size
                            database.child("roomMembers").child(id).child(userId).child("id").setValue(userId).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    //   refresh();
                                    refresh();
                                    Toast.makeText(getApplicationContext(), "Room Added Succefly", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                }
            }

        });
    }

    private void pushToFireBase(String roomName, String roomCapacity) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        String id = database.push().getKey();
        String roomMembers = "1";
        RoomModel roomModel1 = new RoomModel(roomName, roomCapacity, id, roomMembers, user.getUid());
        String userId = fAuth.getCurrentUser().getUid();
        database.child("rooms").child(id).setValue(roomModel1).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    database.child("userRooms").child(userId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            DataSnapshot snapshot = task.getResult();
                            // To get the size of rooms for a specefic user in userRooms tree
                            int i = 1;
                            for (int j = 0; j < snapshot.getChildrenCount(); j++) {
                                i++;
                            }
                            // to add a new roomId for userRooms tree after getting the size
                            database.child("userRooms").child(userId).child(String.valueOf(i)).setValue(id).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    database.child("roomMembers").child(id).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                                            DataSnapshot snapshot = task.getResult();
                                            // To get the size of member for a specefic room in roomMembers tree
                                            int i = 1;
                                            for (int j = 0; j < snapshot.getChildrenCount(); j++) {
                                                i++;
                                            }
                                            // to add a new userId for roomMembers tree after getting the size
                                            database.child("roomMembers").child(id).child(String.valueOf(i)).setValue(userId).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {

                                                    //refresh();
                                                    refresh();
                                                    Toast.makeText(getApplicationContext(), "Room Added Succefly", Toast.LENGTH_SHORT).show();
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
            }
        });
    }

    private void getRooms2() {
        roomsRef = FirebaseDatabase.getInstance().getReference("rooms");
        roomsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    RoomModel roomModel = dataSnapshot.getValue(RoomModel.class);
                    // Toast.makeText(getApplicationContext(),roomModel.getId() , Toast.LENGTH_SHORT).show();
                    for (int i = 0; i < userRooms.size(); i++) {
                        if (roomModel.getId().equals(userRooms.get(i))) {
                            rooms.add(roomModel);
                        }
                    }
                }
                roomAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void getRooms() {

        roomsRef = FirebaseDatabase.getInstance().getReference("rooms");
        roomsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                rooms.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                    RoomModel roomModel = dataSnapshot.getValue(RoomModel.class);
                    // Toast.makeText(getApplicationContext(),roomModel.getId() , Toast.LENGTH_SHORT).show();
                    for (int i = 0; i < userRooms.size(); i++) {
                        if (roomModel.getId().equals(userRooms.get(i))) {
                            rooms.add(roomModel);
                        }
                    }
                }
                roomAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }

    private void logOut() {
        fAuth.signOut();
        Intent intent = new Intent(getApplicationContext(), Welcome.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        finish();
        startActivity(intent);
    }

    private void addRoom() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.add_room, null);
        apply = (Button) view.findViewById(R.id.apply);
        cancel = (Button) view.findViewById(R.id.cancel);
        roomNameText = view.findViewById(R.id.dialogeRoomName);
        roomCapacityText = view.findViewById(R.id.roomCapacity);
        builder.setView(view);
        final AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String roomName = roomNameText.getText().toString();
                String roomCapacity = roomCapacityText.getText().toString();
                if (roomName.isEmpty()) {
                    roomNameText.setError("This Field Is Required");
                    roomNameText.requestFocus();
                } else if (roomCapacity.isEmpty()) {
                    roomCapacityText.setError("This Field Is Required");
                    roomCapacityText.requestFocus();
                } else {
                    //  pushToFireBase(roomName, roomCapacity);
                    pushToFireBase2(roomName, roomCapacity);
                    dialog.cancel();
                }
            }
        });
    }


    public void updateNavHeader() {

        View headerView = navigationView.getHeaderView(0);

        headerImage = headerView.findViewById(R.id.headerProfilImage);
        headerText = headerView.findViewById(R.id.headerProfileName);


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

    public void checkRooms() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userRoomsRef.child(uid).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    DataSnapshot snapshot = task.getResult();
                    if (snapshot.hasChildren()) {
                        //   Toast.makeText(Home.this, "Yeeeeeess", Toast.LENGTH_SHORT).show();
                        // refresh();
                        refresh();
                    } else {
                        updateNavHeader();
                    }
                }
            }
        });

    }

    public void refresh() {
        // updateNavHeader();
        binding.homePBar.setVisibility(View.VISIBLE);
        rooms.clear();
        userRooms.clear();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        userRoomsRef = FirebaseDatabase.getInstance().getReference("userRooms");
        userRoomsRef.child(uid).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    // The data has been retrieved successfully
                    DataSnapshot snapshot = task.getResult();
                    //   String name = ((DataSnapshot) (snapshot.getChildren())).child("id").getValue(String.class);
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        String roomId = String.valueOf(dataSnapshot.child("id").getValue(String.class));
                        //  Log.d("children", roomId);
                        userRooms.add(roomId);
                    }
                    getRooms();
                    binding.homePBar.setVisibility(View.GONE);
                }
            }
        });

        updateNavHeader();
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.logout) {
            logOut();
            // drawerLayout.closeDrawer(GravityCompat.START);
        } else if (id == R.id.settings) {
            Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
            // intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            //   finish();
            startActivity(intent);

        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void getLoaction() {

    }

    private void getLastLocation() {
        // Toast.makeText(Home.this, "firist", Toast.LENGTH_SHORT).show();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {

                        //    Toast.makeText(Home.this, location.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            askLoscationPermesstion();
        }
    }

    private void askLoscationPermesstion() {
        ActivityCompat.requestPermissions(Home.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUSTE_CODE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == REQUSTE_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            } else {
                Toast.makeText(this, "Required Permission", Toast.LENGTH_SHORT).show();
            }
        }


        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void deleteUserFromRoom(String roomId) {

        String userId = user.getUid();
        roomMembersRef = FirebaseDatabase.getInstance().getReference("roomMembers");
        userRoomsRef = FirebaseDatabase.getInstance().getReference("userRooms");

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
                refresh();
            }
        });
    }

    private void showConfirmationDialoge(String message, String roomId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.delete_dialoge, null);
        TextView bodyMessage = view.findViewById(R.id.bodyMessage1);
        bodyMessage.setText(message);
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
                roomAdapter.notifyDataSetChanged();
            }
        });
        dialogeSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                deleteUserFromRoom(deletedRoom.getId());
                searchArryList.clear();
            }
        });

    }
}