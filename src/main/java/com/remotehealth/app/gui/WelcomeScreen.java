package com.remotehealth.app.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import com.remotehealth.app.utils.*;

public class WelcomeScreen extends JFrame {
    public WelcomeScreen() {
        setTitle("Remote Health Monitoring System");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        initUI();
    }
    
    private void initUI() {
        setLayout(new BorderLayout());
        getContentPane().setBackground(UITheme.SECONDARY_COLOR);
        
        // Main content
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(UITheme.SECONDARY_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);
        gbc.fill = GridBagConstraints.CENTER;
        
        // Title
        JLabel titleLabel = new JLabel("Remote Health Monitoring System");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        titleLabel.setForeground(UITheme.PRIMARY_COLOR);
        gbc.gridx = 0;
        gbc.gridy = 0;
        mainPanel.add(titleLabel, gbc);
        
        // Subtitle
        JLabel subtitleLabel = new JLabel("Advanced Patient Care Through Technology");
        subtitleLabel.setFont(UITheme.SUBHEADER_FONT);
        gbc.gridy = 1;
        mainPanel.add(subtitleLabel, gbc);
        
        // Features panel
        JPanel featuresPanel = new JPanel(new GridLayout(1, 3, 20, 20));
        featuresPanel.setBackground(UITheme.SECONDARY_COLOR);
        featuresPanel.add(createFeatureCard("Real-time Monitoring", "Track vital signs continuously"));
        featuresPanel.add(createFeatureCard("Secure Communication", "Encrypted doctor-patient messaging"));
        featuresPanel.add(createFeatureCard("Emergency Alerts", "Instant notifications for critical conditions"));
        
        gbc.gridy = 2;
        mainPanel.add(featuresPanel, gbc);
        
        // Login button
        JButton loginButton = UITheme.createPrimaryButton("Get Started");
        loginButton.setPreferredSize(new Dimension(200, 50));
        loginButton.addActionListener(e -> {
            dispose();
            WindowManager.showLoginScreen();
        });
        
        gbc.gridy = 3;
        mainPanel.add(loginButton, gbc);
        
        add(mainPanel, BorderLayout.CENTER);
        
        // Footer
        JLabel footerLabel = new JLabel("© 2025 Remote Health Monitoring System", SwingConstants.CENTER);
        footerLabel.setFont(UITheme.BODY_FONT);
        footerLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(footerLabel, BorderLayout.SOUTH);
    }
    
    private JPanel createFeatureCard(String title, String description) {
        JPanel card = UITheme.createCardPanel();
        card.setLayout(new BorderLayout(10, 10));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(UITheme.SUBHEADER_FONT);
        titleLabel.setForeground(UITheme.PRIMARY_COLOR);
        
        JLabel descLabel = new JLabel("<html><center>" + description + "</center></html>");
        descLabel.setFont(UITheme.BODY_FONT);
        
        card.add(titleLabel, BorderLayout.NORTH);
        card.add(descLabel, BorderLayout.CENTER);
        
        return card;
    }
}