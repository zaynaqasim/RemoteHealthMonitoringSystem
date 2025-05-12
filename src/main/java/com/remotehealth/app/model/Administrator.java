/**
 * The Administrator class represents a system administrator with privileges to manage
 * users, view system logs, and perform administrative functions in the Remote Health
 * Monitoring System.
 * 
 * @author 
 * @version 1.0
 */
package com.remotehealth.app.model;

import java.util.ArrayList;
import java.util.ArrayList;
import java.util.Scanner;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Connection;

public class Administrator extends User {
    // Authentication credentials
    private final String password;
    
    // Database access
    private final DatabaseManager dbManager;

    /**
     * Constructs a new Administrator with full privileges.
     * 
     * @param id Administrator ID
     * @param name Full name
     * @param email Email address
     * @param password Authentication password
     * @param dbManager Database access manager
     */
    public Administrator(String id, String name, String email, String password, DatabaseManager dbManager) {
        super(id, name, email);
        this.password = password;
        this.dbManager = dbManager;
    }

    // ========== AUTHENTICATION METHODS ==========

    /**
     * Authenticates administrator credentials.
     * 
     * @param username Input username
     * @param password Input password
     * @return true if credentials match
     */
    public boolean authenticate(String username, String password) {
        System.out.println("Stored name: " + this.getName());
        System.out.println("Input name: " + username);
        System.out.println("Stored password: " + this.password);
        System.out.println("Input password: " + password);

        return this.getName().trim().equalsIgnoreCase(username.trim()) &&
               this.password.trim().equals(password.trim());
    }

    /**
     * Retrieves administrator by credentials from database.
     * 
     * @param username Administrator username
     * @param password Administrator password
     * @param dbManager Database access manager
     * @return Administrator object if authenticated, null otherwise
     * @throws RuntimeException if database operation fails
     */
    public static Administrator getAdminByCredentials(String username, String password, DatabaseManager dbManager) {
        String sql = "SELECT id, name, email, password FROM admin WHERE name = ?";
        try (PreparedStatement ps = dbManager.getConnection().prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String storedId = rs.getString("id");
                String storedName = rs.getString("name");
                String storedEmail = rs.getString("email");
                String storedPassword = rs.getString("password");
               if (storedPassword != null && storedPassword.trim().equals(password.trim())) {
                return new Administrator(storedId, storedName, storedEmail, storedPassword, dbManager);
                }
            }
            return null;
        } catch (SQLException e) {
            dbManager.saveLog("Error retrieving admin " + username + ": " + e.getMessage());
            throw new RuntimeException("Failed to retrieve admin", e);
        }
    }

    // ========== USER MANAGEMENT METHODS ==========

    /**
     * Adds a new doctor to the system.
     * 
     * @param id Doctor ID
     * @param name Full name
     * @param email Email address
     * @param password Authentication password
     */
    public void addDoctor(String id, String name, String email, String password) {
        Doctor doctor = new Doctor(id, name, email, password);
        dbManager.saveDoctor(doctor);
        System.out.println("[INFO] Doctor added to database.");
        logAction("Doctor " + name + " added to system.");
    }

    /**
     * Adds a new patient to the system.
     * 
     * @param id Patient ID
     * @param name Full name
     * @param email Email address
     * @param password Authentication password
     */
    public void addPatient(String id, String name, String email, String password) {
        Patient patient = new Patient(id, name, email, password);
        dbManager.savePatient(patient);
        System.out.println("[INFO] Patient added to database.");
        logAction("Patient " + name + " added to system.");
    }

    // ========== LOGGING METHODS ==========

    /**
     * Records an administrative action in the system logs.
     * 
     * @param message Log message
     */
    public void logAction(String message) {
        dbManager.saveLog("[LOG] " + message);
    }

    /**
     * Retrieves system logs from database.
     * 
     * @return ArrayList of log entries
     */
    public ArrayList<String> getSystemLogs() {
        return dbManager.fetchLogs();
    }

    // ========== GETTER METHODS ==========
    
    /**
     * @return Administrator's password (should be handled securely in production)
     */
    public String getPassword() {
        return password;
    }
}