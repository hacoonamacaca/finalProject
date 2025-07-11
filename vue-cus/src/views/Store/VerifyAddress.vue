    <template>
        <div v-if="errorMsg" class="alert alert-danger mb-2">{{ errorMsg }}</div>
        <div class="address-bg py-5">
            <div class="container" style="max-width:420px;">
                <h3 class="fw-bold mb-2">驗證您的商家地址</h3>
                <div class="mb-2 text-secondary" style="font-size:15px;">
                    消費者及外送夥伴將透過下方地址找到您的餐廳或商店
                </div>

                <!-- 城市 -->
                <div class="mb-2 position-relative">
                    <select class="form-select no-arrow" v-model="city" @change="searchLocation" required>
                        <option value="" disabled>城市 *</option>
                        <option v-for="item in cityList" :key="item.city" :value="item.city">{{ item.city }}</option>
                    </select>
                    <button class="clear-btn" v-if="city" @mousedown.prevent="clearCity" type="button"
                        style="right:11px;top:13px;">
                        <svg width="18" height="18" fill="none" viewBox="0 0 22 22">
                            <path d="M6 6l10 10M16 6L6 16" stroke="#ccc" stroke-width="2" stroke-linecap="round" />
                        </svg>
                    </button>
                </div>
                <!-- 地區 -->
                <template v-if="city">
                    <div class="mb-2">
                        <select class="form-select no-arrow" v-model="district" @change="searchLocation" required>
                            <option value="" disabled>地區 *</option>
                            <option v-for="d in districtList" :key="d.name" :value="d.name">{{ d.name }}</option>
                        </select>
                    </div>
                    <!-- 郵遞區域 -->
                    <div class="mb-2">
                        <input type="text" class="form-control" v-model="zip" placeholder="郵遞區域 *" readonly required />
                    </div>
                    <!-- 街道 -->
                    <div class="mb-2">
                        <input type="text" class="form-control" v-model="street" placeholder="街道名稱 *"
                            @blur="searchLocation" required />
                    </div>
                    <!-- 門牌 -->
                    <div class="mb-2">
                        <input type="text" class="form-control" v-model="door" placeholder="門牌號碼及樓層(其他地址詳細資訊) *"
                            @blur="searchLocation" required />
                    </div>
                    <!-- 公司地址英文 -->
                    <div class="mb-2">
                        <input type="text" class="form-control" v-model="enAddress" placeholder="您的公司地址英文翻譯 *"
                            required />
                    </div>
                </template>

                <!-- Leaflet 地圖 -->
                <div class="mb-2 d-flex align-items-center">
                    <button class="btn btn-sm btn-secondary me-2" @click="searchLocation">
                        查詢位置
                    </button>
                    <small class="text-muted">（離開欄位或按此按鈕都會自動定位）</small>
                </div>
                <div id="map" style="height:220px; border-radius:12px;"></div>

                <!-- 緯經度 -->
                <div v-if="lat && lon" class="mb-2 text-secondary">
                    緯度：<span class="fw-bold">{{ lat }}</span>，
                    經度：<span class="fw-bold">{{ lon }}</span>
                </div>

                <!-- Google Maps iframe 備用 -->
                <div class="mb-3" style="height:0;overflow:hidden;">
                    <iframe width="100%" height="220" frameborder="0" style="border:0; pointer-events: none;"
                        :src="googleUrl" allowfullscreen></iframe>
                </div>

                <!-- 商家地址（自動帶入、readonly） -->
                <div class="mb-2">
                    <input type="text" class="form-control" :value="mainAddress" placeholder="商家地址" readonly />
                </div>

                <button type="submit" class="btn btn-main w-100" @click="goNext">繼續</button>
            </div>
        </div>
    </template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { cityDistrictMap } from '@/assets/cityDistrictMap.js'
import { useUserStore } from '@/stores/user.js'
import L from 'leaflet'
import 'leaflet/dist/leaflet.css'
import axios from '@/plungins/axios.js'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

// Pinia 狀態同步
userStore.syncFromStorage()

const city = ref('')
const district = ref('')
const zip = ref('')
const street = ref('')
const door = ref('')
const enAddress = ref('')
const lat = ref('')
const lon = ref('')
const errorMsg = ref('')

let map, marker

// storeId 優先順序（route.query > Pinia > localStorage（這樣頁面跳轉最新 storeId 一定優先））
const storeId = ref(
    route.query.storeId ||
    userStore.storeId ||
    localStorage.getItem('storeId')
)
console.log('進入 verifyAddress, storeId:', storeId.value)
if (!storeId.value) {
    alert('請先完成商家基本資料')
    router.push('/registerStoreInfo')
}

