/**
 * 時間段服務 - 處理與時間段相關的 API 調用
 */

/**
 * 獲取餐廳的時間段數據
 * @param {string|number} restaurantId - 餐廳 ID
 * @returns {Promise<Array>} 時間段數據數組
 */
export async function fetchRestaurantTimeSlots(restaurantId) {
    try {
        // 實際生產環境中的 API 調用
        // const response = await fetch(`/api/restaurants/${restaurantId}/time-slots`)
        // return await response.json()

        // 開發環境使用本地 JSON 文件 - 修正路徑
        const response = await fetch('/ReservationTimeSlot/timeslots_30d.json')
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`)
        }
        const allTimeSlots = await response.json()

        // 過濾出指定餐廳的時間段
        const restaurantIdNum = parseInt(restaurantId) || 1
        return allTimeSlots.filter(slot => slot.restaurantId === restaurantIdNum)
    } catch (error) {
        console.error('獲取時間段失敗:', error)
        throw new Error('無法獲取時間段數據')
    }
}

/**
 * 獲取餐廳的已預訂時間段
 * @param {string|number} restaurantId - 餐廳 ID
 * @param {string} date - 日期 (YYYY-MM-DD 格式)
 * @returns {Promise<Array>} 已預訂時間段數組
 */
export async function fetchBookedTimeSlots(restaurantId, date = null) {
    try {
        // 實際生產環境中的 API 調用
        // const url = date 
        //     ? `/api/restaurants/${restaurantId}/booked-slots?date=${date}`
        //     : `/api/restaurants/${restaurantId}/booked-slots`
        // const response = await fetch(url)
        // return await response.json()

        // 開發環境模擬數據
        const restaurantIdNum = parseInt(restaurantId) || 1
        const mockBookedSlots = [{
                restaurantId: restaurantIdNum,
                date: '2025-06-27',
                startTime: '12:00',
                customerName: '張先生'
            },
            {
                restaurantId: restaurantIdNum,
                date: '2025-06-27',
                startTime: '19:00',
                customerName: '李小姐'
            },
            {
                restaurantId: restaurantIdNum,
                date: '2025-06-28',
                startTime: '13:00',
                customerName: '王先生'
            },
            {
                restaurantId: restaurantIdNum,
                date: '2025-06-29',
                startTime: '18:30',
                customerName: '陳小姐'
            },
        ]

        if (date) {
            return mockBookedSlots.filter(slot => slot.date === date)
        }

        return mockBookedSlots
    } catch (error) {
        console.error('獲取已預訂時間段失敗:', error)
        throw new Error('無法獲取已預訂時間段數據')
    }
}

/**
 * 提交預約
 * @param {Object} reservationData - 預約數據
 * @returns {Promise<Object>} 預約結果
 */
export async function submitReservation(reservationData) {
    try {
        // 實際生產環境中的 API 調用
        // const response = await fetch('/api/reservations', {
        //     method: 'POST',
        //     headers: {
        //         'Content-Type': 'application/json',
        //     },
        //     body: JSON.stringify(reservationData)
        // })
        // return await response.json()

        // 開發環境模擬成功響應
        console.log('提交預約數據:', reservationData)

        // 模擬 API 延遲
        await new Promise(resolve => setTimeout(resolve, 1000))

        return {
            success: true,
            reservationId: 'RES' + Date.now(),
            message: '預約成功！我們將儘快與您聯絡確認。',
            data: reservationData
        }
    } catch (error) {
        console.error('提交預約失敗:', error)
        throw new Error('預約提交失敗，請稍後再試')
    }
}

/**
 * 檢查時間段可用性
 * @param {string|number} restaurantId - 餐廳 ID
 * @param {string} date - 日期 (YYYY-MM-DD 格式)
 * @param {string} timeSlot - 時間段 (HH:MM 格式)
 * @returns {Promise<boolean>} 是否可用
 */
export async function checkTimeSlotAvailability(restaurantId, date, timeSlot) {
    try {
        // 實際生產環境中的 API 調用
        // const response = await fetch(`/api/restaurants/${restaurantId}/availability?date=${date}&time=${timeSlot}`)
        // const result = await response.json()
        // return result.available

        // 開發環境模擬檢查
        const bookedSlots = await fetchBookedTimeSlots(restaurantId, date)
        return !bookedSlots.some(slot => slot.startTime === timeSlot)
    } catch (error) {
        console.error('檢查時間段可用性失敗:', error)
        return false
    }
}





// src/
// ├── components/
// │   ├── ReservationForm.vue          # 主要預約表單組件
// │   └── TimePickerSectioned.vue      # 時間段選擇器組件
// ├── utils/
// │   └── timeSlotUtils.js             # 時間段處理工具函數
// ├── services/
// │   └── timeSlotService.js           # API 服務層
// ├── assets/css/
// │   └── reservationform.css          # 預約表單樣式
// └── data/
//     └── timeslots_30d.json           # 時間段數據