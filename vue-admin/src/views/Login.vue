<template>
    <div>
        <h2>管理員登入</h2>
        <form @submit.prevent="login">
            <label>帳號
                <input type="text" name="username" v-model="username">
            </label><br>
            <label>密碼
                <input type="text" name="password" v-model="password">
            </label><br>
            <button type="submit">登入</button>
        </form>
    </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();
const username = ref('');
const password = ref('');

function login() {
    if (username.value && password.value) {
        router.push('/')
    } else {
        alert('請輸入帳號密碼');
    }
}

</script>

<style></style>