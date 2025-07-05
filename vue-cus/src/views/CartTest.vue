<template>
    <div class="container mt-5">
        <h2>多餐廳購物車功能測試</h2>

        <div class="row">
            <div class="col-md-8">
                <h3>餐廳商品列表</h3>

                <!-- 餐廳 1 -->
                <div class="card mb-4">
                    <div class="card-header">
                        <h4>🍕 披薩店</h4>
                    </div>
                    <div class="card-body">
                        <div class="row">
                            <div v-for="item in pizzaItems" :key="item.id" class="col-md-6 mb-3">
                                <div class="card">
                                    <img :src="item.image" class="card-img-top" :alt="item.name"
                                        style="height: 200px; object-fit: cover;">
                                    <div class="card-body">
                                        <h5 class="card-title">{{ item.name }}</h5>
                                        <p class="card-text">{{ item.description }}</p>
                                        <p class="card-text"><strong>NT${{ item.price }}</strong></p>
                                        <button class="btn btn-primary" @click="addToCart(item, pizzaRestaurant)">
                                            加入購物車
                                        </button>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- 餐廳 2 -->
                <div class="card mb-4">
                    <div class="card-header">
                        <h4>🍜 拉麵店</h4>
                    </div>
                    <div class="card-body">
                        <div class="row">
                            <div v-for="item in ramenItems" :key="item.id" class="col-md-6 mb-3">
                                <div class="card">
                                    <img :src="item.image" class="card-img-top" :alt="item.name"
                                        style="height: 200px; object-fit: cover;">
                                    <div class="card-body">
                                        <h5 class="card-title">{{ item.name }}</h5>
                                        <p class="card-text">{{ item.description }}</p>
                                        <p class="card-text"><strong>NT${{ item.price }}</strong></p>
                                        <button class="btn btn-primary" @click="addToCart(item, ramenRestaurant)">
                                            加入購物車
                                        </button>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- 餐廳 3 -->
                <div class="card mb-4">
                    <div class="card-header">
                        <h4>☕ 咖啡店</h4>
                    </div>
                    <div class="card-body">
                        <div class="row">
                            <div v-for="item in coffeeItems" :key="item.id" class="col-md-6 mb-3">
                                <div class="card">
                                    <img :src="item.image" class="card-img-top" :alt="item.name"
                                        style="height: 200px; object-fit: cover;">
                                    <div class="card-body">
                                        <h5 class="card-title">{{ item.name }}</h5>
                                        <p class="card-text">{{ item.description }}</p>
                                        <p class="card-text"><strong>NT${{ item.price }}</strong></p>
                                        <button class="btn btn-primary" @click="addToCart(item, coffeeRestaurant)">
                                            加入購物車
                                        </button>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <div class="col-md-4">
                <h3>購物車狀態</h3>
                <div class="card">
                    <div class="card-body">
                        <p><strong>餐廳數量：</strong>{{ restaurantCount }} 家</p>
                        <p><strong>商品數量：</strong>{{ cartCount }}</p>
                        <p><strong>總金額：</strong>NT${{ totalAmount }}</p>
                        <button class="btn btn-success" @click="showCart" :disabled="cartCount === 0">
                            查看購物車
                        </button>
                        <button class="btn btn-warning" @click="clearCart" :disabled="cartCount === 0">
                            清空購物車
                        </button>
                    </div>
                </div>

                <div class="mt-3">
                    <h4>各餐廳購物車</h4>
                    <div v-if="restaurantCount === 0" class="text-muted">
                        購物車是空的
                    </div>
                    <div v-else>
                        <div v-for="(restaurantCart, restaurantId) in cartByRestaurant" :key="restaurantId"
                            class="card mb-2">
                            <div class="card-header">
                                <h6 class="mb-0">{{ restaurantCart.restaurant.name }}</h6>
                            </div>
                            <div class="card-body p-2">
                                <div v-for="item in restaurantCart.items" :key="item.id" class="mb-2">
                                    <div class="d-flex justify-content-between align-items-center">
                                        <div>
                                            <h6 class="mb-0">{{ item.name }}</h6>
                                            <small class="text-muted">NT${{ item.price }} × {{ item.quantity }}</small>
                                        </div>
                                        <div class="btn-group btn-group-sm">
                                            <button class="btn btn-outline-secondary"
                                                @click="updateQuantity(item.id, item.quantity - 1, restaurantId)">
                                                -
                                            </button>
                                            <button class="btn btn-outline-secondary"
                                                @click="updateQuantity(item.id, item.quantity + 1, restaurantId)">
                                                +
                                            </button>
                                            <button class="btn btn-outline-danger"
                                                @click="removeItem(item.id, restaurantId)">
                                                ×
                                            </button>
                                        </div>
                                    </div>
                                </div>
                                <hr>
                                <div class="d-flex justify-content-between">
                                    <strong>小計：NT${{ getRestaurantTotal(restaurantId) }}</strong>
                                    <button class="btn btn-sm btn-success" @click="checkoutRestaurant(restaurantId)">
                                        結帳此餐廳
                                    </button>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import { computed } from 'vue'
