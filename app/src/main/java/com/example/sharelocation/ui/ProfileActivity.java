package com.example.sharelocation.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.example.sharelocation.R;
import com.example.sharelocation.databinding.ActivityProfileBinding;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class ProfileActivity extends AppCompatActivity {
    AlertDialog dialog;
    private ActivityProfileBinding binding;
    private FirebaseAuth fAuth;
    private FirebaseUser user;
    private DatabaseReference userRef;
    private String userId;
    private UserProfileChangeRequest profile;
    private Bitmap bitmap;
    private StorageReference imageRef;
    private String profileEmail;
    private String fireBaseImageUri;

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
        imagePicker();
        updatPassward();
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
                binding.currentPassward.getText().clear();
                binding.newPassward.getText().clear();
                binding.reNewPassward.getText().clear();
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

    private void imagePicker() {
        binding.editProfilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.with(ProfileActivity.this).crop()                    //Crop image(Optional), Check Customization for more option
                        .compress(1024)            //Final image size will be less than 1 MB(Optional)
                        .maxResultSize(1080, 1080)    //Final image resolution will be less than 1080 x 1080(Optional)
                        .start();

            }
        });
    }

    private void refresh() {

        userRef.child(userId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {

                    DataSnapshot snapshot = task.getResult();
                    ProfileActivity.this.profileEmail = snapshot.child("email").getValue(String.class);
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

    private void updatPassward() {
        binding.confirmPassward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmPasswordToFirebase();
            }
        });
    }

    private void confirmPasswordToFirebase() {
        showProgreesBar();
        user = FirebaseAuth.getInstance().getCurrentUser();
        final String email = user.getEmail();
        String oldpass = String.valueOf(binding.currentPassward.getText());
        AuthCredential credential = EmailAuthProvider.getCredential(email, oldpass);
        if (binding.currentPassward.getText().toString().isEmpty()) {
            dialog.cancel();
            Toast.makeText(this, "This Field Must Completed", Toast.LENGTH_SHORT).show();
            binding.currentPassward.requestFocus();
        } else if (!String.valueOf(binding.newPassward.getText()).equals(String.valueOf(binding.reNewPassward.getText()))) {
            dialog.cancel();
            Toast.makeText(this, "Passward dosent matches", Toast.LENGTH_SHORT).show();
        } else {


            user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        user.updatePassword(String.valueOf(binding.reNewPassward.getText())).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                dialog.cancel();
                                updatePasswardView();
                                if (!task.isSuccessful()) {
                                    Snackbar snackbar_fail = Snackbar.make(binding.changePassward, "Something went wrong. Please try again later", Snackbar.LENGTH_LONG);
                                    snackbar_fail.show();
                                } else {
                                    Snackbar snackbar_su = Snackbar.make(binding.changePassward, "Password Successfully Modified", Snackbar.LENGTH_LONG);
                                    snackbar_su.show();
                                }
                            }
                        });
                    } else {
                        dialog.cancel();
                        Snackbar snackbar_su = Snackbar.make(binding.changePassward, "Authentication Failed", Snackbar.LENGTH_LONG);
                        snackbar_su.show();
                    }
                }
            });

        }
    }

    private void uploadImage(Uri uri) {
        showProgreesBar();
        binding.profilePhoto.setImageURI(uri);
        imageRef = FirebaseStorage.getInstance().getReference("profileImages/" + profileEmail + ".jpg");

        imageRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        fireBaseImageUri = uri.toString();
                        userRef.child(userId).child("profilePhoto").setValue(fireBaseImageUri).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                profile = new UserProfileChangeRequest.Builder().setPhotoUri(uri).build();
                                user.updateProfile(profile).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            dialog.cancel();
                                            Toast.makeText(ProfileActivity.this, "Photo Updated Successfly", Toast.LENGTH_SHORT).show();

                                            refresh();

                                        }
                                    }
                                });

                            }
                        });

                    }
                });
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

    private void showProgreesBar() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.load_dialoge, null);
        ProgressBar progressBar = view.findViewById(R.id.profilePbar);

        builder.setView(view);
        dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {


        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {

            Uri uri = data.getData();
            if (uri != null) {
                uploadImage(uri);
            }
        }

    }


}