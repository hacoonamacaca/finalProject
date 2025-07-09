<template>
    <div class="restaurant-menu restaurant-theme rounded-3">
        <div class="menu-container" id="all-categories">
            <nav class="sticky-nav" ref="stickyNav">
                <div class="sticky-nav-container">
                    <button v-if="showScrollButtons" @click="scrollTabs('left')"
                        :class="['scroll-button', 'scroll-button--left', { 'hidden': !canScrollLeft }]">
                        ←
                    </button>

                    <div class="nav-tabs-wrapper" ref="tabsContainer">
                        <a v-for="category in categories" :key="category.id"
                            :class="['nav-tab', { 'active': activeCategory === category.name }]"
                            @click="onTabClick($event, category)">
                            {{ category.name }}
                            <span class="tab-count">({{ getCategoryItems(category.name).length }})</span>
                        </a>
                        <!-- <a :class="['nav-tab', { 'active': activeCategory === 'all' }]"
                            @click="onTabClick($event, { name: 'all', id: 'all' })">
                            全部菜單
                            <span class="tab-count">({{ allItemsCount }})</span>
                        </a> -->
                    </div>

                    <button v-if="showScrollButtons" @click="scrollTabs('right')"
                        :class="['scroll-button', 'scroll-button--right', { 'hidden': !canScrollRight }]">
                        →
                    </button>
                </div>

                <!-- 調試顯示器 -->
                <!-- <div class="debug-indicator"
                    style="position: absolute; top: -30px; right: 20px; background: #333; color: white; padding: 4px 8px; border-radius: 4px; font-size: 12px; z-index: 1000;">
                    當前活動: {{ activeCategory }}
                </div> -->
            </nav>

            <main class="menu-main">
                <div v-if="hasMenuItems">
                    <section v-for="category in categories" :key="category.id" :id="`category-${category.id}`"
                        class="category-section">
                        <h2 v-if="getCategoryItems(category.name).length > 0" class="category-title">
                            {{ category.name }}
                            <span class="category-count">({{ getCategoryItems(category.name).length }})</span>
                        </h2>
                        <div class="menu-grid" v-if="getCategoryItems(category.name).length > 0">
                            <div class="menu-item" v-for="item in getCategoryItems(category.name)" :key="item.id"
                                @click="openItemDetail(item)">
                                <!-- <div class="item-tags" v-if="item.tags && item.tags.length > 0">
                                    <span v-for="tag in item.tags" :key="tag" class="item-tag">{{ tag }}</span>
                                </div> -->

                                <div class="item-image">
                                    <img :src="item.imageResource || restaurant.image" :alt="item.name" />
                                </div>
                                <div class="item-content">
                                    <div class="item-info">
                                        <h5 class="item-name">{{ item.name }}</h5>
                                        <p class="item-desc">{{ item.description }}</p>
                                        <div class="price-section">
                                        
                                            <span class="current-price">NT${{ item.discountPrice || item.price }}</span>
                                        </div>
                                        <!-- <span v-if="item.originalPrice && item.originalPrice !== item.discountPrice" class="original-price">NT${{ item.originalPrice }} 暫時移除 --> 
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

        <ItemDetailModal v-if="showItemDetail" :item="selectedItem" :show="showItemDetail" @close="closeItemDetail"
            @add-to-cart="handleAddToCart" />

        <!-- <CartModal v-if="cartStore.isCartVisible" :cartItems="cartStore.cartItems" :totalAmount="cartStore.totalAmount"
            @close="cartStore.hideCart" @update-quantity="updateCartItemQuantity" @remove-item="removeCartItem"
            @checkout="checkout" /> -->
    </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, nextTick, watch } from 'vue'
import apiClient from '../../plungins/axios.js'; // 導入 apiClient
import ItemDetailModal from './ItemDetailModal.vue'
import CartModal from './CartModal.vue'
import { useCartStore } from '@/stores/cart.js'
import '@/assets/css/restaurant-theme.css'

const props = defineProps({
    restaurant: {
        type: Object,
        required: true
    }
})

const emit = defineEmits(['checkout'])

