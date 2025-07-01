<template>
    <section class="popout" v-if="showPopout">
      <div class="popout-content">
        <button class="close-btn" @click="showPopout = false">✕</button>
        <input type="text" placeholder="輸入您的地址" @focus="address = ''" v-model="address" />
        <button class="search-btn" @click="searchAddress">搜尋</button>
      </div>
    </section>
  
    <!-- 附近熱門美食 -->
    <section class="popular-section" v-if="address != ''">
      <h2>附近熱門美食</h2>
      <div class="restaurant-scroll">
        <div class="restaurant-card" v-for="restaurant in popularRestaurants" :key="restaurant.id">
          <img :src="restaurant.image" :alt="restaurant.name" />
          <div class="info">
            <h3>{{ restaurant.name }}</h3>
            <p>{{ restaurant.category }} • {{ restaurant.deliveryTime }} 分鐘</p>
            <p>{{ restaurant.score }} <Comment v-if="restaurant.comment.length > 0" :comments="restaurant.comment" /></p>
            <div class="tags">
              <span v-for="tag in restaurant.tags" :key="tag">{{ tag }}</span>
            </div>
          </div>
        </div>
      </div>
    </section>
  
    <!-- 搜尋與位置區域 -->
    <section class="hero-section">
      <div class="search-container">
        <input type="text" placeholder="金碗 您 想來點甚麼呢?" v-model="searched" @focus="showDropdown = true"
          @blur="hideDropdownWithDelay" @input="filterSuggestions" @keydown.enter="handleSearch" />
        <button @click="handleSearch">搜尋</button>
        <div class="search-dropdown" v-show="showDropdown">
          <!-- 搜索歷史 -->
          <div class="search-section" v-if="searchHistory.length > 0">
            <h4>最近搜尋</h4>
            <ul>
              <li v-for="(item, index) in filteredHistory" :key="item" @click="selectSuggestion(item)"
                class="search-item">
                {{ item }}
                <button class="clear-history" @click.stop.prevent="removeHistoryItem(item)">✕</button>
              </li>
            </ul>
          </div>
          <!-- 熱門搜索 -->
          <div class="search-section">
            <h4>熱門搜尋</h4>
            <ul>
              <li v-for="(item, index) in filteredHotSearches" :key="index" @click="selectSuggestion(item)"
                class="search-item">
                {{ item }}
              </li>
            </ul>
          </div>
        </div>
      </div>
    </section>
  
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
          <img :src="restaurant.image" :alt="restaurant.name" @click="$router.push(`/`)" style="cursor: pointer;" />
          <div class="info">
            <h3>{{ restaurant.name }}</h3>
            <p>{{ restaurant.category }} • {{ restaurant.deliveryTime }} 分鐘 • {{ restaurant.promo || '' }}</p>
            <p>{{ restaurant.score }}
              <Comment v-if="restaurant.comment.length > 0" :comments="restaurant.comment" />
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
  import { ref, computed, onMounted } from 'vue';
  import TopFilterButtons from '@/components/Jimmy/TopFilterButtons.vue';
  import SidebarFilters from '@/components/Jimmy/SidebarFilters.vue';
  import Comment from '@/components/Jimmy/Comment.vue';

  const isSidebarActive = ref(false);
  const toggleSidebar = () => {
    isSidebarActive.value = !isSidebarActive.value;
  };
  
  // 搜索相關
  const searched = ref('');
  const searchHistory = ref(JSON.parse(localStorage.getItem('searchHistory')) || []);
  const hotSearches = ref(['滷肉飯', '壽司', '披薩', '炸雞', '義大利麵']);
  const showDropdown = ref(false);
  const filteredHistory = ref([]);
  const filteredHotSearches = ref([...hotSearches.value]);
  
  const saveSearchHistory = () => {
    localStorage.setItem('searchHistory', JSON.stringify(searchHistory.value));
  };
  
  const handleSearch = () => {
    if (searched.value.trim()) {
      if (!searchHistory.value.includes(searched.value)) {
        searchHistory.value.unshift(searched.value);
        if (searchHistory.value.length > 5) {
          searchHistory.value.pop();
        }
        saveSearchHistory();
      }
      console.log('搜尋內容:', searched.value);
      showDropdown.value = false;
    }
  };
  
  const selectSuggestion = (item) => {
    searched.value = item;
    handleSearch();
  };
  
  const removeHistoryItem = (item) => {
    const index = searchHistory.value.indexOf(item);
    if (index !== -1) {
      searchHistory.value.splice(index, 1);
      saveSearchHistory();
      filterSuggestions();
    }
  };
  
  const filterSuggestions = () => {
    const query = searched.value.toLowerCase().trim();
    filteredHistory.value = query
      ? searchHistory.value.filter((item) => item.toLowerCase().includes(query))
      : [...searchHistory.value];
    filteredHotSearches.value = query
      ? hotSearches.value.filter((item) => item.toLowerCase().includes(query))
      : [...hotSearches.value];
  };
  
  const hideDropdownWithDelay = () => {
    setTimeout(() => {
      showDropdown.value = false;
    }, 200);
  };
  
  onMounted(() => {
    filterSuggestions();
  });
  
  // 餐廳數據
  const restaurants = ref([
    {
      id: 1,
      name: '美味餐廳',
      category: '中式',
      deliveryTime: 25,
      score: 4,
      comment: 120,
      tags: ['滷肉飯', '便當'],
      image: '/image/giachi.jpg',
      promo: '免運費',
      popularityScore: 70,
      comment: [
        {
          id: 1,
          order_id: 1,
          user_id: "Jimmy1",
          content: "comment 01 der la",
          score: 4,
          create_time: "2023-10-01 10:00:00",
          reply: "reply 01 der la",
          reply_update_time: "2023-10-02 10:00:00",
          is_hidden: false,
          comment_img: [
            { img: "/image/kiva.jpg" },
            { img: "/image/kaimu.jpg" },
            { img: "/image/kuga.jpg" }
          ]
        },
        {
          id: 2,
          order_id: 3,
          user_id: "Jimmy3",
          content: "comment 02 der la",
          score: 5,
          create_time: "2023-10-02 10:00:00",
          is_hidden: false,
          comment_img: [
            { img: "/image/zero1.jpg" },
            { img: "/image/build.jpg" }
          ]
        },
        {
          id: 3,
          order_id: 2,
          user_id: "Tom",
          content: "comment 03 der la",
          score: 3,
            create_time: "2023-10-02 10:00:00",
            is_hidden: false,
            comment_img: [
              { img: "/image/pizza.jpg" }
            ]
          }
        ]
      },
      {
        id: 2,
        name: '壽司之家',
        category: '日式',
        deliveryTime: 10,
        score: 5,
        comment: 200,
        tags: ['壽司', '生魚片'],
        image: '/image/sooshi.jpg',
        promo: '',
        popularityScore: 80,
        comment: [
          {
            id: 4,
            order_id: 1,
            user_id: "Jimmy1",
            content: "comment 04 der la",
            score: 1,
            create_time: "2023-10-01 10:00:00",
            reply: "reply 02 der la",
            reply_update_time: "2023-10-02 10:00:00",
            is_hidden: false,
            comment_img: [
              { img: "/image/kaimu.jpg" },
              { img: "/image/kuga.jpg" }
            ]
          },
          {
            id: 5,
            order_id: 4,
            user_id: "Bob",
            content: "comment 05 der la",
            score: 5,
            create_time: "2023-10-02 10:00:00",
            is_hidden: false,
            comment_img: [
              { img: "/image/build.jpg" }
            ]
          }
        ]
      },
      {
        id: 3,
        name: '披薩樂園',
        category: '西式',
        deliveryTime: 30,
        score: 4.5,
        comment: 150,
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
        comment: 80,
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
        comment: 120,
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
        comment: 200,
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
        comment: 150,
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
        comment: 80,
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
      promo: []
    });
  
    // 排序選項
    const sortOption = ref('評分最高');
  
    // 計算屬性：熱門餐廳
    const popularRestaurants = computed(() => {
      return [...restaurants.value]
        .sort((a, b) => b.popularityScore - a.popularityScore)
        .slice(0, 10);
    });
  
    // 計算屬性：篩選後的餐廳列表
    const filteredRestaurants = computed(() => {
      let filtered = [...restaurants.value];
  
      if (filters.value.category.length > 0) {
        filtered = filtered.filter(restaurant => filters.value.category.includes(restaurant.category));
      }
  
      filtered = filtered.filter(restaurant => restaurant.score >= filters.value.minscore);
  
      if (filters.value.promo.length > 0) {
        filtered = filtered.filter(restaurant =>
          filters.value.promo.some(promo => restaurant.promo.includes(promo))
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
  
    onMounted(() => {
      filterSuggestions();
    });
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
  
  /* 搜尋與位置區域 */
  .hero-section {
    background-color: #fff;
    padding: 20px;
    text-align: center;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
    margin: 20px;
    border-radius: 8px;
  }
  
  .hero-section h1 {
    font-size: 30px;
    margin-bottom: 10px;
  }
  
  .hero-section p {
    color: #666;
    margin-bottom: 15px;
  }
  
  .hero-section input {
    width: 60%;
    max-width: 400px;
    padding: 10px;
    font-size: 16px;
    border: 1px solid #ccc;
    border-radius: 4px;
  }
  
  .hero-section button {
    padding: 10px 20px;
    background-color: #ffba20;
    color: white;
    border: none;
    border-radius: 4px;
    cursor: pointer;
    margin-left: 10px;
  }
  
  /* 附近熱門美食 */
  .popular-section {
    padding: 10px;
    background-color: #fff;
    margin: 20px;
    border-radius: 8px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  }
  
  .popular-section h2 {
    font-size: 12px;
    margin-bottom: 7.5px;
    color: #333;
  }
  
  .restaurant-scroll {
    display: flex;
    overflow-x: auto;
    gap: 10px;
    padding-bottom: 5px;
    scroll-behavior: smooth;
  }
  
  .restaurant-scroll::-webkit-scrollbar {
    height: 4px;
  }
  
  .restaurant-scroll::-webkit-scrollbar-thumb {
    background-color: #ffba20;
    border-radius: 4px;
  }
  
  .restaurant-scroll .restaurant-card {
    flex: 0 0 140px;
    width: 140px;
  }
  
  .restaurant-card img {
    width: 100%;
    height: 80px;
    object-fit: cover;
  }
  
  .restaurant-card .info {
    padding: 7.5px;
  }
  
  .restaurant-card h3 {
    font-size: 9px;
    margin-bottom: 4px;
  }
  
  .restaurant-card p {
    color: #666;
    font-size: 7px;
    margin-bottom: 2.5px;
  }
  
  .restaurant-card .tags {
    display: flex;
    gap: 5px;
    margin-top: 5px;
  }
  
  .restaurant-card .tags span {
    background-color: #f0f0f0;
    padding: 2.5px 5px;
    border-radius: 6px;
    font-size: 6px;
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
    box-shadow: 0 2px 4 The parameter px rgba(0, 0, 0, 0.1);
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
  
  .search-container {
    position: relative;
    display: flex;
    align-items: center;
    justify-content: center;
    width: 100%;
    max-width: 600px;
    margin: 0 auto;
  }
  
  .search-container input {
    width: 100%;
    padding: 12px;
    font-size: 16px;
    border: 1px solid #ccc;
    border-radius: 4px;
    outline: none;
  }
  
  .search-container button {
    padding: 12px 20px;
    margin-left: 10px;
  }
  
  .search-dropdown {
    position: absolute;
    top: 100%;
    left: 0;
    right: 0;
    background-color: white;
    border: 1px solid #ddd;
    border-radius: 4px;
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
    z-index: 1000;
    max-height: 300px;
    overflow-y: auto;
    margin-top: 5px;
  }
  
  .search-section {
    padding: 10px;
  }
  
  .search-section h4 {
    font-size: 14px;
    color: #333;
    margin-bottom: 8px;
    padding: 0 10px;
  }
  
  .search-section ul {
    list-style: none;
    padding: 0;
    margin: 0;
  }
  
  .search-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 10px;
    font-size: 14px;
    color: #333;
    cursor: pointer;
    transition: background-color 0.2s;
  }
  
  .search-item:hover {
    background-color: #f5f5f5;
  }
  
  .clear-history {
    background: none;
    border: none;
    color: #999;
    cursor: pointer;
    font-size: 14px;
    padding: 5px;
    line-height: 1;
  }
  
  .clear-history:hover {
    color: #ffba20;
  }
  
  /* 彈出視窗樣式 */
  .popout {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background-color: rgba(0, 0, 0, 0.5);
    display: flex;
    justify-content: center;
    align-items: center;
    z-index: 2000;
  }
  
  .popout-content {
    background-color: #fff;
    padding: 20px;
    border-radius: 8px;
    width: 90%;
    max-width: 400px;
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.2);
    position: relative;
    text-align: center;
  }
  
  .popout-content h3 {
    font-size: 18px;
    margin-bottom: 15px;
    color: #333;
  }
  
  .popout-content input {
    width: 100%;
    padding: 10px;
    font-size: 16px;
    border: 1px solid #ccc;
    border-radius: 4px;
    margin-bottom: 15px;
  }
  
  .search-btn {
    padding: 10px 20px;
    background-color: #ffba20;
    color: white;
    border: none;
    border-radius: 4px;
    cursor: pointer;
    font-size: 16px;
    width: 100%;
  }
  
  .search-btn:hover {
    background-color: #b00c50;
  }
  
  .close-btn {
    position: absolute;
    top: 10px;
    right: 10px;
    background: none;
    border: none;
    font-size: 18px;
    color: #999;
    cursor: pointer;
  }
  
  .close-btn:hover {
    color: #ffba20;
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
  
    .popular-section,
    .filters,
    .hero-section {
      margin: 10px 10px 20px;
    }
  }
  
  @media (min-width: 769px) {
    .filter-toggle {
      display: none;
    }
  }
  
  @media (max-width: 768px) {
    .sidebar {
      display: none;
    }
  
    .sidebar.active {
      display: block;
    }
  }
  </style>