package com.nblinternship.mrbms.service;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;

public interface ReportService {
    void exportBookingsExcel(HttpServletResponse response, LocalDate startDate, LocalDate endDate) throws IOException;
    void exportBookingsPdf(HttpServletResponse response, LocalDate startDate, LocalDate endDate) throws IOException;
    void exportRoomUtilizationExcel(HttpServletResponse response) throws IOException;
}