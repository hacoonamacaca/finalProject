<template>
    <RestaurantTemplate :restaurant="restaurant" />
</template>

<script setup>
import RestaurantTemplate from "@/components/KTlu/RestaurantTemplate.vue";
defineProps(["restaurant"]);
</script>
