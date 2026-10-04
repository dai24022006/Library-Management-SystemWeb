const API_URL = "http://localhost:8080/api/borrows";

let currentStatus = "BORROWING";
let currentSort = "";
let currentBorrowId = null;
let currentReturnRequest = null;
let currentSortField = "";
let currentSortDirection = "";

let currentPage = 1;
const pageSize = 10;
let index = 0;
let currentBorrows = [];

document.addEventListener("DOMContentLoaded", () => {
  loadBorrows();
});

// =========================
// LOAD DATA
// =========================

async function loadBorrows() {
  const keyword = document.getElementById("searchInput").value.trim();

  try {
    await fetch(`${API_URL}/update-overdue`, { method: "PUT" });

    let url = `${API_URL}/filter`;

    const params = new URLSearchParams();

    // Từ khóa tìm kiếm
    if (keyword) {
      params.append("id", keyword);
    }

    // Trạng thái
    params.append("status", currentStatus);

    // Sắp xếp
    if (currentSortField && currentSortDirection) {
      params.append("sortBy", `${currentSortField}_${currentSortDirection}`);
    }

    url += `?${params.toString()}`;

    const response = await fetch(url);

    if (!response.ok) {
      const errorText = await response.text();
      throw new Error(errorText || "Không thể lấy danh sách mượn");
    }

    const borrows = await response.json();

    renderBorrows(borrows);
  } catch (error) {
    console.error(error);
    alert(error.message || "Không thể kết nối đến server");
  }
}

// =========================
// RENDER TABLE
// =========================

function renderBorrows(borrows) {
  const table = document.getElementById("borrowTable");

  table.innerHTML = "";

  currentBorrows = borrows;

  if (borrows.length === 0) {
    table.innerHTML = `
      <tr>
        <td colspan="9">
          Không có dữ liệu
        </td>
      </tr>
    `;

    renderPagination();
    return;
  }

  // Tính vị trí bắt đầu và kết thúc của trang hiện tại
  const start = (currentPage - 1) * pageSize;
  const end = start + pageSize;

  // Chỉ lấy borrow của trang hiện tại
  const pageBorrows = borrows.slice(start, end);

  pageBorrows.forEach((borrow, index) => {
    const stt = (currentPage - 1) * pageSize + index + 1;

    const row = document.createElement("tr");

    row.innerHTML = `
      <td>${stt}</td>

      <td>
        ${borrow.borrowId ?? "N/A"}
      </td>

      <td>
        ${borrow.readerId ?? "N/A"}
      </td>

      <td>
        ${borrow.readerName ?? "N/A"}
      </td>

      <td>
        ${formatDate(borrow.borrowDate)}
      </td>

      <td>
        ${formatDate(borrow.dueDate)}
      </td>

      <td>
        ${formatDate(borrow.returnDate)}
      </td>

      <td>
        ${getStatus(borrow)}
      </td>

      <td>
        ${
          borrow.status === "BORROWING" || borrow.status === "OVERDUE"
            ? `
              <button
                class="btn-return"
                onclick="returnBook('${borrow.borrowId}')">
                Trả sách
              </button>
            `
            : ""
        }
        <button
          class="btn-detail"
          onclick="viewDetail('${borrow.borrowId}')">
          Chi tiết
        </button>
      </td>
    `;

    table.appendChild(row);
  });

  renderPagination();
}

function renderPagination() {
  const pagination = document.querySelector(".pagination");

  const totalPages = Math.ceil(currentBorrows.length / pageSize);

  pagination.innerHTML = "";

  // Không có dữ liệu hoặc chỉ có 1 trang
  if (totalPages <= 1) {
    return;
  }

  // Nút trang trước
  const prevButton = document.createElement("button");

  prevButton.textContent = "< ";
  prevButton.disabled = currentPage === 1;

  prevButton.onclick = () => {
    if (currentPage > 1) {
      currentPage--;
      renderBorrows(currentBorrows);
    }
  };

  pagination.appendChild(prevButton);

  // Các số trang
  for (let i = 1; i <= totalPages; i++) {
    const pageButton = document.createElement("button");

    pageButton.textContent = i;

    if (i === currentPage) {
      pageButton.classList.add("active");
    }

    pageButton.onclick = () => {
      currentPage = i;
      renderBorrows(currentBorrows);
    };

    pagination.appendChild(pageButton);
  }

  // Nút trang sau
  const nextButton = document.createElement("button");

  nextButton.textContent = ">";
  nextButton.disabled = currentPage === totalPages;

  nextButton.onclick = () => {
    if (currentPage < totalPages) {
      currentPage++;
      renderBorrows(currentBorrows);
    }
  };

  pagination.appendChild(nextButton);
}

