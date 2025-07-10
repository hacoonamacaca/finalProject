<template>
    <div class="container py-5">
        <div class="text-center mb-4">
            <h3 class="d-inline-block">我的帳戶</h3>
            <i class="text-secondary ms-2"></i>
        </div>
        <!-- 姓名+手機 -->
        <div class="mx-auto" style="max-width: 360px;">
            <div class="mb-3 text-center">
                <label class="form-label w-100 text-start">姓名</label>
                <input v-model="userFullName" type="text" class="form-control rounded-pill" placeholder="請輸入姓名" />
            </div>
            <div class="mb-3 text-center">
                <label class="form-label w-100 text-start">手機號碼</label>
                <input v-model="phone" class="form-control rounded-pill" placeholder="0912345678" />
            </div>
            <div class="mb-2 text-center">
                <label class="form-label w-100 text-start">電子郵件</label>
                <input type="email" v-model="emailLocal" class="form-control rounded-pill" placeholder="請輸入 email" />
            </div>
            <div class="d-flex align-items-center justify-content-center mb-3">
                <i class="bi me-2"
                    :class="isEmailVerified ? 'bi-check-circle-fill text-success' : 'bi-exclamation-circle-fill text-warning'"></i>
                <small class="text-secondary">
                    {{ isEmailVerified ? '已驗證' : '未驗證' }}
                </small>
            </div>
            <!-- 合併一顆儲存按鈕 -->
            <button type="button" class="btn btn-primary rounded-pill px-4 d-block mx-auto mb-2" :disabled="!isDirty"
                @click="handleSave">
                儲存
            </button>
            <button type="button" class="btn btn-primary rounded-pill px-4 d-block mx-auto mt-2 mb-4" @click="goBack">
                返回
            </button>
        </div>
    </div>
</template>

<script setup>
import { ref, computed, onMounted, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user.js'

const router = useRouter()
const userStore = useUserStore()

function goBack() {
    router.push('/')
}

// 改這裡：只用 userFullName
const userFullName = ref('')
const phone = ref('')
const emailLocal = ref('')

const initial = reactive({
    userFullName: '',
    phone: '',
    email: '',
    isEmailVerified: true
})

onMounted(() => {
    initial.userFullName = localStorage.getItem('userFullName') || ''
    userFullName.value = initial.userFullName

    initial.email = localStorage.getItem('userEmail') || ''
    emailLocal.value = initial.email

    initial.phone = localStorage.getItem('userPhone') || ''
    phone.value = initial.phone
})

const isDirty = computed(() => {
    return (
        userFullName.value !== initial.userFullName ||
        phone.value !== initial.phone ||
        emailLocal.value !== initial.email
    )
})

const isEmailVerified = computed(() =>
    emailLocal.value === initial.email && initial.isEmailVerified
)

function handleSave() {
    // 只要有改就存
    if (userFullName.value !== initial.userFullName) {
        initial.userFullName = userFullName.value
        userStore.setFullName(userFullName.value)
        localStorage.setItem('userFullName', userFullName.value)
    }
    if (phone.value !== initial.phone) {
        initial.phone = phone.value
        localStorage.setItem('userPhone', phone.value)
    }
    if (emailLocal.value !== initial.email) {
        initial.email = emailLocal.value
        initial.isEmailVerified = false
        localStorage.setItem('userEmail', emailLocal.value)
    }
}
</script>

<style scoped>
.form-control.rounded-pill {
    border-radius: 50px;
}

.btn.rounded-pill {
    border-radius: 50px;
}

.btn.btn-primary {
    background-color: #ffba20;
    border-color: #ffba20;
    color: #fff;
}

.btn.btn-primary:disabled {
    background-color: #d5d5d5;
    border-color: #d5d5d5;
    color: #fff;
}

.btn.btn-primary:hover:not(:disabled) {
    background-color: #f1cd78;
    border-color: #f1cd78;
}
</style>