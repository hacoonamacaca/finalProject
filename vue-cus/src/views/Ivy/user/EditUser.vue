<template>
    <div class="container py-5">
        <div class="text-center mb-4">
            <h3 class="d-inline-block">我的帳戶</h3>
            <i class="bi bi-info-circle text-secondary ms-2"></i>
        </div>
        <!-- 姓名+手機 -->
        <div class="mx-auto" style="max-width: 360px;">
            <div class="mb-3 text-center">
                <label class="form-label w-100 text-start">姓名</label>
                <input v-model="fullName" type="text" class="form-control rounded-pill" placeholder="請輸入姓名" />
            </div>
            <div class="mb-3 text-center">
                <label class="form-label w-100 text-start">手機號碼</label>
                <input v-model="phone" class="form-control rounded-pill" placeholder="0912345678" />
            </div>
            <div class="mb-2 text-center">
                <label class="form-label w-100 text-start">電子郵件</label>
                <div class="input-group">
                <input type="email" v-model="emailLocal" class="form-control rounded-pill" placeholder="請輸入 email" />
                <button
                v-if="!isEmailVerified"
                class="btn btn-outline-warning ms-2"
                type="button"
                @click="handleResend"
                >
                重新驗證
            </button>
        </div>
    </div>
            <!-- 只有未驗證時才會顯示重新驗證按鈕 -->
            <div v-if="verificationMsg" class="text-center my-2" :style="{ color: verificationMsgColor }">
                {{ verificationMsg }}
            </div>
            <div class="d-flex align-items-center justify-content-center mb-3">
                <i class="bi me-2"
                    :class="isEmailVerified ? 'bi-check-circle-fill text-success' : 'bi-exclamation-circle-fill text-warning'"></i>
                <small class="text-secondary">
                    {{ isEmailVerified ? '已驗證' : '未驗證' }}
                </small>
            </div>
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
import { useRouter, useRoute} from 'vue-router'
import { useUserStore } from '@/stores/user.js'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

function goBack() {
    router.back()
}

const initial = reactive({
    firstName: '',
    lastName: '',
    phone: '',
    email: '',
    isEmailVerified: false
})

const fullName = ref('')
const phone = ref('')
const emailLocal = ref('')
const verificationMsg = ref('')
const verificationMsgColor = ref('')

onMounted(async() => {
    const storedName = localStorage.getItem('userFullName')
    if (storedName) {
        const [first, ...rest] = storedName.trim().split(' ')
        initial.firstName = first
        initial.lastName = rest.join(' ')
        fullName.value = storedName
    }
    const storedEmail = localStorage.getItem('userEmail')
    if (storedEmail) {
        initial.email = storedEmail
        emailLocal.value = storedEmail
    }
    const storedPhone = localStorage.getItem('userPhone')
    if (storedPhone) {
        initial.phone = storedPhone
        phone.value = storedPhone
    }

// 從localStorage恢復email驗證狀態
initial.isEmailVerified = localStorage.getItem('userEmailVerified') === 'true'

//如果網址有token，則自動驗證
const token = route.query.token
if(token){
    try{
        const res = await fetch(`/api/verify-email?token=${token}`)
        if(res.ok){
            initial.isEmailVerified = true
            localStorage.setItem('userEmailVerified', 'true')
            verificationMsg.value = 'Email驗證成功'
            verificationMsgColor.value = 'green'
        }else{
            initial.isEmailVerified = false
            localStorage.setItem('userEmailVerified', 'false')
            verificationMsg.value = 'Email驗證失敗或連結已過期'
            verificationMsgColor.value = 'red'
        }
        }catch(e){
            initial.isEmailVerified = false
            localStorage.setItem('userEmailVerified', 'false')
            verificationMsg.value = '驗證過程發生錯誤'
            verificationMsgColor.value = 'red'
        }
    }
})

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

const isEmailVerified = computed(() =>
    emailLocal.value === initial.email && initial.isEmailVerified
)

async function handleResend() {
    if (!emailLocal.value) {
        verificationMsg.value = '請輸入電子郵件'
        verificationMsgColor.value = 'red'
        return
    }
    try {
        const res = await fetch('/api/send-verify-email', {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: new URLSearchParams({ email: emailLocal.value }),
        })
        const text = await res.text()
        verificationMsg.value = text
        verificationMsgColor.value = res.ok ? 'green' : 'red'
    } catch (err) {
        verificationMsg.value = '寄送失敗，請稍後再試'
        verificationMsgColor.value = 'red'
    }
}

function handleSave() {
    // 姓名+手機
    const [first, ...rest] = fullName.value.trim().split(' ')
    const last = rest.join(' ')
    const dirtyBasic = (
        first !== initial.firstName ||
        last !== initial.lastName ||
        phone.value !== initial.phone
    )
    if (dirtyBasic) {
        Object.assign(initial, {
            firstName: first,
            lastName: last,
            phone: phone.value
        })
        userStore.setFullName(fullName.value.trim())
        localStorage.setItem('userPhone', phone.value)
    }

    // Email
    const dirtyEmail = emailLocal.value !== initial.email
    if (dirtyEmail) {
        initial.email = emailLocal.value
        initial.isEmailVerified = false
        localStorage.setItem('userEmail', emailLocal.value)
        localStorage.setItem('userEmailVerified', 'false')
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