// 購物車 store
const cartStore = useCartStore()

// 基本狀態
const selectedItem = ref(null)
const showItemDetail = ref(false)

// 導航狀態
const activeCategory = ref(null) // 初始沒有Category
// const activeCategory = ref('人氣精選') // 初始設為第一個分類
const stickyNav = ref(null)
// 新增 ref 來引用可滾動的分類導航容器
const tabsContainer = ref(null);
// 控制滾動按鈕的顯示/隱藏狀態
const showScrollButtons = ref(false);
// 控制左右按鈕的禁用狀態
const canScrollLeft = ref(false);
const canScrollRight = ref(false);

// 監控 activeCategory 變化
watch(activeCategory, (newValue, oldValue) => {
    console.log(`🎯 RestaurantMenu activeCategory 變化: ${oldValue} → ${newValue}`)
}, { immediate: true })

// Intersection Observer 相關
let observers = [] // 儲存所有的 Intersection Observers

// Sticky navigation constants
const STICKY_TOP_POSITION = 100 // sticky nav固定時的top位置（與CSS一致）

// 分類和商品資料 (移除假資料)
const categories = ref([]) //將用來存放FoodClassDTO

const items = ref([]) //將用來存放FoodDTO

// 計算屬性
const hasMenuItems = computed(() => {
    return items.value.length > 0
})



const allItemsCount = computed(() => {
    return items.value.length
})

// 根據分類獲取商品
const getCategoryItems = (categoryName) => {
    return items.value.filter(item => item.categoryName === categoryName)
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
    cartStore.addToCart(itemToAdd, props.restaurant)

    if (showItemDetail.value) {
        closeItemDetail()
    }

    // 只在購物車未開啟時才開啟
    if (!cartStore.isCartVisible) {
        cartStore.showCart()
    }
}

const updateCartItemQuantity = (itemId, newQuantity) => {
    cartStore.updateQuantity(itemId, newQuantity, props.restaurant.id)
}

const removeCartItem = (itemId) => {
    cartStore.removeItem(itemId, props.restaurant.id)
}



const checkout = () => {
    const orderData = cartStore.checkoutSingleRestaurant(props.restaurant.id)
    if (orderData) {
        emit('checkout', orderData)
        cartStore.hideCart()
    }
}

// 滾動方法
const scrollTabs = (direction) => {
    console.log(`🔄 滾動按鈕點擊: ${direction}`)
    if (tabsContainer.value) {
        const scrollAmount = 150; // 每次滾動的像素量，可調整
        const currentScrollLeft = tabsContainer.value.scrollLeft;
        const newScrollLeft = direction === 'left' ? currentScrollLeft - scrollAmount : currentScrollLeft + scrollAmount;

        console.log(`📊 滾動前: ${currentScrollLeft}, 滾動後: ${newScrollLeft}`)

        tabsContainer.value.scrollTo({
            left: newScrollLeft,
            behavior: 'smooth'
        });

        // 滾動完成後重新檢查按鈕狀態
        setTimeout(() => {
            checkScrollButtonVisibility();
        }, 300);
    } else {
        console.error('❌ tabsContainer 未找到')
    }
};

// 檢查滾動按鈕可見性及禁用狀態
const checkScrollButtonVisibility = () => {
    console.log('🔍 檢查滾動按鈕可見性...')
    if (tabsContainer.value) {
        const { scrollWidth, clientWidth, scrollLeft } = tabsContainer.value;
        const shouldShowButtons = scrollWidth > clientWidth;
        const canScrollLeftNow = scrollLeft > 0;
        const canScrollRightNow = scrollLeft + clientWidth < scrollWidth;

        console.log(`📊 滾動容器狀態:`, {
            scrollWidth,
            clientWidth,
            scrollLeft,
            shouldShowButtons,
            canScrollLeftNow,
            canScrollRightNow
        })

        showScrollButtons.value = shouldShowButtons;
        canScrollLeft.value = canScrollLeftNow;
        canScrollRight.value = canScrollRightNow;

        console.log(`🎯 按鈕狀態:`, {
            showScrollButtons: showScrollButtons.value,
            canScrollLeft: canScrollLeft.value,
            canScrollRight: canScrollRight.value
        })
    } else {
        console.error('❌ tabsContainer 未找到，無法檢查滾動按鈕')
    }
};

