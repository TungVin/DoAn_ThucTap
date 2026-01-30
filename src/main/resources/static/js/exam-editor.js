// Constants
const CLEAR_ON_DISABLE = true;
const MAX_UPLOAD_MB = 10;
const DEFAULT_ACCEPT = "image/*,application/pdf"; // Update if needed

// Upload Configuration
function getUploadUrl() {
  const form = document.querySelector("form[data-upload-url]");
  return form?.dataset?.uploadUrl || "/api/uploads";
}

function getCsrfHeaders() {
  const csrf = document.querySelector('meta[name="_csrf"]')?.content;
  const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.content;
  if (csrf && csrfHeader) return { [csrfHeader]: csrf };
  return {};
}

async function uploadFileToServer(file) {
  const fd = new FormData();
  fd.append("file", file);

  const res = await fetch(getUploadUrl(), {
    method: "POST",
    body: fd,
    headers: { ...getCsrfHeaders() }
  });

  if (!res.ok) throw new Error("Upload failed");
  return await res.json(); // {url, originalName ...}
}

// Toggle functionality to disable/enable input fields
function bindToggle(cbId, inputIds) {
  const cb = document.getElementById(cbId);
  if (!cb) return;

  const inputs = inputIds
    .map(id => document.getElementById(id))
    .filter(Boolean);

  const apply = () => {
    const on = cb.checked;

    inputs.forEach(i => {
      i.disabled = !on;
      i.classList.toggle("is-disabled", !on);

      if (!on && CLEAR_ON_DISABLE) {
        i.value = "";
      }
    });
  };

  cb.addEventListener("change", apply);
  apply(); // Initial state
}

// ================== QUESTION MANAGEMENT ==================
let questionIndex = 0;

// Function to create HTML for each answer row
function buildAnswerRowHtml(qIdx) {
  return `
    <div class="answer-row">
      <label class="answer-radio">
        <input type="radio" name="q${qIdx}-correct">
      </label>
      <input class="input answer-content" placeholder="Câu trả lời...">
      <input type="hidden" class="answer-attachment-url" value="">
      <input type="file" class="answer-file" accept="${DEFAULT_ACCEPT}" hidden>
      <button type="button" class="icon-btn upload-btn" title="Đính kèm tệp">📤</button>
      <span class="upload-status muted small"></span>
    </div>
  `;
}

// Function to create a question block
function createQuestionBlock(index) {
  const wrapper = document.createElement("div");
  wrapper.className = "question-block";
  wrapper.dataset.index = index;

  let answersHtml = "";
  for (let i = 0; i < 4; i++) {
    answersHtml += buildAnswerRowHtml(index);
  }

  wrapper.innerHTML = `
    <div class="question-inner">
      <div class="question-header-row">
        <div class="question-title">Nhập nội dung câu hỏi ?</div>
        <div class="question-type">
          <span class="badge">Chọn 1 kết quả</span>
        </div>
      </div>
      <textarea class="textarea question-content" placeholder="Nhập nội dung câu hỏi..."></textarea>
      <div class="answers">${answersHtml}</div>
      <button type="button" class="btn small" data-role="add-answer">Thêm câu trả lời +</button>
      <div class="question-footer">
        <div class="question-score">
          <span>Điểm</span>
          <input class="input score-input" type="number" min="0" step="0.25" value="10">
        </div>
        <div class="question-actions">
          <button type="button" class="icon-btn" data-role="duplicate-question" title="Nhân bản câu hỏi">📄</button>
          <button type="button" class="icon-btn danger" data-role="delete-question" title="Xoá câu hỏi">🗑</button>
        </div>
      </div>
    </div>
  `;

  wireQuestionBlock(wrapper);
  return wrapper;
}

