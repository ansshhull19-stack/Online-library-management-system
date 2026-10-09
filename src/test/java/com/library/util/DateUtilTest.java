package com.library.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;

public class DateUtilTest {

    @Test
    @DisplayName("calculateDueDate should return timestamp approximately 14 days in future")
    public void testCalculateDueDate() {
        Timestamp due = DateUtil.calculateDueDate(14);
        assertNotNull(due);

        long diffDays = (due.getTime() - System.currentTimeMillis()) / (1000 * 60 * 60 * 24);
        assertEquals(14, diffDays, "Difference should be 14 days");
    }

    @Test
    @DisplayName("calculateOverdueDays should return 0 for unexpired loan and positive for overdue loan")
    public void testCalculateOverdueDays() {
        Timestamp futureDue = Timestamp.from(Instant.now().plus(5, ChronoUnit.DAYS));
        assertEquals(0, DateUtil.calculateOverdueDays(futureDue, null), "Future due date should have 0 overdue days");

        Timestamp pastDue = Timestamp.from(Instant.now().minus(5, ChronoUnit.DAYS));
        long overdueDays = DateUtil.calculateOverdueDays(pastDue, null);
        assertTrue(overdueDays >= 4 && overdueDays <= 6, "Overdue days should reflect roughly 5 days past due");
    }

    @Test
    @DisplayName("calculateFine should calculate correct penalty based on daily rate")
    public void testCalculateFine() {
        assertEquals(BigDecimal.ZERO, DateUtil.calculateFine(0), "No fine for 0 overdue days");
        assertEquals(new BigDecimal("5.00"), DateUtil.calculateFine(5), "5 overdue days should equal $5.00");
    }
}
