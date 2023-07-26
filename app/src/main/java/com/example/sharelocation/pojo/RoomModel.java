package com.example.sharelocation.pojo;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class RoomModel {
    private String name;
    private String numberOfRooms;
    private String roomCapacity;
    private String id;
    private String admin;

    public RoomModel() {

    }

    public RoomModel(String name, String numberOfRooms, String id, String roomCapacity, String admin) {
        this.name = name;
        this.numberOfRooms = numberOfRooms;
        this.id = id;
        this.roomCapacity = roomCapacity;
        this.admin = admin;
    }

    public String getAdmin() {
        return admin;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRoomCapacity() {
        return roomCapacity;
    }

    public void setRoomCapacity(String roomCapacity) {
        this.roomCapacity = roomCapacity;
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
