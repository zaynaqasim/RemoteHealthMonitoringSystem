/**
 * The ReminderService class handles automated reminders for appointments and medications
 * in the Remote Health Monitoring System. It coordinates with the EmailNotification
 * service to send timely reminders to patients about upcoming appointments and
 * medication schedules.
 * 
 * @author 
 * @version 1.0
 */
package com.remotehealth.app.service;

import com.remotehealth.app.model.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class ReminderService {
    // Service dependencies
    private final EmailNotification emailService;
    private final AppointmentManager appointmentManager;
    private final DatabaseManager dbManager;
    
    // Configuration constants
    private final int REMINDER_HOUR = 9; // Default reminder time (9 AM)

    /**
     * Constructs a ReminderService with required dependencies.
     * 
     * @param emailService The email notification service
     * @param appointmentManager The appointment management service
     * @param dbManager The database access manager
     */
    public ReminderService(EmailNotification emailService, AppointmentManager appointmentManager, DatabaseManager dbManager) {
        this.emailService = emailService;
        this.appointmentManager = appointmentManager;
        this.dbManager = dbManager;
    }

    /**
     * Sends appointment reminders to a patient for upcoming appointments.
     * Filters appointments that are approved and scheduled for today or tomorrow.
     * 
     * @param patient The patient to send reminders to
     */
    public void sendAppointmentReminders(Patient patient) {
        try {
            List<Appointment> upcomingAppointments = appointmentManager
                .findAppointmentsByPatientID(patient.getId())
                .stream()
                .filter(appt -> "APPROVED".equals(appt.getStatus()))
                .filter(appt -> isTodayOrTomorrow(appt.getDateTime().toLocalDate()))
                .collect(Collectors.toList());

            if (!upcomingAppointments.isEmpty()) {
                String body = buildAppointmentReminderBody(patient, upcomingAppointments);
                emailService.sendNotification(
                    patient.getEmail(),
                    "Upcoming Appointment Reminder\n" + body
                );
            }
        } catch (Exception e) {
            System.err.println("Error sending appointment reminders: " + e.getMessage());
        }
    }

    /**
     * Sends notification when an appointment is approved.
     * 
     * @param appointment The approved appointment
     */
    public void sendAppointmentApprovalNotification(Appointment appointment) {
        try {
            String body = "\n\nYour appointment has been approved:\n\n" +
                         "Doctor: Dr. " + appointment.getDoctor().getName() + "\n" +
                         "Date: " + appointment.getDateTime().toLocalDate() + "\n" +
                         "Time: " + appointment.getDateTime().toLocalTime() + "\n\n" +
                         "You will receive reminders as the appointment date approaches.";

            emailService.sendNotification(
                appointment.getPatient().getEmail(),
                "Appointment Approved\n" + body
            );
        } catch (Exception e) {
            System.err.println("Error sending approval notification: " + e.getMessage());
        }
    }

    /**
     * Sends medication reminders to a patient based on active prescriptions.
     * Filters prescriptions that are active and due for reminder at configured hour.
     * 
     * @param patient The patient to send reminders to
     */
    public void sendPrescriptionReminders(Patient patient) {
        try {
            List<Prescription> activePrescriptions = dbManager.getPrescriptionsForPatient(patient.getId())
                .stream()
                .filter(this::isPrescriptionActive)
                .filter(this::isTimeForReminder)
                .collect(Collectors.toList());

            if (!activePrescriptions.isEmpty()) {
                String body = buildPrescriptionReminderBody(patient, activePrescriptions);
                emailService.sendNotification(
                    patient.getEmail(),
                    "Medication Reminder\n" + body
                );
            }
        } catch (Exception e) {
            System.err.println("Error sending prescription reminders: " + e.getMessage());
        }
    }

    /**
     * Sends all automatic reminders (both appointments and medications) to a patient.
     * 
     * @param patient The patient to send reminders to
     */
    public void sendAllAutomaticReminders(Patient patient) {
        sendPrescriptionReminders(patient);
        sendAppointmentReminders(patient);
    }

    // ========== PRIVATE HELPER METHODS ==========

    /**
     * Builds the email body for appointment reminders.
     * 
     * @param patient The patient receiving the reminder
     * @param appointments List of upcoming appointments
     * @return Formatted email body content
     */
    private String buildAppointmentReminderBody(Patient patient, List<Appointment> appointments) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n\nHello ").append(patient.getName()).append(",\n\n");
        sb.append("You have the following upcoming appointments:\n\n");

        for (Appointment appt : appointments) {
            sb.append("- ")
              .append(formatAppointmentDateTime(appt.getDateTime()))
              .append(" with Dr. ")
              .append(appt.getDoctor().getName())
              .append("\n\n");
        }

        sb.append("\nPlease arrive 15 minutes early.\n");
        return sb.toString();
    }

    /**
     * Builds the email body for prescription reminders.
     * 
     * @param patient The patient receiving the reminder
     * @param prescriptions List of active prescriptions
     * @return Formatted email body content
     */
    private String buildPrescriptionReminderBody(Patient patient, List<Prescription> prescriptions) {
        StringBuilder sb = new StringBuilder();
        sb.append("Hello ").append(patient.getName()).append(",\n\n");
        sb.append("Medication Reminders for ").append(LocalDate.now()).append(":\n\n");

        prescriptions.forEach(p ->
            sb.append("➔ ").append(p.getMedication()).append("\n")
              .append("   Dosage: ").append(p.getDosage()).append("\n")
              .append("   Time: ").append(p.getSchedule()).append("\n")
              .append("   Prescribed by: Dr. ").append(p.getPrescribingDoctor()).append("\n\n")
        );

        sb.append("\nHave a healthy day!");
        return sb.toString();
    }

    /**
     * Formats appointment date/time for display.
     * 
     * @param dateTime The appointment date/time
     * @return Formatted string representation
     */
    private String formatAppointmentDateTime(LocalDateTime dateTime) {
        return dateTime.format(DateTimeFormatter.ofPattern("EEEE, MMMM d 'at' h:mm a"));
    }

    /**
     * Checks if a date is today or tomorrow.
     * 
     * @param date The date to check
     * @return true if date is today or tomorrow
     */
    private boolean isTodayOrTomorrow(LocalDate date) {
        LocalDate today = LocalDate.now();
        return date.equals(today) || date.equals(today.plusDays(1));
    }

    /**
     * Checks if a prescription is currently active.
     * Currently returns true for all prescriptions (placeholder implementation).
     * 
     * @param p The prescription to check
     * @return true if prescription is active
     */
    private boolean isPrescriptionActive(Prescription p) {
        return true;
    }

    /**
     * Checks if it's time to send a reminder based on configured hour.
     * 
     * @param p The prescription to check
     * @return true if current hour matches reminder hour
     */
    private boolean isTimeForReminder(Prescription p) {
        return LocalTime.now().getHour() == REMINDER_HOUR;
    }
}