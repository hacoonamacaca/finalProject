<!-- src/views/Jimmy/Home.vue -->
<template>
  <!-- 附近熱門美食 -->
  <PopularRestaurants :address="address" :restaurants="filteredRestaurants" />

  <!-- 搜尋與位置區域 -->
  <SearchSection v-model:initialSearch="searched" @search="updateSearchQuery" />

  <div class="filter-toggle" @click="toggleSidebar">篩選條件</div>
  <!-- 篩選與排序（頂部） -->
  <!-- <section class="filters">
    <TopFilterButtons :filters="filters" @update:filters="filters = $event" />
    <div class="sort">
      <select v-model="sortOption" @change="sortRestaurants">
        <option value="評分最高">評分最高</option>
        <option value="距離最近">距離最近</option>
        <option value="最快送達">最快送達</option>
      </select>
    </div>
  </section> -->

  <!-- 內容容器 -->
  <div class="content-container">
    <aside class="sidebar" :class="{ active: isSidebarActive }">
      <SidebarFilters
        :filters="filters"
        @update:filters="filters = $event"
        @update-score="updatescore"
        :availableCategories="uniqueCategoryNames" />
    </aside>

    <RestaurantListSection :restaurants="filteredRestaurants" />
  </div>

  <!-- 頁腳 -->
  <footer class="footer">
    <p>© 2025 外送平台. 版權所有。</p>
    <p>
      <a href="#">關於我們</a>
      <a href="#">聯繫我們</a>
      <a href="#">隱私政策</a>
      <a href="#">服務條款</a>
    </p>
  </footer>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue';
import { useRoute } from 'vue-router';
import TopFilterButtons from '@/components/Jimmy/TopFilterButtons.vue';
import SidebarFilters from '@/components/Jimmy/SidebarFilters.vue';
import PopularRestaurants from '@/components/Jimmy/PopularRestaurants.vue';
import SearchSection from '@/components/Jimmy/SearchSection.vue';
import RestaurantListSection  from "@/components/Jimmy/RestaurantListSection.vue"
import axios from 'axios';

const route = useRoute();
const address = ref('');
const API_URL = import.meta.env.VITE_API_URL;


// 從路由查詢參數獲取地址並設定給本地的 address
onMounted(() => {
  if (route.query.address) {
    address.value = route.query.address;
  }
  fetchStores(searchQuery.value);
});

// 監聽路由查詢參數的變化
watch(
  () => route.query.address,
  (newAddress) => {
    if (newAddress) {
      address.value = newAddress;
    } else {
      address.value = '';
    }
  },
  { immediate: true }
);

const isSidebarActive = ref(false);
const toggleSidebar = () => {
  isSidebarActive.value = !isSidebarActive.value;
};

// 搜索相關
const searched = ref(''); // 用於與 SearchSection 的 v-model 綁定
const searchQuery = ref(''); // 新增：實際用於觸發後端搜尋的查詢詞

// 處理來自 SearchSection 的搜尋事件
const updateSearchQuery = (searchTerm) => {
  console.log('Search.vue 收到搜尋內容:', searchTerm);
  searchQuery.value = searchTerm;
  fetchStores(searchTerm); // 當搜尋詞改變時，重新從後端獲取資料
};




// 餐廳數據 (從後端取得)
const allStores = ref([]); // 存放從後端取得的原始 Store 數據

// 異步函數：從後端獲取 Store 數據
// <-- **重要修改：添加 searchTerm 參數**
const fetchStores = async (searchTerm = '') => { 
  try {
      // 根據 searchTerm 是否存在來構建 URL
      const url = searchTerm
        ? `${API_URL}/api/stores?search=${encodeURIComponent(searchTerm)}`
        : `${API_URL}/api/stores`;

      console.log("Fetching stores from URL:", url); // 添加日誌確認 URL

      const response = await axios.get(url); // <-- **使用構建好的 URL**
      allStores.value = response.data;
      console.log("從後端取得的商店資料:", allStores.value);
      } catch (error) {
      console.error('獲取商店資料失敗:', error);
    }
};


