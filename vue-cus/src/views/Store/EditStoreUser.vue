<template>
    <div class="container py-5">
        <div class="text-center mb-4">
            <h3 class="d-inline-block">我的帳戶</h3>
        </div>
        <div class="mx-auto" style="max-width: 360px;">
            <div class="mb-3 text-center">
                <label class="form-label w-100 text-start">姓名</label>
                <input v-model="storeFullName" type="text" class="form-control rounded-pill" placeholder="請輸入姓名" />
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
import { computed, onMounted, reactive } from 'vue'
import { useRouter } from 'vue-router'
import axios from '@/plungins/axios.js'
import { useUserStore } from '@/stores/user.js'

const router = useRouter()
const userStore = useUserStore()

const storeFullName = computed({
    get: () => userStore.ownerFullName,
    set: val => userStore.setOwnerFullName(val)
})
const phone = computed({
    get: () => userStore.phone,
    set: val => userStore.setPhone(val)
})
const emailLocal = computed({
    get: () => userStore.ownerEmail,
    set: val => userStore.setOwnerEmail(val)
})

const initial = reactive({
    storeFullName: '',
    phone: '',
    email: '',
    isEmailVerified: false // 預設為未驗證
})

// 返回首頁
function goBack() {
    router.push('/store')
}

// onMounted 取得帳戶資料時，同步 Pinia 狀態
onMounted(async () => {
    const ownerId = localStorage.getItem('ownerId')
    if (!ownerId) {
        alert('未取得業者ID，請重新登入')
        return
    }
    try {
        const res = await axios.get(`/api/owner/${ownerId}`)
        const data = res.data.owner || res.data
        initial.storeFullName = data.name
        initial.phone = data.phone
        initial.email = data.email
        initial.isEmailVerified = data.isVerify

        // 直接用 pinia setter（computed 綁定會自動寫回表單）
        userStore.setOwnerFullName(data.name)
        userStore.setOwnerEmail(data.email)
        userStore.setPhone(data.phone)
        userStore.setOwnerId(ownerId + '')
    } catch (e) {
        alert('取得帳戶資料失敗')
        console.error(e)
    }
})

const isDirty = computed(() => (
    storeFullName.value !== initial.storeFullName ||
    phone.value !== initial.phone ||
    emailLocal.value !== initial.email
))

const isEmailVerified = computed(() =>
    emailLocal.value === initial.email && !!initial.isEmailVerified
)

async function handleSave() {
    const ownerId = localStorage.getItem('ownerId')
    if (!ownerId) {
        alert('未取得業者ID，請重新登入')
        return
    }
    try {
        const res = await axios.put(`/api/owner/${ownerId}`, {
            name: storeFullName.value,
            phone: phone.value,
            email: emailLocal.value
        })
        if (res.data.success) {
            userStore.setOwnerFullName(storeFullName.value)
            userStore.setOwnerEmail(emailLocal.value)
            userStore.setPhone(phone.value)
            userStore.setOwnerId(ownerId + '')
            initial.storeFullName = storeFullName.value
            initial.phone = phone.value
            initial.email = emailLocal.value
            alert('儲存成功')
        } else {
            alert(res.data.message || '儲存失敗')
        }
    } catch (e) {
        alert('儲存失敗，請稍後再試')
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