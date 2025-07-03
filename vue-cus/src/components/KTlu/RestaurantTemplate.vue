<template>
    <div class="restaurant-container restaurant-theme">
        <!-- 餐廳橫幅 -->
        <RestaurantBanner :restaurant="restaurant" />

        <!-- 餐廳名稱區域 -->
        <div class="restaurant-header">
            <h1 class="restaurant-main-title">{{ restaurant.name }}</h1>
        </div>

        <!-- TabMenu 區域 -->
        <div class="tab-menu-section restaurant-light-bg">
            <div class="tab-menu-container">
                <div class="tab-menu-tabs">
                    <button class="tab-menu-tab" :class="{ active: activeTab === 'reservation' }"
                        @click="activeTab = 'reservation'">
                        <i class="pi pi-calendar"></i>
                        <span>預約訂位</span>
                    </button>
                    <button class="tab-menu-tab" :class="{ active: activeTab === 'order' }"
                        @click="activeTab = 'order'">
                        <i class="pi pi-shopping-cart"></i>
                        <span>線上訂餐</span>
                    </button>
                </div>
            </div>
        </div>

        <!-- 餐廳資訊 -->
        <RestaurantInfo :restaurant="restaurant" />

        <!-- Tab 內容區域 -->
        <div class="tab-content-section">
            <!-- 預約訂位內容 -->
            <div v-if="activeTab === 'reservation'" class="tab-content" id="reservation-content">
                <ReservationForm :restaurant-id="restaurant.id.toString()" />
            </div>

            <!-- 線上訂餐內容 -->
            <div v-if="activeTab === 'order'" class="tab-content" id="order-content">
                <RestaurantMenu :restaurant="restaurant" @checkout="handleCheckout" />
            </div>
        </div>

        <!-- 地圖 -->
        <RestaurantMap :restaurant="restaurant" />

        <!-- 頁腳 -->
        <RestaurantFooter :restaurant="restaurant" />
    </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import RestaurantBanner from './RestaurantBanner.vue'
import RestaurantInfo from './RestaurantInfo.vue'
import RestaurantMenu from './RestaurantMenu.vue'
import RestaurantMap from './RestaurantMap.vue'
import ReservationForm from './ReservationForm.vue'
import RestaurantFooter from './RestaurantFooter.vue'
import '@/assets/css/restaurant-theme.css'

// 接收餐廳資料
defineProps({
    restaurant: {
        type: Object,
        required: true
    }
})

// Tab狀態管理
const activeTab = ref('order') // 預設顯示線上訂餐以展示Hero風格菜單導航

// 處理結帳
const handleCheckout = (orderData) => {
    console.log('處理結帳:', orderData)
    // 這裡可以添加結帳處理邏輯
    // 例如：跳轉到支付頁面、顯示結帳表單等
    alert(`訂單總計：NT$${orderData.totalAmount}\n正在處理訂單...`)
}

// IntersectionObserver 相關變量
let categoryObservers = []
let currentActiveCategory = null

// 創建觸發區域標示
const createTriggerIndicator = (categoryName, triggerTop) => {
    const triggerIndicator = document.createElement('div')
    triggerIndicator.className = 'trigger-indicator'
    triggerIndicator.dataset.category = categoryName
    triggerIndicator.style.cssText = `
        position: absolute;
        left: 0;
        right: 0;
        height: 4px;
        background: linear-gradient(90deg, #ff0000, #ff6b6b, #ff0000);
        z-index: 1000;
        pointer-events: none;
        opacity: 0.8;
        border-radius: 2px;
        box-shadow: 0 0 10px rgba(255, 0, 0, 0.5);
        top: ${triggerTop}px;
    `

    // 添加標籤文字
    const label = document.createElement('div')
    label.style.cssText = `
        position: absolute;
        top: -25px;
        left: 20px;
        background: #ff0000;
        color: white;
        padding: 2px 8px;
        border-radius: 4px;
        font-size: 12px;
        font-weight: bold;
        white-space: nowrap;
    `
    label.textContent = `觸發區域: ${categoryName}`
    triggerIndicator.appendChild(label)

    return triggerIndicator
}

// 更新觸發指示器狀態
const updateTriggerIndicator = (categoryName, isActive) => {
    const triggerIndicator = document.querySelector(`.trigger-indicator[data-category="${categoryName}"]`)
    if (triggerIndicator) {
        const label = triggerIndicator.querySelector('div')

        if (isActive) {
            triggerIndicator.style.background = 'linear-gradient(90deg, #00ff00, #00ff88, #00ff00)'
            triggerIndicator.style.boxShadow = '0 0 15px rgba(0, 255, 0, 0.8)'
            if (label) {
                label.style.background = '#00ff00'
                label.textContent = `✅ 當前觸發: ${categoryName}`
            }
        } else {
            triggerIndicator.style.background = 'linear-gradient(90deg, #ff0000, #ff6b6b, #ff0000)'
            triggerIndicator.style.boxShadow = '0 0 10px rgba(255, 0, 0, 0.5)'
            if (label) {
                label.style.background = '#ff0000'
                label.textContent = `觸發區域: ${categoryName}`
            }
        }
    }
}

