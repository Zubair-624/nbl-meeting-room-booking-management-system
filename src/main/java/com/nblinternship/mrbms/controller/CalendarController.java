package com.nblinternship.mrbms.controller;

import com.nblinternship.mrbms.entity.Booking;
import com.nblinternship.mrbms.repository.BookingRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class CalendarController {

    private final BookingRepository bookingRepository;

    public CalendarController(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    @GetMapping("/api/calendar/events")
    public List<Map<String, Object>> getEvents(@RequestParam(value = "roomId", required = false) Integer roomId) {
        List<Booking> bookings = bookingRepository.findAllWithDetails();

        return bookings.stream()
                .filter(b -> roomId == null || b.getRoom().getRoomId().equals(roomId))
                .map(b -> {
                    String color;
                    if ("Cancelled".equalsIgnoreCase(b.getStatus()) || "Rejected".equalsIgnoreCase(b.getStatus())) {
                        color = "#6c757d"; // gray
                    } else {
                        color = "#dc3545"; // red (occupied)
                    }

                    return Map.<String, Object>of(
                            "title", b.getMeetingTitle() + " (" + b.getRoom().getRoomName() + ")",
                            "start", b.getBookingDate() + "T" + b.getStartTime(),
                            "end", b.getBookingDate() + "T" + b.getEndTime(),
                            "color", color
                    );
                })
                .collect(Collectors.toList());
    }
}