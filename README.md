# Remote Health Monitoring System

## Overview

The **Remote Health Monitoring System** is a console-based application designed to manage health-related information for patients, doctors, and administrators. It allows the management of appointments, vital signs, prescriptions, patient records, and system logs.

The system supports the following roles:
- **Administrator**: Manages doctors, patients, and logs.
- **Doctor**: Views patient information, adds feedback, and prescribes medications.
- **Patient**: Views personal health information and interacts with their doctor.

## Features

- **Admin Panel**: 
  - Add new doctors and patients.
  - View and manage system logs.
  
- **Doctor Panel**:
  - View patient details, medical history, and vital signs.
  - Provide feedback and prescribe medication.
  
- **Patient Panel**:
  - View and update vital signs and health data.
  - View appointment history.
  
- **Appointment Manager**:
  - Schedule, view, and manage appointments between doctors and patients.

- **Vital Sign Monitoring**:
  - Track and store patient vitals such as heart rate, oxygen levels, blood pressure, and temperature.

- **Reminder and Alert System**:
  - Set reminders for appointments and receive emergency alerts.

## Prerequisites

Before running the system, ensure that you have the following installed on your machine:

- **Java 8 or higher**: The system is built using Java.
  - You can download Java from the official [Oracle website](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html) or [OpenJDK](https://openjdk.java.net/).

- **IDE/Text Editor**: Any text editor like **Visual Studio Code**, **Sublime Text**, or an IDE like **IntelliJ IDEA** or **Eclipse** will work.

## Getting Started

### 1. Clone the Repository

Clone the repository to your local machine using Git:

git clone https://github.com/zaynaqasim/RemoteHealthMonitoringSystem.git
### 2. Compile the Code
Once the project is downloaded, open the terminal (or command prompt) and navigate to the project folder:

bash
Copy
Edit
cd path/to/RemoteHealthMonitoringSystem
Compile the Java files by running the following command:

bash
Copy
Edit
javac *.java
### 3. Run the Application
After the code is compiled, you can run the system using the following command:

bash
Copy
Edit
java RemoteHealthMonitoringSystem
This will start the application and present you with the main menu.

### 4. Interact with the System
The following menu options will be available:

plaintext
Copy
Edit
=== Remote Health Monitoring ===
[1] Patient
[2] Doctor
[3] Admin
[4] Appointment Manager
[0] Exit
Patient: Choose option [1] to log in as a patient.

Doctor: Choose option [2] to log in as a doctor.

Admin: Choose option [3] to log in as an administrator (use admin / admin123 credentials).

Appointment Manager: Choose option [4] to manage appointments.

5. Admin Login
To log in as an administrator, use the following default credentials:

Username: admin

Password: admin123

6. Doctor Login
To log in as a doctor, use the doctor’s name and password, which are initially set in the code.

7. Patient Interaction
Patients can interact with their medical records, vital signs, and doctors as specified.

Directory Structure
The project is structured as follows:

bash
Copy
Edit
/RemoteHealthMonitoringSystem
├── Doctor.java                # Doctor-related functionalities
├── Patient.java               # Patient-related functionalities
├── Administrator.java         # Admin-related functionalities
├── VitalSign.java             # Vital sign tracking
├── Feedback.java              # Doctor's feedback
├── Prescription.java          # Prescription management
├── MedicalHistory.java        # Medical history for patients
├── AppointmentManager.java    # Appointment scheduling and management
├── ReminderService.java       # Reminder and notification management
├── EmergencyAlert.java        # Emergency alert system
├── PanicButton.java           # Panic button functionality
├── ChatServer.java            # Chat server functionality
├── ChatClient.java            # Chat client functionality
├── VideoCall.java             # Video call functionality
├── RemoteHealthMonitoringSystem.java  # Main program entry point
└── README.md                 # Documentation file
How to Run the System
Step 1: Set Up Your Environment
Install Java 8 or higher on your system.

Use a text editor or IDE to edit and manage the code.

Step 2: Compile and Run the Code
Clone the repository or download the ZIP file.

Compile all .java files in the project folder using:

bash
Copy
Edit
javac *.java
Run the system using:

bash
Copy
Edit
java RemoteHealthMonitoringSystem
Step 3: Interact with the System
Follow the prompts and choose options for the respective role:

Admin can add doctors and patients and view logs.

Doctor can manage prescriptions and provide feedback.

Patient can view and update their vital signs and health information.
