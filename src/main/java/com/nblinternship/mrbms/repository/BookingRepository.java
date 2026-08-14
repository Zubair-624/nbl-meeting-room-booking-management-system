package com.nblinternship.mrbms.repository;

import com.nblinternship.mrbms.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {

    @Query("SELECT b FROM Booking b WHERE b.room.roomId = :roomId AND b.bookingDate = :date AND LOWER(b.status) != 'cancelled'")
    List<Booking> findActiveBookingsByRoomAndDate(@Param("roomId") Integer roomId, @Param("date") LocalDate date);

    @Query("SELECT b FROM Booking b JOIN FETCH b.room JOIN FETCH b.user WHERE b.user.userId = :userId ORDER BY b.bookingDate DESC")
    List<Booking> findByUserUserId(@Param("userId") Integer userId);

    @Query("SELECT b FROM Booking b JOIN FETCH b.room JOIN FETCH b.user ORDER BY b.createdDate DESC")
    List<Booking> findAllWithDetails();

    @Query("SELECT b FROM Booking b JOIN FETCH b.room JOIN FETCH b.user WHERE b.bookingDate = :date AND LOWER(b.status) != 'cancelled'")
    List<Booking> findActiveBookingsByRoomAndDateAllRooms(@Param("date") LocalDate date);

    @Query("SELECT COUNT(b) > 0 FROM Booking b " +
            "WHERE b.room.roomId = :roomId " +
            "AND b.bookingDate = :date " +
            "AND LOWER(b.status) != 'cancelled' " +
            "AND (:startTime < b.endTime AND :endTime > b.startTime)")
    boolean existsOverlappingBooking(@Param("roomId") Integer roomId,
                                     @Param("date") LocalDate date,
                                     @Param("startTime") LocalTime startTime,
                                     @Param("endTime") LocalTime endTime);
}