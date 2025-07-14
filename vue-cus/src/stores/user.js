// src/stores/user.js
import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useUserStore = defineStore('user', () => {
    // 初始值從 localStorage 取，但後續都用 pinia 狀態！
    const fullName = ref(localStorage.getItem('userFullName') || '');
    const userId = ref(localStorage.getItem('userId') || ''); 
    const email = ref(localStorage.getItem('userEmail') || '');
    const token = ref(localStorage.getItem('token') || '');
    const isLogin = ref(false);

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

    return {
        fullName, setFullName,
        email, setEmail,
        token, setToken,
        userId, setUserId, 
        isLogin, setLogin
    }
});
