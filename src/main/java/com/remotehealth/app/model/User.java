package com.remotehealth.app.model;

/**
 * BASE USER CLASS (Abstract)
 * 
 * Represents the common attributes for all users in the system.
 * Serves as the parent class for Patient and Doctor.
 */
abstract public class User {
    public String id;     // Unique identifier for the user
    private String name;   // Full name of the user
    private String email;  // Contact email address
    
    // Constructor to initialize User attributes
    public User(String id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }
    
    // Default constructor
    public User() {
        this("", "", ""); // Initialize with empty values
    }

    // Getter methods
    public String getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
}
