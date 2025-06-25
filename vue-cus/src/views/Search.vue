<template>
    <!-- 導航欄 -->
    <header class="navbar">
        <div class="logo">金碗GoldenBowl Foolog</div>
        <div>
            <button class="location-btn" @click="showPopout = true">
                目前位置為： {{ address }}<a @click.stop="getCurrentLocationAndNavigate"><button style="background: transparent; border: none; color: white;">📍</button></a>
            </button>
        </div>
        <button class="hamburger" @click="toggleMenu">
            <span></span>
            <span></span>
            <span></span>
        </button>
        <div class="nav-links" :class="{ active: isMenuOpen }">
            <!-- auth-section 在行動版置頂 -->
            <div class="auth-section">
                <a href="#" @click="getLogin" v-if="!isLoggedIn">登入</a>
                <UserDropdown v-if="isLoggedIn" />
            </div>
            <!-- 其他導航項 -->
            <div class="nav-items">
                <a href="#" @click.prevent="toggleRestaurantMenu">{{ isRestaurant ? '餐廳' : '餐點' }}</a>
                <a href="#">優惠通知</a>
                <a href="#">購物車</a>
            </div>
        </div>
    </header>
    <section class="popout" v-if="showPopout">
        <div class="popout-content">
            <button class="close-btn" @click="showPopout = false">✕</button>         
            <input type="text" placeholder="輸入您的地址" @focus="address = ''" v-model="address" />
            <button class="search-btn" @click="searchAddress">搜尋</button>
        </div>
    </section>

    <!-- 附近熱門美食 -->
    <section class="popular-section" v-if="address !=''">
        <h2>附近熱門美食</h2>
        <div class="restaurant-scroll">
            <div class="restaurant-card" v-for="restaurant in popularRestaurants" :key="restaurant.id">
                <img :src="restaurant.image" :alt="restaurant.name" />
                <div class="info">
                    <h3>{{ restaurant.name }}</h3>
                    <p>{{ restaurant.cuisine }} • {{ restaurant.deliveryTime }} 分鐘</p>
                    <p>{{ restaurant.rating }} ({{ restaurant.reviews }}+ 評價)</p>
                    <div class="tags">
                        <span v-for="tag in restaurant.tags" :key="tag">{{ tag }}</span>
                    </div>
                </div>
            </div>
        </div>
    </section>

    <!-- 搜尋與位置區域 -->
    <section class="hero-section">
        <!-- <h1>探索附近美食</h1> -->
        <div class="search-container">
            <input type="text" placeholder="金碗 您 想來點甚麼呢?" v-model="searched" @focus="showDropdown = true"
                @blur="hideDropdownWithDelay" @input="filterSuggestions" @keydown.enter="handleSearch" />
            <button @click="handleSearch">搜尋</button>
            <div class="search-dropdown" v-show="showDropdown">
                <!-- 搜索歷史 -->
                <div class="search-section" v-if="searchHistory.length > 0">
                    <h4>最近搜尋</h4>
                    <ul>
                        <li v-for="(item, index) in filteredHistory" :key="item" @click="selectSuggestion(item)" class="search-item">
                            {{ item }}
                            <button class="clear-history" @click.stop.prevent="removeHistoryItem(item)">✕</button>
                        </li>
                    </ul>
                </div>
                <!-- 熱門搜索 -->
                <div class="search-section">
                    <h4>熱門搜尋</h4>
                    <ul>
                        <li v-for="(item, index) in filteredHotSearches" :key="index" @click="selectSuggestion(item)" class="search-item">
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
            <SidebarFilters :filters="filters" @update:filters="filters = $event" @update-rating="updateRating" />
        </aside>
        

        <!-- 餐廳列表 -->
        <section class="restaurant-list">
            <div class="restaurant-card" v-for="restaurant in filteredRestaurants" :key="restaurant.id">
                <img :src="restaurant.image" :alt="restaurant.name" />
                <div class="info">
                    <h3>{{ restaurant.name }}</h3>
                    <p>{{ restaurant.cuisine }} • {{ restaurant.deliveryTime }} 分鐘 • {{ restaurant.promo || '' }}</p>
                    <p>{{ restaurant.rating }} ({{ restaurant.reviews }}+ 評價)</p>
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
const isLoggedIn = ref(true)


