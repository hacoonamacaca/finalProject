<template>
    <div v-if="show" class="modal-overlay restaurant-theme" @click="closeModal">
        <div class="modal-content" @click.stop>
            <div class="modal-header">
                <h4 class="modal-title">商品詳情</h4>
                <button class="close-btn" @click="closeModal">×</button>
            </div>

            <div class="modal-body">
                <div class="item-image">
                    <img :src="item.image" :alt="item.name" />
                </div>

                <div class="item-details">
                    <h3 class="item-name">{{ item.name }}</h3>
                    <p class="item-description">{{ item.description }}</p>

                    <div class="price-section">
                        <span v-if="item.originalPrice && item.originalPrice !== item.discountPrice"
                            class="original-price">NT${{ item.originalPrice }}</span>
                        <span class="current-price">NT${{ item.discountPrice || item.price }}</span>
                    </div>

                    <!-- 商品选项 (如果有的话) -->
                    <div v-if="item.options && item.options.length > 0" class="item-options">
                        <h5>選項</h5>
                        <div v-for="option in item.options" :key="option.id" class="option-group">
                            <label class="option-label">{{ option.name }}</label>
                            <div class="option-items">
                                <label v-for="optionItem in option.items" :key="optionItem.id" class="option-item">
                                    <input v-if="option.type === 'radio'" type="radio" :name="option.id"
                                        :value="optionItem.id" v-model="selectedOptions[option.id]" />
                                    <input v-else type="checkbox" :value="optionItem.id"
                                        v-model="selectedOptions[option.id]" />
                                    <span>{{ optionItem.name }}</span>
                                    <span v-if="optionItem.price > 0" class="option-price">
                                        +NT${{ optionItem.price }}
                                    </span>
                                </label>
                            </div>
                        </div>
                    </div>

                    <!-- 数量选择 -->
                    <div class="quantity-section">
                        <label class="quantity-label">數量</label>
                        <div class="quantity-controls">
                            <button class="quantity-btn" @click="decreaseQuantity" :disabled="quantity <= 1">-</button>
                            <span class="quantity-display">{{ quantity }}</span>
                            <button class="quantity-btn" @click="increaseQuantity">+</button>
                        </div>
                    </div>

                    <!-- 备注 -->
                    <div class="notes-section">
                        <label class="notes-label">備註</label>
                        <textarea v-model="notes" placeholder="特殊要求或備註..." class="notes-input" rows="3"></textarea>
                    </div>
                </div>
            </div>

            <div class="modal-footer">
                <div class="total-price">
                    總計：NT${{ totalPrice }}
                </div>
                <button class="add-to-cart-btn" @click="addToCart">
                    加入購物車 ({{ quantity }})
                </button>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import '@/assets/css/restaurant-theme.css'

const props = defineProps({
    item: {
        type: Object,
        required: true
    },
    show: {
        type: Boolean,
        default: false
    }
})

const emit = defineEmits(['close', 'add-to-cart'])

// 组件状态
const quantity = ref(1)
const selectedOptions = ref({})
const notes = ref('')

// 计算总价
const totalPrice = computed(() => {
    let basePrice = props.item.discountPrice || props.item.price
    let optionsPrice = 0

    // 计算选项价格
    if (props.item.options) {
        props.item.options.forEach(option => {
            const selected = selectedOptions.value[option.id]
            if (selected) {
                if (Array.isArray(selected)) {
                    // 多选
                    selected.forEach(optionItemId => {
                        const optionItem = option.items.find(item => item.id === optionItemId)
                        if (optionItem) optionsPrice += optionItem.price || 0
                    })
                } else {
                    // 单选
                    const optionItem = option.items.find(item => item.id === selected)
                    if (optionItem) optionsPrice += optionItem.price || 0
                }
            }
        })
    }

    return (basePrice + optionsPrice) * quantity.value
})

// 方法
const closeModal = () => {
    emit('close')
}

