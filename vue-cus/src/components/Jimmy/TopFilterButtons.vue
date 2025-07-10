<template>
  <div class="filter-buttons">
    <!-- <button :class="{ active: filters.category.length === 0 && filters.promo.length === 0 }" @click="applyFilter('全部')">該訂便當了吧</button> -->
    <button :class="{ active: filters.category.includes('全部') }" @click="applyFilter('全部')">該訂便當了吧</button>
    <button :class="{ active: filters.category.includes('中式') }" @click="applyFilter('中式')">夏日炎炎 來點清涼的吧!~</button>
    <button :class="{ active: filters.category.includes('日式') }" @click="applyFilter('日式')">小孩放電好去處</button>
    <button :class="{ active: filters.category.includes('西式') }" @click="applyFilter('西式')">燒烤! 啤酒! 嚕串!</button>
    <button :class="{ active: filters.category.includes('韓式') }" @click="applyFilter('韓式')">想跟大夥一起看球賽嗎?</button>
    <button :class="{ active: filters.promo.includes('免運費') }" @click="applyFilter('免運費')">猜你喜歡</button>
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
    newFilters.category = [];
    newFilters.promo = [];
    newFilters.minscore = 0;
  } else if (['中式', '日式', '西式', '韓式'].includes(filterType)) {
    if (newFilters.category.includes(filterType)) {
      newFilters.category = newFilters.category.filter(c => c !== filterType);
    } else {
      newFilters.category.push(filterType);
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
  background-color: #ffba20;
  color: white;
}
</style>