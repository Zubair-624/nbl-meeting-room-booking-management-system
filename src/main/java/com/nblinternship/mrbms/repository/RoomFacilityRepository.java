package com.nblinternship.mrbms.repository;

import com.nblinternship.mrbms.entity.RoomFacility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomFacilityRepository extends JpaRepository<RoomFacility, Integer> {
    List<RoomFacility> findByRoomRoomId(Integer roomId);
}