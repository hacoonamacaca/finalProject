<template>
    <footer class="restaurant-footer">
        {{ restaurant.copyrightText || '© 2021 Restaurant All Rights Reserved' }}
    </footer>
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
.restaurant-footer {
    text-align: center;
    color: #666;
    margin-top: 3rem;
    padding: 2rem 0;
    font-size: 0.9rem;
    border-top: 1px solid #eee;
}
</style>