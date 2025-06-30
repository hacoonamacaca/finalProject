<template>
  <div>
    <h3>篩選條件</h3>
    <div class="filter-group">
      <h4>美食類型</h4>
      <label><input type="checkbox" v-model="localFilters.cuisine" value="中式"> 中式</label>
      <label><input type="checkbox" v-model="localFilters.cuisine" value="日式"> 日式</label>
      <label><input type="checkbox" v-model="localFilters.cuisine" value="西式"> 西式</label>
      <label><input type="checkbox" v-model="localFilters.cuisine" value="韓式"> 韓式</label>
    </div>
    <div class="filter-group">
      <h4>最低星數</h4>
      <input type="range" min="0" max="5" step="0.5" v-model.number="localFilters.minRating" @input="emitUpdateRating" />
      <div class="range-value">{{ localFilters.minRating }} 星</div>
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

const emit = defineEmits(['update:filters', 'update-rating']);

const localFilters = ref({ ...props.filters });

watch(() => props.filters, (newFilters) => {
  localFilters.value = { ...newFilters };
}, { deep: true });

watch(localFilters, (newFilters) => {
  emit('update:filters', { ...newFilters });
}, { deep: true });

const emitUpdateRating = () => {
  emit('update-rating');
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

.filter-group input[type="range"] {
  width: 100%;
}

.filter-group .range-value {
  font-size: 14px;
  color: #666;
  margin-top: 5px;
}
</style>