// 設置 IntersectionObserver
const setupCategoryObservers = () => {
    // 清除之前的觀察器
    categoryObservers.forEach(observer => observer.disconnect())
    categoryObservers = []

    // 清除之前的觸發區域標示
    const existingTriggers = document.querySelectorAll('.trigger-indicator')
    existingTriggers.forEach(el => el.remove())

    // 獲取所有分類 section
    const categorySections = document.querySelectorAll('[id^="category-"]')

    categorySections.forEach((section, index) => {
        const h2Element = section.querySelector('.category-title')
        if (!h2Element) return

        const categoryName = h2Element.textContent.trim()
        const sectionTop = section.offsetTop
        const h2Top = sectionTop + h2Element.offsetTop
        const triggerTop = h2Top - 220 // H2 標題向上 220px

        // 獲取 header 高度
        const header = document.querySelector('header')
        const headerHeight = header ? header.offsetHeight : 100

        // 創建觸發區域標示（相對於 header 定位）
        const triggerIndicator = createTriggerIndicator(categoryName, triggerTop - headerHeight)
        document.body.appendChild(triggerIndicator)

        // 創建 IntersectionObserver
        const observer = new IntersectionObserver((entries) => {
            entries.forEach(entry => {
                const categoryName = entry.target.dataset.category

                if (entry.isIntersecting) {
                    // 分類進入 header navbar 下緣
                    console.log(`分類進入 header navbar 下緣: ${categoryName}`)
                    currentActiveCategory = categoryName
                    updateTriggerIndicator(categoryName, true)

                    // 更新其他分類為非活動狀態
                    categorySections.forEach(otherSection => {
                        const otherH2 = otherSection.querySelector('.category-title')
                        if (otherH2 && otherH2.textContent.trim() !== categoryName) {
                            updateTriggerIndicator(otherH2.textContent.trim(), false)
                        }
                    })
                } else {
                    // 分類離開 header navbar 下緣
                    console.log(`分類離開 header navbar 下緣: ${categoryName}`)
                    updateTriggerIndicator(categoryName, false)
                }
            })
        }, {
            root: header, // 使用 header 作為根元素
            rootMargin: '0px 0px 0px 0px', // 精確對齊 header 下緣
            threshold: 0.1 // 10% 可見時觸發
        })

        // 創建一個不可見的觸發元素（相對於 header 定位）
        const triggerElement = document.createElement('div')
        triggerElement.style.cssText = `
            position: absolute;
            top: ${triggerTop - headerHeight}px;
            left: 0;
            width: 100%;
            height: 1px;
            pointer-events: none;
            z-index: -1;
        `
        triggerElement.dataset.category = categoryName
        document.body.appendChild(triggerElement)

        // 觀察觸發元素
        observer.observe(triggerElement)
        categoryObservers.push(observer)

        console.log(`設置觀察器: ${categoryName}, 觸發位置: ${triggerTop - headerHeight}px (相對於header)`)
    })
}

// 監聽菜單載入
const watchMenuLoad = () => {
    // 使用 MutationObserver 監聽 DOM 變化
    const menuObserver = new MutationObserver((mutations) => {
        mutations.forEach((mutation) => {
            if (mutation.type === 'childList') {
                mutation.addedNodes.forEach((node) => {
                    if (node.nodeType === Node.ELEMENT_NODE) {
                        // 檢查是否有新的分類 section 被添加
                        const categorySections = node.querySelectorAll ? node.querySelectorAll('[id^="category-"]') : []
                        if (categorySections.length > 0 || node.matches && node.matches('[id^="category-"]')) {
                            console.log('檢測到新的分類 section，重新設置觀察器')
                            setTimeout(() => {
                                setupCategoryObservers()
                            }, 100)
                        }
                    }
                })
            }
        })
    })

    // 開始觀察
    menuObserver.observe(document.body, {
        childList: true,
        subtree: true
    })

    return menuObserver
}

// 生命周期
onMounted(() => {
    nextTick(() => {
        // 延遲設置，確保菜單組件已載入
        setTimeout(() => {
            setupCategoryObservers()
            watchMenuLoad()
        }, 1500)
    })
})

onUnmounted(() => {
    // 清除所有觀察器
    categoryObservers.forEach(observer => observer.disconnect())

    // 清除觸發區域標示
    const existingTriggers = document.querySelectorAll('.trigger-indicator')
    existingTriggers.forEach(el => el.remove())

    // 清除觸發元素
    const triggerElements = document.querySelectorAll('[data-category]')
    triggerElements.forEach(el => el.remove())
})
</script>

