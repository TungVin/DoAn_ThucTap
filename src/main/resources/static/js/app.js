document.addEventListener("DOMContentLoaded", () => {

    const bell = document.querySelector(".bell");
    bell?.addEventListener("click", () => {
        Swal.fire({
            icon: "info",
            title: "Thông báo",
            text: "Bạn hiện chưa có thông báo mới.",
            confirmButtonText: "Đã hiểu"
        });
    });

    const tabs = document.querySelectorAll(".tab");
    const contents = document.querySelectorAll(".tab-content");

    tabs.forEach((tab, i) => {
        tab.addEventListener("click", () => {
            tabs.forEach(t => t.classList.remove("active"));
            contents.forEach(c => c.classList.remove("active"));

            tab.classList.add("active");
            contents[i].classList.add("active");
        });
    });

    // Auto select tab by query param: ?tab=classes | history | news
    try {
        const url = new URL(window.location.href);
        const tab = (url.searchParams.get("tab") || "").toLowerCase();
        const idx = tab === "history" ? 1 : (tab === "classes" ? 2 : 0);
        if (tabs[idx]) tabs[idx].click();
    } catch (e) {
        // ignore
    }

    const filterBtn = document.querySelector(".filter-btn");
    const filterBox = document.getElementById("filterBox");
    const cancelFilter = document.getElementById("cancelFilter");
    const applyFilter = document.getElementById("applyFilter");

    if (filterBtn && filterBox && cancelFilter && applyFilter) {
        filterBtn.addEventListener("click", () => {
            filterBox.classList.toggle("hide");
        });

        cancelFilter.addEventListener("click", () => {
            filterBox.classList.add("hide");
        });

        applyFilter.addEventListener("click", () => {
            const type = document.getElementById("filterType").value;

            if (!type) {
                Swal.fire({
                    icon: "warning",
                    title: "Chưa chọn loại bài",
                    text: "Vui lòng chọn loại bài kiểm tra trước khi lọc.",
                    confirmButtonText: "OK"
                });
            } else {
                const label = type === "public" ? "Public" : "Private";
                Swal.fire({
                    icon: "success",
                    title: "Đã áp dụng bộ lọc",
                    text: "Đang lọc theo loại bài: " + label,
                    confirmButtonText: "OK"
                });
            }

            filterBox.classList.add("hide");
        });
    }

    const openJoin = document.getElementById("openJoin");
    const joinPopup = document.getElementById("joinPopup");
    const overlay = document.getElementById("overlay");
    const cancelJoin = document.getElementById("cancelJoin");
    const confirmJoin = document.getElementById("confirmJoin");
    const classCodeInput = document.getElementById("classCode");
    const joinClassForm = document.getElementById("joinClassForm");

    function openJoinPopup() {
        overlay.classList.remove("hide");
        joinPopup.classList.remove("hide");
        classCodeInput.value = "";
        classCodeInput.focus();
    }

    function closePopup() {
        overlay.classList.add("hide");
        joinPopup.classList.add("hide");
    }

    if (openJoin && overlay && joinPopup && cancelJoin && confirmJoin && classCodeInput) {
        openJoin.addEventListener("click", openJoinPopup);
        overlay.addEventListener("click", closePopup);
        cancelJoin.addEventListener("click", closePopup);

        // format code as user types
        classCodeInput.addEventListener("input", () => {
            const v = classCodeInput.value || "";
            classCodeInput.value = v.toUpperCase().replace(/\s+/g, "");
        });

        // ESC closes popup
        document.addEventListener("keydown", (e) => {
            if (e.key === "Escape" && !overlay.classList.contains("hide")) {
                closePopup();
            }
        });

        if (joinClassForm) {
            joinClassForm.addEventListener("submit", (e) => {
                const code = classCodeInput.value.trim();
                if (code === "") {
                    e.preventDefault();
                    Swal.fire({
                        icon: "warning",
                        title: "Thiếu mã lớp",
                        text: "Vui lòng nhập mã lớp trước khi tham gia.",
                        confirmButtonText: "Đã hiểu"
                    });
                    return;
                }
                // submit bình thường để backend xử lý + flash message
                confirmJoin.disabled = true;
            });
        }
    }

    // Copy class code chip
    document.addEventListener("click", async (e) => {
        const btn = e.target?.closest?.(".mychip--copy");
        if (!btn) return;
        const code = (btn.getAttribute("data-code") || "").trim();
        if (!code) return;
        try {
            await navigator.clipboard.writeText(code);
            Swal.fire({
                icon: "success",
                title: "Đã copy mã lớp",
                text: code,
                timer: 1200,
                showConfirmButton: false
            });
        } catch (err) {
            Swal.fire({
                icon: "info",
                title: "Không thể copy tự động",
                text: "Bạn hãy bôi đen và copy: " + code,
                confirmButtonText: "OK"
            });
        }
    });

});


// ====== SEARCH CLEAR BUTTON ======
const searchInput = document.getElementById("searchInput");
const clearSearchBtn = document.getElementById("clearSearchBtn");
const searchWrapper = document.querySelector(".search-wrapper");

if (searchInput && clearSearchBtn && searchWrapper) {
  const toggleClearBtn = () => {
    if (searchInput.value.trim() !== "") {
      searchWrapper.classList.add("has-value");
    } else {
      searchWrapper.classList.remove("has-value");
    }
  };

  // hiện / ẩn nút X khi gõ
  searchInput.addEventListener("input", toggleClearBtn);

  // click X để xóa text
  clearSearchBtn.addEventListener("click", () => {
    searchInput.value = "";
    searchInput.focus();
    toggleClearBtn();
    // TODO: nếu bạn có logic filter, có thể gọi lại hàm load dữ liệu tại đây
  });

  // khởi tạo
  toggleClearBtn();
}
