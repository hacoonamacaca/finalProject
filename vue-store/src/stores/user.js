// src/stores/user.js
import {
    defineStore
} from 'pinia'
import {
    ref
} from 'vue'
import axios from '@/plungins/axios.js' // 🔥 NEW: 導入 axios

export const useUserStore = defineStore('user', () => {
    // 初始值從 localStorage 取，但後續都用 pinia 狀態！
    const fullName = ref(localStorage.getItem('userFullName') || '');
    const userId = ref(localStorage.getItem('userId') || '');
    const email = ref(localStorage.getItem('userEmail') || '');
    const token = ref(localStorage.getItem('token') || '');
    const isLogin = ref(false);

    // 🔥 NEW: 店家相關狀態
    const ownerId = ref(localStorage.getItem('ownerId') || '');
    const storeProfile = ref({});

    function setFullName(name) {
        fullName.value = name;
        localStorage.setItem('userFullName', name); // 雙向寫入
    }

    function setUserId(id) { // <-- 新增這一行
        userId.value = id;
        localStorage.setItem('userId', id);
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

    // 🔥 NEW: 店家相關函數
    function setOwnerId(id) {
        ownerId.value = id;
        localStorage.setItem('ownerId', id);
    }
    
    function setStoreProfile(profile) {
        storeProfile.value = profile;
        // 🔥 同步到 localStorage 以便與現有系統相容
        localStorage.setItem('storeProfile', JSON.stringify(profile));
        if (profile.id) {
            localStorage.setItem('storeId', profile.id);
        }
        console.log('📋 [Pinia] setStoreProfile:', profile);
    }

    // 🔥 NEW: 從後端獲取店家資料
    async function fetchStoreProfile() {
        try {
            const ownerIdValue = ownerId.value || localStorage.getItem('ownerId');
            if (!ownerIdValue) {
                console.warn('⚠️ [Pinia] 沒有 ownerId，無法獲取 store profile');
                return;
            }

            console.log(`🚀 [Pinia] 正在獲取 owner ${ownerIdValue} 的店家資料...`);
            
            // 🔥 修正：使用正確的 API 端點並加上錯誤處理
            try {
                const response = await axios.get('/api/stores/profile/all', {
                    params: { ownerId: ownerIdValue }
                });

                console.log('✅ [Pinia] 店家列表:', response.data);

                // 取第一個店家作為主要店家
                if (response.data && response.data.length > 0) {
                    const firstStore = response.data[0];
                    setStoreProfile(firstStore);
                    console.log('📋 [Pinia] 設定主要店家:', firstStore);
                } else {
                    console.warn('⚠️ [Pinia] 該 owner 沒有店家資料');
                    setStoreProfile({});
                }
            } catch (apiError) {
                // 🔥 如果 API 失敗，嘗試從 localStorage 讀取
                console.warn('⚠️ [Pinia] API 呼叫失敗，嘗試從 localStorage 讀取:', apiError.message);
                const storedProfile = localStorage.getItem('storeProfile');
                if (storedProfile) {
                    try {
                        const parsedProfile = JSON.parse(storedProfile);
                        setStoreProfile(parsedProfile);
                        console.log('📋 [Pinia] 從 localStorage 載入店家資料:', parsedProfile);
                    } catch (parseError) {
                        console.error('❌ [Pinia] 無法解析 localStorage 中的店家資料');
                        setStoreProfile({});
                    }
                } else {
                    setStoreProfile({});
                }
            }

        } catch (error) {
            console.error('❌ [Pinia] fetchStoreProfile failed:', error);
            setStoreProfile({});
        }
    }

    // 🔥 NEW: 初始化函數
    function initializeFromStorage() {
        const storedOwnerId = localStorage.getItem('ownerId');
        const storedStoreProfile = localStorage.getItem('storeProfile');
        
        if (storedOwnerId) {
            ownerId.value = storedOwnerId;
        }
        
        if (storedStoreProfile) {
            try {
                storeProfile.value = JSON.parse(storedStoreProfile);
            } catch (e) {
                console.warn('⚠️ [Pinia] 無法解析 storeProfile from localStorage');
            }
        }
    }

    return {
        fullName,
        setFullName,
        email,
        setEmail,
        token,
        setToken,
        userId,
        setUserId,
        isLogin,
        setLogin,

        // 🔥 NEW: 店家相關狀態和方法
        ownerId,
        setOwnerId,
        storeProfile,
        setStoreProfile,
        fetchStoreProfile,
        initializeFromStorage
    }
});