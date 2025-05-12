/**
 * The VideoCall class enables starting a video consultation between a doctor and patient
 * using the Jitsi Meet platform. It constructs a unique meeting link based on user names,
 * copies the link to the clipboard, and opens it in the default browser.
 * This class provides a simple integration with external video calling without requiring in-app video infrastructure.
 * 
 * Example usage:
 * <pre>
 *     VideoCall call = new VideoCall();
 *     call.startCall("PatientName", "DoctorName", "secure123");
 * </pre>
 * 
 * Note: Users must have internet access and a compatible browser for this feature to work.
 * 
 * @author 
 * @version 1.0
 */
package com.remotehealth.app.communication;

import java.awt.Desktop;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.net.URI;
import javax.swing.JOptionPane;

public class VideoCall {

    /**
     * Initiates a video call between two users by creating a Jitsi Meet link.
     * The link is copied to the clipboard and opened in the default system browser.
     * 
     * @param from The user initiating the call (typically patient)
     * @param to The recipient of the call (typically doctor)
     * @param password Optional password to secure the meeting room
     */
    public void startCall(String from, String to, String password) {
        try {
            // Generate room name (alphanumeric only)
            String roomName = "health-" + 
                from.replaceAll("[^a-zA-Z0-9]", "").toLowerCase() + "-" + 
                to.replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
            
            String callUrl = "https://meet.jit.si/" + roomName + 
                (password == null || password.isEmpty() ? "" : "?password=" + password);
            
            // Copy link to clipboard
            copyToClipboard(callUrl);
            JOptionPane.showMessageDialog(null, 
                "Video call link copied to clipboard!\nShare it with the doctor",
                "Call Started", 
                JOptionPane.INFORMATION_MESSAGE);
            
            // Open in browser
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(new URI(callUrl));
            } else {
                JOptionPane.showMessageDialog(null,
                    "Please open this link manually: " + callUrl,
                    "Browser Not Supported",
                    JOptionPane.WARNING_MESSAGE);
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                "Error starting video call: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Copies a given text string to the system clipboard.
     * Used internally to copy the video call link.
     * 
     * @param text The string to copy to clipboard
     */
    private void copyToClipboard(String text) {
        try {
            StringSelection selection = new StringSelection(text);
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(selection, null);
        } catch (Exception e) {
            System.out.println("[Warning] Couldn't copy link automatically");
        }
    }
}
