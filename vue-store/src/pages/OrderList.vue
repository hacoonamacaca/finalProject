<script setup>
import { ref } from 'vue';

const props = defineProps({
  orders: {
    type: Array,
    required: true,
  },
});

const emit = defineEmits(['select-order']);

const selectOrder = (order) => {
  emit('select-order', order);
};
</script>

<template>
  <div class="table-responsive">
    <table class="table table-hover table-borderless align-middle text-nowrap">
      <thead>
        <tr>
          <th scope="col" class="text-muted text-uppercase">訂單 ID</th>
          <th scope="col" class="text-muted text-uppercase">商品詳細資訊</th>
          <th scope="col" class="text-muted text-uppercase">日期</th>
          <th scope="col" class="text-muted text-uppercase">時間</th>
          <th scope="col" class="text-muted text-uppercase">顧客</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="order in orders" :key="order.id" @click="selectOrder(order)" class="cursor-pointer border-bottom rounded-3 overflow-hidden">
          <td>{{ order.id }}</td>
          <td>{{ order.shop }}</td>
          <td>{{ order.date }}</td>
          <td>{{ order.time }}</td>
          <td>{{ order.customerName }} <i class="fas fa-user-circle ms-2 text-muted"></i></td>
        </tr>
        <tr v-if="orders.length === 0">
          <td colspan="5" class="text-center py-4 text-muted">沒有找到訂單。</td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<style scoped>
.cursor-pointer {
  cursor: pointer;
}

/* 为行添加圆角和阴影效果 */
tbody tr {
  transition: background-color 0.2s ease, transform 0.2s ease;
  border-radius: 8px; /* 圆角 */
  overflow: hidden; /* 确保内容不会溢出圆角 */
}

tbody tr:hover {
  background-color: #f8f9fa; /* 悬停背景色 */
  transform: scale(1.01); /* 悬停时轻微放大 */
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1); /* 悬停时添加阴影 */
}

/* 确保表格头部和单元格对齐 */
.table th, .table td {
  padding: 1rem 1rem; /* 调整内边距 */
  border-color: #eee; /* 轻微边框颜色 */
}

.table thead th {
  border-bottom: 2px solid #e9ecef; /* 头部底边框 */
}

/* 隐藏最后一行底部边框，使卡片感觉更强 */
tbody tr:last-child {
  border-bottom: none !important;
}

/* 调整表格背景，使其融入卡片背景 */
.table {
  background-color: transparent;
}
</style>