import { ref, computed, onMounted, watch } from 'vue';
import { useRoute } from 'vue-router';
import TopFilterButtons from '@/components/TopFilterButtons.vue';
import SidebarFilters from '@/components/SidebarFilters.vue';
import UserDropdown from '@/components/UserDropdown.vue';


const isSidebarActive = ref(false);
const toggleSidebar = () => {
  isSidebarActive.value = !isSidebarActive.value;
};


// 控制漢堡選單的顯示
const isMenuOpen = ref(false);
const toggleMenu = () => {
  isMenuOpen.value = !isMenuOpen.value;
};

// 控制彈出視窗的顯示
const showPopout = ref(false);

// 搜尋地址（假設已存在，僅確保關閉彈窗）
const searchAddress = () => {
  getCoordinates(); // 調用已有的地址查詢函數
  showPopout.value = false; // 搜尋後關閉彈窗
};


// 搜索相關
const searched = ref('');
const searchHistory = ref(JSON.parse(localStorage.getItem('searchHistory')) || []);
const hotSearches = ref(['滷肉飯', '壽司', '披薩', '炸雞', '義大利麵']);
const showDropdown = ref(false);
const filteredHistory = ref([]);
const filteredHotSearches = ref([...hotSearches.value]);

// 保存搜索歷史到 localStorage
const saveSearchHistory = () => {
    localStorage.setItem('searchHistory', JSON.stringify(searchHistory.value));
};

// 點擊搜尋或按 Enter 鍵處理搜索
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

// 選擇建議項
const selectSuggestion = (item) => {
    searched.value = item;
    handleSearch();
};

// 刪除單個搜索歷史
const removeHistoryItem = (item) => {
    const index = searchHistory.value.indexOf(item);
    if (index !== -1) {
        searchHistory.value.splice(index, 1);
        saveSearchHistory();
        filterSuggestions();
    }
};

// 過濾建議（歷史和熱門搜索）
const filterSuggestions = () => {
    const query = searched.value.toLowerCase().trim();
    filteredHistory.value = query
        ? searchHistory.value.filter((item) => item.toLowerCase().includes(query))
        : [...searchHistory.value];
    filteredHotSearches.value = query
        ? hotSearches.value.filter((item) => item.toLowerCase().includes(query))
        : [...hotSearches.value];
};

// 延遲隱藏下拉清單
const hideDropdownWithDelay = () => {
    setTimeout(() => {
        showDropdown.value = false;
    }, 200);
};

// 初始化時加載搜索歷史
onMounted(() => {
    filterSuggestions();
});


// 地址查詢用
const route = useRoute();
const address = ref(route.query.address || ''); // 從查詢參數初始化
const coordinates = ref(null); // 儲存查詢到的座標
const loading = ref(false); // 控制載入狀態
const error = ref(''); // 儲存錯誤訊息

// 格式化地址的函數
const formatAddress = (input) => {
    if (!input.trim()) return input;

    // 完整地址的正則表達式
    const fullAddressRegex = /^(\S+?)([縣市])(.+?)(區|鄉|鎮|市)(.*?)(\d+號)/;
    // 簡化地址的正則表達式（僅路名和號）
    const simpleAddressRegex = /^(.+?)(\d+號)$/;

    let match = input.match(fullAddressRegex);
    if (match) {
        // 提取完整地址的各部分
        const country = match[1]; // 國名 (如台灣)
        const city = match[2]; // 縣市 (如台北市)
        const district = match[3] + match[4]; // 行政區 (如信義區)
        const road = match[5].trim(); // 路段巷弄 (如松山路465巷27弄)
        const number = match[6]; // 號 (如16號)
        return `${country} ${city} ${district} ${road} ${number}`;
    }

    match = input.match(simpleAddressRegex);
    if (match) {
        const road = match[1].trim(); // 路名 (如松江路)
        const number = match[2]; // 號 (如146號)
        return `${road} ${number}`;
    }

    return input;
};

