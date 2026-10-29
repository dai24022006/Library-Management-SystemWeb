(() => {
  const app = window.AdminApp;
  const $ = app.$;
  const API_READERS = "/api/readers";

  let readers = [];
  let editingReaderId = null;

  // =========================
  // DANH SÁCH ĐỘC GIẢ
  // =========================

  async function loadReaders() {
    try {
      readers = await app.getList(API_READERS);
      renderReaders();
    } catch (error) {
      console.error(error);

      app.showEmpty("readerTable", "Không tải được danh sách độc giả", 6);

      app.notify(error.message, false);
    }
  }

  function genderText(gender) {
    if (!gender) return "—";

    const values = {
      MALE: "Nam",
      FEMALE: "Nữ",
      OTHER: "Khác",
      NAM: "Nam",
      NU: "Nữ",
    };

    return values[gender] ?? gender;
  }

  function renderReaders() {
    const tbody = $("readerTable");
    tbody.replaceChildren();

    const keyword = $("readerSearch").value.trim();

    const filtered = readers.filter((reader) =>
      app.searchMatch(
        [reader.readerId, reader.fullName, reader.phoneNumber, reader.gender],
        keyword,
      ),
    );

    if (!filtered.length) {
      app.showEmpty("readerTable", "Không có độc giả phù hợp", 6);
      return;
    }

    filtered.forEach((reader, index) => {
      const row = document.createElement("tr");

      app.createCell(row, index + 1);
      app.createCell(row, reader.readerId ?? reader.id);
      app.createCell(row, reader.fullName);
      app.createCell(row, reader.phoneNumber);
      app.createCell(row, genderText(reader.gender));

      const actions = document.createElement("td");

      actions.appendChild(
        app.createAction("Sửa", "secondary-btn", () => openReaderForm(reader)),
      );

      actions.appendChild(
        app.createAction("Xóa", "danger-btn", () => deleteReader(reader)),
      );

      row.appendChild(actions);
      tbody.appendChild(row);
    });
  }

  // =========================
  // FORM THÊM / SỬA ĐỘC GIẢ
  // =========================

  function openReaderForm(reader = null) {
    $("readerForm").reset();

    editingReaderId = reader ? reader.id : null;

    $("readerModalTitle").textContent = reader
      ? "Sửa thông tin độc giả"
      : "Thêm độc giả mới";

    $("readerIdInput").value = reader?.readerId ?? "";

    $("readerNameInput").value = reader?.fullName ?? "";

    $("readerDobInput").value = reader?.dateOfBirth
      ? String(reader.dateOfBirth).substring(0, 10)
      : "";

    const genderSelect = $("readerGenderInput");
    const currentGender = reader?.gender ?? "";

    // Hỗ trợ giá trị enum đang có trong database
    if (
      currentGender &&
      !Array.from(genderSelect.options).some(
        (option) => option.value === currentGender,
      )
    ) {
      const option = document.createElement("option");
      option.value = currentGender;
      option.textContent = genderText(currentGender);
      genderSelect.appendChild(option);
    }

    genderSelect.value = currentGender;

    $("readerPhoneInput").value = reader?.phoneNumber ?? "";

    $("saveReaderBtn").textContent = reader ? "Lưu thay đổi" : "Thêm độc giả";

    app.openModal("readerModal");
  }

  function closeReaderForm() {
    app.closeModal("readerModal");
    $("readerForm").reset();
    editingReaderId = null;
  }

  function getReaderData() {
    return {
      readerId: $("readerIdInput").value.trim(),
      fullName: $("readerNameInput").value.trim(),
      dateOfBirth: $("readerDobInput").value || null,
      gender: $("readerGenderInput").value || null,
      phoneNumber: $("readerPhoneInput").value.trim(),
    };
  }

  // =========================
  // LƯU ĐỘC GIẢ
  // =========================

  async function saveReader(event) {
    event.preventDefault();

    const data = getReaderData();
    const editing = editingReaderId !== null;

    if (!data.readerId || !data.fullName) {
      app.notify("Vui lòng nhập mã độc giả và họ tên.", false);
      return;
    }

    app.setSaving("saveReaderBtn", true);

    try {
      const url = editing ? `${API_READERS}/${editingReaderId}` : API_READERS;

      await app.sendJson(url, editing ? "PUT" : "POST", data);

      closeReaderForm();
      await loadReaders();

      app.notify(
        editing ? "Cập nhật độc giả thành công!" : "Thêm độc giả thành công!",
      );
    } catch (error) {
      app.notify(error.message, false);
    } finally {
      app.setSaving("saveReaderBtn", false);
    }
  }

  // =========================
  // XÓA ĐỘC GIẢ
  // =========================

  async function deleteReader(reader) {
    const name = reader.fullName ?? reader.readerId;

    if (!confirm(`M có chắc muốn xóa độc giả "${name}" không?`)) {
      return;
    }

    try {
      await app.request(`${API_READERS}/${reader.id}`, { method: "DELETE" });

      await loadReaders();
      app.notify("Xóa độc giả thành công!");
    } catch (error) {
      app.notify(error.message, false);
    }
  }

  // =========================
  // KHỞI TẠO MODULE READER
  // =========================

  function init() {
    $("readerSearch").addEventListener("input", renderReaders);

    $("addReaderBtn").addEventListener("click", () => openReaderForm());

    app.bindModalClose(
      "readerModal",
      ["closeReaderModalBtn", "cancelReaderModalBtn"],
      closeReaderForm,
    );

    $("readerForm").addEventListener("submit", saveReader);
  }

  app.readers = {
    init,
    load: loadReaders,
  };
})();
