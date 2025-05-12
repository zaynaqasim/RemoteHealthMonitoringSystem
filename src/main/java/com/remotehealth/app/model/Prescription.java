/**
 * The Prescription class represents a medication prescription with dosage
 * and schedule information. It includes details about the prescribing doctor
 * and any recommended tests.
 * 
 * @author 
 * @version 1.0
 */
package com.remotehealth.app.model;

public class Prescription {
    // Prescription details
    private String medication;         // Name of prescribed medication
    private String dosage;             // Dosage instructions
    private String schedule;           // Administration schedule
    private String prescribingDoctor;  // Name of prescribing doctor
    private String tests;              // Recommended tests (optional)

    /**
     * Constructs a new Prescription with all details.
     * 
     * @param medication Name of medication
     * @param dosage Dosage instructions
     * @param schedule Administration schedule
     * @param prescribingDoctor Name of prescribing doctor
     * @param tests Recommended tests (optional)
     */
    public Prescription(String medication, String dosage, String schedule,
                        String prescribingDoctor, String tests) {
        this.medication = medication;
        this.dosage = dosage;
        this.schedule = schedule;
        this.prescribingDoctor = prescribingDoctor;
        this.tests = tests;
    }

    // ========== GETTER METHODS ==========
    
    /**
     * @return Medication name
     */
    public String getMedication() { return medication; }
    
    /**
     * @return Dosage instructions
     */
    public String getDosage() { return dosage; }
    
    /**
     * @return Administration schedule
     */
    public String getSchedule() { return schedule; }
    
    /**
     * @return Prescribing doctor's name
     */
    public String getPrescribingDoctor() { return prescribingDoctor; }
    
    /**
     * @return Recommended tests (may be empty)
     */
    public String getTests() { return tests; }

    /**
     * Returns a formatted string suitable for reminder messages.
     * 
     * @return Formatted reminder string
     */
    public String getFormattedReminder() {
        return "Take " + medication + " (" + dosage + ") at " + schedule +
               " [Prescribed by Dr. " + prescribingDoctor + "]";
    }

    /**
     * Returns a detailed string representation of the prescription.
     * 
     * @return Formatted prescription details
     */
    @Override
    public String toString() {
        String prescription = medication + " (" + dosage + ", " + schedule + "), Prescribed by: Dr. " + prescribingDoctor;
        if (tests != null && !tests.isEmpty()) {
            prescription += "\nRecommended Tests: " + tests;
        }
        return prescription;
    }
}