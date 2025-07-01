<template>
    <div class="cart-modal-overlay" @click="closeModal">
        <div class="cart-modal-content" @click.stop>
            <div class="cart-header">
                <h4 class="cart-title">購物車 ({{ totalItems }})</h4>
                <button class="close-btn" @click="closeModal">×</button>
            </div>

            <div class="cart-body">
                <div v-if="cartItems.length === 0" class="empty-cart">
                    <div class="empty-cart-icon">🛒</div>
                    <p>購物車是空的</p>
                    <p class="empty-cart-desc">快去選購美味的商品吧！</p>
                </div>

                <div v-else class="cart-items">
                    <div v-for="item in cartItems" :key="item.id" class="cart-item">
                        <div class="item-image">
                            <img :src="item.image" :alt="item.name" />
                        </div>

                        <div class="item-details">
                            <h5 class="item-name">{{ item.name }}</h5>

                            <!-- 显示选项 -->
                            <div v-if="item.selectedOptions && hasSelectedOptions(item.selectedOptions)"
                                class="item-options">
                                <div v-for="(optionValue, optionId) in item.selectedOptions" :key="optionId">
                                    <span v-if="optionValue && optionValue.length > 0" class="option-text">
                                        {{ formatOptions(optionValue) }}
                                    </span>
                                </div>
                            </div>

                            <!-- 显示备注 -->
                            <div v-if="item.notes" class="item-notes">
                                <span class="notes-label">備註：</span>
                                <span class="notes-text">{{ item.notes }}</span>
                            </div>

                            <div class="item-price">NT${{ item.price }}</div>
                        </div>

                        <div class="item-controls">
                            <div class="quantity-controls">
                                <button class="quantity-btn" @click="updateQuantity(item.id, item.quantity - 1)"
                                    :disabled="item.quantity <= 1">-</button>
                                <span class="quantity-display">{{ item.quantity }}</span>
                                <button class="quantity-btn"
                                    @click="updateQuantity(item.id, item.quantity + 1)">+</button>
                            </div>

                            <button class="remove-btn" @click="removeItem(item.id)" title="移除商品">
                                <i class="pi pi-trash"></i>
                            </button>
                        </div>

                        <div class="item-total">
                            NT${{ item.price * item.quantity }}
                        </div>
                    </div>
                </div>
            </div>

            <div v-if="cartItems.length > 0" class="cart-footer">
                <div class="cart-summary">
                    <div class="summary-row">
                        <span>商品小計</span>
                        <span>NT${{ totalAmount }}</span>
                    </div>
                    <div class="summary-row">
                        <span>外送費</span>
                        <span>NT${{ deliveryFee }}</span>
                    </div>
                    <div class="summary-row total-row">
                        <span>總計</span>
                        <span>NT${{ totalAmount + deliveryFee }}</span>
                    </div>
                </div>

                <div class="cart-actions">
                    <button class="continue-shopping-btn" @click="closeModal">
                        繼續購物
                    </button>
                    <button class="checkout-btn" @click="proceedToCheckout">
                        前往結帳 (NT${{ totalAmount + deliveryFee }})
                    </button>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
    cartItems: {
        type: Array,
        required: true
    },
    totalAmount: {
        type: Number,
        required: true
    }
})

const emit = defineEmits(['close', 'update-quantity', 'remove-item', 'checkout'])

// 计算属性
const totalItems = computed(() => {
    return props.cartItems.reduce((sum, item) => sum + item.quantity, 0)
})

const deliveryFee = computed(() => {
    // 满额免运费逻辑
    return props.totalAmount >= 300 ? 0 : 50
})

// 方法
const closeModal = () => {
    emit('close')
}

const updateQuantity = (itemId, newQuantity) => {
    emit('update-quantity', itemId, newQuantity)
}

const removeItem = (itemId) => {
    emit('remove-item', itemId)
}

const proceedToCheckout = () => {
    emit('checkout')
}

const hasSelectedOptions = (selectedOptions) => {
    if (!selectedOptions) return false
    return Object.values(selectedOptions).some(option => {
        if (Array.isArray(option)) {
            return option.length > 0
        }
        return option !== null && option !== undefined
    })
}

const formatOptions = (optionValue) => {
    // 这里应该根据实际的选项数据来格式化显示
    // 暂时简化处理
    if (Array.isArray(optionValue)) {
        return optionValue.join(', ')
    }
    return optionValue
}
</script>

<style scoped>
.cart-modal-overlay {
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

.cart-modal-content {
    background: #fff;
    border-radius: 12px;
    width: 100%;
    max-width: 600px;
    max-height: 90vh;
    display: flex;
    flex-direction: column;
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3);
}

.cart-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 1.5rem;
    border-bottom: 1px solid #eee;
    flex-shrink: 0;
}

