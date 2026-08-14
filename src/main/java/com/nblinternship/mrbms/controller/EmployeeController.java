package com.nblinternship.mrbms.controller;

import com.nblinternship.mrbms.entity.Booking;
import com.nblinternship.mrbms.entity.MeetingRoom;
import com.nblinternship.mrbms.entity.User;
import com.nblinternship.mrbms.service.BookingService;
import com.nblinternship.mrbms.service.MeetingRoomService;
import com.nblinternship.mrbms.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.Optional;

@Controller
@RequestMapping("/employee")
public class EmployeeController {

    private final UserService userService;
    private final BookingService bookingService;
    private final MeetingRoomService meetingRoomService;

    public EmployeeController(UserService userService,
                              BookingService bookingService,
                              MeetingRoomService meetingRoomService) {
        this.userService = userService;
        this.bookingService = bookingService;
        this.meetingRoomService = meetingRoomService;
    }

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = getAuthenticatedUser(userDetails);
        if (user == null) return "redirect:/login";

        model.addAttribute("user", user);
        model.addAttribute("bookings", bookingService.getBookingsByUserId(user.getUserId()));
        model.addAttribute("rooms", meetingRoomService.getAllActiveRooms());
        return "employee/dashboard";
    }

    // Available Rooms -> templates/employee/rooms.html
    @GetMapping("/rooms")
    public String viewRooms(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = getAuthenticatedUser(userDetails);
        if (user == null) return "redirect:/login";

        model.addAttribute("user", user);
        model.addAttribute("rooms", meetingRoomService.getAllActiveRooms());
        return "employee/rooms";
    }

    // Booking Form -> templates/employee/booking-form.html
    @GetMapping("/bookings/new")
    public String showBookingForm(@RequestParam("roomId") Integer roomId,
                                  @AuthenticationPrincipal UserDetails userDetails,
                                  Model model,
                                  RedirectAttributes redirectAttributes) {
        User user = getAuthenticatedUser(userDetails);
        if (user == null) return "redirect:/login";

        Optional<MeetingRoom> roomOpt = meetingRoomService.getRoomById(roomId);
        if (roomOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Selected meeting room does not exist.");
            return "redirect:/employee/rooms";
        }

        Booking booking = new Booking();
        booking.setRoom(roomOpt.get());
        booking.setBookingDate(LocalDate.now());

        model.addAttribute("user", user);
        model.addAttribute("booking", booking);
        model.addAttribute("room", roomOpt.get());
        return "employee/booking-form";
    }

    // Create Booking Action
    @PostMapping("/bookings/create")
    public String createBooking(@ModelAttribute("booking") Booking booking,
                                BindingResult result,
                                @RequestParam("roomId") Integer roomId,
                                @AuthenticationPrincipal UserDetails userDetails,
                                RedirectAttributes redirectAttributes) {
        User user = getAuthenticatedUser(userDetails);
        if (user == null) return "redirect:/login";

        Optional<MeetingRoom> roomOpt = meetingRoomService.getRoomById(roomId);
        if (roomOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Selected meeting room does not exist.");
            return "redirect:/employee/rooms";
        }

        if (booking.getStartTime() == null || booking.getEndTime() == null || booking.getBookingDate() == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Booking date, start time, and end time are required.");
            return "redirect:/employee/bookings/new?roomId=" + roomId;
        }

        if (booking.getEndTime().isBefore(booking.getStartTime()) || booking.getEndTime().equals(booking.getStartTime())) {
            redirectAttributes.addFlashAttribute("errorMessage", "End time must be after start time.");
            return "redirect:/employee/bookings/new?roomId=" + roomId;
        }

        booking.setUser(user);
        booking.setRoom(roomOpt.get());

        try {
            bookingService.createBooking(booking);
            redirectAttributes.addFlashAttribute("successMessage", "Meeting room booked successfully!");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/employee/bookings/new?roomId=" + roomId;
        }

        return "redirect:/employee/bookings";
    }

    // Personal Bookings -> templates/employee/my-bookings.html
    @GetMapping("/bookings")
    public String myBookings(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = getAuthenticatedUser(userDetails);
        if (user == null) return "redirect:/login";

        model.addAttribute("user", user);
        model.addAttribute("bookings", bookingService.getBookingsByUserId(user.getUserId()));
        return "employee/my-bookings";
    }

    @GetMapping("/calendar")
    public String calendar(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = getAuthenticatedUser(userDetails);
        if (user == null) return "redirect:/login";

        model.addAttribute("user", user);
        return "employee/calendar";
    }

    @PostMapping("/bookings/{id}/cancel")
    public String cancelBooking(@PathVariable("id") Integer bookingId,   // changed from Long
                                @AuthenticationPrincipal UserDetails userDetails,
                                RedirectAttributes redirectAttributes) {
        User user = getAuthenticatedUser(userDetails);
        if (user == null) return "redirect:/login";

        try {
            bookingService.cancelBooking(bookingId, user.getUserId());
            redirectAttributes.addFlashAttribute("successMessage", "Booking cancelled successfully.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/employee/bookings";
    }

    private User getAuthenticatedUser(UserDetails userDetails) {
        if (userDetails == null) return null;
        return userService.getUserByEmail(userDetails.getUsername()).orElse(null);
    }
}