// Tab點擊事件 (更新為使用 scrollIntoView)
const onTabClick = (event, category) => {
    event.preventDefault()

    if (category.name === 'all' || category.id === 'all') {
        const menuMain = document.querySelector('.menu-main')
        if (menuMain) {
            window.scrollTo({
                top: menuMain.offsetTop - STICKY_TOP_POSITION,
                behavior: 'smooth'
            })
        }
        return
    }

    const target = document.getElementById(`category-${category.id}`)
    if (target) {
        const yOffset = - (STICKY_TOP_POSITION + (stickyNav.value ? stickyNav.value.offsetHeight : 60) + 10);
        const y = target.getBoundingClientRect().top + window.scrollY + yOffset;

        window.scrollTo({ top: y, behavior: 'smooth' });

        activeCategory.value = category.name;

        // 點擊後，將當前點擊的 tab 滾動到 tabsContainer 的中心（可選）
        nextTick(() => {
            const activeTab = tabsContainer.value.querySelector('.nav-tab.active');
            if (activeTab && tabsContainer.value) {
                // 計算需要滾動的距離，讓 activeTab 盡量居中
                const tabOffsetLeft = activeTab.offsetLeft;
                const tabWidth = activeTab.offsetWidth;
                const containerWidth = tabsContainer.value.clientWidth;
                const scrollLeft = tabOffsetLeft - (containerWidth / 2) + (tabWidth / 2);

                tabsContainer.value.scrollTo({
                    left: scrollLeft,
                    behavior: 'smooth'
                });
            }
        });
    }
}

// 備用滾動檢測機制
const checkActiveCategoryOnScroll = () => {
    const scrollY = window.scrollY;
    const menuContainerTop = document.querySelector('.menu-container')?.offsetTop || 0;
    const stickyNavHeight = stickyNav.value ? stickyNav.value.offsetHeight : 60;
    const triggerOffset = STICKY_TOP_POSITION + stickyNavHeight;

    // 檢查是否在頂部
    const isAtTop = scrollY < menuContainerTop - STICKY_TOP_POSITION + 50;
    if (isAtTop && categories.value.length > 0) {
        const firstCategory = categories.value[0].name;
        if (activeCategory.value !== firstCategory) {
            console.log(`🔄 滾動檢測 - 頁面頂部，設置第一個分類: ${firstCategory}`)
            activeCategory.value = firstCategory;
        }
        return;
    }

    // 檢查每個分類的位置
    let currentActiveCategory = null;
    let minDistance = Infinity;

    categories.value.forEach(category => {
        const element = document.getElementById(`category-${category.id}`);
        if (element) {
            const rect = element.getBoundingClientRect();
            const distance = Math.abs(rect.top - triggerOffset);

            // 如果分類標題在觸發線附近，選擇距離最小的
            if (rect.top <= triggerOffset + 50 && rect.bottom >= triggerOffset - 50) {
                if (distance < minDistance) {
                    minDistance = distance;
                    currentActiveCategory = category.name;
                }
            }
        }
    });

    if (currentActiveCategory && activeCategory.value !== currentActiveCategory) {
        console.log(`🔄 滾動檢測 - 更新 activeCategory: ${activeCategory.value} → ${currentActiveCategory}`)
        activeCategory.value = currentActiveCategory;
    }
};

// Sticky導航檢測 (保持不變)
const checkStickyNavPosition = () => {
    if (!stickyNav.value) return

    const scrollY = window.scrollY
    const menuContainer = document.querySelector('.menu-container')

    if (menuContainer) {
        const menuTop = menuContainer.offsetTop

        if (scrollY >= menuTop - STICKY_TOP_POSITION) {
            stickyNav.value.classList.add('sticky-nav--fixed')
        } else {
            stickyNav.value.classList.remove('sticky-nav--fixed')
        }
    }

    // 同時檢查 active category
    checkActiveCategoryOnScroll();
}

