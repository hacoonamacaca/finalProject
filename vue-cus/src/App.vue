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

<style>
/* 整個專案前台背景色，到時候看要不要拉一個全站的頁首頁尾的css*/
html, body {
  background-color: #fff9f0;
}
</style>