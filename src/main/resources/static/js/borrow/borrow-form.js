const API_URL = "http://localhost:8080/api/borrows";

const borrowForm = document.getElementById("borrowForm");

// Danh sách sách trong phiếu
let books = [];

// =========================
// THÊM SÁCH
// =========================

async function addBook() {
  const bookIdInput = document.getElementById("bookId");
  const quantityInput = document.getElementById("quantity");

  const bookId = bookIdInput.value.trim();
  const quantity = Number(quantityInput.value);

  // =========================
  // KIỂM TRA INPUT
  // =========================

  if (!bookId) {
    alert("Vui lòng nhập mã sách");
    bookIdInput.focus();
    return;
  }

  if (!quantity || quantity <= 0) {
    alert("Số lượng phải lớn hơn 0");
    quantityInput.focus();
    return;
  }

  // =========================
  // KIỂM TRA SÁCH ĐÃ THÊM
  // =========================

  const existingBook = books.find((book) => book.bookId === bookId);

  if (existingBook) {
    alert("Sách này đã được thêm vào phiếu");
    bookIdInput.focus();
    return;
  }

  // =========================
  // KIỂM TRA BOOK TRÊN SERVER
  // =========================

  try {
    const response = await fetch(API_URL + `/book/${bookId}`);

    if (!response.ok) {
      if (response.status === 404) {
        throw new Error("Không tìm thấy sách " + bookId);
      }

      throw new Error("Không thể kiểm tra sách");
    }

    const book = await response.json();

    // =========================
    // KIỂM TRA SỐ LƯỢNG
    // =========================

    if (book.quantity < quantity) {
      alert(`Sách ${bookId} chỉ còn ${book.quantity} quyển`);

      return;
    }

    // =========================
    // THÊM VÀO DANH SÁCH
    // =========================

    books.push({
      bookId: bookId,
      quantity: quantity,
    });

    renderBooks();

    // Reset input
    bookIdInput.value = "";
    quantityInput.value = 1;

    bookIdInput.focus();
  } catch (error) {
    console.error(error);

    alert(error.message);
  }
}

// =========================
// HIỂN THỊ SÁCH
// =========================

function renderBooks() {
  const bookList = document.getElementById("bookList");

  // Không có sách
  if (books.length === 0) {
    bookList.innerHTML = `
      <div class="empty-message">
        Chưa có sách nào
      </div>
    `;

    return;
  }

  // Có sách
  bookList.innerHTML = books
    .map((book, index) => {
      return `
        <div class="book-item">

          <div class="book-info">

            <strong>
              ${book.bookId}
            </strong>

          </div>


          <div class="book-actions">

            <div class="quantity-control">

              <button
                type="button"
                onclick="decreaseQuantity(${index})"
              >
                −
              </button>


              <span>
                ${book.quantity}
              </span>


              <button
                type="button"
                onclick="increaseQuantity(${index})"
              >
                +
              </button>

            </div>


            <button
              type="button"
              class="btn-remove"
              onclick="removeBook(${index})"
            >
              Xóa
            </button>

          </div>

        </div>
      `;
    })
    .join("");
}

// =========================
// TĂNG SỐ LƯỢNG
// =========================

function increaseQuantity(index) {
  books[index].quantity++;

  renderBooks();
}

// =========================
// GIẢM SỐ LƯỢNG
// =========================

function decreaseQuantity(index) {
  if (books[index].quantity <= 1) {
    return;
  }

  books[index].quantity--;

  renderBooks();
}

// =========================
// XÓA SÁCH
// =========================

function removeBook(index) {
  const book = books[index];

  const confirmDelete = confirm(`Bạn có chắc muốn xóa sách ${book.bookId}?`);

  if (!confirmDelete) {
    return;
  }

  books.splice(index, 1);

  renderBooks();
}

// =========================
// SUBMIT
// =========================

borrowForm.addEventListener("submit", async function (event) {
  event.preventDefault();

  const readerId = document.getElementById("readerId").value.trim();

  // Phải có sách
  if (books.length === 0) {
    alert("Vui lòng thêm ít nhất một sách");

    return;
  }

  // =========================
  // REQUEST
  // =========================

  const borrowRequest = {
    readerId: readerId,

    books: books,
  };

  console.log("Borrow request:", borrowRequest);

  try {
    const response = await fetch(API_URL, {
      method: "POST",

      headers: {
        "Content-Type": "application/json",
      },

      body: JSON.stringify(borrowRequest),
    });

    // Backend trả lỗi
    if (!response.ok) {
      const errorData = await response.json();

      throw new Error(errorData.message || "Không thể tạo phiếu mượn");
    }

    // Thành công
    const borrow = await response.json();

    console.log("Borrow created:", borrow);

    alert("Tạo phiếu mượn thành công!");

    window.location.href = "borrows.html";
  } catch (error) {
    console.error(error);

    alert(error.message);
  }
});

// =========================
// HỦY
// =========================

function cancelForm() {
  window.location.href = "borrows.html";
}