// =======================================================
// Intersection Observer 實現高亮判斷
// =======================================================
const setupIntersectionObserver = () => {
    console.log('🔄 RestaurantMenu: 設置 IntersectionObserver')

    observers.forEach(observer => observer.disconnect());
    observers = [];

    // 計算觸發位置：sticky nav 底部位置
    const stickyNavHeight = stickyNav.value ? stickyNav.value.offsetHeight : 60;
    const triggerOffset = STICKY_TOP_POSITION + stickyNavHeight;

    console.log(`📊 觸發位置計算: STICKY_TOP_POSITION=${STICKY_TOP_POSITION}, stickyNavHeight=${stickyNavHeight}, triggerOffset=${triggerOffset}`)

    const observerOptions = {
        root: null,
        rootMargin: `-${triggerOffset}px 0px 0px 0px`, // 只考慮頂部觸發
        threshold: 0.1 // 10% 可見時觸發
    };

    const observer = new IntersectionObserver((entries) => {
        console.log('👁️ IntersectionObserver 觸發，entries:', entries.length)

        // 找到所有正在相交的元素
        const intersectingEntries = entries.filter(entry => entry.isIntersecting);
        console.log(`📊 相交的元素數量: ${intersectingEntries.length}`)

        if (intersectingEntries.length === 0) {
            // 沒有元素相交，檢查是否在頂部
            const menuContainerTop = document.querySelector('.menu-container')?.offsetTop || 0;
            const scrollY = window.scrollY;
            const isAtTop = scrollY < menuContainerTop - STICKY_TOP_POSITION + 50;

            if (isAtTop && categories.value.length > 0) {
                const firstCategory = categories.value[0].name;
                if (activeCategory.value !== firstCategory) {
                    console.log(`🏠 頁面頂部，設置第一個分類: ${firstCategory}`)
                    activeCategory.value = firstCategory;
                }
            }
            return;
        }

        // 找到最靠近頂部的相交元素
        let closestEntry = null;
        let minTop = Infinity;

        intersectingEntries.forEach(entry => {
            const top = entry.boundingClientRect.top;
            console.log(`📊 檢查分類: ${entry.target.id}, top: ${top}`)

            if (top < minTop) {
                minTop = top;
                closestEntry = entry;
            }
        });

        if (closestEntry) {
            const categoryId = closestEntry.target.id.replace('category-', '');
            const newActiveCategory = categories.value.find(cat => cat.id === categoryId)?.name;

            console.log(`🎯 最靠近頂部的分類: ${newActiveCategory}, top: ${minTop}`)

            if (newActiveCategory && activeCategory.value !== newActiveCategory) {
                console.log(`🎯 更新 activeCategory: ${activeCategory.value} → ${newActiveCategory}`)
                activeCategory.value = newActiveCategory;
            }
        }
    }, observerOptions);

    // 觀察所有分類區塊
    categories.value.forEach(category => {
        const element = document.getElementById(`category-${category.id}`);
        if (element) {
            observer.observe(element);
            console.log(`👁️ 觀察分類: ${category.name} (ID: ${category.id})`)
        } else {
            console.error(`❌ 找不到分類元素: category-${category.id}`)
        }
    });

    observers.push(observer);
    console.log('✅ IntersectionObserver 設置完成')
};

