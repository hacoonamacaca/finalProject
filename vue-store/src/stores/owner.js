import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import axios from '@/plungins/axios.js' // 你的 axios 寫法

export const useOwnerStore = defineStore('user', () => {
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
    const isOwnerLogin = computed(() => !!ownerId.value)

    // === 同步 Pinia 狀態與 localStorage ===
    function syncFromStorage() {
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

    // 餐廳 setter ...
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
    async function fetchStoreProfile() {
        const ownerIdVal = localStorage.getItem('ownerId')
        if (!ownerIdVal) return

        const resp = await axios.get('/api/stores/profile', {
            params: { ownerId: ownerIdVal }
        })
        if (resp.data) setStoreProfile(resp.data)
    }

    // 全部清空
    function logoutAll() {
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
        storeProfile, setStoreProfile, fetchStoreProfile,
        // 全清
        logoutAll,
        // 同步 localStorage 狀態
        syncFromStorage,
    }
})