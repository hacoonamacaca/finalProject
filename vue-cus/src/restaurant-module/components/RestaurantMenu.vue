<template>
    <div class="restaurant-menu restaurant-theme">
        <!-- 菜單分類導航組件 -->
        <MenuNavigation :categories="categories" :restaurant="restaurant" v-model:activeCategory="activeCategory"
            @categoryClick="handleCategoryClick" ref="menuNavigation" />

        <div class="menu-container">
            <h3 class="menu-title">{{ restaurant.menuTitle || '餐廳菜單' }}</h3>

            <!-- 分類按鈕區域 -->
            <div class="category-buttons-section">
                <div class="category-buttons-container">
                    <button v-for="category in categories" :key="category.id"
                        :class="['category-button', { 'active': activeCategory === category.name }]"
                        @click="selectCategory(category.name)">
                        <span class="category-name">{{ category.name }}</span>
                        <span class="category-count">({{ category.count }})</span>
                    </button>
                    <button :class="['category-button', { 'active': activeCategory === '' }]"
                        @click="selectCategory('')">
                        <span class="category-name">全部</span>
                        <span class="category-count">({{ allItemsCount }})</span>
                    </button>
                </div>
            </div>

            <!-- 菜品內容區域 -->
            <main class="menu-main">
                <div v-if="hasMenuItems">
                    <!-- 依分類顯示菜品 -->
                    <section v-for="category in categories" :key="category.id" :id="`category-${category.id}`"
                        class="menu-slide category-section"
                        v-show="activeCategory === '' || activeCategory === category.name">
                        <h2 v-show="filteredItemsByCategory(category.name).length > 0" class="slide-category-title">
                            {{ category.name }}
                        </h2>
                        <div class="menu-grid" v-if="filteredItemsByCategory(category.name).length > 0">
                            <div class="menu-item" v-for="item in filteredItemsByCategory(category.name)" :key="item.id"
                                @click="openItemDetail(item)">
                                <!-- 標籤 -->
                                <div class="item-tags" v-if="item.tags && item.tags.length > 0">
                                    <span v-for="tag in item.tags" :key="tag" class="item-tag">{{ tag }}</span>
                                </div>

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
                    </section>
                </div>
                <div v-else class="no-menu">
                    <p>暫無菜品資訊</p>
                </div>
            </main>
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
import { ref, computed, onMounted, onUnmounted, nextTick } from 'vue'
import ItemDetailModal from './ItemDetailModal.vue'
import CartModal from './CartModal.vue'
import MenuNavigation from './MenuNavigation.vue'
import '@/assets/css/restaurant-theme.css'

const props = defineProps({
    restaurant: {
        type: Object,
        required: true
    }
})

const emit = defineEmits(['checkout'])

// 基本狀態
const selectedItem = ref(null)
const showItemDetail = ref(false)
const cartItems = ref([])
const isCartVisible = ref(false)

// 分類狀態
const activeCategory = ref('')

// Refs
const menuNavigation = ref(null)

// 預設分類和商品資料
const categories = ref([
    { id: 'popular', name: '人氣精選', count: 3 },
    { id: 'new-arrivals', name: '新品上市', count: 3 },
    { id: 'chef-picks', name: '店長推薦', count: 4 },
    { id: 'drinks', name: '茗品系列', count: 2 },
    { id: 'yogurt', name: '優多系列', count: 3 },
    { id: 'winter-melon', name: '冬瓜 / 百香果系列', count: 3 },
    { id: 'milk-tea', name: '奶茶系列', count: 4 },
    { id: 'fresh-milk', name: '鮮奶拿鐵', count: 3 },
])

