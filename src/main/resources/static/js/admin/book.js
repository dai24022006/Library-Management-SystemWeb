(() => {
  const app = window.AdminApp;
  const $ = app.$;
  const API_BOOKS = "/api/books";

  let books = [];
  let editingBookId = null;

  // =========================
  // DANH SÁCH SÁCH
  // =========================

  async function loadBooks() {
    try {
      books = await app.getList(API_BOOKS);
      renderBooks();
    } catch (error) {
      console.error(error);

      app.showEmpty("bookTable", "Không tải được danh sách sách", 6);

      app.notify(error.message, false);
    }
  }

  function renderBooks() {
    const tbody = $("bookTable");
    tbody.replaceChildren();

    const keyword = $("bookSearch").value.trim();

    const filtered = books.filter((book) =>
      app.searchMatch(
        [book.bookId, book.title, book.author, book.publisher, book.category],
        keyword,
      ),
    );

    if (!filtered.length) {
      app.showEmpty("bookTable", "Không có sách phù hợp", 6);
      return;
    }

    filtered.forEach((book, index) => {
      const row = document.createElement("tr");

      app.createCell(row, index + 1);
      app.createCell(row, book.bookId ?? book.id);
      app.createCell(row, book.title);
      app.createCell(row, book.author);
      app.createCell(row, book.quantity);

      const actions = document.createElement("td");

      actions.appendChild(
        app.createAction("Sửa", "secondary-btn", () => openBookForm(book)),
      );

      actions.appendChild(
        app.createAction("Xóa", "danger-btn", () => deleteBook(book)),
      );

      row.appendChild(actions);
      tbody.appendChild(row);
    });
  }

  // =========================
  // FORM THÊM / SỬA SÁCH
  // =========================

  function openBookForm(book = null) {
    $("bookForm").reset();

    editingBookId = book ? book.id : null;

    $("bookModalTitle").textContent = book
      ? "Sửa thông tin sách"
      : "Thêm sách mới";

    $("bookIdInput").value = book?.bookId ?? "";
    $("bookTitleInput").value = book?.title ?? "";
    $("bookAuthorInput").value = book?.author ?? "";
    $("bookPublisherInput").value = book?.publisher ?? "";
    $("bookYearInput").value = book?.publicationYear ?? "";
    $("bookCategoryInput").value = book?.category ?? "";
    $("bookPriceInput").value = book?.price ?? "";
    $("bookQuantityInput").value = book?.quantity ?? 0;

    $("saveBookBtn").textContent = book ? "Lưu thay đổi" : "Thêm sách";

    app.openModal("bookModal");
  }

  function closeBookForm() {
    app.closeModal("bookModal");
    $("bookForm").reset();
    editingBookId = null;
  }

  function getBookData() {
    const year = $("bookYearInput").value;
    const price = $("bookPriceInput").value;

    return {
      bookId: $("bookIdInput").value.trim(),
      title: $("bookTitleInput").value.trim(),
      author: $("bookAuthorInput").value.trim(),
      publisher: $("bookPublisherInput").value.trim(),
      publicationYear: year ? Number(year) : null,
      category: $("bookCategoryInput").value.trim(),
      price: price ? Number(price) : null,
      quantity: Number($("bookQuantityInput").value),
    };
  }

  // =========================
  // LƯU SÁCH
  // =========================

  async function saveBook(event) {
    event.preventDefault();

    const data = getBookData();
    const editing = editingBookId !== null;

    if (!data.bookId || !data.title) {
      app.notify("Vui lòng nhập mã sách và tên sách.", false);
      return;
    }

    if (!Number.isInteger(data.quantity) || data.quantity < 0) {
      app.notify("Số lượng sách không hợp lệ.", false);
      return;
    }

    app.setSaving("saveBookBtn", true);

    try {
      const url = editing ? `${API_BOOKS}/${editingBookId}` : API_BOOKS;

      await app.sendJson(url, editing ? "PUT" : "POST", data);

      closeBookForm();
      await loadBooks();

      app.notify(
        editing ? "Cập nhật sách thành công!" : "Thêm sách thành công!",
      );
    } catch (error) {
      app.notify(error.message, false);
    } finally {
      app.setSaving("saveBookBtn", false);
    }
  }

  // =========================
  // XÓA SÁCH
  // =========================

  async function deleteBook(book) {
    const name = book.title ?? book.bookId;

    if (!confirm(`M có chắc muốn xóa sách "${name}" không?`)) {
      return;
    }

    try {
      await app.request(`${API_BOOKS}/${book.id}`, { method: "DELETE" });

      await loadBooks();
      app.notify("Xóa sách thành công!");
    } catch (error) {
      app.notify(error.message, false);
    }
  }

  // =========================
  // KHỞI TẠO MODULE BOOK
  // =========================

  function init() {
    $("bookSearch").addEventListener("input", renderBooks);

    $("addBookBtn").addEventListener("click", () => openBookForm());

    app.bindModalClose(
      "bookModal",
      ["closeBookModalBtn", "cancelBookModalBtn"],
      closeBookForm,
    );

    $("bookForm").addEventListener("submit", saveBook);
  }

  app.books = {
    init,
    load: loadBooks,
  };
})();
