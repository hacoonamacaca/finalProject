<!-- 優惠券主頁面 -->
<template>
  <div class="container my-5">
    <h2 class="mb-4 fw-bold text-center">我的優惠券</h2>

    <!-- 📌 Tabs 切換分類 -->
    <ul class="nav nav-tabs justify-content-center mb-4">
      <li class="nav-item" v-for="tab in tabs" :key="tab.value">
        <button
          class="nav-link"
          :class="{ active: activeTab === tab.value }"
          @click="activeTab = tab.value"
        >
          {{ tab.icon }} {{ tab.label }}
        </button>
      </li>
    </ul>

    <!-- 📌 優惠券清單 -->
    <div v-if="filteredPromotions.length > 0" class="d-flex flex-column gap-3">
      <VoucherCard
        v-for="promotion in filteredPromotions"
        :key="promotion.id"
        :promotion="promotion"
        :cartAmount="cartAmount"
        @use="handleUse"
      />
    </div>
    <div v-else class="text-muted text-center mt-4">
      此分類目前沒有可用優惠券
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import VoucherCard from '../components/VoucherCard.vue'
import { promotionList } from '@/mock/promotionData.js' // 確保路徑正確

const cartAmount = ref(500)
const activeTab = ref('all')


const promotions = ref(promotionList)

const tabs = [
  { label: '全部', value: 'all', icon: '📂' },
  { label: '全平台', value: 'global', icon: '🌐' },
  { label: '餐廳限定', value: 'restaurant', icon: '🍽️' },
  { label: '餐點限定', value: 'food', icon: '🍔' },
  { label: '會員限定', value: 'member', icon: '👑' },
  { label: '歷史紀錄', value: 'history', icon: '🕓' }
]

const filteredPromotions = computed(() => {
  const current = activeTab.value
  return promotions.value.filter(p => {
    if (current === 'history') return p.used
    if (current === 'all') return !p.used
    if (p.used) return false
    if (current === 'global') return !p.restaurant_id && !p.food_category_id && !p.plan_id
    if (current === 'restaurant') return !!p.restaurant_id
    if (current === 'food') return !!p.food_category_id
    if (current === 'member') return !!p.plan_id
    return false
  })
})

const handleUse = (promo) => {
  console.log('使用優惠券：', promo.title)
}
</script>

<style scoped>
.nav-link {
  cursor: pointer;
}

.nav-link.active {
  font-weight: bold;
  color: #4b80d0;
}
</style>
