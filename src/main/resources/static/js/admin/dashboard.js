(() => {
  const API_BOOKS = "/api/books";
  const API_READERS = "/api/readers";
  const API_LIBRARIANS = "/api/admin/librarians";

  const $ = (id) => document.getElementById(id);

  let librarians = [];
  let editingLibrarianId = null;

  const app = {
    books: null,
    readers: null,
    $,

    notify(message, success = true) {
      const element = $("notification");
      element.textContent = message;
      element.className = "notification " + (success ? "success" : "error");
    },

    clearNotification() {
      $("notification").textContent = "";
      $("notification").className = "notification";
    },

    normalizeList(response) {
      const data = response?.data ?? response;

      if (Array.isArray(data)) return data;
      if (Array.isArray(data?.content)) return data.content;
      if (Array.isArray(data?.items)) return data.items;
      if (Array.isArray(data?.books)) return data.books;
      if (Array.isArray(data?.readers)) return data.readers;

      throw new Error("API không trả về danh sách hợp lệ");
    },

    async request(url, options = {}) {
      const response = await fetch(url, {
        credentials: "same-origin",
        ...options,
      });

      const raw = await response.text();
      let result = {};

      if (raw) {
        try {
          result = JSON.parse(raw);
        } catch {
          result = { message: raw };
        }
      }

      if (!response.ok) {
        throw new Error(
          result.message ||
            result.error ||
            `Yêu cầu thất bại (${response.status})`,
        );
      }

      return result;
    },

    async getList(url) {
      const result = await this.request(url);
      return this.normalizeList(result);
    },

    async sendJson(url, method, data) {
      return this.request(url, {
        method,
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(data),
      });
    },

    createCell(row, value) {
      const td = document.createElement("td");

      td.textContent =
        value === null || value === undefined || value === ""
          ? "—"
          : String(value);

      row.appendChild(td);
      return td;
    },

    showEmpty(tbodyId, message, columns) {
      const tbody = $(tbodyId);
      tbody.replaceChildren();

      const row = document.createElement("tr");
      const td = this.createCell(row, message);

      td.colSpan = columns;
      td.style.textAlign = "center";
      td.style.padding = "24px";
      td.style.color = "#64748b";

      tbody.appendChild(row);
    },

    createAction(text, className, handler) {
      const button = document.createElement("button");

      button.type = "button";
      button.textContent = text;
      button.className = className;
      button.style.marginRight = "6px";

      button.addEventListener("click", handler);
      return button;
    },

    searchMatch(values, keyword) {
      return values
        .filter((value) => value !== null && value !== undefined)
        .join(" ")
        .toLowerCase()
        .includes(keyword.toLowerCase());
    },

    openModal(id) {
      $(id).classList.add("show");
    },

    closeModal(id) {
      $(id).classList.remove("show");
    },

    bindModalClose(modalId, buttons, callback) {
      buttons.forEach((id) => {
        $(id).addEventListener("click", callback);
      });

      $(modalId).addEventListener("click", (event) => {
        if (event.target === $(modalId)) {
          callback();
        }
      });
    },

    setSaving(buttonId, saving) {
      $(buttonId).disabled = saving;
    },

    async refreshOverview() {
      const results = await Promise.allSettled([
        this.getList(API_BOOKS),
        this.getList(API_READERS),
        this.getList(API_LIBRARIANS),
      ]);

      const ids = ["totalBooks", "totalReaders", "totalLibrarians"];

      results.forEach((result, index) => {
        if (result.status === "fulfilled") {
          $(ids[index]).textContent = result.value.length;
        } else {
          $(ids[index]).textContent = "—";
          console.error(result.reason);
        }
      });
    },
  };

  // Cung cấp hàm chung cho book.js và reader.js
  window.AdminApp = app;

  // =========================
  // CHUYỂN TAB
  // =========================

  const tabs = {
    overview: {
      breadcrumb: "Tổng quan",
      title: "Tổng quan hệ thống",
      description: "Theo dõi và quản lý hoạt động thư viện.",
    },
    books: {
      breadcrumb: "Quản lý sách",
      title: "Quản lý sách",
      description: "Thêm, sửa và xóa dữ liệu sách.",
    },
    readers: {
      breadcrumb: "Quản lý độc giả",
      title: "Quản lý độc giả",
      description: "Thêm, sửa và xóa dữ liệu độc giả.",
    },
    librarians: {
      breadcrumb: "Quản lý thủ thư",
      title: "Quản lý thủ thư",
      description: "Quản lý tài khoản thủ thư.",
    },
  };

  async function showTab(tab) {
    const info = tabs[tab];
    if (!info) return;

    document.querySelectorAll(".tab-section").forEach((section) => {
      section.classList.toggle("active", section.id === tab);
    });

    document.querySelectorAll(".nav-item").forEach((button) => {
      button.classList.toggle("active", button.dataset.tab === tab);
    });

    $("breadcrumb").textContent = info.breadcrumb;
    $("pageTitle").textContent = info.title;
    $("pageDescription").textContent = info.description;

    app.clearNotification();

    try {
      if (tab === "overview") {
        await app.refreshOverview();
      } else if (tab === "books") {
        await app.books.load();
      } else if (tab === "readers") {
        await app.readers.load();
      } else if (tab === "librarians") {
        await loadLibrarians();
      }
    } catch (error) {
      console.error(error);
      app.notify(error.message, false);
    }
  }

  // =========================
  // QUẢN LÝ THỦ THƯ
  // =========================

  async function loadLibrarians() {
    try {
      librarians = await app.getList(API_LIBRARIANS);
      renderLibrarians();
    } catch (error) {
      console.error(error);

      app.showEmpty("librarianTable", "Không tải được danh sách thủ thư", 6);

      app.notify(error.message, false);
    }
  }

  function renderLibrarians() {
    const tbody = $("librarianTable");
    tbody.replaceChildren();

    const keyword = $("librarianSearch").value.trim();

    const filtered = librarians.filter((user) =>
      app.searchMatch([user.username, user.email], keyword),
    );

    if (!filtered.length) {
      app.showEmpty("librarianTable", "Không có thủ thư phù hợp", 6);
      return;
    }

    filtered.forEach((user, index) => {
      const row = document.createElement("tr");

      app.createCell(row, index + 1);
      app.createCell(row, user.id);
      app.createCell(row, user.username);
      app.createCell(row, user.email);
      app.createCell(row, user.role);

      const actions = document.createElement("td");

      actions.appendChild(
        app.createAction("Sửa", "secondary-btn", () => openLibrarianForm(user)),
      );

      actions.appendChild(
        app.createAction("Xóa", "danger-btn", () => deleteLibrarian(user)),
      );

      row.appendChild(actions);
      tbody.appendChild(row);
    });
  }

  function openLibrarianForm(user = null) {
    $("librarianForm").reset();

    editingLibrarianId = user ? user.id : null;

    $("modalTitle").textContent = user
      ? "Sửa thông tin thủ thư"
      : "Thêm tài khoản thủ thư";

    $("newUsername").value = user?.username ?? "";
    $("newEmail").value = user?.email ?? "";
    $("newPassword").value = "";
    $("newPassword").required = !user;

    $("newPassword").placeholder = user
      ? "Để trống nếu không đổi mật khẩu"
      : "Nhập mật khẩu";

    $("saveLibrarianBtn").textContent = user
      ? "Lưu thay đổi"
      : "Thêm tài khoản";

    app.openModal("librarianModal");
  }

  function closeLibrarianForm() {
    app.closeModal("librarianModal");
    $("librarianForm").reset();
    editingLibrarianId = null;
  }

  async function saveLibrarian(event) {
    event.preventDefault();

    const editing = editingLibrarianId !== null;

    const data = {
      username: $("newUsername").value.trim(),
      email: $("newEmail").value.trim(),
      password: $("newPassword").value,
    };

    if (!data.username || !data.email) {
      app.notify("Vui lòng nhập tên đăng nhập và email.", false);
      return;
    }

    if (!editing && !data.password) {
      app.notify("Vui lòng nhập mật khẩu.", false);
      return;
    }

    app.setSaving("saveLibrarianBtn", true);

    try {
      const url = editing
        ? `${API_LIBRARIANS}/${editingLibrarianId}`
        : API_LIBRARIANS;

      await app.sendJson(url, editing ? "PUT" : "POST", data);

      closeLibrarianForm();
      await loadLibrarians();

      app.notify(
        editing ? "Cập nhật thủ thư thành công!" : "Thêm thủ thư thành công!",
      );
    } catch (error) {
      app.notify(error.message, false);
    } finally {
      app.setSaving("saveLibrarianBtn", false);
    }
  }

  async function deleteLibrarian(user) {
    if (!confirm(`M có chắc muốn xóa thủ thư "${user.username}" không?`)) {
      return;
    }

    try {
      await app.request(`${API_LIBRARIANS}/${user.id}`, { method: "DELETE" });

      await loadLibrarians();
      app.notify("Xóa thủ thư thành công!");
    } catch (error) {
      app.notify(error.message, false);
    }
  }

  // =========================
  // KIỂM TRA SESSION
  // =========================

  async function initialize() {
    try {
      const response = await fetch("/api/session/me", {
        credentials: "same-origin",
      });

      if (!response.ok) {
        window.location.replace("/login");
        return;
      }

      const result = await response.json();
      const user = result?.data ?? result;

      if (user.role !== "ADMIN") {
        window.location.replace("/borrows");
        return;
      }

      $("adminUsername").textContent = user.username ?? "Admin";

      await showTab("overview");
    } catch (error) {
      console.error(error);
      window.location.replace("/login");
    }
  }

  // =========================
  // KHỞI TẠO GIAO DIỆN
  // =========================

  document.addEventListener("DOMContentLoaded", () => {
    document.querySelectorAll("[data-tab]").forEach((button) => {
      button.addEventListener("click", () => {
        showTab(button.dataset.tab);
      });
    });

    $("librarianSearch").addEventListener("input", renderLibrarians);

    $("addLibrarianBtn").addEventListener("click", () => openLibrarianForm());

    app.bindModalClose(
      "librarianModal",
      ["closeModalBtn", "cancelModalBtn"],
      closeLibrarianForm,
    );

    $("librarianForm").addEventListener("submit", saveLibrarian);

    // Khởi tạo chức năng của người 2 và người 3
    app.books.init();
    app.readers.init();

    initialize();
  });

  window.addEventListener("pageshow", (event) => {
    if (event.persisted) {
      window.location.reload();
    }
  });
})();
