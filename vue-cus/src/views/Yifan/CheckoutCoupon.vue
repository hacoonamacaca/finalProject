<template>
  <div class="container my-5">
    <h2>結帳頁</h2>
    <p>原價：{{ cartAmount }} 元</p>
    <p v-if="selected">折扣後金額：{{ discountedTotal }} 元</p>

    <!-- <button class="btn btn-primary mb-3" @click="show = true"> -->
    <button class="btn btn-primary mb-3" @click="openCouponModal()">
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
    <!-- 結帳按鈕 -->
    <button class="btn btn-success" @click="submitOrder">
  模擬送出訂單（含優惠券）
</button>
<p v-if="selected">折扣：{{ cartAmount - discountedTotal }} 元</p>

  </div>
</template>

<script setup>

// 💡 模擬登入使用者與購物車商店
const currentUser = { id: 2 }
const currentStore = { id: 3 }

import { ref, computed } from 'vue'
import axios from '@/plungins/axios.js'
import CouponSelectorModal from '@/components/Yifan/CouponSelectorModal.vue'
import globalImg from '@/assets/vouchers/global.png'
import restaurantImg from '@/assets/vouchers/restaurant.png'
import foodImg from '@/assets/vouchers/food.png'
import memberImg from '@/assets/vouchers/member.png'
import Swal from 'sweetalert2'

// 購物車金額
const discountedTotal = computed(() => {
  if (!selected.value) return cartAmount.value

  const discount = selected.value.discountType === 'amount'
    ? selected.value.discountValue
    : cartAmount.value * (1 - selected.value.discountValue / 10)

  return Math.max(0, Math.floor(cartAmount.value - discount))
})


// 彈窗顯示控制
const show = ref(false)
// 已選擇的券
const selected = ref(null)
// 購物車金額（範例）
const cartAmount = ref(1000)


// 優惠券列表（從後端載入）
const promotionList = ref([])


const openCouponModal = async () => {
  try {
    const response = await axios.get('/promotions/available', {
  params: {
    userId: currentUser.id,
    storeId: currentStore.id,
    amount: cartAmount.value
  }
})

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
        type
      }
    })

    show.value = true
  } catch (error) {
    console.error('載入結帳可用優惠券失敗', error)
  }
}



const handleSelected = (promotion) => {
  selected.value = promotion
  console.log('已選擇：', promotion)
}


//訂單結帳
const submitOrder = async () => {
  if (!selected.value) {
    // ⚠️ 未選擇優惠券的提醒
    Swal.fire({
      icon: 'warning',
      title: '請先選擇優惠券',
      confirmButtonText: '了解'
    })
    return
  }

  const orderPayload = {
    userId: currentUser.id,
    storeId: currentStore.id,
    promotionId: selected.value ? selected.value.id : null,
    total: discountedTotal.value,
    status: 'COMPLETED',
  }

  try {
    const res = await axios.post('/api/orders', orderPayload, {
      headers: { 'Content-Type': 'application/json' }
    })

    // ✅ 成功彈窗
    Swal.fire({
      icon: 'success',
      title: '訂單送出成功！',
      html: `訂單編號：<strong>${res.data.id}</strong>`,
      confirmButtonText: '太好了！'
    })

  } catch (err) {
    console.error('送出訂單失敗', err)

    // ❌ 失敗提示
    Swal.fire({
      icon: 'error',
      title: '訂單送出失敗',
      text: '請稍後再試一次',
      confirmButtonText: '關閉'
    })
  }
}
</script>

<style scoped>
.container {
  max-width: 600px;
}
</style>
