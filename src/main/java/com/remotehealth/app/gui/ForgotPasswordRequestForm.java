package com.remotehealth.app.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import com.remotehealth.app.model.DatabaseManager;

/**
 * GUI class for handling password reset requests from users.
 * Allows users to enter their username and submit a request to reset their password.
 */
public class ForgotPasswordRequestForm extends JFrame {
    private JTextField usernameField;       // Field to input username
    private JTextArea messageArea;          // Field to input message (optional description or reason)
    private JButton submitBtn;              // Button to submit the request
    private DatabaseManager dbManager;      // Database manager for interacting with the database

    /**
     * Constructor that initializes the password reset form and sets up the GUI components.
     * 
     * @param dbManager Reference to the shared DatabaseManager instance for database operations.
     */
    public ForgotPasswordRequestForm(DatabaseManager dbManager) {
        this.dbManager = dbManager;  // Save the reference to the DatabaseManager

        // Set basic frame properties
        setTitle("Forgot Password Request");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);  // Only close this window, not the entire application
        setLocationRelativeTo(null);  // Center the window on the screen

        // Set layout for arranging components vertically
        setLayout(new GridLayout(4, 1));

        // Create input fields and button
        usernameField = new JTextField("Enter Username");
        messageArea = new JTextArea("Enter your message");
        submitBtn = new JButton("Submit Request");

        // Attach an action listener to handle the submit button click
        submitBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();     // Get input from username field
                String message = messageArea.getText();        // Get input from message field
                submitRequest(username, message);              // Submit the request to the system
            }
        });

        // Add all components to the frame
        add(usernameField);
        add(messageArea);
        add(submitBtn);

        // Make the form visible
        setVisible(true);
    }

    /**
     * Submits the password reset request to the database.
     *
     * @param username The username for which password reset is being requested.
     * @param message  Optional message or context for the request.
     */
    private void submitRequest(String username, String message) {
        // Set the default role (can be customized or detected in future)
        String role = "patient";

        // Call the database manager to save the password reset request
        dbManager.submitPasswordResetRequest(username, role);
        
        // Notify user of success and close the form
        JOptionPane.showMessageDialog(this, "Request submitted successfully!");
        dispose();  // Close the form window after submission
    }
}
