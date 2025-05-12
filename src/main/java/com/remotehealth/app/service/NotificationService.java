package com.remotehealth.app.service;

/**
 * Base interface for all notification services
 */
interface NotificationService {
    void sendNotification(String to, String message) throws NotificationException;
}


