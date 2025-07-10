<template>
    <div class="reservation-form-container goldenbowl-restaurant-theme">
        <div class="guest-date-section">
            <div class="guest-selection">
                <label class="form-label">用餐人數</label>
                <div class="select-group">
                    <Select v-model="selectedGuest" :options="guests" optionLabel="name" placeholder="請選擇用餐人數" checkmark
                        :highlightOnSelect="false" class="guest-select" />
                    <Select v-model="selectChild" :options="children" optionLabel="name" placeholder="請選擇用餐人數" checkmark
                        :highlightOnSelect="false" class="child-select" />
                </div>

                <div class="date-selection">
                    <label class="form-label">用餐日期</label>
                    <DatePicker v-model="date" placeholder="請選擇" dateFormat="yy/mm/dd" :minDate="minDate"
                        :selectableDate="isDateSelectable" class="date-picker" />
                </div>

                <div class="date-hint">
                    <small class="hint-text">
                        <i class="pi pi-info-circle"></i>
                        僅顯示有時間段的可預約日期・可接受1-6位訂位（含大人與小孩）・超過6人請電話預約
                        <a href="tel:0227845677" class="phone-link">02-2784-5677</a>
                    </small>
                </div>
            </div>
        </div>

        <hr class="divider" />

        <div class="time-section">
            <h5 class="section-title">選擇用餐時間</h5>
            <div v-if="loading" class="loading-state">
                <i class="pi pi-spinner pi-spin"></i> 載入時間段中...
            </div>
            <div v-else-if="!date" class="empty-state">
                請先選擇用餐日期
            </div>
            <div v-else-if="!timeSections || timeSections.length === 0" class="empty-state">
                <i class="pi pi-info-circle"></i> 當日暫無可用時間段
            </div>
            <TimePickerSectioned v-else v-model="selectedTime" :sections="timeSections"
                :disabledSlots="disabledTimeSlots || []" />
        </div>

        <h4 class="section-title">預約訂位</h4>

        <!-- 姓名 -->
        <div class="form-group">
            <label class="form-label">姓名</label>
            <InputText v-model="name" class="form-input" placeholder="請輸入姓名" />
        </div>

        <!-- 電話 -->
        <div class="form-group">
            <label class="form-label">電話</label>
            <InputText v-model="phone" class="form-input" placeholder="請輸入電話" />
        </div>

        <!-- 備註 -->
        <div class="form-group">
            <label class="form-label">備註</label>
            <InputText v-model="note" class="form-input" placeholder="(選填)" />
        </div>

        <Button label="送出預約" icon="pi pi-check" class="submit-btn" @click="submit" />
    </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import Swal from 'sweetalert2'
import InputText from 'primevue/inputtext'
import Button from 'primevue/button'
import DatePicker from 'primevue/datepicker'
import Select from 'primevue/select'
import TimePickerSectioned from './TimePickerSectioned.vue'
import '@/assets/css/restaurant-theme.css'
import {
    getTimeSlotsForDate,
    groupTimeSlotsByPeriod,
    formatDateToString
} from '@/utils/timeSlotUtils.js'
import { fetchRestaurantTimeSlots, fetchBookedTimeSlots } from '@/services/timeSlotService.js'

// 定義 props 接收餐廳 ID
const props = defineProps({
    restaurantId: {
        type: String,
        required: true
    }
})

const date = ref(null)
const name = ref('')
const phone = ref('')
const note = ref('')
const selectedTime = ref('')
const minDate = new Date()

const selectedGuest = ref();
const selectChild = ref();
const guests = ref([
    { name: '1 位大人' },
    { name: '2 位大人' },
    { name: '3 位大人' },
    { name: '4 位大人' },
    { name: '5 位大人' }
]);
const children = ref([
    { name: '0 位小孩' },
    { name: '1 位小孩' },
    { name: '2 位小孩' },
    { name: '3 位小孩' },
    { name: '4 位小孩' }
]);

// 後台數據狀態 - 確保初始值為空數組
const timeSlots = ref([])
const bookedSlots = ref([])
const loading = ref(false)

