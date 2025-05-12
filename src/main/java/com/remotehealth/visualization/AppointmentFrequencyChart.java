/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.remotehealth.visualization;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import com.remotehealth.app.model.DatabaseManager;

import javax.swing.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class AppointmentFrequencyChart extends JPanel {
    private DatabaseManager dbManager;

    // Constructor now takes a DatabaseManager as a parameter
    public AppointmentFrequencyChart(DatabaseManager dbManager) {
        this.dbManager = dbManager;
        
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        // Use DatabaseManager's getConnection method to get the connection
        try (Connection conn = dbManager.getConnection(); // Use the passed dbManager
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT appointment_date, COUNT(*) as total FROM appointments GROUP BY appointment_date")) {
            
            // Iterate over the result set and populate the dataset
            while (rs.next()) {
                dataset.setValue(rs.getInt("total"), "Appointments", rs.getString("appointment_date"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Create the chart using the populated dataset
        JFreeChart chart = ChartFactory.createBarChart(
                "Appointments per Day",       // chart title
                "Date",                       // x-axis label
                "Count",                      // y-axis label
                dataset,                      // dataset
                org.jfree.chart.plot.PlotOrientation.VERTICAL, // orientation
                false,                        // include legend
                true,                         // tooltips
                false                         // URLs
        );

        // Add the chart to a panel and display it
        ChartPanel chartPanel = new ChartPanel(chart);
        this.add(chartPanel);
    }

    // Main method to test the chart display
    public static void main(String[] args) {
        DatabaseManager dbManager = new DatabaseManager(null); // Pass null initially, will use default connection
        JFrame frame = new JFrame("Appointment Frequency Chart");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new AppointmentFrequencyChart(dbManager)); // Pass the dbManager to the chart
        frame.pack();
        frame.setVisible(true);
    }
}
