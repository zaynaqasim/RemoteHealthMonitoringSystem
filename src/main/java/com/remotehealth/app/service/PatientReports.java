
package com.remotehealth.app.service;

import com.remotehealth.app.model.DatabaseManager;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import java.io.IOException;
import java.util.List;
import com.remotehealth.app.model.Prescription;
import com.remotehealth.app.model.VitalSign;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.io.IOException;
import java.util.List;

public class PatientReports {

    public static void generateReport(String patientId, String filePath, DatabaseManager dbManager) {
        try (
            PDDocument document = new PDDocument();
        ) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            PDPageContentStream content = new PDPageContentStream(document, page);
            content.setFont(PDType1Font.HELVETICA_BOLD, 18);
            content.beginText();
            content.newLineAtOffset(50, 750);
            content.showText("Patient Report for ID: " + patientId);
            content.endText();

            content.setFont(PDType1Font.HELVETICA, 12);
            float yPosition = 720;

            

            List<VitalSign> vitals = dbManager.getVitalsForPatient(patientId);
            content.beginText();
            content.newLineAtOffset(50, yPosition);
            content.showText("Vital Signs:");
            content.endText();
            yPosition -= 20;

            for (VitalSign v : vitals) {
                content.beginText();
                content.newLineAtOffset(50, yPosition);
                content.showText(v.toString());
                content.endText();
                yPosition -= 15;
            }

            List<Prescription> prescriptions = dbManager.getPrescriptionsForPatient(patientId);
            content.beginText();
            content.newLineAtOffset(50, yPosition - 10);
            content.showText("Prescriptions:");
            content.endText();
            yPosition -= 30;

            for (Prescription p : prescriptions) {
                content.beginText();
                content.newLineAtOffset(50, yPosition);
                content.showText(p.toString());
                content.endText();
                yPosition -= 15;
            }

            content.close();
            document.save(filePath);
            System.out.println("Report saved to " + filePath);

       } catch (IOException e) {
    e.printStackTrace();
}

    }
}
