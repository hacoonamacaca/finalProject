<template>
    <div class="time-picker-sectioned restaurant-theme">
        <div v-for="section in sections" :key="section.label" class="time-section">
            <h6 class="section-label">{{ section.label }}</h6>
            <div class="time-slot-group">
                <button v-for="slot in section.slots" :key="slot" :class="timeSlotClasses(slot)"
                    @click="selectTime(slot)" :disabled="isDisabled(slot)">
                    <span class="time-text">{{ slot }}</span>
                </button>
            </div>
        </div>
    </div>
</template>

<script setup>
import { computed } from 'vue'
import '@/assets/css/restaurant-theme.css'

const props = defineProps({
    modelValue: {
        type: String,
        default: ''
    },
    sections: {
        type: Array,
        default: () => []
    },
    disabledSlots: {
        type: Array,
        default: () => []
    }
})

const emit = defineEmits(['update:modelValue'])

const selectTime = (slot) => {
    if (!isDisabled(slot)) {
        emit('update:modelValue', slot)
    }
}

const isDisabled = (slot) => {
    return props.disabledSlots.includes(slot)
}

const timeSlotClasses = (slot) => {
    return [
        'time-slot',
        {
            'selected': props.modelValue === slot,
            'disabled': isDisabled(slot)
        }
    ]
}
</script>

<style scoped>
.time-picker-sectioned {
    margin: 1rem 0;
}

.time-section {
    margin-bottom: 1.5rem;
}

.section-label {
    color: var(--restaurant-primary);
    font-size: 0.9rem;
    font-weight: 600;
    margin-bottom: 0.75rem;
    text-transform: uppercase;
    letter-spacing: 0.5px;
    text-shadow: 0 1px 2px var(--restaurant-shadow-light);
}

.time-slot-group {
    display: grid;
    grid-template-columns: repeat(4, 120px);
    gap: 0.75rem;
    justify-content: start;
}

.time-slot {
    background: var(--restaurant-bg-primary);
    border: 2px solid var(--restaurant-border-medium);
    border-radius: 8px;
    padding: 0.75rem 0.5rem;
    cursor: pointer;
    transition: all 0.3s ease;
    font-weight: 500;
    font-size: 0.9rem;
    width: 120px;
    height: 48px;
    display: flex;
    align-items: center;
    justify-content: center;
    color: var(--restaurant-text-primary);
    box-shadow: 0 1px 4px var(--restaurant-shadow-light);
}

.time-slot:hover:not(.disabled) {
    border-color: var(--restaurant-primary);
    background: var(--restaurant-shadow-light);
    transform: translateY(-2px);
    box-shadow: 0 4px 12px var(--restaurant-shadow-medium);
}

.time-slot.selected {
    background: var(--restaurant-gradient-primary);
    border-color: var(--restaurant-primary);
    color: var(--restaurant-text-primary);
    font-weight: 600;
    box-shadow: 0 4px 16px var(--restaurant-shadow-medium);
}

.time-slot.disabled {
    background: var(--restaurant-bg-light);
    border-color: var(--restaurant-border-light);
    color: var(--restaurant-text-muted);
    cursor: not-allowed;
    opacity: 0.6;
}

.time-text {
    font-size: 0.9rem;
    line-height: 1;
}

/* 響應式設計 - 保持固定大小 */
@media (max-width: 768px) {
    .time-slot-group {
        grid-template-columns: repeat(3, 110px);
        gap: 0.5rem;
        justify-content: start;
    }

    .time-slot {
        width: 110px;
        height: 44px;
        padding: 0.5rem 0.25rem;
    }

    .time-text {
        font-size: 0.8rem;
    }
}

@media (max-width: 480px) {
    .time-slot-group {
        grid-template-columns: repeat(2, 140px);
        gap: 0.5rem;
        justify-content: center;
    }

    .time-slot {
        width: 140px;
        height: 44px;
    }
}
</style>