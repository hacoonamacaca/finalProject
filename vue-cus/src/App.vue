<template>
  <Navigation v-if="$route.path !== '/'" />

  <div>
    <RouterView></RouterView>
  </div>
</template>

<script setup>
import { RouterView } from 'vue-router';
// import Home from "@/views/Home.vue"
import Navigation from "@/components/Navigation.vue"
</script>

<style></style>
