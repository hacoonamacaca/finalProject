<template>
    <div class="restaurant-menu">
        <div class="menu-container">
            <h3 class="menu-title">{{ restaurant.menuTitle || '餐廳菜單' }}</h3>

            <div class="menu-tabs" v-if="restaurant.menuCategories && restaurant.menuCategories.length > 0">
                <button v-for="(category, index) in restaurant.menuCategories" :key="index"
                    :class="['menu-tab', { active: activeTab === index }]" @click="activeTab = index">
                    {{ category.name }}
                </button>
            </div>

            <div class="menu-content">
                <div v-if="restaurant.menuCategories && restaurant.menuCategories.length > 0">
                    <div class="menu-grid">
                        <!-- 模擬菜品展示，實際項目中這裡會是真實的菜品數據 -->
                        <div class="menu-item" v-for="item in 8" :key="item">
                            <div class="item-image">
                                <img :src="restaurant.image" :alt="`菜品 ${item}`" />
                            </div>
                            <div class="item-info">
                                <h5 class="item-name">{{ restaurant.menuCategories[activeTab]?.name }} {{ item }}</h5>
                                <p class="item-desc">美味的{{ restaurant.menuCategories[activeTab]?.name }}系列</p>
                            </div>
                        </div>
                    </div>
                    <p class="menu-note">
                        詳細菜單內容與價格請洽詢店家或電話預約時詢問
                    </p>
                </div>
                <div v-else class="no-menu">
                    <p>菜單資訊請洽詢店家</p>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref } from 'vue'

defineProps({
    restaurant: {
        type: Object,
        required: true
    }
})

const activeTab = ref(0)
</script>

<style scoped>
.restaurant-menu {
    margin: 3rem 0;
    padding: 0 1rem;
}

.menu-container {
    width: 100%;
    margin: 0 0;
}

.menu-title {
    font-size: 2rem;
    font-weight: 600;
    color: #333;
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
    border: 2px solid #ff6c00;
    background: #fff;
    color: #ff6c00;
    font-weight: 500;
    cursor: pointer;
    border-radius: 25px;
    transition: all 0.3s ease;
    white-space: nowrap;
}

.menu-tab:hover {
    background: #ff6c00;
    color: #fff;
}

.menu-tab.active {
    background: #ff6c00;
    color: #fff;
}

.menu-content {
    padding: 1rem 0;
}

.menu-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
    gap: 1.5rem;
    margin-bottom: 2rem;
}

.menu-item {
    background: #fff;
    border-radius: 12px;
    overflow: hidden;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    transition: transform 0.3s ease;
}

.menu-item:hover {
    transform: translateY(-4px);
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
}

.item-image {
    width: 100%;
    height: 140px;
    overflow: hidden;
}

.item-image img {
    width: 100%;
    height: 100%;
    object-fit: cover;
}

.item-info {
    padding: 1rem;
}

.item-name {
    font-size: 1rem;
    font-weight: 600;
    color: #333;
    margin: 0 0 0.5rem 0;
}

.item-desc {
    font-size: 0.9rem;
    color: #666;
    margin: 0;
    line-height: 1.4;
}

.menu-note {
    text-align: center;
    color: #666;
    font-size: 0.9rem;
    line-height: 1.5;
    font-style: italic;
}

.no-menu {
    text-align: center;
    color: #666;
    padding: 3rem 0;
    font-size: 1.1rem;
}

/* 響應式設計 */
@media (max-width: 768px) {
    .menu-container {
        width: 100%;
    }

    .menu-grid {
        grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
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
}

@media (max-width: 480px) {
    .menu-container {
        width: 100%;
    }

    .menu-grid {
        grid-template-columns: repeat(2, 1fr);
    }

    .item-image {
        height: 120px;
    }

    .item-info {
        padding: 0.75rem;
    }
}
</style>