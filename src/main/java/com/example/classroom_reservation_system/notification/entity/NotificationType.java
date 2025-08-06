package com.example.classroom_reservation_system.notification.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NotificationType {

    RESERVATION_EVENT("/history"),
    CHANGE_REQUEST_RECEIVED("/admin/requests"),
    CHANGE_REQUEST_PROCESSED("/history");

    private final String destinationPath;

}
