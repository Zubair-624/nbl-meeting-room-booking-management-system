package com.nblinternship.mrbms.repository;

import com.nblinternship.mrbms.entity.MeetingRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MeetingRoomRepository extends JpaRepository<MeetingRoom, Integer> {

    List<MeetingRoom> findByActiveTrue();

    Optional<MeetingRoom> findByRoomCode(String roomCode);
}