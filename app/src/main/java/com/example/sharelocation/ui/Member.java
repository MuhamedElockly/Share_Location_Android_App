package com.example.sharelocation.ui;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.example.sharelocation.R;
import com.example.sharelocation.databinding.ActivityMemberBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Member extends AppCompatActivity {
    ActivityMemberBinding binding;
    DatabaseReference memberRef;
    Toolbar toolbar;
    SwipeRefreshLayout swipeRefreshLayout;
    boolean isImageFitToScreen = false;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        binding = DataBindingUtil.setContentView(this, R.layout.activity_member);

        Intent intent = getIntent();
        userId = intent.getStringExtra("userId");
        Toast.makeText(this, userId, Toast.LENGTH_SHORT).show();
        refresh();

        swipeRefreshLayout = binding.swipeToRefresh;
        swipeRefreshLayout.setEnabled(false);
        // binding.memberProfileImage.setScaleType(ImageView.ScaleType.FIT_XY);

        binding.memberProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), FullscreenActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                // finish();
                startActivity(intent);

 /*

                if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH ){
                    binding.memberProfileImage.setSystemUiVisibility( View.SYSTEM_UI_FLAG_HIDE_NAVIGATION );

                }
                else if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB )
                    binding.memberProfileImage.setSystemUiVisibility( View.STATUS_BAR_HIDDEN );
                else{}

                // binding.memberProfileImage.setScaleType(ImageView.ScaleType.FIT_START);

                if (isImageFitToScreen) {
                    isImageFitToScreen = false;
                    binding.memberProfileImage.setLayoutParams(new FrameLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                    binding.memberProfileImage.setAdjustViewBounds(true);
                } else {
                    isImageFitToScreen = true;
                    // binding.memberProfileImage.setLayoutParams(new FrameLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                    //binding.memberProfileImage.setScaleType(ImageView.ScaleType.FIT_XY);
                }

                 */
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.room_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    public void fullScreen() {

        // BEGIN_INCLUDE (get_current_ui_flags)
        // The UI options currently enabled are represented by a bitfield.
        // getSystemUiVisibility() gives us that bitfield.
        int uiOptions = getWindow().getDecorView().getSystemUiVisibility();
        int newUiOptions = uiOptions;
        // END_INCLUDE (get_current_ui_flags)
        // BEGIN_INCLUDE (toggle_ui_flags)
        boolean isImmersiveModeEnabled = ((uiOptions | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY) == uiOptions);
        if (isImmersiveModeEnabled) {
            //  Log.i(TAG, "Turning immersive mode mode off. ");
        } else {
            //Log.i(TAG, "Turning immersive mode mode on.");
        }

        // Navigation bar hiding:  Backwards compatible to ICS.
        if (Build.VERSION.SDK_INT >= 14) {
            newUiOptions ^= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        }

        // Status bar hiding: Backwards compatible to Jellybean
        if (Build.VERSION.SDK_INT >= 16) {
            newUiOptions ^= View.SYSTEM_UI_FLAG_FULLSCREEN;
        }

        // Immersive mode: Backward compatible to KitKat.
        // Note that this flag doesn't do anything by itself, it only augments the behavior
        // of HIDE_NAVIGATION and FLAG_FULLSCREEN.  For the purposes of this sample
        // all three flags are being toggled together.
        // Note that there are two immersive mode UI flags, one of which is referred to as "sticky".
        // Sticky immersive mode differs in that it makes the navigation and status bars
        // semi-transparent, and the UI flag does not get cleared when the user interacts with
        // the screen.
        if (Build.VERSION.SDK_INT >= 18) {
            newUiOptions ^= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        }

        getWindow().getDecorView().setSystemUiVisibility(newUiOptions);
        //END_INCLUDE (set_ui_flags)
    }

    private void refresh() {


        memberRef = FirebaseDatabase.getInstance().getReference("users");
        memberRef.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = snapshot.child("name").getValue(String.class);
                String photoUri = snapshot.child("profilePhoto").getValue(String.class);
                Glide.with(getApplicationContext()).load(photoUri).into(binding.memberProfileImage);
                binding.profileName.setText(name);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}