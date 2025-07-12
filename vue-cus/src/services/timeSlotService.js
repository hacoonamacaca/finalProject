import axios from '@/plungins/axios'

// 獲取餐廳的時間段數據
export const fetchRestaurantTimeSlots = async (restaurantId, date = null) => {
    try {
        const params = {
            date: date || new Date().toISOString().split('T')[0] // 如果沒有指定日期，使用今天
        }

        const response = await axios.get(`/api/booking/all-slots/${restaurantId}`, {
            params
        })
        return response.data
    } catch (error) {
        console.error('獲取餐廳時間段失敗:', error)
        throw error
    }
}

// 獲取已預訂的時間段
export const fetchBookedTimeSlots = async (restaurantId, date = null) => {
    try {
        const params = {
            date: date || new Date().toISOString().split('T')[0] // 如果沒有指定日期，使用今天
        }

        const response = await axios.get(`/api/booking/booked-slots/${restaurantId}`, {
            params
        })
        return response.data
    } catch (error) {
        console.error('獲取已預訂時間段失敗:', error)
        throw error
    }
}

// 檢查特定時間的可用性
export const checkTimeAvailability = async (restaurantId, date, time, guests) => {
    try {
        const response = await axios.get('/api/booking/check', {
            params: {
                storeId: restaurantId,
                date: date,
                time: time,
                guests: guests,
                duration: 120 // 2小時
            }
        })
        return response.data
    } catch (error) {
        console.error('檢查時間可用性失敗:', error)
        throw error
    }
}

// 創建預約
export const createReservation = async (reservationData) => {
    try {
        const response = await axios.post('/api/reservations', reservationData)
        return response.data
    } catch (error) {
        console.error('創建預約失敗:', error)
        throw error
    }
}

// 獲取用戶預約記錄
export const getUserReservations = async (userId) => {
    try {
        const response = await axios.get(`/api/reservations/user/${userId}`)
        return response.data
    } catch (error) {
        console.error('獲取用戶預約記錄失敗:', error)
        throw error
    }
}

// 取消預約
export const cancelReservation = async (reservationId, userId) => {
    try {
        const response = await axios.delete(`/api/reservations/${reservationId}`, {
            params: {
                userId: userId
            }
        })
        return response.data
    } catch (error) {
        console.error('取消預約失敗:', error)
        throw error
    }
}