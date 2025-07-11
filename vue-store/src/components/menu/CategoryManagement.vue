<script setup>
// 1. 定義 Props & Emits，這是組件的「合約」
defineProps({
    categories: {
        type: Array,
        required: true
    }
});

const emit = defineEmits(['addNewCategory', 'editCategory']);

// 2. 定義事件處理函式
const handleAddNew = () => {
    emit('addNewCategory'); // 向上發出「新增」訊號
};

const handleEdit = (category) => {
    emit('editCategory', category); // 向上發出「編輯」訊號，並附上被點擊的 category 資料
};
</script>

<template>
    <div>
        <!-- 頂部操作區 -->
        <div class="d-flex justify-content-end mb-3">
            <!-- 只有一個「新增」按鈕，所以讓它靠右即可 -->
            <button class="btn btn-warning" @click="handleAddNew">
                + 新增品項類別
            </button>
        </div>

        <!-- 類別列表容器 -->
        <div class="list-group">
            <!-- 使用 v-if 處理沒有資料時的提示 -->
            <div v-if="categories.length === 0" class="list-group-item text-center text-muted">
                目前沒有任何品項類別，請點擊右上角新增。
            </div>

            <!-- 使用 v-for 遍歷傳入的 categories 陣列 -->
            <div 
                v-else
                v-for="category in categories" 
                :key="category.id" 
                class="list-group-item list-group-item-action d-flex align-items-center"
                @click="handleEdit(category)"
                style="cursor: pointer;"
            >
                <!-- 拖曳圖標 -->
                <span class="drag-handle me-3 text-muted">☰</span>
                
                <!-- 類別名稱 -->
                <span class="fw-bold">{{ category.name }}</span>
            </div>
        </div>
    </div>
</template>

<style scoped>
/* 可以為拖曳圖標增加一點樣式，讓它看起來更像可操作的 */
.drag-handle {
    cursor: grab;
}
</style>