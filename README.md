# Remote Health Monitoring System

A full-featured Java-based desktop application designed to facilitate remote healthcare management. The system supports real-time communication, medical data monitoring, and administrative tools for doctors, patients, and administrators.

## 🩺 Project Overview

The **Remote Health Monitoring System** enables doctors and patients to interact digitally while providing core functionality like:

- Appointment scheduling and management
- Health data and vitals tracking
- Medical history and prescription handling
- Doctor-patient feedback system
- Admin dashboards for system control
- Video call module (placeholder for future integration)
- Reminder services via email
- Secure login system with password recovery

## 📁 Project Structure

RemoteHealthMonitoringSystemProject/
├── .env # Environment variables
├── pom.xml # Maven dependencies
├── src/
│ └── main/
│ └── java/
│ └── com/remotehealth/app/
│ ├── RemoteHealthMonitoringSystemProject.java # Main class
│ ├── communication/VideoCall.java # Placeholder for video calls
│ ├── gui/ # Swing UI screens
│ │ ├── LoginScreen.java
│ │ ├── PatientDashboard.java
│ │ ├── AdminDashboard.java
│ │ └── ...
│ └── model/ # Core business logic
│ ├── Doctor.java
│ ├── Patient.java
│ ├── Appointment.java
│ ├── Prescription.java
│ ├── Feedback.java
│ └── ...


## ⚙️ Technologies Used

- **Java** (JDK 17+)
- **Swing** for GUI development
- **Maven** for dependency management
- **Gmail API** for email alerts and reminders
- **MySQL** (to be integrated for persistent data storage)

## 🔐 Features

| Feature                          | Description |
|----------------------------------|-------------|
| 🧑‍⚕️ Doctor Dashboard           | View appointments, feedback, prescribe medicines |
| 👩‍⚕️ Patient Dashboard          | View vitals, upcoming appointments, and medical history |
| 🗓️ Appointment Management        | Schedule and track patient-doctor sessions |
| 💊 Prescription Handling         | Doctors can issue prescriptions and attach them to patient profiles |
| 📬 Email Reminders               | Email notifications for appointments and prescriptions |
| 🔒 Login & Forgot Password       | Authentication and password reset functionality |
| 📹 Video Call (Prototype)        | Framework for future video call integration |
| 📊 Admin Dashboard               | Full control over users, logs, and feedback |

## 🛠️ Setup Instructions

### Prerequisites

- JDK 17 or higher
- Maven
- (Optional) MySQL Server (for database persistence)
- Internet access for Gmail API email integration

### Installation

1. **Clone or unzip the repository:**

   ```bash
   git clone https://github.com/your-username/RemoteHealthMonitoringSystem.git
   cd RemoteHealthMonitoringSystem
Configure environment variables:

Update the .env file with:
GMAIL_API_KEY=your-api-key
GMAIL_SENDER_ADDRESS=your-email@gmail.com

Build the project with Maven:
mvn clean install

Run the application:
mvn exec:java -Dexec.mainClass="com.remotehealth.app.RemoteHealthMonitoringSystemProject"

📈 Future Extensions
Database integration using MySQL

Real-time video consultation using WebRTC or Jitsi

Role-based access control and audit logs

Mobile application (Android/iOS)

Health analytics dashboard using charting libraries