import { useCartStore } from '@/stores/cart'

const cartStore = useCartStore()

// 餐廳資訊
const pizzaRestaurant = {
    id: 1,
    name: '🍕 披薩店',
    image: 'https://placehold.co/400x300/E7E7E7/333333?text=Pizza'
}

const ramenRestaurant = {
    id: 2,
    name: '🍜 拉麵店',
    image: 'https://placehold.co/400x300/E7E7E7/333333?text=Ramen'
}

const coffeeRestaurant = {
    id: 3,
    name: '☕ 咖啡店',
    image: 'https://placehold.co/400x300/E7E7E7/333333?text=Coffee'
}

// 測試商品
const pizzaItems = [
    {
        id: 1,
        name: '瑪格麗特披薩',
        description: '番茄醬、莫札瑞拉起司、羅勒葉',
        price: 280,
        image: 'https://placehold.co/400x300/E7E7E7/333333?text=Pizza+1'
    },
    {
        id: 2,
        name: '夏威夷披薩',
        description: '火腿、鳳梨、起司',
        price: 320,
        image: 'https://placehold.co/400x300/E7E7E7/333333?text=Pizza+2'
    }
]

const ramenItems = [
    {
        id: 3,
        name: '豚骨拉麵',
        description: '濃郁豚骨湯底、叉燒肉、溏心蛋',
        price: 180,
        image: 'https://placehold.co/400x300/E7E7E7/333333?text=Ramen+1'
    },
    {
        id: 4,
        name: '味噌拉麵',
        description: '味噌湯底、豆芽菜、玉米',
        price: 160,
        image: 'https://placehold.co/400x300/E7E7E7/333333?text=Ramen+2'
    }
]

const coffeeItems = [
    {
        id: 5,
        name: '美式咖啡',
        description: '香濃美式咖啡',
        price: 80,
        image: 'https://placehold.co/400x300/E7E7E7/333333?text=Coffee+1'
    },
    {
        id: 6,
        name: '拿鐵咖啡',
        description: '濃縮咖啡配蒸煮牛奶',
        price: 120,
        image: 'https://placehold.co/400x300/E7E7E7/333333?text=Coffee+2'
    }
]

// 購物車相關
const cartCount = computed(() => cartStore.cartCount)
const restaurantCount = computed(() => cartStore.restaurantCount)
const cartByRestaurant = computed(() => cartStore.cartByRestaurant)
const totalAmount = computed(() => cartStore.totalAmount)

const addToCart = (item, restaurant) => {
    cartStore.addToCart({
        id: item.id,
        name: item.name,
        price: item.price,
        image: item.image,
        quantity: 1
    }, restaurant)
}

const updateQuantity = (itemId, newQuantity, restaurantId) => {
    cartStore.updateQuantity(itemId, newQuantity, restaurantId)
}

const removeItem = (itemId, restaurantId) => {
    cartStore.removeItem(itemId, restaurantId)
}

const clearCart = () => {
    cartStore.clearCart()
}

const showCart = () => {
    cartStore.showCart()
}

const getRestaurantTotal = (restaurantId) => {
    return cartStore.getRestaurantTotal(restaurantId)
}

const checkoutRestaurant = (restaurantId) => {
    const orderData = cartStore.checkoutSingleRestaurant(restaurantId)
    if (orderData) {
        console.log('結帳餐廳：', orderData)
        alert(`已結帳 ${orderData.restaurant.name}，總金額：NT$${orderData.totalAmount}`)
    }
}
</script>