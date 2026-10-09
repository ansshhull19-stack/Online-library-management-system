package com.library.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;

public class ModelValidationTest {

    @Test
    @DisplayName("Book availability should correctly reflect remaining copies")
    public void testBookAvailability() {
        Book book = new Book();
        book.setTotalCopies(5);
        book.setAvailableCopies(2);
        assertTrue(book.isAvailable(), "Book with available copies > 0 should be available");

        book.setAvailableCopies(0);
        assertFalse(book.isAvailable(), "Book with 0 available copies should not be available");
    }

    @Test
    @DisplayName("User roles should correctly identify admin vs student")
    public void testUserRoles() {
        User admin = new User();
        admin.setRole("ADMIN");
        assertTrue(admin.isAdmin());
        assertFalse(admin.isStudent());

        User student = new User();
        student.setRole("STUDENT");
        assertTrue(student.isStudent());
        assertFalse(student.isAdmin());
    }

    @Test
    @DisplayName("Borrowing overdue logic should check status and due date")
    public void testBorrowingOverdue() {
        Borrowing loan = new Borrowing();
        loan.setStatus("BORROWED");
        loan.setDueDate(Timestamp.from(Instant.now().minus(2, ChronoUnit.DAYS)));
        assertTrue(loan.isOverdue(), "Past due date must be marked overdue");

        loan.setStatus("RETURNED");
        assertFalse(loan.isOverdue(), "Returned book cannot be overdue");
    }
}
