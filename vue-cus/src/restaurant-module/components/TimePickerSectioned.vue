<template>
    <div class="time-picker-sectioned">
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
    color: #666;
    font-size: 0.9rem;
    font-weight: 600;
    margin-bottom: 0.75rem;
    text-transform: uppercase;
    letter-spacing: 0.5px;
}

.time-slot-group {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(80px, 1fr));
    gap: 0.5rem;
}

.time-slot {
    background: #fff;
    border: 2px solid #e9ecef;
    border-radius: 6px;
    padding: 0.75rem 0.5rem;
    cursor: pointer;
    transition: all 0.2s ease;
    font-weight: 500;
    font-size: 0.9rem;
    min-height: 44px;
    display: flex;
    align-items: center;
    justify-content: center;
}

.time-slot:hover:not(.disabled) {
    border-color: #ff6c00;
    background-color: #fff3e0;
    transform: translateY(-1px);
}

.time-slot.selected {
    background-color: #ff6c00;
    border-color: #ff6c00;
    color: white;
    font-weight: 600;
}

.time-slot.disabled {
    background-color: #f8f9fa;
    border-color: #dee2e6;
    color: #adb5bd;
    cursor: not-allowed;
    opacity: 0.6;
}

.time-text {
    font-size: 0.9rem;
    line-height: 1;
}

/* 響應式設計 */
@media (max-width: 768px) {
    .time-slot-group {
        grid-template-columns: repeat(auto-fit, minmax(70px, 1fr));
        gap: 0.4rem;
    }

    .time-slot {
        padding: 0.5rem 0.25rem;
        min-height: 40px;
    }

    .time-text {
        font-size: 0.8rem;
    }
}

@media (max-width: 480px) {
    .time-slot-group {
        grid-template-columns: repeat(3, 1fr);
    }
}
</style>