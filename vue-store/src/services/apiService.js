// 這個檔案專門負責與後端 API 溝通

// 基礎 URL，未來換成真實後端 API 時，只需要改這裡
const BASE_URL = '/api';

// 封裝一個通用的 fetch 函式
async function http(path) {
    const response = await fetch(`${BASE_URL}${path}`);
    if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
    }
    return await response.json();
}

// 導出我們需要的各種 API 請求函式
export const apiService = {
    // 獲取所有品項類別
    getCategories: () => http('/categories.json'),

    // 獲取所有品項
    getItems: () => http('/items.json'),

    // 獲取所有客製化規格
    getSpecs: () => http('/specs.json'),

    // 未來可以新增更多，例如：
    // updateItem: (itemId, data) => fetch(`${BASE_URL}/items/${itemId}`, { method: 'PUT', body: JSON.stringify(data) ... }),
    // createItem: (data) => fetch(`${BASE_URL}/items`, { method: 'POST', ... })
};