// 從後台抓取時間段數據
const fetchTimeSlots = async () => {
    loading.value = true
    try {
        // 使用服務層來獲取數據
        const result = await fetchRestaurantTimeSlots(props.restaurantId)
        timeSlots.value = Array.isArray(result) ? result : []
        console.log('成功獲取時間段數據:', timeSlots.value.length, '筆')

        // 獲取已預訂的時間段
        await fetchBookedSlots()
    } catch (error) {
        console.error('抓取時間段失敗:', error)
        timeSlots.value = []

        // 顯示錯誤訊息
        await Swal.fire({
            title: '載入失敗',
            text: '無法載入用餐時間，請重新整理頁面或聯繫客服',
            icon: 'error',
            confirmButtonText: '確定'
        })
    } finally {
        loading.value = false
    }
}

// 從後台抓取已預訂的時間段
const fetchBookedSlots = async () => {
    try {
        const result = await fetchBookedTimeSlots(props.restaurantId)
        bookedSlots.value = Array.isArray(result) ? result : []
        console.log('成功獲取已預訂時間段:', bookedSlots.value.length, '筆')
    } catch (error) {
        console.error('抓取已預訂時間段失敗:', error)
        bookedSlots.value = []
    }
}

// 其他邏輯保持不變...
const availableDates = computed(() => {
    if (!timeSlots.value || !Array.isArray(timeSlots.value) || timeSlots.value.length === 0) return new Set()

    try {
        const today = new Date()
        const validDates = timeSlots.value
            .map(slot => {
                // 確保 slot 和 slot.date 存在
                if (!slot || !slot.date || typeof slot.date !== 'string') {
                    return null
                }
                return slot.date
            })
            .filter(dateStr => {
                if (!dateStr) return false
                try {
                    const slotDate = new Date(dateStr)
                    // 檢查日期是否有效
                    if (isNaN(slotDate.getTime())) return false
                    return slotDate >= today
                } catch (error) {
                    console.error('處理日期時發生錯誤:', dateStr, error)
                    return false
                }
            })

        return new Set(validDates)
    } catch (error) {
        console.error('處理可用日期時發生錯誤:', error)
        return new Set()
    }
})

const isDateSelectable = (date) => {
    if (!date || !(date instanceof Date)) return false
    try {
        const year = date.getFullYear()
        const month = String(date.getMonth() + 1).padStart(2, '0')
        const day = String(date.getDate()).padStart(2, '0')
        const dateString = `${year}-${month}-${day}`
        return availableDates.value.has(dateString)
    } catch (error) {
        console.error('檢查日期可選性時發生錯誤:', date, error)
        return false
    }
}

const timeSections = computed(() => {
    if (!date.value || !timeSlots.value || timeSlots.value.length === 0) return []
    try {
        const dateString = formatDateToString(date.value)
        if (!dateString) {
            console.error('無法格式化日期:', date.value)
            return []
        }

        // 現在 getTimeSlotsForDate 可以接受字符串參數
        const daySlots = getTimeSlotsForDate(timeSlots.value, dateString)
        if (!Array.isArray(daySlots)) {
            console.error('getTimeSlotsForDate 返回了非數組值:', daySlots)
            return []
        }

        const sections = groupTimeSlotsByPeriod(daySlots)
        return Array.isArray(sections) ? sections : []
    } catch (error) {
        console.error('處理時間段時發生錯誤:', error)
        return []
    }
})

const disabledTimeSlots = computed(() => {
    if (!date.value || !bookedSlots.value) return []
    try {
        const dateString = formatDateToString(date.value)
        if (!dateString) {
            console.error('無法格式化日期:', date.value)
            return []
        }

        const filtered = bookedSlots.value
            .filter(slot => slot && slot.date === dateString)
            .map(slot => slot.startTime)
            .filter(time => time) // 過濾掉可能的 undefined

        return Array.isArray(filtered) ? filtered : []
    } catch (error) {
        console.error('處理已預訂時間段時發生錯誤:', error)
        return []
    }
})

