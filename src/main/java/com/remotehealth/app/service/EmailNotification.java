/**
 * The EmailNotification class handles all email communications for the Remote Health
 * Monitoring System. It implements retry logic for failed sends and provides
 * comprehensive error handling for SMTP operations.
 * 
 * @author 
 * @version 1.0
 */
package com.remotehealth.app.service;

import jakarta.mail.Authenticator;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EmailNotification implements NotificationService {
    
    // Logger for tracking email operations
    private static final Logger logger = Logger.getLogger(EmailNotification.class.getName());
    
    // Configuration constants
    private static final int MAX_RETRIES = 3;
    private static final long RETRY_DELAY_MS = 2000;
    private static final String SMTP_USERNAME = "healthmonitoringsystemDS2A@gmail.com";
    private static final String SMTP_PASSWORD = "vmmp kqpp nfyr byqe"; // Use app-specific password
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final int SMTP_PORT = 587;
    
    /**
     * Sends a notification email with retry logic.
     * Implements NotificationService interface method.
     * 
     * @param to Recipient email address
     * @param message Email content
     */
    @Override
    public void sendNotification(String to, String message) {
        sendEmail(to, "Health Monitoring Notification", message);
    }

    /**
     * Main email sending method with retry logic.
     * 
     * @param toEmail Recipient email address
     * @param subject Email subject
     * @param body Email content
     */
    public void sendEmail(String toEmail, String subject, String body) {
        int attempt = 0;
        boolean sentSuccessfully = false;
        Exception lastException = null;
        
        while (attempt < MAX_RETRIES && !sentSuccessfully) {
            attempt++;
            try {
                if (attempt > 1) {
                    logger.info("Retry attempt " + attempt + " for email to: " + toEmail);
                    Thread.sleep(RETRY_DELAY_MS);
                }
                
                internalSendEmail(toEmail, subject, body);
                sentSuccessfully = true;
                logger.info("Email successfully sent to: " + toEmail);
                
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                logger.log(Level.WARNING, "Email sending interrupted during retry delay", ie);
                break;
            } catch (EmailException e) {
                lastException = e;
                logger.log(Level.WARNING, "Email attempt " + attempt + " failed for " + toEmail, e);
            }
        }
        
        if (!sentSuccessfully) {
            String errorMsg = "Failed to send email to " + toEmail + " after " + MAX_RETRIES + " attempts";
            logger.log(Level.SEVERE, errorMsg, lastException);
            throw new EmailSendingException(errorMsg, lastException);
        }
    }
    
    /**
     * Internal email sending implementation with SMTP.
     * 
     * @param toEmail Recipient email address
     * @param subject Email subject
     * @param body Email content
     * @throws EmailException if sending fails
     */
    private void internalSendEmail(String toEmail, String subject, String body) throws EmailException {
        try {
            // Validate inputs
            if (toEmail == null || toEmail.trim().isEmpty()) {
                throw new IllegalArgumentException("Recipient email cannot be null or empty");
            }
            if (subject == null || subject.trim().isEmpty()) {
                throw new IllegalArgumentException("Email subject cannot be null or empty");
            }
            
            // Configure SMTP properties
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", SMTP_HOST);
            props.put("mail.smtp.port", SMTP_PORT);
            props.put("mail.smtp.connectiontimeout", "5000"); // 5 seconds
            props.put("mail.smtp.timeout", "5000"); // 5 seconds
            
            // Create authenticated session
            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(SMTP_USERNAME, SMTP_PASSWORD);
                }
            });

            // Build and send message
            MimeMessage emailMessage = new MimeMessage(session);
            emailMessage.setFrom(new InternetAddress(SMTP_USERNAME));
            emailMessage.setRecipients(jakarta.mail.Message.RecipientType.TO, InternetAddress.parse(toEmail));
            emailMessage.setSubject(subject);
            emailMessage.setText(body);
            
            Transport.send(emailMessage);
            
        } catch (MessagingException e) {
            throw new EmailException("Failed to send email due to messaging error", e);
        } catch (Exception e) {
            throw new EmailException("Unexpected error while sending email", e);
        }
    }
    
    /**
     * Custom exception for email-related errors.
     */
    public static class EmailException extends Exception {
        public EmailException(String message, Throwable cause) {
            super(message, cause);
        }
    }
    
    /**
     * Runtime exception for failed email sending after retries.
     */
    public static class EmailSendingException extends RuntimeException {
        public EmailSendingException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}