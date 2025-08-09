# 📖 Baithak Project

## 📌 Overview
The **Baithak Project** is a scheduling and information management system designed to store and manage **Shri Baithak** details, including:
- Baithak type (e.g., पुरुष, महिला, mixed)
- Location (e.g., खोपोली, शिळफाटा)
- Timing and day of the week
- Special service requirements or other event-specific data

The system uses **PostgreSQL** for storing structured data and can be integrated with web or desktop applications for management and viewing.

---

## 🎯 Features
- Store **Baithak schedules** with timings and days.
- Organize by **Baithak type**, location, and code.
- Easy retrieval for reports or display boards.
- Scalable for multiple locations and timings.
- Query support for custom filters (e.g., day, type, location).

---

## 🗂 Database Structure
**Table:** `baithak_schedule`

| Column Name         | Data Type    | Description                                |
|---------------------|-------------|--------------------------------------------|
| `baithak_code`      | INT         | Unique ID for each Baithak                 |
| `baithak_type`      | TEXT        | Type of Baithak (e.g., पुरुष, महिला)       |
| `baithak_name`      | TEXT        | Location name                              |
| `timing_code`       | INT         | Unique timing reference code               |
| `time`              | TEXT        | Time slot (e.g., रात्री ७:४५ ते १०:३०)     |
| `vaar_code`         | INT         | Unique code for the day of the week        |
| `vaar_name`         | TEXT        | Day of the week (e.g., सोमवार)            |

---

## 🛠 Setup

### 1️⃣ Prerequisites
- [PostgreSQL](https://www.postgresql.org/download/) installed
- Basic knowledge of SQL

### 2️⃣ Database Creation
Run the following SQL command to create the table:

```sql
CREATE TABLE baithak_schedule (
    baithak_code INT,
    baithak_type TEXT,
    baithak_name TEXT,
    timing_code INT,
    time TEXT,
    vaar_code INT,
    vaar_name TEXT
);
