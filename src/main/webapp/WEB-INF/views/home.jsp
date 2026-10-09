<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Home - LibraryHub" />
<c:set var="pageName" value="home" />
<jsp:include page="common/header.jsp" />

<section class="hero">
    <h1>Welcome to LibraryHub</h1>
    <p>Empowering academic learning with our digital catalog. Search thousands of textbooks, check real-time availability, and borrow books in seconds.</p>
    <div class="hero-actions">
        <a href="${pageContext.request.contextPath}/catalog" class="btn btn-primary btn-lg" style="background:#fff; color:#1e3a8a;">Explore Catalog</a>
        <c:if test="${empty sessionScope.loggedUser}">
            <a href="${pageContext.request.contextPath}/register" class="btn btn-secondary btn-lg" style="background:rgba(255,255,255,0.15); color:#fff; border-color:rgba(255,255,255,0.3);">Register as Student</a>
        </c:if>
    </div>
</section>

<!-- Library Inventory Overview Statistics -->
<section class="stats-grid">
    <div class="stat-card">
        <div class="stat-icon stat-blue">📖</div>
        <div class="stat-info">
            <div class="stat-value">${totalTitles}</div>
            <div class="stat-label">Book Titles</div>
        </div>
    </div>

    <div class="stat-card">
        <div class="stat-icon stat-green">📦</div>
        <div class="stat-info">
            <div class="stat-value">${availableCopies}</div>
            <div class="stat-label">Available Copies</div>
        </div>
    </div>

    <div class="stat-card">
        <div class="stat-icon stat-yellow">🔄</div>
        <div class="stat-info">
            <div class="stat-value">${activeLoans}</div>
            <div class="stat-label">Active Loans</div>
        </div>
    </div>

    <div class="stat-card">
        <div class="stat-icon stat-red">🎓</div>
        <div class="stat-info">
            <div class="stat-value">${registeredStudents}</div>
            <div class="stat-label">Registered Students</div>
        </div>
    </div>
</section>

<!-- Featured Books Section -->
<section style="margin-bottom: 3rem;">
    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 1.5rem;">
        <div>
            <h2 style="font-size: 1.5rem; font-weight: 800; color: #0f172a;">Featured Books</h2>
            <p style="color: #64748b; font-size: 0.95rem;">Popular computer science and academic textbooks in our collection</p>
        </div>
        <a href="${pageContext.request.contextPath}/catalog" class="btn btn-secondary btn-sm">View All &rarr;</a>
    </div>

    <div class="book-grid">
        <c:forEach var="book" items="${featuredBooks}">
            <div class="book-card">
                <div class="book-header">
                    <span class="category-badge">${book.category}</span>
                    <span class="isbn-code">ISBN: ${book.isbn}</span>
                </div>
                <div class="book-body">
                    <h3 class="book-title">${book.title}</h3>
                    <p class="book-author">by ${book.author}</p>
                    <p class="book-description">${book.description}</p>
                    <div class="book-meta">
                        <span>Shelf: <strong>${book.locationShelf != null ? book.locationShelf : 'General'}</strong></span>
                        <c:choose>
                            <c:when test="${book.availableCopies > 0}">
                                <span class="badge badge-success">${book.availableCopies} available</span>
                            </c:when>
                            <c:otherwise>
                                <span class="badge badge-danger">Out of stock</span>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
                <div class="book-footer">
                    <c:choose>
                        <c:when test="${not empty sessionScope.loggedUser}">
                            <c:choose>
                                <c:when test="${book.availableCopies > 0}">
                                    <button type="button" class="btn btn-primary btn-sm btn-block"
                                            onclick="showBorrowModal(${book.bookId}, '${book.title}', '${book.author}', ${book.availableCopies})">
                                        Borrow Book
                                    </button>
                                </c:when>
                                <c:otherwise>
                                    <button class="btn btn-secondary btn-sm btn-block" disabled>Unavailable</button>
                                </c:otherwise>
                            </c:choose>
                        </c:when>
                        <c:otherwise>
                            <a href="${pageContext.request.contextPath}/login?redirect=/catalog" class="btn btn-secondary btn-sm btn-block">Sign in to Borrow</a>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </c:forEach>
    </div>
</section>

<!-- Borrow Confirmation Modal -->
<div id="borrowModal" class="modal-overlay">
    <div class="modal-content">
        <div class="modal-header">
            <h3>Confirm Book Loan</h3>
            <button class="modal-close" onclick="closeModal('borrowModal')">&times;</button>
        </div>
        <form method="POST" action="${pageContext.request.contextPath}/borrow">
            <div class="modal-body">
                <input type="hidden" id="borrowBookId" name="bookId" value="" />
                <h4 id="borrowModalTitle" style="font-size: 1.2rem; color: #1e293b; margin-bottom: 0.25rem;"></h4>
                <p id="borrowModalAuthor" style="color: #64748b; font-size: 0.95rem; margin-bottom: 1rem;"></p>
                <div id="borrowModalStock" class="badge badge-success" style="margin-bottom: 1.25rem;"></div>

                <div style="background: #eff6ff; border: 1px solid #bfdbfe; border-radius: 8px; padding: 1rem; margin-bottom: 1rem; font-size: 0.9rem; color: #1e40af;">
                    <strong>Loan Policy Terms:</strong>
                    <ul style="margin-left: 1.25rem; margin-top: 0.5rem; line-height: 1.5;">
                        <li>Standard student loan duration is <strong>14 days</strong> from checkout date.</li>
                        <li>Books must be returned or renewed prior to the due date.</li>
                        <li>Overdue books incur a fee of $1.00 per day.</li>
                    </ul>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary btn-sm" onclick="closeModal('borrowModal')">Cancel</button>
                <button type="submit" class="btn btn-primary btn-sm">Confirm &amp; Checkout</button>
            </div>
        </form>
    </div>
</div>

<jsp:include page="common/footer.jsp" />
