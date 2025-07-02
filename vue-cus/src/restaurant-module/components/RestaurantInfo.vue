<template>
    <div class="restaurant-info">
        <h3 class="info-title">餐廳資訊</h3>
        <ul class="info-list">
            <li class="info-item">
                <i class="pi pi-map-marker info-icon"></i>
                <span>{{ restaurant.address }}</span>
            </li>
            <li class="info-item">
                <i class="pi pi-phone info-icon"></i>
                <a :href="'tel:' + restaurant.phone" class="phone-link">{{ restaurant.phone }}</a>
            </li>
        </ul>
        <h4 class="hours-title">營業時間</h4>
        <p class="hours-text">{{ restaurant.businessHours }}</p>
    </div>
</template>

<script setup>
defineProps({
    restaurant: {
        type: Object,
        required: true
    }
})
</script>

<style scoped>
.restaurant-info {
    width: 100%;
    margin: 2rem 0;
    padding: 1.5rem;
    background: #f8f9fa;
    border-radius: 8px;
}

.info-title {
    color: #ff6c00;
    font-size: 1.25rem;
    font-weight: 600;
    margin-bottom: 1rem;
}

.info-list {
    list-style: none;
    padding: 0;
    margin: 0 0 1.5rem 0;
}

.info-item {
    display: flex;
    align-items: center;
    margin-bottom: 0.75rem;
    color: #333;
}

.info-icon {
    color: #ff6c00;
    margin-right: 0.75rem;
    font-size: 1.1rem;
    width: 20px;
    text-align: center;
}

.phone-link {
    color: inherit;
    text-decoration: none;
}

.phone-link:hover {
    color: #ff6c00;
    text-decoration: underline;
}

.hours-title {
    color: #ff6c00;
    font-size: 1.1rem;
    font-weight: 600;
    margin-bottom: 0.5rem;
}

.hours-text {
    color: #333;
    margin: 0;
    line-height: 1.5;
}

/* 響應式設計 */
@media (max-width: 768px) {
    .restaurant-info {
        width: 100%;
        padding: 1rem;
    }

    .info-title,
    .hours-title {
        font-size: 1.1rem;
    }
}

@media (max-width: 480px) {
    .restaurant-info {
        width: 100%;
    }
}
</style>