<script setup>
import { ref } from 'vue';

const emit = defineEmits(['close', 'save']);

// 確保月份和日期始終是兩位數的格式化函數
const getFormattedDate = (date) => {
  const year = date.getFullYear();
  // 月份從 0 開始，所以要加 1
  const month = String(date.getMonth() + 1).padStart(2, '0'); // 補零，例如 7 -> 07
  const day = String(date.getDate()).padStart(2, '0');     // 補零，例如 6 -> 06
  return `${year}-${month}-${day}`;
};

const today = new Date(); // 取得當前日期


const specialHoursList = ref({
  recordType: 'specific',
  specificDate:  getFormattedDate(today),
  startDate:  getFormattedDate(today),
  endDate: '',
  isClose: false,
  openTime: '08:00',
  closeTime: '17:00',
});



// 時間選項（分鐘，以 15 分鐘為間隔，用於下拉選單）
const timeOptions = ref([]);
const generateTimeOptions = () => {
  const options = [];
  for (let h = 0; h < 24; h++) {
    for (let m = 0; m < 60; m += 15) {
      const hour = String(h).padStart(2, '0');
      const minute = String(m).padStart(2, '0');
      options.push(`${hour}:${minute}`);
    }
  }
  return options;
};
timeOptions.value = generateTimeOptions();

// 保存更改
const saveChanges = () => {
  console.log(specialHoursList.value)
  let newRecord = {};
  if (specialHoursList.value.recordType === 'specific') {
    if (!specialHoursList.value.specificDate) {
      alert('請選擇特定日期！'); // 使用自定義提示框替代
      return;
    }
 
  } else {
    if (!specialHoursList.value.startDate || !specialHoursList.value.endDate) {
      alert('請選擇日期範圍！'); // 使用自定義提示框替代
      return;
    }

  }
  emit('save', specialHoursList.value); // 觸發保存事件，並將新記錄傳遞給父組件
};
</script>

<template>
  <div class="h-100 d-flex flex-column p-4">
    <!-- 頂部標題和關閉按鈕 -->
    <div class="d-flex justify-content-between align-items-center mb-4">
      <h4 class="mb-0 fw-bold">新增特殊營業時間</h4>
      <button class="btn-close" @click="emit('close')"></button>
    </div>
    <hr>

    <!-- 可滾動內容區域 -->
    <div class="flex-grow-1 overflow-auto pe-2 py-3">
      <!-- 日期類型選擇 -->
      <div class="btn-group w-100 mb-4" role="group">
        <input type="radio" class="btn-check" name="recordType" id="specificDay" value="specific" v-model="specialHoursList.recordType" autocomplete="off">
        <label class="btn btn-outline-primary" for="specificDay">單日</label>

        <input type="radio" class="btn-check" name="recordType" id="multipleDays" value="range" v-model="specialHoursList.recordType" autocomplete="off">
        <label class="btn btn-outline-primary" for="multipleDays">多日</label>
      </div>

      <!-- 日期輸入 -->
      <div class="mb-4">
        <label for="dateInput" class="form-label text-muted">日期</label>
        <input
          v-if="specialHoursList.recordType === 'specific'"
          type="date"
          class="form-control"
          id="dateInput"
          v-model="specialHoursList.specificDate"
        >
        <div v-else class="d-flex align-items-center gap-2">
          <input type="date" class="form-control" v-model="specialHoursList.startDate">
          <span class="text-muted">至</span>
          <input type="date" class="form-control" v-model="specialHoursList.endDate">
        </div>
      </div>

      <!-- 全日關閉開關 -->
      <div class="form-check form-switch mb-4">
        <input class="form-check-input" type="checkbox" id="closedAllDay" v-model="specialHoursList.isClose">
        <label class="form-check-label" for="closedAllDay">全日關閉</label>
      </div>

      <!-- 營業時間段 -->
      <div v-if="specialHoursList.isClose" class="mb-4 p-3 border rounded-3 bg-light">
        <h6 class="mb-3 fw-bold">營業時段</h6>
        <div class="d-flex align-items-center gap-2 mb-3">
          <select class="form-select form-select-sm" v-model="specialHoursList.openTime">
            <option v-for="option in timeOptions" :key="option" :value="option">{{ option }}</option>
          </select>
          <span class="text-muted">至</span>
          <select class="form-select form-select-sm" v-model="specialHoursList.closeTime">
            <option v-for="option in timeOptions" :key="option" :value="option">{{ option }}</option>
          </select>
        </div>
        <!-- <button class="btn btn-sm btn-outline-primary w-100 py-2">
          <i class="fas fa-plus me-2"></i>新增時段
        </button> -->
      </div>

      <p class="text-muted small">
        <i class="fas fa-info-circle me-2"></i>
        請勿下方重複您設定的營業時段，此營業時段將會覆蓋您所選擇的日期中的日期。
      </p>
    </div>

    <!-- 底部操作按鈕 -->
    <div class="mt-auto pt-3 border-top d-flex justify-content-end">
      <button class="btn btn-primary  py-2 px-4" @click="saveChanges">儲存</button>
    </div>
  </div>
</template>

<style scoped>
/* 側邊欄內部容器的基本樣式 */
.h-100.d-flex.flex-column {
  background-color: white;
}

/* 時間選擇器調整 */
.form-select-sm {
  max-width: 120px; /* 限制下拉選單寬度 */
}
</style>
