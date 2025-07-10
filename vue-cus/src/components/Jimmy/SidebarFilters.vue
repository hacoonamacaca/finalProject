<template>
  <div>
    <h3>篩選條件</h3>
    <div class="filter-group">
      <h4>美食類型</h4>
      <label><input type="checkbox" v-model="localFilters.category" value="中式料理"> 中式料理</label>
      <label><input type="checkbox" v-model="localFilters.category" value="西式料理"> 西式料理</label>
      <label><input type="checkbox" v-model="localFilters.category" value="日式料理"> 日式料理</label>
      <label><input type="checkbox" v-model="localFilters.category" value="韓式料理"> 韓式料理</label>
    </div>
    <div class="filter-group">
      <h4>最低星數</h4>
      <div class="star-rating">
        <span
          v-for="index in 5"
          :key="index"
          class="star"
          :class="{ 'half': isHalfStar(index), 'full': isFullStar(index) }"
          @click="setStarRating(index, $event)"
        >
          ★
        </span>
      </div>
      <div class="range-value">{{ localFilters.minscore.toFixed(1) }} 星</div>
    </div>
    <div class="filter-group">
      <h4>營業狀態</h4>
      <label><input type="checkbox" v-model="localFilters.isOpen"> 營業中</label>
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue';

const props = defineProps({
  filters: Object,  
});

const emit = defineEmits(['update:filters', 'update-score']);

const localFilters = ref({
  category: props.filters.category || [],
  minscore: props.filters.minscore || 0,
  // promo: props.filters.promo || [], // 移除 promo 初始化
  isOpen: props.filters.isOpen || false // 確保這裡有 isOpen 且為布林值
});


watch(() => props.filters, (newFilters) => {
  // 深度複製，確保所有屬性都被更新，同時要小心處理新/舊屬性
  localFilters.value = {
    category: newFilters.category || [],
    minscore: newFilters.minscore || 0,
    // promo: newFilters.promo || [], // 移除 promo watch
    isOpen: newFilters.isOpen || false // 確保 isOpen 被正確更新
  };
}, { deep: true });

watch(localFilters, (newFilters) => {
  emit('update:filters', { ...newFilters });
}, { deep: true });

const emitUpdatescore = () => {
  emit('update-score');
};

// 星星評分邏輯
const setStarRating = (index, event) => {
  const rect = event.target.getBoundingClientRect();
  const clickX = event.clientX - rect.left;
  const starWidth = rect.width;
  const isHalf = clickX < starWidth / 2; // 點擊左半邊為半顆星
  localFilters.value.minscore = isHalf ? index - 0.5 : index;
  emitUpdatescore();
};

const isHalfStar = (index) => {
  return localFilters.value.minscore === index - 0.5;
};

const isFullStar = (index) => {
  return localFilters.value.minscore >= index;
};
</script>

<style scoped>
.sidebar {  
  width: 250px;
  background-color: #fff;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.sidebar h3 {
  font-size: 18px;
  margin-bottom: 15px;
}

.filter-group {
  margin-bottom: 20px;
}

.filter-group label {
  display: block;
  margin-bottom: 10px;
  font-size: 14px;
  color: #333;
}

.filter-group input[type="checkbox"] {
  margin-right: 8px;
}

.star-rating {
  display: flex;
  font-size: 2.25rem; /* 原 1.5rem 放大 1.5 倍 = 2.25rem */
  cursor: pointer;
}

.star {
  color: #ccc; /* 未選中的星星顏色 */
  margin-right: 7.5px; /* 原 5px 放大 1.5 倍 = 7.5px */
}

.star.full {
  color: #f39c12; /* 整顆星的顏色 */
}

.star.half::before {
  content: '★';
  position: absolute;
  color: #f39c12; /* 半顆星的顏色 */
  width: 50%;
  overflow: hidden;
}

.star.half {
  position: relative;
  color: #ccc; /* 半顆星的背景顏色 */
}

.range-value {
  font-size: 14px;
  color: #666;
  margin-top: 5px;
}
</style>