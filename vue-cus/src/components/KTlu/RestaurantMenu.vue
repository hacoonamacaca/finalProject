<template>
    <div class="restaurant-menu restaurant-theme">
        <!-- 菜品內容區域 - 完全攤開顯示 -->
        <div class="menu-container" id="all-categories">
            <!-- Sticky分類導航區域 - 初始時在容器內部 -->
            <nav class="sticky-nav" ref="stickyNav">
                <div class="sticky-nav-container">
                    <div class="nav-tabs">
                        <a v-for="category in categories" :key="category.id"
                            :class="['nav-tab', { 'active': activeCategory === category.name }]"
                            :href="`#category-${category.id}`" @click="onTabClick($event, category)">
                            {{ category.name }}
                            <span class="tab-count">({{ getCategoryItems(category.name).length }})</span>
                        </a>
                        <a :class="['nav-tab', { 'active': activeCategory === 'all' }]" href="#all-categories"
                            @click="onTabClick($event, { name: 'all', id: 'all' })">
                            全部菜單
                            <span class="tab-count">({{ allItemsCount }})</span>
                        </a>
                    </div>
                </div>
            </nav>

            <main class="menu-main">
                <div v-if="hasMenuItems">
                    <!-- 顯示所有菜品，按分類分組 -->
                    <section v-for="category in categories" :key="category.id" :id="`category-${category.id}`"
                        class="category-section">
                        <h2 v-if="getCategoryItems(category.name).length > 0" class="category-title">
                            {{ category.name }}
                            <span class="category-count">({{ getCategoryItems(category.name).length }})</span>
                        </h2>
                        <div class="menu-grid" v-if="getCategoryItems(category.name).length > 0">
                            <div class="menu-item" v-for="item in getCategoryItems(category.name)" :key="item.id"
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

        <!-- 購物車浮動按鈕 -->
        <div class="cart-float-btn" v-if="totalCartQuantity > 0" @click="toggleCartVisibility">
            <div class="cart-icon">
                <i class="pi pi-shopping-cart"></i>
                <span class="cart-badge">{{ totalCartQuantity }}</span>
            </div>
            <div class="cart-total">NT${{ totalCartAmount }}</div>
        </div>

        <!-- 模態框 -->
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

// 導航狀態
const activeCategory = ref('人氣精選') // 初始設為第一個分類
const stickyNav = ref(null)

// Sticky navigation constants
const STICKY_TOP_POSITION = 100 // sticky nav固定時的top位置（與CSS一致）

