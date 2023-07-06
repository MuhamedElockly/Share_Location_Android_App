package com.example.sharelocation.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.sharelocation.R;
import com.example.sharelocation.databinding.ActivityLoginBinding;
import com.example.sharelocation.pojo.LogInModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

public class LogIn extends AppCompatActivity {
    ActivityLoginBinding binding;
    DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        binding.setLogIn(this);
        binding.setLifecycleOwner(this);
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("users");
    }

    public void logIn() {

        //  Toast.makeText(getApplicationContext(), "gggg", Toast.LENGTH_LONG).show();
        String email = binding.loginEmail.getText().toString().trim();
        String passward = binding.passward.getText().toString().trim();
        if (email.isEmpty()) {
            binding.loginEmail.setError("This Field Is Required");
            binding.loginEmail.requestFocus();
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.loginEmail.setError("Email Is Not Falid");
            binding.loginEmail.requestFocus();
        } else if (passward.isEmpty()) {
            binding.passward.setError("This Field Is Required");
            binding.passward.requestFocus();
        } else {
            binding.pBar.setVisibility(View.VISIBLE);
            mAuth.signInWithEmailAndPassword(email, passward).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    binding.pBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        user = mAuth.getCurrentUser();
                        if (!user.isEmailVerified()) {
                            Toast.makeText(getApplicationContext(), "Please Verify Your E-mail", Toast.LENGTH_LONG).show();
                            mAuth.signOut();
                        } else {
                            DatabaseReference database = FirebaseDatabase.getInstance().getReference();

                            String userEmail = user.getEmail();
                            String userName = user.getDisplayName();
                            String userTokenId = String.valueOf(user.getIdToken(false));
                            String userId = user.getUid();
                            LogInModel logInModel = new LogInModel(userName, userEmail, userTokenId,userId);



                            String id = database.push().getKey();
                            database.child("users").child(user.getUid()).setValue(logInModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getApplicationContext(), "Added Successfly", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                            //}

                            Intent intent = new Intent(getApplicationContext(), Home.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            finish();
                            startActivity(intent);
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });
        }

    }
}