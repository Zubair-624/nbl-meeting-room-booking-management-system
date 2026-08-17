package com.nblinternship.mrbms.controller;

import com.nblinternship.mrbms.entity.Booking;
import com.nblinternship.mrbms.service.ReportService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import com.nblinternship.mrbms.entity.Booking;
import com.nblinternship.mrbms.repository.BookingRepository;
import org.springframework.ui.Model;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/reports")
public class AdminReportController {

    private final ReportService reportService;

    private final BookingRepository bookingRepository;

    public AdminReportController(ReportService reportService, BookingRepository bookingRepository) {
        this.reportService = reportService;
        this.bookingRepository = bookingRepository;
    }

    @GetMapping
    public String reportsPage() {
        return "admin-reports";
    }

    @GetMapping("/bookings/excel")
    public void bookingsExcel(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                              @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                              HttpServletResponse response) throws IOException {
        reportService.exportBookingsExcel(response, startDate, endDate);
    }

    @GetMapping("/bookings/view-pdf")
    public void viewBookingsPdf(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                                HttpServletResponse response) throws IOException {
        reportService.viewBookingsPdf(response, startDate, endDate);
    }


    @GetMapping("/bookings/view")
    public String viewBookingsTable(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                    @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                                    Model model) {
        List<Booking> bookings = bookingRepository.findAllWithDetails().stream()
                .filter(b -> !b.getBookingDate().isBefore(startDate) && !b.getBookingDate().isAfter(endDate))
                .sorted((a, b) -> a.getBookingId().compareTo(b.getBookingId()))
                .collect(Collectors.toList());

        model.addAttribute("bookings", bookings);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        return "admin-report-view";
    }


    @GetMapping("/bookings/pdf")
    public void bookingsPdf(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                            HttpServletResponse response) throws IOException {
        reportService.exportBookingsPdf(response, startDate, endDate);
    }

    @GetMapping("/room-utilization/excel")
    public void roomUtilizationExcel(HttpServletResponse response) throws IOException {
        reportService.exportRoomUtilizationExcel(response);
    }
}