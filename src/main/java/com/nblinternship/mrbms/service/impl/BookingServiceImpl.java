package com.nblinternship.mrbms.service.impl;

import com.nblinternship.mrbms.entity.Booking;
import com.nblinternship.mrbms.repository.BookingRepository;
import com.nblinternship.mrbms.service.AuditService;
import com.nblinternship.mrbms.service.BookingService;
import com.nblinternship.mrbms.service.EmailService;
import com.nblinternship.mrbms.util.RequestUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final AuditService auditService;
    private final EmailService emailService;

    public BookingServiceImpl(BookingRepository bookingRepository, AuditService auditService, EmailService emailService) {
        this.bookingRepository = bookingRepository;
        this.auditService = auditService;
        this.emailService = emailService;
    }

    @Override
    public List<Booking> getAllBookings() {
        return bookingRepository.findAllWithDetails();
    }

    @Override
    public List<Booking> getBookingsByUserId(Integer userId) {
        return bookingRepository.findByUserUserId(userId);
    }

    @Override
    public List<Booking> getActiveBookingsByRoomAndDate(Integer roomId, LocalDate date) {
        return bookingRepository.findActiveBookingsByRoomAndDate(roomId, date);
    }

    @Override
    public Optional<Booking> getBookingById(Integer id) {
        return bookingRepository.findById(id);
    }

    @Override
    public long getBookingCount() {
        return bookingRepository.count();
    }

    @Override
    public boolean isRoomAvailable(Integer roomId, LocalDate date, LocalTime startTime, LocalTime endTime) {
        return !bookingRepository.existsOverlappingBooking(roomId, date, startTime, endTime);
    }

    @Override
    @Transactional
    public void cancelBooking(Integer bookingId, Integer userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found with ID: " + bookingId));

        if (!booking.getUser().getUserId().equals(userId)) {
            throw new IllegalStateException("You are not authorized to cancel this booking.");
        }

        if ("Cancelled".equalsIgnoreCase(booking.getStatus())) {
            throw new IllegalStateException("This booking is already cancelled.");
        }

        booking.setStatus("Cancelled");
        bookingRepository.save(booking);

        auditService.log(userId, AuditService.ACTION_BOOKING_CANCELLED, AuditService.MODULE_BOOKING, RequestUtil.getClientIp());

        emailService.sendBookingCancellation(
                booking.getUser().getEmail(),
                booking.getMeetingTitle(),
                booking.getRoom().getRoomName(),
                booking.getBookingDate().toString(),
                booking.getStartTime() + " - " + booking.getEndTime()
        );
    }

    @Override
    @Transactional
    public Booking createBooking(Booking booking) {
        if (!isRoomAvailable(booking.getRoom().getRoomId(),
                booking.getBookingDate(),
                booking.getStartTime(),
                booking.getEndTime())) {
            throw new IllegalArgumentException("The selected meeting room is already booked for this time slot.");
        }

        Booking savedBooking = bookingRepository.save(booking);

        auditService.log(savedBooking.getUser().getUserId(), AuditService.ACTION_BOOKING_CREATED, AuditService.MODULE_BOOKING, RequestUtil.getClientIp());

        emailService.sendBookingConfirmation(
                savedBooking.getUser().getEmail(),
                savedBooking.getMeetingTitle(),
                savedBooking.getRoom().getRoomName(),
                savedBooking.getBookingDate().toString(),
                savedBooking.getStartTime() + " - " + savedBooking.getEndTime()
        );

        return savedBooking;
    }

    @Override
    @Transactional
    public Booking updateBookingStatus(Integer bookingId, String status) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found with ID: " + bookingId));

        booking.setStatus(status);
        Booking updatedBooking = bookingRepository.save(booking);

        auditService.log(updatedBooking.getUser().getUserId(), AuditService.ACTION_BOOKING_UPDATED, AuditService.MODULE_BOOKING, RequestUtil.getClientIp());

        return updatedBooking;
    }
}