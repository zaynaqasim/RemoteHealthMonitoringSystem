/**
 * The MedicalHistory class manages a patient's medical records including
 * prescriptions, doctor's notes, and other health information. It integrates
 * with the database to retrieve and format medical history data.
 * 
 * @author 
 * @version 1.0
 */
package com.remotehealth.app.model;

import java.util.ArrayList;

public class MedicalHistory {
    // Doctor's notes
    private String notes;

    /**
     * Sets doctor's notes for the patient.
     * 
     * @param notes Notes content
     */
    public void setNotes(String notes) {
        this.notes = notes;
    }

    /**
     * Generates a formatted string of the patient's medical history
     * by querying the database for relevant information.
     * 
     * @param dbManager Database access manager
     * @param patientId ID of the patient
     * @return Formatted medical history string
     */
    public String getFormattedHistory(DatabaseManager dbManager, String patientId) {
        StringBuilder sb = new StringBuilder();

        // Fetch prescriptions from database
        ArrayList<Prescription> prescriptions = dbManager.getPrescriptionsForPatient(patientId);
        if (!prescriptions.isEmpty()) {
            sb.append("Active Prescriptions:\n");
            for (Prescription p : prescriptions) {
                sb.append("- ").append(p.toString()).append("\n");
            }
        }

        // Include doctor's notes if available
        if (notes != null && !notes.isEmpty()) {
            sb.append("\nDoctor's Notes:\n").append(notes);
        }

        return sb.toString().isEmpty() ? "No medical history recorded." : sb.toString();
    }
}