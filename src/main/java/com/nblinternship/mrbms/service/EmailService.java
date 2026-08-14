package com.nblinternship.mrbms.service;

public interface EmailService {
    void sendBookingConfirmation(String toEmail, String meetingTitle, String roomName, String date, String time);
    void sendBookingCancellation(String toEmail, String meetingTitle, String roomName, String date, String time);
    void sendBookingReminder(String toEmail, String meetingTitle, String roomName, String time);
}