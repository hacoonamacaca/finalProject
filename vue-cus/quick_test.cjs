const axios = require('axios');

async function testReservation() {
    try {
        console.log('=== 開始測試預約功能 ===');

        const reservationData = {
            userId: 1,
            storeId: 1,
            reservedDate: "2025-07-15",
            reservedTime: "18:00",
            guests: 2,
            duration: 120,
            content: "測試預約"
        };

        console.log('發送預約請求:', JSON.stringify(reservationData, null, 2));

        const response = await axios.post('http://localhost:8080/api/reservations', reservationData);

        console.log('預約回應狀態:', response.status);
        console.log('預約回應數據:', JSON.stringify(response.data, null, 2));

        // 測試查詢用戶預約記錄
        console.log('\n=== 開始測試查詢用戶預約記錄 ===');
        const queryResponse = await axios.get('http://localhost:8080/api/reservations/user/1');

        console.log('查詢回應狀態:', queryResponse.status);
        console.log('查詢回應數據:', JSON.stringify(queryResponse.data, null, 2));

    } catch (error) {
        console.error('測試失敗:');
        if (error.response) {
            console.error('錯誤狀態:', error.response.status);
            console.error('錯誤數據:', error.response.data);
        } else {
            console.error('錯誤信息:', error.message);
        }
    }
}

testReservation();