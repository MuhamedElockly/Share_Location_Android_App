package com.example.sharelocation.pojo;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class MemebrsModel {
    private String name;
    private String id;
    private String tokenId;
    private String email;

    public MemebrsModel() {
    }

    public MemebrsModel(String name, String userId, String tokenId, String email) {
        this.name = name;
        this.id = userId;
        this.tokenId = tokenId;
        this.email = email;

    }

    public String getTokenId() {
        return tokenId;
    }

    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
