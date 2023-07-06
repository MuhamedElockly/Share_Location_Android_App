package com.example.sharelocation.ui;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.sharelocation.pojo.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class SingUpViewModel extends ViewModel {
    MutableLiveData<String> mutableLiveData = new MutableLiveData<>();
    String feedback = " ";
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private UserProfileChangeRequest profile;

    private String addUserToFireBase(UserModel userModel, Context resourceActivity) {

        mAuth = FirebaseAuth.getInstance();

        mAuth.createUserWithEmailAndPassword(userModel.getEmail(), userModel.getPassward()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {

            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {
                    user = mAuth.getCurrentUser();

                    profile = new UserProfileChangeRequest.Builder().setDisplayName(userModel.getName()).build();
                    user.updateProfile(profile);
                    user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task1) {
                            if (task1.isSuccessful()) {
                                feedback = "Please Verify Your E-mail";
                                //   Toast.makeText(resourceActivity, "Please Verify Your E-mail", Toast.LENGTH_LONG).show();
                                mutableLiveData.setValue(feedback);
                            } else {
                                feedback = task.getException().getMessage();
                                mutableLiveData.setValue(feedback);
                            }
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
        });
        //   Toast.makeText(resourceActivity, feedback, Toast.LENGTH_SHORT).show();
        return feedback;
    }

    public void getRegestrationFeedback(UserModel userModel, Context resourceActivity) {
        String feedback = addUserToFireBase(userModel, resourceActivity);
        // mutableLiveData.setValue(feedback);
    }
}