// 查詢 Nominatim API 的函數
const getCoordinates = async () => {
    if (!address.value.trim()) {
        error.value = '請輸入地址';
        coordinates.value = null;
        return;
    }

    address.value = formatAddress(address.value);

    loading.value = true;
    error.value = '';
    coordinates.value = null;

    try {
        const url = `https://nominatim.openstreetmap.org/search?format=json&q=${encodeURIComponent(address.value)}`;
        const response = await fetch(url, {
            headers: {
                'User-Agent': 'Jimmy/tokin81@yahoo.com.tw'
            }
        });
        const data = await response.json();

        if (data.length > 0) {
            coordinates.value = {
                lat: data[0].lat,
                lon: data[0].lon
            };
        } else {
            error.value = '無法找到該地址的座標';
        }
    } catch (err) {
        error.value = '查詢失敗，請稍後再試';
        console.error('API 錯誤:', err);
    } finally {
        loading.value = false;
    }
};

// 新增當前位置導航函數
const getCurrentLocationAndNavigate = async () => {
    const success = await getCurrentLocation();
    if (success) {
        router.push({
        path: '/search',
        query: { address: address.value }
        });
    }
};


// 格式化 Nominatim API 回傳的地址為台灣常見格式
const formatTaiwanAddress = (addressData) => {
    if (!addressData) return '';

    // 提取地址各部分
    const country = addressData.country || '臺灣';
    const postcode = addressData.postcode || '';
    const city = addressData.city || addressData.county || '';
    const district = addressData.suburb || addressData.town || addressData.city_district || '';
    const village = addressData.neighbourhood || addressData.village || ''; // 里
    const road = addressData.road || '';
    let houseNumber = addressData.house_number || '';

    // 處理 "X之Y號" 格式，轉為 "Y號"
    if (houseNumber.includes('之')) {
        houseNumber = houseNumber.split('之')[1] || houseNumber;
    }

    // 組合地址，忽略空值
    const parts = [
        country,       
        city,
        district,
        village,
        road,
        houseNumber
    ].filter(part => part); // 過濾空值

    return parts.join('');
};


// 獲取當前位置
const getCurrentLocation = async () => {
  if (!navigator.geolocation) {
    alert('您的瀏覽器不支援定位功能');
    return false;
  }

  try {
    const position = await new Promise((resolve, reject) => {
      navigator.geolocation.getCurrentPosition(resolve, reject);
    });

    const { latitude, longitude } = position.coords;

    const response = await fetch(
      `https://nominatim.openstreetmap.org/reverse?lat=${latitude}&lon=${longitude}&format=json&addressdetails=1`
    );
    const data = await response.json();

    if (data && data.display_name) {
      address.value = formatTaiwanAddress(data.address);      
      return true; // 表示成功
    } else {
      alert('無法解析地址，請稍後再試');
      return false;
    }
  } catch (error) {
    console.error('定位失敗:', error);
    alert('無法獲取位置，請檢查權限或稍後再試');
    return false;
  }
};

onMounted(() => {
  console.log('初始路由地址:', route.query.address); // 確認查詢參數
  address.value = route.query.address || '';
});

// 監聽路由變化，動態更新地址
watch(() => route.query.address, (newAddress) => {
  address.value = newAddress || '';
});


// 餐廳/餐點切換
const isRestaurant = ref(true);
const toggleRestaurantMenu = () => {
    isRestaurant.value = !isRestaurant.value;
    console.log("目前頁面餐廳為是/餐點為否:" + isRestaurant.value);
};

