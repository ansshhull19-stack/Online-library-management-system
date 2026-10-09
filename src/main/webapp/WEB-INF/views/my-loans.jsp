<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="pageTitle" value="My Loans - LibraryHub" />
<c:set var="pageName" value="loans" />
<jsp:include page="common/header.jsp" />

<div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 2rem;">
    <div>
        <h1 style="font-size: 2rem; font-weight: 800; color: #0f172a; margin-bottom: 0.25rem;">My Book Loans</h1>
        <p style="color: #64748b;">Track your current borrowed textbooks and view return history</p>
    </div>
    <a href="${pageContext.request.contextPath}/catalog" class="btn btn-primary btn-sm">Borrow Another Book</a>
</div>

<!-- Loans Summary Cards -->
<section class="stats-grid" style="margin-bottom: 2rem;">
    <div class="stat-card">
        <div class="stat-icon stat-blue">📖</div>
        <div class="stat-info">
            <div class="stat-value">${activeLoanCount}</div>
            <div class="stat-label">Currently Borrowed Books</div>
        </div>
    </div>
    <div class="stat-card">
        <div class="stat-icon ${totalFines > 0 ? 'stat-red' : 'stat-green'}">💵</div>
        <div class="stat-info">
            <div class="stat-value">$<fmt:formatNumber value="${totalFines}" minFractionDigits="2" maxFractionDigits="2"/></div>
            <div class="stat-label">Total Overdue Fines</div>
        </div>
    </div>
</section>

<!-- Active Borrowings -->
<div style="margin-bottom: 3rem;">
    <h2 style="font-size: 1.35rem; font-weight: 700; color: #1e293b; margin-bottom: 1rem;">Active Loans</h2>

    <div class="table-responsive">
        <table class="custom-table">
            <thead>
                <tr>
                    <th>Book Title</th>
                    <th>Author</th>
                    <th>Category</th>
                    <th>Borrow Date</th>
                    <th>Due Date</th>
                    <th>Status</th>
                    <th>Action</th>
                </tr>
            </thead>
            <tbody>
                <c:choose>
                    <c:when test="${not empty activeLoans}">
                        <c:forEach var="loan" items="${activeLoans}">
                            <tr>
                                <td>
                                    <strong>${loan.book.title}</strong>
                                    <div style="font-size: 0.75rem; color: #94a3b8; font-family: monospace;">ISBN: ${loan.book.isbn}</div>
                                </td>
                                <td>${loan.book.author}</td>
                                <td><span class="badge badge-secondary">${loan.book.category}</span></td>
                                <td><fmt:formatDate value="${loan.borrowDate}" pattern="MMM dd, yyyy" /></td>
                                <td>
                                    <strong><fmt:formatDate value="${loan.dueDate}" pattern="MMM dd, yyyy" /></strong>
                                </td>
                                <td>
                                    <c:choose>
                                        <c:when test="${loan.overdue}">
                                            <span class="badge badge-danger">OVERDUE</span>
                                            <c:if test="${loan.fineAmount > 0}">
                                                <div style="font-size: 0.75rem; color: #ef4444; font-weight: 600; margin-top: 0.2rem;">
                                                    Fine: $<fmt:formatNumber value="${loan.fineAmount}" minFractionDigits="2"/>
                                                </div>
                                            </c:if>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="badge badge-primary">ACTIVE</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <form method="POST" action="${pageContext.request.contextPath}/return"
                                          onsubmit="return confirm('Confirm returning: ${loan.book.title}?');">
                                        <input type="hidden" name="borrowId" value="${loan.borrowId}" />
                                        <button type="submit" class="btn btn-success btn-sm">Return Book</button>
                                    </form>
                                </td>
                            </tr>
                        </c:forEach>
                    </c:when>
                    <c:otherwise>
                        <tr>
                            <td colspan="7" style="text-align: center; padding: 2.5rem; color: #94a3b8;">
                                You currently have no borrowed books. <a href="${pageContext.request.contextPath}/catalog">Browse catalog to borrow</a>.
                            </td>
                        </tr>
                    </c:otherwise>
                </c:choose>
            </tbody>
        </table>
    </div>
</div>

<!-- Loan History -->
<div>
    <h2 style="font-size: 1.35rem; font-weight: 700; color: #1e293b; margin-bottom: 1rem;">Loan History</h2>

    <div class="table-responsive">
        <table class="custom-table">
            <thead>
                <tr>
                    <th>Book Title</th>
                    <th>Author</th>
                    <th>Borrow Date</th>
                    <th>Due Date</th>
                    <th>Returned On</th>
                    <th>Status</th>
                    <th>Fine Paid</th>
                </tr>
            </thead>
            <tbody>
                <c:choose>
                    <c:when test="${not empty pastLoans}">
                        <c:forEach var="loan" items="${pastLoans}">
                            <tr>
                                <td>
                                    <strong>${loan.book.title}</strong>
                                    <div style="font-size: 0.75rem; color: #94a3b8; font-family: monospace;">ISBN: ${loan.book.isbn}</div>
                                </td>
                                <td>${loan.book.author}</td>
                                <td><fmt:formatDate value="${loan.borrowDate}" pattern="MMM dd, yyyy" /></td>
                                <td><fmt:formatDate value="${loan.dueDate}" pattern="MMM dd, yyyy" /></td>
                                <td><fmt:formatDate value="${loan.returnDate}" pattern="MMM dd, yyyy" /></td>
                                <td><span class="badge badge-success">RETURNED</span></td>
                                <td>$<fmt:formatNumber value="${loan.fineAmount}" minFractionDigits="2" maxFractionDigits="2"/></td>
                            </tr>
                        </c:forEach>
                    </c:when>
                    <c:otherwise>
                        <tr>
                            <td colspan="7" style="text-align: center; padding: 2.5rem; color: #94a3b8;">
                                No previous loan records found.
                            </td>
                        </tr>
                    </c:otherwise>
                </c:choose>
            </tbody>
        </table>
    </div>
</div>

<jsp:include page="common/footer.jsp" />