const items = ref([
    { id: 1, name: '武樓全牌鹽水雞沙拉', description: '字樣示意描述：雞肉、玉米、青菜、辣粉', image: 'https://placehold.co/400x300/E7E7E7/333333?text=Product+1', originalPrice: 379, discountPrice: 296, category: '人氣精選', tags: ['熱銷', '推薦'] },
    { id: 2, name: '檸香法式丹麥Sunny舒肥雞', description: '檸香舒肥雞、歐姆蛋、麵包等', image: 'https://placehold.co/400x300/E7E7E7/333333?text=Product+2', originalPrice: 480, discountPrice: 384, category: '新品上市', tags: ['新品'] },
    { id: 3, name: '招牌起司牛肉堡', description: '經典牛肉、濃郁起司、新鮮蔬菜', image: 'https://placehold.co/400x300/E7E7E7/333333?text=Product+3', originalPrice: 250, discountPrice: 200, category: '店長推薦', tags: ['招牌'] },
    { id: 4, name: '香煎鮭魚排', description: '鮮嫩鮭魚、時蔬、特製醬汁', image: 'https://placehold.co/400x300/E7E7E7/333333?text=Product+4', originalPrice: 350, discountPrice: 280, category: '人氣精選', tags: ['健康'] },
    { id: 5, name: '義式肉醬麵', description: '經典肉醬、Q彈義大利麵', image: 'https://placehold.co/400x300/E7E7E7/333333?text=Product+5', originalPrice: 180, discountPrice: 150, category: '茗品系列' },
    { id: 6, name: '特調水果茶', description: '多種新鮮水果、清爽茶底', image: 'https://placehold.co/400x300/E7E7E7/333333?text=Product+6', originalPrice: 120, discountPrice: 100, category: '優多系列', tags: ['清爽'] },
    { id: 7, name: '黑糖珍珠鮮奶', description: '香濃黑糖、Q彈珍珠、新鮮牛奶', image: 'https://placehold.co/400x300/E7E7E7/333333?text=Product+7', originalPrice: 90, discountPrice: 75, category: '奶茶系列', tags: ['經典'] },
    { id: 8, name: '經典美式咖啡', description: '嚴選咖啡豆、香醇濃郁', image: 'https://placehold.co/400x300/E7E7E7/333333?text=Product+8', originalPrice: 70, discountPrice: 60, category: '冬瓜 / 百香果系列' },
    { id: 9, name: '酥炸雞米花', description: '外酥內嫩、香辣可口', image: 'https://placehold.co/400x300/E7E7E7/333333?text=Product+9', originalPrice: 100, discountPrice: 85, category: '人氣精選', tags: ['酥脆'] },
    { id: 10, name: '抹茶拿鐵', description: '日式抹茶、香醇牛奶', image: 'https://placehold.co/400x300/E7E7E7/333333?text=Product+10', originalPrice: 110, discountPrice: 90, category: '奶茶系列', tags: ['日式'] },
    { id: 11, name: '綜合水果優格', description: '新鮮水果、低脂優格', image: 'https://placehold.co/400x300/E7E7E7/333333?text=Product+11', originalPrice: 150, discountPrice: 120, category: '優多系列', tags: ['健康', '低脂'] },
    { id: 12, name: '香草冰淇淋', description: '濃郁香草、清涼消暑', image: 'https://placehold.co/400x300/E7E7E7/333333?text=Product+12', originalPrice: 80, discountPrice: 70, category: '店長推薦', tags: ['甜品'] },

    // 新增更多測試商品
    { id: 13, name: '蜂蜜芥末雞腿堡', description: '酥脆雞腿、蜂蜜芥末醬、生菜', image: 'https://placehold.co/400x300/E7E7E7/333333?text=Product+13', originalPrice: 220, discountPrice: 180, category: '新品上市', tags: ['新品', '辣味'] },
    { id: 14, name: '日式照燒豚肉飯', description: '軟嫩豚肉、照燒醬汁、白飯', image: 'https://placehold.co/400x300/E7E7E7/333333?text=Product+14', originalPrice: 200, discountPrice: 160, category: '店長推薦', tags: ['日式'] },
    { id: 15, name: '芒果芝士蛋糕', description: '濃郁芝士、新鮮芒果、酥脆餅底', image: 'https://placehold.co/400x300/E7E7E7/333333?text=Product+15', originalPrice: 180, discountPrice: 150, category: '店長推薦', tags: ['甜品', '限量'] },
    { id: 16, name: '冬瓜檸檬蜜', description: '清香冬瓜、酸甜檸檬、天然蜂蜜', image: 'https://placehold.co/400x300/E7E7E7/333333?text=Product+16', originalPrice: 85, discountPrice: 70, category: '冬瓜 / 百香果系列', tags: ['清爽'] },
    { id: 17, name: '百香果氣泡水', description: '新鮮百香果、清涼氣泡、薄荷葉', image: 'https://placehold.co/400x300/E7E7E7/333333?text=Product+17', originalPrice: 95, discountPrice: 80, category: '冬瓜 / 百香果系列', tags: ['氣泡'] },
    { id: 18, name: '招牌奶茶', description: '濃郁茶香、香醇鮮奶、完美比例', image: 'https://placehold.co/400x300/E7E7E7/333333?text=Product+18', originalPrice: 75, discountPrice: 60, category: '奶茶系列', tags: ['招牌'] },
    { id: 19, name: '焦糖瑪奇朵', description: '濃縮咖啡、蒸煮牛奶、焦糖糖漿', image: 'https://placehold.co/400x300/E7E7E7/333333?text=Product+19', originalPrice: 120, discountPrice: 100, category: '鮮奶拿鐵', tags: ['咖啡'] },
    { id: 20, name: '香草拿鐵', description: '香草風味、濃郁咖啡、綿密奶泡', image: 'https://placehold.co/400x300/E7E7E7/333333?text=Product+20', originalPrice: 110, discountPrice: 90, category: '鮮奶拿鐵', tags: ['香草'] },
    { id: 21, name: '草莓優格杯', description: '新鮮草莓、低脂優格、燕麥片', image: 'https://placehold.co/400x300/E7E7E7/333333?text=Product+21', originalPrice: 130, discountPrice: 110, category: '優多系列', tags: ['水果', '健康'] },
    { id: 22, name: '藍莓司康餅', description: '酥脆司康、新鮮藍莓、奶油', image: 'https://placehold.co/400x300/E7E7E7/333333?text=Product+22', originalPrice: 90, discountPrice: 75, category: '茗品系列', tags: ['烘焙'] },
    { id: 23, name: '榛果拿鐵', description: '榛果香氣、濃縮咖啡、蒸煮牛奶', image: 'https://placehold.co/400x300/E7E7E7/333333?text=Product+23', originalPrice: 115, discountPrice: 95, category: '鮮奶拿鐵', tags: ['堅果'] },
    { id: 24, name: '芝麻奶茶', description: '香濃芝麻、經典奶茶、古早味', image: 'https://placehold.co/400x300/E7E7E7/333333?text=Product+24', originalPrice: 85, discountPrice: 70, category: '奶茶系列', tags: ['古早味'] },
])