<style scoped>
.restaurant-container {
    max-width: 1200px;
    margin: 0 auto;
    padding: 0;
    background: var(--restaurant-bg-primary);
    box-shadow: 0 0 20px var(--restaurant-shadow-light);
}

.restaurant-header {
    text-align: left;
    padding: 2rem 1rem;
    background: var(--restaurant-gradient-subtle);
    width: 100%;
    margin: 0 0;
    border-bottom: 2px solid var(--restaurant-border-light);
}

.restaurant-main-title {
    font-size: 2.5rem;
    font-weight: 700;
    color: var(--restaurant-text-primary);
    margin: 0;
    letter-spacing: 1px;
    text-shadow: 0 1px 2px var(--restaurant-shadow-neutral);
}

/* TabMenu 樣式 - 優雅主題 */
.tab-menu-section {
    background: var(--restaurant-bg-secondary);
    padding: 1rem;
    border-bottom: 2px solid var(--restaurant-border-light);
}

.tab-menu-container {
    max-width: 600px;
    margin: 0 auto;
    text-align: center;
}

.tab-menu-tabs {
    display: inline-flex;
    background: var(--restaurant-bg-primary);
    border: 1px solid var(--restaurant-border-medium);
    border-radius: 8px;
    padding: 0.25rem;
    gap: 0.25rem;
    box-shadow: 0 2px 8px var(--restaurant-shadow-neutral);
}

.tab-menu-tab {
    display: flex;
    align-items: center;
    gap: 0.5rem;
    padding: 0.75rem 1.5rem;
    background: transparent;
    border: none;
    border-radius: 6px;
    cursor: pointer;
    transition: all 0.3s ease;
    font-size: 0.9rem;
    color: var(--restaurant-text-secondary);
    font-weight: 500;
}

.tab-menu-tab:hover {
    background: var(--restaurant-bg-accent);
    color: var(--restaurant-text-primary);
    transform: translateY(-1px);
}

.tab-menu-tab.active {
    background: var(--restaurant-text-secondary);
    color: var(--restaurant-bg-primary);
    box-shadow: 0 3px 12px var(--restaurant-shadow-medium);
    transform: translateY(-1px);
}

.tab-menu-tab i {
    font-size: 1.1rem;
}

.tab-menu-tab span {
    font-weight: 500;
}

/* Tab 內容區域 - 黃白主題 */
.tab-content-section {
    background: var(--restaurant-bg-primary);
    width: 100%;
    margin: 0;
    padding: 0;
    border-top: 1px solid var(--restaurant-border-light);
}

.tab-content {
    padding: 1.5rem 1rem;
    scroll-margin-top: 80px;
    background: var(--restaurant-bg-primary);
}

/* 響應式設計 */
@media (max-width: 768px) {
    .restaurant-main-title {
        font-size: 2rem;
    }

    .restaurant-header {
        padding: 1.5rem 1rem;
        width: 100%;
    }

    .tab-menu-section {
        padding: 0.75rem;
    }

    .tab-menu-title {
        font-size: 1.25rem;
    }

    .tab-menu-subtitle {
        font-size: 0.8rem;
    }

    .tab-menu-tabs {
        gap: 0.2rem;
        margin-top: 0.75rem;
    }

    .tab-menu-tab {
        padding: 0.65rem 1.25rem;
        font-size: 0.85rem;
    }

    .tab-menu-tab i {
        font-size: 1rem;
    }

    .tab-content {
        padding: 1.25rem 0.75rem;
    }

    .content-header h3 {
        font-size: 1.1rem;
    }

    .content-header p {
        font-size: 0.8rem;
    }
}

@media (max-width: 480px) {
    .restaurant-main-title {
        font-size: 1.75rem;
    }

    .restaurant-header {
        width: 100%;
    }

    .tab-menu-section {
        padding: 0.5rem;
    }

    .tab-menu-container {
        padding: 0;
    }

    .tab-menu-title {
        font-size: 1.15rem;
    }

    .tab-menu-subtitle {
        font-size: 0.75rem;
    }

    .tab-menu-tabs {
        margin-top: 0.75rem;
        padding: 0.2rem;
    }

    .tab-menu-tab {
        padding: 0.6rem 1rem;
        font-size: 0.8rem;
    }

    .tab-menu-tab i {
        font-size: 0.95rem;
    }

    .tab-content {
        padding: 1rem 0.5rem;
    }

    .content-header {
        padding: 0;
        margin-bottom: 0.75rem;
    }

    .content-header h3 {
        font-size: 1rem;
    }

    .content-header p {
        font-size: 0.75rem;
    }
}
</style>