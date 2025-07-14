<template>
  <div class="filters">
    <div class="time-based-filters">
      <button v-if="isMorning" @click="emitSearchKeyword('早餐')" class="filter-button">
        ☀️ 早餐推薦
      </button>
      <button v-else @click="emitSearchKeyword('宵夜')" class="filter-button">
        🌙 宵夜推薦
      </button>
    </div>

    <div class="temperature-based-filters">
      <button v-if="isCold" @click="emitSearchKeyword('熱')" class="filter-button">
        🍲 熱湯推薦
      </button>
      <button v-else-if="isHot" @click="emitSearchKeyword('冰')" class="filter-button">
        🍧 冰品推薦
      </button>
      <span v-if="temperature !== null" class="temperature-display">
        目前溫度: {{ temperature }}°C
      </span>
      <span v-else class="temperature-display">
        載入溫度中...
      </span>
    </div>

    <div class="sort-options">
      <label for="sort-select">排序：</label>
      <select id="sort-select" :value="modelValue" @change="handleSortChange">
        <option value="評分最高">評分最高</option>
        <option value="距離最近">距離最近</option>
        <option value="最受歡迎">最受歡迎</option>
      </select>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue';
import { useLocationStore } from '@/stores/location';

const locationStore = useLocationStore();

const props = defineProps({
  modelValue: {
    type: String,
    default: '評分最高'
  }
});

// 修改 emit 事件名稱，現在發出 'search-keyword'
const emit = defineEmits(['update:modelValue', 'search-keyword']);

const temperature = computed(() => locationStore.temperature);

const isMorning = computed(() => {
  const hour = new Date().getHours();
  // 早餐時間設定為早上 5 點到 10 點前
  return hour >= 5 && hour < 10;
});

// 判斷是否為宵夜時間
const isSupper = computed(() => {
  const hour = new Date().getHours();
  // 宵夜時間設定為晚上 10 點到凌晨 4 點前
  return hour >= 22 || hour < 4;
});

// 判斷溫度是冷還是熱 (可根據實際需求調整閾值)
const isCold = computed(() => {
  return temperature.value !== null && temperature.value < 18; // 低於 18 度為冷，建議熱湯
});

const isHot = computed(() => {
  return temperature.value !== null && temperature.value > 28; // 高於 28 度為熱，建議冰品
});

const handleSortChange = (event) => {
  emit('update:modelValue', event.target.value);
};

// **新的方法：發出搜尋關鍵字**
const emitSearchKeyword = (keyword) => {
  emit('search-keyword', keyword);
};

onMounted(() => {
  // 如果沒有座標或溫度，嘗試獲取
  if (locationStore.coordinates && locationStore.coordinates.lat && locationStore.coordinates.lon && locationStore.temperature === null) {
    locationStore.getTemperature(locationStore.coordinates.lat, locationStore.coordinates.lon);
  }
});

watch(() => locationStore.coordinates, (newCoords) => {
  if (newCoords && newCoords.lat && newCoords.lon && locationStore.temperature === null) {
    locationStore.getTemperature(newCoords.lat, newCoords.lon);
  }
}, { deep: true });
</script>

<style scoped>
/* 樣式與之前相同 */
.filters {
  padding: 15px 20px;
  background-color: #fff;
  margin: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 15px;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.filter-button {
  padding: 8px 15px;
  background-color: #ffba20;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: background-color 0.2s ease;
  margin-right: 10px;
}

.filter-button:hover {
  background-color: #e6a71c;
}

.temperature-display {
  font-size: 14px;
  color: #555;
  margin-left: 10px;
}

.sort-options label {
  font-size: 14px;
  color: #333;
  margin-right: 5px;
}

.sort-options select {
  padding: 8px;
  border: 1px solid #ccc;
  border-radius: 4px;
  font-size: 14px;
  background-color: white;
  cursor: pointer;
}

@media (max-width: 768px) {
  .filters {
    flex-direction: column;
    align-items: flex-start;
  }

  .time-based-filters,
  .temperature-based-filters,
  .sort-options {
    width: 100%;
    display: flex;
    flex-wrap: wrap;
    gap: 10px;
    margin-bottom: 10px;
  }

  .filter-button {
    flex-grow: 1;
    margin-right: 0;
  }
}
</style>