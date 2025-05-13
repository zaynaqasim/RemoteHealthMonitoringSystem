/**
 * The AppointmentManager class handles all appointment-related operations in the
 * Remote Health Monitoring System, including scheduling, approval, conflict
 * detection, and coordination with reminder services.
 * 
 * @author 
 * @version 1.0
 */
package com.remotehealth.app.service;

import com.remotehealth.app.model.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AppointmentManager {
    // Service dependencies
    private ReminderService reminderService;
    private DatabaseManager dbManager;

    // === Constructors ===
    
    /**
     * Constructs an AppointmentManager with database access.
     * 
     * @param dbManager The database manager for appointment records
     */
    public AppointmentManager(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    // === Core Appointment Operations ===
    
    /**
     * Requests a new appointment, checking for conflicts first.
     * 
     * @param appointment The appointment to request
     */
    public void requestAppointment(Appointment appointment) {
        // First validate the appointment
        if (appointment.getDoctor() == null || appointment.getPatient() == null) {
            throw new IllegalArgumentException("Appointment must have both doctor and patient");
        }
    
        if (hasConflict(appointment, null)) {
           System.out.println("[ERROR] Appointment conflict detected.");
        } else {
           appointment.setStatus("Pending");
           dbManager.saveAppointment(appointment);
           System.out.println("[INFO] Appointment requested successfully for " + appointment.getPatient().getName());
       }
    }

    /**
     * Approves an appointment, checking for conflicts at the new time.
     * 
     * @param appointment The appointment to approve
     * @param dateTime The proposed date/time for the appointment
     * @throws IllegalStateException if time slot is not available
     */
    public void approveAppointment(Appointment appointment, LocalDateTime dateTime) {
        Appointment tempAppt = new Appointment(dateTime, appointment.getPatient(), appointment.getDoctor());
        
        if (hasConflict(tempAppt, appointment)) {
            System.out.println("[ERROR] Time slot not available");
            throw new IllegalStateException("Time slot not available");
        }
        
        appointment.setDateTime(dateTime);
        appointment.setStatus("Approved");
        updateAppointmentInDatabase(appointment);
        System.out.println("[INFO] Appointment approved for " + appointment.getPatient().getName());
        
        // Send notifications if reminder service is available
        if (reminderService != null) {
            reminderService.sendAppointmentApprovalNotification(appointment);
            reminderService.sendAppointmentReminders(appointment.getPatient());
        }
    }

    /**
     * Checks for scheduling conflicts with existing appointments.
     * 
     * @param newAppt The new appointment to check
     * @param toExclude An existing appointment to exclude from conflict check
     * @return true if a conflict exists
     */
    private boolean hasConflict(Appointment newAppt, Appointment toExclude) {
        if (newAppt.getDoctor() == null) {
        return false; 
        }
        ArrayList<Appointment> doctorAppointments = dbManager.getAppointmentsForDoctor(newAppt.getDoctor().getName());
        
        for (Appointment existing : doctorAppointments) {
            // Skip the appointment we're trying to modify
            if (toExclude != null &&
                existing.getDateTime().equals(toExclude.getDateTime()) &&
                existing.getPatient().getId().equals(toExclude.getPatient().getId()) &&
                existing.getDoctor().getName().equals(toExclude.getDoctor().getName())) {
                continue;
            }
            
            // Only check approved or pending appointments
            if (!existing.getStatus().equals("Approved") && !existing.getStatus().equals("Pending")) {
                continue;
            }
            
            // Check time overlap (assuming 30-minute appointments)
            LocalDateTime existingStart = existing.getDateTime();
            LocalDateTime existingEnd = existingStart.plusMinutes(30);
            LocalDateTime newStart = newAppt.getDateTime();
            LocalDateTime newEnd = newStart.plusMinutes(30);
            
            if (newStart.isBefore(existingEnd) && newEnd.isAfter(existingStart)) {
                return true; // Conflict found
            }
        }
        return false; // No conflict
    }

    /**
     * Rejects an appointment request.
     * 
     * @param appointment The appointment to reject
     */
    public void rejectAppointment(Appointment appointment) {
        appointment.setStatus("Rejected");
        updateAppointmentInDatabase(appointment);
        System.out.println("[INFO] Appointment rejected for " + appointment.getPatient().getName());
    }

    /**
     * Reschedules an appointment to a new time, checking for conflicts.
     * 
     * @param appointment The appointment to reschedule
     * @param newDateTime The proposed new date/time
     */
    public void rescheduleAppointment(Appointment appointment, LocalDateTime newDateTime) {
        Appointment temp = new Appointment(newDateTime, appointment.getPatient(), appointment.getDoctor());
        if (hasConflict(temp, appointment)) {
            System.out.println("[ERROR] Appointment conflict detected.");
        } else {
            appointment.setDateTime(newDateTime);
            updateAppointmentInDatabase(appointment);
            System.out.println("[INFO] Appointment rescheduled to: " + newDateTime);
        }
    }
    
    /**
     * Sets the reminder service dependency.
     * 
     * @param reminderService The reminder service to use
     */
    public void setReminderService(ReminderService reminderService) {
        this.reminderService = reminderService;
    }

    // === Query Methods ===
    
    /**
     * Finds all appointments for a specific patient.
     * 
     * @param patientID The patient's ID
     * @return List of the patient's appointments
     */
    public List<Appointment> findAppointmentsByPatientID(String patientID) {
        return dbManager.getAppointmentsForPatient(patientID);
    }

    // === Helper Methods ===
    
    /**
     * Updates an appointment in the database.
     * Currently implemented as delete+insert due to DB manager limitations.
     * 
     * @param appointment The appointment to update
     */
    private void updateAppointmentInDatabase(Appointment appointment) {
        // Since DatabaseManager doesn't have an update method, we'll delete and re-insert
        dbManager.deleteAppointmentsByPatientId(appointment.getPatient().getId());
        dbManager.saveAppointment(appointment);
    }
}