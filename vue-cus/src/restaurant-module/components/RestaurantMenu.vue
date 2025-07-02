<template>
    <div class="restaurant-menu restaurant-theme">
        <!-- Hero Banner區域 - 先凸顯主題 -->
        <section class="menu-hero-banner">
            <div class="hero-content">
                <h1 class="hero-title">{{ restaurant?.name || '美味餐廳' }}</h1>
                <p class="hero-description">探索我們精心調製的美食世界</p>
                <div class="hero-stats">
                    <span class="stat-item">
                        <i class="pi pi-shopping-cart"></i>
                        {{ categories.length }} 個分類
                    </span>
                    <span class="stat-item">
                        <i class="pi pi-heart"></i>
                        {{ allItemsCount }} 道精選料理
                    </span>
                </div>
            </div>
            <div class="hero-decoration"></div>
        </section>

        <!-- 黏性導航區域 - 滾動時才固定 -->
        <nav class="sticky-nav" ref="stickyNav">
            <div class="sticky-nav-container" ref="tabsContainer">
                <div class="nav-tabs">
                    <a v-for="category in categories" :key="category.id"
                        :class="['nav-tab', { 'active': activeCategory === category.name }]"
                        :href="`#category-${category.id}`" @click="onTabClick($event, category)">
                        {{ category.name }}
                        <span class="tab-count">({{ category.count }})</span>
                    </a>
                    <a :class="['nav-tab', { 'active': activeCategory === '' }]" href="#all-categories"
                        @click="onTabClick($event, { name: '', id: 'all' })">
                        全部
                        <span class="tab-count">({{ allItemsCount }})</span>
                    </a>
                </div>
                <div class="nav-slider" ref="tabSlider"></div>
            </div>
        </nav>

        <!-- 菜品內容區域 -->
        <div class="menu-container">
            <main class="menu-main">
                <div v-if="hasMenuItems">
                    <!-- 依分類顯示菜品 -->
                    <section v-for="category in categories" :key="category.id" :id="`category-${category.id}`"
                        class="menu-slide category-section"
                        v-show="activeCategory === '' || activeCategory === category.name">
                        <h2 v-show="filteredItemsByCategory(category.name).length > 0" class="category-title">
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
import { ref, computed, onMounted, onUnmounted, nextTick, watch } from 'vue'
import ItemDetailModal from './ItemDetailModal.vue'
import CartModal from './CartModal.vue'
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
const tabsContainer = ref(null)
const tabSlider = ref(null)
const stickyNav = ref(null)

// Sticky navigation constants
const STICKY_OFFSET = 80 // 固定時的偏移量

// State for sticky navigation
let currentId = null
let currentTab = null
let isSticky = false

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
}

// Hero導航相關方法
const onTabClick = (event, category) => {
    event.preventDefault()

    // 更新活動分類
    activeCategory.value = category.name

    // 如果點擊的是分類（不是"全部"），滾動到對應分類
    if (category.name && category.id !== 'all') {
        const target = document.getElementById(`category-${category.id}`)
        if (target) {
            const scrollTop = target.offsetTop - STICKY_OFFSET
            window.scrollTo({
                top: scrollTop,
                behavior: 'smooth'
            })
        }
    } else {
        // 點擊"全部"時滾動到菜單開始位置
        const menuContainer = document.querySelector('.menu-container')
        if (menuContainer) {
            const scrollTop = menuContainer.offsetTop - STICKY_OFFSET
            window.scrollTo({
                top: scrollTop,
                behavior: 'smooth'
            })
        }
    }
}

const checkStickyNavPosition = () => {
    if (!stickyNav.value) return

    const heroBanner = document.querySelector('.menu-hero-banner')
    if (heroBanner) {
        const heroBottom = heroBanner.offsetTop + heroBanner.offsetHeight
        const currentScrollY = window.scrollY

        if (currentScrollY >= heroBottom - 20) { // 20px緩衝區
            if (!isSticky) {
                isSticky = true
                stickyNav.value.classList.add('sticky-nav--fixed')
                // 為body添加padding避免內容跳動
                document.body.style.paddingTop = `${STICKY_OFFSET}px`
                setTimeout(() => {
                    setSliderCss()
                }, 100)
            }
        } else {
            if (isSticky) {
                isSticky = false
                stickyNav.value.classList.remove('sticky-nav--fixed')
                document.body.style.paddingTop = '0'
                setTimeout(() => {
                    setSliderCss()
                }, 100)
            }
        }
    }
}

