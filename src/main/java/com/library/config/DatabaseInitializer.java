package com.library.config;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Initializes database tables and seed data if not present.
 * Ensures that both MySQL and local fallback databases have complete schema and demo data.
 */
public class DatabaseInitializer {

    private static final Logger LOGGER = Logger.getLogger(DatabaseInitializer.class.getName());
    public static synchronized void ensureInitialized(Connection conn) {
        if (conn == null) {
            return;
        }

        try {
            createSchema(conn);
            insertSeedData(conn);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed during database initialization: " + e.getMessage(), e);
        }
    }

    private static void createSchema(Connection conn) throws Exception {
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS users (" +
                    "user_id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "username VARCHAR(50) NOT NULL UNIQUE, " +
                    "email VARCHAR(100) NOT NULL UNIQUE, " +
                    "password_hash VARCHAR(255) NOT NULL, " +
                    "full_name VARCHAR(100) NOT NULL, " +
                    "phone VARCHAR(20), " +
                    "role VARCHAR(20) NOT NULL DEFAULT 'STUDENT', " +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");

            stmt.execute("CREATE TABLE IF NOT EXISTS admins (" +
                    "admin_id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "user_id INT NOT NULL UNIQUE, " +
                    "department VARCHAR(100) NOT NULL DEFAULT 'Library Administration', " +
                    "employee_code VARCHAR(50) NOT NULL UNIQUE, " +
                    "access_level VARCHAR(20) NOT NULL DEFAULT 'SUPER_ADMIN', " +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                    "CONSTRAINT fk_admin_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE)");

            stmt.execute("CREATE TABLE IF NOT EXISTS books (" +
                    "book_id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "isbn VARCHAR(20) NOT NULL UNIQUE, " +
                    "title VARCHAR(200) NOT NULL, " +
                    "author VARCHAR(150) NOT NULL, " +
                    "category VARCHAR(50) NOT NULL, " +
                    "publisher VARCHAR(100), " +
                    "publication_year INT, " +
                    "total_copies INT NOT NULL DEFAULT 1, " +
                    "available_copies INT NOT NULL DEFAULT 1, " +
                    "location_shelf VARCHAR(50), " +
                    "cover_image_url VARCHAR(255), " +
                    "description TEXT, " +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");

            stmt.execute("CREATE TABLE IF NOT EXISTS borrowings (" +
                    "borrow_id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "user_id INT NOT NULL, " +
                    "book_id INT NOT NULL, " +
                    "borrow_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                    "due_date TIMESTAMP NOT NULL, " +
                    "return_date TIMESTAMP NULL, " +
                    "status VARCHAR(20) NOT NULL DEFAULT 'BORROWED', " +
                    "fine_amount DECIMAL(6,2) NOT NULL DEFAULT 0.00, " +
                    "notes VARCHAR(255), " +
                    "CONSTRAINT fk_borrow_user FOREIGN KEY (user_id) REFERENCES users(user_id), " +
                    "CONSTRAINT fk_borrow_book FOREIGN KEY (book_id) REFERENCES books(book_id))");
        }
    }

    private static void insertSeedData(Connection conn) {
        try (Statement stmt = conn.createStatement()) {
            // Check if admin user exists
            boolean hasAdmin = false;
            try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM users WHERE username = 'admin'")) {
                if (rs.next() && rs.getInt(1) > 0) {
                    hasAdmin = true;
                }
            } catch (Exception ignored) {}

            if (!hasAdmin) {
                stmt.executeUpdate("INSERT INTO users (user_id, username, email, password_hash, full_name, phone, role) VALUES " +
                        "(1, 'admin', 'admin@library.edu', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewdBPj/gCclq6t0e', 'System Administrator', '+1-555-0199', 'ADMIN')");
                stmt.executeUpdate("INSERT INTO users (user_id, username, email, password_hash, full_name, phone, role) VALUES " +
                        "(2, 'john_doe', 'john.doe@university.edu', '$2a$12$U1O0uE.Z8Ea6I2sV7X/Hpef0zW3eZ2K7w4e7f2V0n1l3q5r7s9u2a', 'John Doe', '+1-555-0101', 'STUDENT')");
                stmt.executeUpdate("INSERT INTO admins (admin_id, user_id, department, employee_code, access_level) VALUES " +
                        "(1, 1, 'Department of Library Services', 'LIB-EMP-001', 'SUPER_ADMIN')");
            }

            // Check if books exist
            int bookCount = 0;
            try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM books")) {
                if (rs.next()) {
                    bookCount = rs.getInt(1);
                }
            } catch (Exception ignored) {}

            if (bookCount == 0) {
                stmt.executeUpdate("INSERT INTO books (book_id, isbn, title, author, category, publisher, publication_year, total_copies, available_copies, location_shelf, description) VALUES " +
                        "(1, '978-0132350884', 'Clean Code: A Handbook of Agile Software Craftsmanship', 'Robert C. Martin', 'Computer Science', 'Prentice Hall', 2008, 5, 4, 'CS-A1-01', 'A handbook of agile software craftsmanship and best programming practices.')");
                stmt.executeUpdate("INSERT INTO books (book_id, isbn, title, author, category, publisher, publication_year, total_copies, available_copies, location_shelf, description) VALUES " +
                        "(2, '978-0201633610', 'Design Patterns: Elements of Reusable Object-Oriented Software', 'Erich Gamma et al.', 'Computer Science', 'Addison-Wesley', 1994, 4, 3, 'CS-A1-02', 'Classic catalog of software design patterns.')");
                stmt.executeUpdate("INSERT INTO books (book_id, isbn, title, author, category, publisher, publication_year, total_copies, available_copies, location_shelf, description) VALUES " +
                        "(3, '978-0134685991', 'Effective Java (3rd Edition)', 'Joshua Bloch', 'Computer Science', 'Addison-Wesley', 2018, 6, 6, 'CS-A1-03', 'Definitive guide to writing robust, maintainable Java code.')");
                stmt.executeUpdate("INSERT INTO books (book_id, isbn, title, author, category, publisher, publication_year, total_copies, available_copies, location_shelf, description) VALUES " +
                        "(4, '978-0262033848', 'Introduction to Algorithms (4th Edition)', 'Thomas H. Cormen et al.', 'Computer Science', 'MIT Press', 2022, 5, 5, 'CS-A2-01', 'Comprehensive textbook on algorithms and data structures.')");
                stmt.executeUpdate("INSERT INTO books (book_id, isbn, title, author, category, publisher, publication_year, total_copies, available_copies, location_shelf, description) VALUES " +
                        "(5, '978-0596007126', 'Head First Design Patterns', 'Eric Freeman, Elisabeth Robson', 'Computer Science', 'O''Reilly Media', 2004, 4, 4, 'CS-A2-02', 'Brain-friendly guide to object-oriented patterns.')");
                stmt.executeUpdate("INSERT INTO books (book_id, isbn, title, author, category, publisher, publication_year, total_copies, available_copies, location_shelf, description) VALUES " +
                        "(6, '978-1449373320', 'Designing Data-Intensive Applications', 'Martin Kleppmann', 'Database & Systems', 'O''Reilly Media', 2017, 3, 3, 'DB-B1-01', 'Key principles behind reliable, scalable data architectures.')");
                stmt.executeUpdate("INSERT INTO books (book_id, isbn, title, author, category, publisher, publication_year, total_copies, available_copies, location_shelf, description) VALUES " +
                        "(7, '978-0131103627', 'The C Programming Language (2nd Edition)', 'Brian Kernighan, Dennis Ritchie', 'Computer Science', 'Prentice Hall', 1988, 3, 3, 'CS-A3-01', 'Authoritative guide to the C programming language.')");
                stmt.executeUpdate("INSERT INTO books (book_id, isbn, title, author, category, publisher, publication_year, total_copies, available_copies, location_shelf, description) VALUES " +
                        "(8, '978-0262035613', 'Deep Learning', 'Ian Goodfellow, Yoshua Bengio', 'Artificial Intelligence', 'MIT Press', 2016, 4, 4, 'AI-C1-01', 'Foundational textbook on neural networks and deep learning.')");
                stmt.executeUpdate("INSERT INTO books (book_id, isbn, title, author, category, publisher, publication_year, total_copies, available_copies, location_shelf, description) VALUES " +
                        "(9, '978-0062316097', 'Sapiens: A Brief History of Humankind', 'Yuval Noah Harari', 'History & Philosophy', 'Harper', 2015, 6, 6, 'GEN-D1-01', 'Exploration of human history, evolution, and society.')");
                stmt.executeUpdate("INSERT INTO books (book_id, isbn, title, author, category, publisher, publication_year, total_copies, available_copies, location_shelf, description) VALUES " +
                        "(10, '978-0451524935', '1984', 'George Orwell', 'Classic Literature', 'Signet Classic', 1949, 8, 8, 'LIT-E1-01', 'Classic cautionary dystopian novel on totalitarian surveillance.')");
                LOGGER.info("10 curated textbooks seeded into books table.");
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error seeding data: " + e.getMessage(), e);
        }
    }
}
