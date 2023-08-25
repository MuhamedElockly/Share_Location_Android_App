package com.example.sharelocation.pojo;

public class LogInModel {
    private String name;
    private String email;
    private String userId;
    private String tokenId;
    private String profilePhoto;
    private String phoneNumber;

    public boolean isSignedByGoogle() {
        return signedByGoogle;
    }

    public void setSignedByGoogle(boolean signedByGoogle) {
        this.signedByGoogle = signedByGoogle;
    }

    private boolean signedByGoogle;

    public LogInModel(String name, String email, String tokenId, String userId, String profilePhoto, String phoneNumber, boolean signedByGoogle) {
        this.name = name;
        this.email = email;
        this.tokenId = tokenId;
        this.userId = userId;
        this.profilePhoto = profilePhoto;
        this.phoneNumber = phoneNumber;
        this.signedByGoogle = signedByGoogle;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getProfilePhoto() {
        return profilePhoto;
    }

    public void setProfilePhoto(String profilePhoto) {
        this.profilePhoto = profilePhoto;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTokenId() {
        return tokenId;
    }

    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


}