// Add event listeners to the question block
function wireQuestionBlock(block) {
  const addAnswerBtn = block.querySelector('[data-role="add-answer"]');
  const answersContainer = block.querySelector(".answers");

  if (addAnswerBtn && answersContainer) {
    addAnswerBtn.addEventListener("click", () => {
      const idx = block.dataset.index;
      const row = document.createElement("div");
      row.innerHTML = buildAnswerRowHtml(idx);
      answersContainer.appendChild(row.firstElementChild);
    });
  }

  const deleteBtn = block.querySelector('[data-role="delete-question"]');
  const dupBtn = block.querySelector('[data-role="duplicate-question"]');

  if (deleteBtn) {
    deleteBtn.addEventListener("click", () => {
      const list = document.getElementById("questionList");
      if (!list) return;
      list.removeChild(block);
      if (list.children.length === 0) {
        addQuestion();
      }
    });
  }

  if (dupBtn) {
    dupBtn.addEventListener("click", () => {
      const list = document.getElementById("questionList");
      if (!list) return;
      const clone = createQuestionBlock(++questionIndex);

      const srcQ = block.querySelector(".question-content");
      const dstQ = clone.querySelector(".question-content");
      if (srcQ && dstQ) dstQ.value = srcQ.value;

      const srcAnswers = block.querySelectorAll(".answer-content");
      const dstAnswers = clone.querySelectorAll(".answer-content");
      srcAnswers.forEach((a, i) => {
        if (dstAnswers[i]) dstAnswers[i].value = a.value;
      });

      const srcUrls = block.querySelectorAll(".answer-attachment-url");
      const dstUrls = clone.querySelectorAll(".answer-attachment-url");
      srcUrls.forEach((u, i) => {
        if (dstUrls[i]) dstUrls[i].value = u.value;
      });

      list.appendChild(clone);
    });
  }
}

// Add a new question to the list
function addQuestion() {
  const list = document.getElementById("questionList");
  if (!list) return;
  const qb = createQuestionBlock(questionIndex++);
  list.appendChild(qb);
}

// ================== UPLOAD HANDLING ==================
document.addEventListener("click", (e) => {
  const btn = e.target.closest(".upload-btn");
  if (!btn) return;

  const row = btn.closest(".answer-row");
  if (!row) return;

  const fileInput = row.querySelector(".answer-file");
  if (!fileInput) return;
  fileInput.click();
});

document.addEventListener("change", async (e) => {
  const fileInput = e.target.closest(".answer-file");
  if (!fileInput) return;

  const row = fileInput.closest(".answer-row");
  if (!row) return;

  const statusEl = row.querySelector(".upload-status");
  const hiddenUrl = row.querySelector(".answer-attachment-url");
  const btn = row.querySelector(".upload-btn");

  const file = fileInput.files?.[0];
  if (!file) return;

  // validate size
  if (file.size > MAX_UPLOAD_MB * 1024 * 1024) {
    if (statusEl) statusEl.textContent = `File quá lớn (>${MAX_UPLOAD_MB}MB)`;
    fileInput.value = "";
    return;
  }

  try {
    if (statusEl) statusEl.textContent = "Đang tải...";
    if (btn) btn.disabled = true;

    const data = await uploadFileToServer(file);
    if (!data?.url) throw new Error("Server không trả về url");

    if (hiddenUrl) hiddenUrl.value = data.url;
    if (statusEl) statusEl.textContent = `Đã tải: ${data.originalName || file.name}`;
  } catch (err) {
    console.error(err);
    if (statusEl) statusEl.textContent = "Tải lên thất bại!";
    if (hiddenUrl) hiddenUrl.value = "";
  } finally {
    if (btn) btn.disabled = false;
    fileInput.value = "";
  }
});

