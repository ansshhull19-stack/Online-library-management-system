<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="pageTitle" value="Registered Students - Admin - LibraryHub" />
<c:set var="pageName" value="admin_users" />
<jsp:include page="../common/header.jsp" />

<div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 2rem;">
    <div>
        <h1 style="font-size: 2rem; font-weight: 800; color: #0f172a; margin-bottom: 0.25rem;">Student Directory</h1>
        <p style="color: #64748b;">All registered student library member accounts (${studentCount} enrolled)</p>
    </div>
</div>

<div class="table-responsive">
    <table class="custom-table">
        <thead>
            <tr>
                <th>Student ID</th>
                <th>Full Name</th>
                <th>Username</th>
                <th>Email Address</th>
                <th>Phone</th>
                <th>Role</th>
                <th>Enrolled Date</th>
            </tr>
        </thead>
        <tbody>
            <c:choose>
                <c:when test="${not empty students}">
                    <c:forEach var="s" items="${students}">
                        <tr>
                            <td>#${s.userId}</td>
                            <td><strong>${s.fullName}</strong></td>
                            <td><code>${s.username}</code></td>
                            <td>${s.email}</td>
                            <td>${not empty s.phone ? s.phone : 'N/A'}</td>
                            <td><span class="badge badge-primary">${s.role}</span></td>
                            <td><fmt:formatDate value="${s.createdAt}" pattern="MMM dd, yyyy" /></td>
                        </tr>
                    </c:forEach>
                </c:when>
                <c:otherwise>
                    <tr>
                        <td colspan="7" style="text-align: center; padding: 2.5rem; color: #94a3b8;">
                            No students registered yet.
                        </td>
                    </tr>
                </c:otherwise>
            </c:choose>
        </tbody>
    </table>
</div>

<jsp:include page="../common/footer.jsp" />
