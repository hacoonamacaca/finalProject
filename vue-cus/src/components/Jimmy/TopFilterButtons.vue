<template>
  <div class="filter-buttons">
    <button :class="{ active: filters.cuisine.length === 0 && filters.promo.length === 0 }" @click="applyFilter('全部')">全部</button>
    <button :class="{ active: filters.cuisine.includes('中式') }" @click="applyFilter('中式')">中式</button>
    <button :class="{ active: filters.cuisine.includes('日式') }" @click="applyFilter('日式')">日式</button>
    <button :class="{ active: filters.cuisine.includes('西式') }" @click="applyFilter('西式')">西式</button>
    <button :class="{ active: filters.cuisine.includes('韓式') }" @click="applyFilter('韓式')">韓式</button>
    <button :class="{ active: filters.promo.includes('免運費') }" @click="applyFilter('免運費')">免運費</button>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue';

const props = defineProps({
  filters: Object
});

const emit = defineEmits(['update:filters']);

const localFilters = ref({ ...props.filters });

watch(() => props.filters, (newFilters) => {
  localFilters.value = { ...newFilters };
}, { deep: true });

const applyFilter = (filterType) => {
  const newFilters = { ...localFilters.value };

  if (filterType === '全部') {
    newFilters.cuisine = [];
    newFilters.promo = [];
    newFilters.minRating = 0;
  } else if (['中式', '日式', '西式', '韓式'].includes(filterType)) {
    if (newFilters.cuisine.includes(filterType)) {
      newFilters.cuisine = newFilters.cuisine.filter(c => c !== filterType);
    } else {
      newFilters.cuisine.push(filterType);
    }
  } else if (filterType === '免運費') {
    if (newFilters.promo.includes('免運費')) {
      newFilters.promo = newFilters.promo.filter(p => p !== '免運費');
    } else {
      newFilters.promo.push('免運費');
    }
  }

  emit('update:filters', newFilters);
};
</script>

<style scoped>
.filter-buttons button {
  padding: 8px 16px;
  border: 1px solid #ccc;
  background-color: #fff;
  cursor: pointer;
  border-radius: 20px;
  margin-right: 10px;
  font-size: 14px;
}

.filter-buttons button:hover,
.filter-buttons button.active {
  background-color: #d70f64;
  color: white;
}
</style>