// =========================
// STATUS
// =========================

function getStatus(borrow) {
  if (borrow.status === "RETURNED") {
    return `
      <span class="status returned">
        Đã trả
      </span>
    `;
  }

  if (borrow.status === "OVERDUE") {
    return `
      <span class="status overdue">
        Quá hạn
      </span>
    `;
  }

  return `
    <span class="status borrowing">
      Đang mượn
    </span>
  `;
}

// =========================
// FILTER
// =========================

function filterStatus(status, button) {
  currentStatus = status;
  currentPage = 1;

  document.querySelectorAll(".status-filter button").forEach((button) => {
    button.classList.remove("active");
  });

  button.classList.add("active");

  loadBorrows();
}

// =========================
// SEARCH
// =========================

async function searchBorrow() {
  const keyword = document.getElementById("searchInput").value.trim();

  if (!keyword) {
    loadBorrows();
    return;
  }

  try {
    let url = `${API_URL}/search?id=${encodeURIComponent(keyword)}`;

    // Thêm status vào request
    url += `&status=${encodeURIComponent(currentStatus)}`;

    const response = await fetch(url);

    if (!response.ok) {
      const errorData = await response.json();

      throw new Error(errorData.message || "Không thể tìm kiếm");
    }

    const borrows = await response.json();

    currentPage = 1;

    renderBorrows(borrows);
  } catch (error) {
    console.error(error);
    alert(error.message);
  }
}

// =========================
// SORT
// =========================

function sortBorrow(field) {
  if (currentSortField === field) {
    // Bấm lần 2 -> đảo chiều
    currentSortDirection = currentSortDirection === "asc" ? "desc" : "asc";
  } else {
    // Field mới -> mặc định tăng dần
    currentSortField = field;
    currentSortDirection = "asc";
  }

  const sortBy = `${currentSortField}_${currentSortDirection}`;

  // cập nhật icon
  document.querySelectorAll(".sort-icon").forEach((icon) => {
    icon.textContent = "↕";
    icon.classList.remove("active");
  });

  const currentIcon = document.getElementById(`sort-${field}`);

  if (currentIcon) {
    currentIcon.textContent = currentSortDirection === "asc" ? "↑" : "↓";

    currentIcon.classList.add("active");
  }

  // gọi lại dữ liệu
  loadBorrows();
}

// =========================
// RETURN BOOK
// =========================

