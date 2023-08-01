package com.example.sharelocation.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.example.sharelocation.R;
import com.example.sharelocation.databinding.ActivityProfileBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;

public class ProfileActivity extends AppCompatActivity {
    private ActivityProfileBinding binding;
    private FirebaseAuth fAuth;
    private FirebaseUser user;
    private DatabaseReference userRef;
    private String userId;
    private UserProfileChangeRequest profile;
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile);

        setSupportActionBar(binding.profileToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();

        String memberName = intent.getStringExtra("memberName");
        getSupportActionBar().setTitle(memberName);
        userRef = FirebaseDatabase.getInstance().getReference("users");
        fAuth = FirebaseAuth.getInstance();
        user = fAuth.getCurrentUser();
        userId = user.getUid();


        refresh();
        binding.swipeToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
                binding.swipeToRefresh.setRefreshing(false);
            }
        });
        //   binding.swipeToRefresh.setEnabled(false);
        updatePasswardView();
        updateProfileName();
        updateProfilePhone();

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

    private void updateProfileName() {
        String lastName = String.valueOf(binding.name.getText());
        binding.editName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.editName.setVisibility(View.GONE);
                binding.confirmName.setVisibility(View.VISIBLE);
                binding.closeName.setVisibility(View.VISIBLE);
                binding.name.setEnabled(true);
                binding.name.requestFocus();
                binding.name.setSelection(binding.name.getText().length());
                binding.name.setShowSoftInputOnFocus(true);

                binding.closeName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        binding.editName.setVisibility(View.VISIBLE);
                        binding.confirmName.setVisibility(View.GONE);
                        binding.closeName.setVisibility(View.GONE);
                        binding.name.setEnabled(false);
                        refresh();
                    }
                });
                binding.confirmName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String newName = String.valueOf(binding.name.getText());
                        if (!lastName.equals(newName)) {
                            userRef.child(userId).child("name").setValue(newName).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    profile = new UserProfileChangeRequest.Builder().setDisplayName(newName).build();
                                    user.updateProfile(profile).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            binding.name.setEnabled(false);
                                            binding.editName.setVisibility(View.VISIBLE);
                                            binding.confirmName.setVisibility(View.GONE);
                                            binding.closeName.setVisibility(View.GONE);
                                            getSupportActionBar().setTitle(newName);
                                            refresh();
                                        }
                                    });

                                }
                            });
                        }
                    }
                });
            }
        });
    }


    private void updateProfilePhone() {
        String lasetPhoneNumber = String.valueOf(binding.phoneNumber.getText());
        binding.editPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.editPhone.setVisibility(View.GONE);
                binding.confirmPhone.setVisibility(View.VISIBLE);
                binding.closePhone.setVisibility(View.VISIBLE);
                binding.phoneNumber.setEnabled(true);
                binding.phoneNumber.requestFocus();
                binding.phoneNumber.setSelection(binding.phoneNumber.getText().length());
                binding.phoneNumber.setShowSoftInputOnFocus(true);

                binding.closePhone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        binding.editPhone.setVisibility(View.VISIBLE);
                        binding.confirmPhone.setVisibility(View.GONE);
                        binding.closePhone.setVisibility(View.GONE);
                        binding.phoneNumber.setEnabled(false);
                        refresh();
                    }
                });
                binding.confirmPhone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String newPhoneNumber = String.valueOf(binding.phoneNumber.getText());
                        if (!lasetPhoneNumber.equals(newPhoneNumber)) {
                            userRef.child(userId).child("phoneNumber").setValue(newPhoneNumber).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    binding.phoneNumber.setEnabled(false);
                                    binding.editPhone.setVisibility(View.VISIBLE);
                                    binding.confirmPhone.setVisibility(View.GONE);
                                    binding.closePhone.setVisibility(View.GONE);
                                    refresh();
                                }
                            });


                        }
                    }
                });
            }
        });
    }


    private void refresh() {

        userRef.child(userId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {

                    DataSnapshot snapshot = task.getResult();
                    String profilePhotoUri = snapshot.child("profilePhoto").getValue(String.class);
                    binding.name.setText(snapshot.child("name").getValue(String.class));
                    binding.email.setText(snapshot.child("email").getValue(String.class));
                    //  Log.e("profileName", String.valueOf(snapshot.getValue()));
                    binding.phoneNumber.setText("01000594861");
                    Glide.with(getApplicationContext()).load(profilePhotoUri).into(binding.profilePhoto);
                    binding.profilePhoto.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getApplicationContext(), FullscreenActivity.class);
                            intent.putExtra("imageUri", profilePhotoUri);
                            //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            // finish();
                            startActivity(intent);
                        }
                    });

                }
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


    public void loadImage(View view) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, 10);

    }

    public void camera(View view) {
        getPermession();
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 12);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 10) {
            if (data != null) {
                Uri uri = data.getData();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                    binding.profilePhoto.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else if (requestCode == 12) {
            bitmap = (Bitmap) data.getExtras().get("data");
            binding.profilePhoto.setImageBitmap(bitmap);
        }


        super.onActivityResult(requestCode, resultCode, data);
    }

    void getPermession() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(ProfileActivity.this, new String[]{Manifest.permission.CAMERA}, 11);

            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == 11) {
            if (grantResults.length > 0) {
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    getPermession();
                }
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

}