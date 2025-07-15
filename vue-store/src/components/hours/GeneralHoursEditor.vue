<script setup>
import { ref, onMounted, watch } from 'vue'; // 引入 watch

const props = defineProps({
  generalHours: {
    type: Array, // 改為 Array 類型
    required: true,
  },
});

const emit = defineEmits(['close', 'save']);

// 內部狀態，用於編輯營業時間
const editableHours = ref([]); // 初始化為空陣列

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

onMounted(() => {
  // 深拷貝 props，以便在子組件中修改而不影響父組件
  // 由於數據格式現在是陣列，複製方式也需要調整
  // 並且在複製時，將時間數據初始化為可選的選項，如果 'isOpen' 為 false
  editableHours.value = props.generalHours.map(hour => ({ ...hour })); // 淺拷貝物件陣列
  console.log('editableHours.value',editableHours.value)
  // 排序 editableHours，確保星期一到星期日依序顯示
  // 假設 dayOfWeek 是 'MONDAY', 'TUESDAY', ... 的字串
  // 我們需要一個映射來進行排序
  const dayOrder = {
    'MONDAY': 1, 'TUESDAY': 2, 'WEDNESDAY': 3, 'THURSDAY': 4,
    'FRIDAY': 5, 'SATURDAY': 6, 'SUNDAY': 7
  };
  editableHours.value.sort((a, b) => dayOrder[a.dayOfWeek] - dayOrder[b.dayOfWeek]);
  

  // 初始化選擇時間
  timeOptions.value = generateTimeOptions();
});

// 使用 watch 監聽 props.generalHours 變化，重新初始化 editableHours
// 這在父組件通過非 prop 更新數據時很有用
watch(() => props.generalHours, (newHours) => {
  editableHours.value = newHours.map(hour => ({ ...hour }));
  const dayOrder = {
    'MONDAY': 1, 'TUESDAY': 2, 'WEDNESDAY': 3, 'THURSDAY': 4,
    'FRIDAY': 5, 'SATURDAY': 6, 'SUNDAY': 7
  };
  editableHours.value.sort((a, b) => dayOrder[a.dayOfWeek] - dayOrder[b.dayOfWeek]);
}, { deep: true }); // deep: true 可以監聽陣列內部物件的變化

// 處理開關切換
const toggleDayStatus = (item) => {
  console.log('切換開關前',item.isOpen)

  console.log('切換開關後',item.isOpen)
  if (!item.isOpen) {
    // 如果關閉了，可以清空時間或者設定一個預設關閉狀態的值
    item.openTimeStr = '00:00'; // 例如：如果關閉，時間設為 00:00
    item.closeTimeStr = '00:00';
  } else {
    // 如果開啟了，且沒有時間，給個預設值
    if (!item.openTimeStr || item.openTimeStr === '00:00') {
      item.openTimeStr = '10:00';
    }
    if (!item.closeTimeStr || item.closeTimeStr === '00:00') {
      item.closeTimeStr = '22:00';
    }
  }
};

// 解析時間字串和格式化時間字串的函數在新數據格式下可能不再需要
// 因為我們直接操作 openTimeStr 和 closeTimeStr。
// 如果你想用於顯示，可以保留 formatTimeRange

// 保存更改
const saveChanges = () => {
  // 在這裡可以執行數據驗證
  // 確保 openTimeStr 和 closeTimeStr 是有效的時間格式
  // 以及 openTimeStr < closeTimeStr
  // 

  
  emit('save', editableHours.value);
};

// 重置為初始狀態
const resetChanges = () => {
  editableHours.value = props.generalHours.map(hour => ({ ...hour })); // 從 props 重新初始化
  const dayOrder = {
    'MONDAY': 1, 'TUESDAY': 2, 'WEDNESDAY': 3, 'THURSDAY': 4,
    'FRIDAY': 5, 'SATURDAY': 6, 'SUNDAY': 7
  };
  editableHours.value.sort((a, b) => dayOrder[a.dayOfWeek] - dayOrder[b.dayOfWeek]);
};
</script>

<template>
  <div class="h-100 d-flex flex-column p-4">
    <div class="d-flex justify-content-between align-items-center mb-4">
      <h4 class="mb-0 fw-bold">調整一般營業時間</h4>
      <button class="btn-close" @click="emit('close')"></button>
    </div>
    <hr />

    <div class="flex-grow-1 overflow-auto pe-2 py-3">
      <div class="mb-3 form-check form-switch">
        </div>

      <div v-for="item in editableHours" :key="item.id" class="mb-4 p-3 border rounded-3 bg-light">
        <div class="d-flex justify-content-between align-items-center mb-3">
          <h6 class="mb-0 fw-bold">{{ item.dayName }}</h6>
          <div class="form-check form-switch">
            <input
              class="form-check-input"
              type="checkbox"
              :id="`toggle-${item.dayOfWeek}`"
              v-model="item.isOpen"
              @change="toggleDayStatus(item)"
            />
            <label class="form-check-label" :for="`toggle-${item.dayOfWeek}`"></label>
          </div>
        </div>

        <div v-if="item.isOpen" class="d-flex align-items-center gap-2 mb-3">
          <select class="form-select form-select-sm" v-model="item.openTimeStr">
            <option v-for="option in timeOptions" :key="option" :value="option">{{ option }}</option>
          </select>
          <span class="text-muted">至</span>
          <select class="form-select form-select-sm" v-model="item.closeTimeStr">
            <option v-for="option in timeOptions" :key="option" :value="option">{{ option }}</option>
          </select>
          </div>
        <div v-else class="text-muted text-center py-2">
            關閉
        </div>


        </div>
    </div>

    <div class="mt-auto pt-3 border-top d-flex justify-content-between gap-2">
      <button class="btn btn-outline-secondary py-2 flex-grow-1" @click="resetChanges">重置</button>
      <button class="btn btn-primary py-2 flex-grow-1" @click="saveChanges">下一步</button>
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