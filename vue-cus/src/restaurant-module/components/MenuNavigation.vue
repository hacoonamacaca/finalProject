<template>
    <div class="menu-navigation">
        <!-- Hero風格的分類導航 -->
        <section class="menu-hero-tabs" v-if="categories.length > 1">
            <h1 class="hero-title">{{ restaurant?.name || '餐廳菜單' }}</h1>

            <div class="menu-hero-tabs-container" ref="tabsContainer">
                <a v-for="category in categories" :key="category.id"
                    :class="['menu-hero-tab', { 'active': activeCategory === category.name }]"
                    :href="`#category-${category.id}`" @click="onTabClick($event, category)">
                    {{ category.name }}
                    <span class="category-count">({{ category.count }})</span>
                </a>
                <span class="menu-hero-tab-slider" ref="tabSlider"></span>
            </div>
        </section>

        <!-- 如果分類少於2個的提示 -->
        <div v-else class="insufficient-categories">
            ⚠️ 分類數量不足: {{ categories.length }} 個（需要至少2個分類才顯示導航）
        </div>
    </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick } from 'vue'

// Props
const props = defineProps({
    categories: {
        type: Array,
        required: true
    },
    activeCategory: {
        type: String,
        default: ''
    },
    restaurant: {
        type: Object,
        default: () => ({})
    }
})

// Emits
const emit = defineEmits(['update:activeCategory', 'categoryClick'])

// Refs
const tabsContainer = ref(null)
const tabSlider = ref(null)
const tabContainerHeight = 70

// State
let currentId = null
let currentTab = null

// Methods
const onTabClick = (event, category) => {
    event.preventDefault()
    const target = document.getElementById(`category-${category.id}`)
    if (target) {
        const scrollTop = target.offsetTop - tabContainerHeight + 1
        window.scrollTo({
            top: scrollTop,
            behavior: 'smooth'
        })

        // 更新活動分類
        emit('update:activeCategory', category.name)
        emit('categoryClick', category)
    }
}

const checkTabContainerPosition = () => {
    if (!tabsContainer.value) return

    const heroTabs = document.querySelector('.menu-hero-tabs')
    if (heroTabs) {
        const offset = heroTabs.offsetTop + heroTabs.offsetHeight - tabContainerHeight
        const currentScrollY = window.scrollY

        if (currentScrollY > offset) {
            if (!tabsContainer.value.classList.contains('menu-hero-tabs-container--top')) {
                tabsContainer.value.classList.add('menu-hero-tabs-container--top')
                setTimeout(() => {
                    setSliderCss()
                }, 50)
            }
        } else {
            if (tabsContainer.value.classList.contains('menu-hero-tabs-container--top')) {
                tabsContainer.value.classList.remove('menu-hero-tabs-container--top')
                setTimeout(() => {
                    setSliderCss()
                }, 50)
            }
        }
    }
}

const findCurrentTabSelector = () => {
    let newCurrentId = null
    let newCurrentTab = null

    const tabs = tabsContainer.value?.querySelectorAll('.menu-hero-tab')
    if (!tabs) return

    tabs.forEach(tab => {
        const id = tab.getAttribute('href')
        const element = document.querySelector(id)
        if (element) {
            const offsetTop = element.offsetTop - tabContainerHeight
            const offsetBottom = element.offsetTop + element.offsetHeight - tabContainerHeight
            const currentScrollY = window.scrollY

            if (currentScrollY > offsetTop && currentScrollY < offsetBottom) {
                newCurrentId = id
                newCurrentTab = tab
            }
        }
    })

    if (currentId !== newCurrentId || currentId === null) {
        currentId = newCurrentId
        currentTab = newCurrentTab

        // 更新活動分類
        if (newCurrentTab) {
            const categoryName = newCurrentTab.textContent.replace(/\(\d+\)/, '').trim()
            if (props.activeCategory !== categoryName) {
                emit('update:activeCategory', categoryName)
            }
        }

        setSliderCss()
    }
}

const setSliderCss = () => {
    if (!tabSlider.value || !tabsContainer.value) return

    let width = 0
    let left = 0

    if (currentTab) {
        const rect = currentTab.getBoundingClientRect()
        const containerRect = tabsContainer.value.getBoundingClientRect()

        width = rect.width
        left = rect.left - containerRect.left
    }

    tabSlider.value.style.width = `${width}px`
    tabSlider.value.style.left = `${left}px`
    tabSlider.value.style.opacity = width > 0 ? '1' : '0'
}

const onScroll = () => {
    checkTabContainerPosition()
    findCurrentTabSelector()
}

const onResize = () => {
    if (currentId) {
        setSliderCss()
    }
}

// 生命周期
onMounted(() => {
    // 初始化滑塊位置
    nextTick(() => {
        if (props.activeCategory) {
            // 找到當前活動的tab
            const activeTab = tabsContainer.value?.querySelector(`.menu-hero-tab[href*="${props.activeCategory}"]`)
            if (activeTab) {
                currentTab = activeTab
                setSliderCss()
            }
        }

        // 檢查初始位置
        checkTabContainerPosition()
    })

    // 設置事件監聽
    window.addEventListener('scroll', onScroll, { passive: true })
    window.addEventListener('resize', onResize)
})

onUnmounted(() => {
    window.removeEventListener('scroll', onScroll)
    window.removeEventListener('resize', onResize)
})

// 暴露方法給父組件
defineExpose({
    setSliderCss,
    checkTabContainerPosition,
    findCurrentTabSelector
})
</script>

