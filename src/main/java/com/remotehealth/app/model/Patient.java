/**
 * The Patient class represents a patient user in the Remote Health Monitoring System.
 * It extends the base User class and adds medical-specific functionality including
 * vital sign tracking, prescription management, and appointment scheduling.
 * 
 * @author 
 * @version 1.0
 */
package com.remotehealth.app.model;


import java.util.ArrayList;
import java.util.List;

public class Patient extends User {
    // Authentication credentials
    private String password;
    
    // Medical records
    private MedicalHistory medicalHistory;

    /**
     * Constructs a new Patient with authentication credentials.
     * 
     * @param id Patient ID
     * @param name Full name
     * @param email Email address
     * @param password Authentication password
     */
    public Patient(String id, String name, String email, String password) {
        super(id, name, email);
        this.password = password;
        this.medicalHistory = new MedicalHistory();
    }

    /**
     * Constructs a Patient for database retrieval (without password).
     * 
     * @param id Patient ID
     * @param name Full name
     * @param email Email address
     */
    public Patient(String id, String name, String email) {
        super(id, name, email);
        this.medicalHistory = new MedicalHistory();
    }
    
    /**
     * Default constructor for framework compatibility.
     */
    public Patient() {
        super();
    }

    // ========== MEDICAL HISTORY ACCESS ==========
    
    /**
     * Gets the patient's medical history object.
     * 
     * @return MedicalHistory instance
     */
    public MedicalHistory getMedicalHistory() {
        return this.medicalHistory;
    }

    // ========== AUTHENTICATION METHODS ==========
    
    /**
     * Authenticates the patient against provided credentials.
     * 
     * @param username Input username/ID
     * @param password Input password
     * @return true if credentials match
     */
    public boolean authenticate(String username, String password) {
        System.out.println("Patient - Stored name: " + this.getName());
        System.out.println("Patient - Input name: " + username);
        System.out.println("Patient - Stored password: " + this.password);
        System.out.println("Patient - Input password: " + password);

        return this.getId().trim().equalsIgnoreCase(username.trim()) &&
               this.password.trim().equals(password.trim());
    }

    // ========== DATABASE-INTEGRATED METHODS ==========
    
    /**
     * Adds a vital sign measurement to the patient's records.
     * 
     * @param vital VitalSign measurement
     * @param dbManager Database access manager
     */
    public void addVital(VitalSign vital, DatabaseManager dbManager) {
        dbManager.saveVitals(this.getId(), vital);
        System.out.println("Vital saved to database.");
    }

    /**
     * Adds a new prescription to the patient's records.
     * 
     * @param prescription Prescription details
     * @param dbManager Database access manager
     */
    public void addPrescription(Prescription prescription, DatabaseManager dbManager) {
        dbManager.savePrescription(this, prescription.getMedication(), prescription.getDosage(), prescription.getSchedule(), prescription.getPrescribingDoctor());
        System.out.println("Prescription saved to database.");
    }

    /**
     * Schedules a new appointment for the patient.
     * 
     * @param appointment Appointment details
     * @param dbManager Database access manager
     */
    public void scheduleAppointment(Appointment appointment, DatabaseManager dbManager) {
        dbManager.saveAppointment(appointment);
        System.out.println("Appointment scheduled.");
    }

    /**
     * Retrieves all vital sign measurements for the patient.
     * 
     * @param dbManager Database access manager
     * @return List of VitalSign objects
     */
    public List<VitalSign> getVitals(DatabaseManager dbManager) {
        return dbManager.getVitalsForPatient(this.getId());
    }

    /**
     * Retrieves all appointments for the patient.
     * 
     * @param dbManager Database access manager
     * @return List of Appointment objects
     */
    public List<Appointment> getAppointments(DatabaseManager dbManager) {
        return dbManager.getAppointmentsForPatient(this.getId());
    }

    /**
     * Retrieves all feedback entries for the patient.
     * 
     * @param dbManager Database access manager
     * @return ArrayList of Feedback objects
     */
    public ArrayList<Feedback> getFeedbacks(DatabaseManager dbManager) {
        return dbManager.getFeedbacksForPatient(this.getId());
    }

    /**
     * Returns a formatted string of all feedback entries.
     * 
     * @param dbManager Database access manager
     * @return Formatted feedback string
     */
    public String getFormattedFeedbacks(DatabaseManager dbManager) {
        ArrayList<Feedback> feedbacks = dbManager.getFeedbacksForPatient(this.getId());
        if (feedbacks.isEmpty()) return "No feedback available";

        StringBuilder sb = new StringBuilder();
        for (Feedback fb : feedbacks) sb.append(fb.toString()).append("\n\n");
        return sb.toString();
    }

    /**
     * Returns a placeholder string for medical history (implementation note).
     * 
     * @param dbManager Database access manager
     * @return Placeholder string
     */
    public String getFormattedMedicalHistory(DatabaseManager dbManager) {
        return "[History fetched via database - use viewMedicalHistory()]";
    }

    // ========== GETTER METHODS ==========
    
    /**
     * @return Patient's password (should be handled securely in production)
     */
    public String getPassword() {
        return password;
    }

}