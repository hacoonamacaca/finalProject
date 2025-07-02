<template>
  <Navigation v-if="$route.path !== '/'" />

  <div>
    <RouterView></RouterView>
  </div>
</template>
<!-- 在 index.html 加 -->

<script setup>
import { RouterView } from 'vue-router';
import Home from "@/views/Jimmy/Home.vue"
import Navigation from '@/components/Navigation.vue';
// import Navigation from "@/views/Navigation.vue"
</script>

<style>
/* 整個專案前台背景色，到時候看要不要拉一個全站的頁首頁尾的css*/
html, body {
  background-color: #fff9f0;
}
</style>