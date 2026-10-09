package com.library.util;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class DateUtil {

    public static final int DEFAULT_LOAN_PERIOD_DAYS = 14;
    public static final BigDecimal DAILY_FINE_RATE = new BigDecimal("1.00");

    private static final String DATE_PATTERN = "MMM dd, yyyy";
    private static final String DATE_TIME_PATTERN = "MMM dd, yyyy HH:mm";

    /**
     * Computes the due date given a loan duration in days.
     */
    public static Timestamp calculateDueDate(int daysFromNow) {
        Instant due = Instant.now().plus(daysFromNow, ChronoUnit.DAYS);
        return Timestamp.from(due);
    }

    /**
     * Formats a SQL Timestamp to readable date.
     */
    public static String formatDate(Timestamp timestamp) {
        if (timestamp == null) return "N/A";
        return new SimpleDateFormat(DATE_PATTERN).format(timestamp);
    }

    /**
     * Formats a SQL Timestamp to readable date and time.
     */
    public static String formatDateTime(Timestamp timestamp) {
        if (timestamp == null) return "N/A";
        return new SimpleDateFormat(DATE_TIME_PATTERN).format(timestamp);
    }

    /**
     * Calculates the number of overdue days.
     */
    public static long calculateOverdueDays(Timestamp dueDate, Timestamp returnDate) {
        if (dueDate == null) return 0;
        long targetTime = (returnDate != null) ? returnDate.getTime() : System.currentTimeMillis();
        long diffMillis = targetTime - dueDate.getTime();
        if (diffMillis <= 0) return 0;
        return diffMillis / (1000 * 60 * 60 * 24);
    }

    /**
     * Computes fine amount based on overdue days.
     */
    public static BigDecimal calculateFine(long overdueDays) {
        if (overdueDays <= 0) {
            return BigDecimal.ZERO;
        }
        return DAILY_FINE_RATE.multiply(BigDecimal.valueOf(overdueDays));
    }
}
