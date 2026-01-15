/* ========================= TABS ========================= */
const tabs = document.querySelectorAll('.tabs .tab');
// Use section-block containers with data-section attributes
const sections = document.querySelectorAll('.page-container .section-block');

function showOnly(targetKey) {
  // toggle active class on tabs
  tabs.forEach(t => {
    t.classList.toggle('active', t.getAttribute('data-target') === targetKey);
  });
  // show only matching section by class
  sections.forEach(sec => {
    if (sec.classList.contains('section-' + targetKey)) {
      sec.style.display = '';
    } else {
      sec.style.display = 'none';
    }
  });
}

// Map tab clicks to target keys via data-target
tabs.forEach(tab => {
  tab.addEventListener('click', () => {
    const key = tab.getAttribute('data-target');
    showOnly(key);
  });
});

// Initial view: based on the tab already marked active (server may set), fallback to first tab
(function initView(){
  const activeTab = document.querySelector('.tabs .tab.active');
  const key = activeTab ? activeTab.getAttribute('data-target') : (tabs[0] ? tabs[0].getAttribute('data-target') : null);
  if (key) showOnly(key);
})();


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

/* ===================== CATEGORY MODAL ===================== */
(function(){
  const modal = document.getElementById('addCategoryModal');
  const openBtn = document.getElementById('openAddCategoryModal');
  const closeBtn = document.getElementById('closeCategoryModal');
  const cancelBtn = document.getElementById('cancelCategoryBtn');

  function open(){ if(modal){ modal.classList.add('show'); modal.style.display='flex'; } }
  function close(){ if(modal){ modal.classList.remove('show'); modal.style.display=''; } }

  if(openBtn) openBtn.addEventListener('click', open);
  if(closeBtn) closeBtn.addEventListener('click', close);
  if(cancelBtn) cancelBtn.addEventListener('click', close);
  window.addEventListener('click', (e)=>{ if(e.target===modal){ close(); } });
})();

/* ===================== CLASS MODAL ===================== */
(function(){
  const modal = document.getElementById('addClassModal');
  const openBtn = document.getElementById('openAddClassModal');
  const closeBtn = document.getElementById('closeClassModal');
  const cancelBtn = document.getElementById('cancelClassBtn');

  function open(){ if(modal){ modal.classList.add('show'); modal.style.display='flex'; } }
  function close(){ if(modal){ modal.classList.remove('show'); modal.style.display=''; } }

  if(openBtn) openBtn.addEventListener('click', open);
  if(closeBtn) closeBtn.addEventListener('click', close);
  if(cancelBtn) cancelBtn.addEventListener('click', close);
  window.addEventListener('click', (e)=>{ if(e.target===modal){ close(); } });
})();

/* ===================== ONLINE SCHEDULE MODAL ===================== */
(function(){
  const modal = document.getElementById('addScheduleModal');
  const openBtn = document.getElementById('openAddScheduleModal');
  const closeBtn = document.getElementById('closeScheduleModal');
  const cancelBtn = document.getElementById('cancelScheduleBtn');

  function open(){ if(modal){ modal.classList.add('show'); modal.style.display='flex'; } }
  function close(){ if(modal){ modal.classList.remove('show'); modal.style.display=''; } }

  if(openBtn) openBtn.addEventListener('click', open);
  if(closeBtn) closeBtn.addEventListener('click', close);
  if(cancelBtn) cancelBtn.addEventListener('click', close);
  window.addEventListener('click', (e)=>{ if(e.target===modal){ close(); } });

  // Set minimum date to today for schedule dates
  const today = new Date().toISOString().split('T')[0];
  const startDateInput = document.getElementById('scheduleStartDate');
  const endDateInput = document.getElementById('scheduleEndDate');
  if(startDateInput) startDateInput.min = today;
  if(endDateInput) endDateInput.min = today;
})();