// 餐廳數據
const restaurants = ref([
    {
        id: 1,
        name: '美味餐廳',
        cuisine: '中式',
        deliveryTime: 25,
        rating: 4,
        reviews: 120,
        tags: ['滷肉飯', '便當'],
        image: '/image/giachi.jpg',
        promo: '免運費',
        popularityScore: 70,
    },
    {
        id: 2,
        name: '壽司之家',
        cuisine: '日式',
        deliveryTime: 10,
        rating: 5,
        reviews: 200,
        tags: ['壽司', '生魚片'],
        image: '/image/sooshi.jpg',
        promo: '',
        popularityScore: 80,
    },
    {
        id: 3,
        name: '披薩樂園',
        cuisine: '西式',
        deliveryTime: 30,
        rating: 4.5,
        reviews: 150,
        tags: ['披薩', '義大利麵'],
        image: '/image/pizza.jpg',
        promo: '滿 $200 免運',
        popularityScore: 85,
    },
    {
        id: 4,
        name: '韓式炸雞',
        cuisine: '韓式',
        deliveryTime: 8,
        rating: 2,
        reviews: 80,
        tags: ['炸雞', '泡菜'],
        image: '/image/fryC.jpg',
        promo: '折扣',
        popularityScore: 65,
    },
    {
        id: 5,
        name: 'haha餐廳',
        cuisine: '中式',
        deliveryTime: 25,
        rating: 3,
        reviews: 120,
        tags: ['滷肉飯', '便當'],
        image: '/image/giachi2.jpg',
        promo: '免運費',
        popularityScore: 67,
    },
    {
        id: 6,
        name: 'lala之家',
        cuisine: '日式',
        deliveryTime: 10,
        rating: 3.5,
        reviews: 200,
        tags: ['壽司', '生魚片'],
        image: '/image/sooshi2.jpg',
        promo: '',
        popularityScore: 75,
    },
    {
        id: 7,
        name: 'wola樂園',
        cuisine: '西式',
        deliveryTime: 30,
        rating: 5,
        reviews: 150,
        tags: ['披薩', '義大利麵'],
        image: '/image/pizza2.jpg',
        promo: '滿 $200 免運',
        popularityScore: 90,
    },
    {
        id: 8,
        name: 'GG炸雞',
        cuisine: '韓式',
        deliveryTime: 8,
        rating: 4.5,
        reviews: 80,
        tags: ['炸雞', '泡菜'],
        image: '/image/fryC2.jpg',
        promo: '折扣',
        popularityScore: 70,
    },
]);

// 篩選條件
const filters = ref({
    cuisine: [],
    minRating: 0,
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

  if (filters.value.cuisine.length > 0) {
    filtered = filtered.filter(restaurant => filters.value.cuisine.includes(restaurant.cuisine));
  }

  filtered = filtered.filter(restaurant => restaurant.rating >= filters.value.minRating);

  if (filters.value.promo.length > 0) {
    filtered = filtered.filter(restaurant =>
      filters.value.promo.some(promo => restaurant.promo.includes(promo))
    );
  }

  if (sortOption.value === '評分最高') {
    filtered = filtered.sort((a, b) => b.rating - a.rating);
  } else if (sortOption.value === '距離最近' || sortOption.value === '最快送達') {
    filtered = filtered.sort((a, b) => a.deliveryTime - b.deliveryTime);
  }

  return filtered;
});

// 更新配送時間
const updateRating = () => {
    // 觸發篩選更新
};

