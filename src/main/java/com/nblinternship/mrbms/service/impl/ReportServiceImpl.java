package com.nblinternship.mrbms.service.impl;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Paragraph;
import com.nblinternship.mrbms.entity.Booking;
import com.nblinternship.mrbms.entity.MeetingRoom;
import com.nblinternship.mrbms.repository.BookingRepository;
import com.nblinternship.mrbms.repository.MeetingRoomRepository;
import com.nblinternship.mrbms.service.ReportService;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportServiceImpl implements ReportService {

    private final BookingRepository bookingRepository;
    private final MeetingRoomRepository meetingRoomRepository;

    public ReportServiceImpl(BookingRepository bookingRepository, MeetingRoomRepository meetingRoomRepository) {
        this.bookingRepository = bookingRepository;
        this.meetingRoomRepository = meetingRoomRepository;
    }

    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setColor(IndexedColors.WHITE.getIndex());
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return headerStyle;
    }

    @Override
    public void exportBookingsExcel(HttpServletResponse response, LocalDate startDate, LocalDate endDate) throws IOException {
        List<Booking> bookings = bookingRepository.findAllWithDetails().stream()
                .filter(b -> !b.getBookingDate().isBefore(startDate) && !b.getBookingDate().isAfter(endDate))
                .sorted((a, b) -> a.getBookingId().compareTo(b.getBookingId()))
                .collect(Collectors.toList());

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Bookings Report");

        CellStyle headerStyle = createHeaderStyle(workbook);

        Row header = sheet.createRow(0);
        String[] columns = {"Booking ID", "Meeting Title", "Room", "Booked By", "Date", "Start Time", "End Time", "Status"};
        for (int i = 0; i < columns.length; i++) {
            org.apache.poi.ss.usermodel.Cell cell = header.createCell(i);
            cell.setCellValue(columns[i]);
            cell.setCellStyle(headerStyle);
        }

        int rowIdx = 1;
        for (Booking b : bookings) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(b.getBookingId());
            row.createCell(1).setCellValue(b.getMeetingTitle());
            row.createCell(2).setCellValue(b.getRoom().getRoomName());
            row.createCell(3).setCellValue(b.getUser().getName());
            row.createCell(4).setCellValue(b.getBookingDate().toString());
            row.createCell(5).setCellValue(b.getStartTime().toString());
            row.createCell(6).setCellValue(b.getEndTime().toString());
            row.createCell(7).setCellValue(b.getStatus());
        }

        for (int i = 0; i < columns.length; i++) {
            sheet.autoSizeColumn(i);
        }

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=bookings-report.xlsx");

        workbook.write(response.getOutputStream());
        workbook.close();
    }

    @Override
    public void exportBookingsPdf(HttpServletResponse response, LocalDate startDate, LocalDate endDate) throws IOException {
        List<Booking> bookings = bookingRepository.findAllWithDetails().stream()
                .filter(b -> !b.getBookingDate().isBefore(startDate) && !b.getBookingDate().isAfter(endDate))
                .sorted((a, b) -> a.getBookingId().compareTo(b.getBookingId()))
                .collect(Collectors.toList());

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=bookings-report.pdf");

        PdfWriter writer = new PdfWriter(response.getOutputStream());
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        document.add(new Paragraph("MRBMS Booking Report").setBold().setFontSize(16));
        document.add(new Paragraph("Period: " + startDate + " to " + endDate).setFontSize(10));
        document.add(new Paragraph(" "));

        com.itextpdf.kernel.colors.Color headerBg = new com.itextpdf.kernel.colors.DeviceRgb(13, 110, 253);

        Table table = new Table(6);
        String[] headers = {"Title", "Room", "Booked By", "Date", "Time", "Status"};
        for (String h : headers) {
            table.addHeaderCell(new Cell().add(new Paragraph(h).setBold()
                            .setFontColor(com.itextpdf.kernel.colors.ColorConstants.WHITE))
                    .setBackgroundColor(headerBg));
        }

        for (Booking b : bookings) {
            table.addCell(b.getMeetingTitle());
            table.addCell(b.getRoom().getRoomName());
            table.addCell(b.getUser().getName());
            table.addCell(b.getBookingDate().toString());
            table.addCell(b.getStartTime() + " - " + b.getEndTime());
            table.addCell(b.getStatus());
        }

        document.add(table);
        document.close();
    }

    @Override
    public void exportRoomUtilizationExcel(HttpServletResponse response) throws IOException {
        List<MeetingRoom> rooms = meetingRoomRepository.findAll();
        List<Booking> allBookings = bookingRepository.findAllWithDetails();

        Map<Integer, Long> bookingCountByRoom = allBookings.stream()
                .collect(Collectors.groupingBy(b -> b.getRoom().getRoomId(), Collectors.counting()));

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Room Utilization");

        CellStyle headerStyle = createHeaderStyle(workbook);

        Row header = sheet.createRow(0);
        String[] columns = {"Room Name", "Room Code", "Total Bookings"};
        for (int i = 0; i < columns.length; i++) {
            org.apache.poi.ss.usermodel.Cell cell = header.createCell(i);
            cell.setCellValue(columns[i]);
            cell.setCellStyle(headerStyle);
        }

        int rowIdx = 1;
        for (MeetingRoom room : rooms) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(room.getRoomName());
            row.createCell(1).setCellValue(room.getRoomCode());
            row.createCell(2).setCellValue(bookingCountByRoom.getOrDefault(room.getRoomId(), 0L));
        }

        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
        sheet.autoSizeColumn(2);

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=room-utilization-report.xlsx");

        workbook.write(response.getOutputStream());
        workbook.close();
    }


    @Override
    public void viewBookingsPdf(HttpServletResponse response, LocalDate startDate, LocalDate endDate) throws IOException {
        List<Booking> bookings = bookingRepository.findAllWithDetails().stream()
                .filter(b -> !b.getBookingDate().isBefore(startDate) && !b.getBookingDate().isAfter(endDate))
                .sorted((a, b) -> a.getBookingId().compareTo(b.getBookingId()))
                .collect(Collectors.toList());

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "inline; filename=bookings-report.pdf");

        PdfWriter writer = new PdfWriter(response.getOutputStream());
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        document.add(new Paragraph("MRBMS Booking Report").setBold().setFontSize(16));
        document.add(new Paragraph("Period: " + startDate + " to " + endDate).setFontSize(10));
        document.add(new Paragraph(" "));

        com.itextpdf.kernel.colors.Color headerBg = new com.itextpdf.kernel.colors.DeviceRgb(13, 110, 253);

        Table table = new Table(6);
        String[] headers = {"Title", "Room", "Booked By", "Date", "Time", "Status"};
        for (String h : headers) {
            table.addHeaderCell(new Cell().add(new Paragraph(h).setBold()
                            .setFontColor(com.itextpdf.kernel.colors.ColorConstants.WHITE))
                    .setBackgroundColor(headerBg));
        }

        for (Booking b : bookings) {
            table.addCell(b.getMeetingTitle());
            table.addCell(b.getRoom().getRoomName());
            table.addCell(b.getUser().getName());
            table.addCell(b.getBookingDate().toString());
            table.addCell(b.getStartTime() + " - " + b.getEndTime());
            table.addCell(b.getStatus());
        }

        document.add(table);
        document.close();
    }
}