// 新增 computed 屬性來收集所有餐廳的唯一類別名稱
const uniqueCategoryNames = computed(() => {
  const categories = new Set();
  allStores.value.forEach(store => {
    if (store.categoryNames) {
      store.categoryNames.forEach(name => categories.add(name));
    }
  });
  return Array.from(categories).sort(); // 返回排序後的唯一類別名稱陣列
});

// 計算屬性：篩選後的餐廳列表
const filteredRestaurants = computed(() => {
  let filtered = [...allStores.value];

  // 第一步：根據篩選條件進行過濾
  if (filters.value.category.length > 0) {
    filtered = filtered.filter((store) =>
      store.categoryNames && store.categoryNames.some(catName => filters.value.category.includes(catName))
    );
  }

  filtered = filtered.filter((store) => (store.score || 0) >= filters.value.minscore);

  if (filters.value.isOpen) {
    filtered = filtered.filter((store) => store.isOpen === true);
  }

  // 第二步：應用排序
  if (sortOption.value === '評分最高') {
    filtered = filtered.sort((a, b) => (b.score || 0) - (a.score || 0));
  } else if (sortOption.value === '距離最近' || sortOption.value === '最快送達') {
    // 假設 deliveryTime 已經在後端計算好並返回
    filtered = filtered.sort((a, b) => a.deliveryTime - b.deliveryTime);
  }

  // 第三步：將處理後的 StoreDTO 轉換為 RestaurantListSection 需要的格式
  // 注意：模糊搜尋應該已經在 `fetchStores` (後端) 完成，這裡不再做二次搜尋過濾
  return filtered.map(store => ({
    id: store.id,
    name: store.name,
    categoryNames: store.categoryNames || [],
    deliveryTime: store.deliveryTime || 20, // 使用後端提供的 deliveryTime
    score: store.score || 0,
    comments: store.comments || [],
    // 聚合食物標籤 (保持不變)
    tags: store.foods ? [...new Set(store.foods.flatMap(food => food.tagNames || []))] : [],
    image: store.photo,
    promo: '', // 如果後端有提供促銷資訊，可以在這裡映射
    popularityScore: store.popularityScore != null ? store.popularityScore : (store.score != null ? parseFloat(store.score) * 10 : 0),
    isOpen: store.isOpen
  }));
});



// 篩選條件
const filters = ref({
  category: [],
  minscore: 0,
  isOpen: false,
});

// 排序選項
const sortOption = ref('評分最高');


// 更新配送時間
const updatescore = () => {
  // 觸發篩選更新
};

// 排序餐廳（實際上由 computed 處理，這裡僅為兼容）
const sortRestaurants = () => {
  // 由 filteredRestaurants computed 屬性處理
};
</script>

<style scoped>
/* 這裡保留 Home.vue 原有的全局或佈局相關 CSS */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
  font-family: Arial, sans-serif;
}

body {
  background-color: #f7f7f7;
}

/* 篩選與排序（頂部） */
.filters {
  padding: 15px 20px;
  background-color: #fff;
  margin: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 10px;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.filters .sort select {
  padding: 8px;
  border: 1px solid #ccc;
  border-radius: 4px;
  font-size: 14px;
}

/* 內容容器 */
.content-container {
  display: flex;
  padding: 20px;
  gap: 20px;
}

/* 左側篩選欄的 CSS 保持不變，因為它仍然在 Home.vue 中 */
.sidebar {
  width: 250px; /* 固定寬度，可根據需求調整 */
  flex-shrink: 0; /* 防止縮小 */
  background-color: #fff;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}


/* 頁腳 */
.footer {
  background-color: #333;
  color: white;
  padding: 20px;
  text-align: center;
  margin-top: 30px;
}

.footer a {
  color: #ffba20;
  text-decoration: none;
  margin: 0 10px;
}

/* 響應式設計 */
@media (max-width: 768px) {
  .content-container {
    flex-direction: column;
  }

  .sidebar {
    width: 100%;
    display: none;
  }

  .sidebar.active {
    display: block;
  }

  .filter-toggle {
    display: block;
    padding: 10px;
    background-color: #ffba20;
    color: white;
    text-align: center;
    cursor: pointer;
    margin: 10px 20px;
    border-radius: 4px;
  }

  .filters {
    margin: 10px 10px 20px;
  }
}

@media (min-width: 769px) {
  .filter-toggle {
    display: none;
  }
}
</style>