// 如果有填過地址，初始化填回去
onMounted(() => {
    userStore.syncFromStorage()
    if (userStore.address && userStore.address.city) {
        city.value = userStore.address.city
        district.value = userStore.address.district
        zip.value = userStore.address.zip
        street.value = userStore.address.street
        door.value = userStore.address.door
        enAddress.value = userStore.address.enAddress
        lat.value = userStore.address.lat
        lon.value = userStore.address.lon
    }
    if (storeId.value) {
        userStore.setStoreId(storeId.value)
    }
    map = L.map('map', {
        zoomControl: false,
        dragging: false,
        scrollWheelZoom: false,
        doubleClickZoom: false,
        attributionControl: false,
    }).setView([25.0340, 121.5645], 13)
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png').addTo(map)

    if (lat.value && lon.value) {
        const p = [parseFloat(lat.value), parseFloat(lon.value)]
        marker = L.marker(p).addTo(map)
        map.setView(p, 17)
    }
})

const cityList = cityDistrictMap
const districtList = computed(() => {
    const c = cityDistrictMap.find(i => i.city === city.value)
    return c ? c.districts : []
})

watch([city, district], () => {
    const c = cityDistrictMap.find(i => i.city === city.value)
    const d = c?.districts.find(i => i.name === district.value)
    zip.value = d?.zip || ''
})

const mainAddress = computed(() =>
    [zip.value, city.value, district.value, street.value, door.value]
        .filter(Boolean).join(' ')
)

const googleUrl = computed(() =>
    lat.value && lon.value
        ? `https://www.google.com/maps?q=${lat.value},${lon.value}&hl=zh-TW&z=16&output=embed`
        : ''
)

watch([city, district, street, door], ([c, d, s, o]) => {
    if (c && d && s && o) {
        searchLocation()
    } else {
        lat.value = ''
        lon.value = ''
        if (marker) marker.remove()
        marker = null
    }
})

// 使用自己的後端 API 查經緯度
async function searchLocation() {
    errorMsg.value = ''
    if (!mainAddress.value) return
    try {
        const url = `/api/geo/latlon?address=${encodeURIComponent(mainAddress.value)}`
        const res = await axios.get(url)
        if (!res.data.success) {
            errorMsg.value = res.data.message || '查無經緯度，請確認地址格式'
            lat.value = ''
            lon.value = ''
            if (marker) marker.remove()
            marker = null
            return
        }
        lat.value = res.data.lat
        lon.value = res.data.lon || res.data.lng

        // 更新地圖
        const p = [parseFloat(lat.value), parseFloat(lon.value)]
        map.setView(p, 17)
        if (!marker) {
            marker = L.marker(p).addTo(map)
        } else {
            marker.setLatLon(p)
        }
    } catch (e) {
        errorMsg.value = 'API 請求失敗，請稍後再試'
        lat.value = ''
        lon.value = ''
        if (marker) marker.remove()
        marker = null
    }
}

function clearCity() {
    city.value = ''
    district.value = ''
    zip.value = ''
    enAddress.value = ''
}

async function goNext() {
    console.log('goNext 送出 storeId:', storeId.value)
    if (!storeId.value) {
        errorMsg.value = '找不到 storeId，請回前頁重新填寫'
        router.push('/registerStoreInfo')
        return
    }

    // --- Pinia 狀態同步 ---
    userStore.setAddressInfo({
        city: city.value,
        district: district.value,
        zip: zip.value,
        street: street.value,
        door: door.value,
        enAddress: enAddress.value,
        lat: lat.value,
        lon: lon.value
    })

    const payload = {
        storeId: storeId.value,
        address: mainAddress.value,
        city: city.value,
        district: district.value,
        zip: zip.value,
        street: street.value,
        door: door.value,
        enAddress: enAddress.value,
        lat: lat.value,
        lon: lon.value
    }
    try {
        const res = await axios.post('/api/stores/updateAddress', payload)
        if (res.data.success) {
            router.push('/verifyPending')
        } else {
            errorMsg.value = res.data.message || '地址更新失敗'
        }
    } catch (e) {
        errorMsg.value = '儲存失敗，請稍後再試'
    }
}
</script>

<style scoped>
.address-bg {
    min-height: 100vh;
    background: #fafafb;
}

.container {
    background: #fff;
    border-radius: 13px;
    box-shadow: 0 2px 18px 4px rgba(0, 0, 0, 0.08);
    padding: 38px 24px 28px 24px;
}

.btn-main {
    background: #ffba20;
    color: #fff;
    font-weight: bold;
    font-size: 20px;
    border-radius: 10px;
    border: none;
    transition: filter 0.15s;
    box-shadow: 0 2px 8px 1px #ffba200f;
    height: 48px;
}

.btn-main:hover {
    filter: brightness(1.08);
    background: #ffba20;
}

.text-pink {
    color: #ffba20;
    font-weight: 600;
}

.custom-suggest-list {
    position: absolute;
    left: 0;
    top: 44px;
    min-width: 100%;
    border-radius: 9px;
    box-shadow: 0 3px 12px 0 #eee;
    border: none;
    padding: 0;
    z-index: 100;
}

.clear-btn {
    background: none;
    border: none;
    position: absolute;
    right: 9px;
    top: 10px;
    cursor: pointer;
    z-index: 10;
    padding: 0;
}

select.no-arrow {
    appearance: none;
    -webkit-appearance: none;
    background-image: none !important;
}

select.no-arrow::-ms-expand {
    display: none;
}
</style>
