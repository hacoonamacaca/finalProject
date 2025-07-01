<template>
  <div class="container my-5">
    <h2>結帳頁</h2>

    <button class="btn btn-primary mb-3" @click="show = true">
      選擇優惠券
    </button>

    <p>已選擇：{{ selected?.title || '尚未選擇' }}</p>

    <!-- 彈窗元件 -->
    <CouponSelectorModal
      v-model:show="show"
      :promotions="promotionList"
      :cartAmount="cartAmount"
      @selected="handleSelected"
    />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import CouponSelectorModal from '@/components/Yifan/CouponSelectorModal.vue'
import globalImg from '@/assets/vouchers/global.png'
import restaurantImg from '@/assets/vouchers/restaurant.png'
import foodImg from '@/assets/vouchers/food.png'
import memberImg from '@/assets/vouchers/member.png'


// 彈窗顯示控制
const show = ref(false)
// 已選擇的券
const selected = ref(null)
// 購物車金額（範例）
const cartAmount = ref(500)

// 可用優惠券（假資料）
const promotionList = ref([
  {
    id: 1,
    title: '全平台券 - 滿 500 折 50',
    iconClass: 'fas fa-globe',
    imageUrl: globalImg,
    restaurant_id: null,
    food_category_id: null,
    plan_id: null,
    min_spend: 500,
    discount_value: 50,
    start_time: '2025-06-01',
    end_time: '2025-06-30',
    description: '全站可使用'
  },
  {
    id: 2,
    title: '餐廳限定券 - 滿 800 折 100',
    iconClass: 'fas fa-utensils',
    imageUrl: restaurantImg,
    restaurant_id: 2,
    food_category_id: null,
    plan_id: null,
    min_spend: 800,
    discount_value: 100,
    start_time: '2025-06-01',
    end_time: '2025-06-30',
    description: '僅限餐廳 ID 2'
  },
  {
    id: 3,
    title: '餐點限定券 - 炸雞類折 30',
    iconClass: 'fas fa-hamburger',
    imageUrl: foodImg,
    restaurant_id: null,
    food_category_id: 5,
    plan_id: null,
    min_spend: 300,
    discount_value: 30,
    start_time: '2025-06-01',
    end_time: '2025-06-30',
    description: '僅限分類 ID 5'
  },
  {
    id: 4,
    title: '會員限定券 - VIP 9 折',
    iconClass: 'fas fa-crown',
    imageUrl: memberImg,
    restaurant_id: null,
    food_category_id: null,
    plan_id: 1,
    min_spend: 0,
    discount_value: 10,
    start_time: '2025-06-01',
    end_time: '2025-06-30',
    description: 'VIP 會員專用'
  }
])



// 後端完成後，這段就可以改呼叫 API
// const loadPromotions = async () => {
//   const res = await fetch('/api/promotions/available')
//   promotionList.value = await res.json()
// }
// onMounted(() => {
//   loadPromotions()
// })

const handleSelected = (promotion) => {
  selected.value = promotion
  console.log('已選擇：', promotion)
}
</script>

<style scoped>
.container {
  max-width: 600px;
}
</style>
