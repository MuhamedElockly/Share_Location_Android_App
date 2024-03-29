package com.example.sharelocation.ui;

import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.sharelocation.R;
import com.example.sharelocation.databinding.ActivityLoginBinding;
import com.example.sharelocation.pojo.LogInModel;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;

public class LogIn extends AppCompatActivity {
    private static final int REQ_ONE_TAP = 2;  // Can be any integer unique to the Activity.
    ActivityLoginBinding binding;
    DatabaseReference databaseReference;
    GoogleSignInOptions googleSignInOptions;
    GoogleSignInClient googleSignInClient;
    LogInModel logInModel;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseFirestore firestore;
    private boolean showOneTapUI = true;
    private AlertDialog dialog;
    private AsyncTask mMyTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        binding.setLogIn(this);
        binding.setLifecycleOwner(this);
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        user = mAuth.getCurrentUser();
        googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken("15264368424-a9dj1bndgfosdkc3vh1fhv6q74p4c16b.apps.googleusercontent.com").requestEmail().build();
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);


        forgetPassward();

    }

    private void requestPhoneNumber() {

        GoogleSignIn.requestPermissions(this, 20, GoogleSignIn.getLastSignedInAccount(LogIn.this), new Scope(Scopes.PLUS_ME));
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), Welcome.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        finish();
        startActivity(intent);


    }

    public void googleSignIn() {

        if (!isNetworkAvailable()) {
            showConfirmationDialoge("Please check internet connection !");
            return;
        }
        Intent intent = googleSignInClient.getSignInIntent();
        startActivityForResult(intent, 100);
        //     showProgreesBar();
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

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
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

    private void forgetPassward() {
        binding.forgetPassward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgreesBar();
                String email = binding.loginEmail.getText().toString();
                if (!email.isEmpty()) {
                    //  binding.forgetPassward.setEnabled(true);
                    if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {


                        mAuth.sendPasswordResetEmail(binding.loginEmail.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                dialog.cancel();
                                if (task.isSuccessful()) {
                                    //  Toast.makeText(LogIn.this, "We Sent Email", Toast.LENGTH_SHORT).show();
                                    showConfirmationDialoge("Verification email was sent ");
                                    //   dialog.cancel();
                                } else if (task.getException() instanceof FirebaseAuthException) {
                                    String errorCode = ((FirebaseAuthException) task.getException()).getErrorCode();
                                    if (errorCode.equals("ERROR_USER_NOT_FOUND")) {
                                        //  Toast.makeText(LogIn.this, "This email not exist", Toast.LENGTH_LONG).show();
                                        showConfirmationDialoge("This email not exist");
                                    }
                                } else if (task.getException() instanceof FirebaseNetworkException) {
                                    // Toast.makeText(LogIn.this, "Please check internet connection !", Toast.LENGTH_LONG).show();
                                    showConfirmationDialoge("Please check internet connection !");
                                } else {

                                    //   Toast.makeText(LogIn.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    showConfirmationDialoge(task.getException().getMessage());
                                }
                            }

                        });

                    } else {
                        dialog.cancel();
                        //    binding.loginEmail.setError("Email is not vaild");
                        showConfirmationDialoge("Email is not vaild");
                        binding.loginEmail.requestFocus();
                    }

                } else {
                    dialog.cancel();
                    //  binding.loginEmail.setError("Email is empty");
                    showConfirmationDialoge("Email is empty");
                    binding.loginEmail.requestFocus();
                }
            }

        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            showProgreesBar();
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);

                if (account != null) {
                    firebaseAuthWithGoogle(account);
                } else {
                    dialog.cancel();
                    if (task.getException() instanceof NetworkErrorException) {

                        // Toast.makeText(LogIn.this, "Please check internet connection !", Toast.LENGTH_LONG).show();
                        showConfirmationDialoge("Please check internet connection !");
                    } else {

                        Log.w("AUTH", "Account is NULL");
                        //    Toast.makeText(LogIn.this, "Sign-in failed, try again later.", Toast.LENGTH_LONG).show();
                        showConfirmationDialoge("Sign-in failed, try again later.");
                    }
                }
            } catch (ApiException e) {
                dialog.cancel();
                if (task.getException() instanceof FirebaseNetworkException) {

                    // Toast.makeText(LogIn.this, "Please check internet connection !", Toast.LENGTH_LONG).show();
                    showConfirmationDialoge("Please check internet connection !");
                } else {

                    Log.w("AUTH", "Account is NULL");
                    //    Toast.makeText(LogIn.this, "Sign-in failed, try again later.", Toast.LENGTH_LONG).show();
                    showConfirmationDialoge("Sign-in failed, try again later.");
                }
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
                    DatabaseReference database = FirebaseDatabase.getInstance().getReference("users");
                    String userId = mAuth.getCurrentUser().getUid();
                    database.orderByChild("userId").equalTo(userId).addListenerForSingleValueEvent(new ValueEventListener() {

                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            if (snapshot.exists()) {
                                dialog.cancel();
                                //  Toast.makeText(getApplicationContext(), "PRESENT", Toast.LENGTH_LONG).show();
                                Toast.makeText(LogIn.this, "Sign-in successful!", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(getApplicationContext(), Home.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                finish();
                                startActivity(intent);
                            } else {
                                // Toast.makeText(getApplicationContext(), "Nooo", Toast.LENGTH_LONG).show();
                                //    downloadImageFromPath(acct.getPhotoUrl().getPath());


                                logInModel = new LogInModel(acct.getDisplayName(), acct.getEmail(), acct.getIdToken(), userId, String.valueOf(acct.getPhotoUrl()), "01000002", true);

                                mMyTask = new DownloadTask().execute(stringToURL(acct.getPhotoUrl().toString()));


                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            dialog.cancel();
                            showConfirmationDialoge(error.getMessage());
                        }
                    });

                } else if (task.getException() instanceof FirebaseNetworkException) {
                    dialog.cancel();
                    // Toast.makeText(LogIn.this, "Please check internet connection !", Toast.LENGTH_LONG).show();
                    showConfirmationDialoge("Please check internet connection !");
                } else {
                    dialog.cancel();
                    //   Toast.makeText(LogIn.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    showConfirmationDialoge(task.getException().getMessage());
                }
            }
        });
    }

    public void downloadImageFromPath(String path) {
        InputStream in = null;
        Bitmap bmp = null;
        //ImageView iv = (ImageView)findViewById(R.id.img1);
        int responseCode = -1;
        try {

            URL url = new URL(path);//"http://192.xx.xx.xx/mypath/img1.jpg
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setDoInput(true);
            con.connect();
            responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                //download
                in = con.getInputStream();
                bmp = BitmapFactory.decodeStream(in);
                in.close();
                // iv.setImageBitmap(bmp);
                Toast.makeText(LogIn.this, "Downloadedddd", Toast.LENGTH_LONG).show();
                Log.e("downloadedImage", "Doooownloadde");
            } else {
                Log.e("downloadedImage", "NOoo");
            }

        } catch (Exception ex) {
            Log.e("Exception", ex.toString());
        }
    }

    public void logIn() {
        /*
        if (!isNetworkAvailable()) {
            showConfirmationDialoge("Please check internet connection !");
            return;
        }

         */

        //  Toast.makeText(getApplicationContext(), "gggg", Toast.LENGTH_LONG).show();
        String email = binding.loginEmail.getText().toString().trim();
        String passward = binding.passward.getText().toString().trim();
        if (email.isEmpty()) {
            //  binding.loginEmail.setError("This Field Is Required");
            showConfirmationDialoge("Email feild is required");
            binding.loginEmail.requestFocus();
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            //  binding.loginEmail.setError("Email Is Not Falid");
            showConfirmationDialoge("Email is not valid");
            binding.loginEmail.requestFocus();
        } else if (passward.isEmpty()) {
            //  binding.passward.setError("This Field Is Required");
            showConfirmationDialoge("Passward feild is required");
            binding.passward.requestFocus();
        } else {
            binding.pBar.setVisibility(View.VISIBLE);
            mAuth.signInWithEmailAndPassword(email, passward).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    showConfirmationDialoge(e.getMessage());
                }
            }).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    binding.pBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        user = mAuth.getCurrentUser();
                        if (!user.isEmailVerified()) {
                            //  Toast.makeText(getApplicationContext(), "Please Verify Your E-mail", Toast.LENGTH_LONG).show();
                            showConfirmationDialoge("Please verify your email !");
                            mAuth.signOut();
                        } else {
                            /*
                            DatabaseReference database = FirebaseDatabase.getInstance().getReference();

                            String userEmail = user.getEmail();
                            String userName = user.getDisplayName();
                            String userTokenId = String.valueOf(user.getIdToken(false));
                            String userId = user.getUid();
                            String profilePhoto = String.valueOf(user.getPhotoUrl());
                            String phoneNumber = user.getPhoneNumber();
                            Toast.makeText(LogIn.this, phoneNumber, Toast.LENGTH_LONG);
                            //  Log.e("phoneNumber", " : " + user.getPhoneNumber());
                            LogInModel logInModel = new LogInModel(userName, userEmail, userTokenId, userId, profilePhoto, phoneNumber, false);


                            String id = database.push().getKey();
                            database.child("users").child(user.getUid()).setValue(logInModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getApplicationContext(), "Sign-in successful", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                             */
                            //}

                            Intent intent = new Intent(getApplicationContext(), Home.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            finish();
                            startActivity(intent);
                            Toast.makeText(getApplicationContext(), "Sign-in successful", Toast.LENGTH_SHORT).show();
                        }
                    } else if (task.getException() instanceof FirebaseAuthException) {
                        String errorCode = ((FirebaseAuthException) task.getException()).getErrorCode();
                        if (errorCode.equals("ERROR_USER_NOT_FOUND")) {
                            //  Toast.makeText(LogIn.this, "This email not exist", Toast.LENGTH_LONG).show();
                            showConfirmationDialoge("This email is not exist");
                            return;
                        }
                    } else if (task.getException() instanceof FirebaseNetworkException) {
                        // Toast.makeText(LogIn.this, "Please check internet connection !", Toast.LENGTH_LONG).show();
                        showConfirmationDialoge("Please check internet connection !");
                    } else {

                        //   Toast.makeText(LogIn.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        showConfirmationDialoge(task.getException().getMessage());
                    }
                }
            });
        }

    }

    protected URL stringToURL(String path) {
        try {
            URL url = new URL(path);
            return url;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    private class DownloadTask extends AsyncTask<URL, Void, Bitmap> {
        protected void onPreExecute() {
            //   mProgressDialog.show();
        }

        protected Bitmap doInBackground(URL... urls) {
            URL url = urls[0];
            HttpURLConnection connection = null;
            try {
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream inputStream = connection.getInputStream();
                BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
                return BitmapFactory.decodeStream(bufferedInputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        // When all async task done
        protected void onPostExecute(Bitmap result) {

            if (result != null) {

                Calendar calendar = Calendar.getInstance();
                long now = calendar.getTimeInMillis();
                StorageReference imageRef = FirebaseStorage.getInstance().getReference("profileImages/" + now + ".jpg");

                imageRef.putFile(getImageUri(LogIn.this, result)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                logInModel.setProfilePhoto(String.valueOf(uri));

                                DatabaseReference database = FirebaseDatabase.getInstance().getReference("users");

                                database.child(logInModel.getUserId()).setValue(logInModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            dialog.cancel();
                                            Toast.makeText(LogIn.this, "Sign-in successful!", Toast.LENGTH_LONG).show();
                                            Intent intent = new Intent(getApplicationContext(), Home.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            finish();
                                            startActivity(intent);

                                        }
                                    }
                                });
                            }
                        });

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        logInModel.setProfilePhoto("");

                        DatabaseReference database = FirebaseDatabase.getInstance().getReference("users");
                        database.child(logInModel.getUserId()).setValue(logInModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    dialog.cancel();
                                    Toast.makeText(LogIn.this, "Sign-in successful!", Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(getApplicationContext(), Home.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    finish();
                                    startActivity(intent);

                                }
                            }
                        });
                    }
                });

            } else {
                StorageReference defaultImage = FirebaseStorage.getInstance().getReference("profileImages/" + "default.jpg");
                defaultImage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        logInModel.setProfilePhoto(String.valueOf(uri));
                        DatabaseReference database = FirebaseDatabase.getInstance().getReference("users");
                        database.child(logInModel.getUserId()).setValue(logInModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    dialog.cancel();
                                    Toast.makeText(LogIn.this, "Sign-in successful!", Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(getApplicationContext(), Home.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    finish();
                                    startActivity(intent);

                                }
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        logInModel.setProfilePhoto("");

                        DatabaseReference database = FirebaseDatabase.getInstance().getReference("users");
                        database.child(logInModel.getUserId()).setValue(logInModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    dialog.cancel();
                                    Toast.makeText(LogIn.this, "Sign-in successful!", Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(getApplicationContext(), Home.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    finish();
                                    startActivity(intent);

                                }
                            }
                        });
                    }
                });

            }
        }
    }
}