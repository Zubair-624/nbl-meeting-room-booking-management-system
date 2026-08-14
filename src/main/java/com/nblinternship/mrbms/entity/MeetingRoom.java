package com.nblinternship.mrbms.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "MeetingRooms")
public class MeetingRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "RoomID")
    private Integer roomId;

    @Column(name = "RoomCode", length = 20, nullable = false, unique = true)
    private String roomCode;

    @Column(name = "RoomName", length = 100, nullable = false)
    private String roomName;

    @Column(name = "Building", length = 100, nullable = false)
    private String building;

    @Column(name = "Floor", nullable = false)
    private Integer floor;

    @Column(name = "Capacity", nullable = false)
    private Integer capacity;

    @Column(name = "Description", length = 300, nullable = true)
    private String description;

    @Column(name = "Status", nullable = false)
    private Boolean active = true; // Mapped to existing 'Status' column in SQL Server

    @Column(name = "ImagePath", length = 255, nullable = true)
    private String imagePath;

    public MeetingRoom() {
    }

    public Integer getRoomId() {
        return roomId;
    }

    public void setRoomId(Integer roomId) {
        this.roomId = roomId;
    }

    public String getRoomCode() {
        return roomCode;
    }

    public void setRoomCode(String roomCode) {
        this.roomCode = roomCode;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public Integer getFloor() {
        return floor;
    }

    public void setFloor(Integer floor) {
        this.floor = floor;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}