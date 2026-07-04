# Scholarship Application Tracker

A comprehensive Java Desktop (Swing) application designed to streamline, manage, and track student scholarship applications. The system employs a robust Model-View-Controller (MVC) style structure, utilizes Data Access Objects (DAOs) for database operations, and connects to a MySQL backend.

## 🚀 Features
* **User Authentication:** Dedicated, secure Login and Registration systems for students and administrators.
* **Interactive Dashboard:** Role-based access controls providing customized views for both applicants and administrative managers.
* **Scholarship Analytics:** Built-in data visualization components (`BarChartPanel` and `PieChartPanel`) to track application metrics and distributions graphically.
* **Application Management:** Complete lifecycle management for scholarships (creation, tracking, status updates, and notifications).

## 🛠️ Tech Stack
* **Language:** Java (JDK 8 or higher)
* **GUI Framework:** Java Swing & AWT
* **Database:** MySQL 8.0 Server
* **Architecture:** DAO (Data Access Object) Design Pattern with Singleton Database Connections

## 📂 Project Structure
* `/src/model/` - Contains core data entities (`User`, `Scholarship`, `Application`).
* `/src/dao/` - Handles all database operations, CRUD functions, and connection management.
* `/src/ui/` - Contains all graphical user interface components and visual charts.
* `/database.sql` - Complete relational schema setup script.

## 📋 Setup and Installation

### 1. Database Setup
1. Open your MySQL command-line tool or workbench.
2. Execute the schema structure provided in the `database.sql` file at the root of this project to instantly generate the `scholarship_tracker` database and its corresponding tables.

### 2. Configuration
Before launching the application, verify your local MySQL credentials match the configuration in:
`src/dao/DatabaseConnection.java`

```java
private static final String URL = "jdbc:mysql://localhost:3306/scholarship_tracker";
private static final String USER = "root";
private static final String PASSWORD = "your_mysql_password";
