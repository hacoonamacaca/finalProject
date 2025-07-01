<template>
    <div class="restaurant-menu">
        <div class="menu-container">
            <h3 class="menu-title">{{ restaurant.menuTitle || '餐廳菜單' }}</h3>

            <div class="menu-tabs" v-if="menuCategories && menuCategories.length > 1">
                <button v-for="(category, index) in menuCategories" :key="index"
                    :class="['menu-tab', { active: activeTab === index }]" @click="activeTab = index">
                    {{ category.name }}
                </button>
            </div>

            <div class="menu-content">
                <div class="menu-scroll-container" v-if="currentMenuItems.length > 0">
                    <div class="menu-grid">
                        <div class="menu-item" v-for="item in currentMenuItems" :key="item.id"
                            @click="openItemDetail(item)">
                            <div class="item-image">
                                <img :src="item.image || restaurant.image" :alt="item.name" />
                            </div>
                            <div class="item-content">
                                <div class="item-info">
                                    <h5 class="item-name">{{ item.name }}</h5>
                                    <p class="item-desc">{{ item.description }}</p>
                                    <div class="price-section">
                                        <span v-if="item.originalPrice && item.originalPrice !== item.discountPrice"
                                            class="original-price">NT${{ item.originalPrice }}</span>
                                        <span class="current-price">NT${{ item.discountPrice || item.price }}</span>
                                    </div>
                                </div>
                                <div class="item-actions">
                                    <span class="pi pi-cart-plus add-to-cart-btn" @click.stop="quickAddToCart(item)"
                                        title="加入購物車"></span>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <div v-else class="no-menu">
                    <p>暫無菜品資訊</p>
                </div>
            </div>
        </div>

        <div class="cart-float-btn" v-if="totalCartQuantity > 0" @click="toggleCartVisibility">
            <div class="cart-icon">
                <i class="pi pi-shopping-cart"></i>
                <span class="cart-badge">{{ totalCartQuantity }}</span>
            </div>
            <div class="cart-total">NT${{ totalCartAmount }}</div>
        </div>

        <ItemDetailModal v-if="showItemDetail" :item="selectedItem" :show="showItemDetail" @close="closeItemDetail"
            @add-to-cart="handleAddToCart" />

        <CartModal v-if="isCartVisible" :cart-items="cartItems" :total-amount="totalCartAmount"
            @close="toggleCartVisibility" @update-quantity="updateCartItemQuantity" @remove-item="removeCartItem"
            @checkout="checkout" />
    </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import ItemDetailModal from './ItemDetailModal.vue'
import CartModal from './CartModal.vue'


const props = defineProps({
    restaurant: {
        type: Object,
        required: true
    }
})

const activeTab = ref(0)
const selectedItem = ref(null)
const showItemDetail = ref(false)
const cartItems = ref([])
const isCartVisible = ref(false)

const menuCategories = computed(() => {
    if (props.restaurant.menuCategories && props.restaurant.menuCategories.length > 0) {
        return props.restaurant.menuCategories
    }
    if (props.restaurant.menuItems && props.restaurant.menuItems.length > 0) {
        return [{ name: '全部菜品', items: props.restaurant.menuItems }]
    }
    return []
})

const currentMenuItems = computed(() => {
    if (menuCategories.value.length === 0) return []

    const currentCategory = menuCategories.value[activeTab.value]
    return currentCategory.items || []
})

const totalCartQuantity = computed(() => {
    return cartItems.value.reduce((sum, item) => sum + item.quantity, 0)
})

const totalCartAmount = computed(() => {
    return cartItems.value.reduce((sum, item) => sum + (item.price * item.quantity), 0)
})

const openItemDetail = (item) => {
    selectedItem.value = item
    showItemDetail.value = true
}

const closeItemDetail = () => {
    showItemDetail.value = false
    selectedItem.value = null
}

const quickAddToCart = (item) => {
    const cartItem = {
        id: item.id,
        name: item.name,
        price: item.discountPrice || item.price,
        image: item.image,
        quantity: 1
    }
    handleAddToCart(cartItem)
}

