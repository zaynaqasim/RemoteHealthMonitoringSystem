/**
 * The Appointment class represents a scheduled medical consultation between
 * a patient and doctor in the Remote Health Monitoring System. It tracks
 * the date/time, participants, and current status of the appointment.
 * 
 * @author 
 * @version 1.0
 */
package com.remotehealth.app.model;

import java.time.LocalDateTime;

public class Appointment {
    // Appointment details
    private LocalDateTime dateTime;  // Scheduled date and time
    private Patient patient;         // Patient attending
    private Doctor doctor;           // Doctor conducting
    private String status;           // Current status (PENDING/APPROVED/etc)

    /**
     * Constructs a new Appointment with default PENDING status.
     * 
     * @param dateTime Scheduled date and time
     * @param patient Patient attending
     * @param doctor Doctor conducting
     */
    public Appointment(LocalDateTime dateTime, Patient patient, Doctor doctor) {
        this.dateTime = dateTime;
        this.patient = patient;
        this.doctor = doctor;
        this.status = "PENDING";
    }

    // ========== GETTER/SETTER METHODS ==========

    /**
     * @return Scheduled date and time
     */
    public LocalDateTime getDateTime() {
        return dateTime;
    }

    /**
     * @return Patient attending
     */
    public Patient getPatient() {
        return patient;
    }

    /**
     * @return Doctor conducting
     */
    public Doctor getDoctor() {
        return doctor;
    }

    /**
     * @return Current appointment status
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the appointment status.
     * 
     * @param status New status (PENDING/APPROVED/etc)
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Reschedules the appointment to a new date/time.
     * 
     * @param dateTime New scheduled date and time
     */
    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    /**
     * @return Date portion of appointment as string
     */
    public String getDate() {
        return dateTime.toLocalDate().toString();
    }

    /**
     * Returns a formatted string representation of the appointment.
     * 
     * @return Formatted appointment details
     */
    @Override
    public String toString() {
        return String.format("Appointment with Dr. %s\nDate: %s\nTime: %s\nStatus: %s",
                doctor.getName(),
                dateTime.toLocalDate(),
                dateTime.toLocalTime(),
                status);
    }
}