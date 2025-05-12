/**
 * The WindowManager class is responsible for managing the GUI screens of the Remote Health Monitoring System.
 * It acts as a central controller to initialize system services, display the login screen, and handle session changes.
 * It also ensures safe shutdown of database connections upon application termination.
 * 
 * @author 
 * @version 1.0
 */
package com.remotehealth.app.utils;

import com.remotehealth.app.gui.*;
import com.remotehealth.app.model.*;
import com.remotehealth.app.service.*;
import com.remotehealth.app.communication.*;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class WindowManager extends JFrame {

    // System-wide shared services and data
    private static ArrayList<Doctor> doctors;
    private static ArrayList<Patient> patients;
    private static Administrator admin;
    private static AppointmentManager appointmentManager;
    private static ReminderService reminderService;
    private static EmergencyAlert emergencyAlert;
    private static VideoCall videoCall;
    private static EmailNotification emailService;
    private static DatabaseManager dbManager;

    /**
     * Initializes the WindowManager with necessary components and services.
     * Ensures all dependencies are properly assigned before screens are displayed.
     */
    public static void initialize(ArrayList<Doctor> doctors, ArrayList<Patient> patients,
                                  Administrator admin, AppointmentManager appointmentManager,
                                  ReminderService reminderService, EmergencyAlert emergencyAlert,
                                  VideoCall videoCall, EmailNotification emailService, DatabaseManager dbManager) {
        // Debug output to verify proper initialization
        System.out.println("Initializing WindowManager...");
        System.out.println("Doctors: " + (doctors == null ? "null" : doctors.size() + " doctors"));
        System.out.println("Patients: " + (patients == null ? "null" : patients.size() + " patients"));
        System.out.println("Admin: " + (admin == null ? "null" : admin.getName()));
        System.out.println("AppointmentManager: " + (appointmentManager == null ? "null" : "initialized"));
        System.out.println("ReminderService: " + (reminderService == null ? "null" : "initialized"));
        System.out.println("EmergencyAlert: " + (emergencyAlert == null ? "null" : "initialized"));
        System.out.println("VideoCall: " + (videoCall == null ? "null" : "initialized"));
        System.out.println("EmailService: " + (emailService == null ? "null" : "initialized"));
        System.out.println("DatabaseManager: " + (dbManager == null ? "null" : "initialized"));

        // Assign dependencies to static fields
        WindowManager.doctors = doctors;
        WindowManager.patients = patients;
        WindowManager.admin = admin;
        WindowManager.appointmentManager = appointmentManager;
        WindowManager.reminderService = reminderService;
        WindowManager.emergencyAlert = emergencyAlert;
        WindowManager.videoCall = videoCall;
        WindowManager.emailService = emailService;
        WindowManager.dbManager = dbManager;
    }

    /**
     * Displays the welcome screen.
     * Currently redirects to the login screen.
     */
    public static void showWelcomeScreen() {
        showLoginScreen();
    }

    /**
     * Launches the login screen on the Event Dispatch Thread.
     * Ensures all services are initialized before proceeding.
     */
    public static void showLoginScreen() {
        SwingUtilities.invokeLater(() -> {
            if (admin == null || appointmentManager == null || reminderService == null ||
                emergencyAlert == null || videoCall == null || emailService == null || dbManager == null) {
                throw new IllegalStateException("WindowManager not properly initialized. Ensure all dependencies are set.");
            }
            LoginScreen loginScreen = new LoginScreen(admin, appointmentManager, reminderService,
                                                    emergencyAlert, videoCall, emailService, dbManager);
            loginScreen.setVisible(true);
        });
    }

    /**
     * Logs out the current user by closing the current frame and returning to the login screen.
     * 
     * @param currentFrame The currently displayed JFrame to be closed.
     */
    public static void logout(JFrame currentFrame) {
        currentFrame.dispose();
        showLoginScreen();
    }

    /**
     * Retrieves the DatabaseManager instance.
     * 
     * @return DatabaseManager object
     * @throws IllegalStateException if dbManager is not initialized
     */
    public static DatabaseManager getDbManager() {
        if (dbManager == null) {
            throw new IllegalStateException("DatabaseManager not initialized in WindowManager.");
        }
        return dbManager;
    }

    // Static block to safely close the database connection on JVM shutdown
    static {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (dbManager != null) {
                dbManager.closeConnection();
                System.out.println("WindowManager shutdown: Database connection closed.");
            }
        }));
    }
}