async function returnBook(borrowId) {
  try {
    const response = await fetch(`${API_URL}/${borrowId}/detail`);

    if (!response.ok) {
      throw new Error("Không tìm thấy phiếu mượn");
    }

    const borrow = await response.json();

    currentBorrowId = borrowId;
    currentReturnRequest = null;

    // Reset phần hóa đơn cũ
    document.getElementById("returnPayment").innerHTML = "";

    // Hiện lại nút xác nhận trả
    document.getElementById("confirmReturnBtn").style.display = "block";

    const content = document.getElementById("returnContent");

    content.innerHTML = `
      <div class="return-reader">
        <b>${borrow.readerId}</b>
        - ${borrow.readerName}
      </div>

      ${borrow.books
        .map((book, index) => {
          const remaining =
            book.quantity -
            (book.returnedQuantity || 0) -
            (book.damagedQuantity || 0) -
            (book.lostQuantity || 0);

          // Đã trả hết thì không hiển thị nữa
          if (remaining <= 0) {
            return "";
          }

          return `
      <div class="return-book">

        <div class="return-book-name">
          ${index + 1}.
          ${book.bookId}
          -
          ${book.bookTitle}
        </div>

        <div class="return-book-quantity">
          Số lượng còn lại:
          ${remaining}
        </div>

        <div class="return-inputs">

          <div>
            <label>
              Trả bình thường
            </label>

            <input
              type="number"
              class="returned-quantity"
              data-book-id="${book.bookId}"
              min="0"
              max="${remaining}"
              value="0"
            >
          </div>

          <div>
            <label>
              Hỏng
            </label>

            <input
              type="number"
              class="damaged-quantity"
              data-book-id="${book.bookId}"
              min="0"
              max="${remaining}"
              value="0"
            >
          </div>

          <div>
            <label>
              Mất
            </label>

            <input
              type="number"
              class="lost-quantity"
              data-book-id="${book.bookId}"
              min="0"
              max="${remaining}"
              value="0"
            >
          </div>

        </div>

      </div>
    `;
        })
        .join("")}
    `;

    document.getElementById("returnContainer").classList.add("show");
  } catch (error) {
    console.error(error);
    alert(error.message);
  }
}

// =========================
// CONFIRM RETURN
// =========================
async function confirmReturn() {
  const books = [];

  const returnedInputs = document.querySelectorAll(".returned-quantity");

  returnedInputs.forEach((input) => {
    const bookId = input.dataset.bookId;

    const returned = Number(input.value);

    const damaged = Number(
      document.querySelector(`.damaged-quantity[data-book-id="${bookId}"]`)
        .value,
    );

    const lost = Number(
      document.querySelector(`.lost-quantity[data-book-id="${bookId}"]`).value,
    );

    if (returned + damaged + lost > 0) {
      books.push({
        bookId: bookId,
        returnedQuantity: returned,
        damagedQuantity: damaged,
        lostQuantity: lost,
      });
    }
  });

  if (books.length === 0) {
    alert("Chưa nhập số lượng trả");
    return;
  }

  const request = {
    books: books,
  };

  try {
    // Chỉ preview, chưa thay đổi database
    const response = await fetch(
      `${API_URL}/${currentBorrowId}/return/preview`,
      {
        method: "POST",

        headers: {
          "Content-Type": "application/json",
        },

        body: JSON.stringify(request),
      },
    );

    if (!response.ok) {
      const error = await response.text();
      throw new Error(error);
    }

    const borrow = await response.json();

    // Có phí -> lưu request lại để thanh toán sau
    if (Number(borrow.totalFee) > 0 && borrow.payStatus === "UNPAID") {
      currentReturnRequest = request;

      showPayment(borrow);

      return;
    }

    // Không có phí -> trả sách thật
    const returnResponse = await fetch(`${API_URL}/${currentBorrowId}/return`, {
      method: "PUT",

      headers: {
        "Content-Type": "application/json",
      },

      body: JSON.stringify(request),
    });

    if (!returnResponse.ok) {
      const error = await returnResponse.text();
      throw new Error(error);
    }

    alert("Trả sách thành công!");

    closeReturn();

    loadBorrows();
  } catch (error) {
    console.error(error);
    alert(error.message || "Không thể trả sách");
  }
}

// =========================
// PAYMENT
// =========================

function showPayment(borrow) {
  const payment = document.getElementById("returnPayment");

  payment.innerHTML = `
    <div class="payment-box">

      <div class="payment-title">
        Hóa đơn
      </div>

      <div class="payment-fee">
        Phí phải trả:

        <strong>
          ${Number(borrow.totalFee).toLocaleString("vi-VN")} đ
        </strong>
      </div>

      <button
        class="btn-pay"
        onclick="payBorrow('${borrow.borrowId}')">
        Thanh toán
      </button>

    </div>
  `;

  // Không cho xác nhận trả lần nữa
  document.getElementById("confirmReturnBtn").style.display = "none";
}

