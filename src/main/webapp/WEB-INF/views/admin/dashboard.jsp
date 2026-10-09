<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="pageTitle" value="Admin Dashboard - LibraryHub" />
<c:set var="pageName" value="admin_dash" />
<jsp:include page="../common/header.jsp" />

<div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 2rem;">
    <div>
        <h1 style="font-size: 2rem; font-weight: 800; color: #0f172a; margin-bottom: 0.25rem;">Administrator Dashboard</h1>
        <p style="color: #64748b;">System overview, real-time inventory levels, and circulation operations</p>
    </div>
    <div style="display: flex; gap: 0.75rem;">
        <a href="${pageContext.request.contextPath}/admin/books" class="btn btn-primary btn-sm">+ Manage Books</a>
        <a href="${pageContext.request.contextPath}/admin/borrowings" class="btn btn-secondary btn-sm">All Transactions</a>
    </div>
</div>

<!-- Key Performance Indicators (KPIs) -->
<section class="stats-grid">
    <div class="stat-card">
        <div class="stat-icon stat-blue">📚</div>
        <div class="stat-info">
            <div class="stat-value">${totalTitles}</div>
            <div class="stat-label">Book Titles</div>
        </div>
    </div>
    <div class="stat-card">
        <div class="stat-icon stat-green">📦</div>
        <div class="stat-info">
            <div class="stat-value">${availableCopies} / ${totalCopies}</div>
            <div class="stat-label">Available / Total Copies</div>
        </div>
    </div>
    <div class="stat-card">
        <div class="stat-icon stat-yellow">🔄</div>
        <div class="stat-info">
            <div class="stat-value">${activeLoans}</div>
            <div class="stat-label">Active Student Loans</div>
        </div>
    </div>
    <div class="stat-card">
        <div class="stat-icon stat-red">⚠️</div>
        <div class="stat-info">
            <div class="stat-value">${overdueLoans}</div>
            <div class="stat-label">Overdue Loans</div>
        </div>
    </div>
    <div class="stat-card">
        <div class="stat-icon stat-blue">🎓</div>
        <div class="stat-info">
            <div class="stat-value">${totalStudents}</div>
            <div class="stat-label">Registered Students</div>
        </div>
    </div>
</section>

<!-- Recent Transactions Feed -->
<section style="margin-top: 2rem;">
    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 1rem;">
        <h2 style="font-size: 1.35rem; font-weight: 700; color: #1e293b;">Recent Circulation Transactions</h2>
        <a href="${pageContext.request.contextPath}/admin/borrowings" class="btn btn-secondary btn-sm">View Complete Log &rarr;</a>
    </div>

    <div class="table-responsive">
        <table class="custom-table">
            <thead>
                <tr>
                    <th>Loan ID</th>
                    <th>Student Name</th>
                    <th>Book Title</th>
                    <th>Borrow Date</th>
                    <th>Due Date</th>
                    <th>Status</th>
                    <th>Action</th>
                </tr>
            </thead>
            <tbody>
                <c:choose>
                    <c:when test="${not empty recentLoans}">
                        <c:forEach var="item" items="${recentLoans}">
                            <tr>
                                <td>#${item.borrowId}</td>
                                <td>
                                    <strong>${item.user.fullName}</strong>
                                    <div style="font-size: 0.75rem; color: #94a3b8;">${item.user.username}</div>
                                </td>
                                <td>${item.book.title}</td>
                                <td><fmt:formatDate value="${item.borrowDate}" pattern="MMM dd, yyyy" /></td>
                                <td><fmt:formatDate value="${item.dueDate}" pattern="MMM dd, yyyy" /></td>
                                <td>
                                    <c:choose>
                                        <c:when test="${item.returned}">
                                            <span class="badge badge-success">RETURNED</span>
                                        </c:when>
                                        <c:when test="${item.overdue}">
                                            <span class="badge badge-danger">OVERDUE</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="badge badge-primary">BORROWED</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <c:if test="${not item.returned}">
                                        <form method="POST" action="${pageContext.request.contextPath}/return" style="display:inline;">
                                            <input type="hidden" name="borrowId" value="${item.borrowId}" />
                                            <input type="hidden" name="source" value="admin" />
                                            <button type="submit" class="btn btn-success btn-sm" onclick="return confirm('Process return for this loan?');">Mark Returned</button>
                                        </form>
                                    </c:if>
                                </td>
                            </tr>
                        </c:forEach>
                    </c:when>
                    <c:otherwise>
                        <tr>
                            <td colspan="7" style="text-align:center; padding: 2.5rem; color: #94a3b8;">
                                No recent transactions recorded.
                            </td>
                        </tr>
                    </c:otherwise>
                </c:choose>
            </tbody>
        </table>
    </div>
</section>

<jsp:include page="../common/footer.jsp" />
