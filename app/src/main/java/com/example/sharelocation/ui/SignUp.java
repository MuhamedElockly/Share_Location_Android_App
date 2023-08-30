package com.example.sharelocation.ui;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.sharelocation.R;
import com.example.sharelocation.databinding.ActivitySignUpBinding;
import com.example.sharelocation.pojo.UserModel;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;

public class SignUp extends AppCompatActivity {
    private static final int CHOOSE_IMAGE = 1;
    ActivitySignUpBinding binding;
    SingUpViewModel singUpViewModel;
    Bitmap bitmap;
    Uri uriProfileImage;
    String name = "";
    String email = "";
    String passward = "";
    String rePassward = "";
    String fireBaseImageUrl = "";
    String phoneNumber = "";
    private FirebaseAuth mAuth;
    private UserModel userModel;
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up);
        mAuth = FirebaseAuth.getInstance();
        binding.setSignUp(this);
        binding.setLifecycleOwner(this);

        uriProfileImage = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + this.getPackageName() + "/drawable/" + R.drawable.insert_photo);


        singUpViewModel = ViewModelProviders.of(this).get(SingUpViewModel.class);
        singUpViewModel.mutableLiveData.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                binding.pBar.setVisibility(View.GONE);
                if (s.equals("Please Verify Your E-mail")) {
                    Toast.makeText(SignUp.this, s, Toast.LENGTH_SHORT).show();
                    startNewActivity();
                } else {
                    //   Toast.makeText(SignUp.this, s, Toast.LENGTH_SHORT).show();
                    showConfirmationDialoge(s);
                }
            }
        });
        singUpViewModel.imageMutableLiveData.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                //   Log.d("userModel", s);
                userModel = new UserModel(name, email, passward, s, phoneNumber, false, "");
                singUpViewModel.getRegestrationFeedback(userModel, getApplicationContext());
            }
        });


        binding.profilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //     Toast.makeText(SignUp.this, "Image", Toast.LENGTH_SHORT).show();
                showImage();
            }
        });

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), Welcome.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        finish();
        startActivity(intent);


    }

    private void imagePicker() {
        binding.profilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.with(SignUp.this).crop()                    //Crop image(Optional), Check Customization for more option
                        .compress(1024)            //Final image size will be less than 1 MB(Optional)
                        .maxResultSize(1080, 1080)    //Final image resolution will be less than 1080 x 1080(Optional)
                        .start();

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //  updateUI(currentUser);
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

    private void showConfirmationDialoge(String erorrMessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.erorr_dialoge, null);
        TextView alertMessage = view.findViewById(R.id.errorBody);
        alertMessage.setText(erorrMessage);
        Button dialogeOk = view.findViewById(R.id.ok);
        builder.setView(view);
        final AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        dialogeOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
    }

    public void register() {
        //  ProgressDialog.show(this, "Loading", "Wait while loading...");


        name = binding.userName.getText().toString().trim();
        email = binding.email.getText().toString().trim();
        passward = binding.passward.getText().toString().trim();
        rePassward = binding.rePassward.getText().toString().trim();
        phoneNumber = binding.phoneNumber.getText().toString().trim();
        if (name.isEmpty()) {
            showConfirmationDialoge("Name field is required");
            //  binding.userName.setError("This Field Is Required");
            binding.userName.requestFocus();
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showConfirmationDialoge("Email not valid");
            //  binding.email.setError("Email Is Not valid");
            binding.email.requestFocus();
        } else if (email.isEmpty()) {
            showConfirmationDialoge("Email field is required");
            //    binding.email.setError("This Field Is Required");
            binding.email.requestFocus();
        } else if (phoneNumber.isEmpty()) {
            showConfirmationDialoge("Phone number field is required");
            // binding.phoneNumber.setError("This Field Is Required");
            binding.phoneNumber.requestFocus();
        } else if (passward.isEmpty()) {
            showConfirmationDialoge("Passward field is required");
            //  binding.passward.setError("This Field Is Required");
            binding.passward.requestFocus();

        } else if (rePassward.isEmpty()) {
            showConfirmationDialoge("RePassward field is required");
            //  binding.rePassward.setError("This Field Is Required");
            binding.rePassward.requestFocus();

        } else if (!passward.equals(rePassward)) {
            showConfirmationDialoge("Passward Is Not Matches");
            //    binding.rePassward.setError("Passward Is Not Matches");
            binding.rePassward.requestFocus();
        } else {
            binding.pBar.setVisibility(View.VISIBLE);
            singUpViewModel.uploadProfileImage(email, uriProfileImage);
            //  userModel = new UserModel(name, email, passward);
            // singUpViewModel.getRegestrationFeedback(userModel, this);
        }
    }

    private void showImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Profile Photo"), CHOOSE_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CHOOSE_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uriProfileImage = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uriProfileImage);
                binding.profilePhoto.setImageBitmap(bitmap);
                //  uploadImage();
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
    }

    private void startNewActivity() {
        Intent intent = new Intent(getApplicationContext(), LogIn.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        finish();
        startActivity(intent);
    }

}