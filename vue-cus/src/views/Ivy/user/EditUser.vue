<template>
    <div class="container py-5">
        <div class="text-center mb-4">
            <h3 class="d-inline-block">我的帳戶</h3>
            <i class="bi bi-info-circle text-secondary ms-2"></i>
        </div>
        <div class="mx-auto" style="max-width: 360px;">
            <!-- 姓名 -->
            <div class="mb-3 text-center">
                <label class="form-label w-100 text-start">姓名</label>
                <input v-model="fullName" type="text" class="form-control rounded-pill" placeholder="請輸入姓名" />
            </div>
            <!-- 手機號碼 -->
            <div class="mb-3 text-center">
                <label class="form-label w-100 text-start">手機號碼</label>
                <input v-model="phone" class="form-control rounded-pill" placeholder="0912345678" />
            </div>
            <!-- 電子郵件 + 驗證 -->
            <div class="mb-2 text-center">
                <label class="form-label w-100 text-start">電子郵件</label>
                <div class="input-group align-items-center">
                    <input
                        type="email"
                        v-model="emailLocal"
                        @change="onEmailChange"
                        :disabled="emailChecking"
                        class="form-control rounded-pill"
                        placeholder="請輸入 email"
                    />
                    <button
                        v-if="!isVerified && emailLocal"
                        class="btn btn-outline-warning ms-2"
                        type="button"
                        @click="handleResend"
                        :disabled="emailChecking || resendLoading"
                    >
                        {{ resendLoading ? "發送中..." : "重新驗證" }}
                    </button>
                </div>
            </div>
            <div class="d-flex align-items-center justify-content-center mb-3">
                <i class="bi me-2"
                    :class="isVerified ? 'bi-check-circle-fill text-success' : 'bi-exclamation-circle-fill text-warning'"></i>
                <small class="text-secondary">
                    {{ isVerified ? '已驗證' : '未驗證' }}
                    <span v-if="emailChecking" class="ms-2 text-primary">檢查中...</span>
                </small>
            </div>
            <button
                type="button"
                class="btn btn-primary rounded-pill px-4 d-block mx-auto mb-2"
                :disabled="!isDirty || saveLoading"
                @click="handleSave"
            >
                {{ saveLoading ? "儲存中..." : "儲存" }}
            </button>
            <button
                type="button"
                class="btn btn-primary rounded-pill px-4 d-block mx-auto mt-2 mb-4"
                @click="goBack"
            >
                返回
            </button>
        </div>
    </div>
</template>

<script setup>
import { ref, computed, onMounted, reactive } from 'vue'
import { useRouter } from 'vue-router'
import axios from '@/plungins/axios.js'

const router = useRouter()
function goBack() {
    router.back()
}

const initial = reactive({
    firstName: '',
    lastName: '',
    phone: '',
    email: '',
    isVerified: false
})

const fullName = ref('')
const phone = ref('')
const emailLocal = ref('')
const isVerified = ref(false)
const userId = ref(null)

// loading狀態
const emailChecking = ref(false)
const saveLoading = ref(false)
const resendLoading = ref(false)

onMounted(async () => {
    // 從 localStorage 拿到 email（作為查詢 key）
    const storedEmail = localStorage.getItem('userEmail')
    if (storedEmail) {
        try {
            // 直接用 email 向後端查詢完整會員資料
            const res = await axios.get('/api/users/profile', {
                params: { email: storedEmail }
            })
            const user = res.data
            if (user) {
                //存userId
                userId.value = user.id
                // 1. 同步畫面顯示
                phone.value = user.phone || ''
                fullName.value = user.name || ''
                emailLocal.value = user.email || ''
                isVerified.value = user.isVerify || false

                // 2. 同步 initial 參考值
                initial.phone = user.phone || ''
                initial.firstName = user.name?.split(' ')[0] || ''
                initial.lastName = user.name?.split(' ').slice(1).join(' ') || ''
                initial.email = user.email || ''
                initial.isVerified = user.isVerify || false

                // 3. 也同步 localStorage（讓你原本其它流程還能用）
                localStorage.setItem('userFullName', user.name || '')
                localStorage.setItem('userPhone', user.phone || '')
                localStorage.setItem('userEmail', user.email || '')
                localStorage.setItem('userEmailVerified', user.isVerify ? 'true' : 'false')
                localStorage.setItem('userId', user.id)
            }
        } catch {
            // 查無會員，全部清空
            phone.value = ''
            fullName.value = ''
            emailLocal.value = storedEmail
            isVerified.value = false

            initial.phone = ''
            initial.firstName = ''
            initial.lastName = ''
            initial.email = storedEmail
            initial.isVerified = false

            // localStorage 也同步清空
            localStorage.setItem('userFullName', '')
            localStorage.setItem('userPhone', '')
            localStorage.setItem('userEmailVerified', 'false')
        }
    }
})

// Email異動時自動檢查
async function onEmailChange() {
    const email = emailLocal.value?.trim()
    if (!email) {
        isVerified.value = false
        return
    }
    emailChecking.value = true
    try {
        const res = await axios.get('/api/users/profile', { params: { email } })
        const user = res.data
        if (user) {
            isVerified.value = !!user.isVerify
        } else {
            isVerified.value = false
        }
    } catch {
        isVerified.value = false
    } finally {
        emailChecking.value = false
    }
}

const isDirty = computed(() => {
    const [first, ...rest] = fullName.value.trim().split(' ')
    const last = rest.join(' ')
    const dirtyBasic = (
        first !== initial.firstName ||
        last !== initial.lastName ||
        phone.value !== initial.phone
    )
    const dirtyEmail = emailLocal.value !== initial.email
    return dirtyBasic || dirtyEmail
})

// 儲存
async function handleSave() {
    const [first, ...rest] = fullName.value.trim().split(' ')
    const last = rest.join(' ')
    if (!userId.value) {
        alert('找不到會員ID 無法更新')
        return
    }
    saveLoading.value = true
    try {
        const updateUser = {
            id: userId.value,
            name: fullName.value.trim(),
            phone: phone.value,
            email: emailLocal.value
        }
        const res = await axios.put(`/api/users/${userId.value}`, updateUser)
        const user = res.data
        fullName.value = user.name || ''
        phone.value = user.phone || ''
        emailLocal.value = user.email || ''
        // 同步 initial
        initial.firstName = user.name?.split(' ')[0] || ''
        initial.lastName = user.name?.split(' ').slice(1).join(' ') || ''
        initial.phone = user.phone || ''
        initial.email = user.email || ''
        // 不同步驗證狀態（保持 onEmailChange 統一來源）
        localStorage.setItem('userFullName', fullName.value.trim())
        localStorage.setItem('userPhone', phone.value)
        localStorage.setItem('userEmail', emailLocal.value)
        alert('更新成功')
    } catch {
        alert('更新失敗')
    } finally {
        saveLoading.value = false
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