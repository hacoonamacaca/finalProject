import { ref, watch } from 'vue';
import { defineStore } from 'pinia';

export const useLocationStore = defineStore('location', () => {
    const address = ref(localStorage.getItem('userAddress') || ''); // 從 localStorage 載入地址
    const coordinates = ref(JSON.parse(localStorage.getItem('userCoordinates')) || null); // 從 localStorage 載入座標
    const loading = ref(false);
    const error = ref('');

    // 監聽 address 或 coordinates 變化，並存入 localStorage
    watch(address, (newAddress) => {
        localStorage.setItem('userAddress', newAddress);
    });

    watch(coordinates, (newCoordinates) => {
        localStorage.setItem('userCoordinates', JSON.stringify(newCoordinates));
    }, { deep: true }); // deep: true 確保當 coordinates 內部屬性變化時也能觸發

    // 格式化地址 (從您的 Header 組件複製過來)
    const formatAddress = (input) => {
        if (!input.trim()) return input;
        const fullAddressRegex = /^(\S+?)([縣市])(.+?)(區|鄉|鎮|市)(.*?)(\d+號)/;
        const simpleAddressRegex = /^(.+?)(\d+號)$/;
        let match = input.match(fullAddressRegex);
        if (match) {
            return `${match[1]}${match[2]}${match[3]}${match[4]}${match[5].trim()}${match[6]}`;
        }
        match = input.match(simpleAddressRegex);
        if (match) {
            return `${match[1].trim()}${match[2]}`;
        }
        return input;
    };

    // 格式化台灣地址 (從您的 Header 組件複製過來)
    const formatTaiwanAddress = (addressData) => {
        if (!addressData) return '';
        const country = addressData.country || '臺灣';
        const city = addressData.city || addressData.county || '';
        const district = addressData.suburb || addressData.town || addressData.city_district || '';
        const village = addressData.neighbourhood || addressData.village || '';
        const road = addressData.road || '';
        let houseNumber = addressData.house_number || '';
        if (houseNumber.includes('之')) {
            houseNumber = houseNumber.split('之')[1] || houseNumber;
        }
        return [country, city, district, village, road, houseNumber].filter(part => part).join('');
    };

    // 查詢座標 (修改為 Store 內部方法)
    const getCoordinates = async (inputAddress) => {
        const addrToSearch = inputAddress ? formatAddress(inputAddress) : formatAddress(address.value);
        if (!addrToSearch.trim()) {
            error.value = '請輸入地址';
            coordinates.value = null;
            return false;
        }
        loading.value = true;
        error.value = '';
        coordinates.value = null;
        try {
            const url = `https://nominatim.openstreetmap.org/search?format=json&q=${encodeURIComponent(addrToSearch)}`;
            const response = await fetch(url, {
                headers: { 'User-Agent': 'Jimmy/tokin81@yahoo.com.tw' }
            });
            const data = await response.json();
            if (data.length > 0) {
                coordinates.value = { lat: data[0].lat, lon: data[0].lon };
                address.value = addrToSearch; // 確保地址和座標同步
                return true;
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

    // 獲取當前位置 (修改為 Store 內部方法)
    const getCurrentLocation = async () => {
        if (!navigator.geolocation) {
            alert('您的瀏覽器不支援定位功能');
            return false;
        }
        loading.value = true;
        error.value = '';
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
                coordinates.value = { lat: latitude, lon: longitude }; // 也要保存當前位置的座標
                return true;
            } else {
                alert('無法解析地址，請稍後再試');
                return false;
            }
        } catch (err) {
            console.error('定位失敗:', err);
            alert('無法獲取位置，請檢查權限或稍後再試');
            return false;
        } finally {
            loading.value = false;
        }
    };

    // 設定地址 (用於外部直接設定，例如從路由參數)
    const setAddress = (newAddress) => {
        address.value = newAddress;
    };

    return {
        address,
        coordinates,
        loading,
        error,
        setAddress,
        getCoordinates,
        getCurrentLocation,
    };
});