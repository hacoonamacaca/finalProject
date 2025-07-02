<template>
  <div>
    <h3>篩選條件</h3>
    <div class="filter-group">
      <h4>美食類型</h4>
      <label><input type="checkbox" v-model="localFilters.category" value="中式"> 中式</label>
      <label><input type="checkbox" v-model="localFilters.category" value="日式"> 日式</label>
      <label><input type="checkbox" v-model="localFilters.category" value="西式"> 西式</label>
      <label><input type="checkbox" v-model="localFilters.category" value="韓式"> 韓式</label>
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
      <h4>優惠活動</h4>
      <label><input type="checkbox" v-model="localFilters.promo" value="免運費"> 免運費</label>
      <label><input type="checkbox" v-model="localFilters.promo" value="折扣"> 折扣</label>
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue';

const props = defineProps({
  filters: Object
});

const emit = defineEmits(['update:filters', 'update-score']);

const localFilters = ref({ ...props.filters });

watch(() => props.filters, (newFilters) => {
  localFilters.value = { ...newFilters };
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