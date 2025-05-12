/**
 * VitalsDatabase is a utility class that handles the persistence and retrieval of patient vital signs.
 * It delegates all data storage operations to the DatabaseManager.
 * This ensures separation of concerns between data access and business logic.
 * 
 * @author 
 * @version 1.0
 */
package com.remotehealth.app.utils;

import com.remotehealth.app.model.DatabaseManager;
import com.remotehealth.app.model.VitalSign;
import java.util.List;

public class VitalsDatabase {

    private DatabaseManager dbManager;

    /**
     * Constructs a VitalsDatabase instance with an injected DatabaseManager.
     * 
     * @param dbManager the database manager to be used for storing and retrieving vitals
     */
    public VitalsDatabase(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    /**
     * Adds a new vital sign entry for the specified patient.
     * 
     * @param patientId the unique identifier of the patient
     * @param vital the vital sign record to be stored
     */
    public void addVital(String patientId, VitalSign vital) {
        if (patientId == null || vital == null) {
            System.out.println("Error: Patient ID or VitalSign cannot be null");
            return;
        }
        try {
            dbManager.saveVitals(patientId, vital); // Persist the vital sign
            System.out.println("Vital signs recorded successfully for patient: " + patientId);
        } catch (Exception e) {
            System.out.println("Error saving vital signs for patient " + patientId + ": " + e.getMessage());
        }
    }

    /**
     * Displays all previously recorded vital signs for the specified patient.
     * 
     * @param patientId the unique identifier of the patient
     */
    public void displayVitals(String patientId) {
        if (patientId == null) {
            System.out.println("Error: Patient ID cannot be null");
            return;
        }
        try {
            List<VitalSign> vitals = dbManager.getVitalsForPatient(patientId); // Retrieve records
            if (vitals.isEmpty()) {
                System.out.println("No vital signs recorded for patient: " + patientId);
            } else {
                System.out.println("Vital signs for patient: " + patientId);
                for (VitalSign v : vitals) {
                    System.out.println(v);
                }
            }
        } catch (Exception e) {
            System.out.println("Error retrieving vital signs for patient " + patientId + ": " + e.getMessage());
        }
    }
}
