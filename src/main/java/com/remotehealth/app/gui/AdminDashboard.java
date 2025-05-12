package com.remotehealth.app.gui;

import com.remotehealth.app.model.*;
import com.remotehealth.app.utils.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

/**
 * GUI class for the Administrator's dashboard.
 * Allows the admin to manage doctors, patients, and view system logs.
 */
public class AdminDashboard extends JFrame {
    private Administrator admin;
    private ArrayList<Doctor> doctors;
    private ArrayList<Patient> patients;
    private DatabaseManager dbManager;

    // UI Components
    private JTabbedPane tabbedPane;
    private JPanel dashboardPanel, manageDoctorsPanel, managePatientsPanel, systemLogsPanel;

    /**
     * Constructs the admin dashboard with data and initializes the GUI.
     */
    public AdminDashboard(Administrator admin, DatabaseManager dbManager) {
        this.admin = admin;
        this.dbManager = dbManager;
        this.doctors = dbManager.getAllDoctors();
        this.patients = dbManager.getAllPatients();

        setTitle("Admin Dashboard");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);

        // Prompt confirmation on window close
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                confirmExit();
            }
        });

        initComponents();
        initMenuBar();
    }

    /**
     * Initializes the menu bar with file menu options.
     */
    private void initMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(UITheme.PRIMARY_COLOR);

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
     * Applies styling to a menu item.
     */
    private void styleMenuItem(JMenuItem item) {
        item.setBackground(Color.WHITE);
        item.setForeground(UITheme.PRIMARY_COLOR);
        item.setFont(UITheme.BODY_FONT);
        item.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
    }

    /**
     * Initializes and sets up GUI components and layout.
     */
    private void initComponents() {
        getContentPane().setBackground(UITheme.SECONDARY_COLOR);
        setLayout(new BorderLayout());

        tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane.setBackground(UITheme.SECONDARY_COLOR);
        tabbedPane.setForeground(UITheme.PRIMARY_COLOR);
        tabbedPane.setFont(UITheme.SUBHEADER_FONT);

        createDashboardTab();
        createManageDoctorsTab();
        createManagePatientsTab();
        createSystemLogsTab();

        add(tabbedPane, BorderLayout.CENTER);

        // Footer with logout button
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
     * Creates the dashboard tab with statistics and welcome message.
     */
    private void createDashboardTab() {
        dashboardPanel = new JPanel(new BorderLayout(10, 10));
        dashboardPanel.setBackground(UITheme.SECONDARY_COLOR);
        dashboardPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel welcomePanel = UITheme.createCardPanel();
        welcomePanel.setLayout(new BorderLayout());

        JLabel welcomeLabel = new JLabel("<html><h1 style='color:" + colorToHex(UITheme.PRIMARY_COLOR) + "'>Welcome, Administrator</h1></html>");
        welcomeLabel.setFont(UITheme.HEADER_FONT);
        welcomePanel.add(welcomeLabel, BorderLayout.WEST);

        JPanel statsPanel = new JPanel(new GridLayout(2, 2, 15, 15));
        statsPanel.setBackground(UITheme.SECONDARY_COLOR);

        statsPanel.add(createStatPanel("Total Doctors", String.valueOf(doctors.size())));
        statsPanel.add(createStatPanel("Total Patients", String.valueOf(patients.size())));
        statsPanel.add(createStatPanel("System Status", "Normal"));
        statsPanel.add(createStatPanel("Last Login", "Today"));

        dashboardPanel.add(welcomePanel, BorderLayout.NORTH);
        dashboardPanel.add(statsPanel, BorderLayout.CENTER);

        tabbedPane.addTab("Dashboard", dashboardPanel);
    }

    /**
     * Creates an individual stat panel for dashboard.
     */
    private JPanel createStatPanel(String title, String value) {
        JPanel panel = UITheme.createCardPanel();
        panel.setLayout(new BorderLayout(10, 10));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(UITheme.BODY_FONT);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel valueLabel = new JLabel(value, SwingConstants.CENTER);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        valueLabel.setForeground(UITheme.PRIMARY_COLOR);

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.add(titleLabel, BorderLayout.NORTH);
        contentPanel.add(valueLabel, BorderLayout.CENTER);

        panel.add(contentPanel, BorderLayout.CENTER);
        return panel;
    }

    /**
     * Creates the Manage Doctors tab allowing addition and viewing of doctors.
     */
    private void createManageDoctorsTab() {
        manageDoctorsPanel = new JPanel(new BorderLayout(10, 10));
        manageDoctorsPanel.setBackground(UITheme.SECONDARY_COLOR);
        manageDoctorsPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Doctor list
        DefaultListModel<Doctor> listModel = new DefaultListModel<>();
        for (Doctor doctor : dbManager.getAllDoctors()) {
            listModel.addElement(doctor);
        }

        JList<Doctor> doctorList = new JList<>(listModel);
        doctorList.setCellRenderer(new DoctorListRenderer());
        doctorList.setFont(UITheme.BODY_FONT);
        doctorList.setBackground(Color.WHITE);

        // Add doctor form
        JPanel addDoctorPanel = UITheme.createCardPanel();
        addDoctorPanel.setLayout(new GridLayout(5, 2, 10, 10));

        JTextField idField = new JTextField();
        JTextField nameField = new JTextField();
        JTextField emailField = new JTextField();
        JPasswordField passwordField = new JPasswordField();

        addDoctorPanel.add(new JLabel("Doctor ID:"));
        addDoctorPanel.add(idField);
        addDoctorPanel.add(new JLabel("Name:"));
        addDoctorPanel.add(nameField);
        addDoctorPanel.add(new JLabel("Email:"));
        addDoctorPanel.add(emailField);
        addDoctorPanel.add(new JLabel("Password:"));
        addDoctorPanel.add(passwordField);

        JButton addButton = UITheme.createPrimaryButton("Add Doctor");
        addButton.addActionListener(e -> {
            try {
                admin.addDoctor(idField.getText(), nameField.getText(), emailField.getText(), new String(passwordField.getPassword()));
                listModel.addElement(new Doctor(idField.getText(), nameField.getText(), emailField.getText(), new String(passwordField.getPassword())));
                clearFields(idField, nameField, emailField, passwordField);
                JOptionPane.showMessageDialog(this, "Doctor added successfully!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error adding doctor: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        addDoctorPanel.add(new JLabel());
        addDoctorPanel.add(addButton);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JScrollPane(doctorList), addDoctorPanel);
        splitPane.setDividerLocation(300);

        manageDoctorsPanel.add(splitPane, BorderLayout.CENTER);
        tabbedPane.addTab("Manage Doctors", manageDoctorsPanel);
    }

    /**
     * Creates the Manage Patients tab allowing addition and viewing of patients.
     */
    private void createManagePatientsTab() {
        managePatientsPanel = new JPanel(new BorderLayout(10, 10));
        managePatientsPanel.setBackground(UITheme.SECONDARY_COLOR);
        managePatientsPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        DefaultListModel<Patient> listModel = new DefaultListModel<>();
        for (Patient patient : dbManager.getAllPatients()) {
            listModel.addElement(patient);
        }

        JList<Patient> patientList = new JList<>(listModel);
        patientList.setCellRenderer(new PatientListRenderer());
        patientList.setFont(UITheme.BODY_FONT);
        patientList.setBackground(Color.WHITE);

        JPanel addPatientPanel = UITheme.createCardPanel();
        addPatientPanel.setLayout(new GridLayout(5, 2, 10, 10));

        JTextField idField = new JTextField();
        JTextField nameField = new JTextField();
        JTextField emailField = new JTextField();
        JPasswordField passwordField = new JPasswordField();

        addPatientPanel.add(new JLabel("Patient ID:"));
        addPatientPanel.add(idField);
        addPatientPanel.add(new JLabel("Name:"));
        addPatientPanel.add(nameField);
        addPatientPanel.add(new JLabel("Email:"));
        addPatientPanel.add(emailField);
        addPatientPanel.add(new JLabel("Password:"));
        addPatientPanel.add(passwordField);

        JButton addButton = UITheme.createPrimaryButton("Add Patient");
        addButton.addActionListener(e -> {
            try {
                admin.addPatient(idField.getText(), nameField.getText(), emailField.getText(), new String(passwordField.getPassword()));
                listModel.addElement(new Patient(idField.getText(), nameField.getText(), emailField.getText(), new String(passwordField.getPassword())));
                clearFields(idField, nameField, emailField, passwordField);
                JOptionPane.showMessageDialog(this, "Patient added successfully!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error adding patient: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        addPatientPanel.add(new JLabel());
        addPatientPanel.add(addButton);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JScrollPane(patientList), addPatientPanel);
        splitPane.setDividerLocation(300);

        managePatientsPanel.add(splitPane, BorderLayout.CENTER);
        tabbedPane.addTab("Manage Patients", managePatientsPanel);
    }

    /**
     * Creates the System Logs tab where the admin can view and refresh logs.
     */
    private void createSystemLogsTab() {
        systemLogsPanel = new JPanel(new BorderLayout(10, 10));
        systemLogsPanel.setBackground(UITheme.SECONDARY_COLOR);
        systemLogsPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel logsPanel = UITheme.createCardPanel();
        logsPanel.setLayout(new BorderLayout());
        logsPanel.setBorder(BorderFactory.createTitledBorder("System Logs"));

        JTextArea logsArea = new JTextArea();
        logsArea.setEditable(false);
        logsArea.setFont(UITheme.BODY_FONT);

        StringBuilder logs = new StringBuilder();
        for (String log : admin.getSystemLogs()) {
            logs.append(log).append("\n");
        }
        logsArea.setText(logs.toString());

        JButton refreshBtn = UITheme.createStyledButton("Refresh Logs", UITheme.ACCENT_COLOR);
        refreshBtn.addActionListener(e -> {
            StringBuilder updatedLogs = new StringBuilder();
            for (String log : admin.getSystemLogs()) {
                updatedLogs.append(log).append("\n");
            }
            logsArea.setText(updatedLogs.toString());
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(refreshBtn);

        logsPanel.add(new JScrollPane(logsArea), BorderLayout.CENTER);
        logsPanel.add(buttonPanel, BorderLayout.SOUTH);

        systemLogsPanel.add(logsPanel, BorderLayout.CENTER);
        tabbedPane.addTab("System Logs", systemLogsPanel);
    }

    /**
     * Prompts logout confirmation and redirects to login screen.
     */
    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?", "Confirm Logout", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            WindowManager.logout(this);
        }
    }

    /**
     * Confirms exit before closing the application.
     */
    private void confirmExit() {
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to exit the application?", "Confirm Exit", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    /**
     * Clears input fields after form submission.
     */
    private void clearFields(JTextField... fields) {
        for (JTextField field : fields) {
            field.setText("");
        }
    }

    /**
     * Converts a color to hexadecimal string for HTML labels.
     */
    private String colorToHex(Color color) {
        return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
    }

    /**
     * Custom renderer to display doctor list nicely.
     */
    private static class DoctorListRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            setFont(UITheme.BODY_FONT);
            if (value instanceof Doctor) {
                Doctor doctor = (Doctor) value;
                setText(doctor.getId() + " - " + doctor.getName());
            }
            return this;
        }
    }

    /**
     * Custom renderer to display patient list nicely.
     */
    private static class PatientListRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            setFont(UITheme.BODY_FONT);
            if (value instanceof Patient) {
                Patient patient = (Patient) value;
                setText(patient.getId() + " - " + patient.getName());
            }
            return this;
        }
    }

    /**
     * Launches the AdminDashboard GUI.
     */
    public static void showDashboard(Administrator admin, DatabaseManager dbManager) {
        SwingUtilities.invokeLater(() -> {
            AdminDashboard dashboard = new AdminDashboard(admin, dbManager);
            dashboard.setVisible(true);
        });
    }
}
