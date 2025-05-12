/**
 * The LoginScreen class represents the graphical user interface for logging into the 
 * Remote Health Monitoring System. Users can log in as a Patient, Doctor, or Administrator.
 * 
 * It handles user authentication, error display, and launches the respective dashboard upon successful login.
 * It also logs all attempts and events using the DatabaseManager.
 * 
 * Key Features:
 * <ul>
 *   <li>Graphical login form with user type selection</li>
 *   <li>Secure credential validation with error handling</li>
 *   <li>Dashboard redirection for authenticated users</li>
 *   <li>Logs every login attempt and error</li>
 * </ul>
 * 
 * Usage:
 * <pre>
 *     new LoginScreen(admin, appointmentManager, reminderService, ...).setVisible(true);
 * </pre>
 * 
 * @author
 * @version 1.0
 */
package com.remotehealth.app.gui;

import com.remotehealth.app.model.*;
import com.remotehealth.app.service.*;
import com.remotehealth.app.communication.*;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class LoginScreen extends JFrame {
    private Administrator admin;
    private AppointmentManager appointmentManager;
    private ReminderService reminderService;
    private EmergencyAlert emergencyAlert;
    private VideoCall videoCall;
    private EmailNotification emailService;
    private DatabaseManager dbManager;

    /**
     * Constructs the login screen with all necessary system services.
     *
     * @param admin Admin object for verification
     * @param appointmentManager Appointment management service
     * @param reminderService Reminder notification service
     * @param emergencyAlert Emergency alert handling service
     * @param videoCall Video call service
     * @param emailService Email service for notifications
     * @param dbManager Database manager for user and log operations
     */
    public LoginScreen(Administrator admin, AppointmentManager appointmentManager,
                      ReminderService reminderService, EmergencyAlert emergencyAlert,
                      VideoCall videoCall, EmailNotification emailService, DatabaseManager dbManager) {
        this.admin = admin;
        this.appointmentManager = appointmentManager;
        this.reminderService = reminderService;
        this.emergencyAlert = emergencyAlert;
        this.videoCall = videoCall;
        this.emailService = emailService;
        this.dbManager = dbManager;

        setTitle("Remote Health - Login");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initComponents();
    }

    /**
     * Initializes and sets up all GUI components for the login screen.
     */
    private void initComponents() {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(UITheme.SECONDARY_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("Remote Health Monitoring System");
        titleLabel.setFont(UITheme.HEADER_FONT);
        titleLabel.setForeground(UITheme.PRIMARY_COLOR);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(titleLabel, gbc);

        JLabel subtitleLabel = new JLabel("Please login to continue");
        subtitleLabel.setFont(UITheme.SUBHEADER_FONT);
        subtitleLabel.setForeground(UITheme.PRIMARY_COLOR);
        gbc.gridy = 1;
        mainPanel.add(subtitleLabel, gbc);

        // Login form UI
        JPanel formPanel = UITheme.createCardPanel();
        formPanel.setLayout(new GridBagLayout());
        GridBagConstraints formGbc = new GridBagConstraints();
        formGbc.insets = new Insets(10, 10, 10, 10);
        formGbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel userTypeLabel = new JLabel("Login As:");
        userTypeLabel.setFont(UITheme.BODY_FONT);
        formGbc.gridx = 0;
        formGbc.gridy = 0;
        formPanel.add(userTypeLabel, formGbc);

        JComboBox<String> userTypeCombo = new JComboBox<>(new String[]{"Patient", "Doctor", "Admin"});
        userTypeCombo.setFont(UITheme.BODY_FONT);
        formGbc.gridx = 1;
        formPanel.add(userTypeCombo, formGbc);

        JLabel usernameLabel = new JLabel("Username/ID:");
        usernameLabel.setFont(UITheme.BODY_FONT);
        formGbc.gridx = 0;
        formGbc.gridy = 1;
        formPanel.add(usernameLabel, formGbc);

        JTextField usernameField = new JTextField(15);
        usernameField.setFont(UITheme.BODY_FONT);
        formGbc.gridx = 1;
        formPanel.add(usernameField, formGbc);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(UITheme.BODY_FONT);
        formGbc.gridx = 0;
        formGbc.gridy = 2;
        formPanel.add(passwordLabel, formGbc);

        JPasswordField passwordField = new JPasswordField(15);
        passwordField.setFont(UITheme.BODY_FONT);
        formGbc.gridx = 1;
        formPanel.add(passwordField, formGbc);

        JButton loginButton = UITheme.createPrimaryButton("Login");
        loginButton.setPreferredSize(new Dimension(200, 40));
        formGbc.gridx = 0;
        formGbc.gridy = 3;
        formGbc.gridwidth = 2;
        formGbc.fill = GridBagConstraints.CENTER;
        formPanel.add(loginButton, formGbc);

        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.CENTER;
        mainPanel.add(formPanel, gbc);

        JLabel footerLabel = new JLabel("© 2025 Remote Health Monitoring System", SwingConstants.CENTER);
        footerLabel.setFont(UITheme.BODY_FONT);
        footerLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        gbc.gridy = 3;
        mainPanel.add(footerLabel, gbc);

        // Login button action listener
        loginButton.addActionListener(e -> {
            String userType = (String) userTypeCombo.getSelectedItem();
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());

            if (username.isEmpty() || password.isEmpty()) {
                dbManager.saveLog("Login attempt with empty credentials: " + userType);
                JOptionPane.showMessageDialog(this, 
                    "Username and password cannot be empty.", 
                    "Input Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                if (authenticate(userType, username, password)) {
                    dbManager.saveLog("Successful login: " + userType + " (" + username + ")");
                    openDashboard(userType, username);
                    dispose();
                } else {
                    dbManager.saveLog("Failed login attempt: " + userType + " (" + username + ")");
                    JOptionPane.showMessageDialog(this, 
                        "Invalid credentials. Please try again.", 
                        "Login Failed", 
                        JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                dbManager.saveLog("Login error: " + ex.getMessage());
                JOptionPane.showMessageDialog(this, 
                    "An error occurred during login: " + ex.getMessage(), 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        });

        // Forgot password button
        JButton forgotBtn = new JButton("Forgot Password?");
        forgotBtn.addActionListener(e -> {
            new ForgotPasswordRequestForm(dbManager);
        });

        add(mainPanel);
    }

    /**
     * Authenticates the user based on the user type, username, and password.
     *
     * @param userType The selected user type ("Admin", "Doctor", or "Patient")
     * @param username The user's ID or username
     * @param password The user's password
     * @return true if authentication is successful, false otherwise
     */
    private boolean authenticate(String userType, String username, String password) {
        try {
            switch (userType) {
                case "Admin":
                    Administrator adminFromDb = dbManager.getAdminByUsername(username, dbManager);
                    boolean adminAuth = adminFromDb != null && adminFromDb.authenticate(username, password);
                    dbManager.saveLog("Admin authentication for " + username + ": " + adminAuth);
                    return adminAuth;
                case "Doctor":
                    Doctor doctor = dbManager.getDoctorById(username);
                    if (doctor == null) {
                        dbManager.saveLog("Doctor not found: " + username);
                        return false;
                    }
                    boolean doctorAuth = doctor.authenticate(password);
                    dbManager.saveLog("Doctor authentication for " + username + ": " + doctorAuth);
                    return doctorAuth;
                case "Patient":
                    Patient patient = dbManager.getPatientById(username);
                    if (patient == null) {
                        dbManager.saveLog("Patient not found: " + username);
                        return false;
                    }
                    boolean patientAuth = patient.authenticate(username, password);
                    dbManager.saveLog("Patient authentication for " + username + ": " + patientAuth + 
                                      ", Password match: " + (patient.getPassword() != null && patient.getPassword().equals(password)));
                    return patientAuth;
                default:
                    dbManager.saveLog("Invalid user type: " + userType);
                    return false;
            }
        } catch (Exception e) {
            dbManager.saveLog("Authentication error for " + userType + " (" + username + "): " + e.getMessage());
            return false;
        }
    }

    /**
     * Opens the corresponding dashboard after successful login.
     *
     * @param userType The logged-in user type
     * @param username The username or ID of the user
     */
    private void openDashboard(String userType, String username) {
        try {
            switch (userType) {
                case "Admin":
                    AdminDashboard.showDashboard(admin, dbManager);
                    break;
                case "Doctor":
                    Doctor doctor = dbManager.getDoctorById(username);
                    if (doctor != null) {
                        DoctorDashboard.showDashboard(doctor, dbManager, appointmentManager, 
                                                      emergencyAlert, emailService, videoCall);
                    } else {
                        dbManager.saveLog("Doctor not found for dashboard: " + username);
                        JOptionPane.showMessageDialog(this, 
                            "Doctor not found.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                    break;
                case "Patient":
                    Patient patient = dbManager.getPatientById(username);
                    if (patient != null) {
                        ArrayList<Doctor> doctors = dbManager.getAllDoctors();
                        PatientDashboard.showDashboard(patient, doctors, appointmentManager, 
                                                       reminderService, emergencyAlert, videoCall, dbManager);
                    } else {
                        dbManager.saveLog("Patient not found for dashboard: " + username);
                        JOptionPane.showMessageDialog(this, 
                            "Patient not found.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                    break;
            }
        } catch (Exception e) {
            dbManager.saveLog("Error opening dashboard for " + userType + " (" + username + "): " + e.getMessage());
            JOptionPane.showMessageDialog(this, 
                "Error opening dashboard: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
}
