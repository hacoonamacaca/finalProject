// 餐廳模組主要入口點
// 導出所有需要併入其他專案的核心組件和功能

// 核心組件
export {
    default as RestaurantTemplate
}
from '../components/KTlu/RestaurantTemplate.vue'
export {
    default as ReservationForm
}
from '../components/KTlu/ReservationForm.vue'
export {
    default as RestaurantBanner
}
from '../components/KTlu/RestaurantBanner.vue'
export {
    default as RestaurantInfo
}
from '../components/KTlu/RestaurantInfo.vue'
export {
    default as RestaurantMenu
}
from '../components/KTlu/RestaurantMenu.vue'
export {
    default as RestaurantMap
}
from '../components/KTlu/RestaurantMap.vue'
export {
    default as RestaurantFooter
}
from '../components/KTlu/RestaurantFooter.vue'
export {
    default as TimePickerSectioned
}
from '../components/KTlu/TimePickerSectioned.vue'

// 資料和服務
export {
    restaurants,
    getRestaurantById as getKTluRestaurantById
}
from '../data/restaurants.js'
export {
    fetchRestaurantTimeSlots,
    fetchBookedTimeSlots,
    submitReservation,
    checkTimeSlotAvailability
}
from '../services/timeSlotService.js'
export * from '../utils/timeSlotUtils.js'

// 為install函數導入組件
import RestaurantTemplate from '../components/KTlu/RestaurantTemplate.vue'
import RestaurantBanner from '../components/KTlu/RestaurantBanner.vue'
import RestaurantInfo from '../components/KTlu/RestaurantInfo.vue'
import RestaurantMenu from '../components/KTlu/RestaurantMenu.vue'
import RestaurantMap from '../components/KTlu/RestaurantMap.vue'
import RestaurantFooter from '../components/KTlu/RestaurantFooter.vue'
import ReservationForm from '../components/KTlu/ReservationForm.vue'
import TimePickerSectioned from '../components/KTlu/TimePickerSectioned.vue'
import ItemDetailModal from '../components/KTlu/ItemDetailModal.vue'
import CartModal from '../components/KTlu/CartModal.vue'

// CSS 變數（供主專案使用）
export const restaurantTheme = {
    primaryColor: '#ff6c00',
    primaryLight: '#fff3e0',
    textOrange: '#ff6c00',
    borderColor: '#eee',
    shadowMd: '0 2px 8px rgba(0, 0, 0, 0.03)'
}

// 安裝函數（供 Vue 專案使用）
export default {
    install(app, options = {}) {
        // 註冊所有組件
        app.component('RestaurantTemplate', RestaurantTemplate)
        app.component('RestaurantBanner', RestaurantBanner)
        app.component('RestaurantInfo', RestaurantInfo)
        app.component('RestaurantMenu', RestaurantMenu)
        app.component('RestaurantMap', RestaurantMap)
        app.component('RestaurantFooter', RestaurantFooter)
        app.component('ReservationForm', ReservationForm)
        app.component('TimePickerSectioned', TimePickerSectioned)
        app.component('ItemDetailModal', ItemDetailModal)
        app.component('CartModal', CartModal)

        // 提供全域設定
        app.provide('restaurantConfig', {
            theme: restaurantTheme,
            ...options
        })
    }
}