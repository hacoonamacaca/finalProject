<template>
    <RestaurantTemplate :restaurant="restaurant" />
</template>

<script setup>
import RestaurantTemplate from "@/components/KTlu/RestaurantTemplate.vue";
defineProps(["restaurant"]);
</script>



<!-- vue-cus/
├── src/
│   ├── components/
│   │   └── KTlu/              # 您的組件目錄
│   │       ├── RestaurantTemplate.vue
│   │       ├── ReservationForm.vue
│   │       ├── RestaurantBanner.vue
│   │       ├── RestaurantInfo.vue
│   │       ├── RestaurantMenu.vue
│   │       ├── RestaurantMap.vue
│   │       ├── RestaurantFooter.vue
│   │       ├── TimePickerSectioned.vue
│   │       ├── ItemDetailModal.vue
│   │       └── CartModal.vue
│   ├── data/                  # 資料目錄
│   │   ├── restaurants.js
│   │   └── timeslots_30d.json
│   ├── services/              # 服務目錄
│   │   └── timeSlotService.js
│   ├── utils/                 # 工具函數目錄
│   │   └── timeSlotUtils.js
│   └── restaurant-index.js    # 原index.js的備份 -->
