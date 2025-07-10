const axios = require('axios');

async function testSlotsAPI() {
    try {
        console.log('=== 測試時段查詢API ===');

        const url = 'http://localhost:8080/api/booking/slots/1?date=2025-07-15&guests=4';
        console.log('查詢URL:', url);

        const response = await axios.get(url);

        console.log('回應狀態:', response.status);
        console.log('回應數據結構:');
        console.log('- 數據類型:', Array.isArray(response.data) ? 'Array' : typeof response.data);
        console.log('- 數量:', response.data.length);

        if (response.data.length > 0) {
            console.log('- 第一筆資料:', JSON.stringify(response.data[0], null, 2));
            console.log('- 數據大小估計:', JSON.stringify(response.data).length + ' 字元');
        }

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

testSlotsAPI();