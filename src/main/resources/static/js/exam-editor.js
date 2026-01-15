// Nếu bạn muốn tắt toggle là xoá luôn dữ liệu -> true
// Nếu muốn tắt toggle chỉ disable nhưng giữ value (khi bật lại vẫn còn) -> false
const CLEAR_ON_DISABLE = true;

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
  apply(); // init state khi load
}

// ================== PHẦN 1: CÂU HỎI ==================

let questionIndex = 0;

function createQuestionBlock(index) {
  const wrapper = document.createElement("div");
  wrapper.className = "question-block";
  wrapper.dataset.index = index;

  let answersHtml = "";
  for (let i = 0; i < 4; i++) {
    answersHtml += `
      <div class="answer-row">
        <label class="answer-radio">
          <input type="radio" name="q${index}-correct">
        </label>
        <input class="input answer-content" placeholder="Câu trả lời...">
        <button type="button" class="icon-btn upload-btn" title="Đính kèm tệp">
          📤
        </button>
      </div>
    `;
  }

  wrapper.innerHTML = `
    <div class="question-inner">
      <div class="question-header-row">
        <div class="question-title">
          Nhập nội dung câu hỏi ?
        </div>
        <div class="question-type">
          <span class="badge">Chọn 1 kết quả</span>
        </div>
      </div>

      <textarea class="textarea question-content"
                placeholder="Nhập nội dung câu hỏi..."></textarea>

      <div class="answers">
        ${answersHtml}
      </div>

      <button type="button" class="btn small" data-role="add-answer">
        Thêm câu trả lời +
      </button>

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
      row.className = "answer-row";
      row.innerHTML = `
        <label class="answer-radio">
          <input type="radio" name="q${idx}-correct">
        </label>
        <input class="input answer-content" placeholder="Câu trả lời...">
        <button type="button" class="icon-btn upload-btn" title="Đính kèm tệp">
          📤
        </button>
      `;
      answersContainer.appendChild(row);
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

// ================== DOM READY ==================

document.addEventListener("DOMContentLoaded", () => {
  // Toggle thời gian
  bindToggle("timeLimitEnabled", ["timeLimit"]);
  bindToggle("startEnabled", ["startDate", "startTime"]);
  bindToggle("endEnabled", ["endDate", "endTime"]);

  // Set min date = today
  const today = new Date().toISOString().split("T")[0];
  const sd = document.getElementById("startDate");
  const ed = document.getElementById("endDate");
  if (sd) sd.min = today;
  if (ed) ed.min = today;

  // Copy share link (chỉ mode edit)
  const btn = document.getElementById("copyLinkBtn");
  const inp = document.getElementById("shareLink");
  if (btn && inp) {
    btn.addEventListener("click", async () => {
      try {
        await navigator.clipboard.writeText(inp.value);
        const old = btn.textContent;
        btn.textContent = "Đã copy";
        setTimeout(() => (btn.textContent = old || "Copy"), 1200);
      } catch {
        inp.focus();
        inp.select();
        document.execCommand("copy");
      }
    });
  }

  // Hệ thống tự động chia điểm -> enable/disable ô điểm tối đa
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

  // Khởi tạo Phần 1
  const qList = document.getElementById("questionList");
  const addQBtn = document.getElementById("addQuestionBtn");
  if (qList && addQBtn) {
    addQBtn.addEventListener("click", () => addQuestion());
    addQuestion(); // luôn có sẵn 1 câu
  }
});
