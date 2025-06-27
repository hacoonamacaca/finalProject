<template>
    <div>
    <!-- 搜尋框和新增按鈕 -->
        <div class="d-flex justify-content-between align-items-center mb-3">
            <div class="input-group" style="max-width: 400px;">
                <span class="input-group-text">🔍</span>
                <input type="text" class="form-control" placeholder="搜尋品項">
            </div>
            <button class="btn btn-warning" @click="handleAddNew">+ 新增</button>
        </div>

        <div class="list-group">
            <!-- 點擊整列觸發編輯 -->
            <div v-for="item in items" :key="item.id"
                class="list-group-item list-group-item-action d-flex justify-content-between align-items-center"
                @click="handleEdit(item)" style="cursor: pointer;">
                <div class="d-flex align-items-center">
                    <span class="me-3">☰</span>
                    <img :src="item.img || 'https://via.placeholder.com/60x60'" alt="item image" width="60" height="60"
                        class="me-3">
                    <span>{{ item.name }}</span>
                </div>
                <div class="d-flex align-items-center">
                    <span class="me-4">NT$ {{ item.price }}</span>

                    <!-- 將 dropdown 相關的邏輯用一個 div 包起來 -->
                    <div class="position-relative dropdown-container">
                        <!-- 點擊按鈕時阻止事件冒泡到父層的 handleEdit，並觸發我們自己的 toggleDropdown -->
                        <button class="btn btn-sm dropdown-toggle"
                            :class="item.status === '供應中' ? 'btn-outline-success' : 'btn-outline-secondary'"
                            type="button" @click.stop="toggleDropdown(item.id)">
                            {{ item.status }}
                        </button>
                        <!-- 使用 v-if 根據 activeDropdown 的狀態來決定是否顯示下拉選單 -->
                        <ul class="dropdown-menu" :class="{ show: activeDropdown === item.id }" style="z-index: 10;">
                            <!-- 點擊選項時也要阻止冒泡 -->
                            <li><a class="dropdown-item" href="#" @click.stop>供應中</a></li>
                            <li><a class="dropdown-item" href="#" @click.stop>暫停供應</a></li>
                        </ul>
                    </div>

                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref } from 'vue';

defineProps({
    items: {
        type: Array,
        required: true
    }
});

const emit = defineEmits(['addNewItem', 'editItem']);

// 為每個下拉選單獨立控制其開關狀態
const activeDropdown = ref(null); // 用來儲存當前打開的下拉選單的 item id

const toggleDropdown = (itemId) => {
    if (activeDropdown.value === itemId) {
        activeDropdown.value = null; // 如果再次點擊已打開的，就關閉它
    } else {
        activeDropdown.value = itemId; // 否則打開新的
    }
};

// 點擊品項列時，要確保不會因為點到下拉選單而關閉它
const handleEdit = (item) => {
    emit('editItem', item);
};

// 點擊新增按鈕
const handleAddNew = () => {
    emit('addNewItem');
}

// 點擊頁面其他地方時，關閉所有下拉選單
document.addEventListener('click', (e) => {
    // 檢查點擊的目標是否在 dropdown 內部，如果不是，則關閉
    if (!e.target.closest('.dropdown-container')) {
        activeDropdown.value = null;
    }
})

</script>