const findCurrentTabSelector = () => {
    let newCurrentId = null
    let newCurrentTab = null

    const tabs = tabsContainer.value?.querySelectorAll('.nav-tab')
    if (!tabs) return

    tabs.forEach(tab => {
        const id = tab.getAttribute('href')
        if (id && id !== '#all-categories') {
            const element = document.querySelector(id)
            if (element) {
                const offsetTop = element.offsetTop - STICKY_OFFSET
                const offsetBottom = element.offsetTop + element.offsetHeight - STICKY_OFFSET
                const currentScrollY = window.scrollY

                if (currentScrollY >= offsetTop && currentScrollY < offsetBottom) {
                    newCurrentId = id
                    newCurrentTab = tab
                }
            }
        }
    })

    if (currentId !== newCurrentId) {
        currentId = newCurrentId
        currentTab = newCurrentTab

        // 更新活動分類
        if (newCurrentTab) {
            const categoryName = newCurrentTab.textContent.replace(/\(\d+\)/, '').trim()
            if (activeCategory.value !== categoryName && categoryName !== '全部') {
                activeCategory.value = categoryName
            }
        }

        setSliderCss()
    }
}

const setSliderCss = () => {
    if (!tabSlider.value || !tabsContainer.value) return

    let width = 0
    let left = 0

    // 找到當前活動的tab
    const activeTab = tabsContainer.value.querySelector('.nav-tab.active')
    if (activeTab) {
        const rect = activeTab.getBoundingClientRect()
        const containerRect = tabsContainer.value.getBoundingClientRect()

        width = rect.width
        left = rect.left - containerRect.left
    }

    tabSlider.value.style.width = `${width}px`
    tabSlider.value.style.left = `${left}px`
    tabSlider.value.style.opacity = width > 0 ? '1' : '0'
}

const onScroll = () => {
    checkStickyNavPosition()
    if (isSticky) {
        findCurrentTabSelector()
    }
}

const onResize = () => {
    if (activeCategory.value !== null) {
        setSliderCss()
    }
}

// 生命周期
onMounted(() => {
    // 設置預設分類
    if (props.restaurant?.categories?.length > 0) {
        activeCategory.value = '' // 預設顯示所有分類
    }

    // 初始化黏性導航
    nextTick(() => {
        setSliderCss()

        // 添加事件監聽器
        window.addEventListener('scroll', onScroll, { passive: true })
        window.addEventListener('resize', onResize, { passive: true })
    })
})

onUnmounted(() => {
    // 清理事件監聽器
    window.removeEventListener('scroll', onScroll)
    window.removeEventListener('resize', onResize)

    // 清理body padding
    document.body.style.paddingTop = '0'
})

// 監聽活動分類變化
watch(activeCategory, () => {
    nextTick(() => {
        setSliderCss()
    })
})
</script>

