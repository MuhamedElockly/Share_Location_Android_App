package com.example.sharelocation.pojo;

public class UserModel {
    private String name;
    private String email;
    private String passward;
    private String tokenId;



    public UserModel(String name, String email, String passward) {
        this.name = name;
        this.email = email;
        this.passward = passward;

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