// ================== DOM READY ==================
document.addEventListener("DOMContentLoaded", () => {
  bindToggle("timeLimitEnabled", ["timeLimit"]);
  bindToggle("startEnabled", ["startDate", "startTime"]);
  bindToggle("endEnabled", ["endDate", "endTime"]);

  const today = new Date().toISOString().split("T")[0];
  const sd = document.getElementById("startDate");
  const ed = document.getElementById("endDate");
  if (sd) sd.min = today;
  if (ed) ed.min = today;

  const btn = document.getElementById("copyLinkBtn");
  const inp = document.getElementById("shareLink");
  if (btn && inp) {
    btn.addEventListener("click", async () => {
      try {
        await navigator.clipboard.writeText(inp.value);
        const old = btn.textContent;
        btn.textContent = "Đã sao chép";
        setTimeout(() => (btn.textContent = old || "Sao chép"), 1200);
      } catch {
        inp.focus();
        inp.select();
        document.execCommand("copy");
      }
    });
  }

  const autoCb = document.getElementById("autoDivideScore");
  const maxScoreInput = document.getElementById("maxScore");

  if (autoCb && maxScoreInput) {
    const applyAuto = () => {
      const on = autoCb.checked;
      maxScoreInput.disabled = !on;
      maxScoreInput.classList.toggle("is-disabled", !on);
    };
    autoCb.addEventListener("change", applyAuto);
    applyAuto();
  }

  const qList = document.getElementById("questionList");
  const addQBtn = document.getElementById("addQuestionBtn");
  if (qList && addQBtn) {
    addQBtn.addEventListener("click", () => addQuestion());
    addQuestion(); // always add one question initially
  }

  // Thêm Nhóm
  const addGroupBtn = document.getElementById("addGroupBtn");
  const addGroupModal = document.getElementById("addGroupModal");
  const closeGroupModalBtn = document.getElementById("closeGroupModalBtn");
  const closeGroupModalFooterBtn = document.getElementById("closeGroupModalFooterBtn");
  const addGroupSubmitBtn = document.getElementById("addGroupSubmitBtn");
  const classSelect = document.getElementById("classSelect");

  // Mở modal khi nhấn "Thêm Nhóm"
  addGroupBtn.addEventListener("click", () => {
    addGroupModal.style.display = "block";
  });

  // Đóng modal khi nhấn "X"
  closeGroupModalBtn.addEventListener("click", () => {
    addGroupModal.style.display = "none";
  });

  // Đóng modal khi nhấn "Hủy"
  closeGroupModalFooterBtn.addEventListener("click", () => {
    addGroupModal.style.display = "none";
  });

  // Xử lý khi nhấn "Thêm Nhóm"
  addGroupSubmitBtn.addEventListener("click", () => {
    const selectedClass = classSelect.value;
    if (selectedClass) {
      const groupList = document.getElementById("groupList");
      const groupItem = document.createElement("p");
      groupItem.textContent = `Nhóm: ${selectedClass}`;
      groupList.appendChild(groupItem);
      addGroupModal.style.display = "none"; // Đóng modal
    } else {
      alert("Vui lòng chọn lớp!");
    }
  });

  // Hiển thị link công khai khi checkbox "Công khai" được tick
  const publicCheckbox = document.getElementById("publicCheckbox");
  const publicLinkSection = document.getElementById("publicLinkSection");

  publicCheckbox.addEventListener("change", function () {
    if (publicCheckbox.checked) {
      const randomLink = generateRandomLink();
      document.getElementById("publicLink").value = randomLink; // Gán link ngẫu nhiên
      publicLinkSection.style.display = "block"; // Hiển thị link công khai
    } else {
      publicLinkSection.style.display = "none"; // Ẩn link công khai
    }
  });

  // Đảm bảo trạng thái ban đầu của checkbox
  if (publicCheckbox.checked) {
    const randomLink = generateRandomLink();
    document.getElementById("publicLink").value = randomLink; // Gán link ngẫu nhiên
    publicLinkSection.style.display = "block";
  } else {
    publicLinkSection.style.display = "none";
  }

  // Hàm tạo link ngẫu nhiên
  function generateRandomLink() {
    const randomStr = Math.random().toString(36).substring(2, 15);
    return `https://example.com/test/${randomStr}`;
  }
});