// 生命周期
onMounted(async() => {
    console.log('🏪 餐廳菜單已載入，顯示所有菜品');
        
    try {
        // 獲取當前店家的 ID
        const storeId = props.restaurant.id;
        if (!storeId) {
            throw new Error("店家 ID 未提供！");
        }

        // 使用 Promise.all 來並行發送兩個 API 請求，提升效能
        console.log(`🔄 開始為店家 ID: ${storeId} 獲取菜單資料...`);
        const [categoriesResponse, itemsResponse] = await Promise.all([
            apiClient.get(`/api/food-classes/store/${storeId}`),
            apiClient.get(`/api/foods/store/${storeId}`)
        ]);

        // 將從後端獲取的資料，賦值給 ref
        categories.value = categoriesResponse.data;
        items.value = itemsResponse.data;

        // 如果有分類，將第一個分類設為預設 active
        if (categories.value.length > 0) {
            activeCategory.value = categories.value[0].name;
        }

        console.log("✅ 成功載入店家分類:", categories.value);
        console.log("✅ 成功載入店家菜單:", items.value);

    } catch (error) {
        console.error("❌ 載入菜單資料失敗:", error);
        categories.value = [];
        items.value = [];
    }

    // 使用 await nextTick() 來確保 v-for 已經渲染完畢
    await nextTick(() => {
        console.log('🎨 DOM 已根據新資料更新完畢。');
        console.log('🔄 開始初始化組件...')

        // 延遲設置 IntersectionObserver，確保 DOM 完全渲染
        setTimeout(() => {
            console.log('⏰ 延遲設置 IntersectionObserver...')
            setupIntersectionObserver();
        }, 500);

        window.addEventListener('scroll', checkStickyNavPosition, { passive: true });
        checkStickyNavPosition(); // 初始檢查 sticky nav 狀態

        // 監聽 tabsContainer 自身滾動事件，以更新按鈕禁用狀態
        if (tabsContainer.value) {
            console.log('✅ tabsContainer 找到，設置滾動監聽器')
            tabsContainer.value.addEventListener('scroll', checkScrollButtonVisibility, { passive: true });
        } else {
            console.error('❌ tabsContainer 未找到')
        }

        // 延遲檢查滾動按鈕可見性，確保 DOM 完全渲染
        setTimeout(() => {
            console.log('⏰ 延遲檢查滾動按鈕可見性...')
            checkScrollButtonVisibility();
        }, 100);

        // 多次強制檢查，確保按鈕狀態正確
        setTimeout(() => {
            forceCheckScrollButtons();
        }, 300);

        setTimeout(() => {
            forceCheckScrollButtons();
        }, 500);

        setTimeout(() => {
            forceCheckScrollButtons();
        }, 1000);

        // 監聽窗口大小變化，當佈局變化時重新檢查按鈕可見性
        window.addEventListener('resize', () => {
            console.log('📱 窗口大小變化，重新檢查滾動按鈕')
            setTimeout(checkScrollButtonVisibility, 100);
        });

        console.log('✅ 組件初始化完成')
    });
})

onUnmounted(() => {
    window.removeEventListener('scroll', checkStickyNavPosition);
    window.removeEventListener('resize', checkScrollButtonVisibility);
    if (tabsContainer.value) {
        tabsContainer.value.removeEventListener('scroll', checkScrollButtonVisibility);
    }
    observers.forEach(observer => observer.disconnect());
})
</script>

<style scoped>
/* 保持原有的 .restaurant-menu, .menu-container, .sticky-nav 樣式 */
.restaurant-menu {
    background: #f8f9fa;
    min-height: 100vh;
    padding: 20px 0; 
}

.menu-container {
    max-width: 1200px;
    margin: 20px auto 0;
    padding: 0 20px;
    position: relative;
}

