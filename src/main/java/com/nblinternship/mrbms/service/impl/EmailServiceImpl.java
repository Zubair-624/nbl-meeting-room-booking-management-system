package com.nblinternship.mrbms.service.impl;

import com.nblinternship.mrbms.service.EmailService;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendBookingConfirmation(String toEmail, String meetingTitle, String roomName, String date, String time) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject("Booking Confirmed: " + meetingTitle);
            message.setText("Your meeting room booking has been confirmed.\n\n" +
                    "Meeting: " + meetingTitle + "\n" +
                    "Room: " + roomName + "\n" +
                    "Date: " + date + "\n" +
                    "Time: " + time + "\n\n" +
                    "- MRBMS Notification System");
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Failed to send confirmation email: " + e.getMessage());
        }
    }

    @Override
    public void sendBookingCancellation(String toEmail, String meetingTitle, String roomName, String date, String time) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject("Booking Cancelled: " + meetingTitle);
            message.setText("Your meeting room booking has been cancelled.\n\n" +
                    "Meeting: " + meetingTitle + "\n" +
                    "Room: " + roomName + "\n" +
                    "Date: " + date + "\n" +
                    "Time: " + time + "\n\n" +
                    "- MRBMS Notification System");
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Failed to send cancellation email: " + e.getMessage());
        }
    }

    @Override
    public void sendBookingReminder(String toEmail, String meetingTitle, String roomName, String time) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject("Reminder: " + meetingTitle + " starts in 30 minutes");
            message.setText("This is a reminder for your upcoming meeting.\n\n" +
                    "Meeting: " + meetingTitle + "\n" +
                    "Room: " + roomName + "\n" +
                    "Time: " + time + "\n\n" +
                    "- MRBMS Notification System");
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Failed to send reminder email: " + e.getMessage());
        }
    }
}