package com.nblinternship.mrbms.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "RoomFacilities", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"RoomID", "FacilityID"})
})
public class RoomFacility {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "RoomFacilityID")
    private Integer roomFacilityId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RoomID", nullable = false)
    private MeetingRoom room;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FacilityID", nullable = false)
    private Facility facility;

    public RoomFacility() {
    }

    public Integer getRoomFacilityId() {
        return roomFacilityId;
    }

    public void setRoomFacilityId(Integer roomFacilityId) {
        this.roomFacilityId = roomFacilityId;
    }

    public MeetingRoom getRoom() {
        return room;
    }

    public void setRoom(MeetingRoom room) {
        this.room = room;
    }

    public Facility getFacility() {
        return facility;
    }

    public void setFacility(Facility facility) {
        this.facility = facility;
    }
}