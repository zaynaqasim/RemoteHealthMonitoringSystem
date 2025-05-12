/**
 * The DoctorDashboard class represents the main user interface for doctors in the RemoteHealth application.
 * It provides functionality for managing patients, appointments, prescriptions, and emergency alerts.
 * 
 * @author 
 * @version 1.0
 */
package com.remotehealth.app.gui;

import com.remotehealth.app.model.*;
import com.remotehealth.app.service.*;
import com.remotehealth.app.utils.*;
import com.remotehealth.app.communication.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class DoctorDashboard extends JFrame {
    // Doctor data and services
    private Doctor doctor;                          // Current doctor using the dashboard
    private ArrayList<Patient> patients;            // List of available patients
    private AppointmentManager appointmentManager;  // Handles appointment scheduling
    private EmergencyAlert emergencyAlert;          // Emergency alert system
    private EmailNotification emailService;         // Email notification service
    private VideoCall videoCall;                    // Video consultation service
    private DatabaseManager dbManager;              // Database access manager

    // UI Components
    private JTabbedPane tabbedPane;                 // Main tabbed interface
    private JPanel dashboardPanel;                  // Dashboard overview panel
    private JPanel patientsPanel;                   // Patient management panel
    private JPanel appointmentsPanel;               // Appointment management panel
    private JPanel feedbackPanel;                   // Patient feedback panel
    private JPanel emergencyPanel;                  // Emergency alerts panel

    /**
     * Constructs a new DoctorDashboard instance.
     * 
     * @param doctor The doctor using this dashboard
     * @param dbManager Database access manager
     * @param appointmentManager Appointment scheduling service
     * @param emergencyAlert Emergency alert system
     * @param emailService Email notification service
     * @param videoCall Video consultation service
     */
    public DoctorDashboard(Doctor doctor, DatabaseManager dbManager, AppointmentManager appointmentManager, 
                         EmergencyAlert emergencyAlert, EmailNotification emailService, VideoCall videoCall) {
        this.doctor = doctor;
        this.dbManager = dbManager;
        this.appointmentManager = appointmentManager;
        this.emergencyAlert = emergencyAlert;
        this.emailService = emailService;
        this.videoCall = videoCall;
        
        // Initialize services
        ReminderService reminderService = new ReminderService(emailService, appointmentManager, dbManager);
        appointmentManager.setReminderService(reminderService);
        
        // Load patient data
        this.patients = dbManager.getAllPatients();
        
        // Window configuration
        setTitle("Doctor Dashboard - Dr. " + doctor.getName());
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Confirm before closing window
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                confirmExit();
            }
        });

        // Initialize UI components
        initComponents();
        initMenuBar();
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
     * Initializes all UI components and sets up the main dashboard layout.
     */
    private void initComponents() {
        // Main container with theme colors
        getContentPane().setBackground(UITheme.SECONDARY_COLOR);
        setLayout(new BorderLayout());
        
        // Create styled tabbed pane
        tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane.setBackground(UITheme.SECONDARY_COLOR);
        tabbedPane.setForeground(UITheme.PRIMARY_COLOR);
        tabbedPane.setFont(UITheme.SUBHEADER_FONT);
        
        // Create all dashboard tabs
        createDashboardTab();
        createPatientsTab();
        createAppointmentsTab();
        createFeedbackTab();
        createEmergencyTab();
        createPrescribeMedicationTab();
        
        add(tabbedPane, BorderLayout.CENTER);
        
        // Footer panel with logout button
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footerPanel.setBackground(UITheme.SECONDARY_COLOR);
        footerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, UITheme.PRIMARY_COLOR),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        
        JButton logoutButton = UITheme.createStyledButton("Logout", UITheme.PRIMARY_COLOR);
        logoutButton.addActionListener(e -> logout());
        
        footerPanel.add(logoutButton);
        add(footerPanel, BorderLayout.SOUTH);
    }

    /**
     * Creates the main dashboard tab with welcome message and quick actions.
     */
    private void createDashboardTab() {
        dashboardPanel = new JPanel(new BorderLayout(10, 10));
        dashboardPanel.setBackground(UITheme.SECONDARY_COLOR);
        dashboardPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Welcome panel
        JPanel welcomePanel = UITheme.createCardPanel();
        welcomePanel.setLayout(new BorderLayout());
        JLabel welcomeLabel = new JLabel("<html><h1 style='color:" + colorToHex(UITheme.PRIMARY_COLOR) + 
                                       "'>Welcome, Dr. " + doctor.getName() + "</h1></html>");
        welcomeLabel.setFont(UITheme.HEADER_FONT);
        welcomePanel.add(welcomeLabel, BorderLayout.WEST);
        
        // Quick actions panel
        JPanel quickActionsPanel = UITheme.createCardPanel();
        quickActionsPanel.setLayout(new GridLayout(3, 2, 15, 15));
        
        // Create and configure quick action buttons
        JButton viewPatientsBtn = createDashboardButton("View Patients");
        JButton viewAppointmentsBtn = createDashboardButton("View Appointments");
        JButton giveFeedbackBtn = createDashboardButton("Provide Feedback");
        JButton emergencyBtn = createDashboardButton("Emergency Alerts");
        JButton prescribeBtn = createDashboardButton("Prescribe Medication");
        
        // Set button actions to navigate to corresponding tabs
        viewPatientsBtn.addActionListener(e -> tabbedPane.setSelectedIndex(1));
        viewAppointmentsBtn.addActionListener(e -> tabbedPane.setSelectedIndex(2));
        giveFeedbackBtn.addActionListener(e -> tabbedPane.setSelectedIndex(3));
        emergencyBtn.addActionListener(e -> tabbedPane.setSelectedIndex(4));
        prescribeBtn.addActionListener(e -> tabbedPane.setSelectedIndex(5));
        
        // Add buttons to panel
        quickActionsPanel.add(viewPatientsBtn);
        quickActionsPanel.add(viewAppointmentsBtn);
        quickActionsPanel.add(giveFeedbackBtn);
        quickActionsPanel.add(emergencyBtn);
        quickActionsPanel.add(prescribeBtn);
        
        // Recent activity panel
        JPanel recentActivityPanel = UITheme.createCardPanel();
        recentActivityPanel.setLayout(new BorderLayout());
        recentActivityPanel.setBorder(BorderFactory.createTitledBorder("Recent Activity"));
        
        JTextArea activityArea = new JTextArea();
        activityArea.setEditable(false);
        activityArea.setFont(UITheme.BODY_FONT);
        activityArea.setText(getRecentActivity());
        
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
     * Creates the patient management tab with list and details view.
     */
    private void createPatientsTab() {
        patientsPanel = new JPanel(new BorderLayout(10, 10));
        patientsPanel.setBackground(UITheme.SECONDARY_COLOR);
        patientsPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Patients list panel
        JPanel listPanel = UITheme.createCardPanel();
        listPanel.setLayout(new BorderLayout());
        listPanel.setBorder(BorderFactory.createTitledBorder("Patient List"));
        
        // Create patient list model
        DefaultListModel<String> patientListModel = new DefaultListModel<>();
        for (Patient patient : patients) {
            patientListModel.addElement(patient.getId() + ": " + patient.getName());
        }
        
        // Configure patient list
        JList<String> patientList = new JList<>(patientListModel);
        patientList.setFont(UITheme.BODY_FONT);
        patientList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        patientList.setBackground(Color.WHITE);
        
        // Patient details panel
        JPanel detailsPanel = UITheme.createCardPanel();
        detailsPanel.setLayout(new BorderLayout());
        detailsPanel.setBorder(BorderFactory.createTitledBorder("Patient Details"));
        
        JTextArea detailsArea = new JTextArea();
        detailsArea.setEditable(false);
        detailsArea.setFont(UITheme.BODY_FONT);
        
        // Add selection listener to show details when patient is selected
        patientList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int index = patientList.getSelectedIndex();
                if (index >= 0) {
                    Patient selectedPatient = patients.get(index);
                    detailsArea.setText(getPatientDetails(selectedPatient));
                }
            }
        });
        
        // Assemble patients tab
        listPanel.add(new JScrollPane(patientList), BorderLayout.CENTER);
        detailsPanel.add(new JScrollPane(detailsArea), BorderLayout.CENTER);
        
        // Create split pane for list and details
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, listPanel, detailsPanel);
        splitPane.setDividerLocation(300);
        splitPane.setBackground(UITheme.SECONDARY_COLOR);
        
        patientsPanel.add(splitPane, BorderLayout.CENTER);
        tabbedPane.addTab("Patients", patientsPanel);
    }

    /**
     * Creates the appointment management tab with filtering and actions.
     */
    private void createAppointmentsTab() {
        appointmentsPanel = new JPanel(new BorderLayout(10, 10));
        appointmentsPanel.setBackground(UITheme.SECONDARY_COLOR);
        appointmentsPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Appointments list with selection
        JPanel listPanel = UITheme.createCardPanel();
        listPanel.setLayout(new BorderLayout());
        listPanel.setBorder(BorderFactory.createTitledBorder("Your Appointments"));
        
        // Create appointments list model
        DefaultListModel<Appointment> listModel = new DefaultListModel<>();
        JList<Appointment> appointmentsList = new JList<>(listModel);
        appointmentsList.setCellRenderer(new AppointmentListRenderer());
        appointmentsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        appointmentsList.setFont(UITheme.BODY_FONT);
        
        // Initialize with all appointments
        for (Appointment appt : dbManager.getAppointmentsForDoctor(doctor.getName())) {
            listModel.addElement(appt);
        }
        
        // Filter buttons panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        filterPanel.setBackground(Color.WHITE);
        
        // Create filter buttons
        JButton allBtn = createFilterButton("All");
        JButton pendingBtn = createFilterButton("Pending");
        JButton upcomingBtn = createFilterButton("Upcoming");

        // Configure filter button actions
        allBtn.addActionListener(e -> {
            listModel.clear();
            for (Appointment appt : dbManager.getAppointmentsForDoctor(doctor.getName())) {
                listModel.addElement(appt);
            }
        });

        pendingBtn.addActionListener(e -> {
            listModel.clear();
            for (Appointment appt : dbManager.getAppointmentsForDoctor(doctor.getName())) {
                listModel.addElement(appt);
            }
        });

        upcomingBtn.addActionListener(e -> {
            listModel.clear();
            for (Appointment appt : dbManager.getAppointmentsForDoctor(doctor.getName())) {
                listModel.addElement(appt);
            }
        });

        // Add filter components to panel
        filterPanel.add(new JLabel("Filter:"));
        filterPanel.add(allBtn);
        filterPanel.add(pendingBtn);
        filterPanel.add(upcomingBtn);
        
        // Appointment actions panel
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        actionPanel.setBackground(Color.WHITE);
        
        // Create action buttons
        JButton approveBtn = createActionButton("Approve", UITheme.SUCCESS_COLOR);
        JButton rejectBtn = createActionButton("Reject", UITheme.DANGER_COLOR);
        JButton rescheduleBtn = createActionButton("Reschedule", UITheme.ACCENT_COLOR);
        JButton refreshBtn = createActionButton("Refresh", UITheme.PRIMARY_COLOR);
        
        // Configure action button handlers
        approveBtn.addActionListener(e -> handleAppointmentApproval(appointmentsList, listModel));
        rejectBtn.addActionListener(e -> handleAppointmentRejection(appointmentsList, listModel));
        rescheduleBtn.addActionListener(e -> handleAppointmentReschedule(appointmentsList, listModel));
        refreshBtn.addActionListener(e -> refreshAppointmentsList(listModel));
        
        // Add buttons to panel
        actionPanel.add(approveBtn);
        actionPanel.add(rejectBtn);
        actionPanel.add(rescheduleBtn);
        actionPanel.add(refreshBtn);
        
        // Assemble appointments tab
        listPanel.add(filterPanel, BorderLayout.NORTH);
        listPanel.add(new JScrollPane(appointmentsList), BorderLayout.CENTER);
        listPanel.add(actionPanel, BorderLayout.SOUTH);
        
        appointmentsPanel.add(listPanel, BorderLayout.CENTER);
        tabbedPane.addTab("Appointments", appointmentsPanel);
    }

    /**
     * Creates a styled filter button for appointment filtering.
     * 
     * @param text The button text
     * @return Configured JButton instance
     */
    private JButton createFilterButton(String text) {
        JButton button = new JButton(text);
        button.setFont(UITheme.BODY_FONT);
        button.setBackground(UITheme.SECONDARY_COLOR);
        button.setForeground(UITheme.PRIMARY_COLOR);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.PRIMARY_COLOR, 1),
            BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
        return button;
    }

    /**
     * Creates a styled action button for appointment management.
     * 
     * @param text The button text
     * @param color The button background color
     * @return Configured JButton instance
     */
    private JButton createActionButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(UITheme.BUTTON_FONT);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        return button;
    }

    /**
     * Handles appointment approval with date/time selection.
     * 
     * @param appointmentsList The list of appointments
     * @param listModel The list model backing the appointments
     */
    private void handleAppointmentApproval(JList<Appointment> appointmentsList, DefaultListModel<Appointment> listModel) {
        Appointment selected = appointmentsList.getSelectedValue();
        if (selected != null) {
            // Prompt for new date/time
            String dateTimeStr = JOptionPane.showInputDialog(this,
                "Enter appointment date/time (YYYY-MM-DD HH:MM):",
                selected.getDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
            
            if (dateTimeStr != null && !dateTimeStr.isEmpty()) {
                try {
                    // Parse and validate new date/time
                    LocalDateTime newDateTime = LocalDateTime.parse(dateTimeStr, 
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                    
                    try {
                        // Approve appointment with new time
                        appointmentManager.approveAppointment(selected, newDateTime);
                        int index = listModel.indexOf(selected);
                        if (index >= 0) {
                            listModel.set(index, selected);
                        }
                        JOptionPane.showMessageDialog(this, "Appointment approved and reminder sent");
                    } catch (IllegalStateException ex) {
                        JOptionPane.showMessageDialog(this, ex.getMessage(), "Time Slot Unavailable", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Invalid date/time format", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select an appointment first");
        }
    }

    /**
     * Handles appointment rejection.
     * 
     * @param appointmentsList The list of appointments
     * @param listModel The list model backing the appointments
     */
    private void handleAppointmentRejection(JList<Appointment> appointmentsList, DefaultListModel<Appointment> listModel) {
        Appointment selected = appointmentsList.getSelectedValue();
        if (selected != null) {
            appointmentManager.rejectAppointment(selected);
            listModel.setElementAt(selected, appointmentsList.getSelectedIndex());
            JOptionPane.showMessageDialog(this, "Appointment rejected");
        } else {
            JOptionPane.showMessageDialog(this, "Please select an appointment first");
        }
    }

    /**
     * Handles appointment rescheduling.
     * 
     * @param appointmentsList The list of appointments
     * @param listModel The list model backing the appointments
     */
    private void handleAppointmentReschedule(JList<Appointment> appointmentsList, DefaultListModel<Appointment> listModel) {
        Appointment selected = appointmentsList.getSelectedValue();
        if (selected != null) {
            // Prompt for new date/time
            String newDate = JOptionPane.showInputDialog(this, 
                "Enter new date/time (YYYY-MM-DD HH:MM):", 
                selected.getDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
            
            if (newDate != null && !newDate.isEmpty()) {
                try {
                    // Parse and validate new date/time
                    LocalDateTime newDateTime = LocalDateTime.parse(newDate, 
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                    appointmentManager.rescheduleAppointment(selected, newDateTime);
                    listModel.setElementAt(selected, appointmentsList.getSelectedIndex());
                    JOptionPane.showMessageDialog(this, "Appointment rescheduled");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Invalid date format", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select an appointment first");
        }
    }

    /**
     * Refreshes the appointments list from the database.
     * 
     * @param listModel The list model to refresh
     */
    private void refreshAppointmentsList(DefaultListModel<Appointment> listModel) {
        listModel.clear();
        for (Appointment appt : dbManager.getAppointmentsForDoctor(doctor.getName())) {
            listModel.addElement(appt);
        }
    }

    /**
     * Creates the patient feedback tab with input form.
     */
    private void createFeedbackTab() {
        feedbackPanel = new JPanel(new BorderLayout(10, 10));
        feedbackPanel.setBackground(UITheme.SECONDARY_COLOR);
        feedbackPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Feedback form card
        JPanel formPanel = UITheme.createCardPanel();
        formPanel.setLayout(new GridLayout(4, 1, 10, 10));
        
        // Patient selection panel
        JPanel patientPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        patientPanel.setBackground(Color.WHITE);
        JLabel patientLabel = new JLabel("Select Patient:");
        patientLabel.setFont(UITheme.BODY_FONT);
        
        // Patient selection combo box
        JComboBox<Patient> patientCombo = new JComboBox<>(patients.toArray(new Patient[0]));
        patientCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, 
                                                        boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Patient) {
                    setText(((Patient)value).getName() + " (" + ((Patient)value).getId() + ")");
                }
                return this;
            }
        });
        patientCombo.setFont(UITheme.BODY_FONT);
        patientPanel.add(patientLabel);
        patientPanel.add(patientCombo);
        
        // Feedback input panel
        JPanel feedbackInputPanel = new JPanel(new BorderLayout(10, 10));
        feedbackInputPanel.setBackground(Color.WHITE);
        JLabel feedbackLabel = new JLabel("Feedback:");
        feedbackLabel.setFont(UITheme.BODY_FONT);
        
        JTextArea feedbackArea = new JTextArea(5, 20);
        feedbackArea.setFont(UITheme.BODY_FONT);
        feedbackArea.setLineWrap(true);
        feedbackArea.setWrapStyleWord(true);
        
        feedbackInputPanel.add(feedbackLabel, BorderLayout.NORTH);
        feedbackInputPanel.add(new JScrollPane(feedbackArea), BorderLayout.CENTER);
        
        // Submit button
        JButton submitBtn = UITheme.createPrimaryButton("Submit Feedback");
        submitBtn.addActionListener(e -> {
            Patient selectedPatient = (Patient)patientCombo.getSelectedItem();
            String feedbackText = feedbackArea.getText();
            
            if (selectedPatient != null && !feedbackText.isEmpty()) {
                // Save feedback to database
                doctor.addFeedback(selectedPatient, feedbackText, dbManager);
                JOptionPane.showMessageDialog(this, "Feedback submitted successfully!");
                feedbackArea.setText("");
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Please select a patient and enter feedback", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        // Assemble components
        formPanel.add(patientPanel);
        formPanel.add(feedbackInputPanel);
        formPanel.add(submitBtn);
        
        feedbackPanel.add(formPanel, BorderLayout.CENTER);
        tabbedPane.addTab("Feedback", feedbackPanel);
    }

    /**
     * Creates the emergency alerts management tab.
     */
    private void createEmergencyTab() {
        emergencyPanel = new JPanel(new BorderLayout(10, 10));
        emergencyPanel.setBackground(UITheme.SECONDARY_COLOR);
        emergencyPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Top panel with buttons
        JPanel topPanel = UITheme.createCardPanel();
        topPanel.setLayout(new GridLayout(1, 2, 10, 10));
        
        // View Alerts button
        JButton viewAlertsBtn = UITheme.createPrimaryButton("View Critical Alerts");
        viewAlertsBtn.addActionListener(e -> showCriticalAlertsDialog());
        topPanel.add(viewAlertsBtn);

        // Panic Status indicator
        JPanel panicStatusPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panicStatusPanel.setBackground(Color.WHITE);
        JLabel panicStatusLabel = new JLabel("Panic Status: Normal");
        panicStatusLabel.setForeground(UITheme.SUCCESS_COLOR);
        panicStatusLabel.setFont(UITheme.BUTTON_FONT);
        panicStatusPanel.add(panicStatusLabel);
        topPanel.add(panicStatusPanel);

        emergencyPanel.add(topPanel, BorderLayout.NORTH);

        // Main emergency alerts display
        JPanel alertsPanel = UITheme.createCardPanel();
        alertsPanel.setLayout(new BorderLayout());
        alertsPanel.setBorder(BorderFactory.createTitledBorder("Emergency Alerts"));
        
        // Alerts list with custom renderer
        DefaultListModel<EmergencyAlert.Emergency> alertsListModel = new DefaultListModel<>();
        JList<EmergencyAlert.Emergency> alertsList = new JList<>(alertsListModel);
        alertsList.setCellRenderer(new EmergencyListRenderer());
        alertsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        alertsList.setFont(UITheme.BODY_FONT);
        
        // Details panel
        JPanel detailsPanel = new JPanel(new BorderLayout());
        detailsPanel.setBackground(Color.WHITE);
        detailsPanel.setBorder(BorderFactory.createTitledBorder("Alert Details"));
        
        JTextArea detailsArea = new JTextArea();
        detailsArea.setEditable(false);
        detailsArea.setFont(UITheme.BODY_FONT);
        
        // Action buttons
        JButton acknowledgeBtn = createActionButton("Acknowledge", UITheme.SUCCESS_COLOR);
        JButton contactPatientBtn = createActionButton("Contact Patient", UITheme.ACCENT_COLOR);
        JButton refreshBtn = createActionButton("Refresh", UITheme.PRIMARY_COLOR);
        JButton clearBtn = createActionButton("Clear All", UITheme.DANGER_COLOR);
        
        // Configure button actions
        acknowledgeBtn.addActionListener(e -> {
            EmergencyAlert.Emergency selected = alertsList.getSelectedValue();
            if (selected != null) {
                selected.acknowledge();
                alertsList.repaint();
                updateEmergencyDetails(detailsArea, selected);
            }
        });
        
        contactPatientBtn.addActionListener(e -> {
            EmergencyAlert.Emergency selected = alertsList.getSelectedValue();
            if (selected != null) {
                initiatePatientContact(selected.getPatient());
            }
        });
        
        refreshBtn.addActionListener(e -> {
            updateEmergencyAlerts(alertsListModel);
            alertsList.clearSelection();
            detailsArea.setText("");
        });
        
        clearBtn.addActionListener(e -> {
            try {
                // Clear acknowledged emergencies from database
                for (EmergencyAlert.Emergency emergency : dbManager.getAllEmergencies()) {
                    if (emergency.isAcknowledged()) {
                        dbManager.deleteEmergency(emergency);
                    }
                }
                updateEmergencyAlerts(alertsListModel);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error clearing acknowledged emergencies.");
            }
        });
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(acknowledgeBtn);
        buttonPanel.add(contactPatientBtn);
        buttonPanel.add(refreshBtn);
        buttonPanel.add(clearBtn);
        
        // Selection listener for alerts
        alertsList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                EmergencyAlert.Emergency selected = alertsList.getSelectedValue();
                if (selected != null) {
                    updateEmergencyDetails(detailsArea, selected);
                }
            }
        });
        
        // Assemble details panel
        detailsPanel.add(new JScrollPane(detailsArea), BorderLayout.CENTER);
        detailsPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Create split pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
            new JScrollPane(alertsList),
            detailsPanel);
        splitPane.setDividerLocation(250);
        splitPane.setResizeWeight(0.5);
        splitPane.setBackground(UITheme.SECONDARY_COLOR);
        
        alertsPanel.add(splitPane, BorderLayout.CENTER);
        emergencyPanel.add(alertsPanel, BorderLayout.CENTER);
        
        // Initial load of emergency alerts
        updateEmergencyAlerts(alertsListModel);
        
        tabbedPane.addTab("Emergency", emergencyPanel);
    }
    
    /**
     * Creates the medication prescription tab with form and history view.
     */
    private void createPrescribeMedicationTab() {
        JPanel prescribePanel = new JPanel(new BorderLayout(10, 10));
        prescribePanel.setBackground(UITheme.SECONDARY_COLOR);
        prescribePanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Create a form panel for prescription input
        JPanel formPanel = UITheme.createCardPanel();
        formPanel.setLayout(new GridLayout(6, 2, 10, 10));

        // Patient selection
        JLabel patientLabel = new JLabel("Patient:");
        patientLabel.setFont(UITheme.BODY_FONT);
        JComboBox<Patient> patientCombo = new JComboBox<>(patients.toArray(new Patient[0]));
        patientCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, 
                                                        boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Patient) {
                    setText(((Patient)value).getName() + " (" + ((Patient)value).getId() + ")");
                }
                return this;
            }
        });
        patientCombo.setFont(UITheme.BODY_FONT);

        // Medication input
        JLabel medLabel = new JLabel("Medication:");
        medLabel.setFont(UITheme.BODY_FONT);
        JTextField medField = new JTextField();

        // Dosage input
        JLabel dosageLabel = new JLabel("Dosage:");
        dosageLabel.setFont(UITheme.BODY_FONT);
        JTextField dosageField = new JTextField();

        // Schedule input
        JLabel scheduleLabel = new JLabel("Schedule:");
        scheduleLabel.setFont(UITheme.BODY_FONT);
        JTextField scheduleField = new JTextField();

        // Tests input
        JLabel testsLabel = new JLabel("Recommended Tests:");
        testsLabel.setFont(UITheme.BODY_FONT);
        JTextField testsField = new JTextField();

        // Add components to form
        formPanel.add(patientLabel);
        formPanel.add(patientCombo);
        formPanel.add(medLabel);
        formPanel.add(medField);
        formPanel.add(dosageLabel);
        formPanel.add(dosageField);
        formPanel.add(scheduleLabel);
        formPanel.add(scheduleField);
        formPanel.add(testsLabel);
        formPanel.add(testsField);

        // Submit button
        JButton submitBtn = UITheme.createPrimaryButton("Prescribe Medication");
        submitBtn.addActionListener(e -> {
            Patient selectedPatient = (Patient)patientCombo.getSelectedItem();
            String medication = medField.getText();
            String dosage = dosageField.getText();
            String schedule = scheduleField.getText();
            String tests = testsField.getText();

            if (selectedPatient != null && !medication.isEmpty() && !dosage.isEmpty() && !schedule.isEmpty()) {
                // Save prescription and notify patient
                doctor.prescribeMedication(selectedPatient, medication, dosage, schedule, tests, emailService, dbManager);
                JOptionPane.showMessageDialog(this, "Prescription created and sent to patient!");
                
                // Clear fields
                medField.setText("");
                dosageField.setText("");
                scheduleField.setText("");
                testsField.setText("");
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Please fill all required fields (Patient, Medication, Dosage, Schedule)",
                    "Incomplete Form", JOptionPane.WARNING_MESSAGE);
            }
        });

        // View existing prescriptions panel
        JPanel viewPanel = UITheme.createCardPanel();
        viewPanel.setLayout(new BorderLayout());
        viewPanel.setBorder(BorderFactory.createTitledBorder("Patient's Existing Prescriptions"));

        JTextArea prescriptionsArea = new JTextArea();
        prescriptionsArea.setEditable(false);
        prescriptionsArea.setFont(UITheme.BODY_FONT);
        prescriptionsArea.setLineWrap(true);
        prescriptionsArea.setWrapStyleWord(true);

        // Add listener to update prescriptions display when patient changes
        patientCombo.addActionListener(e -> {
            Patient p = (Patient)patientCombo.getSelectedItem();
            if (p != null) {
                StringBuilder sb = new StringBuilder();
                for (Prescription prescription : dbManager.getPrescriptionsForPatient(p.getId())) {
                    sb.append(prescription.toString()).append("\n\n");
                }
                prescriptionsArea.setText(sb.toString());
            }
        });

        viewPanel.add(new JScrollPane(prescriptionsArea), BorderLayout.CENTER);

        // Assemble the tab
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(formPanel, BorderLayout.CENTER);
        topPanel.add(submitBtn, BorderLayout.SOUTH);

        prescribePanel.add(topPanel, BorderLayout.NORTH);
        prescribePanel.add(viewPanel, BorderLayout.CENTER);

        tabbedPane.addTab("Prescribe", prescribePanel);
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
     * Displays a dialog with critical alerts summary.
     */
    private void showCriticalAlertsDialog() {
        String alerts = emergencyAlert.displayAlerts();
        
        JTextArea textArea = new JTextArea(alerts);
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(600, 400));
        
        JOptionPane.showMessageDialog(
            this, 
            scrollPane, 
            "Critical Alerts Summary", 
            JOptionPane.WARNING_MESSAGE
        );
    }

    /**
     * Updates the list of emergency alerts in the provided model.
     * Clears the model and populates it with all the emergencies retrieved from the database.
     * 
     * @param model The model to update with the list of emergencies
     */
    private void updateEmergencyAlerts(DefaultListModel<EmergencyAlert.Emergency> model) {
        model.clear();
        for (EmergencyAlert.Emergency e : dbManager.getAllEmergencies()) {
            model.addElement(e);
        }
    }

    /**
     * Updates the details area with information about a specific emergency.
     * This includes the type of alert, patient information, timestamp, status, and any related vitals.
     * 
     * @param detailsArea The JTextArea where the emergency details will be displayed
     * @param emergency The emergency object containing the information to display
     */
    private void updateEmergencyDetails(JTextArea detailsArea, EmergencyAlert.Emergency emergency) {
        StringBuilder sb = new StringBuilder();
        sb.append("Alert Type: ").append(emergency.getType()).append("\n");
        sb.append("Patient: ").append(emergency.getPatient().getName()).append("\n");
        sb.append("ID: ").append(emergency.getPatient().getId()).append("\n");
        sb.append("Timestamp: ").append(emergency.getTimestamp().format(
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append("\n");
        sb.append("Status: ").append(emergency.isAcknowledged() ? "ACKNOWLEDGED" : "UNACKNOWLEDGED").append("\n\n");
        sb.append("Details:\n").append(emergency.getMessage()).append("\n\n");
    
        // Add patient vitals if available
        List<VitalSign> vitals = dbManager.getVitalsForPatient(emergency.getPatient().getId());
        if (!vitals.isEmpty()) {
            VitalSign latest = vitals.get(vitals.size() - 1);
            sb.append("Latest Vitals:\n").append(latest.toString()).append("\n");
        }

        detailsArea.setText(sb.toString());
    }

    /**
     * Initiates contact with a patient during an emergency, providing options to video call,
     * send an email, or send emergency instructions. The user's choice determines the action.
     * 
     * @param patient The patient to contact regarding the emergency
     */
    private void initiatePatientContact(Patient patient) {
        String[] options = {"Video Call", "Send Email", "Send Emergency Instructions"};
        int choice = JOptionPane.showOptionDialog(
            this,
            "Contact " + patient.getName() + " regarding emergency:",
            "Emergency Contact",
             JOptionPane.DEFAULT_OPTION,
             JOptionPane.QUESTION_MESSAGE,
             null,
             options,
             options[0]);

        switch (choice) {
            case 0: // Video Call
                String password = JOptionPane.showInputDialog(
                   this, 
                   "Set video call password (leave empty for none):",
                   "Secure Video Call",
                   JOptionPane.QUESTION_MESSAGE);
                videoCall.startCall("Dr. " + doctor.getName(), patient.getName(), password);
                break;
            
            case 1: // Send Email
               String message = JOptionPane.showInputDialog(
                   this,
                   "Enter emergency message to send:",
                   "URGENT: Emergency Consultation Required",
                    JOptionPane.PLAIN_MESSAGE);
             
                if (message != null && !message.isEmpty()) {
                    emailService.sendNotification(
                    patient.getEmail(),
                    "URGENT: Message From Dr. " + doctor.getName() +
                    "\n\n-Doctor Message:\n" + message + 
                    "\n\nPlease respond immediately!\n\n" +
                    "Patient: " + patient.getName() +
                    "\nDoctor: " + doctor.getName()
                    );
                    JOptionPane.showMessageDialog(this, "Emergency email sent!");
                }
                break;
            
                case 2: // Send Emergency Instructions
                   String instructions = "EMERGENCY PROTOCOL:\n\n" +
                   "1. Stay calm and in your current location\n" +
                   "2. Prepare your medical information\n" +
                   "3. Have your medications ready\n" +
                   "4. Await further instructions";
            
                   emailService.sendNotification(
                      patient.getEmail(),
                      "URGENT: Emergency Instructions From Your Doctor" +
                      instructions
                    );
                JOptionPane.showMessageDialog(this, "Emergency instructions sent!");
                break;
        } 
    }

    /**
     * Renders appointment details in a list, displaying appointment time, patient name, status, and doctor name.
     * The background color is adjusted based on the appointment's status.
     */
    private static class AppointmentListRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, 
                                                boolean isSelected, boolean cellHasFocus) {
           super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
           setFont(UITheme.BODY_FONT);
    
            if (value instanceof Appointment) {
                Appointment appt = (Appointment)value;
                setText(String.format("%s | %s | %s | %s", 
                    appt.getDateTime().format(DateTimeFormatter.ofPattern("MMM dd, HH:mm")),
                    appt.getPatient().getName(),
                    appt.getStatus(),
                    appt.getDoctor().getName()));
        
                // Color coding
               switch (appt.getStatus()) {
                    case "PENDING":
                        setBackground(isSelected ? new Color(200, 200, 255) : new Color(240, 240, 255));
                        break;
                    case "Approved":
                        setBackground(isSelected ? new Color(200, 255, 200) : new Color(240, 255, 240));
                        break;
                    case "Rejected":
                        setBackground(isSelected ? new Color(255, 200, 200) : new Color(255, 240, 240));
                        break;
                }
            }
            return this;
        }
    }

    /**
     * Renders emergency alert details in a list, displaying the type of emergency, patient name, and status.
     * Emergency alerts are color-coded based on whether they have been acknowledged or not.
     */
    private static class EmergencyListRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, 
                                                boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            setFont(UITheme.BODY_FONT);
        
            if (value instanceof EmergencyAlert.Emergency) {
                EmergencyAlert.Emergency e = (EmergencyAlert.Emergency)value;
                setText(String.format("%s - %s (%s)", 
                    e.getType(), 
                    e.getPatient().getName(),
                    e.isAcknowledged() ? "ACK" : "NEW"));
             
                if (!e.isAcknowledged()) {
                   setBackground(isSelected ? new Color(255, 220, 220) : new Color(255, 240, 240));
                   setForeground(UITheme.DANGER_COLOR);
                   setFont(getFont().deriveFont(Font.BOLD));
                } else {
                   setBackground(isSelected ? new Color(220, 255, 220) : new Color(240, 255, 240));
                   setForeground(UITheme.PRIMARY_COLOR);
                }
            
                if ("PANIC".equals(e.getType())) {
                   setFont(getFont().deriveFont(Font.BOLD, 14f));
                }
            }
        
            return this;
        }
    }

    /**
     * Returns a detailed string representation of an emergency, including the alert type,
     * patient details, timestamp, status, and associated vitals if available.
     * 
     * @param emergency The emergency object containing the details to format
     * @return A string representation of the emergency details
     */
    private String getEmergencyDetails(EmergencyAlert.Emergency emergency) {
        StringBuilder sb = new StringBuilder();
        sb.append("Type: ").append(emergency.getType()).append("\n");
        sb.append("Patient: ").append(emergency.getPatient().getName()).append("\n");
        sb.append("Time: ").append(emergency.getTimestamp()).append("\n");
        sb.append("Status: ").append(emergency.isAcknowledged() ? "ACKNOWLEDGED" : "PENDING").append("\n\n");
        sb.append("Details:\n").append(emergency.getMessage()).append("\n\n");
    
        // Add patient vitals if available
        List<VitalSign> vitals = dbManager.getVitalsForPatient(emergency.getPatient().getId());
        if (!vitals.isEmpty()) {
            sb.append("Latest Vitals:\n");
            sb.append(vitals.get(vitals.size() - 1).toString()).append("\n");
        }

        return sb.toString();
    }

    /**
     * Returns a formatted string containing the doctor's recent activity, including recent appointments.
     * 
     * @return A string representation of the doctor's recent activity
     */
    private String getRecentActivity() {
        StringBuilder sb = new StringBuilder();
    
        // Recent appointments
        if (!dbManager.getAppointmentsForDoctor(doctor.getName()).isEmpty()) {
            sb.append("Recent Appointments:\n");
            int count = Math.min(3, dbManager.getAppointmentsForDoctor(doctor.getName()).size());
            for (int i = dbManager.getAppointmentsForDoctor(doctor.getName()).size() - 1; i >= dbManager.getAppointmentsForDoctor(doctor.getName()).size() - count; i--) {
                sb.append("- ").append(dbManager.getAppointmentsForDoctor(doctor.getName()).get(i)).append("\n");
            }
       }
    
       return sb.toString();
    }

    /**
     * Returns a detailed string representation of a patient's information, including medical history,
     * recent vitals, and appointments.
     * 
     * @param patient The patient object containing the details to format
     * @return A string representation of the patient's details
     */
    private String getPatientDetails(Patient patient) {
        StringBuilder sb = new StringBuilder();
        sb.append("Patient ID: ").append(patient.getId()).append("\n");
        sb.append("Name: ").append(patient.getName()).append("\n");
        sb.append("Email: ").append(patient.getEmail()).append("\n\n");
    
        sb.append("Medical History:\n");
        sb.append(patient.getMedicalHistory().getFormattedHistory(dbManager, patient.getId())).append("\n");
    
        sb.append("\nRecent Vital Signs:\n");
        List<VitalSign> vitals = dbManager.getVitalsForPatient(patient.getId());

        int count = Math.min(3, vitals.size());
        if (count > 0) {
            for (int i = vitals.size() - 1; i >= vitals.size() - count; i--) {
                sb.append("- ").append(vitals.get(i)).append("\n");
            }
        }
    
        return sb.toString();
    }

    /**
     * Returns a string representation of a list of appointments.
     * 
     * @param appointments The list of appointments to format
     * @return A string representation of the appointments
     */
    private String getAppointmentsAsString(ArrayList<Appointment> appointments) {
        if (appointments == null || appointments.isEmpty()) {
           return "No appointments found";
        }
    
       StringBuilder sb = new StringBuilder();
       for (Appointment appt : appointments) {
            sb.append("Date: ").append(appt.getDateTime())
            .append("\nPatient: ").append(appt.getPatient().getName())
            .append("\nStatus: ").append(appt.getStatus())
            .append("\n\n");
        }
    return sb.toString();
    }

    /**
     * Updates the displayed appointments for a doctor in the given text area.
     * 
     * @param area The JTextArea to display the appointments
     */
    private void updateAppointmentsDisplay(JTextArea area) {
        StringBuilder sb = new StringBuilder();
        for (Appointment appt : dbManager.getAppointmentsForDoctor(doctor.getName())) {
            sb.append(appt).append("\n\n");
        }
        area.setText(sb.toString());
    }

    /**
     * Displays the doctor's dashboard in a new window, ensuring the UI is updated on the Event Dispatch Thread.
     * 
     * @param doctor The doctor whose dashboard is being displayed
     * @param dbManager The database manager used to fetch data
     * @param appointmentManager The appointment manager used for scheduling
     * @param emergencyAlert The emergency alert system to manage emergencies
     * @param emailService The email service to send notifications
     * @param videoCall The video call service for patient communication
     */
    public static void showDashboard(Doctor doctor, DatabaseManager dbManager, AppointmentManager appointmentManager, EmergencyAlert emergencyAlert, EmailNotification emailService, VideoCall videoCall) {
        // Ensure this runs on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            DoctorDashboard dashboard = new DoctorDashboard(doctor, dbManager, appointmentManager, emergencyAlert, emailService, videoCall);
        

            // Configure window
            dashboard.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            dashboard.setLocationRelativeTo(null); // Center on screen
            dashboard.setVisible(true);
        });
    }
}
