<template>
    <Navigation></Navigation>

    <div>
        <RouterView></RouterView>
    </div>
</template>

<script setup>
import { RouterView } from 'vue-router';
import "bootstrap/dist/js/bootstrap.bundle.min.js"
import "bootstrap/dist/css/bootstrap.min.css"
import Home from "@/views/Home.vue"
</script>

<style></style>