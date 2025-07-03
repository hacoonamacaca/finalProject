# 餐廳預約模組

一個完整的餐廳預約系統組件庫，可以輕鬆整合到任何 Vue.js 專案中。

## 🚀 快速開始

### 安裝依賴

```bash
npm install primevue primeicons sweetalert2
```

### 在主專案中使用

#### 方法一：全域註冊

```javascript
// main.js
import { createApp } from "vue";
import RestaurantModule from "./restaurant-module";

const app = createApp(App);
app.use(RestaurantModule);
```

#### 方法二：按需導入

```vue
<template>
  <RestaurantTemplate :restaurant="restaurant" />
</template>

<script setup>
import { RestaurantTemplate, getRestaurantById } from "./restaurant-module";

const restaurant = getRestaurantById(1);
</script>
```

## 📋 組件清單

### 核心組件

- `RestaurantTemplate` - 餐廳頁面模板（主要入口）
- `ReservationForm` - 預約表單
- `RestaurantBanner` - 餐廳橫幅
- `RestaurantInfo` - 餐廳資訊
- `RestaurantMenu` - 餐廳菜單
- `RestaurantMap` - 餐廳地圖
- `RestaurantFooter` - 餐廳頁腳
- `TimePickerSectioned` - 時間選擇器

### 資料結構

```javascript
// 餐廳資料格式
const restaurant = {
  id: 1,
  name: "Plants",
  image: "path/to/image",
  address: "台北市大安區忠孝東路四段181巷40號",
  phone: "02-2771-2050",
  businessHours: "週一至週日 11:00–20:00",
  menuTitle: "Plants 菜單",
  menuCategories: [{ name: "主餐" }, { name: "飲品" }, { name: "甜點" }],
  copyrightText: "© 2021 Plants All Rights Reserved",
};
```

## 🎨 樣式系統

模組使用 CSS 變數，可在主專案中自訂：

```css
:root {
  --primary-color: #ff6c00;
  --primary-light: #fff3e0;
  --text-orange: #ff6c00;
  --border-color: #eee;
  --shadow-md: 0 2px 8px rgba(0, 0, 0, 0.03);
}
```

## 📁 文件結構

```
restaurant-module/
├── index.js                 # 模組入口點
├── components/              # 所有組件
│   ├── RestaurantTemplate.vue
│   ├── ReservationForm.vue
│   ├── RestaurantBanner.vue
│   ├── RestaurantInfo.vue
│   ├── RestaurantMenu.vue
│   ├── RestaurantMap.vue
│   ├── RestaurantFooter.vue
│   └── TimePickerSectioned.vue
├── data/                    # 資料文件
│   ├── restaurants.js
│   └── timeslots_30d.json
├── services/                # 服務層
│   └── timeSlotService.js
├── utils/                   # 工具函數
│   └── timeSlotUtils.js
└── README.md               # 說明文檔
```

## 🔧 API 說明

### 組件 Props

#### RestaurantTemplate

- `restaurant` (Object, required) - 餐廳資料物件

#### ReservationForm

- `restaurantId` (String, required) - 餐廳 ID

### 工具函數

```javascript
import { getRestaurantById, timeSlotService } from "./restaurant-module";

// 根據ID獲取餐廳
const restaurant = getRestaurantById(1);

// 時間段相關工具
import {
  getTimeSlotsForDate,
  groupTimeSlotsByPeriod,
  formatDateToString,
} from "./restaurant-module";
```

## 💡 特色功能

- ✅ **完全模組化** - 可獨立運作，不依賴主專案樣式
- ✅ **Scoped CSS** - 樣式隔離，避免衝突
- ✅ **響應式設計** - 支援桌面和移動端
- ✅ **TypeScript 友好** - 提供完整的類型定義
- ✅ **無外部樣式依賴** - 所有樣式都內建在組件中
- ✅ **易於客製化** - 透過 CSS 變數調整主題色彩

## 🎯 整合建議

1. **樣式隔離** - 所有組件使用 scoped CSS，不會影響主專案
2. **資料格式一致** - 保持現有的餐廳和時間段資料格式
3. **漸進式整合** - 可以逐步替換現有組件
4. **效能優化** - 按需載入，不會增加主專案打包大小

## 🔄 遷移指南

從現有專案遷移到模組化結構：

1. 複製整個 `restaurant-module` 目錄到目標專案
2. 安裝必要依賴
3. 在目標專案中導入需要的組件
4. 調整路由和資料來源（如需要）

## 📞 技術支援

如有問題或建議，請聯繫開發團隊。
