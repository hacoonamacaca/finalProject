    <template>
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
import { useRouter } from 'vue-router'
import { cityDistrictMap } from '@/assets/cityDistrictMap.js'
import L from 'leaflet'
import 'leaflet/dist/leaflet.css'

const router = useRouter()
const city = ref('')
const district = ref('')
const zip = ref('')
const street = ref('')
const door = ref('')
const enAddress = ref('')
const lat = ref('')
const lon = ref('')
let map, marker

// 全台城市/區域清單
const cityList = cityDistrictMap
const districtList = computed(() => {
    const c = cityDistrictMap.find(i => i.city === city.value)
    return c ? c.districts : []
})

// 郵遞區域
watch([city, district], () => {
    const c = cityDistrictMap.find(i => i.city === city.value)
    const d = c?.districts.find(i => i.name === district.value)
    zip.value = d?.zip || ''
})

// 合併完整地址
const mainAddress = computed(() =>
    [zip.value, city.value, district.value, street.value, door.value]
        .filter(Boolean).join(' ')
)

// Google Maps iframe URL（隱藏）
const googleUrl = computed(() =>
    lat.value && lon.value
        ? `https://www.google.com/maps?q=${lat.value},${lon.value}&hl=zh-TW&z=16&output=embed`
        : ''
)

// 建立 Leaflet 地圖
onMounted(() => {
    map = L.map('map', {
        zoomControl: false,
        dragging: false,
        scrollWheelZoom: false,
        doubleClickZoom: false,
        attributionControl: false,
    }).setView([25.0340, 121.5645], 13)
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png').addTo(map)
})

// 只要四個欄位任一改變，就自動重新查位置
watch([door, street, city, district], () => {
    if (city.value && district.value && street.value) {
        searchLocation()
    } else {
        lat.value = lon.value = ''
    }
})

// 呼叫 Nominatim API 查經緯
async function searchLocation() {
    if (!mainAddress.value) return
    try {
        const res = await fetch(
            `https://nominatim.openstreetmap.org/search?format=json&addressdetails=1&q=${encodeURIComponent(mainAddress.value)}`
        )
        if (!res.ok) throw new Error()
        const data = await res.json()
        if (!data[0]) return
        lat.value = data[0].lat
        lon.value = data[0].lon

        // 更新 Leaflet
        const p = [parseFloat(lat.value), parseFloat(lon.value)]
        map.setView(p, 17)
        if (!marker) {
            marker = L.marker(p).addTo(map)
        } else {
            marker.setLatLng(p)
        }
    } catch (e) {
        console.error('定位失敗', e)
        lat.value = lon.value = ''
    }
}

function clearCity() {
    city.value = ''
    district.value = ''
    zip.value = ''
    enAddress.value = ''
}

function goNext() {
    router.push('/verifyPending')
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
