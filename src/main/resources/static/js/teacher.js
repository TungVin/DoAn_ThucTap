/* ========================= TABS ========================= */
const tabs = document.querySelectorAll(".tab");
const sections = document.querySelectorAll(".page-container .section");
const onlineSection = document.getElementById("onlineLearningSection");
const quizTopAction = document.querySelector(".page-container > .top-action"); // first top-action belongs to quiz
const onlineTopAction = onlineSection ? onlineSection.querySelector(".top-action") : null;
const categoriesSection = document.getElementById("categoriesSection");
const questionLibrarySection = document.getElementById("questionLibrarySection");
const classesSection = document.getElementById("classesSection");

function showOnlyOnline() {
    // sections
    sections.forEach(sec => {
        if (sec === onlineSection) {
            sec.style.display = "";
        } else {
            sec.style.display = "none";
        }
    });
    // top-actions
    if (quizTopAction) quizTopAction.style.display = "none";
    if (onlineTopAction) onlineTopAction.style.display = "";
}

function showOnlyQuiz() {
    // show quiz sections (assumed first two .section elements are quiz lists)
    sections.forEach((sec, idx) => {
        if (idx === 0 || idx === 1) {
            sec.style.display = "";
        } else {
            sec.style.display = "none";
        }
    });
    // top-actions
    if (quizTopAction) quizTopAction.style.display = "";
    if (onlineTopAction) onlineTopAction.style.display = "none";
}

function showOnlyCategories() {
    sections.forEach(sec => {
        if (sec === categoriesSection) {
            sec.style.display = "";
        } else {
            sec.style.display = "none";
        }
    });
    if (quizTopAction) quizTopAction.style.display = "none";
    if (onlineTopAction) onlineTopAction.style.display = "none";
}

function showOnlyQuestionLibrary() {
    sections.forEach(sec => {
        if (sec === questionLibrarySection) {
            sec.style.display = "";
        } else {
            sec.style.display = "none";
        }
    });
    if (quizTopAction) quizTopAction.style.display = "none";
    if (onlineTopAction) onlineTopAction.style.display = "none";
}

function showOnlyClasses() {
    sections.forEach(sec => {
        if (sec === classesSection) {
            sec.style.display = "";
        } else {
            sec.style.display = "none";
        }
    });
    if (quizTopAction) quizTopAction.style.display = "none";
    if (onlineTopAction) onlineTopAction.style.display = "none";
}

function showAllSections() {
    sections.forEach(sec => { sec.style.display = ""; });
    if (quizTopAction) quizTopAction.style.display = "";
    if (onlineTopAction) onlineTopAction.style.display = "";
}

function applyTabViewByLabel(label) {
    if (label === "Học trực tuyến") {
        showOnlyOnline();
    } else if (label === "Bài kiểm tra") {
        showOnlyQuiz();
    } else if (label === "Danh mục") {
        showOnlyCategories();
    } else if (label === "Thư viện câu hỏi") {
        showOnlyQuestionLibrary();
    } else if (label === "Lớp") {
        showOnlyClasses();
    } else {
        showAllSections();
    }
}

tabs.forEach(tab => {
    tab.addEventListener("click", () => {
        tabs.forEach(t => t.classList.remove("active"));
        tab.classList.add("active");
        applyTabViewByLabel(tab.textContent.trim());
    });
});

// Apply initial view based on the active tab at load
const activeTab = document.querySelector(".tabs .tab.active");
if (activeTab) {
    applyTabViewByLabel(activeTab.textContent.trim());
}


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