.cart-title {
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

.cart-body {
    flex: 1;
    overflow-y: auto;
    padding: 1rem 1.5rem;
}

.empty-cart {
    text-align: center;
    padding: 3rem 1rem;
    color: var(--text-secondary, #666);
}

.empty-cart-icon {
    font-size: 4rem;
    margin-bottom: 1rem;
}

.empty-cart p {
    margin: 0.5rem 0;
}

.empty-cart-desc {
    font-size: 0.9rem;
    color: var(--text-muted, #999);
}

.cart-items {
    display: flex;
    flex-direction: column;
    gap: 1rem;
}

.cart-item {
    display: grid;
    grid-template-columns: 80px 1fr auto auto;
    gap: 1rem;
    align-items: start;
    padding: 1rem;
    background: #f8f9fa;
    border-radius: 8px;
}

.item-image {
    width: 80px;
    height: 80px;
    border-radius: 6px;
    overflow: hidden;
}

.item-image img {
    width: 100%;
    height: 100%;
    object-fit: cover;
}

.item-details {
    min-width: 0;
}

.item-name {
    font-size: 1rem;
    font-weight: 600;
    color: var(--text-primary, #333);
    margin: 0 0 0.5rem 0;
}

.item-options {
    font-size: 0.85rem;
    color: var(--text-secondary, #666);
    margin-bottom: 0.5rem;
}

.option-text {
    display: block;
    margin-bottom: 0.25rem;
}

.item-notes {
    font-size: 0.85rem;
    color: var(--text-secondary, #666);
    margin-bottom: 0.5rem;
}

.notes-label {
    font-weight: 500;
}

.notes-text {
    font-style: italic;
}

.item-price {
    font-size: 0.9rem;
    color: var(--price-color, #e74c3c);
    font-weight: 500;
}

.item-controls {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 0.5rem;
}

.quantity-controls {
    display: flex;
    align-items: center;
    gap: 0.5rem;
}

.quantity-btn {
    width: 2rem;
    height: 2rem;
    border: 1px solid #ddd;
    background: #fff;
    border-radius: 4px;
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 1rem;
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
    font-size: 1rem;
    font-weight: 600;
    min-width: 1.5rem;
    text-align: center;
}

.remove-btn {
    background: none;
    border: none;
    cursor: pointer;
    padding: 0.25rem;
    border-radius: 4px;
    transition: background-color 0.2s ease;
}

.remove-btn:hover {
    background: #ffebee;
}

.item-total {
    font-size: 1rem;
    font-weight: 600;
    color: var(--text-primary, #333);
    text-align: right;
}

.cart-footer {
    flex-shrink: 0;
    border-top: 1px solid #eee;
    padding: 1.5rem;
    background: #f8f9fa;
    border-radius: 0 0 12px 12px;
}

.cart-summary {
    margin-bottom: 1.5rem;
}

.summary-row {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 0.5rem 0;
    color: var(--text-secondary, #666);
}

.total-row {
    border-top: 1px solid #ddd;
    padding-top: 1rem;
    margin-top: 0.5rem;
    font-weight: 600;
    font-size: 1.1rem;
    color: var(--text-primary, #333);
}

.cart-actions {
    display: flex;
    gap: 1rem;
}

.continue-shopping-btn {
    flex: 1;
    padding: 0.75rem 1rem;
    border: 2px solid var(--primary-color, #ff6c00);
    background: #fff;
    color: var(--primary-color, #ff6c00);
    border-radius: 8px;
    font-weight: 600;
    cursor: pointer;
    transition: all 0.3s ease;
}

.continue-shopping-btn:hover {
    background: var(--primary-color, #ff6c00);
    color: #fff;
}

.checkout-btn {
    flex: 2;
    padding: 0.75rem 1rem;
    background: var(--primary-color, #ff6c00);
    color: #fff;
    border: none;
    border-radius: 8px;
    font-weight: 600;
    cursor: pointer;
    transition: background-color 0.3s ease;
}

.checkout-btn:hover {
    background: var(--primary-hover, #e55a00);
}

/* 响应式设计 */
@media (max-width: 768px) {
    .cart-modal-content {
        margin: 0.5rem;
        max-width: none;
        max-height: 95vh;
    }

    .cart-header,
    .cart-body,
    .cart-footer {
        padding: 1rem;
    }

    .cart-item {
        grid-template-columns: 60px 1fr;
        grid-template-rows: auto auto;
        gap: 0.75rem;
    }

    .item-image {
        width: 60px;
        height: 60px;
    }

    .item-controls {
        grid-column: 1;
        grid-row: 2;
        flex-direction: row;
        justify-content: flex-start;
    }

    .item-total {
        grid-column: 2;
        grid-row: 2;
        text-align: right;
        align-self: end;
    }

    .cart-actions {
        flex-direction: column;
    }
}

@media (max-width: 480px) {
    .cart-actions {
        gap: 0.75rem;
    }

    .continue-shopping-btn,
    .checkout-btn {
        padding: 0.875rem;
    }
}
</style>