const submit = async () => {
    if (!name.value || !phone.value || !selectedTime.value || !date.value) {
        await Swal.fire({
            title: '請填寫完整資訊',
            text: '請確認姓名、電話、日期和時間都已填寫',
            icon: 'warning',
            confirmButtonText: '確定'
        })
        return
    }

    try {
        await Swal.fire({
            title: '預約成功！',
            text: `${name.value} 您好，已為您預約 ${formatDateToString(date.value)} ${selectedTime.value} 的位子`,
            icon: 'success',
            confirmButtonText: '確定'
        })

        // 重置表單
        name.value = ''
        phone.value = ''
        note.value = ''
        selectedTime.value = ''
        date.value = null
        selectedGuest.value = null
        selectChild.value = null
    } catch (error) {
        await Swal.fire({
            title: '預約失敗',
            text: '請稍後再試或聯繫客服',
            icon: 'error',
            confirmButtonText: '確定'
        })
    }
}

onMounted(() => {
    fetchTimeSlots()
})

watch(() => props.restaurantId, () => {
    fetchTimeSlots()
})
</script>

<style scoped>
.reservation-form-container {
    width: 100%;
    margin: 0 0;
    padding: 0;
}

.guest-date-section {
    margin-bottom: 2rem;
}

.guest-selection {
    background: var(--restaurant-bg-light);
    border: 1px solid var(--restaurant-border-light);
    padding: 1.5rem;
    border-radius: 12px;
    margin-bottom: 1rem;
    box-shadow: 0 2px 8px var(--restaurant-shadow-light);
}

.form-label {
    display: block;
    margin-bottom: 0.5rem;
    font-weight: 600;
    color: var(--restaurant-text-primary);
}

.select-group {
    display: flex;
    gap: 1rem;
    margin-bottom: 1.5rem;
}

.guest-select,
.child-select {
    flex: 1;
}

.date-selection {
    margin-bottom: 1rem;
}

.date-picker {
    width: 100%;
}

.date-hint {
    margin-top: 0.5rem;
}

.hint-text {
    color: var(--restaurant-text-secondary);
    font-size: 0.9rem;
    line-height: 1.4;
    padding: 0.75rem;
    background: var(--restaurant-bg-secondary);
    border-radius: 6px;
    border: 1px solid var(--restaurant-border-light);
}

.phone-link {
    color: var(--restaurant-primary);
    text-decoration: none;
    font-weight: 500;
}

.phone-link:hover {
    text-decoration: underline;
    color: var(--restaurant-primary-hover);
}

.divider {
    border: none;
    border-top: 2px solid var(--restaurant-border-light);
    margin: 2rem 0;
}

.time-section {
    margin-bottom: 2rem;
}

.section-title {
    color: var(--restaurant-primary);
    margin-bottom: 1rem;
    font-size: 1.25rem;
    font-weight: 600;
    text-shadow: 0 1px 2px var(--restaurant-shadow-light);
}

.loading-state,
.empty-state {
    text-align: center;
    padding: 1.5rem;
    color: var(--restaurant-text-secondary);
    background: var(--restaurant-bg-light);
    border-radius: 8px;
    border: 1px solid var(--restaurant-border-light);
}

.form-group {
    margin-bottom: 1.5rem;
}

.form-input {
    width: 100%;
    padding: 0.75rem;
    border: 1px solid var(--restaurant-border-medium);
    background: var(--restaurant-bg-primary);
    border-radius: 8px;
    font-size: 1rem;
    color: var(--restaurant-text-primary);
    transition: all 0.2s ease;
}

.form-input:focus {
    outline: none;
    border-color: var(--restaurant-primary);
    box-shadow: 0 0 0 2px var(--restaurant-shadow-light);
}

.submit-btn {
    background: var(--restaurant-gradient-primary) !important;
    border: 1px solid var(--restaurant-primary-light) !important;
    padding: 0.75rem 2rem;
    font-size: 1rem;
    font-weight: 600;
    border-radius: 8px;
    width: 100%;
    color: var(--restaurant-text-primary) !important;
    box-shadow: 0 2px 8px var(--restaurant-shadow-light);
    transition: all 0.3s ease !important;
}

.submit-btn:hover {
    background: var(--restaurant-primary-hover) !important;
    border-color: var(--restaurant-primary) !important;
    transform: translateY(-2px);
    box-shadow: 0 4px 16px var(--restaurant-shadow-medium) !important;
}

/* 響應式設計 */
@media (max-width: 768px) {
    .reservation-form-container {
        width: 100%;
        padding: 15px;
    }

    .select-group {
        flex-direction: column;
    }
}

@media (max-width: 480px) {
    .reservation-form-container {
        width: 100%;
    }
}
</style>