/* ===================== QUESTION MODAL (Create Question) ===================== */
(function(){
  const qModal = document.getElementById('createQuestionModal');
  const openQBtn = document.getElementById('openCreateQuestionModal');
  const closeQBtn = document.getElementById('closeCreateQuestionModal');
  const cancelQBtn = document.getElementById('cancelCreateQuestionBtn');
  const typeSelect = document.getElementById('questionTypeSelect');
  const optionsArea = document.getElementById('questionOptionsArea');

  function openQ(){ if(qModal){ qModal.classList.add('show'); qModal.style.display='flex'; } }
  function closeQ(){ if(qModal){ qModal.classList.remove('show'); qModal.style.display=''; } }

  if(openQBtn) openQBtn.addEventListener('click', openQ);
  if(closeQBtn) closeQBtn.addEventListener('click', closeQ);
  if(cancelQBtn) cancelQBtn.addEventListener('click', closeQ);
  window.addEventListener('click', (e)=>{ if(e.target===qModal){ closeQ(); } });

  function renderTypeUI(type){
    const makeOptionRow = (idx)=>`<div class="answer"><input type="checkbox" name="correctIndexes" value="${idx}" ${type==='single_choice'?'style=\'display:none\'':''}><input class="answer-text" name="options" placeholder="Đáp án ${idx+1}"></div>`;
    if(type==='single_choice' || type==='multiple_choice'){
      optionsArea.innerHTML = `
        <div>
          <p style="margin:6px 0;color:#666">Thêm các đáp án và chọn đáp án đúng${type==='single_choice'?' (chỉ một)':''}.</p>
          <div id="optionsList">${[0,1,2,3].map(makeOptionRow).join('')}</div>
          <button type="button" class="add-answer" id="addOptionBtn">+ Thêm đáp án</button>
        </div>`;
      const optionsList = optionsArea.querySelector('#optionsList');
      const addBtn = optionsArea.querySelector('#addOptionBtn');
      let count = 4;
      addBtn.addEventListener('click', ()=>{ optionsList.insertAdjacentHTML('beforeend', makeOptionRow(count++)); });
      if(type==='single_choice'){
        optionsArea.querySelectorAll('input[type=checkbox]').forEach((cb,i)=>{
          const radio = document.createElement('input');
          radio.type='radio'; radio.name='correctIndex'; radio.value=String(i);
          cb.replaceWith(radio);
        });
      }
    } else if(type==='short_answer' || type==='best_short_answer'){
      optionsArea.innerHTML = `
        <div>
          <label>Đáp án mẫu (tuỳ chọn)</label>
          <input type="text" name="sampleAnswer" class="input" placeholder="Nhập đáp án mẫu">
        </div>`;
    } else if(type==='match_pairs'){
      optionsArea.innerHTML = `
        <div>
          <p style="margin:6px 0;color:#666">Nhập các cặp cần nối.</p>
          <div id="pairsList">
            <div class="row-2col">
              <input class="input" name="left" placeholder="Trái 1">
              <input class="input" name="right" placeholder="Phải 1">
            </div>
            <div class="row-2col">
              <input class="input" name="left" placeholder="Trái 2">
              <input class="input" name="right" placeholder="Phải 2">
            </div>
          </div>
          <button type="button" class="add-answer" id="addPairBtn">+ Thêm cặp</button>
        </div>`;
      const pairsList = optionsArea.querySelector('#pairsList');
      const addPairBtn = optionsArea.querySelector('#addPairBtn');
      let idx = 3;
      addPairBtn.addEventListener('click', ()=>{
        pairsList.insertAdjacentHTML('beforeend', `<div class="row-2col"><input class="input" name="left" placeholder="Trái ${idx}"><input class="input" name="right" placeholder="Phải ${idx}"></div>`);
        idx++;
      });
    } else if(type==='yes_no'){
      optionsArea.innerHTML = `
        <div class="answer">
            <label><input type="radio" name="yn" value="yes"> Có</label>
            <label style="margin-left:12px"><input type="radio" name="yn" value="no"> Không</label>
        </div>`;
    } else if(type==='true_false'){
      optionsArea.innerHTML = `
        <div class="answer">
            <label><input type="radio" name="tf" value="true"> Đúng</label>
            <label style="margin-left:12px"><input type="radio" name="tf" value="false"> Sai</label>
        </div>`;
    } else {
      optionsArea.innerHTML = '';
    }
  }

  if(typeSelect && optionsArea){
    renderTypeUI(typeSelect.value);
    typeSelect.addEventListener('change', ()=>renderTypeUI(typeSelect.value));
  }
})();
