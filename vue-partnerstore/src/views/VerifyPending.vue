    <template>
        <div class="d-flex flex-column align-items-center justify-content-center" style="min-height: 80vh;">
            <h2 class="mb-4">註冊資料待審核</h2>
            <div class="mb-3 text-secondary">
                嗨，感謝您的註冊待審核後通知
            </div>
            <hr class="w-100" />
            <button class="btn btn-main my-4 px-5" @click="handleVerify">
                返回首頁
            </button>
            <!-- <div class="mt-5 text-end w-100" style="max-width: 350px;">
            謝謝，<br>
            <span style="color: #ffba20; font-weight: bold;">您的 foodpanda 團隊</span>
        </div> -->
        </div>
    </template>

<script setup>
import { useRouter, useRoute } from 'vue-router'
const router = useRouter()
const route = useRoute()

// email 建議用 query 傳過來
const email = route.query.email || ''

function handleVerify() {
    // 這裡可以呼叫 API 查驗證狀態，成功就導頁
    router.push({
        path: '/',
        query: { email }
    })
}
</script>

<style scoped>
.btn-main {
    background: #ffba20;
    color: #fff;
    font-weight: bold;
    font-size: 20px;
    height: 48px;
    border-radius: 24px;
    border: none;
    letter-spacing: 2px;
    transition: filter 0.15s;
    box-shadow: 0 2px 8px 1px #ffba200f;
}

.btn-main:hover {
    filter: brightness(1.08);
    background: #ffba20;
}
</style>
