<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="pageTitle" value="Circulation Transactions - Admin - LibraryHub" />
<c:set var="pageName" value="admin_borrowings" />
<jsp:include page="../common/header.jsp" />

<div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 2rem;">
    <div>
        <h1 style="font-size: 2rem; font-weight: 800; color: #0f172a; margin-bottom: 0.25rem;">Circulation &amp; Borrowings Log</h1>
        <p style="color: #64748b;">Complete audit ledger of student checkout transactions and returns</p>
    </div>
</div>

<!-- Status Filter Tabs -->
<div style="display: flex; gap: 0.5rem; margin-bottom: 1.5rem; flex-wrap: wrap;">
    <a href="${pageContext.request.contextPath}/admin/borrowings?status=ALL"
       class="btn btn-sm ${selectedStatus == 'ALL' ? 'btn-primary' : 'btn-secondary'}">
        All Transactions (${borrowings.size()})
    </a>
    <a href="${pageContext.request.contextPath}/admin/borrowings?status=BORROWED"
       class="btn btn-sm ${selectedStatus == 'BORROWED' ? 'btn-primary' : 'btn-secondary'}">
        Active Loans (${activeLoansCount})
    </a>
    <a href="${pageContext.request.contextPath}/admin/borrowings?status=OVERDUE"
       class="btn btn-sm ${selectedStatus == 'OVERDUE' ? 'btn-primary' : 'btn-secondary'}">
        Overdue Loans (${overdueLoansCount})
    </a>
    <a href="${pageContext.request.contextPath}/admin/borrowings?status=RETURNED"
       class="btn btn-sm ${selectedStatus == 'RETURNED' ? 'btn-primary' : 'btn-secondary'}">
        Returned Records
    </a>
</div>

<!-- Transactions Table -->
<div class="table-responsive">
    <table class="custom-table">
        <thead>
            <tr>
                <th>Loan ID</th>
                <th>Student</th>
                <th>Book Title</th>
                <th>Borrow Date</th>
                <th>Due Date</th>
                <th>Returned On</th>
                <th>Status</th>
                <th>Fine</th>
                <th>Action</th>
            </tr>
        </thead>
        <tbody>
            <c:choose>
                <c:when test="${not empty borrowings}">
                    <c:forEach var="item" items="${borrowings}">
                        <tr>
                            <td>#${item.borrowId}</td>
                            <td>
                                <strong>${item.user.fullName}</strong>
                                <div style="font-size: 0.75rem; color: #64748b;">${item.user.email}</div>
                                <div style="font-size: 0.7rem; color: #94a3b8;">User: ${item.user.username}</div>
                            </td>
                            <td>
                                <strong>${item.book.title}</strong>
                                <div style="font-size: 0.75rem; color: #94a3b8; font-family: monospace;">ISBN: ${item.book.isbn}</div>
                            </td>
                            <td><fmt:formatDate value="${item.borrowDate}" pattern="MMM dd, yyyy" /></td>
                            <td>
                                <strong><fmt:formatDate value="${item.dueDate}" pattern="MMM dd, yyyy" /></strong>
                            </td>
                            <td>
                                <c:choose>
                                    <c:when test="${item.returnDate != null}">
                                        <fmt:formatDate value="${item.returnDate}" pattern="MMM dd, yyyy" />
                                    </c:when>
                                    <c:otherwise>
                                        <span style="color: #94a3b8;">&mdash;</span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td>
                                <c:choose>
                                    <c:when test="${item.returned}">
                                        <span class="badge badge-success">RETURNED</span>
                                    </c:when>
                                    <c:when test="${item.overdue}">
                                        <span class="badge badge-danger">OVERDUE</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="badge badge-primary">ACTIVE</span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td>$<fmt:formatNumber value="${item.fineAmount}" minFractionDigits="2" maxFractionDigits="2"/></td>
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
                        <td colspan="9" style="text-align: center; padding: 2.5rem; color: #94a3b8;">
                            No records found for the selected status.
                        </td>
                    </tr>
                </c:otherwise>
            </c:choose>
        </tbody>
    </table>
</div>

<jsp:include page="../common/footer.jsp" />
