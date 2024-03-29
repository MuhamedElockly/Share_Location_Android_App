package com.example.sharelocation.pojo;

public class UserModel {
    private String name;
    private String email;
    private String passward;
    private String tokenId;
    private String imageUri;
    private String phoneNumber;

    private boolean signedByGoogle;

    public UserModel(String name, String email, String passward, String imageUri, String phoneNumber, boolean signedByGoogle
            , String tokenId) {
        this.name = name;
        this.email = email;
        this.passward = passward;
        this.imageUri = imageUri;
        this.phoneNumber = phoneNumber;
        this.signedByGoogle = signedByGoogle;
        this.tokenId = tokenId;
    }

    public boolean isSignedByGoogle() {
        return signedByGoogle;
    }

    public void setSignedByGoogle(boolean signedByGoogle) {
        this.signedByGoogle = signedByGoogle;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
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

    public String getPassward() {
        return passward;
    }

    public void setPassward(String passward) {
        this.passward = passward;
    }
}
