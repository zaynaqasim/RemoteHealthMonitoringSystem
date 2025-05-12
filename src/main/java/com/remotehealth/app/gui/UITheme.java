package com.remotehealth.app.gui;

import java.awt.*;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.BorderFactory;

public class UITheme {
    // Color Palette
    public static final Color PRIMARY_COLOR = new Color(0, 102, 153); // Dark blue
    public static final Color SECONDARY_COLOR = new Color(240, 240, 240); // Light gray
    public static final Color ACCENT_COLOR = new Color(0, 150, 136); // Teal
    public static final Color WARNING_COLOR = new Color(255, 87, 34); // Orange
    public static final Color SUCCESS_COLOR = new Color(76, 175, 80); // Green
    public static final Color DANGER_COLOR = new Color(244, 67, 54); // Red
    
    // Fonts
    public static final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 24);
    public static final Font SUBHEADER_FONT = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font BODY_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 14);
    
    // Spacing
    public static final int PADDING = 15;
    public static final int COMPONENT_GAP = 10;
    
    // Helper methods
    public static JButton createPrimaryButton(String text) {
        return createStyledButton(text, PRIMARY_COLOR);
    }
    
    public static JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFont(BUTTON_FONT);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        return button;
    }
    
    public static JPanel createCardPanel() {
        JPanel card = new JPanel();
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(SECONDARY_COLOR, 1),
            BorderFactory.createEmptyBorder(PADDING, PADDING, PADDING, PADDING)
        ));
        return card;
    }
}