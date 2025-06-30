<script setup>
import { ref } from 'vue';

// 1. 定義接口 (Props & Emits)，這是組件的合約
defineProps({
    specs: {
        type: Array,
        required: true
    }
});
const emit = defineEmits(['addNewSpec', 'editSpec']);

// 2. 組件內部狀態 (Local State)
const activeDropdown = ref(null);

// 3. 組件方法 (Methods)，處理用戶交互
const handleAddNew = () => emit('addNewSpec');
const handleEdit = (spec) => emit('editSpec', spec);

const toggleDropdown = (specId) => {
    activeDropdown.value = activeDropdown.value === specId ? null : specId;
};

// 4. 副作用 (Side Effects)，處理與外部的交互
//    (這段和 MenuOverview.vue 重複了，未來可以抽成共用的 Hook)
document.addEventListener('click', (e) => {
    if (!e.target.closest('.dropdown-container')) {
        activeDropdown.value = null;
    }
});
</script>

<template>
    <div>
        <!-- Header Section -->
        <div class="d-flex justify-content-between align-items-center mb-3">
            <div class="input-group" style="max-width: 400px;">
                <span class="input-group-text">🔍</span>
                <input type="text" class="form-control" placeholder="搜尋規格">
            </div>
            <button class="btn btn-warning" @click="handleAddNew">+ 新增規格</button>
        </div>
    
        <!-- Specs List -->
        <div class="list-group">
            <div 
                v-for="spec in specs" 
                :key="spec.id" 
                class="list-group-item list-group-item-action d-flex justify-content-between align-items-center"
            >
                <!-- 左側：規格名稱和選項預覽 -->
                <!-- 點擊這個區域觸發編輯 -->
                <div @click="handleEdit(spec)" class="flex-grow-1 me-3" style="cursor: pointer;">
                    <p class="fw-bold mb-0">{{ spec.name }}</p>
                    <small class="text-muted text-truncate d-block" style="max-width: 400px;">{{ spec.options }}</small>
                </div>

                <!-- 右側：狀態控制 -->
                <div class="position-relative dropdown-container flex-shrink-0">
                    <button 
                        class="btn btn-sm dropdown-toggle"
                        :class="spec.status === '供應中' ? 'btn-outline-success' : 'btn-outline-secondary'"
                        type="button" 
                        @click.stop="toggleDropdown(spec.id)"
                    >
                        {{ spec.status }}
                    </button>
                    <ul class="dropdown-menu dropdown-menu-end" :class="{ show: activeDropdown === spec.id }">
                        <li><a class="dropdown-item" href="#" @click.stop>供應中</a></li>
                        <li><a class="dropdown-item" href="#" @click.stop>暫停供應</a></li>
                    </ul>
                </div>
            </div>
        </div>
    </div>
</template>