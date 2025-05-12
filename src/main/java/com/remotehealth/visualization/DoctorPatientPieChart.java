
package com.remotehealth.visualization;


import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;
import com.remotehealth.app.model.DatabaseManager;
import javax.swing.*;
import java.sql.*;

public class DoctorPatientPieChart extends JPanel {
    private DatabaseManager dbManager;

    // Constructor takes a DatabaseManager instance
    public DoctorPatientPieChart(DatabaseManager dbManager) {
        this.dbManager = dbManager;
        DefaultPieDataset dataset = new DefaultPieDataset();
        
        try (Connection conn = dbManager.getConnection(); // Use the passed dbManager
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT doctor_name, COUNT(*) AS total FROM patients GROUP BY doctor_name")) {
            
            while (rs.next()) {
                dataset.setValue(rs.getString("doctor_name"), rs.getInt("total"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        JFreeChart chart = ChartFactory.createPieChart("Patients per Doctor", dataset, true, true, false);
        ChartPanel chartPanel = new ChartPanel(chart);
        this.add(chartPanel);
    }

    public static void main(String[] args) {
        DatabaseManager dbManager = new DatabaseManager(null); // Pass null initially, will use default connection
        JFrame frame = new JFrame("Doctor Patient Pie Chart");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new DoctorPatientPieChart(dbManager)); // Pass the dbManager to the chart
        frame.pack();
        frame.setVisible(true);
    }
}
