# 🎓 Student Management System (EduPortal)

A modern, desktop-based **Student Management System** developed using **Java Swing** and **MySQL**. This system provides a clean, web-inspired user interface for managing students, teachers, courses, and attendance efficiently.

---

## ✨ Features

- **🔐 User Authentication**
  - Modern Web-Style Login UI.
  - Functional **Sign Up** for new user registration.
  - **Forgot Password** feature for quick credential updates.

- **👨‍🎓 Student Management**
  - Add, update, view, and delete student details.
  - Track student contact information and enrollment data.

- **👨‍🏫 Teacher & Course Management**
  - Maintain teacher records and specializations.
  - Manage course listings, durations, and fee structures.

- **📅 Attendance Tracker**
  - Record daily attendance for students with simple present/absent statuses.

---

## 🛠️ Tech Stack & Technologies

- **Programming Language:** Java (JDK 8 or higher)
- **GUI Framework:** Java Swing / AWT
- **Database:** MySQL
- **Database Driver:** MySQL Connector/J (`mysql-connector-j-x.x.x.jar`)
- **IDE:** Visual Studio Code / Eclipse / NetBeans

---

## 🚀 Getting Started

Follow these steps to set up and run the project locally on your machine.

### Prerequisites

1. **Java Development Kit (JDK)** installed.
2. **XAMPP** or **MySQL Server** installed and running.
3. **MySQL Connector JAR** added to your Java project libraries.

---

### 🗄️ Database Setup

1. Open **phpMyAdmin** (`http://localhost/phpmyadmin`) or MySQL Workbench.
2. Create a new database named `student_system`.
3. Run the following SQL queries to create the necessary tables and default admin user:

```sql
CREATE DATABASE IF NOT EXISTS student_system;
USE student_system;

-- Users Table
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(50) NOT NULL
);

-- Default Admin Account
INSERT INTO users (username, password) VALUES ('admin', '123')
ON DUPLICATE KEY UPDATE username=username;
