<script setup>
import { ref, watchEffect } from 'vue';

// 1. 定義 Props：接收要編輯的 category 物件
const props = defineProps({
    category: {
        type: Object,
        default: null, // 新增時為 null
    }
});

// 2. 定義 Emits：聲明會發出的事件
const emit = defineEmits(['close', 'save', 'delete']);

// 3. 本地表單狀態
const form = ref({});

// 4. 使用 watchEffect 初始化表單
watchEffect(() => {
    if (props.category) {
        // 編輯模式：複製傳入的資料
        form.value = { ...props.category };
    } else {
        // 新增模式：設定預設空值
        form.value = {
            name: '',
            description: '',
            sort: 0 // 排序預設為 0
        };
    }
});

// 5. 事件處理函式
const handleSave = () => {
    // 簡單的前端驗證
    if (!form.value.name) {
        alert('請輸入品項類別名稱！');
        return;
    }
    emit('save', form.value);
};

const handleDelete = () => {
    if (props.category?.id) {
        emit('delete', props.category.id);
    }
};

const handleClose = () => {
    emit('close');
};
</script>

<template>
    <div>
        <!-- 使用 form 標籤，並監聽 submit 事件 -->
        <form @submit.prevent="handleSave">
        <!-- 類別名稱 -->
        <div class="mb-3">
            <label for="categoryName" class="form-label">類別名稱*</label>
            <input 
            type="text" 
            id="categoryName" 
            class="form-control" 
            v-model.trim="form.name" 
            required
            >
        </div>

        <!-- 描述 -->
        <div class="mb-3">
            <label for="categoryDescription" class="form-label">描述</label>
            <textarea 
            id="categoryDescription" 
            class="form-control" 
            rows="3" 
            v-model="form.description"
            ></textarea>
        </div>
        
        <!-- 排序 -->
        <div class="mb-3">
            <label for="categorySort" class="form-label">排序</label>
            <input 
            type="number" 
            id="categorySort" 
            class="form-control" 
            v-model.number="form.sort" 
            min="0"
            >
            <div class="form-text">數字越小，排序越前面。</div>
        </div>

        <!-- 分隔線 -->
        <hr class="my-4">

        <!-- 底部按鈕區 -->
        <div class="d-flex justify-content-between">
            <!-- 刪除按鈕 (只有編輯模式下顯示) -->
            <button 
            v-if="props.category" 
            type="button" 
            class="btn btn-outline-danger" 
            @click="handleDelete"
            >
            刪除類別
            </button>
            <div v-else></div> <!-- 佔位符 -->

            <!-- 取消和儲存按鈕 -->
            <div class="d-flex gap-2">
            <button 
                type="button" 
                class="btn btn-secondary" 
                @click="handleClose"
            >
                取消
            </button>
            <button 
                type="submit" 
                class="btn btn-primary"
            >
                確定提交
            </button>
            </div>
        </div>
        </form>
    </div>
</template>