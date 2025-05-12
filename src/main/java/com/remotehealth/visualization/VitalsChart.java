/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.remotehealth.visualization;



import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import com.remotehealth.app.model.Patient;
import javax.swing.*;
import java.sql.*;
import java.awt.Color;  
import java.sql.PreparedStatement;
import java.awt.Dimension;
import com.remotehealth.app.model.DatabaseManager;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;



public class VitalsChart extends JPanel {
    private DatabaseManager dbManager;
    private String patientId;

    // Constructor now takes patientId and dbManager
    public VitalsChart(String patientId, DatabaseManager dbManager) {
        this.dbManager = dbManager;
        this.patientId = patientId;
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        String query = "SELECT added_on, heartRate FROM vitals WHERE patientId = ? ORDER BY added_on";
        
        // Now, use the passed patientId directly
        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setString(1, patientId);  // Set patientId correctly
            ResultSet rs = ps.executeQuery();

            boolean hasData = false;

            // Loop through the results and populate the dataset
            while (rs.next()) {
                String timeLabel = rs.getTimestamp("added_on").toString(); // Format it as needed
                int heartRate = rs.getInt("heartRate");

                // Add data to the dataset
                dataset.addValue(heartRate, "Heart Rate", timeLabel);

                hasData = true;
            }

            if (!hasData) {
                System.out.println("️ No data found for patient: " + patientId);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Create the chart
        JFreeChart chart = ChartFactory.createBarChart("Heart Rate Over Time", "Time", "Heart Rate", dataset, PlotOrientation.VERTICAL, true, true, false);
        ChartPanel chartPanel = new ChartPanel(chart);
        this.add(chartPanel);
        
        this.setPreferredSize(new Dimension(580, 300));
    }


}
