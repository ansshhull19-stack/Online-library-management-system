package com.library.dao;

import com.library.model.User;
import com.library.util.PasswordUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class StudentRegistrationTest {

    private final UserDAO userDAO = new UserDAO();

    @Test
    @DisplayName("Verify end-to-end student registration, BCrypt hashing, and persistence")
    public void testStudentRegistrationAndLookup() {
        String uniqueUsername = "test_student_" + System.currentTimeMillis();
        String uniqueEmail = uniqueUsername + "@college.edu";
        String plainPassword = "StudentSecret@123";

        User student = new User();
        student.setFullName("Navin Singh");
        student.setUsername(uniqueUsername);
        student.setEmail(uniqueEmail);
        student.setPhone("+91-9876543210");
        student.setPasswordHash(PasswordUtil.hashPassword(plainPassword));
        student.setRole("STUDENT");

        // 1. Register student
        boolean registered = userDAO.register(student);
        assertTrue(registered, "Student registration must succeed");
        assertTrue(student.getUserId() > 0, "Registered user should receive generated ID");

        // 2. Lookup by username
        User found = userDAO.findByUsername(uniqueUsername);
        assertNotNull(found, "Registered student must be retrievable by username");
        assertEquals("Navin Singh", found.getFullName());
        assertEquals(uniqueEmail, found.getEmail());
        assertTrue(found.isStudent(), "User role should be STUDENT");

        // 3. Verify BCrypt authentication
        assertTrue(PasswordUtil.checkPassword(plainPassword, found.getPasswordHash()),
                "BCrypt password verification must succeed for student");
        assertFalse(PasswordUtil.checkPassword("WrongPassword", found.getPasswordHash()),
                "BCrypt verification must fail for wrong password");
    }
}
