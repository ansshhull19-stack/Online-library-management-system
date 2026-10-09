<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${pageTitle != null ? pageTitle : 'LibraryHub - Online Library Management System'}</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
    <script>
        window.contextPath = "${pageContext.request.contextPath}";
        window.isLoggedIn = ${sessionScope.loggedUser != null ? 'true' : 'false'};
    </script>
</head>
<body>
    <header class="navbar">
        <div class="nav-container">
            <a href="${pageContext.request.contextPath}/home" class="brand">
                <div class="brand-icon">📚</div>
                <span>LibraryHub</span>
            </a>

            <nav>
                <ul class="nav-menu">
                    <li>
                        <a href="${pageContext.request.contextPath}/home"
                           class="nav-link ${pageName == 'home' ? 'active' : ''}">Home</a>
                    </li>
                    <li>
                        <a href="${pageContext.request.contextPath}/catalog"
                           class="nav-link ${pageName == 'catalog' ? 'active' : ''}">Book Catalog</a>
                    </li>

                    <c:if test="${not empty sessionScope.loggedUser}">
                        <c:if test="${sessionScope.loggedUser.student}">
                            <li>
                                <a href="${pageContext.request.contextPath}/loans"
                                   class="nav-link ${pageName == 'loans' ? 'active' : ''}">My Loans</a>
                            </li>
                        </c:if>
                        <c:if test="${sessionScope.loggedUser.admin}">
                            <li>
                                <a href="${pageContext.request.contextPath}/admin/dashboard"
                                   class="nav-link ${pageName == 'admin_dash' ? 'active' : ''}">Dashboard</a>
                            </li>
                            <li>
                                <a href="${pageContext.request.contextPath}/admin/books"
                                   class="nav-link ${pageName == 'admin_books' ? 'active' : ''}">Manage Books</a>
                            </li>
                            <li>
                                <a href="${pageContext.request.contextPath}/admin/borrowings"
                                   class="nav-link ${pageName == 'admin_borrowings' ? 'active' : ''}">Transactions</a>
                            </li>
                            <li>
                                <a href="${pageContext.request.contextPath}/admin/users"
                                   class="nav-link ${pageName == 'admin_users' ? 'active' : ''}">Students</a>
                            </li>
                        </c:if>
                    </c:if>
                </ul>
            </nav>

            <div class="nav-actions">
                <c:choose>
                    <c:when test="${not empty sessionScope.loggedUser}">
                        <div class="user-badge">
                            <span>👤 ${sessionScope.loggedUser.fullName}</span>
                            <span class="role-pill">${sessionScope.loggedUser.role}</span>
                        </div>
                        <a href="${pageContext.request.contextPath}/logout" class="btn btn-secondary btn-sm">Logout</a>
                    </c:when>
                    <c:otherwise>
                        <a href="${pageContext.request.contextPath}/login" class="btn btn-secondary btn-sm">Sign In</a>
                        <a href="${pageContext.request.contextPath}/register" class="btn btn-primary btn-sm">Register</a>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </header>

    <main class="main-content">
        <!-- Global Notification Alerts -->
        <c:if test="${not empty param.msg}">
            <div class="alert ${param.error != null ? 'alert-danger' : 'alert-success'}">
                <span>${param.msg}</span>
                <button type="button" class="btn-sm" style="background:none; border:none; cursor:pointer;" onclick="this.parentElement.remove()">✕</button>
            </div>
        </c:if>
        <c:if test="${not empty errorMessage}">
            <div class="alert alert-danger">
                <span>${errorMessage}</span>
                <button type="button" class="btn-sm" style="background:none; border:none; cursor:pointer;" onclick="this.parentElement.remove()">✕</button>
            </div>
        </c:if>
        <c:if test="${not empty successMessage}">
            <div class="alert alert-success">
                <span>${successMessage}</span>
                <button type="button" class="btn-sm" style="background:none; border:none; cursor:pointer;" onclick="this.parentElement.remove()">✕</button>
            </div>
        </c:if>
