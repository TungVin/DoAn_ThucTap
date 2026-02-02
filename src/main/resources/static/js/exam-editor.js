// ================== CONSTANTS ==================
const CLEAR_ON_DISABLE = true;
const MAX_UPLOAD_MB = 10;
const DEFAULT_ACCEPT = "image/*,application/pdf";

// ================== UPLOAD CONFIG ==================
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

// ================== TOGGLE HELPERS ==================
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
  apply();
}

// ================== QUESTION MANAGEMENT ==================
let questionIndex = 0;

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
        addQuestion(); // luôn đảm bảo có ít nhất 1 câu
      }
    });
  }

  if (dupBtn) {
    dupBtn.addEventListener("click", () => {
      const list = document.getElementById("questionList");
      if (!list) return;

      const clone = createQuestionBlock(questionIndex++);

      // copy question
      const srcQ = block.querySelector(".question-content");
      const dstQ = clone.querySelector(".question-content");
      if (srcQ && dstQ) dstQ.value = srcQ.value;

      // copy score
      const srcScore = block.querySelector(".score-input");
      const dstScore = clone.querySelector(".score-input");
      if (srcScore && dstScore) dstScore.value = srcScore.value;

      // copy answers + attachment urls + correct radio
      const srcRows = block.querySelectorAll(".answer-row");
      const dstRows = clone.querySelectorAll(".answer-row");

      // nếu câu gốc > 4 đáp án => thêm row cho clone
      const dstAnswersContainer = clone.querySelector(".answers");
      if (dstAnswersContainer && srcRows.length > dstRows.length) {
        for (let i = dstRows.length; i < srcRows.length; i++) {
          const row = document.createElement("div");
          row.innerHTML = buildAnswerRowHtml(clone.dataset.index);
          dstAnswersContainer.appendChild(row.firstElementChild);
        }
      }

      const newDstRows = clone.querySelectorAll(".answer-row");

      let checkedIndex = -1;
      srcRows.forEach((row, i) => {
        const radio = row.querySelector('input[type="radio"]');
        if (radio && radio.checked) checkedIndex = i;

        const a = row.querySelector(".answer-content")?.value || "";
        const u = row.querySelector(".answer-attachment-url")?.value || "";

        if (newDstRows[i]) {
          const dstA = newDstRows[i].querySelector(".answer-content");
          const dstU = newDstRows[i].querySelector(".answer-attachment-url");
          if (dstA) dstA.value = a;
          if (dstU) dstU.value = u;
        }
      });

      if (checkedIndex >= 0 && newDstRows[checkedIndex]) {
        const dstRadio = newDstRows[checkedIndex].querySelector('input[type="radio"]');
        if (dstRadio) dstRadio.checked = true;
      }

      list.appendChild(clone);
    });
  }
}

function addQuestion() {
  const list = document.getElementById("questionList");
  if (!list) return;
  const qb = createQuestionBlock(questionIndex++);
  list.appendChild(qb);
}

// ================== RENDER EXISTING QUESTIONS (EDIT MODE) ==================
function parseExistingQuestions() {
  const raw = window.__EXAM_QUESTIONS_JSON__;

  // trường hợp đã là mảng/object
  if (Array.isArray(raw)) return raw;

  // trường hợp là string JSON
  if (typeof raw === "string") {
    const s = raw.trim();
    if (!s) return [];
    try {
      return JSON.parse(s);
    } catch (e) {
      console.warn("Cannot parse __EXAM_QUESTIONS_JSON__", e);
      return [];
    }
  }

  return [];
}

function renderQuestionsFromServer(questions) {
  const list = document.getElementById("questionList");
  if (!list) return;

  list.innerHTML = "";
  questionIndex = 0;

  questions.forEach((q, qPos) => {
    const block = createQuestionBlock(questionIndex++);
    const qContent = block.querySelector(".question-content");
    const scoreInput = block.querySelector(".score-input");

    if (qContent) qContent.value = q?.content || "";
    if (scoreInput) scoreInput.value = (q?.score ?? 10);

    const answersContainer = block.querySelector(".answers");
    const existingAnswers = Array.isArray(q?.answers) ? q.answers : [];

    // nếu server trả >4 đáp án thì add thêm row
    if (answersContainer) {
      const curRows = answersContainer.querySelectorAll(".answer-row").length;
      if (existingAnswers.length > curRows) {
        for (let i = curRows; i < existingAnswers.length; i++) {
          const row = document.createElement("div");
          row.innerHTML = buildAnswerRowHtml(block.dataset.index);
          answersContainer.appendChild(row.firstElementChild);
        }
      }
    }

    const rows = block.querySelectorAll(".answer-row");

    existingAnswers.forEach((a, i) => {
      if (!rows[i]) return;
      const aInput = rows[i].querySelector(".answer-content");
      const uInput = rows[i].querySelector(".answer-attachment-url");
      if (aInput) aInput.value = a?.content || "";
      if (uInput) uInput.value = a?.attachmentUrl || "";
    });

    const ci = (q?.correctIndex ?? -1);
    if (ci >= 0 && rows[ci]) {
      const radio = rows[ci].querySelector('input[type="radio"]');
      if (radio) radio.checked = true;
    }

    list.appendChild(block);
  });

  // nếu trống => add 1 câu trống
  if (list.children.length === 0) addQuestion();
}

// ================== UPLOAD HANDLING (EVENT DELEGATION) ==================
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