// 計算屬性
const filteredItems = computed(() => {
    if (activeCategory.value === '') {
        return items.value // 顯示全部商品
    }
    return items.value.filter(item => item.category === activeCategory.value)
})

const filteredItemsByCategory = (categoryName) => {
    if (activeCategory.value === '') {
        // 顯示全部時，按分類過濾
        return items.value.filter(item => item.category === categoryName)
    } else if (activeCategory.value === categoryName) {
        // 選中特定分類時，只顯示該分類
        return items.value.filter(item => item.category === categoryName)
    }
    return [] // 其他分類不顯示
}

const hasMenuItems = computed(() => {
    if (activeCategory.value === '') {
        return categories.value.some(category =>
            items.value.some(item => item.category === category.name)
        )
    }
    return filteredItems.value.length > 0
})

const totalCartQuantity = computed(() => {
    return cartItems.value.reduce((sum, item) => sum + item.quantity, 0)
})

const totalCartAmount = computed(() => {
    return cartItems.value.reduce((sum, item) => sum + (item.price * item.quantity), 0)
})

const allItemsCount = computed(() => {
    return items.value.length
})

// 方法
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

    emit('checkout', orderData)
    cartItems.value = []
    isCartVisible.value = false
}

// 處理分類點擊事件（從子組件接收）
const handleCategoryClick = (category) => {
    activeCategory.value = category.name
}

// 處理分類按鈕點擊
const selectCategory = (categoryName) => {
    activeCategory.value = categoryName
    // 同步更新MenuNavigation組件
    emit('categoryClick', { name: categoryName })
}

// 生命周期
onMounted(() => {
    // 初始化為顯示全部商品
    activeCategory.value = ''
})

