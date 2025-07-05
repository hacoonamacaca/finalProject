<!-- src/components/Jimmy/PopularRestaurants.vue -->
<template>
<section class="popular-section" v-if="address">
    <h2>附近熱門美食</h2>
    <div class="restaurant-scroll">
    <div class="restaurant-card" v-for="restaurant in popularRestaurants" :key="restaurant.id">
        <img
        :src="restaurant.image"
        :alt="restaurant.name"
        @click="$router.push(`/restaurant/${restaurant.id}`)"
        style="cursor: pointer;"
        />
        <div class="info">
            <h3>
                {{ restaurant.name }} {{ restaurant.score }}★
            </h3>
        </div>
        <Comment :comments="restaurant.comments" :comment-count="restaurant.comments.length" />
    </div>
    </div>
</section>
</template>

<script setup>
import { computed } from 'vue';
import Comment from '@/components/Jimmy/Comment.vue';

// 定義 Props
const props = defineProps({
address: {
    type: String,
    required: true,
},
restaurants: {
    type: Array,
    required: true,
},
});

// 計算屬性：熱門餐廳
const popularRestaurants = computed(() => {
return [...props.restaurants]
    .sort((a, b) => b.popularityScore - a.popularityScore)
    .slice(0, 10);
});
</script>

<style scoped>
    .popular-section {
    padding: 10px;
    background-color: #fff;
    margin: 20px;
    border-radius: 8px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
    }

    .restaurant-scroll {
    display: flex;
    overflow-x: auto;
    gap: 10px;
    padding-bottom: 5px;
    }

    .restaurant-scroll::-webkit-scrollbar {
    height: 4px;
    }

    .restaurant-scroll::-webkit-scrollbar-thumb {
    background-color: #ffba20;
    border-radius: 4px;
    }

    .restaurant-scroll .restaurant-card {
    flex: 0 0 180px;
    width: 180px;
    }

    .restaurant-card img {
    width: 100%;
    height: 120px;
    object-fit: cover;
    }

    .restaurant-card .info {
    padding: 7.5px;
    }

    .restaurant-card h3 {
    font-size: 15px;
    margin-bottom: 4px;
    }
</style>