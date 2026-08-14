package com.nblinternship.mrbms.scheduler;

import com.nblinternship.mrbms.entity.Booking;
import com.nblinternship.mrbms.repository.BookingRepository;
import com.nblinternship.mrbms.service.EmailService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Component
public class BookingReminderScheduler {

    private final BookingRepository bookingRepository;
    private final EmailService emailService;

    public BookingReminderScheduler(BookingRepository bookingRepository, EmailService emailService) {
        this.bookingRepository = bookingRepository;
        this.emailService = emailService;
    }

    @Scheduled(fixedRate = 60000)
    public void sendUpcomingReminders() {
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();
        LocalTime windowEnd = now.plusMinutes(30);

        List<Booking> todaysBookings = bookingRepository.findActiveBookingsByRoomAndDateAllRooms(today);

        for (Booking b : todaysBookings) {
            if (!b.getStartTime().isBefore(now) && !b.getStartTime().isAfter(windowEnd)) {
                emailService.sendBookingReminder(
                        b.getUser().getEmail(),
                        b.getMeetingTitle(),
                        b.getRoom().getRoomName(),
                        b.getStartTime().toString()
                );
            }
        }
    }
}