const axios = require('axios');

async function checkConnection() {
    try {
        console.log('檢查後端連接...');
        const response = await axios.get('http://localhost:8080/api/reservations/user/1');
        console.log('連接成功!');
        console.log('狀態:', response.status);
        console.log('數據:', JSON.stringify(response.data, null, 2));
    } catch (error) {
        console.error('連接失敗:');
        if (error.response) {
            console.error('錯誤狀態:', error.response.status);
            console.error('錯誤數據:', error.response.data);
        } else if (error.request) {
            console.error('沒有回應:', error.code);
        } else {
            console.error('錯誤信息:', error.message);
        }
    }
}

checkConnection();