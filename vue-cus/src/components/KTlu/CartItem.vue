<template>
<div class="d-flex flex-column flex-md-row justify-content-between border-top py-3 gap-3">
  <!-- 商品資訊區 -->
  <div class="d-flex align-items-center">
    <img :src="item.image" alt="商品圖" class="me-2" style="width: 60px;" />
    <div>
      <div class="fw-bold">{{ item.name }}</div>
      <div class="text-muted" v-if="item.soldOut">此規格已售完</div>
    </div>
  </div>

  <!-- 價格 + 數量 + 刪除 -->
  <div class="d-flex align-items-center justify-content-end flex-wrap gap-2">
    <div class="text-nowrap">${{ item.price * item.quantity }}</div>
    <div class="input-group input-group-sm" style="width: 100px;">
      <button class="btn btn-outline-secondary" @click="decrease">－</button>
      <input type="text" class="form-control text-center" :value="item.quantity" readonly />
      <button class="btn btn-outline-secondary" @click="increase">＋</button>
    </div>
    <button class="btn btn-sm btn-outline-danger" @click="$emit('remove')">
      <i class="bi bi-trash"></i>
    </button>
  </div>
</div>
</template>

<script setup>
const props = defineProps({
  item: Object
})

const emit = defineEmits(['remove'])

function increase() {
  if (!props.item.soldOut) props.item.quantity++
}

function decrease() {
  if (props.item.quantity > 1) props.item.quantity--
}
</script>
<style>
@media (max-width: 768px) {
  .cart-item {
    border-radius: 0.5rem;
    padding: 1rem;
    background: #f8f9fa;
  }
}

</style>