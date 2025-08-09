# 📖 Baithak Assignment Management System

## 📌 Overview
The **Baithak Assignment Management System** is designed to manage member assignments to different Baithaks (places) on specific days, times, and rotation patterns.  
It supports:
- Scheduling members to places.
- Automatic rotation logic.
- Manual overrides for special assignments.
- Maintaining historical assignment records.

---

## 🗂 Database Structure

The system uses **four tables**:

### 1️⃣ `members`
Stores details of all registered members.

| Column         | Type           | Description |
|----------------|---------------|-------------|
| `id`           | BIGINT (PK)   | Unique identifier for each member. |
| `name`         | VARCHAR       | Member's full name. |
| `gender`       | VARCHAR       | Gender of the member (`पुरुष`, `महिला`, etc.). |
| `phone_number` | VARCHAR       | Contact number. |
| `created_at`   | TIMESTAMP     | Record creation time. |

---

### 2️⃣ `places`
Stores all Baithak locations and schedule info.

| Column           | Type        | Description |
|------------------|------------|-------------|
| `id`             | BIGINT (PK)| Unique ID for the place. |
| `name`           | VARCHAR    | Place name (e.g., खोपोली). |
| `female_allowed` | BOOLEAN    | Whether female members can be assigned. |
| `vaar_code`      | INT        | Numeric day-of-week code. |
| `vaar_name`      | VARCHAR    | Day of the week in text (e.g., सोमवार). |
| `timing_code`    | INT        | Unique code for time slot. |
| `time_slot`      | VARCHAR    | Human-readable time slot. |
| `created_at`     | TIMESTAMP  | Record creation time. |

---

### 3️⃣ `assignments`
Stores member-to-place assignments.

| Column             | Type         | Description |
|--------------------|--------------|-------------|
| `id`               | BIGINT (PK) | Unique ID for the assignment. |
| `member_id`        | BIGINT (FK) | Links to `members.id`. |
| `place_id`         | BIGINT (FK) | Links to `places.id`. |
| `assigned_date`    | DATE        | Date of the assignment. |
| `day_of_week`      | VARCHAR     | Name of the day (e.g., सोमवार). |
| `week_number`      | INT         | Week number in the year. |
| `is_manual`        | BOOLEAN     | Whether assigned manually. |
| `created_at`       | TIMESTAMP   | Record creation time. |
| `assignment_date`  | DATE        | Duplicate column for explicit assignment date. |
| `confirm_if_repeated` | BOOLEAN  | Whether to confirm repeated assignments. |

---

### 4️⃣ `rotation_state`
Tracks rotation index for automated assignments.

| Column                 | Type         | Description |
|------------------------|--------------|-------------|
| `id`                   | BIGINT (PK) | Unique ID. |
| `gender`               | VARCHAR     | Gender category of the rotation set. |
| `last_used_member_index` | INT        | Last assigned member index in the rotation. |
| `updated_at`           | TIMESTAMP   | Last updated time for the rotation state. |

---

## 🔗 Entity Relationships
```plaintext
members (1) ────< assignments >──── (1) places
   ↑                                  ↑
   └──────── rotation_state ──────────┘
