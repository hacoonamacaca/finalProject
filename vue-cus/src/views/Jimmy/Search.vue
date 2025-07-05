<!-- src/views/Jimmy/Home.vue -->
<template>
  <!-- 附近熱門美食 -->
  <PopularRestaurants :address="address" :restaurants="restaurants" />

  <!-- 搜尋與位置區域 -->
  <SearchSection v-model:search="searched" @search="handleSearch" />

  <!-- 篩選與排序（頂部） -->
  <div class="filter-toggle" @click="toggleSidebar">篩選條件</div>
  <section class="filters">
    <TopFilterButtons :filters="filters" @update:filters="filters = $event" />
    <div class="sort">
      <select v-model="sortOption" @change="sortRestaurants">
        <option value="評分最高">評分最高</option>
        <option value="距離最近">距離最近</option>
        <option value="最快送達">最快送達</option>
      </select>
    </div>
  </section>

  <!-- 內容容器 -->
  <div class="content-container">
    <!-- 左側篩選欄 -->
    <aside class="sidebar" :class="{ active: isSidebarActive }">
      <SidebarFilters :filters="filters" @update:filters="filters = $event" @update-score="updatescore" />
    </aside>

    <!-- 餐廳列表 -->
    <section class="restaurant-list">
      <div class="restaurant-card" v-for="restaurant in filteredRestaurants" :key="restaurant.id">
        <img
          :src="restaurant.image"
          :alt="restaurant.name"
          @click="$router.push(`/restaurant/${restaurant.id}`)"
          style="cursor: pointer;"
        />
        <div class="info">
          <h3>{{ restaurant.name }}</h3>
          <p>{{ restaurant.category }} • {{ restaurant.deliveryTime }} 分鐘 • {{ restaurant.promo || '' }}</p>
          <p>
            {{ restaurant.score }}★
            <Comment :comments="restaurant.comments" :comment-count="restaurant.comments.length" />
          </p>
          <div class="tags">
            <span v-for="tag in restaurant.tags" :key="tag">{{ tag }}</span>
          </div>
        </div>
      </div>
    </section>
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
import Comment from '@/components/Jimmy/Comment.vue';
import PopularRestaurants from '@/components/Jimmy/PopularRestaurants.vue';
import SearchSection from '@/components/Jimmy/SearchSection.vue';

const route = useRoute();
const address = ref('');