// 分類和商品資料
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
const hasMenuItems = computed(() => {
    return items.value.length > 0
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

// 根據分類獲取商品
const getCategoryItems = (categoryName) => {
    return items.value.filter(item => item.category === categoryName)
}

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

// Tab點擊事件
const onTabClick = (event, category) => {
    event.preventDefault()

    // 如果點擊的是"全部菜單"，讓菜單內容上緣貼齊navbar下緣，不更新活動狀態
    if (category.name === 'all' || category.id === 'all') {
        const menuMain = document.querySelector('.menu-main')
        if (menuMain) {
            // 讓菜單內容的上緣剛好貼齊navbar下緣(100px)
            const scrollTop = menuMain.offsetTop - STICKY_TOP_POSITION
            window.scrollTo({
                top: Math.max(0, scrollTop),
                behavior: 'smooth'
            })
        }
        return // 不更新activeCategory，讓滾動追蹤自然處理
    }

    // 對於具體分類，更新活動分類並滾動到對應位置
    activeCategory.value = category.name
    const target = document.getElementById(`category-${category.id}`)
    if (target) {
        // 計算正確的滾動位置：分類section頂部 - sticky nav固定位置 - sticky nav高度 - 緩衝空間
        const stickyNavHeight = stickyNav.value ? stickyNav.value.offsetHeight : 60
        const scrollTop = target.offsetTop - STICKY_TOP_POSITION - stickyNavHeight - 10

        window.scrollTo({
            top: Math.max(0, scrollTop), // 確保不會滾動到負值
            behavior: 'smooth'
        })
    }
}

// Sticky導航檢測
const checkStickyNavPosition = () => {
    if (!stickyNav.value) return

    const scrollY = window.scrollY
    const menuContainer = document.querySelector('.menu-container')

    if (menuContainer) {
        // 當滾動位置超過菜單容器頂部時，變成sticky
        const menuTop = menuContainer.offsetTop

        if (scrollY >= menuTop - STICKY_TOP_POSITION) {
            stickyNav.value.classList.add('sticky-nav--fixed')
        } else {
            stickyNav.value.classList.remove('sticky-nav--fixed')
        }
    }
}

// 檢測當前可見分類
const checkActiveCategory = () => {
    if (!stickyNav.value) return

    // 獲取sticky-nav-container的位置
    const stickyNavContainer = stickyNav.value.querySelector('.sticky-nav-container')
    if (!stickyNavContainer) return

    const navRect = stickyNavContainer.getBoundingClientRect()

    // 獲取sticky nav下緣的絕對位置
    const navBottomPosition = navRect.bottom + window.scrollY

    // 使用nav下緣位置作為trigger，但提前一些距離來讓分類更早激活
    const triggerPosition = navBottomPosition - 50 // 提前50px觸發

    // 調試：印出所有分類section的絕對位置
    console.log('=== RestaurantMenu 位置調試信息 ===')
    console.log(`Sticky Nav 下緣絕對位置: ${navBottomPosition}px`)
    console.log(`Trigger 位置: ${triggerPosition}px (下緣-50px)`)
    console.log(`當前滾動位置: ${window.scrollY}px`)
    console.log(`Nav Rect Top: ${navRect.top}px, Bottom: ${navRect.bottom}px, Height: ${navRect.height}px`)
    console.log('=====================================')

    let newActiveCategory = null

    // 正向遍歷分類，找到trigger位置接觸到的分類
    for (let i = 0; i < categories.value.length; i++) {
        const category = categories.value[i]
        const element = document.getElementById(`category-${category.id}`)
        if (element) {
            const sectionTop = element.offsetTop
            const sectionBottom = sectionTop + element.offsetHeight

            // 找到該分類的 H2 標題元素
            const h2Element = element.querySelector('.category-title')
            let triggerTop = sectionTop // 預設為 section 頂部

            if (h2Element) {
                // 使用 H2 標題的上緣作為觸發點
                triggerTop = sectionTop + h2Element.offsetTop
            }

            // 如果trigger位置在這個section的範圍內（H2 標題向上 220px）
            if (triggerPosition >= triggerTop - 220 && triggerPosition <= sectionBottom - 170) {
                newActiveCategory = category.name
                break
            }
            // 如果trigger位置還沒到達這個section，但已經超過了前一個section
            else if (triggerPosition < triggerTop - 220) {
                // 如果是第一個分類，且trigger位置還沒到達，檢查距離
                if (i === 0) {
                    // 如果距離第一個分類很近（200px內），設為第一個分類
                    if (triggerTop - 220 - triggerPosition <= 200) {
                        newActiveCategory = category.name
                    } else {
                        newActiveCategory = null
                    }
                } else {
                    // 取前一個分類
                    newActiveCategory = categories.value[i - 1].name
                }
                break
            }
        }
    }

    // 如果沒有找到，檢查是否滾動到了最後面
    if (newActiveCategory === null && categories.value.length > 0) {
        const lastCategory = categories.value[categories.value.length - 1]
        const lastElement = document.getElementById(`category-${lastCategory.id}`)
        if (lastElement) {
            const lastH2Element = lastElement.querySelector('.category-title')
            let lastTriggerTop = lastElement.offsetTop

            if (lastH2Element) {
                lastTriggerTop = lastElement.offsetTop + lastH2Element.offsetTop
            }

            // 如果trigger位置已經過了最後一個分類的H2標題位置（H2 標題向上 220px）
            if (triggerPosition >= lastTriggerTop - 220) {
                newActiveCategory = lastCategory.name
            }
        }
    }

    // 更新活動分類
    if (newActiveCategory && activeCategory.value !== newActiveCategory) {
        activeCategory.value = newActiveCategory
    } else if (!newActiveCategory && activeCategory.value) {
        // 如果沒有任何分類應該是active的，清除active狀態
        activeCategory.value = null
    }
}

const onScroll = () => {
    checkStickyNavPosition()
    checkActiveCategory()
}

// 生命周期
onMounted(() => {
    // 組件掛載後的初始化
    console.log('餐廳菜單已載入，顯示所有菜品')

    nextTick(() => {
        window.addEventListener('scroll', onScroll, { passive: true })
        // 初始檢查活動分類
        checkActiveCategory()
    })
})

onUnmounted(() => {
    window.removeEventListener('scroll', onScroll)
})
</script>

<style scoped>
.restaurant-menu {
    background: #f8f9fa;
    min-height: 100vh;
    padding: 20px 0;
    /* 恢復原來的padding */
}

.menu-container {
    max-width: 1200px;
    margin: 20px auto 0;
    padding: 0 20px;
    position: relative;
    /* 為內部sticky nav提供定位參考 */
}

/* Sticky導航區域 - 初始時在容器內部 */
.sticky-nav {
    position: relative;
    /* 初始狀態：在容器內部 */
    top: 0;
    left: 0;
    right: 0;
    background: rgba(255, 255, 255, 0.95);
    backdrop-filter: blur(20px);
    border-bottom: 1px solid rgba(0, 0, 0, 0.1);
    z-index: 100;
    transition: all 0.3s ease;
    margin-bottom: 20px;
    border-radius: 12px 12px 0 0;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

/* Sticky狀態：固定在header下方 */
.sticky-nav--fixed {
    position: fixed !important;
    top: 100px;
    /* header高度 */
    left: 0;
    right: 0;
    margin: 0;
    border-radius: 0;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
    border-bottom: 2px solid var(--restaurant-primary, #ffba20);
}

/* 固定時分類按鈕攤開排列 */
.sticky-nav--fixed .nav-tabs {
    justify-content: space-between;
    /* 攤開排列 */
    flex-wrap: nowrap;
    /* 不換行 */
    overflow-x: visible;
    /* 顯示所有按鈕 */
    gap: 4px;
    /* 減小間距 */
}

.sticky-nav--fixed .nav-tab {
    flex: 1;
    /* 每個按鈕平分寬度 */
    max-width: none;
    /* 移除最大寬度限制 */
    text-align: center;
    /* 文字居中 */
    padding: 8px 12px;
    /* 調整內邊距 */
    font-size: 0.85rem;
    /* 稍微縮小字體 */
}

.sticky-nav-container {
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 20px;
}

.nav-tabs {
    display: flex;
    justify-content: center;
    align-items: center;
    gap: 8px;
    padding: 12px 0;
    flex-wrap: wrap;
    overflow-x: auto;
    scrollbar-width: none;
    -ms-overflow-style: none;
}

.nav-tabs::-webkit-scrollbar {
    display: none;
}

.nav-tab {
    display: inline-flex;
    align-items: center;
    gap: 8px;
    padding: 10px 20px;
    color: #666;
    text-decoration: none;
    font-weight: 500;
    font-size: 0.9rem;
    border-radius: 25px;
    transition: all 0.3s ease;
    white-space: nowrap;
    background: white;
    border: 2px solid #eee;
    min-width: fit-content;
}

.nav-tab:hover {
    background: rgba(255, 186, 32, 0.1);
    border-color: rgba(255, 186, 32, 0.3);
    color: var(--restaurant-primary, #ffba20);
    transform: translateY(-2px);
}

.nav-tab.active {
    background: var(--restaurant-primary, #ffba20);
    color: white;
    border-color: var(--restaurant-primary, #ffba20);
    font-weight: 600;
    box-shadow: 0 4px 15px rgba(255, 186, 32, 0.3);
}

.tab-count {
    font-size: 0.8rem;
    opacity: 0.8;
    font-weight: 400;
    background: rgba(255, 255, 255, 0.2);
    padding: 2px 6px;
    border-radius: 12px;
}

.nav-tab:not(.active) .tab-count {
    background: rgba(0, 0, 0, 0.1);
    color: #666;
}

.menu-main {
    background: white;
    border-radius: 0 0 12px 12px;
    /* 只有下方圓角，與sticky nav連接 */
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
    padding: 30px;
}

/* 當sticky nav固定時，調整menu-main的樣式 */
.sticky-nav--fixed+.menu-main,
.sticky-nav--fixed~* .menu-main {
    border-radius: 12px;
    /* 恢復完整圓角 */
    margin-top: 80px;
    /* 為固定的nav預留空間 */
}

.category-section {
    margin-bottom: 50px;
    scroll-margin-top: 320px;
    /* header高度 + sticky nav高度 + 緩衝 */
}

.category-section:last-child {
    margin-bottom: 0;
}

.category-title {
    font-size: 2rem;
    font-weight: 700;
    color: #333;
    margin-bottom: 30px;
    padding-bottom: 15px;
    border-bottom: 3px solid var(--restaurant-primary, #ffba20);
    display: flex;
    align-items: center;
    gap: 15px;
}

.category-count {
    font-size: 1.2rem;
    color: #666;
    font-weight: 500;
}

.menu-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
    gap: 25px;
}

.menu-item {
    background: white;
    border-radius: 12px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
    overflow: hidden;
    transition: all 0.3s ease;
    cursor: pointer;
    border: 1px solid #eee;
    position: relative;
    /* 為標籤提供定位參考 */
}

.item-image {
    position: relative;
    height: 200px;
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

.item-tags {
    position: absolute;
    top: 10px;
    left: 10px;
    z-index: 2;
    display: flex;
    gap: 5px;
    flex-wrap: wrap;
}

.item-tag {
    background: var(--restaurant-primary, #ffba20);
    color: white;
    padding: 4px 8px;
    border-radius: 12px;
    font-size: 0.75rem;
    font-weight: 500;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
}

.item-content {
    padding: 20px;
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
}

.item-info {
    flex: 1;
}

.item-name {
    font-size: 1.2rem;
    font-weight: 600;
    color: #333;
    margin-bottom: 8px;
}

.item-desc {
    color: #666;
    margin-bottom: 15px;
    line-height: 1.4;
    font-size: 0.9rem;
}

.price-section {
    display: flex;
    align-items: center;
    gap: 10px;
}

.original-price {
    text-decoration: line-through;
    color: #999;
    font-size: 0.9rem;
}

.current-price {
    font-size: 1.1rem;
    font-weight: 600;
    color: var(--restaurant-primary, #ffba20);
}

.item-actions {
    display: flex;
    align-items: center;
    margin-left: 15px;
}

.add-to-cart-btn {
    background: var(--restaurant-primary-color, #ff6b35);
    color: white;
    padding: 10px;
    border-radius: 50%;
    font-size: 1.1rem;
    cursor: pointer;
    transition: all 0.3s ease;
    display: flex;
    align-items: center;
    justify-content: center;
    width: 40px;
    height: 40px;
}

.add-to-cart-btn:hover {
    background: #e55a2b;
    transform: scale(1.1);
}

.cart-float-btn {
    position: fixed;
    bottom: 30px;
    right: 30px;
    background: var(--restaurant-primary-color, #ff6b35);
    color: white;
    border-radius: 50px;
    padding: 15px 25px;
    display: flex;
    align-items: center;
    gap: 15px;
    cursor: pointer;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.2);
    transition: all 0.3s ease;
    z-index: 1000;
}

.cart-float-btn:hover {
    background: #e55a2b;
    transform: scale(1.05);
}

.cart-icon {
    position: relative;
    font-size: 1.2rem;
}

.cart-badge {
    position: absolute;
    top: -8px;
    right: -8px;
    background: #ff4444;
    color: white;
    border-radius: 50%;
    padding: 2px 6px;
    font-size: 0.7rem;
    font-weight: 600;
    min-width: 18px;
    height: 18px;
    display: flex;
    align-items: center;
    justify-content: center;
}

.cart-total {
    font-weight: 600;
    font-size: 1.1rem;
}

.no-menu {
    text-align: center;
    padding: 60px 20px;
    color: #666;
    font-size: 1.2rem;
}

/* 響應式設計 */
@media (max-width: 768px) {
    .sticky-nav--fixed {
        top: 150px;
        /* 手機版header較小 */
    }

    .category-section {
        scroll-margin-top: 250px;
        /* 調整手機版滾動偏移 */
    }

    .nav-tabs {
        padding: 10px 0;
        gap: 6px;
    }

    .nav-tab {
        padding: 8px 16px;
        font-size: 0.85rem;
    }

    /* 平板版固定時攤開排列 */
    .sticky-nav--fixed .nav-tab {
        padding: 6px 8px;
        font-size: 0.8rem;
    }

    .menu-container {
        padding: 0 15px;
        margin-top: 15px;
    }

    .menu-main {
        padding: 20px;
    }

    .menu-grid {
        grid-template-columns: 1fr;
        gap: 20px;
    }

    .category-title {
        font-size: 1.5rem;
    }

    .cart-float-btn {
        bottom: 20px;
        right: 20px;
        padding: 12px 20px;
    }
}

@media (max-width: 480px) {
    .sticky-nav--fixed {
        top: 120px;
        /* 小螢幕header更小 */
    }

    .category-section {
        scroll-margin-top: 200px;
    }

    .nav-tabs {
        gap: 4px;
        padding: 8px 0;
    }

    .nav-tab {
        padding: 6px 12px;
        font-size: 0.8rem;
    }

    .tab-count {
        font-size: 0.7rem;
    }

    /* 小螢幕版固定時攤開排列 */
    .sticky-nav--fixed .nav-tab {
        padding: 4px 6px;
        font-size: 0.75rem;
    }
}
</style>