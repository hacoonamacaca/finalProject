<template>
  <!-- 導航欄 -->
  <header class="navbar">
    <div class="logo">金碗GoldenBowl Foolog</div>
    <div class="nav-links">
      <HomeLogin />
    </div>
  </header>
  <section class="hero-section">
    <h1>探索附近美食</h1>
    <p>當前位置：{{ address }}</p>
    <input type="text" placeholder="輸入您的查詢內容" v-model="address" />
    <button @click="address.trim() ? searchAddress() : getCurrentLocationAndNavigate()">搜尋</button>
    <a @click="getCurrentLocationAndNavigate"><button
        style="background: transparent; border: none; color: white;">📍</button></a>
    <p v-if="loading" class="loading">正在查詢...</p>
    <p v-else-if="coordinates" class="result">
      經緯度：{{ coordinates.lat }}, {{ coordinates.lon }}
    </p>
    <p v-else-if="error" class="error">{{ error }}</p>
  </section>

</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import HomeLogin from '@/components/Jimmy/HomeLogin.vue';

const isLoggedIn = ref(false);



//地址查詢用
const address = ref(''); // 儲存輸入的地址
const coordinates = ref(null); // 儲存查詢到的座標
const loading = ref(false); // 控制載入狀態
const error = ref(''); // 儲存錯誤訊息
const router = useRouter();

// 輸入地址查詢
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

    // 組合格式化後的地址
    return `${country} ${city} ${district} ${road} ${number}`;
  }

  // 檢查是否為簡化地址
  match = input.match(simpleAddressRegex);
  if (match) {
    // 提取簡化地址的各部分
    const road = match[1].trim(); // 路名 (如松江路)
    const number = match[2]; // 號 (如146號)

    // 組合格式化後的地址
    return `${road} ${number}`;
  }

  // 如果無法匹配，保留原始輸入
  return input;
};

// 查詢 Nominatim API 的函數
const getCoordinates = async () => {
  if (!address.value.trim()) {
    error.value = '請輸入地址';
    coordinates.value = null;
    return false; // 表示失敗
  }

  // 格式化地址
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
      return true; // 表示成功
    } else {
      error.value = '無法找到該地址的座標';
      return false;
    }
  } catch (err) {
    error.value = '查詢失敗，請稍後再試';
    console.error('API 錯誤:', err);
    return false;
  } finally {
    loading.value = false;
  }
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

// 新增搜尋導航函數
const searchAddress = async () => {
  const success = await getCoordinates();
  if (success) {
    router.push({
      path: '/search',
      query: { address: address.value }
    });
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


</script>

<style scoped>
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

/* 導航欄 */
.navbar {
  background-color: #d70f64;
  color: white;
  padding: 15px 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
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
</style>