async function payBorrow(borrowId) {
  if (!currentReturnRequest) {
    alert("Không tìm thấy thông tin trả sách");
    return;
  }
  const confirmPay = confirm("Bạn có chắc muốn thanh toán hóa đơn này?");
  if (!confirmPay) {
    return;
  }
  try {
    const response = await fetch(`${API_URL}/${borrowId}/pay`, {
      method: "PUT",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(currentReturnRequest),
    });
    if (!response.ok) {
      const error = await response.text();
      throw new Error(error);
    }
    alert("Thanh toán thành công!");
    closeReturn();
    loadBorrows();
  } catch (error) {
    console.error(error);
    alert(error.message || "Không thể thanh toán");
  }
}

function closeReturn() {
  document.getElementById("returnContainer").classList.remove("show");
  document.getElementById("returnPayment").innerHTML = "";
  document.getElementById("confirmReturnBtn").style.display = "block";
  currentBorrowId = null;
  currentReturnRequest = null;
}

// =========================
// DETAIL
// =========================

async function viewDetail(borrowId) {
  try {
    const response = await fetch(`${API_URL}/${borrowId}/detail`);

    if (!response.ok) {
      throw new Error("Không tìm thấy phiếu mượn");
    }

    const borrow = await response.json();

    const container = document.getElementById("detailContainer");

    const detail = document.getElementById("viewDetail");

    detail.innerHTML = `
      <div class="detail-row">

        <div class="detail-label">
          Mã phiếu:
        </div>

        <div class="detail-value">
          ${borrow.borrowId}
        </div>

      </div>

      <div class="detail-row">

        <div class="detail-label">
          ID độc giả:
        </div>

        <div class="detail-value">
          ${borrow.readerId ?? "N/A"}
        </div>

      </div>

      <div class="detail-row">

        <div class="detail-label">
          Tên độc giả:
        </div>

        <div class="detail-value">
          ${borrow.readerName ?? "N/A"}
        </div>

      </div>

      <div class="detail-row">

        <div class="detail-label">
          Sách mượn:
        </div>

        <div class="detail-value book-list">

          ${
            borrow.books && borrow.books.length > 0
              ? borrow.books
                  .map(
                    (book, index) => `
                      <div class="book-detail">
                        ${index + 1}.
                        ${book.bookId}
                        -
                        ${book.bookTitle}
                        (SL: ${book.quantity})
                      </div>
                    `,
                  )
                  .join("")
              : "N/A"
          }

        </div>

      </div>

      <div class="detail-row">

        <div class="detail-label">
          Ngày mượn:
        </div>

        <div class="detail-value">
          ${formatDate(borrow.borrowDate)}
        </div>

      </div>

      <div class="detail-row">

        <div class="detail-label">
          Hạn trả:
        </div>

        <div class="detail-value">
          ${formatDate(borrow.dueDate)}
        </div>

      </div>

      <div class="detail-row">

        <div class="detail-label">
          Ngày trả:
        </div>

        <div class="detail-value">
          ${formatDate(borrow.returnDate)}
        </div>

      </div>

      <div class="detail-row">

        <div class="detail-label">
          Trạng thái:
        </div>

        <div class="detail-value">
          ${getStatus(borrow)}
        </div>

      </div>
    `;

    container.classList.add("show");
  } catch (error) {
    console.error(error);
  }
}

function closeDetail() {
  document.getElementById("detailContainer").classList.remove("show");
}

// =========================
// CREATE BORROW
// =========================

function addBorrow() {
  window.location.href = "borrow-form.html";
}

// =========================
// DATE
// =========================

function formatDate(date) {
  if (!date) {
    return "-";
  }

  const d = new Date(date);

  return d.toLocaleDateString("vi-VN");
}

// =========================
// SEARCH CLEAR BUTTON
// =========================

function toggleClearButton() {
  const input = document.getElementById("searchInput");

  const clearButton = document.getElementById("clearSearchBtn");

  if (input.value.trim() !== "") {
    clearButton.style.display = "block";
  } else {
    clearButton.style.display = "none";
  }
}

function clearSearch() {
  const input = document.getElementById("searchInput");

  input.value = "";

  toggleClearButton();

  searchBorrow();
}
