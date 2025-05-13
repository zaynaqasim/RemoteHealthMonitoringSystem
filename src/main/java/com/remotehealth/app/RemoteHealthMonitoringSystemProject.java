/**
 * The RemoteHealthMonitoringSystemProject class is the entry point of the Remote Health Monitoring System.
 * It connects to the database, initializes the application data (admin, doctors, patients),
 * sets up core services like appointments, reminders, emergency alerts, and video calls,
 * and launches the user interface for login and system interaction.
 * 
 * This class ensures that essential entities are present in the database and prepares the system 
 * for real-time usage. It also manages shutdown behavior to safely close the database connection.
 * 
 * Key responsibilities:
 * <ul>
 *   <li>Connect to the MySQL hospital database</li>
 *   <li>Ensure default admin and sample users exist</li>
 *   <li>Initialize services like appointment management, video calls, and alerts</li>
 *   <li>Launch GUI via WindowManager</li>
 * </ul>
 * 
 * Usage:
 * <pre>
 *     java com.remotehealth.app.RemoteHealthMonitoringSystemProject
 * </pre>
 * 
 * @author 
 * @version 1.0
 */
package com.remotehealth.app;

import com.remotehealth.app.model.*;
import com.remotehealth.app.service.*;
import com.remotehealth.app.communication.*;
import com.remotehealth.app.gui.*;
import java.time.LocalDateTime;
import com.remotehealth.app.utils.*;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;

public class RemoteHealthMonitoringSystemProject {

    /**
     * Entry point of the application. Connects to the database, sets up default users, 
     * initializes services, and starts the GUI.
     * 
     * @param args Command-line arguments (not used)
     */
    public static void main(String[] args) {
        Connection conn = null;
        try {
            // Establish connection to the MySQL hospital database
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/hospital", "root", "zayna2412");
            DatabaseManager dbManager = new DatabaseManager(conn);
            System.out.println("Database connected successfully!");

            // Register shutdown hook to safely close database connection
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                dbManager.closeConnection();
            }));

            // Create or fetch admin user
            String username = "admin";
            String password = "admin123";
            Administrator admin = Administrator.getAdminByCredentials(username, password, dbManager);
            if (admin == null) {
                System.out.println("Admin not found in database, creating default admin");
                admin = new Administrator("A000", "admin", "admin@example.com", "admin123", dbManager);
                // dbManager.saveAdmin(admin); // Optional: save to database if method available
            }

            // Create and store sample doctors if not already present
            Doctor doctor1 = new Doctor("D001", "Naeem Ahmed", "naeem@gmail.com", "doc1");
            Doctor doctor2 = new Doctor("D002", "Qazi Aslam", "qazi@gmail.com", "doc2");
            if (dbManager.getDoctorById(doctor1.getId()) == null) dbManager.saveDoctor(doctor1);
            if (dbManager.getDoctorById(doctor2.getId()) == null) dbManager.saveDoctor(doctor2);

            // Create and store sample patient if not already present
            Patient patient1 = new Patient("P001", "Zayna Qasim", "zaynaqasim@gmail.com", "patient12345");
            if (dbManager.getPatientById(patient1.getId()) == null) dbManager.savePatient(patient1);
            
            dbManager.cleanInvalidAppointments();

            // Fetch all available doctors and patients
            ArrayList<Doctor> doctors = dbManager.getAllDoctors();
            ArrayList<Patient> patients = dbManager.getAllPatients();

            // Initialize system services
            AppointmentManager appointmentManager = new AppointmentManager(dbManager);
            EmailNotification emailService = new EmailNotification();
            ReminderService reminderService = new ReminderService(emailService, appointmentManager, dbManager);
            EmergencyAlert emergencyAlert = new EmergencyAlert(dbManager);
            VideoCall videoCall = new VideoCall();

            // Link reminder service to appointment manager
            appointmentManager.setReminderService(reminderService);

            // Initialize and display login GUI
            WindowManager.initialize(doctors, patients, admin, appointmentManager,
                                     reminderService, emergencyAlert, videoCall, emailService, dbManager);
            WindowManager.showWelcomeScreen();

        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Finds a patient in the database using their unique ID.
     * 
     * @param dbManager The database manager instance
     * @param id The patient ID
     * @return The matching Patient object, or null if not found or input is invalid
     */
    public static Patient findPatientById(DatabaseManager dbManager, String id) {
        if (dbManager == null || id == null) {
            return null;
        }
        return dbManager.getPatientById(id);
    }

    /**
     * Finds a doctor in the database using their unique ID.
     * 
     * @param dbManager The database manager instance
     * @param id The doctor ID
     * @return The matching Doctor object, or null if not found or input is invalid
     */
    public static Doctor findDoctorById(DatabaseManager dbManager, String id) {
        if (dbManager == null || id == null) {
            return null;
        }
        return dbManager.getDoctorById(id);
    }
}
