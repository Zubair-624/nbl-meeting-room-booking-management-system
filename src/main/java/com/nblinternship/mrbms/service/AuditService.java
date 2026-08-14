package com.nblinternship.mrbms.service;

public interface AuditService {

    // Standard action name constants — use these everywhere instead of typing raw strings
    String ACTION_LOGIN = "User Login";
    String ACTION_LOGOUT = "User Logout";
    String ACTION_BOOKING_CREATED = "Booking Created";
    String ACTION_BOOKING_UPDATED = "Booking Updated";
    String ACTION_BOOKING_CANCELLED = "Booking Cancelled";
    String ACTION_ROOM_CREATED = "Room Created";
    String ACTION_ROOM_UPDATED = "Room Updated";
    String ACTION_USER_REGISTERED = "User Registered";

    // Standard module name constants
    String MODULE_AUTH = "Authentication";
    String MODULE_BOOKING = "Booking Management";
    String MODULE_ROOM = "Meeting Room Management";
    String MODULE_USER = "User Management";

    void log(Integer userId, String action, String module, String ipAddress);
}