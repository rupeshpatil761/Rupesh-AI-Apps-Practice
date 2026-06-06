# Clinicals System — Project Overview

A full-stack clinical data management system built with a **React frontend** and a **Spring Boot REST API backend**, backed by a **PostgreSQL** database.

---

## What the System Does

This system allows healthcare users to:

- 👤 **Manage patients** — add, view, update and delete patient records
- 📋 **Record clinical measurements** — track blood pressure, heart rate, and other components per patient
- 📊 **View clinical data** — browse all clinical records for a patient in a popup table
- 🔔 **Get instant feedback** — toast notifications for every action in the UI

---

## System Architecture

```
┌──────────────────────────────────────────────────────────────────────┐
│                        User's Browser                                │
│                    http://localhost:3000                              │
│                                                                      │
│   ┌─────────────────────────────────────────────────────────────┐   │
│   │                  clinicals-ui-app  (React 19)               │   │
│   │                                                             │   │
│   │   Pages                                                     │   │
│   │   ├── /                  Home (patient list + actions)      │   │
│   │   ├── /add-patient       Add Patient form                   │   │
│   │   └── /add-clinicals/:id Add Clinical Data form             │   │
│   │                                                             │   │
│   │   Libraries                                                 │   │
│   │   ├── React Router DOM   (page navigation)                  │   │
│   │   ├── Axios              (HTTP calls to API)                │   │
│   │   └── React Toastify     (notifications)                    │   │
│   └────────────────────────┬────────────────────────────────────┘   │
└────────────────────────────│────────────────────────────────────────┘
                             │
                   HTTP REST (Axios)
                   CORS allowed ✅
                             │
┌────────────────────────────▼────────────────────────────────────────┐
│                clinicalsapi  (Spring Boot 4 / Java 21)              │
│                    http://localhost:8080                             │
│                                                                      │
│   Controllers                                                        │
│   ├── PatientController       /api/patients/**                      │
│   └── ClinicalDataController  /api/clinicaldata/**                  │
│                                                                      │
│   Cross-cutting                                                      │
│   ├── GlobalExceptionHandler  (structured error responses)          │
│   ├── CorsConfig              (allows localhost:3000)               │
│   └── SLF4J Logging           (console, DEBUG level)               │
└────────────────────────────┬────────────────────────────────────────┘
                             │
                    JPA / Hibernate
                             │
┌────────────────────────────▼────────────────────────────────────────┐
│               PostgreSQL Database — clinicals                        │
│               localhost:5432                                         │
│                                                                      │
│   ┌─────────────────┐      ┌─────────────────────────────────┐      │
│   │    patient       │      │         clinical_data           │      │
│   ├─────────────────┤ 1──* ├─────────────────────────────────┤      │
│   │ id (PK)         │      │ id (PK)                         │      │
│   │ first_name      │      │ patient_id (FK)                 │      │
│   │ last_name       │      │ component_name  (bp/heartrate)  │      │
│   │ age             │      │ component_value (120/80, 72…)   │      │
│   └─────────────────┘      │ measured_date_time              │      │
│                             └─────────────────────────────────┘      │
└─────────────────────────────────────────────────────────────────────┘
```

---

## User Flow Diagram

```
User Opens Browser (localhost:3000)
          │
          ▼
    ┌─────────────┐
    │  Home Page  │  ── shows all patients in a table
    └──────┬──────┘
           │
    ┌──────┴───────────────────────────┐
    │                                  │
    ▼                                  ▼
Add Patient                    Select a Patient
    │                                  │
    ▼                            ┌─────┴──────────────┐
Patient created                  │                    │
(POST /api/patients)    Add Clinical Data      View Clinical Data
                              │                      │
                              ▼                      ▼
                     Form: component name    Popup table with
                     + value submitted       all measurements
                     (POST /api/clinicaldata  (GET /api/clinicaldata
                      /clinicals)             /patient/{id})
```

---

## Applications at a Glance

| | clinicals-ui-app | clinicalsapi |
|---|---|---|
| **Type** | Frontend | Backend |
| **Technology** | React 19 + Axios | Spring Boot 4 + Java 21 |
| **Port** | 3000 | 8080 |
| **Routing** | React Router DOM | Spring MVC |
| **HTTP Client** | Axios | — |
| **Database** | — | PostgreSQL (JPA/Hibernate) |
| **Testing** | React Testing Library | JUnit 5 + Mockito |
| **Notifications** | React Toastify | — |
| **Error Handling** | Toast messages | GlobalExceptionHandler |
| **Logging** | Browser console | SLF4J (DEBUG level) |

---

## API Endpoints Used by the UI

| UI Action | Method | Endpoint |
|---|---|---|
| Load patient list | `GET` | `/api/patients` |
| Add new patient | `POST` | `/api/patients` |
| Delete patient | `DELETE` | `/api/patients/{id}` |
| Add clinical data | `POST` | `/api/clinicaldata/clinicals` |
| View clinical data | `GET` | `/api/clinicaldata/patient/{patientId}` |

---

## Quick Start — Run Both Apps

### 1. Start the Database

Make sure PostgreSQL is running and the `clinicals` database exists:

```sql
CREATE DATABASE clinicals;
```

### 2. Start the Backend API

```bash
cd clinicalsapi
mvn spring-boot:run
```

Backend runs at: `http://localhost:8080`

### 3. Start the Frontend UI

```bash
cd clinicals-ui-app
npm install
npm start
```

Frontend runs at: `http://localhost:3000`

---

## Project Structure

```
copilot-practice/
├── clinicalsapi/                        ← Spring Boot Backend
│   ├── src/main/java/.../
│   │   ├── controllers/                 (REST endpoints)
│   │   ├── models/                      (Patient, ClinicalData)
│   │   ├── repositories/                (JPA data access)
│   │   ├── exceptions/                  (error handling)
│   │   ├── dto/                         (request objects)
│   │   └── config/                      (CORS, logging)
│   ├── src/main/resources/
│   │   ├── application.properties
│   │   └── db/clinicals.sql
│   └── pom.xml
│
└── clinicals-ui-app/                    ← React Frontend
    ├── src/
    │   ├── App.js                       (routes + toast container)
    │   ├── components/
    │   │   ├── Home.js                  (patient list, modal, delete)
    │   │   ├── AddPatient.js            (patient form)
    │   │   └── AddClinicals.js          (clinical data form)
    │   └── App.css
    └── package.json
```

---

## Tech Stack Summary

| Layer | Technology |
|---|---|
| UI Framework | React 19 |
| UI Routing | React Router DOM 7 |
| HTTP Client | Axios |
| Notifications | React Toastify |
| Backend Framework | Spring Boot 4.0.6 |
| Language | Java 21 |
| ORM | JPA / Hibernate |
| Database | PostgreSQL |
| Build Tool (BE) | Maven |
| Build Tool (FE) | Create React App |

---

## Author

Rupesh Patil

