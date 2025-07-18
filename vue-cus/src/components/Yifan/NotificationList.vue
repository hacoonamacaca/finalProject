<template>
  <div class="notification-panel">
    <div class="panel-header">
      <i class="bi bi-megaphone-fill me-2"></i>最近優惠通知
    </div>

    <ul class="notification-ul">
      <li
        v-for="item in sortedNotifications"
        :key="item.id"
        :class="{ 'unread': !item.isRead }"
        @click="handleItemClick(item)"
      >
        <div class="d-flex justify-content-between align-items-center mb-1">
          <span class="promotion-title">{{ item.promotionTitle }}</span>
          <span v-if="!item.isRead" class="badge bg-danger">未讀</span>
        </div>
        <div class="promotion-dates">活動時間：<br>
          {{ item.promotionStartTimeStr }} ~ {{ item.promotionEndTimeStr }}
        </div>
      </li>
    </ul>


    <!-- 已讀 -->
    <div class="text-center mt-2">
      <a
        href="#"
        class="text-decoration-none text-dark small"
        @click.prevent="emit('mark-all-as-read')"
      >
        全部標示為已讀
      </a>
    </div>
  </div>
</template>

<script setup>
import { ref,defineProps, defineEmits, computed } from 'vue';

const showAll = ref(false)

const props = defineProps({
  notifications: Array
});

const emit = defineEmits(['mark-as-read', 'mark-all-as-read']);

const handleItemClick = (item) => {
  if (!item.isRead) {
    emit('mark-as-read', item);
  }
};

//  按 createdTime 做降冪排序（新通知排最上）
const sortedNotifications = computed(() => {
  return [...props.notifications].sort((a, b) => {
    return new Date(b.createdTime) - new Date(a.createdTime);
  });
})
</script>

<style scoped>
.notification-panel {
  position: absolute;
  top: 92%;
  right: calc(50% - 160px);
  width: 320px;
  background-color: white;
  border: 1px solid #ddd;
  border-radius: 12px;
  padding: 16px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
  z-index: 1000;
}

.panel-header {
  font-weight: bold;
  font-size: 16px;
  color: #f9a825; /* 黃色標題 */
  margin-bottom: 12px;
}

.notification-ul {
  padding-left: 0;
  list-style: none;
  max-height: 300px;
  overflow-y: auto;
}

.notification-ul li {
  padding: 10px 8px;
  border-bottom: 1px solid #eee;
  cursor: pointer;
  background-color: white; /* 已讀預設為白底 */
}

.notification-ul li.unread {
  background-color: #fff8e1; /* 未讀為淺黃色 */
}

/* 標題為黑色 */
.promotion-title {
  color: #212529;
  font-weight:bold;
}

/* 時間文字為灰色 */
.promotion-dates {
  color: #9d9d9d;
  font-weight:bold ;
  font-size: 12px;
}

/*  內容文字為黑色 */
.text-center a:hover {
  color: #f9a825;
}
</style>
