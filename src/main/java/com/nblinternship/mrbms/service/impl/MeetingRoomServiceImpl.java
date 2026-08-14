package com.nblinternship.mrbms.service.impl;

import com.nblinternship.mrbms.entity.MeetingRoom;
import com.nblinternship.mrbms.repository.MeetingRoomRepository;
import com.nblinternship.mrbms.service.MeetingRoomService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MeetingRoomServiceImpl implements MeetingRoomService {

    private final MeetingRoomRepository meetingRoomRepository;

    public MeetingRoomServiceImpl(MeetingRoomRepository meetingRoomRepository) {
        this.meetingRoomRepository = meetingRoomRepository;
    }

    @Override
    public List<MeetingRoom> getAllRooms() {
        return meetingRoomRepository.findAll();
    }

    @Override
    public List<MeetingRoom> getActiveRooms() {
        return meetingRoomRepository.findByActiveTrue();
    }

    @Override
    public List<MeetingRoom> getAllActiveRooms() {
        return getActiveRooms();
    }

    @Override
    public Optional<MeetingRoom> getRoomById(Integer id) {
        return meetingRoomRepository.findById(id);
    }

    @Override
    public Optional<MeetingRoom> getRoomByCode(String roomCode) {
        return meetingRoomRepository.findByRoomCode(roomCode);
    }

    @Override
    public long getRoomCount() {
        return meetingRoomRepository.count();
    }

    @Override
    public MeetingRoom saveRoom(MeetingRoom meetingRoom) {
        return meetingRoomRepository.save(meetingRoom);
    }

    @Override
    public void deleteRoomById(Integer id) {
        meetingRoomRepository.deleteById(id);
    }
}