// ================== COLLECT QUESTIONS -> JSON ==================
function collectQuestionsToJson() {
  const list = document.getElementById("questionList");
  if (!list) return "[]";

  const blocks = Array.from(list.querySelectorAll(".question-block"));

  const questions = blocks.map((b, idx) => {
    const content = b.querySelector(".question-content")?.value?.trim() || "";
    const score = parseFloat(b.querySelector(".score-input")?.value || "10") || 10;

    const answerRows = Array.from(b.querySelectorAll(".answer-row"));
    const answers = answerRows.map((row) => ({
      content: row.querySelector(".answer-content")?.value?.trim() || "",
      attachmentUrl: row.querySelector(".answer-attachment-url")?.value || ""
    }));

    let correctIndex = -1;
    answerRows.forEach((row, i) => {
      const radio = row.querySelector('input[type="radio"]');
      if (radio && radio.checked) correctIndex = i;
    });

    return {
      orderIndex: idx,
      type: "single_choice",
      content,
      score,
      correctIndex: correctIndex >= 0 ? correctIndex : null,
      answers
    };
  });

  return JSON.stringify(questions);
}

// ================== UTIL: COPY TEXT ==================
async function copyText(text) {
  if (!text) return;
  try {
    await navigator.clipboard.writeText(text);
    alert("Đã sao chép!");
  } catch {
    const ta = document.createElement("textarea");
    ta.value = text;
    document.body.appendChild(ta);
    ta.select();
    document.execCommand("copy");
    document.body.removeChild(ta);
    alert("Đã sao chép!");
  }
}

// ================== DOM READY ==================
document.addEventListener("DOMContentLoaded", () => {
  // toggles
  bindToggle("timeLimitEnabled", ["timeLimit"]);
  bindToggle("startEnabled", ["startDate", "startTime"]);
  bindToggle("endEnabled", ["endDate", "endTime"]);

  // min date
  const today = new Date().toISOString().split("T")[0];
  const sd = document.getElementById("startDate");
  const ed = document.getElementById("endDate");
  if (sd) sd.min = today;
  if (ed) ed.min = today;

  // copy share link (hỗ trợ cả id cũ và mới)
  const oldCopyBtn = document.getElementById("copyLinkBtn");
  const shareBtn = document.getElementById("copyShareLinkBtn");
  const publicBtn = document.getElementById("copyPublicLinkBtn");

  if (oldCopyBtn) {
    const inp = document.getElementById("shareLink");
    oldCopyBtn.addEventListener("click", () => copyText(inp?.value || ""));
  }
  if (shareBtn) {
    const inp = document.getElementById("shareLink");
    shareBtn.addEventListener("click", () => copyText(inp?.value || ""));
  }
  if (publicBtn) {
    const inp = document.getElementById("publicLink");
    publicBtn.addEventListener("click", () => copyText(inp?.value || ""));
  }

  // questions init: EDIT -> render from server, CREATE -> add 1 empty
  const qList = document.getElementById("questionList");
  const addQBtn = document.getElementById("addQuestionBtn");
  if (qList && addQBtn) {
    addQBtn.addEventListener("click", () => addQuestion());

    const existing = parseExistingQuestions();
    if (Array.isArray(existing) && existing.length > 0) {
      renderQuestionsFromServer(existing);
    } else {
      addQuestion();
    }
  }

  // submit => set hidden json
  const form = document.querySelector("form");
  if (form) {
    form.addEventListener("submit", () => {
      const hidden = document.getElementById("questionsJson");
      if (hidden) hidden.value = collectQuestionsToJson();
    });
  }

  // ===== Group modal (null-safe) =====
  const addGroupBtn = document.getElementById("addGroupBtn");
  const addGroupModal = document.getElementById("addGroupModal");
  const closeGroupModalBtn = document.getElementById("closeGroupModalBtn");
  const closeGroupModalFooterBtn = document.getElementById("closeGroupModalFooterBtn");
  const addGroupSubmitBtn = document.getElementById("addGroupSubmitBtn");
  const classSelect = document.getElementById("classSelect");

  if (addGroupBtn && addGroupModal) {
    addGroupBtn.addEventListener("click", () => {
      addGroupModal.style.display = "block";
    });
  }
  if (closeGroupModalBtn && addGroupModal) {
    closeGroupModalBtn.addEventListener("click", () => {
      addGroupModal.style.display = "none";
    });
  }
  if (closeGroupModalFooterBtn && addGroupModal) {
    closeGroupModalFooterBtn.addEventListener("click", () => {
      addGroupModal.style.display = "none";
    });
  }
  if (addGroupSubmitBtn) {
    addGroupSubmitBtn.addEventListener("click", () => {
      const selectedClass = classSelect?.value;
      if (!selectedClass) {
        alert("Vui lòng chọn lớp!");
        return;
      }
      const groupList = document.getElementById("groupList");
      if (!groupList) return;

      const groupItem = document.createElement("p");
      groupItem.textContent = `Nhóm: ${selectedClass}`;
      groupList.appendChild(groupItem);

      if (addGroupModal) addGroupModal.style.display = "none";
    });
  }

  // ===== Public checkbox show/hide (không tạo random link) =====
  const publicCheckbox = document.getElementById("publicCheckbox");
  const publicLinkSection = document.getElementById("publicLinkSection");
  const addNewContainer = document.getElementById("addNewContainer");

  const applyPublicUI = () => {
    const isPublic = !!publicCheckbox?.checked;
    if (publicLinkSection) publicLinkSection.style.display = isPublic ? "block" : "none";
    if (addNewContainer) addNewContainer.style.display = isPublic ? "none" : "block";
  };

  if (publicCheckbox) {
    publicCheckbox.addEventListener("change", applyPublicUI);
    applyPublicUI();
  }
});
