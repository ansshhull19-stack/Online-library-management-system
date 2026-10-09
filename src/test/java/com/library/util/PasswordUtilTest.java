package com.library.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PasswordUtilTest {

    @Test
    @DisplayName("hashPassword should produce valid 60-character BCrypt hash")
    public void testHashPasswordFormat() {
        String password = "StrongPassword@123";
        String hash = PasswordUtil.hashPassword(password);

        assertNotNull(hash, "Hash should not be null");
        assertEquals(60, hash.length(), "BCrypt hash length must be exactly 60 characters");
        assertTrue(hash.startsWith("$2a$"), "Hash should start with BCrypt version prefix $2a$");
    }

    @Test
    @DisplayName("checkPassword should return true for matching password and false for wrong password")
    public void testCheckPasswordVerification() {
        String password = "SecretLibraryKey#99";
        String hash = PasswordUtil.hashPassword(password);

        assertTrue(PasswordUtil.checkPassword(password, hash), "Password verification must succeed for correct password");
        assertFalse(PasswordUtil.checkPassword("WrongPassword#99", hash), "Password verification must fail for wrong password");
        assertFalse(PasswordUtil.checkPassword("", hash), "Empty password candidate must fail verification");
        assertFalse(PasswordUtil.checkPassword(null, hash), "Null candidate must fail verification");
    }

    @Test
    @DisplayName("validatePasswordStrength should reject short passwords and accept strong passwords")
    public void testPasswordStrengthValidation() {
        assertNotNull(PasswordUtil.validatePasswordStrength("12345"), "5-character password should be rejected");
        assertNotNull(PasswordUtil.validatePasswordStrength(null), "Null password should be rejected");
        assertNull(PasswordUtil.validatePasswordStrength("Secure@123"), "Valid password should pass with null error");
    }
}
