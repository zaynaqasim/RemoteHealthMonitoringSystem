/**
 * The DatabaseManager class handles all database operations for the Remote Health application.
 * It provides methods for managing patients, doctors, appointments, vitals, feedback, 
 * prescriptions, emergencies, and administrative functions.
 * 
 * @author 
 * @version 1.0
 */
package com.remotehealth.app.model;

import com.remotehealth.app.service.EmergencyAlert;
import java.sql.*;
import java.util.ArrayList;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.DriverManager;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Date;
import io.github.cdimascio.dotenv.Dotenv;

public class DatabaseManager {
    private Connection connection;

    /**
     * Constructs a new DatabaseManager with the specified connection.
     * 
     * @param connection The database connection to use
     */
    public DatabaseManager(Connection connection) {
        this.connection = connection;
    }

    /**
     * Gets the database connection, re-establishing it if necessary.
     * 
     * @return The active database connection
     */
    public Connection getConnection() {
        try {
        if (connection == null || connection.isClosed()) {
            Dotenv dotenv = Dotenv.load();
            String url = dotenv.get("DB_URL");
            String user = dotenv.get("DB_USER");
            String pass = dotenv.get("DB_PASSWORD");

            connection = DriverManager.getConnection(url, user, pass);
            System.out.println("Re-established database connection");
        }
        return connection;
    } catch (SQLException e) {
        throw new RuntimeException("Failed to re-establish connection", e);
    }
}

