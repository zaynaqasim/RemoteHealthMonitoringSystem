/**
 * The Feedback class represents medical feedback provided by a doctor to a patient.
 * It includes the feedback content, doctor's name, and timestamp of when it was given.
 * 
 * @author 
 * @version 1.0
 */
package com.remotehealth.app.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Feedback {
    // Feedback details
    private String comments;      // Feedback content
    private String doctorName;    // Name of providing doctor
    private LocalDateTime date;   // Timestamp of feedback

    /**
     * Constructs a new Feedback instance.
     * 
     * @param doctorName Name of providing doctor
     * @param comments Feedback content
     */
    public Feedback(String doctorName, String comments) {
        this.doctorName = doctorName;
        this.comments = comments;
        this.date = LocalDateTime.now();
    }

    // ========== GETTER METHODS ==========
    
    /**
     * @return Feedback content
     */
    public String getComments() { return comments; }
    
    /**
     * @return Doctor's name
     */
    public String getDoctorName() { return doctorName; }
    
    /**
     * @return Feedback timestamp
     */
    public LocalDateTime getDate() { return date; }

    /**
     * Returns a formatted string representation of the feedback.
     * 
     * @return Formatted feedback string
     */
    @Override
    public String toString() {
        return String.format("[%s] Dr. %s:\n%s",
            date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
            doctorName,
            comments);
    }
}