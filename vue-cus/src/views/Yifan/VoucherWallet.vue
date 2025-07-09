<template>
  <div class="voucher-container container my-5 p-4 shadow-sm bg-white rounded-4">
    <h2 class="mb-4 fw-bold text-center">
      <i class="bi bi-ticket-perforated me-3 text-warning"></i>我的優惠券
    </h2>

    <!-- 📌 Tabs -->
    <ul class="nav nav-tabs justify-content-center mb-4 border-bottom-yellow">
      <li class="nav-item" v-for="tab in tabs" :key="tab.value">
        <button
          class="nav-link tab-btn px-4 py-2 mx-1"
          :class="{ active: activeTab === tab.value }"
          @click="activeTab = tab.value"
        >
          <i :class="tab.icon" class="me-2"></i>{{ tab.label }}
        </button>
      </li>
    </ul>

    <!-- 📌 列表區 -->
    <div v-if="filteredPromotions.length > 0" class="d-flex flex-column gap-3">
      <VoucherCard
        v-for="promotion in filteredPromotions"
        :key="promotion.id"
        :promotion="promotion"
        :cartAmount="cartAmount"
        @use="handleUse"
      />
    </div>
    <div v-else class="text-muted text-center mt-5 fs-5">
      <i class="fas fa-ticket-alt me-2 text-warning"></i>
      此分類目前沒有可用優惠券
    </div>
  </div>
</template>

<script setup>
import VoucherCard from '@/components/Yifan/VoucherCard.vue'
//引入 axios 並撰寫 API 請求
import axios from '@/plungins/axios.js'
import { onMounted, ref, computed } from 'vue'
//引入優惠券圖片
import globalImg from '@/assets/vouchers/global.png'
import restaurantImg from '@/assets/vouchers/restaurant.png'
import foodImg from '@/assets/vouchers/food.png'
import memberImg from '@/assets/vouchers/member.png'


const cartAmount = ref(500)
const activeTab = ref('all')

const tabs = [
  { label: '全部', value: 'all', icon: 'fas fa-folder-open' },
  { label: '全平台', value: 'global', icon: 'fas fa-globe' },
  { label: '餐廳限定', value: 'restaurant', icon: 'fas fa-utensils' },
  { label: '餐點限定', value: 'food', icon: 'fas fa-drumstick-bite' },
  { label: '會員限定', value: 'member', icon: 'fas fa-crown' },
  { label: '歷史紀錄', value: 'history', icon: 'fas fa-clock' }
]

const promotionList = ref([]) //改連後端資料庫
const filteredPromotions = computed(() => {
  const current = activeTab.value
  return promotionList.value.filter(p => {
    if (current === 'history') return p.used
    if (current === 'all') return !p.used
    if (p.used) return false
    return p.type === current
  })
})

const handleUse = (promo) => {
  console.log('使用優惠券：', promo.title)
}

onMounted(async () => { //載入優惠券資料
  try {
    const response = await axios.get('/promotions')
    console.log('載入優惠券成功', response)
    promotionList.value = response.data.map(item => {
      let imageUrl = globalImg
      let iconClass = 'fas fa-globe'
      let type = 'global'

      if (item.store?.id) {
        imageUrl = restaurantImg
        iconClass = 'fas fa-utensils'
        type = 'restaurant'
      } else if (item.tag?.id) {
        imageUrl = foodImg
        iconClass = 'fas fa-hamburger'
        type = 'food'
      } else if (item.plan?.id) {
        imageUrl = memberImg
        iconClass = 'fas fa-crown'
        type = 'member'
      }

      return {
        ...item,
        iconClass,
        imageUrl,
        type,
      }
    })
  } catch (error) {
    console.error('載入優惠券失敗', error)
  }
})


</script>

<style scoped>
h2 {
  font-size: 1.8rem;
  letter-spacing: 1px;
}

/* 主容器美化 */
.voucher-container {
  background-color: #fffbea;
}

/* Tabs 樣式 */
.nav-tabs {
  border-bottom: 2px solid #ffc94d;
}

.nav-tabs .nav-link {
  border-radius: 0; /* 去掉圓角 */
  color: #555;
  font-weight: 500;
  padding: 10px 20px;
}


.tab-btn {
  background-color: transparent;
  color: #666;
  border: 1px solid transparent;
  transition: all 0.2s ease;
  border-radius: 0 !important;
}

.tab-btn:hover {
  background-color: #fff3cd;
  color: #d48806;
  border-color: #ffe58f;
}

.tab-btn.active {
  background-color: #ffc94d;
  color: white;
  font-weight: bold;
  border-color: #ffc94d;
}

/* 無資料訊息 */
.text-muted i {
  font-size: 1.2rem;
}
</style>