// 從路由查詢參數獲取地址並設定給本地的 address
onMounted(() => {
  if (route.query.address) {
    address.value = route.query.address;
  }
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
const searched = ref('');
const handleSearch = (searchTerm) => {
  console.log('搜尋內容:', searchTerm);
};

// 餐廳數據
const restaurants = ref([
  {
    id: 1,
    name: '美味餐廳',
    category: '中式',
    deliveryTime: 25,
    score: 4,
    comments: [
      {
        id: 1,
        order_id: 1,
        user_id: 'Jimmy1',
        content: 'comment 01 der la',
        score: 4,
        create_time: '2023-10-01 10:00:00',
        reply: 'reply 01 der la',
        reply_update_time: '2023-10-02 10:00:00',
        is_hidden: false,
        comment_img: [
          { img: '/image/kiva.jpg' },
          { img: '/image/kaimu.jpg' },
          { img: '/image/kuga.jpg' },
        ],
      },
      {
        id: 2,
        order_id: 3,
        user_id: 'Jimmy3',
        content: 'comment 02 der la',
        score: 5,
        create_time: '2023-10-02 10:00:00',
        is_hidden: false,
        comment_img: [
          { img: '/image/zero1.jpg' },
          { img: '/image/build.jpg' },
        ],
      },
      {
        id: 3,
        order_id: 2,
        user_id: 'Tom',
        content: 'comment 03 der la',
        score: 3,
        create_time: '2023-10-02 10:00:00',
        is_hidden: false,
        comment_img: [{ img: '/image/pizza.jpg' }],
      },
    ],
    tags: ['滷肉飯', '便當'],
    image: '/image/giachi.jpg',
    promo: '免運費',
    popularityScore: 70,
  },
  {
    id: 2,
    name: '壽司之家',
    category: '日式',
    deliveryTime: 10,
    score: 5,
    comments: [
      {
        id: 4,
        order_id: 1,
        user_id: 'Jimmy1',
        content: 'comment 04 der la',
        score: 1,
        create_time: '2023-10-01 10:00:00',
        reply: 'reply 02 der la',
        reply_update_time: '2023-10-02 10:00:00',
        is_hidden: false,
        comment_img: [
          { img: '/image/kaimu.jpg' },
          { img: '/image/kuga.jpg' },
        ],
      },
      {
        id: 5,
        order_id: 4,
        user_id: 'Bob',
        content: 'comment 05 der la',
        score: 5,
        create_time: '2023-10-02 10:00:00',
        is_hidden: false,
        comment_img: [{ img: '/image/build.jpg' }],
      },
    ],
    tags: ['壽司', '生魚片'],
    image: '/image/sooshi.jpg',
    promo: '',
    popularityScore: 80,
  },
  {
    id: 3,
    name: '披薩樂園',
    category: '西式',
    deliveryTime: 30,
    score: 4.5,
    comments: [],
    tags: ['披薩', '義大利麵'],
    image: '/image/pizza.jpg',
    promo: '滿 $200 免運',
    popularityScore: 85,
  },
  {
    id: 4,
    name: '韓式炸雞',
    category: '韓式',
    deliveryTime: 8,
    score: 2,
    comments: [],
    tags: ['炸雞', '泡菜'],
    image: '/image/fryC.jpg',
    promo: '折扣',
    popularityScore: 65,
  },
  {
    id: 5,
    name: 'haha餐廳',
    category: '中式',
    deliveryTime: 25,
    score: 3,
    comments: [],
    tags: ['滷肉飯', '便當'],
    image: '/image/giachi2.jpg',
    promo: '免運費',
    popularityScore: 67,
  },
  {
    id: 6,
    name: 'lala之家',
    category: '日式',
    deliveryTime: 10,
    score: 3.5,
    comments: [],
    tags: ['壽司', '生魚片'],
    image: '/image/sooshi2.jpg',
    promo: '',
    popularityScore: 75,
  },
  {
    id: 7,
    name: 'wola樂園',
    category: '西式',
    deliveryTime: 30,
    score: 5,
    comments: [],
    tags: ['披薩', '義大利麵'],
    image: '/image/pizza2.jpg',
    promo: '滿 $200 免運',
    popularityScore: 90,
  },
  {
    id: 8,
    name: 'GG炸雞',
    category: '韓式',
    deliveryTime: 8,
    score: 4.5,
    comments: [],
    tags: ['炸雞', '泡菜'],
    image: '/image/fryC2.jpg',
    promo: '折扣',
    popularityScore: 70,
  },
]);

// 篩選條件
const filters = ref({
  category: [],
  minscore: 0,
  promo: [],
});

// 排序選項
const sortOption = ref('評分最高');

// 計算屬性：篩選後的餐廳列表
const filteredRestaurants = computed(() => {
  let filtered = [...restaurants.value];

  if (filters.value.category.length > 0) {
    filtered = filtered.filter((restaurant) => filters.value.category.includes(restaurant.category));
  }

  filtered = filtered.filter((restaurant) => restaurant.score >= filters.value.minscore);

  if (filters.value.promo.length > 0) {
    filtered = filtered.filter((restaurant) =>
      filters.value.promo.some((promo) => restaurant.promo.includes(promo))
    );
  }

  if (sortOption.value === '評分最高') {
    filtered = filtered.sort((a, b) => b.score - a.score);
  } else if (sortOption.value === '距離最近' || sortOption.value === '最快送達') {
    filtered = filtered.sort((a, b) => a.deliveryTime - b.deliveryTime);
  }

  return filtered;
});

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

/* 餐廳列表 */
.restaurant-list {
  flex: 1;
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
}

.restaurant-card {
  background-color: #fff;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: transform 0.2s;
}

.restaurant-card img {
  width: 100%;
  height: 160px;
  object-fit: cover;
}

.restaurant-card .info {
  padding: 15px;
}

.restaurant-card h3 {
  font-size: 18px;
  margin-bottom: 8px;
}

.restaurant-card p {
  color: #666;
  font-size: 14px;
  margin-bottom: 5px;
}

.restaurant-card .tags {
  display: flex;
  gap: 10px;
  margin-top: 10px;
}

.restaurant-card .tags span {
  background-color: #f0f0f0;
  padding: 5px 10px;
  border-radius: 12px;
  font-size: 12px;
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