<template>
  <div>
    <button @click="show = true">選擇可以使用的優惠券</button>
    <p>已選擇: {{ selected?.title || '尚未選擇' }}</p>

    <PromotionSelectorModal
      v-model:show="show"
      :promotions="promotions"
      :cartAmount="200"
      @selected="handleSelected"
    />
  </div>
</template>

<script setup>
import { ref } from 'vue'
import PromotionSelectorModal from '../components/CouponSelectorModal.vue'

// ✅ 匯入統一的假資料
import { promotionList } from '@/mock/promotionData.js'

const show = ref(false)
const selected = ref(null)

// ✅ 包成 ref，供彈窗使用
const promotions = ref(promotionList)

const handleSelected = (promotion) => {
  selected.value = promotion
  console.log('已選擇：', promotion)
}
</script>
