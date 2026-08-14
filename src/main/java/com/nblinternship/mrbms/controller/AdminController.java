package com.nblinternship.mrbms.controller;

import com.nblinternship.mrbms.entity.Booking;
import com.nblinternship.mrbms.entity.MeetingRoom;
import com.nblinternship.mrbms.service.BookingService;
import com.nblinternship.mrbms.service.MeetingRoomService;
import com.nblinternship.mrbms.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final MeetingRoomService meetingRoomService;
    private final UserService userService;
    private final BookingService bookingService;

    public AdminController(MeetingRoomService meetingRoomService,
                           UserService userService,
                           BookingService bookingService) {
        this.meetingRoomService = meetingRoomService;
        this.userService = userService;
        this.bookingService = bookingService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("totalRooms", meetingRoomService.getRoomCount());
        model.addAttribute("totalUsers", userService.getUserCount());
        model.addAttribute("totalBookings", bookingService.getBookingCount());
        model.addAttribute("recentBookings", bookingService.getAllBookings());
        return "admin-dashboard";
    }

    @GetMapping("/rooms/{id}/edit")
    public String showEditRoomForm(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        return meetingRoomService.getRoomById(id)
                .map(room -> {
                    model.addAttribute("room", room);
                    return "admin-rooms-form";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("errorMessage", "Room not found.");
                    return "redirect:/admin/rooms";
                });
    }

    @PostMapping("/rooms/{id}/delete")
    public String deleteRoom(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        meetingRoomService.deleteRoomById(id);
        redirectAttributes.addFlashAttribute("successMessage", "Room deleted successfully.");
        return "redirect:/admin/rooms";
    }

    @PostMapping("/rooms/{id}/save")
    public String updateRoom(@PathVariable("id") Integer id, @ModelAttribute("room") MeetingRoom room, RedirectAttributes redirectAttributes) {
        room.setRoomId(id);
        meetingRoomService.saveRoom(room);
        redirectAttributes.addFlashAttribute("successMessage", "Meeting room updated successfully.");
        return "redirect:/admin/rooms";
    }

    @GetMapping("/dashboard/chart-data")
    @ResponseBody
    public Map<String, Object> chartData() {
        List<Booking> allBookings = bookingService.getAllBookings();

        Map<String, Long> monthlyStats = allBookings.stream()
                .collect(Collectors.groupingBy(
                        b -> b.getBookingDate().getMonth().toString(),
                        Collectors.counting()));

        Map<String, Long> roomUtilization = allBookings.stream()
                .collect(Collectors.groupingBy(
                        b -> b.getRoom().getRoomName(),
                        Collectors.counting()));

        Map<Integer, Long> peakHours = allBookings.stream()
                .collect(Collectors.groupingBy(
                        b -> b.getStartTime().getHour(),
                        Collectors.counting()));

        Map<String, Object> result = new HashMap<>();
        result.put("monthlyLabels", monthlyStats.keySet());
        result.put("monthlyData", monthlyStats.values());
        result.put("roomLabels", roomUtilization.keySet());
        result.put("roomData", roomUtilization.values());
        result.put("hourLabels", peakHours.keySet());
        result.put("hourData", peakHours.values());

        return result;
    }

    @GetMapping("/rooms")
    public String listRooms(Model model) {
        model.addAttribute("rooms", meetingRoomService.getAllRooms());
        return "admin-rooms-list";
    }

    @GetMapping("/rooms/new")
    public String showAddRoomForm(Model model) {
        model.addAttribute("room", new MeetingRoom());
        return "admin-rooms-form";
    }

    @PostMapping("/rooms/save")
    public String saveRoom(@ModelAttribute("room") MeetingRoom room, RedirectAttributes redirectAttributes) {
        meetingRoomService.saveRoom(room);
        redirectAttributes.addFlashAttribute("successMessage", "Meeting room saved successfully.");
        return "redirect:/admin/rooms";
    }

//    @GetMapping("/users")
//    public String listUsers(Model model) {
//        model.addAttribute("users", userService.getAllUsers());
//        return "admin-users-list";
//    }

    @GetMapping("/bookings")
    public String listBookings(Model model) {
        model.addAttribute("bookings", bookingService.getAllBookings());
        return "admin-bookings-list";
    }

    @PostMapping("/bookings/{id}/status")
    public String updateBookingStatus(@PathVariable("id") Integer id,
                                      @RequestParam("status") String status,
                                      RedirectAttributes redirectAttributes) {
        try {
            bookingService.updateBookingStatus(id, status);
            redirectAttributes.addFlashAttribute("successMessage", "Booking status updated to " + status);
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/bookings";
    }
}