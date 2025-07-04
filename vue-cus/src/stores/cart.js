import {
    defineStore
} from 'pinia'
import {
    ref,
    computed
} from 'vue'

export const useCartStore = defineStore('cart', () => {
    // 購物車結構：{ restaurantId: { restaurant, items } }
    const cartByRestaurant = ref({})

    // 購物車是否可見
    const isCartVisible = ref(false)

    // 計算屬性
    const cartCount = computed(() => {
        return Object.values(cartByRestaurant.value).reduce((total, restaurantCart) => {
            return total + restaurantCart.items.reduce((sum, item) => sum + item.quantity, 0)
        }, 0)
    })

    const totalAmount = computed(() => {
        return Object.values(cartByRestaurant.value).reduce((total, restaurantCart) => {
            return total + restaurantCart.items.reduce((sum, item) => sum + (item.price * item.quantity), 0)
        }, 0)
    })

    const restaurantCount = computed(() => {
        return Object.keys(cartByRestaurant.value).length
    })

    const cartItems = computed(() => {
        // 返回所有餐廳的商品列表（扁平化）
        return Object.values(cartByRestaurant.value).flatMap(restaurantCart => restaurantCart.items)
    })

    const cartRestaurants = computed(() => {
        return Object.values(cartByRestaurant.value).map(restaurantCart => restaurantCart.restaurant)
    })

    // 方法
    const addToCart = (item, restaurant) => {
        const restaurantId = restaurant.id

        // 如果該餐廳還沒有購物車，創建一個
        if (!cartByRestaurant.value[restaurantId]) {
            cartByRestaurant.value[restaurantId] = {
                restaurant: {
                    id: restaurant.id,
                    name: restaurant.name,
                    image: restaurant.image
                },
                items: []
            }
        }

        const restaurantCart = cartByRestaurant.value[restaurantId]
        const existingItemIndex = restaurantCart.items.findIndex(cartItem => cartItem.id === item.id)

        if (existingItemIndex > -1) {
            restaurantCart.items[existingItemIndex].quantity += item.quantity
        } else {
            restaurantCart.items.push({
                ...item
            })
        }
    }

    const updateQuantity = (itemId, newQuantity, restaurantId) => {
        if (!cartByRestaurant.value[restaurantId]) return

        if (newQuantity <= 0) {
            removeItem(itemId, restaurantId)
            return
        }

        const itemIndex = cartByRestaurant.value[restaurantId].items.findIndex(item => item.id === itemId)
        if (itemIndex > -1) {
            cartByRestaurant.value[restaurantId].items[itemIndex].quantity = newQuantity
        }
    }

    const removeItem = (itemId, restaurantId) => {
        if (!cartByRestaurant.value[restaurantId]) return

        cartByRestaurant.value[restaurantId].items = cartByRestaurant.value[restaurantId].items.filter(item => item.id !== itemId)

        // 如果該餐廳沒有商品了，移除該餐廳的購物車
        if (cartByRestaurant.value[restaurantId].items.length === 0) {
            delete cartByRestaurant.value[restaurantId]
        }
    }

    const clearCart = () => {
        cartByRestaurant.value = {}
    }

    const clearRestaurantCart = (restaurantId) => {
        delete cartByRestaurant.value[restaurantId]
    }

    const getRestaurantCart = (restaurantId) => {
        return cartByRestaurant.value[restaurantId] || null
    }

    const getRestaurantTotal = (restaurantId) => {
        const restaurantCart = cartByRestaurant.value[restaurantId]
        if (!restaurantCart) return 0

        return restaurantCart.items.reduce((sum, item) => sum + (item.price * item.quantity), 0)
    }

    const getRestaurantItemCount = (restaurantId) => {
        const restaurantCart = cartByRestaurant.value[restaurantId]
        if (!restaurantCart) return 0

        return restaurantCart.items.reduce((sum, item) => sum + item.quantity, 0)
    }

    const toggleCartVisibility = () => {
        isCartVisible.value = !isCartVisible.value
    }

    const showCart = () => {
        isCartVisible.value = true
    }

    const hideCart = () => {
        isCartVisible.value = false
    }

    // 結帳相關方法
    const checkoutSingleRestaurant = (restaurantId) => {
        const restaurantCart = cartByRestaurant.value[restaurantId]
        if (!restaurantCart) return null

        const orderData = {
            restaurant: restaurantCart.restaurant,
            items: [...restaurantCart.items],
            totalAmount: getRestaurantTotal(restaurantId),
            orderTime: new Date().toISOString()
        }

        // 移除該餐廳的購物車
        clearRestaurantCart(restaurantId)

        return orderData
    }

    const checkoutAllRestaurants = () => {
        const orders = []

        Object.keys(cartByRestaurant.value).forEach(restaurantId => {
            const orderData = checkoutSingleRestaurant(restaurantId)
            if (orderData) {
                orders.push(orderData)
            }
        })

        return orders
    }

    return {
        // 狀態
        cartByRestaurant,
        isCartVisible,

        // 計算屬性
        cartCount,
        totalAmount,
        restaurantCount,
        cartItems,
        cartRestaurants,

        // 方法
        addToCart,
        updateQuantity,
        removeItem,
        clearCart,
        clearRestaurantCart,
        getRestaurantCart,
        getRestaurantTotal,
        getRestaurantItemCount,
        toggleCartVisibility,
        showCart,
        hideCart,

        // 結帳方法
        checkoutSingleRestaurant,
        checkoutAllRestaurants
    }
})