/**
 * The VitalSign class represents a patient's vital sign measurements at a specific point in time.
 * It stores key health metrics including heart rate, oxygen level, blood pressure, and temperature.
 * 
 * @author 
 * @version 1.0
 */
package com.remotehealth.app.model;

import java.util.Date;

public class VitalSign {
    // Vital sign metrics
    private int heartRate;        // Heart rate in beats per minute (bpm)
    private int oxygenLevel;      // Blood oxygen saturation percentage
    private String bloodPressure; // Blood pressure in "systolic/diastolic" format
    private double temperature;   // Body temperature in Celsius
    private Date added_on;        // Timestamp when measurement was taken

    /**
     * Constructs a new VitalSign with all measurement values.
     * 
     * @param heartRate Heart rate in bpm
     * @param oxygenLevel Oxygen saturation percentage
     * @param bloodPressure Blood pressure string (e.g., "120/80")
     * @param temperature Body temperature in Celsius
     * @param added_on Measurement timestamp
     */
    public VitalSign(int heartRate, int oxygenLevel, String bloodPressure, double temperature, Date added_on) {
        this.heartRate = heartRate;
        this.oxygenLevel = oxygenLevel;
        this.bloodPressure = bloodPressure;
        this.temperature = temperature;
        this.added_on = added_on;
    }

    // ========== GETTER METHODS ==========

    /**
     * @return Heart rate in beats per minute
     */
    public int getHeartRate() {
        return heartRate;
    }

    /**
     * @return Oxygen saturation percentage
     */
    public int getOxygenLevel() {
        return oxygenLevel;
    }

    /**
     * @return Blood pressure string (e.g., "120/80")
     */
    public String getBloodPressure() {
        return bloodPressure;
    }

    /**
     * @return Body temperature in Celsius
     */
    public double getTemperature() {
        return temperature;
    }
    
    /**
     * @return Timestamp when measurement was taken
     */
    public Date getAddedOn() {
        return added_on;
    }

    /**
     * Returns a formatted string representation of all vital signs.
     * 
     * @return Formatted string with all vital measurements
     */
    @Override
    public String toString() {
        return "Heart Rate: " + heartRate + " bpm, " +
               "Oxygen Level: " + oxygenLevel + "%, " +
               "Blood Pressure: " + bloodPressure + ", " +
               "Temperature: " + temperature + " °C" +
               "Added on Date: " + added_on + " ";
    }
}