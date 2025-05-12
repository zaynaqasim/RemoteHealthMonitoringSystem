/**
 * The Doctor class represents a medical professional user in the Remote Health Monitoring System.
 * It extends the base User class and adds doctor-specific functionality including patient management,
 * appointment scheduling, and prescription capabilities.
 * 
 * @author 
 * @version 1.0
 */
package com.remotehealth.app.model;

import com.remotehealth.app.service.*;
import java.util.ArrayList;
import java.time.LocalDateTime;

public class Doctor extends User {
    // Authentication credentials
    private String password;

    // ========== CONSTRUCTORS ==========

    /**
     * Constructs a new Doctor with full credentials.
     * 
     * @param id Unique doctor ID
     * @param name Full name
     * @param email Email address
     * @param password Authentication password
     */
    public Doctor(String id, String name, String email, String password) {
        super(id, name, email);
        this.password = password;
    }

    /**
     * Constructs a Doctor for database retrieval (without password).
     * 
     * @param id Unique doctor ID
     * @param name Full name
     * @param email Email address
     */
    public Doctor(String id, String name, String email) {
        super(id, name, email);
    }

    /**
     * Default constructor for framework compatibility.
     */
    public Doctor() {
        super();
    }

    // ========== AUTHENTICATION METHODS ==========

    /**
     * Authenticates the doctor against provided credentials.
     * 
     * @param inputPassword Password to verify
     * @return true if password matches
     */
    public boolean authenticate(String inputPassword) {
        return this.password != null && inputPassword != null &&
               this.password.trim().equals(inputPassword.trim());
    }

    // ========== APPOINTMENT MANAGEMENT ==========

    /**
     * Schedules a new appointment via the database manager.
     * 
     * @param appointment Appointment details
     * @param dbManager Database access manager
     */
    public void addAppointment(Appointment appointment, DatabaseManager dbManager) {
        dbManager.saveAppointment(appointment);
    }

    /**
     * Retrieves all appointments for this doctor.
     * 
     * @param dbManager Database access manager
     * @return ArrayList of Appointment objects
     */
    public ArrayList<Appointment> getAppointments(DatabaseManager dbManager) {
        return dbManager.getAppointmentsForDoctor(this.getName());
    }

    // ========== PATIENT INTERACTION METHODS ==========

    /**
     * Provides medical feedback to a patient.
     * 
     * @param patient Patient receiving feedback
     * @param comment Feedback content
     * @param dbManager Database access manager
     */
    public void addFeedback(Patient patient, String comment, DatabaseManager dbManager) {
        dbManager.saveFeedback(patient.getId(), this.getName(), comment);
        System.out.println("[INFO] Feedback saved for " + patient.getName());
    }

    /**
     * Prescribes medication to a patient with email notification.
     * 
     * @param patient Patient receiving prescription
     * @param medication Medication name
     * @param dosage Dosage instructions
     * @param schedule Administration schedule
     * @param tests Recommended tests (optional)
     * @param emailService Email notification service
     * @param dbManager Database access manager
     */
    public void prescribeMedication(Patient patient, String medication,
                                    String dosage, String schedule, String tests,
                                    EmailNotification emailService, DatabaseManager dbManager) {
        // Create and save prescription
        Prescription prescription = new Prescription(medication, dosage, schedule, this.getName(), tests);
        dbManager.savePrescription(patient, medication, dosage, schedule, this.getName());

        // Prepare and send email notification
        String emailBody = "Dear " + patient.getName() + ",\n\n" +
                "You have received a new prescription from Dr. " + this.getName() + ":\n\n" +
                "Medication: " + medication + "\n" +
                "Dosage: " + dosage + "\n" +
                "Schedule: " + schedule + "\n";

        if (tests != null && !tests.isEmpty()) {
            emailBody += "Recommended Tests: " + tests + "\n";
        }

        emailBody += "\nPlease follow the instructions carefully.\n\nBest,\nRemoteHealth Team";

        emailService.sendEmail(patient.getEmail(), "New Prescription from Dr. " + this.getName(), emailBody);

        System.out.println("[INFO] Prescription and email notification sent to " + patient.getName());
    }

    /**
     * Retrieves prescriptions for a patient (placeholder implementation).
     * 
     * @param patient Patient to view
     * @param dbManager Database access manager
     * @return ArrayList of Prescription objects
     */
    public ArrayList<Prescription> viewPatientPrescriptions(Patient patient, DatabaseManager dbManager) {
        return new ArrayList<>(); // Placeholder unless implemented in DatabaseManager
    }

    // ========== GETTER METHODS ==========
    
    /**
     * @return Doctor's password (should be handled securely in production)
     */
    public String getPassword() {
        return password;
    }
}