<style scoped>
/* Hero Banner區域 - 先凸顯主題 */
.menu-hero-banner {
    position: relative;
    background: linear-gradient(135deg,
            var(--restaurant-primary-color, #ff6b35) 0%,
            var(--restaurant-secondary-color, #f7931e) 100%);
    min-height: 70vh;
    padding: 80px 20px 60px;
    display: flex;
    align-items: center;
    justify-content: center;
    color: white;
    overflow: hidden;
}

.hero-content {
    text-align: center;
    z-index: 2;
    position: relative;
    max-width: 800px;
}

.hero-title {
    font-size: 4rem;
    font-weight: 800;
    margin-bottom: 24px;
    text-shadow: 2px 4px 8px rgba(0, 0, 0, 0.3);
    letter-spacing: 2px;
    line-height: 1.1;
}

.hero-description {
    font-size: 1.5rem;
    font-weight: 300;
    margin-bottom: 40px;
    opacity: 0.95;
    letter-spacing: 0.5px;
    line-height: 1.4;
}

.hero-stats {
    display: flex;
    justify-content: center;
    gap: 40px;
    margin-top: 40px;
}

.stat-item {
    display: flex;
    align-items: center;
    gap: 10px;
    font-size: 1.1rem;
    font-weight: 500;
    background: rgba(255, 255, 255, 0.15);
    padding: 12px 24px;
    border-radius: 30px;
    backdrop-filter: blur(10px);
    border: 1px solid rgba(255, 255, 255, 0.2);
}

.stat-item i {
    font-size: 1.3rem;
}

.hero-decoration {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 20"><defs><radialGradient id="a" cx="50%" cy="50%"><stop offset="0%" stop-color="rgba(255,255,255,.1)"/><stop offset="100%" stop-color="rgba(255,255,255,0)"/></radialGradient></defs><circle cx="20" cy="10" r="8" fill="url(%23a)"/><circle cx="80" cy="10" r="6" fill="url(%23a)"/></svg>');
    opacity: 0.5;
    animation: float 20s ease-in-out infinite;
}

@keyframes float {

    0%,
    100% {
        transform: translateY(0px);
    }

    50% {
        transform: translateY(-20px);
    }
}

/* 黏性導航區域 - 符合文章理念 */
.sticky-nav {
    background: white;
    border-bottom: 1px solid rgba(0, 0, 0, 0.1);
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    z-index: 100;
    position: relative;
}

.sticky-nav--fixed {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    background: rgba(255, 255, 255, 0.95);
    backdrop-filter: blur(20px);
    box-shadow: 0 8px 40px rgba(0, 0, 0, 0.12);
    border-bottom: 1px solid var(--restaurant-primary-color, #ff6b35);
    animation: slideDownSticky 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}

@keyframes slideDownSticky {
    from {
        opacity: 0;
        transform: translateY(-100%);
    }

    to {
        opacity: 1;
        transform: translateY(0);
    }
}

.sticky-nav-container {
    max-width: 1200px;
    margin: 0 auto;
    position: relative;
    padding: 0 20px;
}

.nav-tabs {
    display: flex;
    justify-content: center;
    align-items: center;
    gap: 8px;
    padding: 16px 0;
    flex-wrap: wrap;
}

.nav-tab {
    display: inline-flex;
    align-items: center;
    gap: 8px;
    padding: 12px 24px;
    color: var(--restaurant-primary-color, #ff6b35);
    text-decoration: none;
    font-weight: 500;
    font-size: 1rem;
    border-radius: 25px;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    white-space: nowrap;
    position: relative;
    z-index: 2;
    background: transparent;
    border: 2px solid transparent;
}

.nav-tab:hover {
    background: rgba(255, 107, 53, 0.1);
    border-color: rgba(255, 107, 53, 0.2);
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(255, 107, 53, 0.15);
}

.nav-tab.active {
    background: var(--restaurant-primary-color, #ff6b35);
    color: white;
    border-color: var(--restaurant-primary-color, #ff6b35);
    font-weight: 600;
    box-shadow: 0 4px 15px rgba(255, 107, 53, 0.3);
}

.tab-count {
    font-size: 0.85rem;
    opacity: 0.8;
    font-weight: 400;
}

.nav-tab.active .tab-count {
    opacity: 1;
}

/* 滑塊效果 */
.nav-slider {
    position: absolute;
    bottom: 8px;
    height: 3px;
    background: var(--restaurant-primary-color, #ff6b35);
    border-radius: 2px;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    opacity: 0;
    z-index: 1;
}

.sticky-nav--fixed .nav-slider {
    bottom: 6px;
    height: 4px;
    background: linear-gradient(90deg,
            var(--restaurant-primary-color, #ff6b35),
            var(--restaurant-secondary-color, #f7931e));
}

/* 菜單容器 */
.menu-container {
    max-width: 1200px;
    margin: 0 auto;
    padding: 60px 20px;
}

/* 分類區域 */
.category-section {
    margin-bottom: 80px;
    scroll-margin-top: 100px;
    /* 為sticky導航預留空間 */
}

.category-title {
    font-size: 2.5rem;
    font-weight: 700;
    color: var(--restaurant-primary-color, #ff6b35);
    margin-bottom: 40px;
    padding-bottom: 20px;
    border-bottom: 3px solid rgba(255, 107, 53, 0.2);
    position: relative;
}

.category-title::after {
    content: '';
    position: absolute;
    bottom: -3px;
    left: 0;
    width: 60px;
    height: 3px;
    background: var(--restaurant-primary-color, #ff6b35);
    border-radius: 2px;
}

/* 菜品網格 */
.menu-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
    gap: 30px;
    margin-bottom: 40px;
}

.menu-item {
    background: white;
    border-radius: 20px;
    overflow: hidden;
    box-shadow: 0 8px 30px rgba(0, 0, 0, 0.08);
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    cursor: pointer;
    position: relative;
    border: 1px solid rgba(0, 0, 0, 0.06);
}

.menu-item:hover {
    transform: translateY(-8px);
    box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15);
    border-color: var(--restaurant-primary-color, #ff6b35);
}

.item-tags {
    position: absolute;
    top: 15px;
    left: 15px;
    z-index: 3;
    display: flex;
    gap: 6px;
    flex-wrap: wrap;
}

.item-tag {
    background: var(--restaurant-primary-color, #ff6b35);
    color: white;
    padding: 4px 12px;
    border-radius: 20px;
    font-size: 0.75rem;
    font-weight: 500;
    box-shadow: 0 2px 8px rgba(255, 107, 53, 0.3);
}

.item-image {
    position: relative;
    height: 220px;
    overflow: hidden;
}

.item-image img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    transition: transform 0.3s ease;
}

.menu-item:hover .item-image img {
    transform: scale(1.05);
}

.item-content {
    padding: 25px;
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    gap: 15px;
}

.item-info {
    flex: 1;
}

.item-name {
    font-size: 1.3rem;
    font-weight: 600;
    color: #333;
    margin-bottom: 8px;
    line-height: 1.3;
}

.item-desc {
    color: #666;
    font-size: 0.95rem;
    line-height: 1.5;
    margin-bottom: 15px;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    overflow: hidden;
}

.price-section {
    display: flex;
    align-items: center;
    gap: 12px;
}

.original-price {
    color: #999;
    font-size: 0.9rem;
    text-decoration: line-through;
}

.current-price {
    color: var(--restaurant-primary-color, #ff6b35);
    font-size: 1.2rem;
    font-weight: 700;
}

.item-actions {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 10px;
}

.add-to-cart-btn {
    background: var(--restaurant-primary-color, #ff6b35);
    color: white;
    width: 45px;
    height: 45px;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 1.2rem;
    cursor: pointer;
    transition: all 0.3s ease;
    box-shadow: 0 4px 15px rgba(255, 107, 53, 0.3);
}

.add-to-cart-btn:hover {
    background: var(--restaurant-secondary-color, #f7931e);
    transform: scale(1.1);
    box-shadow: 0 6px 20px rgba(255, 107, 53, 0.4);
}

/* 購物車浮動按鈕 */
.cart-float-btn {
    position: fixed;
    bottom: 30px;
    right: 30px;
    background: var(--restaurant-primary-color, #ff6b35);
    color: white;
    border-radius: 30px;
    padding: 15px 25px;
    display: flex;
    align-items: center;
    gap: 15px;
    cursor: pointer;
    box-shadow: 0 8px 30px rgba(255, 107, 53, 0.4);
    transition: all 0.3s ease;
    z-index: 1000;
}

.cart-float-btn:hover {
    background: var(--restaurant-secondary-color, #f7931e);
    transform: translateY(-3px);
    box-shadow: 0 12px 40px rgba(255, 107, 53, 0.5);
}

.cart-icon {
    position: relative;
}

.cart-badge {
    position: absolute;
    top: -8px;
    right: -8px;
    background: #e74c3c;
    color: white;
    border-radius: 50%;
    width: 22px;
    height: 22px;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 0.8rem;
    font-weight: 600;
}

.cart-total {
    font-weight: 600;
    font-size: 1.1rem;
}

.no-menu {
    text-align: center;
    color: #999;
    font-size: 1.2rem;
    margin: 60px 0;
}

/* 響應式設計 */
@media (max-width: 768px) {
    .menu-hero-banner {
        min-height: 60vh;
        padding: 60px 15px 40px;
    }

    .hero-title {
        font-size: 2.8rem;
        margin-bottom: 20px;
    }

    .hero-description {
        font-size: 1.2rem;
        margin-bottom: 30px;
    }

    .hero-stats {
        flex-direction: column;
        align-items: center;
        gap: 20px;
    }

    .sticky-nav-container {
        padding: 0 15px;
    }

    .nav-tabs {
        padding: 12px 0;
        gap: 6px;
    }

    .nav-tab {
        padding: 10px 16px;
        font-size: 0.9rem;
    }

    .menu-container {
        padding: 40px 15px;
    }

    .category-title {
        font-size: 2rem;
        margin-bottom: 30px;
    }

    .menu-grid {
        grid-template-columns: 1fr;
        gap: 20px;
    }

    .cart-float-btn {
        bottom: 20px;
        right: 15px;
        padding: 12px 20px;
        gap: 12px;
    }
}

@media (max-width: 480px) {
    .menu-hero-banner {
        min-height: 50vh;
        padding: 40px 10px 30px;
    }

    .hero-title {
        font-size: 2.2rem;
        letter-spacing: 1px;
    }

    .hero-description {
        font-size: 1rem;
        margin-bottom: 25px;
    }

    .nav-tabs {
        gap: 4px;
        padding: 10px 0;
    }

    .nav-tab {
        padding: 8px 12px;
        font-size: 0.85rem;
    }

    .tab-count {
        font-size: 0.75rem;
    }

    .menu-grid {
        grid-template-columns: 1fr;
        gap: 15px;
    }

    .menu-item {
        border-radius: 15px;
    }

    .item-content {
        padding: 20px;
    }

    .item-name {
        font-size: 1.2rem;
    }

    .category-title {
        font-size: 1.8rem;
    }
}

/* CSS變數定義 */
:root {
    --restaurant-primary-color: #ff6b35;
    --restaurant-secondary-color: #f7931e;
    --restaurant-primary-rgb: 255, 107, 53;
}
</style>