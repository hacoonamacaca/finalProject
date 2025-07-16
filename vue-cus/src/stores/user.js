// src/stores/user.js
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useUserStore = defineStore('user', () => {
    // 會員狀態
    const fullName = ref('')
    const email = ref('')
    const password = ref('')
    const token = ref('')
    const userId = ref('')
    const phone = ref('')
    const verified = ref(false)

    // 狀態判斷
    const isUserLogin = computed(() => !!token.value)

    // === 同步 Pinia 狀態與 localStorage ===
    function syncFromStorage() {
        fullName.value = localStorage.getItem('userFullName') || ''
        email.value = localStorage.getItem('userEmail') || ''
        token.value = localStorage.getItem('token') || ''
        userId.value = localStorage.getItem('userId') || ''
        phone.value = localStorage.getItem('phone') || ''
        verified.value = localStorage.getItem('verified') === '1'
    }
    syncFromStorage()

    // setter ...
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

    // 全部清空
    function logoutAll() {
        userLogout()
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
        // 全清
        logoutAll,
        // 同步 localStorage 狀態
        syncFromStorage,
    }
})