.sticky-nav {
    position: relative;
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

/* 固定時的 sticky-nav 樣式 (保持你修改後的) */
.sticky-nav--fixed {
    position: fixed !important;
    top: 100px;
    left: 0;
    right: 0;
    margin: 0;
    border-radius: 0;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
    border-bottom: 2px solid #ffc933;
    /* 醒目的橙紅色粗線，代表 navBottomPosition */
    z-index: 100;
}

/* 調整 sticky-nav-container 為 flex 容器，以容納按鈕和滾動區域 */
.sticky-nav-container {
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 20px;
    display: flex;
    align-items: center;
    justify-content: space-between;
    height: 100%;
    position: relative;
    /* 確保子元素定位正確 */
}

/* 新增或修改此部分為滾動容器 */
.nav-tabs-wrapper {
    display: flex;
    flex-wrap: nowrap;
    overflow-x: auto;
    -webkit-overflow-scrolling: touch;
    scroll-behavior: smooth;
    flex-grow: 1;
    scrollbar-width: none;
    -ms-overflow-style: none;
    padding: 12px 0;
    margin: 0 10px;
    /* 為按鈕留出空間 */
}

/* 隱藏 Chrome/Safari/Opera 上的滾動條 */
.nav-tabs-wrapper::-webkit-scrollbar {
    display: none;
}

/* 原有的 .nav-tabs 可以移除，或者將其改名為 .nav-tabs-wrapper */
/* 因為我這裡已經把 nav-tabs 改名為 nav-tabs-wrapper 了 */
/* 並且將 justify-content: center 移除，因為按鈕會影響居中 */
/* 如果你需要 nav-tab 本身依然居中，可以在 nav-tabs-wrapper 內部使用 padding 調整 */


.nav-tab {
    flex-shrink: 0;
    /* 防止導航項被縮小 */
    display: inline-flex;
    align-items: center;
    gap: 8px;
    padding: 10px 20px;
    margin-right: 8px;
    /* 項目之間增加間距 */
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
    cursor: pointer;
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

/* 滾動按鈕的樣式 */
.scroll-button {
    background: rgba(255, 255, 255, 0.95);
    border: 2px solid #ddd;
    border-radius: 50%;
    width: 40px;
    height: 40px;
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    font-size: 18px;
    font-weight: bold;
    color: #555;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
    z-index: 10;
    transition: all 0.3s ease;
    flex-shrink: 0;
    /* 防止按鈕被壓縮 */
    position: relative;
    /* 確保按鈕在正確位置 */
}

.scroll-button:hover {
    background: #f0f0f0;
    transform: scale(1.05);
    border-color: #ccc;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
}

.scroll-button:active {
    transform: scale(0.98);
}

.scroll-button.hidden {
    opacity: 0.3;
    pointer-events: none;
    /* 禁用點擊事件 */
    cursor: not-allowed;
}

/* 調整 sticky-nav--fixed 狀態下的樣式 */
.sticky-nav--fixed .sticky-nav-container {
    padding: 0 20px;
    /* 確保左右有足夠空間給按鈕 */
}

/* 移除固定時的 flex-wrap: nowrap 和 overflow-x: visible
   因為 .nav-tabs-wrapper 已經處理了這些 */
.sticky-nav--fixed .nav-tabs-wrapper {
    justify-content: flex-start;
    /* 保持內容左對齊 */
    gap: 8px;
    /* 保持間距，或根據需要調整 */
    padding: 12px 0;
    /* 確保有 padding */
}

.sticky-nav--fixed .nav-tab {
    flex: none;
    /* 移除 flex: 1，讓按鈕保持其內容寬度 */
    max-width: none;
    text-align: center;
    padding: 8px 12px;
    font-size: 0.85rem;
    margin-right: 0;
    /* 移除額外的 margin */
}

/* 保持原有的 .menu-main, .category-section 等樣式 */
.menu-main {
    background: white;
    border-radius: 0 0 12px 12px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
    padding: 30px;
}

.sticky-nav--fixed+.menu-main,
.sticky-nav--fixed~* .menu-main {
    border-radius: 12px;
    margin-top: 80px;
}

.category-section {
    margin-bottom: 50px;
    scroll-margin-top: 320px;
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
    }

    .category-section {
        scroll-margin-top: 250px;
    }

    /* 在小螢幕下，導航按鈕和滾動容器的佈局保持一致 */
    .sticky-nav-container {
        padding: 0 10px;
        /* 減小左右 padding */
    }

    .scroll-button {
        width: 32px;
        height: 32px;
        font-size: 0.9rem;
    }

    .nav-tabs-wrapper {
        padding: 10px 0;
        gap: 6px;
    }

    .nav-tab {
        padding: 8px 16px;
        font-size: 0.85rem;
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


}

@media (max-width: 480px) {
    .sticky-nav--fixed {
        top: 120px;
    }

    .category-section {
        scroll-margin-top: 200px;
    }

    .sticky-nav-container {
        padding: 0 5px;
        /* 進一步減小左右 padding */
    }

    .scroll-button {
        width: 28px;
        height: 28px;
        font-size: 0.8rem;
    }

    .nav-tabs-wrapper {
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
}
</style>
