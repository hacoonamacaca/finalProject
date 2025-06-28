<template>
    <div>
        <!-- 登入表單 -->
        <h3>登入</h3>
        <table>
            <tbody>
                <tr>
                    <td>ID : </td>
                    <td><input type="text" name="username" v-model="username" @blur="checkUsername"
                            @focus="message = ''">
                    </td>
                    <td><span>{{ message }}</span></td>
                </tr>
                <tr>
                    <td>PWD : </td>
                    <td><input type="password" name="password" v-model="password"></td>
                    <td></td>
                </tr>
                <tr>
                    <td></td>
                    <td align="right"><input type="submit" value="login" @click="login"></td>
                </tr>
            </tbody>
        </table>

        <!-- 查詢用戶 Email -->
        <h3>查詢用戶 Email</h3>
        <table>
            <tbody>
                <tr>
                    <td>姓名 : </td>
                    <td><input type="text" v-model="searchName" @blur="fetchUserEmail" @focus="emailMessage = ''"></td>
                    <td><span>{{ emailMessage }}</span></td>
                </tr>
                <tr>
                    <td>Email : </td>
                    <td><input type="text" v-model="userEmail" disabled></td>
                    <td></td>
                </tr>
            </tbody>
        </table>

        <!-- 新增用戶表單 -->
        <h3>新增用戶</h3>
        <table>
            <tbody>
                <tr>
                    <td>姓名 : </td>
                    <td><input type="text" v-model="newUser.name"></td>
                </tr>
                <tr>
                    <td>Email : </td>
                    <td><input type="email" v-model="newUser.email"></td>
                </tr>
                <tr>
                    <td></td>
                    <td align="right"><input type="submit" value="新增用戶" @click="createUser"></td>
                </tr>
            </tbody>
        </table>
    </div>
</template>

<script setup>
import Swal from 'sweetalert2';
import axiosapi from "@/plungins/axios.js";
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import useUserStore from "@/stores/user.js";

const userStore = useUserStore();
const router = useRouter();

const username = ref("");
const password = ref("");
const message = ref("");
const searchName = ref(""); // 用於查詢 Email 的用戶名稱
const userEmail = ref(""); // 用於顯示查詢到的 Email
const emailMessage = ref(""); // 用於顯示查詢 Email 的提示信息
const newUser = ref({ name: "", email: "" }); // 新增用戶的數據

// 登入功能
async function login() {
    Swal.fire({
        title: "Loading......",
        allowOutsideClick: false,
        showConfirmButton: false,
    });

    if (username.value === "") {
        username.value = null;
    }
    if (password.value === "") {
        password.value = null;
    }
    const body = {
        username: username.value,
        password: password.value,
    };

    userStore.setToken("");
    userStore.setEmail("");
    userStore.setLogin(false);
    try {
        const response = await axiosapi.post("/ajax/secure/login", body);
        console.log("response1", response);
        if (response.data.success) {
            await Swal.fire({
                text: response.data.message,
                icon: "success",
            });

            userStore.setToken(response.data.token);
            userStore.setEmail(response.data.user);
            userStore.setLogin(true);
            router.push("/");
        } else {
            Swal.fire({
                text: response.data.message,
                icon: "warning",
            });
        }
    } catch (error) {
        console.log("error", error);
        Swal.fire({
            text: error.message,
            icon: "error",
        });
    }
}

// 檢查用戶名是否存在（僅用於登入）
async function checkUsername() {
    if (username.value === "") {
        username.value = null;
        return;
    }
    const body = {
        username: username.value,
    };
    try {
        const response = await axiosapi.post("/ajax/secure/login/exists", body);
        message.value = response.data;
    } catch (error) {
        console.log("error", error);
        message.value = "用戶不存在";
    }
}

// 查詢用戶 Email
async function fetchUserEmail() {
    if (searchName.value === "") {
        searchName.value = null;
        userEmail.value = "";
        emailMessage.value = "";
        return;
    }
    try {
        const response = await axiosapi.get(`/api/users/name/${searchName.value}`);
        userEmail.value = response.data.email || "";
        emailMessage.value = "查詢成功";
    } catch (error) {
        console.log("error", error);
        emailMessage.value = "用戶不存在";
        userEmail.value = "";
    }
}

// 新增用戶
async function createUser() {
    if (!newUser.value.name || !newUser.value.email) {
        Swal.fire({
            text: "請填寫姓名和 Email",
            icon: "warning",
        });
        return;
    }

    try {
        const response = await axiosapi.post("/api/users", newUser.value);
        await Swal.fire({
            text: "用戶新增成功",
            icon: "success",
        });
        newUser.value = { name: "", email: "" }; // 重置表單
    } catch (error) {
        console.log("error", error);
        Swal.fire({
            text: "新增用戶失敗",
            icon: "error",
        });
    }
}
</script>

<style>
table {
    margin-bottom: 20px;
}

input {
    margin: 5px;
    padding: 5px;
}
</style>