<!-- 優惠券選擇彈窗 -->
<template>
  <Teleport to="body">
    <transition name="fade">
      <div v-if="show" class="overlay" @click.self="cancel">
        <transition name="popup">
          <div class="popup-box">
            <div class="popup-header">
              <h5>選擇優惠券</h5>
              <button class="btn-close" @click="cancel">X</button>
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
                  {{ tab.icon }} {{ tab.label }}
                </button>
              </li>
            </ul>

            <!-- 優惠券列表 -->
            <div v-if="filteredPromotions.length > 0">
              <VoucherCard
                v-for="promotion in filteredPromotions"
                :key="promotion.id"
                :promotion="promotion"
                :cartAmount="cartAmount"
                @use="selectPromotion"
              />
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
import VoucherCard from '@/components/VoucherCard.vue'

const props = defineProps({
  show: Boolean,
  promotions: Array,
  cartAmount: Number
})

const emits = defineEmits(['update:show', 'selected', 'applyCode'])

const tabs = [
  { label: '全部', value: 'all', icon: '📂' },
  { label: '全平台', value: 'global', icon: '🌐' },
  { label: '餐廳限定', value: 'restaurant', icon: '🍽️' },
  { label: '餐點限定', value: 'food', icon: '🍔' },
  { label: '會員限定', value: 'member', icon: '👑' },
  { label: '歷史紀錄', value: 'history', icon: '🕓' }
]

const activeTab = ref('all')
const selectedPromotion = ref(null)
const manualCode = ref('')

const filteredPromotions = computed(() => {
  return props.promotions.filter((p) => {
    // 歷史紀錄 → 顯示已使用過的
    if (activeTab.value === 'history') return p.used

    // 其他 tab → 排除已使用過的
    if (p.used) return false

    switch (activeTab.value) {
      case 'all':
        return p.min_spend <= props.cartAmount // ✅「全部」只顯示可用券
      case 'global':
        return !p.restaurant_id && !p.food_category_id && !p.plan_id
      case 'restaurant':
        return !!p.restaurant_id
      case 'food':
        return !!p.food_category_id
      case 'member':
        return !!p.plan_id
      default:
        return false
    }
  })
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
  max-width: 700px;
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
</style>