const increaseQuantity = () => {
    quantity.value++
}

const decreaseQuantity = () => {
    if (quantity.value > 1) {
        quantity.value--
    }
}

const addToCart = () => {
    const cartItem = {
        id: props.item.id,
        name: props.item.name,
        price: props.item.discountPrice || props.item.price,
        image: props.item.image,
        quantity: quantity.value,
        selectedOptions: { ...selectedOptions.value },
        notes: notes.value,
        totalPrice: totalPrice.value
    }

    emit('add-to-cart', cartItem)

    // 重置状态
    quantity.value = 1
    selectedOptions.value = {}
    notes.value = ''
}

// 监听显示状态变化，重置表单
watch(() => props.show, (newVal) => {
    if (newVal) {
        quantity.value = 1
        selectedOptions.value = {}
        notes.value = ''

        // 初始化选项
        if (props.item.options) {
            props.item.options.forEach(option => {
                if (option.type === 'checkbox') {
                    selectedOptions.value[option.id] = []
                } else {
                    selectedOptions.value[option.id] = null
                }
            })
        }
    }
})
</script>

<style scoped>
.modal-overlay {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: rgba(0, 0, 0, 0.5);
    display: flex;
    align-items: center;
    justify-content: center;
    z-index: 2000;
    padding: 1rem;
}

.modal-content {
    background: var(--restaurant-bg-primary);
    border: 1px solid var(--restaurant-border-light);
    border-radius: 12px;
    width: 100%;
    max-width: 500px;
    max-height: 90vh;
    overflow-y: auto;
    box-shadow: 0 12px 40px var(--restaurant-shadow-dark);
}

.modal-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 1.5rem;
    border-bottom: 1px solid var(--restaurant-border-light);
    background: var(--restaurant-gradient-light);
}

.modal-title {
    font-size: 1.25rem;
    font-weight: 600;
    color: var(--restaurant-text-primary);
    margin: 0;
}

.close-btn {
    background: none;
    border: none;
    font-size: 2rem;
    color: var(--restaurant-text-secondary);
    cursor: pointer;
    padding: 0;
    width: 2rem;
    height: 2rem;
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: 50%;
    transition: all 0.2s ease;
}

.close-btn:hover {
    color: var(--restaurant-primary);
    background: var(--restaurant-shadow-light);
}

.modal-body {
    padding: 1.5rem;
}

.item-image {
    width: 100%;
    height: 200px;
    border-radius: 8px;
    overflow: hidden;
    margin-bottom: 1.5rem;
}

.item-image img {
    width: 100%;
    height: 100%;
    object-fit: cover;
}

.item-name {
    font-size: 1.5rem;
    font-weight: 600;
    color: var(--restaurant-text-primary);
    margin: 0 0 0.75rem 0;
}

.item-description {
    color: var(--restaurant-text-secondary);
    line-height: 1.5;
    margin: 0 0 1.5rem 0;
}

.price-section {
    display: flex;
    align-items: center;
    gap: 0.75rem;
    margin-bottom: 1.5rem;
    padding: 1rem;
    background: var(--restaurant-bg-light);
    border-radius: 8px;
    border: 1px solid var(--restaurant-border-light);
}

.original-price {
    font-size: 1rem;
    color: var(--restaurant-text-muted);
    text-decoration: line-through;
}

.current-price {
    font-size: 1.25rem;
    font-weight: 600;
    color: var(--restaurant-primary-dark);
}

.item-options {
    margin-bottom: 1.5rem;
}

.item-options h5 {
    font-size: 1.1rem;
    font-weight: 600;
    color: var(--restaurant-text-primary);
    margin: 0 0 1rem 0;
}

.option-group {
    margin-bottom: 1.5rem;
    padding: 1rem;
    background: var(--restaurant-bg-light);
    border-radius: 8px;
    border: 1px solid var(--restaurant-border-light);
}

.option-label {
    display: block;
    font-weight: 500;
    color: var(--restaurant-text-primary);
    margin-bottom: 0.5rem;
}

