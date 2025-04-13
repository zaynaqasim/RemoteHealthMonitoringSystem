# RemoteHealthMonitoringSystem
Remote Health Monitoring System
This is a console-based application for remote health monitoring, enabling interaction between Patients, Doctors, and Administrators. The system allows for managing appointments, vital sign tracking, medical history, and other functionalities crucial to remote healthcare. The application provides various services like notifications, emergency alerts, video calls, and more.

>Features
-Patient Management
  View and update vital signs.
  View and manage medical history (diagnoses, treatments, prescriptions, consultations).
  Receive health reminders, alerts, and notifications.
-Doctor Management
   Provide feedback on patient conditions.
   Write prescriptions.
   View medical history and vital signs of patients.
-Administrator Management
   Add patients and doctors to the system.
   View system logs of activities.
-Appointment Management
   Schedule, view, and manage appointments.
-Vital Sign Monitoring
   Track vital signs (heart rate, oxygen levels, blood pressure, and temperature) of patients.
-Emergency & Panic Alerts
   Send alerts in case of a medical emergency.
-Communication
   In-app messaging through a chat system.
   Video call integration for remote consultations.

>Requirements
Java 8 or higher

>Classes Overview
1. Administrator
Responsible for managing system users (doctors, patients) and viewing system logs.

Methods:
authenticate(): Validates admin credentials.
addDoctor(), addPatient(): Adds new users to the system.
logAction(), viewLogs(): Logs and views administrator actions.

2. Doctor
Represents a doctor in the system with attributes like id, name, email, and password.

Methods:
authenticate(): Verifies doctor's password.
handle(): Handles doctor-specific functionality (viewing patients, adding prescriptions, etc.).

3. Patient
Represents a patient with personal details and medical history.

Methods:
handle(): Handles patient-specific functionality (viewing vital signs, history, etc.).

4. AppointmentManager
Manages appointments between patients and doctors.

Methods:
scheduleAppointment(), viewAppointments(), handle(): Scheduling and managing appointments.

5. VitalsDatabase & VitalSign
Manages and stores patient vital signs (heart rate, oxygen level, blood pressure, temperature).

Methods:
addVital(), displayVitals(): Adding and displaying vital signs.

6. MedicalHistory
Stores the patient's medical history, including diagnoses, treatments, prescriptions, and consultations.

Methods:
addDiagnosis(), addTreatment(), addPrescription(), displayHistory(): Manage medical history.

7. EmergencyAlert, PanicButton, ReminderService
Handle emergency situations, panic alerts, and health reminders via notifications (Email/SMS).

Methods:
sendAlert(), sendReminder(): Send alerts and reminders to the user.

8. ChatServer, ChatClient, VideoCall
Provide communication services between the patient and doctor via chat and video call.

How to Run the Application

The system will present a menu, allowing you to interact with different functionalities. The available roles and options are:
Admin Menu:
Log in with the admin credentials (username: admin, password: admin123).
Add new Doctors and Patients.
View System Logs of all actions performed by the admin.

Doctor Menu:
Enter doctor credentials to log in.
View and update Patient Information and Medical History.
Write Prescriptions for patients.
Provide Feedback for patient conditions.

Patient Menu:
Enter Patient ID to view and update personal information.
View Vital Signs such as heart rate, oxygen level, blood pressure, and temperature.
Manage Appointments with doctors.

Appointment Manager:
Schedule, view, and manage appointments between doctors and patients.




