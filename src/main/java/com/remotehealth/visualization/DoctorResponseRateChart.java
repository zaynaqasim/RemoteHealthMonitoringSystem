/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.remotehealth.visualization;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.chart.plot.PlotOrientation;
import com.remotehealth.app.model.DatabaseManager;
import javax.swing.*;
import java.sql.*;

public class DoctorResponseRateChart extends JPanel {
    private DatabaseManager dbManager;

    // Constructor takes a DatabaseManager instance
    public DoctorResponseRateChart(DatabaseManager dbManager) {
        this.dbManager = dbManager;
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        try (Connection conn = dbManager.getConnection(); // Use the passed dbManager
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT d.name AS doctor_name, COUNT(p.id) AS total, SUM(CASE WHEN p.acknowledged = 1 THEN 1 ELSE 0 END) AS acknowledged " +
                     "FROM panic_alerts p JOIN doctors d ON p.doctor_id = d.id GROUP BY doctor_name")) {

            while (rs.next()) {
                int total = rs.getInt("total");
                int acknowledged = rs.getInt("acknowledged");
                double percent = total > 0 ? (acknowledged * 100.0 / total) : 0;
                dataset.setValue(percent, "Response Rate %", rs.getString("doctor_name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        JFreeChart chart = ChartFactory.createBarChart("Doctor Panic Response Rate", "Doctor", "% Acknowledged", dataset, PlotOrientation.VERTICAL, false, true, false);
        ChartPanel chartPanel = new ChartPanel(chart);
        this.add(chartPanel);
    }

    public static void main(String[] args) {
        DatabaseManager dbManager = new DatabaseManager(null); // Pass null initially, will use default connection
        JFrame frame = new JFrame("Doctor Response Rate Chart");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new DoctorResponseRateChart(dbManager)); // Pass the dbManager to the chart
        frame.pack();
        frame.setVisible(true);
    }
}
