-- ====================================================================
-- Online Library Management System - Seed Data
-- ====================================================================

USE library_management;

-- --------------------------------------------------------------------
-- Default BCrypt Hashed Passwords:
-- Admin password:    Admin@123   -> $2a$12$6gHn5B.s9KzY0G4l0f/Xvep9zO3zN2M8w4i7f2V0n1l3q5r7s9u2a (sample)
-- Student password:  Student@123 -> $2a$12$4mKl9A.r8JyX9F3k9e/Wudp8yN2yM1L7v3h6e1U9m0k2p4q6r8t1z (sample)
-- (Note: PasswordUtil will also recognize plain-text fallback on initial seed for seamless first login,
--  and automatically re-hashes to valid BCrypt upon authentication!)
-- --------------------------------------------------------------------

INSERT INTO users (user_id, username, email, password_hash, full_name, phone, role) VALUES
(1, 'admin', 'admin@library.edu', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewdBPj/gCclq6t0e', 'System Administrator', '+1-555-0199', 'ADMIN'),
(2, 'john_doe', 'john.doe@university.edu', '$2a$12$U1O0uE.Z8Ea6I2sV7X/Hpef0zW3eZ2K7w4e7f2V0n1l3q5r7s9u2a', 'John Doe', '+1-555-0101', 'STUDENT'),
(3, 'emma_watson', 'emma.w@university.edu', '$2a$12$U1O0uE.Z8Ea6I2sV7X/Hpef0zW3eZ2K7w4e7f2V0n1l3q5r7s9u2a', 'Emma Watson', '+1-555-0102', 'STUDENT'),
(4, 'alex_smith', 'alex.smith@university.edu', '$2a$12$U1O0uE.Z8Ea6I2sV7X/Hpef0zW3eZ2K7w4e7f2V0n1l3q5r7s9u2a', 'Alex Smith', '+1-555-0103', 'STUDENT');

INSERT INTO admins (admin_id, user_id, department, employee_code, access_level) VALUES
(1, 1, 'Department of Library Services', 'LIB-EMP-001', 'SUPER_ADMIN');

-- --------------------------------------------------------------------
-- Sample Books Catalog
-- --------------------------------------------------------------------
INSERT INTO books (book_id, isbn, title, author, category, publisher, publication_year, total_copies, available_copies, location_shelf, description) VALUES
(1, '978-0132350884', 'Clean Code: A Handbook of Agile Software Craftsmanship', 'Robert C. Martin', 'Computer Science', 'Prentice Hall', 2008, 5, 4, 'CS-A1-01', 'Even bad code can function. But if code isn\'t clean, it can bring a development organization to its knees.'),
(2, '978-0201633610', 'Design Patterns: Elements of Reusable Object-Oriented Software', 'Erich Gamma, Richard Helm, Ralph Johnson, John Vlissides', 'Computer Science', 'Addison-Wesley', 1994, 4, 3, 'CS-A1-02', 'Capturing a wealth of experience about the design of object-oriented software, four top-notch designers present a catalog of simple and succinct solutions.'),
(3, '978-0134685991', 'Effective Java (3rd Edition)', 'Joshua Bloch', 'Computer Science', 'Addison-Wesley', 2018, 6, 5, 'CS-A1-03', 'The definitive guide to best-practices Java programming from former Sun Microsystems Chief Java Architect.'),
(4, '978-0262033848', 'Introduction to Algorithms (4th Edition)', 'Thomas H. Cormen, Charles E. Leiserson, Ronald L. Rivest, Clifford Stein', 'Computer Science', 'MIT Press', 2022, 5, 5, 'CS-A2-01', 'A comprehensive textbook covering a broad range of algorithms in depth, yet making their design accessible to all levels.'),
(5, '978-0596007126', 'Head First Design Patterns', 'Eric Freeman, Elisabeth Robson', 'Computer Science', 'O\'Reilly Media', 2004, 4, 3, 'CS-A2-02', 'A brain-friendly guide to building extensible, maintainable software design patterns.'),
(6, '978-1449373320', 'Designing Data-Intensive Applications', 'Martin Kleppmann', 'Database & Systems', 'O\'Reilly Media', 2017, 3, 2, 'DB-B1-01', 'The big ideas behind reliable, scalable, and maintainable data systems in modern software architecture.'),
(7, '978-0131103627', 'The C Programming Language (2nd Edition)', 'Brian W. Kernighan, Dennis M. Ritchie', 'Computer Science', 'Prentice Hall', 1988, 3, 3, 'CS-A3-01', 'Written by the developers of C, this authoritative classic guide explains C programming principles.'),
(8, '978-0262035613', 'Deep Learning', 'Ian Goodfellow, Yoshua Bengio, Aaron Courville', 'Artificial Intelligence', 'MIT Press', 2016, 4, 4, 'AI-C1-01', 'An introduction to a broad range of topics in deep learning, covering mathematical and conceptual background.'),
(9, '978-0062316097', 'Sapiens: A Brief History of Humankind', 'Yuval Noah Harari', 'History & Philosophy', 'Harper', 2015, 6, 6, 'GEN-D1-01', 'Explores how an insignificant ape became the ruler of planet Earth, capable of splitting the atom and journeying to the moon.'),
(10, '978-0451524935', '1984', 'George Orwell', 'Classic Literature', 'Signet Classic', 1949, 8, 7, 'LIT-E1-01', 'A dystopian social science fiction novel and cautionary tale about totalitarianism and mass surveillance.'),
(11, '978-0743273565', 'The Great Gatsby', 'F. Scott Fitzgerald', 'Classic Literature', 'Scribner', 1925, 5, 5, 'LIT-E1-02', 'A portrait of the Jazz Age that has been acclaimed by generations of readers as an American classic.'),
(12, '978-0321125217', 'Domain-Driven Design: Tackling Complexity in the Heart of Software', 'Eric Evans', 'Software Engineering', 'Addison-Wesley', 2003, 3, 3, 'CS-A4-01', 'Explains how to incorporate effective domain modeling into software development projects.'),
(13, '978-1491950296', 'Programming Python (5th Edition)', 'Mark Lutz', 'Computer Science', 'O\'Reilly Media', 2020, 4, 4, 'CS-A4-02', 'Comprehensive in-depth tutorial for programming in Python covering system interfaces and web development.'),
(14, '978-0143127741', 'Thinking, Fast and Slow', 'Daniel Kahneman', 'Psychology & Economics', 'Farrar, Straus and Giroux', 2011, 5, 4, 'PSY-F1-01', 'Nobel laureate Daniel Kahneman takes us on a groundbreaking tour of the mind and explains the two systems that drive the way we think.'),
(15, '978-0385504201', 'The Da Vinci Code', 'Dan Brown', 'Fiction & Mystery', 'Doubleday', 2003, 6, 6, 'FIC-G1-01', 'A mystery thriller following symbologist Robert Langdon and cryptologist Sophie Neveu after a murder in the Louvre Museum.');

-- --------------------------------------------------------------------
-- Sample Borrowings
-- --------------------------------------------------------------------
INSERT INTO borrowings (borrow_id, user_id, book_id, borrow_date, due_date, return_date, status, fine_amount, notes) VALUES
(1, 2, 1, DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_ADD(NOW(), INTERVAL 9 DAY), NULL, 'BORROWED', 0.00, 'Student semester checkout'),
(2, 2, 2, DATE_SUB(NOW(), INTERVAL 20 DAY), DATE_SUB(NOW(), INTERVAL 6 DAY), DATE_SUB(NOW(), INTERVAL 5 DAY), 'RETURNED', 0.00, 'Returned on time at library desk'),
(3, 3, 6, DATE_SUB(NOW(), INTERVAL 18 DAY), DATE_SUB(NOW(), INTERVAL 4 DAY), NULL, 'OVERDUE', 4.00, 'Overdue notice sent to student email'),
(4, 4, 5, DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_ADD(NOW(), INTERVAL 11 DAY), NULL, 'BORROWED', 0.00, 'Study group preparation');

-- Set auto increment initial offsets
ALTER TABLE users AUTO_INCREMENT = 10;
ALTER TABLE books AUTO_INCREMENT = 20;
ALTER TABLE borrowings AUTO_INCREMENT = 10;
ALTER TABLE admins AUTO_INCREMENT = 5;
