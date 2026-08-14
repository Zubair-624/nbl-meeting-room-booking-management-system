package com.nblinternship.mrbms.controller;

import com.nblinternship.mrbms.entity.Booking;
import com.nblinternship.mrbms.entity.MeetingRoom;
import com.nblinternship.mrbms.repository.BookingRepository;
import com.nblinternship.mrbms.repository.MeetingRoomRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/tv")
public class TvDisplayController {

    private final MeetingRoomRepository meetingRoomRepository;
    private final BookingRepository bookingRepository;

    public TvDisplayController(MeetingRoomRepository meetingRoomRepository, BookingRepository bookingRepository) {
        this.meetingRoomRepository = meetingRoomRepository;
        this.bookingRepository = bookingRepository;
    }

    @GetMapping("/room/{roomCode}")
    public String showTvDisplay(@PathVariable("roomCode") String roomCode, Model model) {
        Optional<MeetingRoom> roomOpt = meetingRoomRepository.findByRoomCode(roomCode);
        if (roomOpt.isEmpty()) {
            return "error";
        }

        MeetingRoom room = roomOpt.get();
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        List<Booking> todayBookings = bookingRepository.findActiveBookingsByRoomAndDate(room.getRoomId(), today);

        Booking currentBooking = todayBookings.stream()
                .filter(b -> !now.isBefore(b.getStartTime()) && !now.isAfter(b.getEndTime()))
                .findFirst()
                .orElse(null);

        Booking nextBooking = todayBookings.stream()
                .filter(b -> b.getStartTime().isAfter(now))
                .min(Comparator.comparing(Booking::getStartTime))
                .orElse(null);

        model.addAttribute("room", room);
        model.addAttribute("currentBooking", currentBooking);
        model.addAttribute("nextBooking", nextBooking);
        model.addAttribute("isOccupied", currentBooking != null);

        return "tv-display";
    }
}