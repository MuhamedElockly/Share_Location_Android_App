package com.example.sharelocation.pojo;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class RoomModel {
    private String name;
    private String numberOfRooms;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private String id;

    public RoomModel() {

    }

    public RoomModel(String name, String numberOfRooms,String id) {
        this.name = name;
        this.numberOfRooms = numberOfRooms;
        this.id=id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumberOfRooms() {
        return numberOfRooms;
    }

    public void setNumberOfRooms(String numberOfRooms) {
        this.numberOfRooms = numberOfRooms;
    }
}
