/**
 * Online Library Management System - JavaScript Client Helper
 */

document.addEventListener('DOMContentLoaded', () => {
    initLiveSearch();
    initRegistrationValidation();
});

// --------------------------------------------------------------------
// Modal Dialog Controls
// --------------------------------------------------------------------
function openModal(modalId) {
    const modal = document.getElementById(modalId);
    if (modal) {
        modal.classList.add('active');
        document.body.style.overflow = 'hidden';
    }
}

function closeModal(modalId) {
    const modal = document.getElementById(modalId);
    if (modal) {
        modal.classList.remove('active');
        document.body.style.overflow = '';
    }
}

// Close modal when clicking outside the dialog window
window.addEventListener('click', (e) => {
    if (e.target.classList.contains('modal-overlay')) {
        e.target.classList.remove('active');
        document.body.style.overflow = '';
    }
});

// --------------------------------------------------------------------
// Borrow Modal Helper
// --------------------------------------------------------------------
function showBorrowModal(bookId, bookTitle, author, availableCopies) {
    const modal = document.getElementById('borrowModal');
    if (!modal) return;

    document.getElementById('borrowBookId').value = bookId;
    document.getElementById('borrowModalTitle').textContent = bookTitle;
    document.getElementById('borrowModalAuthor').textContent = 'By ' + author;
    document.getElementById('borrowModalStock').textContent = availableCopies + ' copies remaining';

    openModal('borrowModal');
}

// --------------------------------------------------------------------
// Admin Book Modal Helpers (Add & Edit)
// --------------------------------------------------------------------
function openAddBookModal() {
    const form = document.getElementById('bookForm');
    if (form) {
        form.reset();
        document.getElementById('bookAction').value = 'add';
        document.getElementById('bookModalHeader').textContent = 'Add New Book to Catalog';
        document.getElementById('bookIdInput').value = '';
        document.getElementById('isbnInput').readOnly = false;
        document.getElementById('availableCopiesGroup').style.display = 'none';
    }
    openModal('bookModal');
}

function openEditBookModal(book) {
    const form = document.getElementById('bookForm');
    if (!form) return;

    document.getElementById('bookAction').value = 'edit';
    document.getElementById('bookModalHeader').textContent = 'Edit Book Details';
    document.getElementById('bookIdInput').value = book.bookId;
    document.getElementById('isbnInput').value = book.isbn || '';
    document.getElementById('titleInput').value = book.title || '';
    document.getElementById('authorInput').value = book.author || '';
    document.getElementById('categoryInput').value = book.category || '';
    document.getElementById('publisherInput').value = book.publisher || '';
    document.getElementById('yearInput').value = book.publicationYear || '';
    document.getElementById('totalCopiesInput').value = book.totalCopies || 1;
    document.getElementById('availableCopiesInput').value = book.availableCopies || 1;
    document.getElementById('shelfInput').value = book.locationShelf || '';
    document.getElementById('descriptionInput').value = book.description || '';
    document.getElementById('availableCopiesGroup').style.display = 'block';

    openModal('bookModal');
}

// --------------------------------------------------------------------
// Live Search Debouncing
// --------------------------------------------------------------------
function initLiveSearch() {
    const searchInput = document.getElementById('searchInput');
    const catalogContainer = document.getElementById('catalogContainer');
    if (!searchInput || !catalogContainer) return;

    let debounceTimer;
    searchInput.addEventListener('input', () => {
        clearTimeout(debounceTimer);
        debounceTimer = setTimeout(() => {
            const query = searchInput.value.trim();
            const categorySelect = document.getElementById('categorySelect');
            const category = categorySelect ? categorySelect.value : 'ALL';
            const availableCheck = document.getElementById('availableCheck');
            const available = availableCheck && availableCheck.checked ? '1' : '0';

            const url = `${window.contextPath || ''}/catalog?format=json&q=${encodeURIComponent(query)}&category=${encodeURIComponent(category)}&available=${available}`;

            fetch(url)
                .then(res => res.json())
                .then(data => {
                    renderCatalogCards(data.books);
                })
                .catch(err => {
                    console.log('Live search error:', err);
                });
        }, 300);
    });
}

function renderCatalogCards(books) {
    const container = document.getElementById('catalogContainer');
    if (!container) return;

    if (!books || books.length === 0) {
        container.innerHTML = `
            <div style="grid-column: 1 / -1; text-align: center; padding: 3rem; background: #fff; border-radius: 10px; border: 1px solid #e2e8f0;">
                <p style="font-size: 1.1rem; color: #64748b; margin-bottom: 1rem;">No books match your search criteria.</p>
                <a href="${window.contextPath || ''}/catalog" class="btn btn-secondary btn-sm">Clear Filters</a>
            </div>
        `;
        return;
    }

    let html = '';
    books.forEach(b => {
        const isAvail = b.availableCopies > 0;
        const stockBadge = isAvail
            ? `<span class="badge badge-success">${b.availableCopies} in stock</span>`
            : `<span class="badge badge-danger">Out of Stock</span>`;

        const borrowButton = window.isLoggedIn
            ? (isAvail
                ? `<button type="button" class="btn btn-primary btn-sm btn-block" onclick="showBorrowModal(${b.bookId}, '${escapeHtml(b.title)}', '${escapeHtml(b.author)}', ${b.availableCopies})">Borrow Book</button>`
                : `<button class="btn btn-secondary btn-sm btn-block" disabled>Unavailable</button>`)
            : `<a href="${window.contextPath || ''}/login?redirect=/catalog" class="btn btn-secondary btn-sm btn-block">Login to Borrow</a>`;

        html += `
            <div class="book-card">
                <div class="book-header">
                    <span class="category-badge">${escapeHtml(b.category)}</span>
                    <span class="isbn-code">ISBN: ${escapeHtml(b.isbn)}</span>
                </div>
                <div class="book-body">
                    <h3 class="book-title">${escapeHtml(b.title)}</h3>
                    <p class="book-author">by ${escapeHtml(b.author)}</p>
                    <p class="book-description">${escapeHtml(b.description || 'No description available.')}</p>
                    <div class="book-meta">
                        <span>Shelf: <strong>${escapeHtml(b.locationShelf || 'General')}</strong></span>
                        ${stockBadge}
                    </div>
                </div>
                <div class="book-footer">
                    ${borrowButton}
                </div>
            </div>
        `;
    });

    container.innerHTML = html;
}

function escapeHtml(str) {
    if (!str) return '';
    return str.replace(/&/g, "&amp;")
              .replace(/</g, "&lt;")
              .replace(/>/g, "&gt;")
              .replace(/"/g, "&quot;")
              .replace(/'/g, "&#039;");
}

// --------------------------------------------------------------------
// Registration Client Validation
// --------------------------------------------------------------------
function initRegistrationValidation() {
    const regForm = document.getElementById('registerForm');
    if (!regForm) return;

    regForm.addEventListener('submit', (e) => {
        const pass = document.getElementById('password').value;
        const confirm = document.getElementById('confirmPassword').value;

        if (pass.length < 6) {
            e.preventDefault();
            alert('Password must be at least 6 characters in length.');
            return;
        }

        if (pass !== confirm) {
            e.preventDefault();
            alert('Passwords do not match. Please verify.');
        }
    });
}