const handleAddToCart = (itemToAdd) => {
    const existingItemIndex = cartItems.value.findIndex(item => item.id === itemToAdd.id)

    if (existingItemIndex > -1) {
        cartItems.value[existingItemIndex].quantity += itemToAdd.quantity
    } else {
        cartItems.value.push({ ...itemToAdd })
    }

    if (showItemDetail.value) {
        closeItemDetail()
    }

    setTimeout(() => {
        isCartVisible.value = true
    }, 300)
}

const updateCartItemQuantity = (itemId, newQuantity) => {
    if (newQuantity <= 0) {
        removeCartItem(itemId)
        return
    }

    const itemIndex = cartItems.value.findIndex(item => item.id === itemId)
    if (itemIndex > -1) {
        cartItems.value[itemIndex].quantity = newQuantity
    }
}

const removeCartItem = (itemId) => {
    cartItems.value = cartItems.value.filter(item => item.id !== itemId)
}

const toggleCartVisibility = () => {
    isCartVisible.value = !isCartVisible.value
}

const checkout = () => {
    const orderData = {
        restaurant: {
            id: props.restaurant.id,
            name: props.restaurant.name
        },
        items: cartItems.value,
        totalAmount: totalCartAmount.value,
        orderTime: new Date().toISOString()
    }

    alert(`訂單總計：NT$${totalCartAmount.value}\n正在前往結帳...`)

    cartItems.value = []
    isCartVisible.value = false
}

onMounted(() => {
    if (!props.restaurant.menuItems && !props.restaurant.menuCategories) {
        console.warn('RestaurantMenu: 未提供菜单数据，请在 restaurant 对象中包含 menuItems 或 menuCategories')
    }
})
</script>

<style scoped>
.restaurant-menu {
    margin: 3rem 0;
    padding: 0 1rem;
    position: relative;
}

.menu-container {
    width: 100%;
    margin: 0 auto;
    max-width: 1200px;
}

