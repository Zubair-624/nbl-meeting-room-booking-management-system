package com.nblinternship.mrbms.service;

import com.nblinternship.mrbms.entity.MeetingRoom;

import java.util.List;
import java.util.Optional;

public interface MeetingRoomService {

    List<MeetingRoom> getAllRooms();
    List<MeetingRoom> getActiveRooms();
    List<MeetingRoom> getAllActiveRooms(); // Added method to match controller call
    Optional<MeetingRoom> getRoomById(Integer id);
    Optional<MeetingRoom> getRoomByCode(String roomCode);
    long getRoomCount();

    MeetingRoom saveRoom(MeetingRoom meetingRoom);
    void deleteRoomById(Integer id);
}