package com.library.util;

import org.mindrot.jbcrypt.BCrypt;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Security utility for BCrypt password hashing and verification.
 * Employs salt rounds of 12 for strong cryptographic security.
 */
public class PasswordUtil {

    private static final Logger LOGGER = Logger.getLogger(PasswordUtil.class.getName());
    private static final int BCRYPT_LOG_ROUNDS = 12;

    /**
     * Hashes a plaintext password using BCrypt.
     *
     * @param plainPassword plaintext password
     * @return 60-character BCrypt hash string
     * @throws IllegalArgumentException if password is null or blank
     */
    public static String hashPassword(String plainPassword) {
        if (plainPassword == null || plainPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty.");
        }
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(BCRYPT_LOG_ROUNDS));
    }

    /**
     * Verifies whether a plaintext password matches a stored hash.
     * Supports standard BCrypt hashes ($2a$, $2b$, $2y$).
     *
     * @param plainPassword plaintext password candidate
     * @param hashedPassword stored hashed password
     * @return true if credentials match, false otherwise
     */
    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        if (plainPassword == null || hashedPassword == null || plainPassword.isEmpty() || hashedPassword.isEmpty()) {
            return false;
        }

        try {
            if (hashedPassword.startsWith("$2a$") || hashedPassword.startsWith("$2b$") || hashedPassword.startsWith("$2y$")) {
                return BCrypt.checkpw(plainPassword, hashedPassword);
            }
        } catch (IllegalArgumentException e) {
            LOGGER.log(Level.WARNING, "Failed to parse BCrypt hash format: " + e.getMessage());
        }

        return false;
    }

    /**
     * Validates that a proposed password meets basic security criteria.
     *
     * @param password password candidate
     * @return validation error message if invalid, or null if valid
     */
    public static String validatePasswordStrength(String password) {
        if (password == null || password.length() < 6) {
            return "Password must be at least 6 characters in length.";
        }
        if (password.length() > 64) {
            return "Password cannot exceed 64 characters.";
        }
        return null;
    }
}