<style scoped>
.menu-navigation {
    width: 100%;
}

/* Hero風格的分類導航 - 仿照et-hero-tabs */
.menu-hero-tabs {
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    height: 60vh;
    position: relative;
    background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
    text-align: center;
    padding: 0 2rem;
    margin-bottom: 2rem;
}

.hero-title {
    font-size: 2.5rem;
    margin: 0;
    letter-spacing: 0.5rem;
    color: var(--restaurant-text-primary, #333);
    font-weight: 300;
}

.hero-subtitle {
    font-size: 1.1rem;
    letter-spacing: 0.2rem;
    opacity: 0.7;
    color: var(--restaurant-text-secondary, #666);
    margin: 1rem 0 2rem 0;
    font-weight: 300;
}

/* 導航容器 - 仿照et-hero-tabs-container */
.menu-hero-tabs-container {
    display: flex;
    flex-direction: row;
    position: absolute;
    bottom: 0;
    width: 100%;
    height: 70px;
    box-shadow: 0 0 20px rgba(0, 0, 0, 0.1);
    background: #ffffff;
    z-index: 10;
    border-radius: 12px 12px 0 0;
    overflow: hidden;
    transition: all 0.3s ease;
}

/* 固定在頂部時的樣式 */
.menu-hero-tabs-container--top {
    position: fixed !important;
    top: 0 !important;
    left: 50% !important;
    transform: translateX(-50%) !important;
    border-radius: 0 0 12px 12px !important;
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.15) !important;
    background: rgba(255, 255, 255, 0.98) !important;
    backdrop-filter: blur(20px) !important;
    -webkit-backdrop-filter: blur(20px) !important;
    animation: slideDownFromTop 0.3s ease-out !important;
    z-index: 1000 !important;

    /* 關鍵：與restaurant容器相同的寬度 */
    width: min(1200px, calc(100vw - 2rem)) !important;
    max-width: 1200px !important;
}

/* 為固定導航添加頂部邊框指示器 */
.menu-hero-tabs-container--top::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    height: 3px;
    background: linear-gradient(90deg, var(--restaurant-primary, #ffba20), #66B1F1);
}

@keyframes slideDownFromTop {
    from {
        transform: translateX(-50%) translateY(-100%);
        opacity: 0;
    }

    to {
        transform: translateX(-50%) translateY(0);
        opacity: 1;
    }
}

/* Tab樣式 - 仿照et-hero-tab */
.menu-hero-tab {
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    flex: 1;
    color: var(--restaurant-text-secondary, #333);
    text-decoration: none;
    letter-spacing: 0.1rem;
    transition: all 0.5s ease;
    font-size: 0.9rem;
    font-weight: 500;
    padding: 0.5rem;
    position: relative;
    cursor: pointer;
    min-height: 60px;
}

.menu-hero-tab:hover {
    color: white;
    background: rgba(102, 177, 241, 0.8);
    transition: all 0.5s ease;
    transform: translateY(-2px);
}

.menu-hero-tab.active {
    color: var(--restaurant-primary, #ffba20);
    font-weight: 600;
    background: rgba(255, 186, 32, 0.1);
}

.category-count {
    font-size: 0.75rem;
    opacity: 0.7;
    margin-top: 0.25rem;
    transition: opacity 0.3s ease;
}

.menu-hero-tab.active .category-count {
    opacity: 1;
    font-weight: 600;
}

/* 滑塊樣式 - 仿照et-hero-tab-slider */
.menu-hero-tab-slider {
    position: absolute;
    bottom: 0;
    width: 0;
    height: 6px;
    background: #66B1F1;
    transition: left 0.3s ease, width 0.3s ease;
    opacity: 0;
    border-radius: 3px 3px 0 0;
}

/* 分類不足提示 */
.insufficient-categories {
    text-align: center;
    padding: 2rem;
    background: #fff3cd;
    border-radius: 12px;
    color: #856404;
    margin: 2rem 0;
    border: 1px solid #ffeaa7;
}

/* 響應式設計 */
@media (min-width: 800px) {
    .hero-title {
        font-size: 3rem;
    }

    .hero-subtitle {
        font-size: 1.2rem;
    }

    .menu-hero-tab {
        font-size: 1rem;
    }
}

@media (max-width: 768px) {
    .menu-hero-tabs {
        height: 50vh;
        padding: 0 1rem;
    }

    .hero-title {
        font-size: 2rem;
        letter-spacing: 0.3rem;
    }

    .hero-subtitle {
        font-size: 1rem;
        letter-spacing: 0.1rem;
    }

    .menu-hero-tabs-container--top {
        width: calc(100vw - 1rem) !important;
        height: 60px !important;
    }

    .menu-hero-tab {
        font-size: 0.8rem;
        padding: 0.25rem;
    }

    .category-count {
        font-size: 0.7rem;
    }
}

@media (max-width: 480px) {
    .menu-hero-tabs {
        height: 40vh;
        padding: 0 0.5rem;
    }

    .hero-title {
        font-size: 1.5rem;
        letter-spacing: 0.2rem;
    }

    .hero-subtitle {
        font-size: 0.9rem;
    }

    .menu-hero-tabs-container--top {
        width: calc(100vw - 0.5rem) !important;
        height: 55px !important;
    }

    .menu-hero-tab {
        font-size: 0.75rem;
        padding: 0.2rem;
    }

    .category-count {
        font-size: 0.65rem;
        margin-top: 0.1rem;
    }
}
</style>