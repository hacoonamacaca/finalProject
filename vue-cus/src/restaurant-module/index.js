// 餐廳模組主要入口點
// 導出所有需要併入其他專案的核心組件和功能

// 核心組件
export {
    default as RestaurantTemplate
}
from './components/RestaurantTemplate.vue'
export {
    default as ReservationForm
}
from './components/ReservationForm.vue'
export {
    default as RestaurantBanner
}
from './components/RestaurantBanner.vue'
export {
    default as RestaurantInfo
}
from './components/RestaurantInfo.vue'
export {
    default as RestaurantMenu
}
from './components/RestaurantMenu.vue'
export {
    default as MenuNavigation
}
from './components/MenuNavigation.vue'
export {
    default as RestaurantMap
}
from './components/RestaurantMap.vue'
export {
    default as RestaurantFooter
}
from './components/RestaurantFooter.vue'
export {
    default as TimePickerSectioned
}
from './components/TimePickerSectioned.vue'

// 資料和服務
export {
    restaurants,
    getRestaurantById
}
from './data/restaurants.js'
export {
    fetchRestaurantTimeSlots,
    fetchBookedTimeSlots,
    submitReservation,
    checkTimeSlotAvailability
}
from './services/timeSlotService.js'
export * from './utils/timeSlotUtils.js'

// 為install函數導入組件
import RestaurantTemplate from './components/RestaurantTemplate.vue'
import ReservationForm from './components/ReservationForm.vue'
import RestaurantBanner from './components/RestaurantBanner.vue'
import RestaurantInfo from './components/RestaurantInfo.vue'
import RestaurantMenu from './components/RestaurantMenu.vue'
import MenuNavigation from './components/MenuNavigation.vue'
import RestaurantMap from './components/RestaurantMap.vue'
import RestaurantFooter from './components/RestaurantFooter.vue'
import TimePickerSectioned from './components/TimePickerSectioned.vue'

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
        app.component('ReservationForm', ReservationForm)
        app.component('RestaurantBanner', RestaurantBanner)
        app.component('RestaurantInfo', RestaurantInfo)
        app.component('RestaurantMenu', RestaurantMenu)
        app.component('MenuNavigation', MenuNavigation)
        app.component('RestaurantMap', RestaurantMap)
        app.component('RestaurantFooter', RestaurantFooter)
        app.component('TimePickerSectioned', TimePickerSectioned)

        // 提供全域設定
        app.provide('restaurantConfig', {
            theme: restaurantTheme,
            ...options
        })
    }
}