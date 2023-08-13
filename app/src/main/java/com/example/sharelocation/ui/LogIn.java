package com.example.sharelocation.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.sharelocation.R;
import com.example.sharelocation.databinding.ActivityLoginBinding;
import com.example.sharelocation.pojo.LogInModel;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

public class LogIn extends AppCompatActivity {
    private static final int REQ_ONE_TAP = 2;  // Can be any integer unique to the Activity.
    ActivityLoginBinding binding;
    DatabaseReference databaseReference;
    GoogleSignInOptions googleSignInOptions;
    GoogleSignInClient googleSignInClient;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseFirestore firestore;
    private boolean showOneTapUI = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        binding.setLogIn(this);
        binding.setLifecycleOwner(this);
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("users");


        googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken("15264368424-a9dj1bndgfosdkc3vh1fhv6q74p4c16b.apps.googleusercontent.com").requestEmail().build();
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
        forgetPassward();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), Welcome.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        finish();
        startActivity(intent);


    }

    public void googleSignIn() {
        /*
        googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
        Intent intent = googleSignInClient.getSignInIntent();
        startActivityForResult(intent, 100);

         */


        Intent intent = googleSignInClient.getSignInIntent();
        startActivityForResult(intent, 100);


    }

    private void forgetPassward() {


        binding.forgetPassward1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  showProgreesBar();
                mAuth.sendPasswordResetEmail(user.getEmail()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(LogIn.this, "We Sent Email", Toast.LENGTH_SHORT).show();
                            //   dialog.cancel();
                        }
                    }
                });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null) {
                    firebaseAuthWithGoogle(account);
                } else {
                    Log.w("AUTH", "Account is NULL");
                    Toast.makeText(LogIn.this, "Sign-in failed, try again later.", Toast.LENGTH_LONG).show();
                }
            } catch (ApiException e) {
                Log.w("AUTH", "Google sign in failed", e);
                Toast.makeText(LogIn.this, "Sign-in failed, try again later.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d("AUTH", "firebaseAuthWithGoogle:" + acct.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    //      Log.d("AUTH", "signInWithCredential:success");
                    //   startActivity(new Intent(this, Home.class));
                    DatabaseReference database = FirebaseDatabase.getInstance().getReference();
                    LogInModel logInModel = new LogInModel(acct.getDisplayName(), acct.getEmail(), acct.getIdToken(), acct.getId(), String.valueOf(acct.getPhotoUrl()));


                    database.child("users").child(acct.getId()).setValue(logInModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(LogIn.this, "Sign-in successful!", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(getApplicationContext(), Home.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                finish();
                                startActivity(intent);

                            }
                        }
                    });


                } else {
                    //    Log.w("AUTH", "signInWithCredential:failure", task.getException());
                    Toast.makeText(LogIn.this, "Sign-in failed, try again later.", Toast.LENGTH_LONG).show();
                }
            }
        });
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
                            String profilePhoto = String.valueOf(user.getPhotoUrl());
                            LogInModel logInModel = new LogInModel(userName, userEmail, userTokenId, userId, profilePhoto);


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