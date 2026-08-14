package com.nblinternship.mrbms.controller;

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

@Controller
@RequestMapping("/admin/reports")
public class AdminReportController {

    private final ReportService reportService;

    public AdminReportController(ReportService reportService) {
        this.reportService = reportService;
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