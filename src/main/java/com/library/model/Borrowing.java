package com.library.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

public class Borrowing implements Serializable {
    private static final long serialVersionUID = 1L;

    private int borrowId;
    private int userId;
    private int bookId;
    private Timestamp borrowDate;
    private Timestamp dueDate;
    private Timestamp returnDate;
    private String status; // 'BORROWED', 'RETURNED', 'OVERDUE'
    private BigDecimal fineAmount;
    private String notes;

    // Joined relationship properties for rich UI display
    private Book book;
    private User user;

    public Borrowing() {
        this.fineAmount = BigDecimal.ZERO;
        this.status = "BORROWED";
    }

    public Borrowing(int borrowId, int userId, int bookId, Timestamp borrowDate, Timestamp dueDate,
                     Timestamp returnDate, String status, BigDecimal fineAmount, String notes) {
        this.borrowId = borrowId;
        this.userId = userId;
        this.bookId = bookId;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
        this.status = status;
        this.fineAmount = fineAmount != null ? fineAmount : BigDecimal.ZERO;
        this.notes = notes;
    }

    public int getBorrowId() {
        return borrowId;
    }

    public void setBorrowId(int borrowId) {
        this.borrowId = borrowId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public Timestamp getBorrowDate() {
        return borrowDate;
    }

    public void setBorrowDate(Timestamp borrowDate) {
        this.borrowDate = borrowDate;
    }

    public Timestamp getDueDate() {
        return dueDate;
    }

    public void setDueDate(Timestamp dueDate) {
        this.dueDate = dueDate;
    }

    public Timestamp getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(Timestamp returnDate) {
        this.returnDate = returnDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getFineAmount() {
        return fineAmount;
    }

    public void setFineAmount(BigDecimal fineAmount) {
        this.fineAmount = fineAmount;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean isReturned() {
        return "RETURNED".equalsIgnoreCase(this.status);
    }

    public boolean isOverdue() {
        if ("RETURNED".equalsIgnoreCase(this.status)) {
            return false;
        }
        return "OVERDUE".equalsIgnoreCase(this.status) ||
                (dueDate != null && dueDate.before(new Timestamp(System.currentTimeMillis())));
    }
}
