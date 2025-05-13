/**
 * The PatientDashboard class represents the main user interface for patients in the RemoteHealth application.
 * It provides access to vital signs monitoring, appointment scheduling, medical history, and emergency features.
 * 
 * @author 
 * @version 1.0
 */
package com.remotehealth.app.gui;

import com.remotehealth.app.model.*;
import com.remotehealth.app.service.*;
import com.remotehealth.app.communication.*;
import com.remotehealth.app.utils.*;
import com.remotehealth.visualization.VitalsChart;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.table.DefaultTableModel;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.awt.Dimension;
import java.io.File;

public class PatientDashboard extends JFrame {
    // Patient data and services
    private Patient patient;                          // Current patient using the dashboard
    private ArrayList<Doctor> doctors;                // List of available doctors
    private AppointmentManager appointmentManager;    // Handles appointment scheduling
    private ReminderService reminderService;          // Manages appointment reminders
    private EmergencyAlert emergencyAlert;            // Emergency alert system
    private VideoCall videoCall;                      // Video consultation service
    private DatabaseManager dbManager;                // Database access manager

    // UI Components
    private JTabbedPane tabbedPane;                   // Main tabbed interface
    private JPanel vitalSignsPanel;                   // Vital signs monitoring panel
    private JPanel appointmentsPanel;                 // Appointment management panel
    private JPanel medicalHistoryPanel;               // Medical history display panel
    private JPanel emergencyPanel;                    // Emergency features panel
    private JPanel feedbackPanel;                     // Doctor feedback panel

