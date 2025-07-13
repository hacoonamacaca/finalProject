// src/stores/user.js
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import axios from '@/plungins/axios.js' // 你的 axios 寫法

export const useUserStore = defineStore('user', () => {
    // 會員狀態
    const fullName = ref('')
    const email = ref('')
    const password = ref('')
    const token = ref('')
    const userId = ref('')
    const phone = ref('')
    const verified = ref(false)

    // 業者狀態
    const ownerFullName = ref('')
    const ownerEmail = ref('')
    const ownerId = ref('')

    // 餐廳註冊流程
    const storeId = ref('')
    const storeName = ref('')
    const address = ref({
        city: '',
        district: '',
        zip: '',
        street: '',
        door: '',
        enAddress: '',
        lat: '',
        lon: ''
    })

    // 餐廳完整資料（集中管理所有欄位）
    const storeProfile = ref({
        storeName: '',
        address: '',
        intro: '',
        phone: '',
        email: '',
        isEmailVerified: false
    })

    // 狀態判斷
    const isUserLogin = computed(() => !!token.value)
    const isOwnerLogin = computed(() => !!ownerId.value)

    // === 同步 Pinia 狀態與 localStorage ===
    function syncFromStorage() {
        fullName.value = localStorage.getItem('userFullName') || ''
        email.value = localStorage.getItem('userEmail') || ''
        token.value = localStorage.getItem('token') || ''
        userId.value = localStorage.getItem('userId') || ''
        phone.value = localStorage.getItem('phone') || ''
        verified.value = localStorage.getItem('verified') === '1'

        ownerFullName.value = localStorage.getItem('storeFullName') || ''
        ownerEmail.value = localStorage.getItem('storeEmail') || ''
        ownerId.value = localStorage.getItem('ownerId') || ''

        storeId.value = localStorage.getItem('storeId') || ''
        try {
            address.value = JSON.parse(localStorage.getItem('address')) || {
                city: '',
                district: '',
                zip: '',
                street: '',
                door: '',
                enAddress: '',
                lat: '',
                lon: ''
            }
        } catch {
            address.value = {
                city: '',
                district: '',
                zip: '',
                street: '',
                door: '',
                enAddress: '',
                lat: '',
                lon: ''
            }
        }
        // storeProfile 也同步
        try {
            const sp = JSON.parse(localStorage.getItem('storeProfile'))
            if (sp) storeProfile.value = sp
        } catch {
            storeProfile.value = {
                storeName: '',
                address: '',
                intro: '',
                phone: '',
                email: '',
                isEmailVerified: false
            }
        }
    }

    // 初始化時同步
    syncFromStorage()

    // 會員 setter ...
    function setFullName(val) { fullName.value = val; localStorage.setItem('userFullName', val) }
    function setEmail(val) { email.value = val; localStorage.setItem('userEmail', val) }
    function setPassword(val) { password.value = val }
    function setToken(val) { token.value = val; localStorage.setItem('token', val) }
    function setUserId(val) { userId.value = val; localStorage.setItem('userId', val) }
    function setPhone(val) { phone.value = val; localStorage.setItem('phone', val) }
    function setLogin({ fullName, email, userId, token }) {
        setFullName(fullName); setEmail(email); setUserId(userId); setToken(token)
        if (phone.value) setPhone(phone.value)
    }
    function setVerified(val) { verified.value = !!val; localStorage.setItem('verified', verified.value ? '1' : '0') }
    function userLogout() {
        fullName.value = ''
        email.value = ''
        userId.value = ''
        phone.value = ''
        token.value = ''
        verified.value = false
        localStorage.removeItem('userFullName')
        localStorage.removeItem('userEmail')
        localStorage.removeItem('userId')
        localStorage.removeItem('phone')
        localStorage.removeItem('token')
        localStorage.removeItem('verified')
    }

    // 業者 setter ...
    function setOwnerFullName(val) { ownerFullName.value = val; localStorage.setItem('storeFullName', val) }
    function setOwnerEmail(val) { ownerEmail.value = val; localStorage.setItem('storeEmail', val) }
    function setOwnerId(val) { ownerId.value = val; localStorage.setItem('ownerId', val) }
    function ownerLogout() {
        ownerFullName.value = ''
        ownerEmail.value = ''
        ownerId.value = ''
        localStorage.removeItem('storeFullName')
        localStorage.removeItem('storeEmail')
        localStorage.removeItem('ownerId')
    }

    // 餐廳setter ...
    function setStoreName(val) { storeName.value = val; localStorage.setItem('storeName', val) }
    function setStoreId(val) { storeId.value = val; localStorage.setItem('storeId', val) }
    function setAddressInfo(obj) {
        address.value = { ...address.value, ...obj }
        localStorage.setItem('address', JSON.stringify(address.value))
    }

    // storeProfile setter
    function setStoreProfile(obj) {
        storeProfile.value = { ...storeProfile.value, ...obj }
        localStorage.setItem('storeProfile', JSON.stringify(storeProfile.value))
    }

    // storeProfile fetch (同步後端)
// user.js
async function fetchStoreProfile() {
    const ownerId = localStorage.getItem('ownerId')
    if (!ownerId) return

    const resp = await axios.get('/api/stores/profile', {
        params: { ownerId }
    })
    if (resp.data) setStoreProfile(resp.data) // << 記得呼叫 setStoreProfile
}

    // 全部清空
    function logoutAll() {
        userLogout()
        ownerLogout()
        storeId.value = ''
        address.value = {
            city: '', district: '', zip: '', street: '', door: '', enAddress: '', lat: '', lon: ''
        }
        storeProfile.value = {
            storeName: '',
            address: '',
            intro: '',
            phone: '',
            email: '',
            isEmailVerified: false
        }
        localStorage.removeItem('storeId')
        localStorage.removeItem('address')
        localStorage.removeItem('storeProfile')
    }

    return {
        // 會員
        fullName, setFullName,
        email, setEmail,
        password, setPassword,
        token, setToken,
        userId, setUserId,
        phone, setPhone,
        isUserLogin,
        userLogout,
        setLogin,
        verified, setVerified,
        // 業者
        ownerFullName, setOwnerFullName,
        ownerEmail, setOwnerEmail,
        ownerId, setOwnerId,
        isOwnerLogin,
        ownerLogout,
        // 餐廳
        storeId, setStoreId,
        storeName, setStoreName,
        address, setAddressInfo,
        // storeProfile
        storeProfile, setStoreProfile, fetchStoreProfile,
        // 全清
        logoutAll,
        // 同步 localStorage 狀態
        syncFromStorage,
    }
})