<template>
    <div class="restaurant-map">
        <h4 class="map-title">地圖</h4>
        <div class="map-container">
            <iframe :src="mapUrl" allowfullscreen :title="restaurant.name + '地圖'" class="map-frame"></iframe>
        </div>
    </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
    restaurant: {
        type: Object,
        required: true
    }
})

const mapUrl = computed(() => {
    return `https://www.google.com/maps?q=${encodeURIComponent(props.restaurant.address)}&output=embed`
})
</script>

<style scoped>
.restaurant-map {
    width: 50%;
    margin: 2rem 0;
    padding: 0 1rem;
}

.map-title {
    color: #ff6c00;
    font-size: 1.25rem;
    font-weight: 600;
    margin-bottom: 1rem;
}

.map-container {
    position: relative;
    width: 100%;
    height: 0;
    padding-bottom: 56.25%;
    /* 16:9 比例 */
    border-radius: 8px;
    overflow: hidden;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.map-frame {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    border: none;
}

/* 響應式設計 */
@media (max-width: 768px) {
    .restaurant-map {
        width: 70%;
    }

    .map-container {
        padding-bottom: 75%;
        /* 移動端使用4:3比例 */
    }
}

@media (max-width: 480px) {
    .restaurant-map {
        width: 90%;
    }
}
</style>