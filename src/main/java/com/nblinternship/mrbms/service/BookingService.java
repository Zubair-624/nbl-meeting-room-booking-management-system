package com.nblinternship.mrbms.service;

import com.nblinternship.mrbms.entity.Booking;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface BookingService {

    List<Booking> getAllBookings();
    List<Booking> getBookingsByUserId(Integer userId);
    List<Booking> getActiveBookingsByRoomAndDate(Integer roomId, LocalDate date);
    Optional<Booking> getBookingById(Integer id);
    long getBookingCount();

    Booking createBooking(Booking booking);
    Booking updateBookingStatus(Integer bookingId, String status);
    boolean isRoomAvailable(Integer roomId, LocalDate date, LocalTime startTime, LocalTime endTime);

    void cancelBooking(Integer bookingId, Integer userId);
}