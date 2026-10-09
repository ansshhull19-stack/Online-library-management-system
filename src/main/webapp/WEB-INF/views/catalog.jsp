<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Book Catalog - LibraryHub" />
<c:set var="pageName" value="catalog" />
<jsp:include page="common/header.jsp" />

<div style="margin-bottom: 2rem;">
    <h1 style="font-size: 2rem; font-weight: 800; color: #0f172a; margin-bottom: 0.5rem;">Library Catalog</h1>
    <p style="color: #64748b;">Search by title, author, or ISBN. Real-time availability updated continuously.</p>
</div>

<!-- Search & Filter Controls -->
<div class="filter-card">
    <form method="GET" action="${pageContext.request.contextPath}/catalog" class="filter-form">
        <div class="input-group">
            <input type="text" id="searchInput" name="q" class="form-control"
                   placeholder="Search by title, author, or ISBN..."
                   value="${searchQuery}" autocomplete="off" />
        </div>

        <div>
            <select id="categorySelect" name="category" class="form-control" onchange="this.form.submit()">
                <option value="ALL">All Categories</option>
                <c:forEach var="cat" items="${categories}">
                    <option value="${cat}" ${selectedCategory == cat ? 'selected' : ''}>${cat}</option>
                </c:forEach>
            </select>
        </div>

        <div>
            <label class="checkbox-label">
                <input type="checkbox" id="availableCheck" name="available" value="1"
                       ${availableOnly ? 'checked' : ''} onchange="this.form.submit()" />
                <span>Available Copies Only</span>
            </label>
        </div>

        <div style="display: flex; gap: 0.5rem;">
            <button type="submit" class="btn btn-primary btn-sm">Search</button>
            <a href="${pageContext.request.contextPath}/catalog" class="btn btn-secondary btn-sm">Reset</a>
        </div>
    </form>
</div>

<!-- Results Counter -->
<div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 1.5rem;">
    <span style="font-size: 0.95rem; color: #64748b;">
        Showing <strong>${books.size()}</strong> of <strong>${totalResults}</strong> books
    </span>
</div>

<!-- Books Grid -->
<div id="catalogContainer" class="book-grid">
    <c:choose>
        <c:when test="${not empty books}">
            <c:forEach var="book" items="${books}">
                <div class="book-card">
                    <div class="book-header">
                        <span class="category-badge">${book.category}</span>
                        <span class="isbn-code">ISBN: ${book.isbn}</span>
                    </div>
                    <div class="book-body">
                        <h3 class="book-title">${book.title}</h3>
                        <p class="book-author">by ${book.author}</p>
                        <p class="book-description">${book.description != null ? book.description : 'No description provided.'}</p>
                        <div class="book-meta">
                            <span>Shelf: <strong>${book.locationShelf != null ? book.locationShelf : 'General'}</strong></span>
                            <c:choose>
                                <c:when test="${book.availableCopies > 0}">
                                    <span class="badge badge-success">${book.availableCopies} in stock</span>
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
        </c:when>
        <c:otherwise>
            <div style="grid-column: 1 / -1; text-align: center; padding: 4rem; background: #fff; border-radius: 10px; border: 1px solid #e2e8f0;">
                <p style="font-size: 1.25rem; font-weight: 600; color: #475569; margin-bottom: 0.5rem;">No books found</p>
                <p style="color: #94a3b8; margin-bottom: 1.5rem;">Try modifying your search terms or clearing the category filters.</p>
                <a href="${pageContext.request.contextPath}/catalog" class="btn btn-secondary btn-sm">Reset All Filters</a>
            </div>
        </c:otherwise>
    </c:choose>
</div>

<!-- Pagination -->
<c:if test="${totalPages > 1}">
    <div style="display: flex; justify-content: center; gap: 0.5rem; margin-top: 2rem;">
        <c:forEach var="i" begin="1" end="${totalPages}">
            <a href="${pageContext.request.contextPath}/catalog?page=${i}&q=${searchQuery}&category=${selectedCategory}&available=${availableOnly ? '1' : '0'}"
               class="btn btn-sm ${currentPage == i ? 'btn-primary' : 'btn-secondary'}">
                ${i}
            </a>
        </c:forEach>
    </div>
</c:if>

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
                        <li>Standard student loan duration is <strong>14 days</strong>.</li>
                        <li>Return or renew prior to the due date to avoid penalties.</li>
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
