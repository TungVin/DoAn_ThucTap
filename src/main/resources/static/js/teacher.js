/* ========================= TABS ========================= */
const tabs = document.querySelectorAll(".tab");
tabs.forEach(tab => {
    tab.addEventListener("click", () => {
        tabs.forEach(t => t.classList.remove("active"));
        tab.classList.add("active");
    });
});


/* ========================= OPEN & CLOSE MODAL ========================= */
const createModal = document.getElementById("createQuizModal");
const openBtn = document.querySelector(".create-btn");
const closeBtn = document.getElementById("closeModal");

// mở modal
if (openBtn) {
    openBtn.addEventListener("click", () => {
        createModal.classList.add("show");
    });
}

// đóng modal
if (closeBtn) {
    closeBtn.addEventListener("click", () => {
        createModal.classList.remove("show");
    });
}

// Click ra ngoài → đóng modal
window.addEventListener("click", (e) => {
    if (e.target === createModal) {
        createModal.classList.remove("show");
    }
});


/* ========================= MODAL TABS ========================= */
document.querySelectorAll(".modal-tab").forEach(tab => {
    tab.addEventListener("click", () => {
        document.querySelectorAll(".modal-tab").forEach(t => t.classList.remove("active"));
        tab.classList.add("active");
    });
});


/* ========================= SWITCH LOGIC (ON/OFF) ========================= */

// 1) Thời gian làm bài
const timeLimitToggle = document.getElementById("timeLimitToggle");
const timeLimitInput = document.getElementById("timeLimitInput");

timeLimitToggle.addEventListener("change", () => {
    timeLimitInput.disabled = !timeLimitToggle.checked;
    if (!timeLimitToggle.checked) {
        timeLimitInput.value = "";
    }
});

// 2) Thời gian bắt đầu làm bài
const startToggle = document.getElementById("startToggle");
const startTime = document.getElementById("startTime");
const startDate = document.getElementById("startDate");

startToggle.addEventListener("change", () => {
    const enable = startToggle.checked;
    startTime.disabled = !enable;
    startDate.disabled = !enable;

    if (!enable) {
        startTime.value = "";
        startDate.value = "";
    }
});

// 3) Thời gian kết thúc làm bài
const endToggle = document.getElementById("endToggle");
const endTime = document.getElementById("endTime");
const endDate = document.getElementById("endDate");

endToggle.addEventListener("change", () => {
    const enable = endToggle.checked;
    endTime.disabled = !enable;
    endDate.disabled = !enable;

    if (!enable) {
        endTime.value = "";
        endDate.value = "";
    }
});


/* ========================= AUTO SET MIN DATE (KHÔNG CHO CHỌN QUÁ KHỨ) ========================= */

const today = new Date().toISOString().split("T")[0]; // yyyy-mm-dd

if (startDate) startDate.min = today;
if (endDate) endDate.min = today;


/* ========================= AUTO VALIDATE (nếu bạn muốn nâng cấp thêm) ========================= */
/*
startDate.addEventListener("change", () => {
    endDate.min = startDate.value;
});
*/
