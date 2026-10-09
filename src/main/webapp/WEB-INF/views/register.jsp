<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Student Registration - LibraryHub" />
<c:set var="pageName" value="register" />
<jsp:include page="common/header.jsp" />

<div class="auth-wrapper">
    <div class="auth-card" style="max-width: 520px;">
        <div class="auth-header">
            <div class="brand-icon" style="margin: 0 auto 1rem; width: 48px; height: 48px; font-size: 1.5rem;">🎓</div>
            <h2>Create Student Account</h2>
            <p>Register to borrow textbooks and manage academic loans</p>
        </div>

        <form id="registerForm" method="POST" action="${pageContext.request.contextPath}/register">
            <div class="form-group">
                <label class="form-label" for="fullName">Full Name *</label>
                <input type="text" id="fullName" name="fullName" class="form-control"
                       placeholder="e.g. Alex Johnson" value="${fullName}" required />
            </div>

            <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 1rem;">
                <div class="form-group">
                    <label class="form-label" for="username">Username *</label>
                    <input type="text" id="username" name="username" class="form-control"
                           placeholder="e.g. alex_j" value="${username}" required />
                </div>
                <div class="form-group">
                    <label class="form-label" for="phone">Phone Number</label>
                    <input type="tel" id="phone" name="phone" class="form-control"
                           placeholder="e.g. +1-555-0123" value="${phone}" />
                </div>
            </div>

            <div class="form-group">
                <label class="form-label" for="email">Student Email *</label>
                <input type="email" id="email" name="email" class="form-control"
                       placeholder="e.g. alex.j@university.edu" value="${email}" required />
            </div>

            <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 1rem;">
                <div class="form-group">
                    <label class="form-label" for="password">Password *</label>
                    <input type="password" id="password" name="password" class="form-control"
                           placeholder="Min 6 characters" required />
                </div>
                <div class="form-group">
                    <label class="form-label" for="confirmPassword">Confirm Password *</label>
                    <input type="password" id="confirmPassword" name="confirmPassword" class="form-control"
                           placeholder="Repeat password" required />
                </div>
            </div>

            <button type="submit" class="btn btn-primary btn-block" style="padding: 0.75rem; margin-top: 1rem;">
                Complete Registration
            </button>
        </form>

        <div class="form-footer">
            Already have an active account?
            <a href="${pageContext.request.contextPath}/login" style="font-weight: 600;">Sign in here</a>
        </div>
    </div>
</div>

<jsp:include page="common/footer.jsp" />
