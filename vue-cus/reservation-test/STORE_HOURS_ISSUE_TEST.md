# 營業時間不一致問題測試

## 🚨 問題描述

### 測試結果

1. **營業時間檢查 API**: `GET /api/stores/1/hours/check?day=WEDNESDAY&time=12:00`

   ```json
   {
     "storeId": 1,
     "isOpen": true,
     "status": "營業中",
     "message": "餐廳目前營業中"
   }
   ```

2. **營業時間查詢 API**: `GET /api/stores/1/hours`
   ```json
   {
     "id": null,
     "storeId": 1,
     "dayOfWeek": "WEDNESDAY",
     "openTime": null,
     "closeTime": null,
     "isOpen": false
   }
   ```

## 🔍 問題分析

### 根本原因

- **營業時間檢查 API** 使用預設邏輯（週三 11:00-22:00）
- **營業時間查詢 API** 只顯示實際資料庫設定
- 兩者不一致造成混淆

### 技術細節

1. `isStoreOpen()` 方法在沒有設定時使用 `isDefaultOpenTime()`
2. `getOpenHoursDTOByStore()` 方法直接查詢資料庫
3. 預設邏輯和實際設定不同步

## 🛠️ 解決方案

### 方案 1: 設定預設營業時間（推薦）

```bash
# 使用 Postman 或 curl 設定預設營業時間
POST {{baseUrl}}/api/stores/1/hours/default
```

### 方案 2: 手動設定週三營業時間

```bash
# 設定週三營業時間
POST {{baseUrl}}/api/stores/1/hours?day=WEDNESDAY&openTime=11:00&closeTime=22:00&isOpen=true
```

### 方案 3: 修改後端邏輯（需要程式碼修改）

- 讓營業時間查詢也使用預設邏輯
- 或者讓營業時間檢查不使用預設邏輯

## 📋 測試步驟

### 步驟 1: 檢查當前狀態

```bash
# 檢查營業時間設定
GET {{baseUrl}}/api/stores/1/hours

# 檢查週三是否營業
GET {{baseUrl}}/api/stores/1/hours/check?day=WEDNESDAY&time=12:00
```

### 步驟 2: 設定預設營業時間

```bash
# 設定預設營業時間
POST {{baseUrl}}/api/stores/1/hours/default
```

### 步驟 3: 驗證修復

```bash
# 再次檢查營業時間設定
GET {{baseUrl}}/api/stores/1/hours

# 再次檢查週三是否營業
GET {{baseUrl}}/api/stores/1/hours/check?day=WEDNESDAY&time=12:00
```

## 🎯 預期結果

### 修復前

- 營業時間查詢：`isOpen: false`
- 營業時間檢查：`isOpen: true`

### 修復後

- 營業時間查詢：`isOpen: true`
- 營業時間檢查：`isOpen: true`
- 兩者一致

## 📊 測試數據

### 預設營業時間設定

- **週一到週五**: 11:00-22:00
- **週六週日**: 10:00-23:00

### 測試時間點

- **週三 12:00**: 應該營業 ✅
- **週三 23:00**: 應該休息 ❌
- **週六 15:00**: 應該營業 ✅
- **週日 09:00**: 應該休息 ❌

## 🔧 自動化測試腳本

### Postman 測試集合

```javascript
// 在 Postman 的 Tests 標籤中添加
pm.test("營業時間檢查和查詢應該一致", function () {
  const checkResponse = pm.response.json();
  const queryResponse = pm.globals.get("hoursQueryResponse");

  pm.expect(checkResponse.isOpen).to.equal(queryResponse.isOpen);
});
```

### 完整測試流程

1. 設定預設營業時間
2. 查詢所有營業時間
3. 檢查特定時間是否營業
4. 驗證兩者結果一致

## ⚠️ 注意事項

1. **資料庫狀態**: 確保資料庫中有餐廳資料
2. **API 權限**: 確保有權限設定營業時間
3. **時間格式**: 使用 HH:MM 格式
4. **日期格式**: 使用 YYYY-MM-DD 格式

## 🐛 故障排除

### 常見問題

1. **404 錯誤**: 檢查餐廳 ID 是否存在
2. **500 錯誤**: 檢查資料庫連接
3. **權限錯誤**: 檢查 API 權限設定
4. **格式錯誤**: 檢查時間格式是否正確

### 調試技巧

1. 檢查後端日誌
2. 使用資料庫查詢驗證
3. 逐步測試每個 API
4. 確認資料庫狀態
