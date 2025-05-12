/**
 * The EmergencyAlert class monitors patient vital signs and manages emergency situations
 * in the Remote Health Monitoring System. It detects critical health conditions,
 * handles panic button activations, and tracks emergency statuses.
 * 
 * @author 
 * @version 1.0
 */
package com.remotehealth.app.service;

import com.remotehealth.app.model.DatabaseManager;
import com.remotehealth.app.model.Patient;
import com.remotehealth.app.model.VitalSign;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class EmergencyAlert {
    private final DatabaseManager dbManager;

    /**
     * Constructs an EmergencyAlert with database access.
     * 
     * @param dbManager The database manager for emergency records
     */
    public EmergencyAlert(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    /**
     * The Emergency class represents an emergency event with details about
     * the patient, timestamp, type, and acknowledgment status.
     */
    public static class Emergency {
        private final String message;
        private final LocalDateTime timestamp;
        private final Patient patient;
        private boolean acknowledged;
        private final String type;

        /**
         * Creates a new unacknowledged emergency.
         */
        public Emergency(String message, Patient patient, String type) {
            this.message = message;
            this.patient = patient;
            this.type = type;
            this.timestamp = LocalDateTime.now();
            this.acknowledged = false;
        }

        /**
         * Creates an emergency with full details (typically used when loading from DB).
         */
        public Emergency(String message, LocalDateTime timestamp, Patient patient, String type, boolean acknowledged) {
            this.message = message;
            this.timestamp = timestamp;
            this.patient = patient;
            this.type = type;
            this.acknowledged = acknowledged;
        }

        // ========== GETTER METHODS ==========
        public String getMessage() { return message; }
        public LocalDateTime getTimestamp() { return timestamp; }
        public Patient getPatient() { return patient; }
        public boolean isAcknowledged() { return acknowledged; }
        public String getType() { return type; }

        /**
         * Marks this emergency as acknowledged.
         */
        public void acknowledge() {
            this.acknowledged = true;
        }

        @Override
        public String toString() {
            return String.format("[%s] %s - %s (%s)",
                    timestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                    type,
                    message,
                    acknowledged ? "ACKNOWLEDGED" : "PENDING");
        }
    }

    /**
     * Triggers alerts based on abnormal vital signs.
     * Checks all vitals for a patient and creates emergencies for critical values.
     * 
     * @param patient The patient to check vitals for
     */
    public void triggerAlert(Patient patient) {
        for (VitalSign vital : dbManager.getVitalsForPatient(patient.getId())) {
            if (vital.getHeartRate() < 40 || vital.getHeartRate() > 120) {
                saveEmergency(new Emergency("Critical heart rate: " + vital.getHeartRate(), patient, "HEART_RATE"));
            }
            if (vital.getOxygenLevel() < 90) {
                saveEmergency(new Emergency("Critical oxygen level: " + vital.getOxygenLevel(), patient, "OXYGEN"));
            }
            if (vital.getTemperature() < 35.0 || vital.getTemperature() > 40.0) {
                saveEmergency(new Emergency("Critical temperature: " + vital.getTemperature(), patient, "TEMPERATURE"));
            }
        }
    }

    /**
     * Handles panic button activation from a patient.
     * 
     * @param patient The patient who activated the panic button
     */
    public void activatePanicButton(Patient patient) {
        saveEmergency(new Emergency("PANIC BUTTON ACTIVATED!", patient, "PANIC"));
    }

    /**
     * Saves an emergency record to the database.
     * 
     * @param e The emergency to save
     */
    public void saveEmergency(Emergency e) {
        dbManager.saveEmergency(e);
    }

    /**
     * Retrieves all unacknowledged emergencies.
     * 
     * @return List of pending emergencies
     */
    public List<Emergency> getPendingEmergencies() {
        return dbManager.getPendingEmergencies();
    }

    /**
     * Formats all emergencies for display.
     * 
     * @return Formatted string of all emergencies
     */
    public String displayAlerts() {
        List<Emergency> emergencies = dbManager.getAllEmergencies();
        if (emergencies.isEmpty()) return "No emergency alerts";

        StringBuilder sb = new StringBuilder("=== EMERGENCY ALERTS ===\n\n");
        for (Emergency e : emergencies) {
            sb.append(e.toString()).append("\n\n");
        }
        return sb.toString();
    }

    /**
     * Acknowledges an emergency and updates its status in the database.
     * 
     * @param e The emergency to acknowledge
     */
    public void acknowledgeEmergency(Emergency e) {
        e.acknowledge();
        dbManager.updateEmergencyAcknowledged(e);
    }
}