<template>
    <div v-if="show" class="modal-overlay" @click="closeModal">
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
    background: #fff;
    border-radius: 12px;
    width: 100%;
    max-width: 500px;
    max-height: 90vh;
    overflow-y: auto;
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3);
}

.modal-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 1.5rem;
    border-bottom: 1px solid #eee;
}

.modal-title {
    font-size: 1.25rem;
    font-weight: 600;
    color: var(--text-primary, #333);
    margin: 0;
}

.close-btn {
    background: none;
    border: none;
    font-size: 2rem;
    color: var(--text-secondary, #666);
    cursor: pointer;
    padding: 0;
    width: 2rem;
    height: 2rem;
    display: flex;
    align-items: center;
    justify-content: center;
}

.close-btn:hover {
    color: var(--text-primary, #333);
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
    color: var(--text-primary, #333);
    margin: 0 0 0.75rem 0;
}

.item-description {
    color: var(--text-secondary, #666);
    line-height: 1.5;
    margin: 0 0 1.5rem 0;
}

.price-section {
    display: flex;
    align-items: center;
    gap: 0.75rem;
    margin-bottom: 1.5rem;
}

.original-price {
    font-size: 1rem;
    color: var(--text-muted, #999);
    text-decoration: line-through;
}

.current-price {
    font-size: 1.25rem;
    font-weight: 600;
    color: var(--price-color, #e74c3c);
}

.item-options {
    margin-bottom: 1.5rem;
}

.item-options h5 {
    font-size: 1.1rem;
    font-weight: 600;
    color: var(--text-primary, #333);
    margin: 0 0 1rem 0;
}

.option-group {
    margin-bottom: 1.5rem;
}

.option-label {
    display: block;
    font-weight: 500;
    color: var(--text-primary, #333);
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
    padding: 0.5rem;
    border-radius: 6px;
    transition: background-color 0.2s ease;
}

.option-item:hover {
    background: #f8f9fa;
}

.option-price {
    margin-left: auto;
    color: var(--price-color, #e74c3c);
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
    color: var(--text-primary, #333);
    margin-bottom: 0.5rem;
}

.quantity-controls {
    display: flex;
    align-items: center;
    gap: 1rem;
}

.quantity-btn {
    width: 2.5rem;
    height: 2.5rem;
    border: 1px solid #ddd;
    background: #fff;
    border-radius: 6px;
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 1.2rem;
    font-weight: 600;
    transition: all 0.2s ease;
}

.quantity-btn:hover:not(:disabled) {
    background: var(--primary-color, #ff6c00);
    color: #fff;
    border-color: var(--primary-color, #ff6c00);
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
}

.notes-input {
    width: 100%;
    padding: 0.75rem;
    border: 1px solid #ddd;
    border-radius: 6px;
    resize: vertical;
    font-family: inherit;
}

.notes-input:focus {
    outline: none;
    border-color: var(--primary-color, #ff6c00);
}

.modal-footer {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 1.5rem;
    border-top: 1px solid #eee;
    background: #f8f9fa;
    border-radius: 0 0 12px 12px;
}

.total-price {
    font-size: 1.25rem;
    font-weight: 600;
    color: var(--text-primary, #333);
}

.add-to-cart-btn {
    background: var(--primary-color, #ff6c00);
    color: #fff;
    border: none;
    padding: 0.75rem 2rem;
    border-radius: 8px;
    font-weight: 600;
    cursor: pointer;
    transition: background-color 0.3s ease;
}

.add-to-cart-btn:hover {
    background: var(--primary-hover, #e55a00);
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