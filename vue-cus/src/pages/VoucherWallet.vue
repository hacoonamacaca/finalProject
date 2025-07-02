// ✅ VoucherWallet.vue
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
import globalImg from '@/assets/vouchers/global.png'
import restaurantImg from '@/assets/vouchers/restaurant.png'
import foodImg from '@/assets/vouchers/food.png'
import memberImg from '@/assets/vouchers/member.png'

const cartAmount = ref(500)
const activeTab = ref('all')

const tabs = [
  { label: '全部', value: 'all', icon: '📂' },
  { label: '全平台', value: 'global', icon: '🌐' },
  { label: '餐廳限定', value: 'restaurant', icon: '🍽️' },
  { label: '餐點限定', value: 'food', icon: '🍔' },
  { label: '會員限定', value: 'member', icon: '👑' },
  { label: '歷史紀錄', value: 'history', icon: '🕓' }
]

// 📌 假資料，含 used 狀態
const promotionList = ref([
  { id: 1, title: '全平台券 - 滿 500 折 50', imageUrl: globalImg, restaurant_id: null, food_category_id: null, plan_id: null, min_spend: 500, discount_value: 50, start_time: '2025-06-01', end_time: '2025-06-30', description: '全站可使用', used: false },
  { id: 2, title: '餐廳限定券 - 滿 800 折 100', imageUrl: restaurantImg, restaurant_id: 2, food_category_id: null, plan_id: null, min_spend: 800, discount_value: 100, start_time: '2025-06-01', end_time: '2025-06-30', description: '僅限餐廳 ID 2', used: false },
  { id: 3, title: '餐點限定券 - 炸雞類折 30', imageUrl: foodImg, restaurant_id: null, food_category_id: 5, plan_id: null, min_spend: 300, discount_value: 30, start_time: '2025-06-01', end_time: '2025-06-30', description: '僅限分類 ID 5', used: true },
  { id: 4, title: '會員限定券 - VIP 9 折', imageUrl: memberImg, restaurant_id: null, food_category_id: null, plan_id: 1, min_spend: 0, discount_value: 10, start_time: '2025-06-01', end_time: '2025-06-30', description: 'VIP 會員專用', used: true }
])

// 📌 篩選資料
const filteredPromotions = computed(() => {
  const current = activeTab.value
  return promotionList.value.filter(p => {
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
/* ✅ 統一 hover 效果 */
.nav-link {
  cursor: pointer;
}

.nav-link.active {
  font-weight: bold;
  color: #4b80d0;
}
</style>