// 行動裝置篩選欄切換
onMounted(() => {
    filterSuggestions(); // 初始化建議清單
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

/* 導航欄 */
.navbar {
    background-color: #d70f64;
    color: white;
    padding: 15px 20px;
    display: flex;
    justify-content: space-between;
    align-items: center;
    position: relative;
}

.navbar .logo {
    font-size: 26px;
    font-weight: bold;
}

.navbar .nav-links {
    display: flex;
    align-items: center;
    gap: 20px;
}

.navbar .nav-links a {
    color: white;
    text-decoration: none;
    font-size: 16px;
}

.navbar .location {
    display: flex;
    align-items: center;
    gap: 10px;
}

.navbar input {
    padding: 8px;
    border: none;
    border-radius: 4px;
    width: 200px;
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
    background-color: #d70f64;
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
    background-color: #d70f64;
    border-radius: 4px;
}

.restaurant-scroll .restaurant-card {
    flex: 0 0 140px;
    min-width: 140px;
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

.restaurant-card:hover {
    transform: scale(1.02);
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
    color: #d70f64;
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
    color: #d70f64;
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
    background-color: #d70f64;
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
    color: #d70f64;
}


/* 導航欄按鈕美化 */
.location-btn {
    background-color: transparent;
    color: white;
    border: 1px solid #fff;
    padding: 8px 16px;
    border-radius: 4px;
    cursor: pointer;
    font-size: 14px;
    display: flex;
    align-items: center;
    gap: 5px;
}

.location-btn:hover {
    background-color: rgba(255, 255, 255, 0.1);
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
        background-color: #d70f64;
        color: white;
        text-align: center;
        cursor: pointer;
        margin: 10px 20px;
        border-radius: 4px;
    }

    .restaurant-scroll {
        flex-wrap: nowrap;
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

/* 漢堡選單按鈕 */
.hamburger {
    display: none;
    flex-direction: column;
    justify-content: space-around;
    width: 30px;
    height: 25px;
    background: transparent;
    border: none;
    cursor: pointer;
    padding: 0;
    z-index: 3000;
}

.hamburger span {
    width: 100%;
    height: 3px;
    background-color: white;
    transition: all 0.3s ease;
}

/* 導航連結 */
.nav-links {
    display: flex;
    align-items: center;
    gap: 20px;
}

.nav-links .nav-items {
    display: flex;
    align-items: center;
    gap: 20px;
}

.nav-links .auth-section {
    display: flex;
    align-items: center;
    gap: 20px;
}

.nav-links a {
    color: white;
    text-decoration: none;
    font-size: 16px;
}

/* 響應式設計 */
@media (max-width: 768px) {
    .hamburger {
        display: flex;
    }

    .nav-links {
        position: fixed;
        top: 0;
        right: 0;
        height: 100%;
        width: 250px;
        background-color: #d70f64;
        flex-direction: column;
        align-items: flex-start;
        padding: 60px 20px;
        transform: translateX(100%);
        transition: transform 0.3s ease-in-out;
        z-index: 2000;
    }

    .nav-links.active {
        transform: translateX(0);
    }

    .nav-links .auth-section {
        width: 100%;
        flex-direction: column;
        align-items: flex-start;
        border-bottom: 1px solid rgba(255, 255, 255, 0.3);
        padding-bottom: 10px;
        margin-bottom: 10px;
        order: -1; /* 確保置頂 */
    }

    .nav-links .nav-items {
        flex-direction: column;
        align-items: flex-start;
        width: 100%;
    }

    .nav-links a,
    .nav-links .auth-section a {
        font-size: 18px;
        margin: 10px 0;
        width: 100%;
        text-align: left;
    }

    .nav-links .auth-section .user-dropdown {
        width: 100%;
        text-align: left;
    }
}

/* 桌機版樣式 */
@media (min-width: 769px) {
    .nav-links {
        display: flex;
        align-items: center;
        justify-content: flex-end; /* 確保右對齊 */
        gap: 20px;
    }

    .nav-links .auth-section {
        order: 1; /* 置右 */
        display: flex;
        align-items: center;
        gap: 20px;
    }

    .nav-links .nav-items {
        order: 0; /* 置左 */
        display: flex;
        align-items: center;
        gap: 20px;
    }
}

</style>