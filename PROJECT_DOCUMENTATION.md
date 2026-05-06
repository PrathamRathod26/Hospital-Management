# Hospital Management System Documentation

## Overview
The Hospital Management System is a comprehensive full-stack application designed to streamline hospital operations. It provides role-based interfaces for Patients, Doctors, and Hospital Staff to manage appointments, medical records, scheduling, and user profiles.

## Technology Stack
- **Backend:** Java 21, Spring Boot 4.0.5 (Data JPA, Security, WebMVC), PostgreSQL, JSON Web Tokens (JJWT), MapStruct, SpringDoc OpenAPI, Apache PDFBox.
- **Frontend:** Angular 21, Angular Material, TypeScript, RxJS, SCSS.

## Backend Architecture
The backend follows a standard N-tier architecture built with Spring Boot:
- `controller/`: REST API endpoints for client communication.
- `service/`: Core business logic and validation.
- `repository/`: Database interactions using Spring Data JPA.
- `entity/`: Database schemas mapping using Hibernate.
- `mapper/`: Data Transfer Object (DTO) mapping using MapStruct.
- `config/`: Security, JWT handling, CORS, and general configurations.

### Core Database Entities
1. **User**: Manages authentication credentials and role assignments (`ADMIN`, `DOCTOR`, `PATIENT`, `STAFF`).
2. **Patient**: Stores comprehensive patient profile details.
3. **Doctor**: Stores doctor profile details, specialty, and schedule references.
4. **Slot**: Manages granular doctor availability slots for appointment booking.
5. **DoctorSchedule**: Tracks overarching schedules for doctors.
6. **Appointment**: Maps patients to doctors for specific slots, including status tracking (e.g., Draft, Confirmed).
7. **Prescription**: Medication and treatment records linked to an appointment.
8. **PatientDocument** & **AppointmentDocument**: Stores file paths or binary data for medical records and reports.

### Security & Role-Based Access Control (RBAC)
The application is secured using **JWT (JSON Web Tokens)**. Endpoints are strictly protected based on the user's role defined in `WebSecurityConfig`:
- **`ROLE_ADMIN`**: Complete access to administrative endpoints (`/api/admin/**`).
- **`ROLE_DOCTOR`**: Can manage availability slots, view assigned patient details, prescribe medication, and manage their own appointments.
- **`ROLE_PATIENT`**: Can view available slots, book appointments, view own medical documents, and manage their personal profile.
- **`ROLE_STAFF`**: Can register new users and create appointments on behalf of patients.
- **Public**: Login/Register APIs, public slot availability, and Swagger UI (`/swagger-ui.html`).

## Frontend Architecture
The frontend is a modular Single Page Application (SPA) built with Angular 21 and styled with Angular Material.

### Application Routing & Features
- **Authentication (`/auth`)**: Separate login/registration flows for `DoctorAuth`, `PatientAuth`, and `StaffAuth`.
- **Patient Dashboard:**
  - `patient/profile`: View and edit personal information.
  - `patient/appointments`: View historical and upcoming appointments.
  - `patient/make-appointment`: Interface to browse slots and book new appointments.
  - `patient/draft`: View unconfirmed or draft appointments.
- **Doctor Dashboard:**
  - `doctor/profile`: View and edit professional information.
  - `doctor/slot`: Manage daily availability slots.
  - `doctor/appointments`: View a list of upcoming patient appointments.
  - `patient-details/:patientId/:appointmentId`: Deep dive into specific patient medical history and interface to add prescriptions.
- **Staff Interface:**
  - `patient/make-appointment/new`: Staff-specific interface to register walk-in patients and instantly book appointments for them.

### Security Guards & Interceptors
- **`auth.interceptor.ts`**: Automatically attaches the JWT Authorization header to outgoing HTTP requests and handles session-level logic.
- **`auth.guard.ts`**: Route guards (`doctorGuard`, `patientGuard`, `staffGuard`, `guestGuard`) ensure that users cannot navigate to unauthorized pages in the SPA.

## API Endpoints Summary
- **User Management**: `/api/user/**` (Registration, login, profile updates)
- **Entity Management**: `/api/doctor/**` / `/api/patient/**` (CRUD operations)
- **Appointments**: `/api/appointment/**` (Booking, status updates, drafts)
- **Scheduling**: `/api/slot/**` (Slot creation for Doctors, viewing for Patients/Public)
- **Medical Records**: `/api/prescription/**` (Prescribing medication), `/api/document/**` (Uploading/viewing records)

## Local Development Setup

### Backend Setup
1. Ensure **PostgreSQL** is running on your system.
2. Verify database credentials in `HospitalManagement/src/main/resources/application.properties` (or `.yml`).
3. Navigate to the `HospitalManagement` directory.
4. Run the Spring Boot application using your IDE or via Maven: `mvn spring-boot:run`.
5. The backend server will start on `http://localhost:8080`.
6. Swagger UI for API testing is available at `http://localhost:8080/swagger-ui.html`.

### Frontend Setup
1. Ensure **Node.js** and **npm** are installed.
2. Navigate to the `HospitalManagementUI` directory.
3. Install dependencies: `npm install`
4. Start the development server: `npm start` (or `ng serve`).
5. Access the application in your browser at `http://localhost:4200`.