    /**
     * Closes the database connection if it's open.
     */
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed");
            }
        } catch (SQLException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }

    // ===================== PATIENT OPERATIONS =====================
    
    /**
     * Saves a patient to the database or updates if they already exist.
     * 
     * @param patient The patient to save
     */
    public void savePatient(Patient patient) {
        String sql = "INSERT INTO patients (id, name, email, password) VALUES (?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE name = VALUES(name), email = VALUES(email), password = VALUES(password)";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, patient.getId());
            ps.setString(2, patient.getName());
            ps.setString(3, patient.getEmail());
            ps.setString(4, patient.getPassword());
            int rowsAffected = ps.executeUpdate();
            saveLog("Saved patient: " + patient.getId() + ", Rows affected: " + rowsAffected + 
                    ", Password: " + patient.getPassword());
        } catch (SQLException e) {
            saveLog("Error saving patient " + patient.getId() + ": " + e.getMessage());
            throw new RuntimeException("Failed to save patient", e);
        }
    }

    /**
     * Retrieves all patients from the database.
     * 
     * @return List of all patients
     */
    public ArrayList<Patient> getAllPatients() {
        ArrayList<Patient> list = new ArrayList<>();
        String sql = "SELECT * FROM patients";
        try (PreparedStatement ps = getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Patient(rs.getString("id"), rs.getString("name"), rs.getString("email")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Retrieves a patient by their ID.
     * 
     * @param id The patient ID to search for
     * @return The found patient or null if not found
     */
    public Patient getPatientById(String id) {
        String sql = "SELECT id, name, email, password FROM patients WHERE id = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Patient patient = new Patient(rs.getString("id"), rs.getString("name"), 
                                             rs.getString("email"), rs.getString("password"));
                saveLog("Retrieved patient: " + id + ", Password: " + rs.getString("password"));
                return patient;
            } else {
                saveLog("Patient not found in database: " + id);
                return null;
            }
        } catch (SQLException e) {
            saveLog("Error retrieving patient " + id + ": " + e.getMessage());
            throw new RuntimeException("Failed to retrieve patient", e);
        }
    }

    /**
     * Deletes a patient by their ID.
     * 
     * @param id The ID of the patient to delete
     */
    public void deletePatientById(String id) {
        String sql = "DELETE FROM patients WHERE id = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ===================== DOCTOR OPERATIONS =====================
    
    /**
     * Saves a doctor to the database or updates if they already exist.
     * 
     * @param doctor The doctor to save
     */
    public void saveDoctor(Doctor doctor) {
        String sql = "INSERT INTO doctors (id, name, email, password) VALUES (?, ?, ?, ?)" + 
                "ON DUPLICATE KEY UPDATE name = VALUES(name), email = VALUES(email), password = VALUES(password)";;
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, doctor.getId());
            ps.setString(2, doctor.getName());
            ps.setString(3, doctor.getEmail());
            ps.setString(4, doctor.getPassword());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Retrieves a doctor by their ID.
     * 
     * @param id The doctor ID to search for
     * @return The found doctor or null if not found
     */
    public Doctor getDoctorById(String id) {
        String query = "SELECT * FROM doctors WHERE id = ?";
        try (PreparedStatement stmt = getConnection().prepareStatement(query)) {
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Doctor(
                    rs.getString("id"),
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("password")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Retrieves a doctor by their name.
     * 
     * @param name The doctor name to search for
     * @return The found doctor or null if not found
     */
    public Doctor getDoctorByName(String name) {
        String sql = "SELECT * FROM doctors WHERE name = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Doctor(rs.getString("id"), rs.getString("name"), rs.getString("email"), rs.getString("password"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Retrieves all doctors from the database.
     * 
     * @return List of all doctors
     */
    public ArrayList<Doctor> getAllDoctors() {
        ArrayList<Doctor> doctors = new ArrayList<>();
        String query = "SELECT * FROM doctors";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                String id = rs.getString("id");
                String name = rs.getString("name");
                String email = rs.getString("email");
                String password = rs.getString("password");
                doctors.add(new Doctor(id, name, email, password));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return doctors;
    }

    /**
     * Deletes a doctor by their ID.
     * 
     * @param id The ID of the doctor to delete
     */
    public void deleteDoctorById(String id) {
        String sql = "DELETE FROM doctors WHERE id = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ===================== APPOINTMENT OPERATIONS =====================
    
    /**
     * Saves an appointment to the database.
     * 
     * @param appointment The appointment to save
     */
    public void saveAppointment(Appointment appointment) {
        try (Connection conn = getConnection();
            PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO appointments (patient_id, doctor_id, date_time, status) VALUES (?, ?, ?, ?)")) {
        
            stmt.setString(1, appointment.getPatient().getId());
            stmt.setString(2, appointment.getDoctor().getId());
            // Use Timestamp.valueOf() to properly store both date and time
            stmt.setTimestamp(3, Timestamp.valueOf(appointment.getDateTime()));
            stmt.setString(4, appointment.getStatus());
            stmt.executeUpdate();
            } catch (SQLException e) {
               e.printStackTrace();
        }
    }

    /**
     * Retrieves all appointments for a specific patient.
     * 
     * @param patientId The ID of the patient
     * @return List of appointments for the patient
     */
    public List<Appointment> getAppointmentsForPatient(String patientId) {
        List<Appointment> appointments = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                "SELECT a.*, d.name as doctor_name, d.id as doctor_id, " +
                "d.email as doctor_email, d.specialization " +
                "FROM appointments a " +
                "LEFT JOIN doctors d ON a.doctor_id = d.id " +
                "WHERE a.patient_id = ? AND a.date_time IS NOT NULL")) {
        
                stmt.setString(1, patientId);
                ResultSet rs = stmt.executeQuery();
        
                while (rs.next()) {
                try {
                    // Parse date/time
                    LocalDateTime dateTime = rs.getTimestamp("date_time").toLocalDateTime();
                
                    // Get doctor (handle null case)
                    Doctor doctor = null;
                    if (rs.getString("doctor_id") != null) {
                        doctor = new Doctor(
                            rs.getString("doctor_id"),
                            rs.getString("doctor_name"),
                            rs.getString("doctor_email"),
                            rs.getString("specialization")
                        );
                    }
                
                    // Get patient 
                    Patient patient = getPatientById(patientId); 
                
                    if (dateTime != null && patient != null && doctor != null) {
                    Appointment appt = new Appointment(dateTime, patient, doctor);
                    appt.setStatus(rs.getString("status"));
                    appointments.add(appt);
                    } else {
                    System.err.println("Skipping invalid appointment record");
                    }
                } catch (Exception e) {
                System.err.println("Error parsing appointment: " + e.getMessage());
                }
            }
        } catch (SQLException e) {
             e.printStackTrace();
        }
        return appointments;
    }

    /**
     * Deletes all appointments for a specific patient.
     * 
     * @param patientId The ID of the patient
     */
    public void deleteAppointmentsByPatientId(String patientId) {
        String sql = "DELETE FROM appointments WHERE patientID = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, patientId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Retrieves all appointments for a specific doctor.
     * 
     * @param doctorName The name of the doctor
     * @return List of appointments for the doctor
     */
   public ArrayList<Appointment> getAppointmentsForDoctor(String doctorName) {
    ArrayList<Appointment> appointments = new ArrayList<>();
    try (Connection conn = getConnection();
        PreparedStatement stmt = conn.prepareStatement(
            "SELECT a.*, p.id as patient_id, p.name as patient_name, p.email as patient_email, " +
            "d.id as doctor_id, d.name as doctor_name, d.email as doctor_email " +
            "FROM appointments a " +
            "JOIN patients p ON a.patient_id = p.id " +
            "JOIN doctors d ON a.doctor_id = d.id WHERE d.name = ?")) {  
    
        stmt.setString(1, doctorName);
        ResultSet rs = stmt.executeQuery();
    
        while (rs.next()) {
            try {
                LocalDateTime dateTime = rs.getTimestamp("date_time").toLocalDateTime();
                Patient patient = new Patient(
                    rs.getString("patient_id"),
                    rs.getString("patient_name"),
                    rs.getString("patient_email")
                );
                Doctor doctor = new Doctor(
                    rs.getString("doctor_id"),
                    rs.getString("doctor_name"),
                    rs.getString("doctor_email"),
                    "" 
                );
                
                Appointment appt = new Appointment(dateTime, patient, doctor);
                appt.setStatus(rs.getString("status"));
                appointments.add(appt);
            } catch (Exception e) {
                System.err.println("Error parsing appointment: " + e.getMessage());
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return appointments;
}
    
    
    public void cleanInvalidAppointments() {
    try (Connection conn = getConnection();
         Statement stmt = conn.createStatement()) {
        // Delete appointments with null doctor or date
        stmt.executeUpdate(
            "DELETE FROM appointments WHERE doctor_id IS NULL OR date_time IS NULL");
    } catch (SQLException e) {
        e.printStackTrace();
    }
    }

    // ===================== VITAL SIGNS OPERATIONS =====================
    
    /**
     * Saves vital signs for a patient.
     * 
     * @param patientId The ID of the patient
     * @param vitals The vital signs to save
     */
    public void saveVitals(String patientId, VitalSign vitals) {
        String sql = "INSERT INTO vitals (patientId, heartRate, oxygenLevel, bloodPressure, temperature, added_on) VALUES (?, ?, ?, ?, ?,?)";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, patientId);
            ps.setInt(2, vitals.getHeartRate());
            ps.setInt(3, vitals.getOxygenLevel());
            ps.setString(4, vitals.getBloodPressure());
            ps.setDouble(5, vitals.getTemperature());
            ps.setDate(6, new java.sql.Date(vitals.getAddedOn().getTime()));
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Deletes all vital signs for a patient.
     * 
     * @param patientId The ID of the patient
     */
    public void deleteVitals(String patientId) {
        String sql = "DELETE FROM vitals WHERE patientId = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, patientId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Retrieves all vital signs for a patient.
     * 
     * @param patientId The ID of the patient
     * @return List of vital signs for the patient
     */
    public List<VitalSign> getVitalsForPatient(String patientId) {
        List<VitalSign> vitals = new ArrayList<>();
        String sql = "SELECT heartRate, oxygenLevel, bloodPressure, temperature, added_on FROM vitals WHERE patientId = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, patientId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                vitals.add(new VitalSign(
                    rs.getInt("heartRate"),
                    rs.getInt("oxygenLevel"),
                    rs.getString("bloodPressure"),
                    rs.getDouble("temperature"),
                    rs.getDate("added_on")
                ));
            }
            saveLog("Retrieved vitals for patient: " + patientId);
            return vitals;
        } catch (SQLException e) {
            saveLog("Error retrieving vitals for patient " + patientId + ": " + e.getMessage());
            throw new RuntimeException("Failed to retrieve vitals", e);
        }
    }

    // ===================== FEEDBACK OPERATIONS =====================
    
    /**
     * Saves feedback for a patient.
     * 
     * @param patientId The ID of the patient
     * @param doctorName The name of the providing doctor
     * @param comment The feedback content
     */
    public void saveFeedback(String patientId, String doctorName, String comment) {
        String sql = "INSERT INTO feedbacks (patientId, doctorName, comments) VALUES (?, ?, ?)";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, patientId);
            ps.setString(2, doctorName);
            ps.setString(3, comment);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Retrieves all feedback for a patient.
     * 
     * @param patientId The ID of the patient
     * @return List of feedback for the patient
     */
    public ArrayList<Feedback> getFeedbacksForPatient(String patientId) {
        ArrayList<Feedback> feedbacks = new ArrayList<>();
        String sql = "SELECT * FROM feedbacks WHERE patientId = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, patientId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Feedback fb = new Feedback(
                        rs.getString("doctorName"),
                        rs.getString("comments")
                );
                feedbacks.add(fb);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return feedbacks;
    }

    /**
     * Deletes all feedback for a patient.
     * 
     * @param patientId The ID of the patient
     */
    public void deleteFeedbackByPatientId(String patientId) {
        String sql = "DELETE FROM feedbacks WHERE patientId = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, patientId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ===================== CONSULTATION OPERATIONS =====================
    
    /**
     * Saves a consultation record.
     * 
     * @param patient The patient involved
     * @param doctor The doctor involved
     * @param diagnosis The diagnosis made
     * @param treatment The treatment prescribed
     */
    public void saveConsultation(Patient patient, Doctor doctor, String diagnosis, String treatment) {
        String sql = "INSERT INTO consultations (patientID, doctorName, diagnosis, treatment) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, patient.getId());
            ps.setString(2, doctor.getName());
            ps.setString(3, diagnosis);
            ps.setString(4, treatment);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Deletes all consultations for a patient.
     * 
     * @param patientId The ID of the patient
     */
    public void deleteConsultationsByPatientId(String patientId) {
        String sql = "DELETE FROM consultations WHERE patientID = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, patientId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ===================== PRESCRIPTION OPERATIONS =====================
    
    /**
     * Saves a prescription for a patient.
     * 
     * @param patient The patient receiving the prescription
     * @param medication The medication prescribed
     * @param dosage The dosage instructions
     * @param schedule The schedule for taking the medication
     * @param prescribingDoctor The doctor who prescribed the medication
     */
    public void savePrescription(Patient patient, String medication, String dosage, String schedule, String prescribingDoctor) {
        String sql = "INSERT INTO prescriptions (patientID, medication, dosage, schedule, prescribingDoctor) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, patient.getId());
            ps.setString(2, medication);
            ps.setString(3, dosage);
            ps.setString(4, schedule);
            ps.setString(5, prescribingDoctor);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Deletes all prescriptions for a patient.
     * 
     * @param patientId The ID of the patient
     */
    public void deletePrescriptionsByPatientId(String patientId) {
        String sql = "DELETE FROM prescriptions WHERE patientID = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, patientId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Retrieves all prescriptions for a patient.
     * 
     * @param patientId The ID of the patient
     * @return List of prescriptions for the patient
     */
    public ArrayList<Prescription> getPrescriptionsForPatient(String patientId) {
        ArrayList<Prescription> prescriptions = new ArrayList<>();
        String sql = "SELECT * FROM prescriptions WHERE patientID = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, patientId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                prescriptions.add(new Prescription(
                        rs.getString("medication"),
                        rs.getString("dosage"),
                        rs.getString("schedule"),
                        rs.getString("prescribingDoctor"),
                        rs.getString("tests")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return prescriptions;
    }
    
    // ===================== EMERGENCY ALERT OPERATIONS =====================
    
    /**
     * Saves an emergency alert to the database.
     * 
     * @param e The emergency alert to save
     */
    public void saveEmergency(EmergencyAlert.Emergency e) {
        String sql = "INSERT INTO emergencies (message, timestamp, patientId, type, acknowledged) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, e.getMessage());
            ps.setTimestamp(2, Timestamp.valueOf(e.getTimestamp()));
            ps.setString(3, e.getPatient().getId());
            ps.setString(4, e.getType());
            ps.setBoolean(5, e.isAcknowledged());
            ps.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    
    /**
     * Retrieves all pending (unacknowledged) emergencies.
     * 
     * @return List of pending emergencies
     */
    public ArrayList<EmergencyAlert.Emergency> getPendingEmergencies() {
        ArrayList<EmergencyAlert.Emergency> list = new ArrayList<>();
        String sql = "SELECT * FROM emergencies WHERE acknowledged = false";

        try (PreparedStatement ps = getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Patient patient = getPatientById(rs.getString("patientId"));
                EmergencyAlert.Emergency e = new EmergencyAlert.Emergency(
                    rs.getString("message"),
                    rs.getTimestamp("timestamp").toLocalDateTime(),
                    patient,
                    rs.getString("type"),
                    false
                );
                list.add(e);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    /**
     * Retrieves all emergencies from the database.
     * 
     * @return List of all emergencies
     */
    public ArrayList<EmergencyAlert.Emergency> getAllEmergencies() {
        ArrayList<EmergencyAlert.Emergency> list = new ArrayList<>();
        String sql = "SELECT * FROM emergencies";

        try (PreparedStatement ps = getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Patient patient = getPatientById(rs.getString("patientId"));
                EmergencyAlert.Emergency e = new EmergencyAlert.Emergency(
                    rs.getString("message"),
                    rs.getTimestamp("timestamp").toLocalDateTime(),
                    patient,
                    rs.getString("type"),
                    rs.getBoolean("acknowledged")
                );
                list.add(e);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    /**
     * Updates an emergency alert to acknowledged status.
     * 
     * @param e The emergency alert to update
     */
    public void updateEmergencyAcknowledged(EmergencyAlert.Emergency e) {
        String sql = "UPDATE emergencies SET acknowledged = true WHERE message = ? AND patientId = ? AND timestamp = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, e.getMessage());
            ps.setString(2, e.getPatient().getId());
            ps.setTimestamp(3, Timestamp.valueOf(e.getTimestamp()));
            ps.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Deletes an emergency alert from the database.
     * 
     * @param e The emergency alert to delete
     */
    public void deleteEmergency(EmergencyAlert.Emergency e) {
        String sql = "DELETE FROM emergencies WHERE patientId = ? AND timestamp = ? AND message = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, e.getPatient().getId());
            ps.setTimestamp(2, Timestamp.valueOf(e.getTimestamp()));
            ps.setString(3, e.getMessage());
            ps.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    // ===================== ADMINISTRATOR OPERATIONS =====================
    
    /**
     * Retrieves an administrator by username.
     * 
     * @param username The username to search for
     * @param dbManager The database manager instance
     * @return The found administrator or null if not found
     */
    public Administrator getAdminByUsername(String username, DatabaseManager dbManager) {
        String sql = "SELECT * FROM admin WHERE name = ?";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String id = rs.getString("id");
                String name = rs.getString("name");
                String email = rs.getString("email");
                String password = rs.getString("password");

                return new Administrator(id, name, email, password, this);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // not found
    }

    /**
     * Submits a password reset request for a user.
     * 
     * @param username The username requesting reset
     * @param role The role of the user (patient/doctor/admin)
     */
    public void submitPasswordResetRequest(String username, String role) {
        String sql = "INSERT INTO password_reset_requests (username, role, request_time, status) " +
                     "VALUES (?, ?, NOW(), 'PENDING')";

        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, role);
            int rowsAffected = stmt.executeUpdate();
            saveLog("Submitted password reset request for username: " + username +
                    ", role: " + role + ", Rows affected: " + rowsAffected);
        } catch (SQLException e) {
            saveLog("Error submitting password reset request for " + username + ": " + e.getMessage());
            throw new RuntimeException("Failed to submit password reset request", e);
        }
    }

    /**
     * Retrieves all pending password reset requests.
     * 
     * @return List of pending request information strings
     */
    public List<String> getPendingPasswordResetRequests() {
        List<String> pendingRequests = new ArrayList<>();
        String sql = "SELECT username, role, request_time FROM password_reset_requests WHERE status = 'PENDING'";

        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                String requestInfo = "Username: " + rs.getString("username") +
                                     ", Role: " + rs.getString("role") +
                                     ", Request Time: " + rs.getString("request_time");
                pendingRequests.add(requestInfo);
            }
        } catch (SQLException e) {
            saveLog("Error fetching pending requests: " + e.getMessage());
            e.printStackTrace();
        }
        return pendingRequests;
    }

    // ===================== LOG OPERATIONS =====================
    
    /**
     * Saves a log message to the database.
     * 
     * @param message The log message to save
     */
    public void saveLog(String message) {
        String sql = "INSERT INTO logs (message, created_at) VALUES (?, NOW())";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, message);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error saving log: " + e.getMessage());
        }
    }
    
    /**
     * Retrieves all logs from the database, ordered by most recent.
     * 
     * @return List of log entries
     */
    public ArrayList<String> fetchLogs() {
        ArrayList<String> logs = new ArrayList<>();
        String sql = "SELECT * FROM logs ORDER BY created_at DESC";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                logs.add(rs.getString("created_at") + " - " + rs.getString("message"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return logs;
    }
}