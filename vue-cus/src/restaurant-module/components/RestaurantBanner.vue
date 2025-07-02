<template>
    <div class="restaurant-banner">
        <img :src="restaurant.image" :alt="restaurant.name + '店面圖片'" class="banner-image" />
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
.restaurant-banner {
    position: relative;
    width: 100%;
    height: 400px;
    margin: 0 0 0 0;
    overflow: hidden;
}

.banner-image {
    width: 100%;
    height: 100%;
    object-fit: cover;
    display: block;
}

/* 響應式設計 */
@media (max-width: 768px) {
    .restaurant-banner {
        height: 300px;
        width: 100%;
    }
}

@media (max-width: 480px) {
    .restaurant-banner {
        height: 250px;
        width: 100%;
    }
}
</style>