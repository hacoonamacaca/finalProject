<template>
  <div class="filters">
    <div class="recommendation-buttons">
      <button v-if="isMorning" @click="emitSearchKeyword('早餐')" class="filter-button">
        ☀️ 早餐推薦
      </button>
      <button v-else-if="isBrunch" @click="emitSearchKeyword('早午餐')" class="filter-button">
        🍳 早午餐推薦
      </button>
      <button v-else-if="isSupper" @click="emitSearchKeyword('宵夜')" class="filter-button">
        🌙 宵夜推薦
      </button>

      <button v-if="isCold" @click="emitSearchKeyword('熱')" class="filter-button">
        🍲 熱食推薦
      </button>
      <button v-else-if="isHot" @click="emitSearchKeyword('冰')" class="filter-button">
        🍧 冰品推薦
      </button>

      <button
        v-for="keyword in randomKeywords"
        :key="keyword"
        @click="emitSearchKeyword(keyword)"
        class="filter-button"
      >
        {{ keyword }}
      </button>
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

const emit = defineEmits(['update:modelValue', 'search-keyword']);

const temperature = computed(() => locationStore.temperature);

const isMorning = computed(() => {
  const hour = new Date().getHours();
  // 早餐時間設定為早上 5 點到 10 點前
  return hour >= 5 && hour < 10;
});

const isBrunch = computed(() => {
  const hour = new Date().getHours();
  // 早午餐時間設定為早上 10 點到下午 2 點前 (14 點)
  return hour >= 10 && hour < 14;
});

const isSupper = computed(() => {
  const hour = new Date().getHours();
  // 宵夜時間設定為晚上 10 點到凌晨 4 點前
  return hour >= 22 || hour < 4;
});

const isCold = computed(() => {
  return temperature.value !== null && temperature.value < 18;
});

const isHot = computed(() => {
  return temperature.value !== null && temperature.value > 28;
});

const handleSortChange = (event) => {
  emit('update:modelValue', event.target.value);
};

const emitSearchKeyword = (keyword) => {
  emit('search-keyword', keyword);
};

// --- 隨機推薦邏輯 ---
const allRecommendationKeywords = [
  '寵物友善', '小孩放電', '觀看直播', '素食', '咖啡廳',
  '異國料理', '甜點', '外帶', '內用', '聚餐', '小吃',
  '健康餐', '快速取餐', '氣氛好', '夜景' // 增加更多範例關鍵字
];
const randomKeywords = ref([]);

const selectRandomKeywords = () => {
  const shuffled = [...allRecommendationKeywords].sort(() => 0.5 - Math.random());
  randomKeywords.value = shuffled.slice(0, 3);
};
// --- 隨機推薦邏輯結束 ---

onMounted(() => {
  // 獲取溫度資訊 (這部分仍保留，因為按鈕需要溫度判斷)
  if (locationStore.coordinates && locationStore.coordinates.lat && locationStore.coordinates.lon && locationStore.temperature === null) {
    locationStore.getTemperature(locationStore.coordinates.lat, locationStore.coordinates.lon);
  }
  // 頁面載入時選擇隨機關鍵字
  selectRandomKeywords();
});

watch(() => locationStore.coordinates, (newCoords) => {
  if (newCoords && newCoords.lat && newCoords.lon && locationStore.temperature === null) {
    locationStore.getTemperature(newCoords.lat, newCoords.lon);
  }
}, { deep: true });
</script>

<style scoped>
.filters {
  padding: 15px 20px;
  background-color: #fff;
  margin: 20px;
  display: flex;
  /* 讓主容器的內容（推薦按鈕區塊和排序選項）水平排列，並靠左 */
  justify-content: flex-start; /* 讓內容靠左 */
  align-items: center; /* 垂直居中對齊 */
  flex-wrap: wrap; /* 允許換行 */
  gap: 15px; /* 主容器內主要區塊間的間距 */
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

/* 新增一個容器來包裹所有推薦按鈕 */
.recommendation-buttons {
  display: flex;
  flex-wrap: wrap;
  gap: 10px; /* 按鈕之間的間距 */
  justify-content: flex-start; /* 確保按鈕在此容器內靠左對齊 */
  /* 可以讓這個容器盡量佔用空間，讓排序選項在右側 */
  flex-grow: 1; 
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
}

.filter-button:hover {
  background-color: #e6a71c;
}

.sort-options {
  display: flex; /* 讓 label 和 select 在一行 */
  align-items: center; /* 垂直居中對齊 */
  gap: 5px; /* label 和 select 之間的間距 */
  flex-shrink: 0; /* 不讓排序選項被壓縮 */
}

.sort-options label {
  font-size: 14px;
  color: #333;
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
    flex-direction: column; /* 小螢幕下垂直堆疊 */
    align-items: flex-start; /* 內容靠左對齊 */
  }

  .recommendation-buttons {
    width: 100%; /* 佔滿寬度 */
    justify-content: flex-start; /* 小螢幕下按鈕也靠左 */
    margin-bottom: 10px; /* 與排序選項之間的間距 */
  }

  .sort-options {
    width: 100%; /* 佔滿寬度 */
    justify-content: flex-start; /* 排序選項靠左 */
  }

  .filter-button {
    /* 在小螢幕下，可以讓按鈕平均分佈或者仍然靠左，
        如果需要平均分佈，可以設定 flex-grow: 1; 或 width: auto; 
        但如果您要嚴格靠左，就不需要 flex-grow: 1; */
    flex-grow: 0; /* 讓按鈕保持其內容寬度，並靠左排列 */
  }
}
</style>