package com.example.sharelocation.ui;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.sharelocation.pojo.LogInModel;
import com.example.sharelocation.pojo.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Calendar;

public class SingUpViewModel extends ViewModel {
    MutableLiveData<String> mutableLiveData = new MutableLiveData<>();
    MutableLiveData<String> imageMutableLiveData = new MutableLiveData<>();

    String feedback = " ";
    StorageReference imageRef;
    Context context;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private UserProfileChangeRequest profile;
    private String fireBaseImageUri;

    private String addUserToFireBase(UserModel userModel, Context resourceActivity) {
        this.context = resourceActivity;
        mAuth = FirebaseAuth.getInstance();

        mAuth.createUserWithEmailAndPassword(userModel.getEmail(), userModel.getPassward()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {

            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {
                    user = mAuth.getCurrentUser();

                    PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(userModel.getPhoneNumber(), "OTP_CODE");
                    user.updatePhoneNumber(phoneAuthCredential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.e("phoneNumber1", " : " + user.getPhoneNumber());
                            } else {
                                Log.e("phoneNumber1", " : " + task.getException().getMessage());
                            }
                        }
                    });
                    profile = new UserProfileChangeRequest.Builder().setDisplayName(userModel.getName())

                            .setPhotoUri(Uri.parse(userModel.getImageUri())).build();
                    user.updateProfile(profile);

                    user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task1) {
                            if (task1.isSuccessful()) {
                                LogInModel logInModel = new LogInModel(user.getDisplayName(), user.getEmail(), String.valueOf(user.getIdToken(false)), user.getUid(), userModel.getImageUri(), userModel.getPhoneNumber(), false);
                                DatabaseReference database = FirebaseDatabase.getInstance().getReference();
                                database.child("users").child(user.getUid()).setValue(logInModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            feedback = "Please Verify Your E-mail";
                                            //   Toast.makeText(resourceActivity, "Please Verify Your E-mail", Toast.LENGTH_LONG).show();
                                            mutableLiveData.setValue(feedback);
                                        } else {
                                            feedback = task.getException().getMessage();
                                            mutableLiveData.setValue(feedback);
                                        }
                                        mAuth.signOut();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        feedback = e.getMessage();
                                        mutableLiveData.setValue(feedback);
                                        mAuth.signOut();
                                        imageRef = FirebaseStorage.getInstance().getReference("profileImages/" + userModel.getEmail() + ".jpg");
                                        imageRef.delete();
                                    }
                                });


                            } else {
                                feedback = task.getException().getMessage();
                                mutableLiveData.setValue(feedback);
                            }
                            //    mAuth.signOut();

                        }
                    });


                } else if (task.getException() instanceof FirebaseAuthUserCollisionException) {

                    feedback = "User Already Exist";
                    mutableLiveData.setValue(feedback);
                } else {
                    feedback = task.getException().getMessage();
                    mutableLiveData.setValue(feedback);
                }

            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                imageRef = FirebaseStorage.getInstance().getReference("profileImages/" + userModel.getEmail() + ".jpg");
                imageRef.delete();
            }
        });

        //   Toast.makeText(resourceActivity, feedback, Toast.LENGTH_SHORT).show();
        return feedback;
    }

    public void uploadProfileImage(String email, Uri uriProfileImage) {
        Calendar calendar = Calendar.getInstance();
        long now = calendar.getTimeInMillis();
        imageRef = FirebaseStorage.getInstance().getReference("profileImages/" + now + ".jpg");

        imageRef.putFile(uriProfileImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        fireBaseImageUri = uri.toString();
                        //   Log.d("name", fireBaseImageUri);
                        // feedback = "Image Successfly Uploaded";
                        imageMutableLiveData.setValue(fireBaseImageUri);

                    }
                });


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                StorageReference defaultImage = FirebaseStorage.getInstance().getReference("profileImages/" + "default.jpg");
                defaultImage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Log.d("imageName", "Existtt");
                        imageMutableLiveData.setValue(String.valueOf(uri));
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        imageMutableLiveData.setValue("");
                    }
                });

            }
        });


    }

    public void getRegestrationFeedback(UserModel userModel, Context resourceActivity) {
        String feedback = addUserToFireBase(userModel, resourceActivity);
        // mutableLiveData.setValue(feedback);
    }
}
