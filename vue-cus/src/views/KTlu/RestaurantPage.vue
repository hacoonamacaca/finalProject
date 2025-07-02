<template>
    <RestaurantTemplate :restaurant="restaurant" />
</template>

<script setup>
import { RestaurantTemplate } from "@/restaurant-module";
defineProps(["restaurant"]);
</script>
