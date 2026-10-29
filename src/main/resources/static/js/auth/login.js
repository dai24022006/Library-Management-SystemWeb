document.addEventListener("DOMContentLoaded", () => {
  const loginForm = document.getElementById("loginForm");
  const usernameInput = document.getElementById("username");
  const passwordInput = document.getElementById("password");
  const togglePassword = document.getElementById("togglePassword");
  const errorMessage = document.getElementById("errorMessage");
  const submitButton = loginForm.querySelector('button[type="submit"]');

  // Hiện / ẩn mật khẩu
  togglePassword.addEventListener("click", () => {
    const hidden = passwordInput.type === "password";

    passwordInput.type = hidden ? "text" : "password";
    togglePassword.textContent = hidden ? "Ẩn" : "Hiện";
  });

  // Hiển thị thông báo
  function showMessage(message, success = false) {
    errorMessage.textContent = message;
    errorMessage.style.display = "block";
    errorMessage.style.background = success ? "#dcfce7" : "#fee2e2";
    errorMessage.style.color = success ? "#166534" : "#b91c1c";
  }

  const params = new URLSearchParams(window.location.search);

  if (params.has("logout")) {
    showMessage("Đăng xuất thành công!", true);
  }

  // Gửi đăng nhập
  loginForm.addEventListener("submit", async (event) => {
    event.preventDefault();

    const username = usernameInput.value.trim();
    const password = passwordInput.value;

    errorMessage.style.display = "none";

    if (!username || !password) {
      showMessage("Vui lòng nhập đầy đủ thông tin!");
      return;
    }

    submitButton.disabled = true;
    submitButton.textContent = "Đang đăng nhập...";

    try {
      const response = await fetch("/api/session/login", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        credentials: "same-origin",
        body: JSON.stringify({
          username,
          password,
        }),
      });

      const result = await response.json();

      if (!response.ok) {
        showMessage(result.message || "Đăng nhập thất bại!");
        return;
      }

      // Backend quyết định trang chuyển đến
      window.location.replace(result.redirect);
    } catch (error) {
      console.error(error);
      showMessage("Không thể kết nối tới máy chủ!");
    } finally {
      submitButton.disabled = false;
      submitButton.innerHTML = "Đăng nhập <span>→</span>";
    }
  });
});
