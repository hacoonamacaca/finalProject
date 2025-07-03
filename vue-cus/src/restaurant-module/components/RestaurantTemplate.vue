<template>
    <div class="restaurant-container">
        <!-- 餐廳橫幅 -->
        <RestaurantBanner :restaurant="restaurant" />

        <!-- 餐廳名稱區域 -->
        <div class="restaurant-header">
            <h1 class="restaurant-main-title">{{ restaurant.name }}</h1>
        </div>

        <!-- 預約按鈕 -->
        <div class="text-center my-4">
            <p class="reserve-text">立即預約享受美食</p>
            <!-- 使用事件冒泡或 $emit 來處理滾動 -->
            <button class="btn btn-warning text-white" @click="scrollToReservation">
                🍽️ 立即預約
            </button>
        </div>

        <!-- 餐廳資訊 -->
        <RestaurantInfo :restaurant="restaurant" />

        <!-- 餐廳菜單 -->
        <RestaurantMenu :restaurant="restaurant" />

        <!-- 預約表單 -->
        <div id="reservation-section">
            <ReservationForm :restaurant-id="restaurant.id.toString()" />
        </div>

        <!-- 地圖 -->
        <RestaurantMap :restaurant="restaurant" />

        <!-- 頁腳 -->
        <RestaurantFooter :restaurant="restaurant" />
    </div>
</template>

<script setup>
import RestaurantBanner from './RestaurantBanner.vue'
import RestaurantInfo from './RestaurantInfo.vue'
import RestaurantMenu from './RestaurantMenu.vue'
import RestaurantMap from './RestaurantMap.vue'
import ReservationForm from './ReservationForm.vue'
import RestaurantFooter from './RestaurantFooter.vue'

// 接收餐廳資料
defineProps({
    restaurant: {
        type: Object,
        required: true
    }
})

const scrollToReservation = () => {
    const element = document.getElementById('reservation-section')
    if (element) {
        element.scrollIntoView({ behavior: 'smooth' })
    }
}
</script>

<style scoped>
.restaurant-container {
    max-width: 1200px;
    margin: 0 auto;
    padding: 0;
    background: #fff;
}

.restaurant-header {
    text-align: left;
    padding: 2rem 1rem;
    background: #fff;
    width: 100%;
    margin: 0 0;
}

.restaurant-main-title {
    font-size: 2.5rem;
    font-weight: 700;
    color: #333;
    margin: 0;
    letter-spacing: 1px;
}

.text-center {
    text-align: left;
}

.my-4 {
    margin: 2rem 0;
    padding: 0 1rem;
    width: 100%;
}

.reserve-text {
    font-size: 1.1rem;
    color: #666;
    margin-bottom: 1.5rem;
}

.btn {
    display: inline-block;
    padding: 12px 32px;
    border: none;
    border-radius: 25px;
    text-decoration: none;
    font-weight: 600;
    cursor: pointer;
    transition: all 0.3s ease;
    font-size: 16px;
}

.btn-warning {
    background-color: #ff6c00;
    color: white;
    box-shadow: 0 4px 15px rgba(255, 108, 0, 0.3);
}

.btn-warning:hover {
    background-color: #e55a00;
    transform: translateY(-2px);
    box-shadow: 0 6px 20px rgba(255, 108, 0, 0.4);
}

.text-white {
    color: white !important;
}

#reservation-section {
    scroll-margin-top: 80px;
    background: #f8f9fa;
    padding: 3rem 0;
}

/* 響應式設計 */
@media (max-width: 768px) {
    .restaurant-main-title {
        font-size: 2rem;
    }

    .restaurant-header {
        padding: 1.5rem 1rem;
        width: 100%;
    }

    .my-4 {
        margin: 1.5rem 0;
        width: 100%;
    }
}

@media (max-width: 480px) {
    .restaurant-main-title {
        font-size: 1.75rem;
    }

    .restaurant-header {
        width: 100%;
    }

    .my-4 {
        width: 100%;
    }
}
</style>