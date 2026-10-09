<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Manage Books - Admin - LibraryHub" />
<c:set var="pageName" value="admin_books" />
<jsp:include page="../common/header.jsp" />

<div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 2rem;">
    <div>
        <h1 style="font-size: 2rem; font-weight: 800; color: #0f172a; margin-bottom: 0.25rem;">Book Catalog Management</h1>
        <p style="color: #64748b;">Add new acquisitions, update metadata, adjust inventory copies, and organize shelves</p>
    </div>
    <button type="button" class="btn btn-primary btn-sm" onclick="openAddBookModal()">+ Add New Book</button>
</div>

<!-- Search & Category Filters -->
<div class="filter-card">
    <form method="GET" action="${pageContext.request.contextPath}/admin/books" class="filter-form" style="grid-template-columns: 2fr 1fr auto auto;">
        <div class="input-group">
            <input type="text" name="q" class="form-control" placeholder="Search by title, author, or ISBN..." value="${searchQuery}" />
        </div>
        <div>
            <select name="category" class="form-control" onchange="this.form.submit()">
                <option value="ALL">All Categories</option>
                <c:forEach var="cat" items="${categories}">
                    <option value="${cat}" ${selectedCategory == cat ? 'selected' : ''}>${cat}</option>
                </c:forEach>
            </select>
        </div>
        <button type="submit" class="btn btn-primary btn-sm">Filter</button>
        <a href="${pageContext.request.contextPath}/admin/books" class="btn btn-secondary btn-sm">Reset</a>
    </form>
</div>

<!-- Books Inventory Table -->
<div class="table-responsive">
    <table class="custom-table">
        <thead>
            <tr>
                <th>ID</th>
                <th>Book Details</th>
                <th>Category</th>
                <th>Shelf</th>
                <th>Total</th>
                <th>Available</th>
                <th style="text-align: right;">Actions</th>
            </tr>
        </thead>
        <tbody>
            <c:choose>
                <c:when test="${not empty books}">
                    <c:forEach var="b" items="${books}">
                        <tr>
                            <td>#${b.bookId}</td>
                            <td>
                                <strong>${b.title}</strong>
                                <div style="font-size: 0.8rem; color: #64748b;">By ${b.author}</div>
                                <div style="font-size: 0.75rem; color: #94a3b8; font-family: monospace;">ISBN: ${b.isbn}</div>
                            </td>
                            <td><span class="badge badge-secondary">${b.category}</span></td>
                            <td>${b.locationShelf != null ? b.locationShelf : 'General'}</td>
                            <td><strong>${b.totalCopies}</strong></td>
                            <td>
                                <c:choose>
                                    <c:when test="${b.availableCopies > 0}">
                                        <span class="badge badge-success">${b.availableCopies} available</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="badge badge-danger">0 available</span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td style="text-align: right;">
                                <div style="display: inline-flex; gap: 0.4rem;">
                                    <button type="button" class="btn btn-secondary btn-sm"
                                            onclick="openEditBookModal({
                                                bookId: ${b.bookId},
                                                isbn: '${b.isbn}',
                                                title: '${b.title}',
                                                author: '${b.author}',
                                                category: '${b.category}',
                                                publisher: '${b.publisher}',
                                                publicationYear: '${b.publicationYear}',
                                                totalCopies: ${b.totalCopies},
                                                availableCopies: ${b.availableCopies},
                                                locationShelf: '${b.locationShelf}',
                                                description: '${b.description}'
                                            })">Edit</button>

                                    <form method="POST" action="${pageContext.request.contextPath}/admin/books" style="display:inline;"
                                          onsubmit="return confirm('Are you sure you want to delete \'${b.title}\'? This action cannot be undone.');">
                                        <input type="hidden" name="action" value="delete" />
                                        <input type="hidden" name="bookId" value="${b.bookId}" />
                                        <button type="submit" class="btn btn-danger btn-sm">Delete</button>
                                    </form>
                                </div>
                            </td>
                        </tr>
                    </c:forEach>
                </c:when>
                <c:otherwise>
                    <tr>
                        <td colspan="7" style="text-align: center; padding: 2.5rem; color: #94a3b8;">
                            No books found matching criteria.
                        </td>
                    </tr>
                </c:otherwise>
            </c:choose>
        </tbody>
    </table>