.menu-title {
    font-size: 2rem;
    font-weight: 600;
    color: var(--text-primary, #333);
    margin-bottom: 2rem;
    text-align: center;
}

.menu-tabs {
    display: flex;
    justify-content: center;
    gap: 1rem;
    margin-bottom: 2rem;
    flex-wrap: wrap;
}

.menu-tab {
    padding: 0.75rem 1.5rem;
    border: 2px solid var(--primary-color, #ff6c00);
    background: #fff;
    color: var(--primary-color, #ff6c00);
    font-weight: 500;
    cursor: pointer;
    border-radius: 25px;
    transition: all 0.3s ease;
    white-space: nowrap;
}

.menu-tab:hover,
.menu-tab.active {
    background: var(--primary-color, #ff6c00);
    color: #fff;
}

.menu-content {
    padding: 0.5rem 0;
}

.menu-scroll-container {
    max-height: 75vh;
    overflow-y: auto;
    padding-right: 0.5rem;
    scrollbar-width: thin;
    scrollbar-color: var(--primary-color, #ff6c00) #f1f1f1;
}

.menu-scroll-container::-webkit-scrollbar {
    width: 6px;
}

.menu-scroll-container::-webkit-scrollbar-track {
    background: #f1f1f1;
    border-radius: 3px;
}

.menu-scroll-container::-webkit-scrollbar-thumb {
    background: var(--primary-color, #ff6c00);
    border-radius: 3px;
}

.menu-scroll-container::-webkit-scrollbar-thumb:hover {
    background: var(--primary-hover, #e55a00);
}

.menu-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
    gap: 1.5rem;
    padding-bottom: 1rem;
}

.menu-item {
    background: #fff;
    border-radius: 12px;
    overflow: hidden;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    transition: all 0.3s ease;
    cursor: pointer;
}

.menu-item:hover {
    transform: translateY(-4px);
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
}

.item-image {
    width: 100%;
    height: 180px;
    overflow: hidden;
}

.item-image img {
    width: 100%;
    height: 100%;
    object-fit: cover;
}

.item-content {
    display: flex;
    justify-content: space-between;
    align-items: stretch;
    padding: 1.2rem;
    flex: 1;
}

.item-info {
    flex: 1;
    display: flex;
    flex-direction: column;
    justify-content: space-between;
}

.item-actions {
    display: flex;
    align-items: flex-end;
    margin-left: 1rem;
}

.item-name {
    font-size: 1.1rem;
    font-weight: 600;
    color: var(--text-primary, #333);
    margin: 0 0 0.5rem 0;
}

.item-desc {
    font-size: 0.9rem;
    color: var(--text-secondary, #666);
    margin: 0 0 1rem 0;
    line-height: 1.4;
}

.price-section {
    display: flex;
    align-items: center;
    gap: 0.5rem;
    margin-bottom: 1rem;
}

.original-price {
    font-size: 0.9rem;
    color: var(--text-muted, #999);
    text-decoration: line-through;
}

.current-price {
    font-size: 1.1rem;
    font-weight: 600;
    color: var(--price-color, #e74c3c);
}

.add-to-cart-btn {
    width: 3rem;
    height: 3rem;
    background: var(--primary-color, #ff6c00);
    color: #fff;
    border: none;
    border-radius: 50%;
    font-size: 1.2rem;
    cursor: pointer;
    transition: all 0.3s ease;
    display: flex;
    justify-content: center;
    align-items: center;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

.add-to-cart-btn:hover {
    background: var(--primary-hover, #e55a00);
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
}

.cart-float-btn {
    position: fixed;
    bottom: 2rem;
    right: 2rem;
    background: var(--primary-color, #ff6c00);
    color: #fff;
    border-radius: 50px;
    padding: 1rem 1.5rem;
    display: flex;
    align-items: center;
    gap: 0.75rem;
    cursor: pointer;
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.2);
    transition: all 0.3s ease;
    z-index: 1000;
}

.cart-float-btn:hover {
    background: var(--primary-hover, #e55a00);
    transform: translateY(-2px);
}

.cart-icon {
    position: relative;
    font-size: 1.5rem;
}

.cart-badge {
    position: absolute;
    top: -8px;
    right: -8px;
    background: #e74c3c;
    color: #fff;
    border-radius: 50%;
    width: 1.2rem;
    height: 1.2rem;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 0.7rem;
    font-weight: bold;
}

.cart-total {
    font-weight: 600;
    font-size: 1rem;
}

.no-menu {
    text-align: center;
    color: var(--text-secondary, #666);
    padding: 3rem 0;
    font-size: 1.1rem;
}

@media (max-width: 768px) {
    .menu-scroll-container {
        max-height: 70vh;
        padding-right: 0.25rem;
    }

    .menu-grid {
        grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
        gap: 1rem;
    }

    .menu-title {
        font-size: 1.75rem;
    }

    .menu-tabs {
        gap: 0.5rem;
    }

    .menu-tab {
        padding: 0.5rem 1rem;
        font-size: 0.9rem;
    }

    .item-content {
        padding: 1rem;
    }

    .item-actions {
        margin-left: 0.5rem;
    }

    .add-to-cart-btn {
        width: 2.5rem;
        height: 2.5rem;
        font-size: 1rem;
    }

    .cart-float-btn {
        bottom: 1rem;
        right: 1rem;
        padding: 0.75rem 1rem;
    }
}

@media (max-width: 480px) {
    .menu-scroll-container {
        max-height: 65vh;
        padding-right: 0.1rem;
    }

    .menu-scroll-container::-webkit-scrollbar {
        width: 4px;
    }

    .menu-grid {
        grid-template-columns: 1fr;
        gap: 1rem;
    }

    .restaurant-menu {
        padding: 0 0.5rem;
    }

    .item-content {
        padding: 0.75rem;
        flex-direction: column;
        align-items: stretch;
    }

    .item-actions {
        margin-left: 0;
        margin-top: 0.75rem;
        justify-content: flex-end;
    }

    .add-to-cart-btn {
        width: 2.25rem;
        height: 2.25rem;
        font-size: 0.9rem;
    }

    .cart-float-btn {
        padding: 0.5rem 0.75rem;
        font-size: 0.9rem;
    }

    .cart-icon {
        font-size: 1.2rem;
    }
}
</style>