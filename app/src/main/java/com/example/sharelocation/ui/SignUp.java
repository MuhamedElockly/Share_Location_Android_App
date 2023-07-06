package com.example.sharelocation.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.sharelocation.R;
import com.example.sharelocation.databinding.ActivitySignUpBinding;
import com.example.sharelocation.pojo.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignUp extends AppCompatActivity {
    ActivitySignUpBinding binding;
    SingUpViewModel singUpViewModel;
    private FirebaseAuth mAuth;
    private UserModel userModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up);
        mAuth = FirebaseAuth.getInstance();
        binding.setSignUp(this);
        binding.setLifecycleOwner(this);
        singUpViewModel = ViewModelProviders.of(this).get(SingUpViewModel.class);
        singUpViewModel.mutableLiveData.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                binding.pBar.setVisibility(View.GONE);
                if (s.equals("Please Verify Your E-mail")) {
                    startNewActivity();
                }
                Toast.makeText(SignUp.this, s, Toast.LENGTH_SHORT).show();
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

    public void register() {
        //  ProgressDialog.show(this, "Loading", "Wait while loading...");


        String name = binding.userName.getText().toString().trim();
        String email = binding.email.getText().toString().trim();
        String passward = binding.passward.getText().toString().trim();
        String rePassward = binding.rePassward.getText().toString().trim();
        if (name.isEmpty()) {
            binding.userName.setError("This Field Is Required");
            binding.userName.requestFocus();
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.email.setError("Email Is Not Falid");
            binding.email.requestFocus();
        } else if (email.isEmpty()) {
            binding.email.setError("This Field Is Required");
            binding.email.requestFocus();
        } else if (passward.isEmpty()) {
            binding.passward.setError("This Field Is Required");
            binding.passward.requestFocus();

        } else if (rePassward.isEmpty()) {
            binding.rePassward.setError("This Field Is Required");
            binding.rePassward.requestFocus();

        } else if (!passward.equals(rePassward)) {
            binding.rePassward.setError("Passward Is Not Matches");
            binding.rePassward.requestFocus();
        } else {
            binding.pBar.setVisibility(View.VISIBLE);
            userModel = new UserModel(name, email, passward);
            singUpViewModel.getRegestrationFeedback(userModel, this);
        }

    }

    private void startNewActivity() {
        Intent intent = new Intent(getApplicationContext(), LogIn.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        finish();
        startActivity(intent);
    }

}