<!-- 優惠券卡片元件 -->

<template>
  <div
    class="voucher-card d-flex shadow-sm"
    :class="{ 'used-card': promotion.used || promotion.min_spend > cartAmount }"
  >
    <!-- 左側圖片區塊 -->
    <div class="voucher-label">
      <img :src="promotion.imageUrl" alt="icon" class="voucher-img" />
    </div>

    <!-- 右側內容區域 -->
    <div class="voucher-content flex-grow-1">
      <div class="d-flex justify-content-between align-items-start">
        <!-- 優惠券標題 -->
        <h5 class="voucher-title">
          {{ promotion.title }}
        </h5>

        <!-- 使用按鈕 -->
        <button
          class="btn btn-use"
          :disabled="promotion.min_spend > cartAmount || promotion.used"
          @click="$emit('use', promotion)"
        >
          使用
        </button>
      </div>

      <!-- 優惠券資訊 -->
      <p class="mb-1 text-secondary">滿 {{ promotion.min_spend }} 折 {{ promotion.discount_value }}</p>
      <p class="mb-1 text-muted">
        有效期限：{{ promotion.start_time }} ~ {{ promotion.end_time }}
      </p>
      <small class="text-muted">{{ promotion.description }}</small>
    </div>
  </div>
</template>

<script setup>
// 接收 props 與 emits
const props = defineProps({
  promotion: Object,
  cartAmount: Number,
})
const emit = defineEmits(['use'])
</script>

<style scoped>
/* 卡片區塊 */
.voucher-card {
  background: white;
  border-radius: 12px;
  border: 1px solid #ddd;
  overflow: hidden;
  display: flex;
  margin-bottom: 20px;
  border-left: 10px solid #8f530f; /* 左側強調色條 */
  transition: box-shadow 0.3s;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}
.voucher-card:hover {
  box-shadow: 0 6px 18px rgba(0, 0, 0, 0.12);
}

/* 已使用或未達門檻 */
.used-card {
  opacity: 0.6;
  filter: grayscale(80%);
  pointer-events: none; /* 無法點選使用 */
}

/* 左側圖片區域 */
.voucher-label {
  width: 150px;
  background: #ffba20;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 24px;
}

.voucher-img {
  max-width: 150px;
  max-height: 150px;
  object-fit: contain;
}

/* 右側文字內容區 */
.voucher-content {
  padding: 20px 24px;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.voucher-title {
  font-size: 1.3rem;
  font-weight: bold;
  color: #333;
  margin-bottom: 4px;
}

/* 使用按鈕樣式 */
.btn-use {
  background-color: #ffba20;
  border: none;
  padding: 8px 18px;
  font-size: 0.95rem;
  font-weight: bold;
  color: #fff;
  border-radius: 6px;
  transition: all 0.3s ease;
}
.btn-use:hover:enabled {
  background-color: #eca300;
  box-shadow: 0 0 0 3px #8f530f;
}
.btn-use:disabled {
  background-color: #e0e0e0;
  color: #888;
  cursor: not-allowed;
  box-shadow: none;
}

.nav-link {
    display: flex;
    align-items: center;
    font-size: 15px;
    color: #6c757d;
    background-color: transparent;
    border: none;
    padding: 0.5rem 1rem;
    border-radius: 6px;
    transition: all 0.3s ease;
    transform: translateX(0); 
}

.nav-link:hover {
    background-color: #fcebc1;
    color: #eca300;
    transform: translateX(4px);
}

</style>
