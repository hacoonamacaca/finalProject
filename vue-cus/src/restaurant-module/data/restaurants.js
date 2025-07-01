// 方法1: 直接導入圖片（推薦）
import restaurantImg from '@/assets/RestaurantPage/restaurant.avif'

// 餐廳資料庫 - 使用數字 ID 作為 key，與 timeslots_30d.json 的 restaurantId 對應
export const restaurants = {
    1: {
        id: 1,
        name: 'Plants',
        image: restaurantImg, // 使用導入的圖片
        address: '台北市大安區忠孝東路四段181巷40號',
        phone: '02-2771-2050',
        businessHours: '週一至週日 11:00–20:00',
        menuTitle: 'Plants 菜單',
        menuCategories: [{
                name: '主餐'
            },
            {
                name: '飲品'
            },
            {
                name: '甜點'
            }
        ],
        copyrightText: '© 2021 Plants All Rights Reserved'
    },

    2: {
        id: 2,
        name: '海景餐廳',
        image: restaurantImg, // 暫時使用同一張圖片
        address: '台北市信義區信義路五段7號',
        phone: '02-2345-6789',
        businessHours: '週一至週日 10:00–22:00',
        menuTitle: '海景菜單',
        menuCategories: [{
                name: '海鮮'
            },
            {
                name: '牛排'
            },
            {
                name: '素食'
            }
        ],
        copyrightText: '© 2021 海景餐廳 All Rights Reserved'
    },

    3: {
        id: 3,
        name: 'Italian Corner',
        image: restaurantImg, // 暫時使用同一張圖片
        address: '台北市中山區南京東路二段123號',
        phone: '02-3456-7890',
        businessHours: '週二至週日 11:30–21:30 (週一公休)',
        menuTitle: 'Italian Menu',
        menuCategories: [{
                name: 'Pizza'
            },
            {
                name: 'Pasta'
            },
            {
                name: 'Dessert'
            }
        ],
        copyrightText: '© 2021 Italian Corner All Rights Reserved'
    }
}

// 根據 ID 獲取餐廳資料
export const getRestaurantById = (id) => {
    // 確保 ID 是數字類型
    const numericId = parseInt(id)
    return restaurants[numericId] || restaurants[1] // 預設返回 Plants (ID: 1)
}