    /**
     * Constructs a new PatientDashboard instance.
     * 
     * @param patient The patient using this dashboard
     * @param doctors List of available doctors
     * @param appointmentManager Appointment scheduling service
     * @param reminderService Reminder notification service
     * @param emergencyAlert Emergency alert system
     * @param videoCall Video consultation service
     * @param dbManager Database access manager
     */
    public PatientDashboard(Patient patient, ArrayList<Doctor> doctors, 
                          AppointmentManager appointmentManager,
                          ReminderService reminderService,
                          EmergencyAlert emergencyAlert,
                          VideoCall videoCall, DatabaseManager dbManager) {
        this.patient = patient;
        this.doctors = doctors;
        this.appointmentManager = appointmentManager;
        this.reminderService = reminderService;
        this.emergencyAlert = emergencyAlert;
        this.videoCall = videoCall;
        this.dbManager = dbManager;

        // Window configuration
        setTitle("Patient Dashboard - " + patient.getName());
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Initialize UI components
        initComponents();
        initMenuBar();

        // Confirm before closing window
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                confirmExit();
            }
        });
    }

    /**
     * Initializes the application menu bar with file operations.
     */
    private void initMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(UITheme.PRIMARY_COLOR);
        
        // File Menu
        JMenu fileMenu = new JMenu("File");
        fileMenu.setForeground(Color.WHITE);
        fileMenu.setFont(UITheme.BUTTON_FONT);
        
        // Logout menu item
        JMenuItem logoutItem = new JMenuItem("Logout");
        logoutItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, InputEvent.CTRL_DOWN_MASK));
        logoutItem.addActionListener(e -> logout());
        styleMenuItem(logoutItem);
        
        // Exit menu item
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_DOWN_MASK));
        exitItem.addActionListener(e -> confirmExit());
        styleMenuItem(exitItem);
        
        fileMenu.add(logoutItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);
        menuBar.add(fileMenu);
        
        setJMenuBar(menuBar);
    }

    /**
     * Applies consistent styling to menu items.
     * 
     * @param item The menu item to style
     */
    private void styleMenuItem(JMenuItem item) {
        item.setBackground(Color.WHITE);
        item.setForeground(UITheme.PRIMARY_COLOR);
        item.setFont(UITheme.BODY_FONT);
        item.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
    }

    /**
     * Handles the logout process with confirmation.
     */
    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to logout?",
            "Confirm Logout",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            WindowManager.logout(this);
        }
    }

    /**
     * Confirms application exit before terminating.
     */
    private void confirmExit() {
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to exit the application?",
            "Confirm Exit",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }
    
    /**
     * Retrieves recent patient activity including vitals and appointments.
     * 
     * @return List of recent activity items
     */
    private List<Object> getRecentActivity() {
        List<Object> recentActivity = new ArrayList<>();
        
        // Add vitals as recent activity
        try {
            List<VitalSign> vitals = dbManager.getVitalsForPatient(patient.getId());
            recentActivity.addAll(vitals);
        } catch (Exception e) {
            System.err.println("Failed to load recent activity (vitals): " + e.getMessage());
        }
        
        // Add appointments as recent activity
        try {
            List<Appointment> appointments = dbManager.getAppointmentsForPatient(patient.getId());
            recentActivity.addAll(appointments);
        } catch (Exception e) {
            System.err.println("Failed to load recent activity (appointments): " + e.getMessage());
        }
        
        return recentActivity;
    }

    /**
     * Initializes all UI components and sets up the main dashboard layout.
     */
    private void initComponents() {
        getContentPane().setBackground(UITheme.SECONDARY_COLOR);
        setLayout(new BorderLayout());
        
        // Create styled tabbed pane
        tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane.setBackground(UITheme.SECONDARY_COLOR);
        tabbedPane.setForeground(UITheme.PRIMARY_COLOR);
        tabbedPane.setFont(UITheme.SUBHEADER_FONT);
        
        // Create all dashboard tabs
        createDashboardTab();
        createVitalSignsTab();
        createAppointmentsTab();
        createMedicalHistoryTab();
        createFeedbackTab();
        createVitalsChartTab();
        createEmergencyTab();
        createPrescriptionsTab();
        createPatientReportTab();
        
        add(tabbedPane, BorderLayout.CENTER);
        
        // Footer panel with logout button
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footerPanel.setBackground(UITheme.SECONDARY_COLOR);
        footerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, UITheme.PRIMARY_COLOR),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        
        JButton logoutButton = UITheme.createPrimaryButton("Logout");
        logoutButton.addActionListener(e -> logout());
        
        footerPanel.add(logoutButton);
        add(footerPanel, BorderLayout.SOUTH);
    }

    /**
     * Creates the main dashboard tab with welcome message and quick actions.
     */
    private void createDashboardTab() {
        JPanel dashboardPanel = new JPanel(new BorderLayout(10, 10));
        dashboardPanel.setBackground(UITheme.SECONDARY_COLOR);
        dashboardPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Welcome panel
        JPanel welcomePanel = UITheme.createCardPanel();
        welcomePanel.setLayout(new BorderLayout());
        JLabel welcomeLabel = new JLabel("<html><h1 style='color:" + colorToHex(UITheme.PRIMARY_COLOR) + 
                                       "'>Welcome, " + patient.getName() + "</h1></html>");
        welcomeLabel.setFont(UITheme.HEADER_FONT);
        welcomePanel.add(welcomeLabel, BorderLayout.WEST);
        
        // Quick actions panel
        JPanel quickActionsPanel = UITheme.createCardPanel();
        quickActionsPanel.setLayout(new GridLayout(3, 2, 15, 15));
        
        // Create and configure quick action buttons
        JButton requestAppointmentBtn = createDashboardButton("Request Appointment");
        JButton viewVitalsBtn = createDashboardButton("View Vital Signs");
        JButton viewHistoryBtn = createDashboardButton("View Medical History");
        JButton viewFeedbackBtn = createDashboardButton("View Doctor Feedback");
        JButton emergencyBtn = createDashboardButton("Emergency Assistance"); 
        JButton viewPrescriptionsBtn = createDashboardButton("View Prescriptions");
        
        // Set button actions to navigate to corresponding tabs
        requestAppointmentBtn.addActionListener(e -> tabbedPane.setSelectedIndex(2));
        viewVitalsBtn.addActionListener(e -> tabbedPane.setSelectedIndex(1));
        viewHistoryBtn.addActionListener(e -> tabbedPane.setSelectedIndex(3));
        viewFeedbackBtn.addActionListener(e -> tabbedPane.setSelectedIndex(4));
        emergencyBtn.addActionListener(e -> tabbedPane.setSelectedIndex(5));
        viewPrescriptionsBtn.addActionListener(e -> tabbedPane.setSelectedIndex(6));
        
        // Add buttons to panel
        quickActionsPanel.add(requestAppointmentBtn);
        quickActionsPanel.add(viewVitalsBtn);
        quickActionsPanel.add(viewHistoryBtn);
        quickActionsPanel.add(viewFeedbackBtn);
        quickActionsPanel.add(emergencyBtn);
        quickActionsPanel.add(viewPrescriptionsBtn);
        
        // Recent activity panel
        JPanel recentActivityPanel = UITheme.createCardPanel();
        recentActivityPanel.setLayout(new BorderLayout());
        recentActivityPanel.setBorder(BorderFactory.createTitledBorder("Recent Activity"));
        
        JTextArea activityArea = new JTextArea();
        activityArea.setEditable(false);
        activityArea.setFont(UITheme.BODY_FONT);
        StringBuilder sb = new StringBuilder();
        for (Object item : getRecentActivity()) {
            sb.append(item.toString()).append("\n\n");
        }
        activityArea.setText(sb.toString());

        recentActivityPanel.add(new JScrollPane(activityArea), BorderLayout.CENTER);
        
        // Assemble dashboard components
        dashboardPanel.add(welcomePanel, BorderLayout.NORTH);
        
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 15, 15));
        centerPanel.setBackground(UITheme.SECONDARY_COLOR);
        centerPanel.add(quickActionsPanel);
        centerPanel.add(recentActivityPanel);
        
        dashboardPanel.add(centerPanel, BorderLayout.CENTER);
        tabbedPane.addTab("Dashboard", dashboardPanel);
    }

    /**
     * Creates a styled dashboard button with consistent appearance.
     * 
     * @param text The button text
     * @return Configured JButton instance
     */
    private JButton createDashboardButton(String text) {
        JButton button = new JButton(text);
        button.setFont(UITheme.BUTTON_FONT);
        button.setBackground(UITheme.PRIMARY_COLOR);
        button.setForeground(Color.WHITE);
        button.setVerticalTextPosition(SwingConstants.BOTTOM);
        button.setHorizontalTextPosition(SwingConstants.CENTER);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(15, 5, 15, 5));
        return button;
    }

    /**
     * Creates the vital signs monitoring tab with input form and history display.
     */
    private void createVitalSignsTab() {
        vitalSignsPanel = new JPanel(new BorderLayout(10, 10));
        vitalSignsPanel.setBackground(UITheme.SECONDARY_COLOR);
        vitalSignsPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Vital signs input form card
        JPanel inputPanel = UITheme.createCardPanel();
        inputPanel.setLayout(new GridLayout(5, 2, 10, 10));
        
        // Create form fields
        JLabel heartRateLabel = new JLabel("Heart Rate (bpm):");
        heartRateLabel.setFont(UITheme.BODY_FONT);
        JTextField heartRateField = new JTextField();
        
        JLabel oxygenLabel = new JLabel("Oxygen Level (%):");
        oxygenLabel.setFont(UITheme.BODY_FONT);
        JTextField oxygenField = new JTextField();
        
        JLabel bloodPressureLabel = new JLabel("Blood Pressure:");
        bloodPressureLabel.setFont(UITheme.BODY_FONT);
        JTextField bloodPressureField = new JTextField();
        
        JLabel tempLabel = new JLabel("Temperature (°C):");
        tempLabel.setFont(UITheme.BODY_FONT);
        JTextField tempField = new JTextField();
        
        JLabel dateLabel = new JLabel("Date (yyyy-MM-dd):");
        dateLabel.setFont(UITheme.BODY_FONT);
        JTextField dateField = new JTextField();

        // Add fields to input panel
        inputPanel.add(heartRateLabel);
        inputPanel.add(heartRateField);
        inputPanel.add(oxygenLabel);
        inputPanel.add(oxygenField);
        inputPanel.add(bloodPressureLabel);
        inputPanel.add(bloodPressureField);
        inputPanel.add(tempLabel);
        inputPanel.add(tempField);
        inputPanel.add(dateLabel);
        inputPanel.add(dateField);

        // Submit button for vital signs
        JButton submitBtn = UITheme.createPrimaryButton("Submit Vital Signs");
        submitBtn.addActionListener(e -> {
            try {
                // Parse and validate input
                int hr = Integer.parseInt(heartRateField.getText());
                int ox = Integer.parseInt(oxygenField.getText());
                String bp = bloodPressureField.getText();
                double temp = Double.parseDouble(tempField.getText());
                java.sql.Date addedOn = java.sql.Date.valueOf(dateField.getText());

                // Save vitals to database
                dbManager.saveVitals(patient.getId(), new VitalSign(hr, ox, bp, temp, addedOn));
                JOptionPane.showMessageDialog(this, "Vital signs recorded successfully!");
                
                // Clear input fields
                heartRateField.setText("");
                oxygenField.setText("");
                bloodPressureField.setText("");
                tempField.setText("");
                dateField.setText("");
                
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter valid numbers!", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        // Vital signs history card
        JPanel historyPanel = UITheme.createCardPanel();
        historyPanel.setLayout(new BorderLayout());
        historyPanel.setBorder(BorderFactory.createTitledBorder("Vital Signs History"));
        
        JTextArea vitalsArea = new JTextArea();
        vitalsArea.setEditable(false);
        vitalsArea.setFont(UITheme.BODY_FONT);
        updateVitalsDisplay(vitalsArea);
        
        JButton refreshBtn = UITheme.createStyledButton("Refresh", UITheme.ACCENT_COLOR);
        refreshBtn.addActionListener(e -> updateVitalsDisplay(vitalsArea));
        
        JPanel historyControlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        historyControlPanel.setBackground(Color.WHITE);
        historyControlPanel.add(refreshBtn);
        
        historyPanel.add(new JScrollPane(vitalsArea), BorderLayout.CENTER);
        historyPanel.add(historyControlPanel, BorderLayout.SOUTH);
        
        // Assemble vital signs tab
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(inputPanel, BorderLayout.CENTER);
        topPanel.add(submitBtn, BorderLayout.SOUTH);
        
        vitalSignsPanel.add(topPanel, BorderLayout.NORTH);
        vitalSignsPanel.add(historyPanel, BorderLayout.CENTER);
        
        tabbedPane.addTab("Vital Signs", vitalSignsPanel);
    }

    /**
     * Creates the appointments management tab with request form and list display.
     */
    private void createAppointmentsTab() {
        appointmentsPanel = new JPanel(new BorderLayout(10, 10));
        appointmentsPanel.setBackground(UITheme.SECONDARY_COLOR);
        appointmentsPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Appointment request form card
        JPanel requestPanel = UITheme.createCardPanel();
        requestPanel.setLayout(new GridLayout(5, 2, 10, 10));
        
        // Create form fields
        JLabel dateLabel = new JLabel("Date (YYYY-MM-DD):");
        dateLabel.setFont(UITheme.BODY_FONT);
        JTextField dateField = new JTextField();
        
        JLabel timeLabel = new JLabel("Time (HH:MM):");
        timeLabel.setFont(UITheme.BODY_FONT);
        JTextField timeField = new JTextField();
        
        JLabel doctorLabel = new JLabel("Doctor:");
        doctorLabel.setFont(UITheme.BODY_FONT);
        
        // Doctor selection combo box
        JComboBox<Doctor> doctorCombo = new JComboBox<>(doctors.toArray(new Doctor[0]));
        doctorCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Doctor) {
                    setText(((Doctor)value).getName() + " (" + ((Doctor)value).getId() + ")");
                }
                return this;
            }
        });
        doctorCombo.setFont(UITheme.BODY_FONT);
        
        // Add fields to request panel
        requestPanel.add(dateLabel);
        requestPanel.add(dateField);
        requestPanel.add(timeLabel);
        requestPanel.add(timeField);
        requestPanel.add(doctorLabel);
        requestPanel.add(doctorCombo);
        
        // Appointment request button
        JButton requestBtn = UITheme.createPrimaryButton("Request Appointment");
        requestBtn.addActionListener(e -> {
            try {
                // Parse and validate input
                String dateStr = dateField.getText();
                String timeStr = timeField.getText(); 
                LocalDateTime dateTime = LocalDateTime.parse(
                dateStr + "T" + timeStr,  // Using ISO format with 'T' separator
                DateTimeFormatter.ISO_LOCAL_DATE_TIME
                );
                Doctor selectedDoctor = (Doctor)doctorCombo.getSelectedItem();
                if (selectedDoctor == null) {
                JOptionPane.showMessageDialog(this, "Please select a doctor", "Error", JOptionPane.ERROR_MESSAGE);
                return;
                }
            
                // Create and save appointment
                Appointment appointment = new Appointment(dateTime, patient, selectedDoctor);
                appointmentManager.requestAppointment(appointment);
                dbManager.saveAppointment(appointment);
            
                JOptionPane.showMessageDialog(this, "Appointment requested successfully!");
                dateField.setText("");
                timeField.setText("");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, 
                "Invalid date/time format. Please use YYYY-MM-DD for date and HH:MM for time", 
                "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        // Appointments list card
        JPanel listPanel = UITheme.createCardPanel();
        listPanel.setLayout(new BorderLayout());
        listPanel.setBorder(BorderFactory.createTitledBorder("Your Appointments"));
        
        JTextArea appointmentsArea = new JTextArea();
        appointmentsArea.setEditable(false);
        appointmentsArea.setFont(UITheme.BODY_FONT);
        updateAppointmentsDisplay(appointmentsArea);
        
        JButton refreshBtn = UITheme.createStyledButton("Refresh", UITheme.ACCENT_COLOR);
        refreshBtn.addActionListener(e -> updateAppointmentsDisplay(appointmentsArea));
        
        JPanel listControlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        listControlPanel.setBackground(Color.WHITE);
        listControlPanel.add(refreshBtn);
        
        listPanel.add(new JScrollPane(appointmentsArea), BorderLayout.CENTER);
        listPanel.add(listControlPanel, BorderLayout.SOUTH);
        
        // Assemble appointments tab
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(requestPanel, BorderLayout.CENTER);
        topPanel.add(requestBtn, BorderLayout.SOUTH);
        
        appointmentsPanel.add(topPanel, BorderLayout.NORTH);
        appointmentsPanel.add(listPanel, BorderLayout.CENTER);
        
        tabbedPane.addTab("Appointments", appointmentsPanel);
    }

    /**
     * Creates the medical history display tab.
     */
    private void createMedicalHistoryTab() {
        medicalHistoryPanel = new JPanel(new BorderLayout(10, 10));
        medicalHistoryPanel.setBackground(UITheme.SECONDARY_COLOR);
        medicalHistoryPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Medical history display card
        JPanel historyPanel = UITheme.createCardPanel();
        historyPanel.setLayout(new BorderLayout());
        
        JTextArea historyArea = new JTextArea();
        historyArea.setEditable(false);
        historyArea.setFont(UITheme.BODY_FONT);
        updateMedicalHistoryDisplay(historyArea);
        
        JButton refreshBtn = UITheme.createStyledButton("Refresh", UITheme.ACCENT_COLOR);
        refreshBtn.addActionListener(e -> updateMedicalHistoryDisplay(historyArea));
        
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        controlPanel.setBackground(Color.WHITE);
        controlPanel.add(refreshBtn);
        
        historyPanel.add(new JScrollPane(historyArea), BorderLayout.CENTER);
        historyPanel.add(controlPanel, BorderLayout.SOUTH);
        
        medicalHistoryPanel.add(historyPanel, BorderLayout.CENTER);
        tabbedPane.addTab("Medical History", medicalHistoryPanel);
    }
    
    /**
     * Creates the vitals chart visualization tab.
     */
    private void createVitalsChartTab() {
        JPanel vitalsChartPanel = new JPanel(new BorderLayout(10, 10));
        vitalsChartPanel.setBackground(UITheme.SECONDARY_COLOR);
        vitalsChartPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Create the VitalsChart visualization
        VitalsChart vitalsChart = new VitalsChart(patient.getId(), dbManager);

        // Set chart dimensions
        vitalsChart.setPreferredSize(new Dimension(580, 300));
        vitalsChart.setMaximumSize(new Dimension(580, 300));

        // Add chart to scroll pane
        JScrollPane scrollPane = new JScrollPane(vitalsChart);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

        vitalsChartPanel.add(scrollPane, BorderLayout.CENTER);
        tabbedPane.addTab("Vitals Chart", vitalsChartPanel);
    }

    /**
     * Creates the emergency assistance tab with panic button and vital monitoring.
     */
    private void createEmergencyTab() {
        emergencyPanel = new JPanel(new BorderLayout(10, 10));
        emergencyPanel.setBackground(UITheme.SECONDARY_COLOR);
        emergencyPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Emergency buttons card
        JPanel buttonPanel = UITheme.createCardPanel();
        buttonPanel.setLayout(new GridLayout(2, 1, 10, 20));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(50, 100, 50, 100));
        
        // Panic button configuration
        JButton panicButton = new JButton("PANIC BUTTON");
        panicButton.setFont(new Font("Arial", Font.BOLD, 24));
        panicButton.setForeground(Color.WHITE);
        panicButton.setBackground(UITheme.DANGER_COLOR);
        panicButton.setOpaque(true);
        panicButton.setBorderPainted(false);
        panicButton.addActionListener(e -> {
            emergencyAlert.activatePanicButton(patient);
            JOptionPane.showMessageDialog(this, 
                "Emergency alert activated! Help is on the way.", 
                "EMERGENCY", JOptionPane.WARNING_MESSAGE);
            
            emergencyAlert.triggerAlert(patient);
        });
        
        // Video call button configuration
        JButton videoCallButton = UITheme.createPrimaryButton("Video Call Doctor");
        videoCallButton.setFont(UITheme.BUTTON_FONT);
        videoCallButton.addActionListener(e -> {
            String password = JOptionPane.showInputDialog(
                this,
                "Set a password for the video call (leave empty for no password):",
                "Video Call Security",
                JOptionPane.QUESTION_MESSAGE);
            
            videoCall.startCall(patient.getName(), "Emergency Doctor", password);
        });
        
        // Vital signs monitoring panel
        JPanel monitoringPanel = UITheme.createCardPanel();
        monitoringPanel.setLayout(new GridLayout(1, 2, 10, 10));
        
        // Current vitals display
        JPanel currentVitalsPanel = new JPanel(new BorderLayout());
        currentVitalsPanel.setBorder(BorderFactory.createTitledBorder("Current Vitals"));
        
        JTextArea vitalsArea = new JTextArea();
        vitalsArea.setEditable(false);
        vitalsArea.setFont(UITheme.BODY_FONT);
        updateCurrentVitalsDisplay(vitalsArea);
        
        currentVitalsPanel.add(new JScrollPane(vitalsArea), BorderLayout.CENTER);
        
        // Emergency instructions display
        JTextArea instructionsArea = new JTextArea();
        instructionsArea.setEditable(false);
        instructionsArea.setText("EMERGENCY PROTOCOL:\n\n" +
                               "1. Press PANIC BUTTON for immediate help\n" +
                               "2. Use Video Call to connect with doctor\n" +
                               "3. Check your vital signs\n" +
                               "4. Stay calm and wait for assistance\n\n" +
                               "Critical Thresholds:\n" +
                               "- Heart Rate: <40 or >120 bpm\n" +
                               "- Oxygen: <90%\n" +
                               "- Temperature: <35°C or >40°C");
        instructionsArea.setFont(UITheme.BODY_FONT);
        
        monitoringPanel.add(currentVitalsPanel);
        monitoringPanel.add(new JScrollPane(instructionsArea));
        
        buttonPanel.add(panicButton);
        buttonPanel.add(videoCallButton);
        
        emergencyPanel.add(buttonPanel, BorderLayout.NORTH);
        emergencyPanel.add(monitoringPanel, BorderLayout.CENTER);
        
        tabbedPane.addTab("Emergency", emergencyPanel);
    }
    
    /**
     * Creates the doctor feedback display tab.
     */
    private void createFeedbackTab() {
        feedbackPanel = new JPanel(new BorderLayout(10, 10));
        feedbackPanel.setBackground(UITheme.SECONDARY_COLOR);
        feedbackPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Feedback display card
        JPanel feedbackCard = UITheme.createCardPanel();
        feedbackCard.setLayout(new BorderLayout());
        
        JTextArea feedbackArea = new JTextArea();
        feedbackArea.setEditable(false);
        feedbackArea.setFont(UITheme.BODY_FONT);
        updateFeedbackDisplay(feedbackArea);

        // Refresh button
        JButton refreshBtn = UITheme.createStyledButton("Refresh Feedback", UITheme.ACCENT_COLOR);
        refreshBtn.addActionListener(e -> updateFeedbackDisplay(feedbackArea));

        // Control panel
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        controlPanel.setBackground(Color.WHITE);
        controlPanel.add(refreshBtn);

        // Assemble components
        feedbackCard.add(new JScrollPane(feedbackArea), BorderLayout.CENTER);
        feedbackCard.add(controlPanel, BorderLayout.SOUTH);

        feedbackPanel.add(feedbackCard, BorderLayout.CENTER);
        tabbedPane.addTab("Doctor Feedback", feedbackPanel);
    }
    
    /**
     * Converts a Color object to its hexadecimal string representation.
     * 
     * @param color The color to convert
     * @return Hexadecimal color string
     */
    private String colorToHex(Color color) {
        return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
    }
    
        /**
     * Creates the report generator tab with to generate reports
     */
    private void createPatientReportTab() {
    JPanel reportPanel = new JPanel(new BorderLayout(10, 10));
    reportPanel.setBackground(UITheme.SECONDARY_COLOR);
    reportPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

    JLabel reportLabel = new JLabel("Download Your Health Report");
    reportLabel.setFont(UITheme.HEADER_FONT);
    reportLabel.setHorizontalAlignment(SwingConstants.CENTER);

    JButton downloadReportBtn = UITheme.createPrimaryButton("Download Report");
    downloadReportBtn.setFont(UITheme.BODY_FONT);
    downloadReportBtn.setPreferredSize(new Dimension(200, 40));
    downloadReportBtn.addActionListener(e -> {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Patient Report");

        String defaultFileName = "PatientReport_" + patient.getId() + ".pdf";
        fileChooser.setSelectedFile(new File(defaultFileName));

        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            try {
                PatientReports.generateReport(patient.getId(), fileToSave.getAbsolutePath(), dbManager);
                JOptionPane.showMessageDialog(this,
                    "Patient report saved to:\n" + fileToSave.getAbsolutePath(),
                    "Report Generated", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this,
                    "Failed to generate report: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    });

    JPanel centerPanel = new JPanel();
    centerPanel.setBackground(UITheme.SECONDARY_COLOR);
    centerPanel.add(downloadReportBtn);

    reportPanel.add(reportLabel, BorderLayout.NORTH);
    reportPanel.add(centerPanel, BorderLayout.CENTER);

    tabbedPane.addTab("Reports", reportPanel);
}
    
    /**
     * Creates the prescriptions management tab with list and details view.
     */
    private void createPrescriptionsTab() {
        JPanel prescriptionsPanel = new JPanel(new BorderLayout(10, 10));
        prescriptionsPanel.setBackground(UITheme.SECONDARY_COLOR);
        prescriptionsPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Prescriptions list panel
        JPanel listPanel = UITheme.createCardPanel();
        listPanel.setLayout(new BorderLayout());
        listPanel.setBorder(BorderFactory.createTitledBorder("Your Prescriptions"));

        // Create prescriptions table model
        String[] columnNames = {"Medication", "Dosage", "Schedule", "Prescribed By", "Tests"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Populate table with patient's prescriptions
        for (Prescription prescription : dbManager.getPrescriptionsForPatient(patient.getId())) {
            Object[] rowData = {
                prescription.getMedication(),
                prescription.getDosage(),
                prescription.getSchedule(),
                prescription.getPrescribingDoctor(),
                prescription.getTests()
            };
            model.addRow(rowData);
        }

        // Configure prescriptions table
        JTable prescriptionsTable = new JTable(model);
        prescriptionsTable.setFont(UITheme.BODY_FONT);
        prescriptionsTable.setRowHeight(30);
        prescriptionsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Prescription details panel
        JPanel detailsPanel = UITheme.createCardPanel();
        detailsPanel.setLayout(new BorderLayout());
        detailsPanel.setBorder(BorderFactory.createTitledBorder("Prescription Details"));

        JTextArea detailsArea = new JTextArea();
        detailsArea.setEditable(false);
        detailsArea.setFont(UITheme.BODY_FONT);
        detailsArea.setLineWrap(true);
        detailsArea.setWrapStyleWord(true);

        // Add selection listener to show details when prescription is selected
        prescriptionsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = prescriptionsTable.getSelectedRow();
                if (selectedRow >= 0) {
                    Prescription selected = dbManager.getPrescriptionsForPatient(patient.getId()).get(selectedRow);
                    detailsArea.setText(selected.toString());
                }
            }
        });

        // Add components to details panel
        detailsPanel.add(new JScrollPane(detailsArea), BorderLayout.CENTER);

        // Create split pane for list and details
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
            new JScrollPane(prescriptionsTable),
            detailsPanel);
        splitPane.setDividerLocation(300);
        splitPane.setResizeWeight(0.5);

        prescriptionsPanel.add(splitPane, BorderLayout.CENTER);
        tabbedPane.addTab("Prescriptions", prescriptionsPanel);
    }

    /**
     * Updates the current vitals display and checks for critical values.
     * 
     * @param area The text area to update with vitals information
     */
    private void updateCurrentVitalsDisplay(JTextArea area) {
        List<VitalSign> vitals = dbManager.getVitalsForPatient(patient.getId());
        if (vitals.isEmpty()) {
            area.setText("No vital signs recorded yet");
            return;
        }
        
        // Get latest vitals
        VitalSign latest = vitals.get(vitals.size() - 1);
        StringBuilder sb = new StringBuilder();
        sb.append("Last Recorded:\n");
        sb.append(latest.toString()).append("\n\n");
        
        // Check for critical values
        boolean critical = false;
        if (latest.getHeartRate() < 40 || latest.getHeartRate() > 120) {
            sb.append("CRITICAL HEART RATE ⚠️\n");
            critical = true;
        }
        if (latest.getOxygenLevel() < 90) {
            sb.append("CRITICAL OXYGEN LEVEL ⚠️\n");
            critical = true;
        }
        if (latest.getTemperature() < 35 || latest.getTemperature() > 40) {
            sb.append("CRITICAL TEMPERATURE ⚠️\n");
            critical = true;
        }
        
        if (critical) {
            sb.append("\nEmergency alert will be automatically triggered!");
        }
        
        area.setText(sb.toString());
        
        // Trigger emergency alert if critical values detected
        if (critical) {
            emergencyAlert.triggerAlert(patient);
        }
    }

    /**
     * Updates the vitals history display.
     * 
     * @param area The text area to update with vitals history
     */
    private void updateVitalsDisplay(JTextArea area) {
        StringBuilder sb = new StringBuilder();
        for (VitalSign vital : dbManager.getVitalsForPatient(patient.getId())) {
            sb.append(vital).append("\n\n");
        }
        area.setText(sb.toString());
    }

    /**
     * Updates the appointments display.
     * 
     * @param area The text area to update with appointments information
     */
    private void updateAppointmentsDisplay(JTextArea area) {
        StringBuilder sb = new StringBuilder();
        for (Appointment appt : dbManager.getAppointmentsForPatient(patient.getId())) {
            sb.append("Appointment with Dr. ").append(appt.getDoctor().getName()).append("\n")
              .append("Date: ").append(appt.getDateTime().toLocalDate()).append("\n")
              .append("Time: ").append(appt.getDateTime().toLocalTime()).append("\n")
              .append("Status: ").append(appt.getStatus()).append("\n\n");
        }
        area.setText(sb.length() > 0 ? sb.toString() : "No appointments scheduled");
    }
    
    /**
     * Updates the medical history display.
     * 
     * @param area The text area to update with medical history
     */
    private void updateMedicalHistoryDisplay(JTextArea area) {
        area.setText(patient.getMedicalHistory().getFormattedHistory(dbManager, patient.getId()));
    }
    
    /**
     * Updates the doctor feedback display.
     * 
     * @param area The text area to update with feedback information
     */
    private void updateFeedbackDisplay(JTextArea area) {
        ArrayList<Feedback> feedbacks = dbManager.getFeedbacksForPatient(patient.getId());
        if (feedbacks.isEmpty()) {
            area.setText("No feedback available from doctors yet.");
        } else {
            StringBuilder sb = new StringBuilder();
            for (Feedback f : feedbacks) {
                sb.append(f.toString()).append("\n\n");
            }
            area.setText(sb.toString());
        }
    }

    /**
     * Displays the patient dashboard window.
     * 
     * @param patient The patient to display dashboard for
     * @param doctors List of available doctors
     * @param appointmentManager Appointment scheduling service
     * @param reminderService Reminder notification service
     * @param emergencyAlert Emergency alert system
     * @param videoCall Video consultation service
     * @param dbManager Database access manager
     */
    public static void showDashboard(Patient patient, ArrayList<Doctor> doctors, 
                                   AppointmentManager appointmentManager,
                                   ReminderService reminderService,
                                   EmergencyAlert emergencyAlert,
                                   VideoCall videoCall, DatabaseManager dbManager) {
        SwingUtilities.invokeLater(() -> {
            PatientDashboard dashboard = new PatientDashboard(patient, doctors, 
                                                           appointmentManager,
                                                           reminderService,
                                                           emergencyAlert,
                                                           videoCall, dbManager);
            dashboard.setVisible(true);
        });
    }
}