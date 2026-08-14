package com.nblinternship.mrbms.service;

import com.nblinternship.mrbms.entity.Facility;
import java.util.List;

public interface FacilityService {
    List<Facility> getAllFacilities();
    Facility saveFacility(Facility facility);
    void deleteFacility(Integer id);
    void assignFacilitiesToRoom(Integer roomId, List<Integer> facilityIds);
    List<Facility> getFacilitiesByRoomId(Integer roomId);
}