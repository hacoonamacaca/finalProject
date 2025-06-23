<template>
    <div class="modal">
        <h2>新增優惠活動</h2>
        <form @submit.prevent="createPromotion">
            <div>
                <label>活動標題：</label>
                <input v-model="form.title" required />
            </div>
            <div>
                <label>優惠內容：</label>
                <input v-model="form.description" />
            </div>
            <div>
                <label>起始時間：</label>
                <input type="datetime-local" v-model="form.start_time" />
            </div>
            <div>
                <label>結束時間：</label>
                <input type="datetime-local" v-model="form.end_time" />
            </div>
            <div>
                <label>折扣類型：</label>
                <input v-model="form.discount_type" />
            </div>
            <div>
                <label>折扣值：</label>
                <input type="number" v-model="form.discount_value" />
            </div>
            <div>
                <label>消費金額門檻：</label>
                <input type="number" v-model="form.min_spend" />
            </div>
            <div>
                <label>優惠碼：</label>
                <input v-model="form.code" />
            </div>
            <div>
                <label>使用上限：</label>
                <input type="number" v-model="form.max_usage" />
            </div>
            <div>
                <label>每人使用上限：</label>
                <input type="number" v-model="form.user_usage_limit" />
            </div>

            <button type="submit">送出</button>
            <button type="button" @click="$emit('close')">取消</button>
        </form>
    </div>
</template>


<script setup>
import { ref } from 'vue'
import axios from 'axios'

const emit = defineEmits(['close', 'created'])

const form = ref({
    title: '',
    description: '',
    start_time: '',
    end_time: '',
    discount_type: '',
    discount_value: 0,
    min_spend: 0,
    code: '',
    max_usage: 0,
    user_usage_limit: 0,
})

const createPromotion = async () => {
    try {
        await axios.post('/api/promotions', form.value)
        emit('created')
    } catch (err) {
        console.error('新增失敗', err)
    }
}
</script>

<style scoped>
.modal {
    background: #fff;
    border: 1px solid #ccc;
    padding: 20px;
}
</style>