.option-items {
    display: flex;
    flex-direction: column;
    gap: 0.5rem;
}

.option-item {
    display: flex;
    align-items: center;
    gap: 0.5rem;
    cursor: pointer;
    padding: 0.75rem;
    border-radius: 6px;
    transition: all 0.2s ease;
    background: var(--restaurant-bg-primary);
    border: 1px solid var(--restaurant-border-medium);
}

.option-item:hover {
    background: var(--restaurant-shadow-light);
    border-color: var(--restaurant-primary-light);
}

.option-price {
    margin-left: auto;
    color: var(--restaurant-primary-dark);
    font-weight: 500;
}

.quantity-section,
.notes-section {
    margin-bottom: 1.5rem;
}

.quantity-label,
.notes-label {
    display: block;
    font-weight: 500;
    color: var(--restaurant-text-primary);
    margin-bottom: 0.5rem;
}

.quantity-controls {
    display: flex;
    align-items: center;
    gap: 1rem;
    padding: 1rem;
    background: var(--restaurant-bg-light);
    border-radius: 8px;
    border: 1px solid var(--restaurant-border-light);
}

.quantity-btn {
    width: 2.5rem;
    height: 2.5rem;
    border: 1px solid var(--restaurant-border-medium);
    background: var(--restaurant-bg-primary);
    border-radius: 6px;
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 1.2rem;
    font-weight: 600;
    transition: all 0.3s ease;
    color: var(--restaurant-text-primary);
}

.quantity-btn:hover:not(:disabled) {
    background: var(--restaurant-gradient-primary);
    color: var(--restaurant-text-primary);
    border-color: var(--restaurant-primary);
    transform: translateY(-1px);
    box-shadow: 0 2px 8px var(--restaurant-shadow-light);
}

.quantity-btn:disabled {
    opacity: 0.5;
    cursor: not-allowed;
}

.quantity-display {
    font-size: 1.2rem;
    font-weight: 600;
    min-width: 2rem;
    text-align: center;
    color: var(--restaurant-text-primary);
}

.notes-input {
    width: 100%;
    padding: 0.75rem;
    border: 1px solid var(--restaurant-border-medium);
    background: var(--restaurant-bg-primary);
    border-radius: 6px;
    resize: vertical;
    font-family: inherit;
    color: var(--restaurant-text-primary);
    transition: all 0.2s ease;
}

.notes-input:focus {
    outline: none;
    border-color: var(--restaurant-primary);
    box-shadow: 0 0 0 2px var(--restaurant-shadow-light);
}

.modal-footer {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 1.5rem;
    border-top: 1px solid var(--restaurant-border-light);
    background: var(--restaurant-bg-secondary);
    border-radius: 0 0 12px 12px;
}

.total-price {
    font-size: 1.25rem;
    font-weight: 600;
    color: var(--restaurant-primary-dark);
}

.add-to-cart-btn {
    background: var(--restaurant-gradient-primary);
    color: var(--restaurant-text-primary);
    border: 1px solid var(--restaurant-primary-light);
    padding: 0.75rem 2rem;
    border-radius: 8px;
    font-weight: 600;
    cursor: pointer;
    transition: all 0.3s ease;
    box-shadow: 0 2px 8px var(--restaurant-shadow-light);
}

.add-to-cart-btn:hover {
    background: var(--restaurant-primary-hover);
    transform: translateY(-2px);
    box-shadow: 0 4px 16px var(--restaurant-shadow-medium);
}

/* 响应式设计 */
@media (max-width: 768px) {
    .modal-content {
        margin: 0.5rem;
        max-width: none;
    }

    .modal-header,
    .modal-body,
    .modal-footer {
        padding: 1rem;
    }

    .modal-footer {
        flex-direction: column;
        gap: 1rem;
        align-items: stretch;
    }

    .add-to-cart-btn {
        width: 100%;
    }
}
</style>