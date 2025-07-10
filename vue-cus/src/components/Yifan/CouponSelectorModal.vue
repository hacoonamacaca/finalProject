<template>
  <Teleport to="body">
    <transition name="fade">
      <div v-if="show" class="overlay" @click.self="cancel">
        <transition name="popup">
          <div class="popup-box">
            <div class="popup-header">
              <h5>選擇優惠券</h5>
              <button class="btn-close" @click="cancel">✕</button>
            </div>

            <!-- 手動輸入 -->
            <div class="input-group mb-3">
              <input
                type="text"
                class="form-control"
                v-model="manualCode"
                placeholder="輸入優惠碼"
              />
              <button class="btn btn-success" @click="applyManualCode">使用</button>
            </div>

            <hr />

            <!-- Tabs -->
            <ul class="nav nav-tabs mb-3">
              <li class="nav-item" v-for="tab in tabs" :key="tab.value">
                <button
                  class="nav-link"
                  :class="{ active: activeTab === tab.value }"
                  @click="activeTab = tab.value"
                >
                  <i :class="tab.iconClass" class="me-2"></i>{{ tab.label }}
                </button>
              </li>
            </ul>

            <!-- 券列表 -->
            <div v-if="filteredPromotions.length > 0">
              <div
                v-for="promotion in filteredPromotions"
                :key="promotion.id"
                class="card mb-2 shadow-sm voucher-card"
                :class="{ 'opacity-50': promotion.minSpend > cartAmount }"
              >
                <div class="card-body d-flex align-items-center">
                  <!-- 左圖 -->
                  <div class="voucher-left me-3 d-flex align-items-center justify-content-center">
                    <img :src="promotion.imageUrl" alt="icon" class="voucher-img" />
                  </div>

                  <!-- 中間文字 -->
                  <div class="voucher-content flex-grow-1">
                    <h5>{{ promotion.title }}</h5>
                    <p>{{ getDiscountText(promotion) }}</p>
                    <p>
                      有效期限：
                      {{ formatDate(promotion.startTime) }} ~ {{ formatDate(promotion.endTime) }}
                    </p>
                    <p class="small text-muted">{{ promotion.description }}</p>
                    <small v-if="promotion.minSpend > cartAmount" class="text-danger">
                      未達到最低消費門檻
                    </small>
                  </div>

                  <!-- 右側按鈕 -->
                  <div class="d-flex align-items-center ms-3">
                    <button
                      class="btn btn-success"
                      :disabled="promotion.minSpend > cartAmount"
                      @click="selectPromotion(promotion)"
                    >
                      使用
                    </button>
                  </div>
                </div>
              </div>
            </div>
            <div v-else>
              <p class="text-muted text-center">此分類沒有符合條件的優惠券</p>
            </div>
          </div>
        </transition>
      </div>
    </transition>
  </Teleport>
</template>

<script setup>
import { ref, computed, watch } from 'vue'

const props = defineProps({
  show: Boolean,
  promotions: Array,
  cartAmount: Number
})

const emits = defineEmits(['update:show', 'selected', 'applyCode'])

const tabs = [
  { label: '全部', value: 'all', iconClass: 'fas fa-folder-open' },
  { label: '全平台', value: 'global', iconClass: 'fas fa-globe' },
  { label: '餐廳限定', value: 'restaurant', iconClass: 'fas fa-utensils' },
  { label: '餐點限定', value: 'food', iconClass: 'fas fa-hamburger' },
  { label: '會員限定', value: 'member', iconClass: 'fas fa-crown' }
]
//折扣條件判斷(%或金額)
const getDiscountText = (p) => {
  if (p.discountType === 'amount') {
    return `滿 $ ${p.minSpend} 折 ${p.discountValue} 元`
  }
  if (p.discountType === 'percent') {
    return `滿 $ ${p.minSpend} 打 ${p.discountValue} 折`
  }
  return '優惠活動'
}

//時間格式
const formatDate = (datetimeStr) => {
  if (!datetimeStr) return '未知'
  const date = new Date(datetimeStr)
  if (isNaN(date)) return '格式錯誤'
  return `${date.getFullYear()}/${date.getMonth() + 1}/${date.getDate()}`
}

const activeTab = ref('all')
const selectedPromotion = ref(null)
const manualCode = ref('')

const filteredPromotions = computed(() => {
  if (activeTab.value === 'all') return props.promotions
  return props.promotions.filter(p => p.type === activeTab.value)
})


watch(
  () => props.show,
  (newVal) => {
    if (newVal) {
      selectedPromotion.value = null
      manualCode.value = ''
      activeTab.value = 'all'
    }
  }
)

const selectPromotion = (promotion) => {
  emits('selected', promotion)
  emits('update:show', false)
}

const cancel = () => {
  emits('update:show', false)
}

const applyManualCode = () => {
  if (manualCode.value.trim()) {
    emits('applyCode', manualCode.value.trim())
    manualCode.value = ''
  } else {
    alert('請輸入優惠碼')
  }
}
</script>

<style scoped>
.overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.4);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 9999;
}

.popup-box {
  background: #fff;
  width: 100%;
  max-width: 600px;
  border-radius: 12px;
  padding: 20px;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.2);
}

.popup-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin: 10px 0px 20px 0px;
}

.btn-close {
  background: none;
  border: none;
  font-size: 20px;
  cursor: pointer;
}


/* 優惠券樣式 */
.voucher-card {
  min-height: 180px; /* 可調整為你希望的高度 */
}

.voucher-left {
  width: 100px;
  height: 80px;
}

.voucher-img {
  max-width: 100px;
  max-height: 100px;
  object-fit: contain;
}

.voucher-content {
  padding-left: 20px;   /* 讓文字稍微往右縮排 */
  line-height: 0.5;     /* 預設大概是 1.6，可改小一點讓文字行距更緊 */

}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

.popup-enter-active,
.popup-leave-active {
  transition: transform 0.3s;
}
.popup-enter-from {
  transform: scale(0.8);
}
.popup-leave-to {
  transform: scale(0.8);
}
</style>
