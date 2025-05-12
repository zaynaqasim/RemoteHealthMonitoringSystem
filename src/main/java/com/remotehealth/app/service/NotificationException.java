package com.remotehealth.app.service;

/**
 * Exception for notification failures
 */
public class NotificationException extends Exception {
    public NotificationException(String message) {
        super(message);
    }

    public NotificationException(String message, Throwable cause) {
        super(message, cause);
    }
}
