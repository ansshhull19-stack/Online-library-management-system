# 📚 Online Library Management System (LibraryHub)

> **College Java Web Project**  
> Built with **Java 17**, **Jakarta Servlets 6.0**, **JSP 3.1**, **Apache Tomcat 10.1**, and **MySQL 8.0**.  
> Bundled with **Maven Wrapper (`mvnw.cmd`)** for zero-setup builds.

---

## 🌟 Key Features

### 👤 Student Features
- **Registration & Authentication**: Student self-registration, BCrypt password hashing, session authentication, and logout.
- **Search & Browse Catalog**: Filter by title, author, ISBN, and category. Real-time availability indicator.
- **Borrow Books**: Atomic stock decrements, 14-day standard loan period, and automatic due date assignment.
- **My Loans & Returns**: Track active loans, overdue alerts, calculated fine estimates ($1.00/day), and one-click return.

### 🛡️ Admin Features
- **Admin Dashboard**: Real-time stats on total books, physical inventory, active loans, overdue books, and student enrollments.
- **Book Management**: Full CRUD operations (Add new books, edit existing metadata/copies, delete inactive titles).
- **Circulation Audit Log**: Ledger of all checkout and return transactions with status filtering (`BORROWED`, `RETURNED`, `OVERDUE`).
- **Student Directory**: Roster of registered student accounts.

### 🔒 Security & Quality Standards
- **BCrypt Hashing**: 12 salt rounds via `jBCrypt` for all passwords.
- **SQL Injection Prevention**: 100% Prepared Statements (`PreparedStatement`) across all DAOs.
- **Transaction Safety**: ACID compliance with `setAutoCommit(false)`, `commit()`, and `rollback()` for loan and inventory operations.
- **Session Management**: Session fixation mitigation and role-based filtering via `AuthFilter`.
- **Credential Safety**: Local `db.properties` is ignored by `.gitignore`.

---

## 🛠️ Technology Stack & Environment Prerequisites

| Component | Specification | Status / Requirement |
| :--- | :--- | :--- |
| **Java** | OpenJDK 17 LTS | Requires JDK 17 to compile and run |
| **Servlet Engine** | Jakarta Servlet 6.0 (Tomcat 10.1 compatible) | Standard `.war` packaging |
| **Database** | MySQL Server 8.0+ | **Already Installed & Running on this machine** |
| **Build Tool** | Apache Maven 3.9.6 | **Bundled via Maven Wrapper (`mvnw.cmd`)** &mdash; no separate installation needed! |
| **Version Control** | Git | Configured with `.gitignore` and GitHub-ready |

---

## 🚀 Quick Setup Instructions (Windows)

### Step 1: Initialize MySQL Database
MySQL Server 8.0 is running on your system. Run the provided SQL scripts using PowerShell or Command Prompt:

```powershell
# Open MySQL terminal and execute schema & seed files:
& "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe" -u root -p < "sql\schema.sql"
& "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe" -u root -p < "sql\seed.sql"
```
*(Alternatively, open **MySQL Workbench**, create a connection, and execute `sql/schema.sql` followed by `sql/seed.sql`.)*

### Step 2: Configure Database Credentials
Edit `src/main/resources/db.properties` (or set environment variables `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`):

```properties
db.url=jdbc:mysql://localhost:3306/library_management?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8
db.username=root
db.password=YOUR_MYSQL_ROOT_PASSWORD
```

### Step 3: Compile and Test
Using the included Maven Wrapper:

```powershell
# Run the automated test suite
.\mvnw.cmd test

# Build the deployable WAR package
.\mvnw.cmd clean package
```
This produces `target/library-management.war`.

### Step 4: Deploy to Apache Tomcat 10.1
1. Download and extract **Apache Tomcat 10.1** (e.g., to `C:\apache-tomcat-10.1`).
2. Copy `target/library-management.war` into Tomcat's `webapps\` directory:
   ```powershell
   Copy-Item "target\library-management.war" "C:\apache-tomcat-10.1\webapps\"
   ```
3. Start Tomcat:
   ```powershell
   & "C:\apache-tomcat-10.1\bin\startup.bat"
   ```
4. Open your browser at:  
   👉 **`http://localhost:8080/library-management`**

---

## 🔑 Demo Login Accounts

| Role | Username | Password | Purpose |
| :--- | :--- | :--- | :--- |
| **System Administrator** | `admin` | `Admin@123` | Full dashboard, catalog CRUD, audit log |
| **Student** | `john_doe` | `Student@123` | Browse, borrow, return, loan history |
| **Student** | `emma_watson` | `Student@123` | Secondary test account with overdue loan |

---

## 🧪 Automated Testing

The project includes JUnit 5 and business logic tests under `src/test/java`:
- `PasswordUtilTest`: Validates 60-character BCrypt hashing and verification.
- `DateUtilTest`: Tests 14-day checkout calculations, overdue days, and fine arithmetic.
- `ModelValidationTest`: Tests book availability bounds and loan status transitions.

Run with:
```powershell
.\mvnw.cmd test
```

---

## 📁 Project Structure

```
library-management-system/
├── .mvn/wrapper/                  # Maven Wrapper bootstrap
├── src/
│   ├── main/
│   │   ├── java/com/library/
│   │   │   ├── config/DBConnection.java
│   │   │   ├── dao/               # UserDAO, BookDAO, BorrowingDAO, AdminDAO
│   │   │   ├── filter/AuthFilter.java
│   │   │   ├── model/             # User, Admin, Book, Borrowing
│   │   │   ├── servlet/           # Auth, Catalog, Borrow, Return, MyLoans
│   │   │   │   └── admin/         # Dashboard, Book CRUD, Transactions, Users
│   │   │   └── util/              # PasswordUtil (BCrypt), DateUtil
│   │   ├── resources/
│   │   │   ├── db.properties.example
│   │   │   └── db.properties
│   │   └── webapp/
│   │       ├── assets/css/style.css
│   │       ├── assets/js/main.js
│   │       ├── WEB-INF/
│   │       │   ├── web.xml
│   │       │   └── views/         # Secure JSP Views (Home, Catalog, Admin, Loans)
│   │       └── index.jsp
│   └── test/java/com/library/     # JUnit 5 test suite
├── sql/
│   ├── schema.sql                 # MySQL schema with FKs and indexes
│   └── seed.sql                   # 15+ books, demo users, sample loans
├── .gitignore
├── mvnw & mvnw.cmd
├── pom.xml
└── README.md
```
