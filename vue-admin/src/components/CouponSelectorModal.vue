<template>
  <Teleport to="body">
    <div
      v-if="show"
      class="modal fade show d-block"
      tabindex="-1"
      style="background: rgba(0,0,0,0.5);"
    >
      <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">

          <!-- Header -->
          <div class="modal-header">
            <h5 class="modal-title">選擇優惠券</h5>
            <button type="button" class="btn-close" @click="cancel"></button>
          </div>

          <!-- Body -->
          <div class="modal-body">
            <!-- 1️⃣ 手動輸入優惠碼 -->
            <div class="input-group mb-3">
              <input
                type="text"
                class="form-control"
                v-model="manualCode"
                placeholder="輸入優惠碼"
              />
              <button class="btn btn-warning" @click="applyManualCode">使用</button>
            </div>

            <hr />

            <!-- 2️⃣ 可用優惠券清單 -->
            <div
              v-for="promotion in promotions"
              :key="promotion.id"
              class="card mb-2"
              :class="{ 'opacity-50': promotion.min_spend > cartAmount }"
            >
              <div class="card-body d-flex justify-content-between align-items-center">
                <div>
                  <h5 class="card-title">{{ promotion.title }}</h5>
                  <p class="card-text mb-1">滿 {{ promotion.min_spend }} 折 {{ promotion.discount_value }}</p>
                  <p class="card-text mb-1">有效期限：{{ promotion.start_time }} ~ {{ promotion.end_time }}</p>
                  <p class="card-text mb-0">適用活動：{{ promotion.description }}</p>
                  <small
                    v-if="promotion.min_spend > cartAmount"
                    class="text-danger"
                  >未達到最低消費門檻</small>
                </div>

                <!-- ✅ 放大後的 radio 包一層 -->
                <div class="promotion-radio">
                  <input
                    type="radio"
                    :disabled="promotion.min_spend > cartAmount"
                    :value="promotion"
                    v-model="selectedPromotion"
                  />
                </div>
              </div>
            </div>
          </div>

          <!-- Footer -->
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" @click="cancel">
              取消
            </button>
            <button
              type="button"
              class="btn btn-primary "
              @click="confirm"
            >
              確定
            </button>
          </div>

        </div>
      </div>
    </div>
  </Teleport>
</template>

<script setup>
import { ref } from 'vue'

const props = defineProps({
  show: Boolean,
  promotions: Array,
  cartAmount: Number,
})

const emits = defineEmits(['update:show', 'selected', 'applyCode'])

const selectedPromotion = ref(null)
const manualCode = ref('')

const confirm = () => {
  emits('selected', selectedPromotion.value)
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
/* ✅ 放大右側 radio 只影響這裡 */
.promotion-radio input[type="radio"] {
  transform: scale(1.2);
  cursor: pointer;
  margin: 0;
}
</style>