onUnmounted(() => {
    // 清理資源
})
</script>

<style scoped>
.restaurant-menu {
    margin: 2rem 0;
    padding: 0 1rem;
    position: relative;
    background: var(--restaurant-bg-primary);
}

.menu-container {
    width: 100%;
    margin: 0 auto;
    max-width: 1200px;
}

.menu-title {
    font-size: 2rem;
    font-weight: 600;
    color: var(--restaurant-text-primary);
    margin-bottom: 2rem;
    text-align: center;
    text-shadow: 0 1px 2px var(--restaurant-shadow-light);
}

/* 分類按鈕區域 */
.category-buttons-section {
    margin-bottom: 2rem;
    padding: 0 1rem;
}

.category-buttons-container {
    display: flex;
    flex-wrap: wrap;
    gap: 0.75rem;
    justify-content: center;
    max-width: 800px;
    margin: 0 auto;
}

.category-button {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 0.25rem;
    padding: 0.75rem 1rem;
    background: var(--restaurant-bg-card, #ffffff);
    border: 2px solid var(--restaurant-border-light, #eee);
    border-radius: 12px;
    cursor: pointer;
    transition: all 0.3s ease;
    min-width: 120px;
    box-shadow: 0 2px 8px var(--restaurant-shadow-light, rgba(0, 0, 0, 0.05));
}

.category-button:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 16px var(--restaurant-shadow-medium, rgba(0, 0, 0, 0.1));
    border-color: var(--restaurant-primary, #ffba20);
}

.category-button.active {
    background: var(--restaurant-primary, #ffba20);
    border-color: var(--restaurant-primary, #ffba20);
    color: white;
    transform: translateY(-2px);
    box-shadow: 0 4px 16px var(--restaurant-shadow-medium, rgba(255, 186, 32, 0.3));
}

.category-name {
    font-size: 0.9rem;
    font-weight: 600;
    text-align: center;
}

.category-count {
    font-size: 0.75rem;
    opacity: 0.8;
    font-weight: 500;
}

.category-button.active .category-count {
    opacity: 1;
}

/* 菜品內容區域 */
.menu-main {
    min-height: 100vh;
}

.menu-slide {
    padding: 2rem 0;
    margin-bottom: 2rem;
    scroll-margin-top: 90px;
    /* 為sticky navigation預留空間 */
}

.slide-category-title {
    font-size: 1.8rem;
    font-weight: 600;
    color: var(--restaurant-text-primary);
    margin-bottom: 2rem;
    text-align: center;
    padding: 1rem;
    background: var(--restaurant-bg-accent);
    border-radius: 12px;
    box-shadow: 0 2px 8px var(--restaurant-shadow-light);
    border-left: 4px solid var(--restaurant-primary);
}

.category-section {
    scroll-margin-top: 90px;
    /* 確保sticky navigation不會遮蓋內容 */
}

/* 當sticky navigation激活時增加body的頂部間距 */
.menu-hero-tabs-container--top~.menu-main {
    padding-top: 2rem;
}

.menu-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(320px, 1fr));
    gap: 1.5rem;
    padding: 1rem 0;
    justify-content: center;
}

.menu-item {
    position: relative;
    background: var(--restaurant-bg-card);
    border: 1px solid var(--restaurant-border-light);
    border-radius: 12px;
    overflow: hidden;
    box-shadow: 0 2px 8px var(--restaurant-shadow-light);
    transition: all 0.3s ease;
    cursor: pointer;
    max-width: 320px;
    height: 420px;
    display: flex;
    flex-direction: column;
    margin: 0 auto;
}

.menu-item:hover {
    transform: translateY(-4px);
    box-shadow: 0 6px 20px var(--restaurant-shadow-medium);
    border-color: var(--restaurant-primary);
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
    color: var(--restaurant-text-primary);
    margin: 0 0 0.5rem 0;
}

.item-desc {
    font-size: 0.9rem;
    color: var(--restaurant-text-secondary);
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
    color: var(--restaurant-text-muted);
    text-decoration: line-through;
}

.current-price {
    font-size: 1.1rem;
    font-weight: 600;
    color: var(--restaurant-primary);
}

.add-to-cart-btn {
    width: 3rem;
    height: 3rem;
    background: var(--restaurant-primary);
    color: var(--restaurant-text-primary);
    border: none;
    border-radius: 50%;
    font-size: 1.2rem;
    cursor: pointer;
    transition: all 0.3s ease;
    display: flex;
    justify-content: center;
    align-items: center;
    box-shadow: 0 2px 8px var(--restaurant-shadow-light);
}

.add-to-cart-btn:hover {
    background: var(--restaurant-primary-hover);
    transform: translateY(-2px) scale(1.05);
    box-shadow: 0 6px 16px var(--restaurant-shadow-medium);
}

.cart-float-btn {
    position: fixed;
    bottom: 2rem;
    right: 2rem;
    background: var(--restaurant-primary);
    color: var(--restaurant-text-primary);
    border-radius: 50px;
    padding: 1rem 1.5rem;
    display: flex;
    align-items: center;
    gap: 0.75rem;
    cursor: pointer;
    box-shadow: 0 6px 20px var(--restaurant-shadow-medium);
    transition: all 0.3s ease;
    z-index: 1000;
}

.cart-float-btn:hover {
    background: var(--restaurant-primary-hover);
    transform: translateY(-3px) scale(1.05);
    box-shadow: 0 8px 24px var(--restaurant-shadow-dark);
}

.cart-icon {
    position: relative;
    font-size: 1.5rem;
}

.cart-badge {
    position: absolute;
    top: -8px;
    right: -8px;
    background: var(--restaurant-gradient-primary);
    color: var(--restaurant-text-primary);
    border-radius: 50%;
    width: 1.2rem;
    height: 1.2rem;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 0.7rem;
    font-weight: bold;
    box-shadow: 0 1px 4px var(--restaurant-shadow-light);
}

.cart-total {
    font-weight: 600;
    font-size: 1rem;
    color: var(--restaurant-text-primary);
}

.no-menu {
    text-align: center;
    color: var(--restaurant-text-secondary);
    padding: 3rem 0;
    font-size: 1.1rem;
    background: var(--restaurant-bg-light);
    border-radius: 12px;
    margin: 1rem 0;
}

/* 商品標籤 */
.item-tags {
    position: absolute;
    top: 0.75rem;
    left: 0.75rem;
    z-index: 10;
    display: flex;
    gap: 0.5rem;
    flex-wrap: wrap;
}

.item-tag {
    padding: 0.25rem 0.75rem;
    background: var(--restaurant-primary);
    color: var(--restaurant-text-primary);
    font-size: 0.75rem;
    font-weight: 600;
    border-radius: 12px;
    box-shadow: 0 1px 4px var(--restaurant-shadow-light);
}

/* 響應式設計 */
@media (max-width: 768px) {
    .restaurant-menu {
        padding: 1rem;
    }

    .menu-title {
        font-size: 1.8rem;
        margin-bottom: 1.5rem;
    }

    .category-buttons-container {
        gap: 0.5rem;
        max-width: 100%;
    }

    .category-button {
        min-width: 100px;
        padding: 0.5rem 0.75rem;
    }

    .category-name {
        font-size: 0.8rem;
    }

    .category-count {
        font-size: 0.7rem;
    }

    .menu-grid {
        grid-template-columns: 1fr;
        gap: 1rem;
    }

    .item-name {
        font-size: 1rem;
    }

    .item-desc {
        font-size: 0.8rem;
    }

    .cart-float-btn {
        bottom: 1rem;
        right: 1rem;
        padding: 0.75rem 1rem;
    }
}

@media (max-width: 480px) {
    .restaurant-menu {
        padding: 0.5rem;
    }

    .menu-title {
        font-size: 1.5rem;
    }

    .category-buttons-container {
        gap: 0.5rem;
        padding: 0 0.5rem;
    }

    .category-button {
        min-width: 80px;
        padding: 0.5rem;
    }

    .category-name {
        font-size: 0.75rem;
    }

    .category-count {
        font-size: 0.65rem;
    }

    .item-name {
        font-size: 0.9rem;
    }

    .current-price,
    .original-price {
        font-size: 0.9rem;
    }

    .cart-float-btn {
        padding: 0.5rem 0.75rem;
    }
}
</style>