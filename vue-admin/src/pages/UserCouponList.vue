<template>
  <div>
    <button @click="show = true">選擇優惠券</button>
    <p>已選擇: {{ selected?.title || '尚未選擇' }}</p>

    <CouponSelectorModal
      v-model:show="show"
      :promotions="promotionList"
      :cartAmount="200"
      @selected="handleSelected"
    />
  </div>
</template>

<script setup>
import { ref } from 'vue'
import CouponSelectorModal from '../components/CouponSelectorModal.vue'

const show = ref(false)
const selected = ref(null)

const promotionList = ref([
  { id: 1, title: '🎁 滿 500 折 50', discount_value: 50, min_spend: 500, start_time: '2025-06-01', end_time: '2025-06-30', description: '優惠券描述' },
  { id: 2, title: '週年慶！全站 95 折 🎉', discount_value: 5, min_spend: 0, start_time: '2025-06-01', end_time: '2025-06-30', description: '適用所有活動' },
  { id: 3, title: '🍹 夏日解渴囉！🔥', discount_value: 30, min_spend: 300, start_time: '2025-06-01', end_time: '2025-06-30', description: '夏日限定優惠' }
])

const handleSelected = (promotion) => {
  selected.value = promotion
  console.log('已選擇：', promotion)
}
</script>
