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

        confirmJoin.addEventListener("click", () => {
            const code = classCodeInput.value.trim();

            if (code === "") {
                Swal.fire({
                    icon: "warning",
                    title: "Thiếu mã lớp",
                    text: "Vui lòng nhập mã lớp trước khi tham gia.",
                    confirmButtonText: "Đã hiểu"
                });
                return;
            }

            Swal.fire({
                icon: "success",
                title: "Tham gia lớp thành công",
                text: "Bạn đã gửi yêu cầu tham gia lớp với mã: " + code,
                confirmButtonText: "OK"
            }).then(() => {
                closePopup();
            });
        });
    }

});