</div>

<!-- Modal: Add / Edit Book -->
<div id="bookModal" class="modal-overlay">
    <div class="modal-content" style="max-width: 650px;">
        <div class="modal-header">
            <h3 id="bookModalHeader">Add New Book to Catalog</h3>
            <button class="modal-close" onclick="closeModal('bookModal')">&times;</button>
        </div>
        <form id="bookForm" method="POST" action="${pageContext.request.contextPath}/admin/books">
            <input type="hidden" id="bookAction" name="action" value="add" />
            <input type="hidden" id="bookIdInput" name="bookId" value="" />

            <div class="modal-body">
                <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 1rem;">
                    <div class="form-group">
                        <label class="form-label" for="isbnInput">ISBN *</label>
                        <input type="text" id="isbnInput" name="isbn" class="form-control" placeholder="e.g. 978-0132350884" required />
                    </div>
                    <div class="form-group">
                        <label class="form-label" for="categoryInput">Category *</label>
                        <input type="text" id="categoryInput" name="category" class="form-control" placeholder="e.g. Computer Science" required />
                    </div>
                </div>

                <div class="form-group">
                    <label class="form-label" for="titleInput">Book Title *</label>
                    <input type="text" id="titleInput" name="title" class="form-control" placeholder="e.g. Clean Code" required />
                </div>

                <div class="form-group">
                    <label class="form-label" for="authorInput">Author(s) *</label>
                    <input type="text" id="authorInput" name="author" class="form-control" placeholder="e.g. Robert C. Martin" required />
                </div>

                <div style="display: grid; grid-template-columns: 2fr 1fr; gap: 1rem;">
                    <div class="form-group">
                        <label class="form-label" for="publisherInput">Publisher</label>
                        <input type="text" id="publisherInput" name="publisher" class="form-control" placeholder="e.g. Prentice Hall" />
                    </div>
                    <div class="form-group">
                        <label class="form-label" for="yearInput">Publication Year</label>
                        <input type="number" id="yearInput" name="publicationYear" class="form-control" placeholder="e.g. 2008" />
                    </div>
                </div>

                <div style="display: grid; grid-template-columns: 1fr 1fr 1fr; gap: 1rem;">
                    <div class="form-group">
                        <label class="form-label" for="totalCopiesInput">Total Copies *</label>
                        <input type="number" id="totalCopiesInput" name="totalCopies" min="0" class="form-control" value="1" required />
                    </div>
                    <div class="form-group" id="availableCopiesGroup" style="display:none;">
                        <label class="form-label" for="availableCopiesInput">Available Copies</label>
                        <input type="number" id="availableCopiesInput" name="availableCopies" min="0" class="form-control" value="1" />
                    </div>
                    <div class="form-group">
                        <label class="form-label" for="shelfInput">Shelf Location</label>
                        <input type="text" id="shelfInput" name="locationShelf" class="form-control" placeholder="e.g. CS-A1-01" />
                    </div>
                </div>

                <div class="form-group">
                    <label class="form-label" for="descriptionInput">Description / Abstract</label>
                    <textarea id="descriptionInput" name="description" class="form-control" rows="3" placeholder="Overview of book contents..."></textarea>
                </div>
            </div>

            <div class="modal-footer">
                <button type="button" class="btn btn-secondary btn-sm" onclick="closeModal('bookModal')">Cancel</button>
                <button type="submit" class="btn btn-primary btn-sm">Save Book</button>
            </div>
        </form>
    </div>
</div>

<jsp:include page="../common/footer.jsp" />
