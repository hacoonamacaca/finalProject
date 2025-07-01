    <template>
    <div class="verify-bg d-flex justify-content-center align-items-center">
        <div class="verify-card">
        <h2 class="fw-bold mb-2" style="font-size: 2rem;">驗證您的電子郵件</h2>
        <div class="text-secondary mb-3" style="font-size: 1.07rem;">
            要驗證您的帳戶，請點擊我們發送到<br>
            <span class="fw-bold text-dark" style="font-size: 1.13rem;">{{ email }}</span>
            的連結。如果看不到，請檢查您的垃圾郵件信箱。
        </div>
        <img src="https://cdn-icons-png.flaticon.com/512/561/561127.png" alt="mail" class="mail-img mb-3">
        <button class="btn btn-main w-100 mb-2" @click="handleContinue">跳過並繼續</button>
        <div class="mb-1" style="font-size: 1rem;">
            還沒收到？
            <a href="#" @click.prevent="handleResend" class="resend-link">重新發送</a>
            或
            <a href="#" @click.prevent="showChangeDialog = true" class="change-link">更改電子郵件</a>
        </div>
        <div class="text-center text-pink mt-3" style="font-size: 15px;">
            繼續註冊流程之前應同意
        </div>
        </div>
        <!-- 彈窗 -->
        <ChangeEmailDialog
        v-if="showChangeDialog"
        :show="showChangeDialog"
        :email="email"
        @close="showChangeDialog = false"
        @send="handleChangeEmail"
        />
    </div>
    </template>

    <script setup>
    import { ref } from 'vue'
    import { useRouter, useRoute } from 'vue-router'
    import ChangeEmailDialog from './ChangeEmailDialog.vue' // 可放同資料夾

    const router = useRouter()
    const route = useRoute()
    const showChangeDialog = ref(false)
    const email = ref(route.query.email || 'eattiy1986@gmail.com') // 要用 ref！

    function handleContinue() {
    router.push({
        path: '/registerStoreInfo',
        query: {
        ...route.query,
        email: email.value,
        }
    })
    }

    function handleResend() {
    alert('已重新發送驗證信至 ' + email.value)
    }

    // 彈窗送出
    function handleChangeEmail(newEmail) {
    email.value = newEmail
    showChangeDialog.value = false
    alert('新驗證信已發送至 ' + newEmail)
    }
    </script>

    <!-- ChangeEmailDialog.vue 子元件，直接 copy 貼這個 -->
    <!-- 建議你獨立存一份 .vue，但也可直接放到 <script> 下 -->

    <script>
    export default {
    name: 'ChangeEmailDialog',
    props: ['show', 'email'],
    emits: ['close', 'send'],
    data() {
        return { newEmail: this.email }
    },
    watch: {
        email(val) { this.newEmail = val }
    },
    methods: {
        emitClose() { this.$emit('close') },
        emitSend() {
        if (!this.newEmail || !/^[\w.-]+@[\w.-]+\.\w{2,}$/.test(this.newEmail)) {
            alert('請輸入有效的電子郵件')
            return
        }
        this.$emit('send', this.newEmail)
        }
    },
    template: `
        <div class="modal-backdrop">
        <div class="modal-content">
            <button class="modal-close" @click="emitClose">×</button>
            <h3 class="modal-title">更改電子郵件</h3>
            <p class="mb-1">
            我們為您準備的電子郵件地址是
            <span class="main-email">{{ email }}</span>
            。如果您想更改它，請提供新的電子郵件，我們將發送一個新的驗證信件。
            </p>
            <input class="input" v-model="newEmail" type="email" placeholder="請輸入新電子郵件" />
            <button class="btn-submit" @click="emitSend">發送</button>
        </div>
        </div>
    `
    }
    </script>


    <style scoped>
    .verify-bg {
        min-height: 100vh;
        background: #f9f9fa;
    }

    .verify-card {
        background: #fff;
        border-radius: 16px;
        box-shadow: 0 2px 20px 2px rgba(44, 47, 74, 0.07);
        padding: 2.4rem 2.2rem 1.3rem 2.2rem;
        max-width: 400px;
        width: 100%;
        margin: 36px auto;
        text-align: center;
        position: relative;
    }

    .mail-img {
        width: 70px;
        height: 70px;
        margin-bottom: 12px;
    }

    .btn-main {
        background: #f1cd78;
        color: #fff;
        font-weight: bold;
        font-size: 20px;
        height: 48px;
        border-radius: 12px;
        border: none;
        letter-spacing: 2px;
        transition: filter 0.15s;
        box-shadow: 0 2px 8px 1px #f1cd780f;
    }

    .btn-main:hover {
        filter: brightness(1.08);
        background: #ffc94d;
    }

    .resend-link,
    .change-link {
        color: #f1cd78;
        font-weight: bold;
        cursor: pointer;
        text-decoration: underline;
    }

    .text-pink {
        color: #f1cd78;
        font-weight: bold;
    }
    </style>
