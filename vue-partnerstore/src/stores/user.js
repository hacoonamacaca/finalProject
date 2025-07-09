// src/stores/user.js
import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useUserStore = defineStore('user', () => {
    // 初始值從 localStorage 取，但後續都用 pinia 狀態！
    const fullName = ref(localStorage.getItem('userFullName') || '');
    const email = ref(localStorage.getItem('userEmail') || '');
    const token = ref(localStorage.getItem('token') || '');
    const isLogin = ref(false);

    function setFullName(name) {
        fullName.value = name;
        localStorage.setItem('userFullName', name); // 雙向寫入
    }

    function setEmail(data) {
        email.value = data;
        localStorage.setItem('userEmail', data);
    }

    function setToken(data) {
        token.value = data;
        localStorage.setItem('token', data);
    }

    function setLogin(data) {
        isLogin.value = data;
    }

    return {
        fullName, setFullName,
        email, setEmail,
        token, setToken,
        isLogin, setLogin
    }
});

export const useStoreRegister = defineStore('storeRegister', {
    state: () => ({
        ownerId: null,
        storeName: '',
        storeCategory: '',
        phone: '',
        storeIntro: '',
        files: null,
        address: '',      // 完整地址字串
        city: '',
        district: '',
        zip: '',
        street: '',
        door: '',
        enAddress: '',
        lat: '',
        lon: '',
    }),
    actions: {
        setBasicInfo({ ownerId, storeName, storeCategory, phone, storeIntro, files }) {
            this.ownerId = ownerId
            this.storeName = storeName
            this.storeCategory = storeCategory
            this.phone = phone
            this.storeIntro = storeIntro
            this.files = files
        },
        setAddressInfo({ city, district, zip, street, door, enAddress, lat, lon }) {
            this.city = city
            this.district = district
            this.zip = zip
            this.street = street
            this.door = door
            this.enAddress = enAddress
            this.lat = lat
            this.lon = lon
            // 組一個完整地址方便用
            this.address = [zip, city, district, street, door].filter(Boolean).join(' ')
        },
        clear() {
            this.ownerId = null
            this.storeName = ''
            this.storeCategory = ''
            this.phone = ''
            this.storeIntro = ''
            this.files = null
            this.address = ''
            this.city = ''
            this.district = ''
            this.zip = ''
            this.street = ''
            this.door = ''
            this.enAddress = ''
            this.lat = ''
            this.lon = ''
        }
    }
})