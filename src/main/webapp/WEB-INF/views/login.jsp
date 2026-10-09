<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Sign In - LibraryHub" />
<c:set var="pageName" value="login" />
<jsp:include page="common/header.jsp" />

<div class="auth-wrapper">
    <div class="auth-card">
        <div class="auth-header">
            <div class="brand-icon" style="margin: 0 auto 1rem; width: 48px; height: 48px; font-size: 1.5rem;">📚</div>
            <h2>Sign in to LibraryHub</h2>
            <p>Enter your library credentials to access your account</p>
        </div>

        <form method="POST" action="${pageContext.request.contextPath}/login">
            <c:if test="${not empty param.redirect}">
                <input type="hidden" name="redirect" value="${param.redirect}" />
            </c:if>

            <div class="form-group">
                <label class="form-label" for="username">Username or Email</label>
                <input type="text" id="username" name="username" class="form-control"
                       placeholder="e.g. john_doe or admin" value="${username}" required autofocus />
            </div>

            <div class="form-group">
                <label class="form-label" for="password">Password</label>
                <input type="password" id="password" name="password" class="form-control"
                       placeholder="Enter your password" required />
            </div>

            <button type="submit" class="btn btn-primary btn-block" style="padding: 0.75rem; margin-top: 1rem;">
                Sign In
            </button>
        </form>

        <!-- Quick Demo Credentials Box for College Evaluators & Students -->
        <div style="margin-top: 1.75rem; padding: 1rem; background: #f8fafc; border: 1px dashed #cbd5e1; border-radius: 8px; font-size: 0.85rem; color: #475569;">
            <strong style="display: block; margin-bottom: 0.5rem; color: #1e293b;">Demo Project Accounts:</strong>
            <div style="display: flex; justify-content: space-between; margin-bottom: 0.25rem;">
                <span>Administrator:</span>
                <code>admin</code> / <code>Admin@123</code>
            </div>
            <div style="display: flex; justify-content: space-between;">
                <span>Student:</span>
                <code>john_doe</code> / <code>Student@123</code>
            </div>
        </div>

        <div class="form-footer">
            Don't have a library account yet?
            <a href="${pageContext.request.contextPath}/register" style="font-weight: 600;">Register here</a>
        </div>
    </div>
</div>

<jsp:include page="common/footer.jsp" />
