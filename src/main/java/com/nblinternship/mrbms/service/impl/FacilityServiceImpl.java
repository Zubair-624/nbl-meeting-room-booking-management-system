package com.nblinternship.mrbms.service.impl;

import com.nblinternship.mrbms.entity.Facility;
import com.nblinternship.mrbms.entity.MeetingRoom;
import com.nblinternship.mrbms.entity.RoomFacility;
import com.nblinternship.mrbms.repository.FacilityRepository;
import com.nblinternship.mrbms.repository.MeetingRoomRepository;
import com.nblinternship.mrbms.repository.RoomFacilityRepository;
import com.nblinternship.mrbms.service.FacilityService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FacilityServiceImpl implements FacilityService {

    private final FacilityRepository facilityRepository;
    private final RoomFacilityRepository roomFacilityRepository;
    private final MeetingRoomRepository meetingRoomRepository;

    public FacilityServiceImpl(FacilityRepository facilityRepository,
                               RoomFacilityRepository roomFacilityRepository,
                               MeetingRoomRepository meetingRoomRepository) {
        this.facilityRepository = facilityRepository;
        this.roomFacilityRepository = roomFacilityRepository;
        this.meetingRoomRepository = meetingRoomRepository;
    }

    @Override
    public List<Facility> getAllFacilities() {
        return facilityRepository.findAll();
    }

    @Override
    public Facility saveFacility(Facility facility) {
        return facilityRepository.save(facility);
    }

    @Override
    public void deleteFacility(Integer id) {
        facilityRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void assignFacilitiesToRoom(Integer roomId, List<Integer> facilityIds) {
        MeetingRoom room = meetingRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Room not found"));

        List<RoomFacility> existing = roomFacilityRepository.findByRoomRoomId(roomId);
        roomFacilityRepository.deleteAll(existing);

        if (facilityIds != null) {
            for (Integer facilityId : facilityIds) {
                Facility facility = facilityRepository.findById(facilityId).orElse(null);
                if (facility != null) {
                    RoomFacility rf = new RoomFacility();
                    rf.setRoom(room);
                    rf.setFacility(facility);
                    roomFacilityRepository.save(rf);
                }
            }
        }
    }

    @Override
    public List<Facility> getFacilitiesByRoomId(Integer roomId) {
        return roomFacilityRepository.findByRoomRoomId(roomId).stream()
                .map(RoomFacility::getFacility)
                .collect(Collectors.toList());
    }
}