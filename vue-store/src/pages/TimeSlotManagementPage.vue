<template>
    <div class="time-slot-management-page">
        <div class="container-fluid">
            <div class="row">
                <div class="col-12">
                    <div class="d-flex justify-content-between align-items-center mb-4">
                        <h4 class="mb-0">
                            <i class="fas fa-calendar-alt me-2"></i>
                            時段管理
                        </h4>
                        <div class="d-flex gap-2">
                            <button class="btn btn-outline-secondary" @click="$router.go(-1)">
                                <i class="fas fa-arrow-left me-2"></i>
                                返回
                            </button>
                        </div>
                    </div>
                </div>
            </div>

            <div class="row">
                <div class="col-12">
                    <TimeSlotManagement :store-id="selectedStore" />
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import { computed } from 'vue';
import { useStore } from '@/composables/useStore.js';
import TimeSlotManagement from '../components/TimeSlotManagement.vue';

// 使用 store composable
const { selectedStore } = useStore();
</script>

<style scoped>
.time-slot-management-page {
    padding: 20px;
    background-color: #f8f9fa;
    min-height: 100vh;
}
</style>