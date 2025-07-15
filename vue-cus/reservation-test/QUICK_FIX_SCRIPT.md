# 🚀 營業時間不一致問題快速修復

## 📋 問題摘要

- **營業時間檢查 API**: 顯示營業中 (`isOpen: true`)
- **營業時間查詢 API**: 顯示休息中 (`isOpen: false`)
- **原因**: 檢查 API 使用預設邏輯，查詢 API 只顯示實際設定

## ⚡ 快速修復步驟

### 步驟 1: 設定預設營業時間

```bash
# 使用 curl 設定預設營業時間
curl -X POST "http://localhost:8080/api/stores/1/hours/default"
```

### 步驟 2: 驗證修復

```bash
# 檢查營業時間設定
curl "http://localhost:8080/api/stores/1/hours"

# 檢查週三 12:00 是否營業
curl "http://localhost:8080/api/stores/1/hours/check?day=WEDNESDAY&time=12:00"
```

## 🎯 預期結果

### 修復前

```json
// GET /api/stores/1/hours
{
  "id": null,
  "storeId": 1,
  "dayOfWeek": "WEDNESDAY",
  "isOpen": false
}

// GET /api/stores/1/hours/check?day=WEDNESDAY&time=12:00
{
  "storeId": 1,
  "isOpen": true,
  "status": "營業中"
}
```

### 修復後

```json
// GET /api/stores/1/hours
{
  "id": 1,
  "storeId": 1,
  "dayOfWeek": "WEDNESDAY",
  "openTime": "11:00:00",
  "closeTime": "22:00:00",
  "isOpen": true
}

// GET /api/stores/1/hours/check?day=WEDNESDAY&time=12:00
{
  "storeId": 1,
  "isOpen": true,
  "status": "營業中"
}
```

## 🔧 Postman 快速測試

### 1. 導入測試集合

- 導入 `Store_Hours_Fix.postman_collection.json`
- 設定環境變數 `BASE_URL = http://localhost:8080`

### 2. 執行測試

1. 執行 "1. 檢查問題狀態" 確認問題
2. 執行 "2. 修復問題" 設定預設營業時間
3. 執行 "3. 驗證修復結果" 確認修復成功
4. 執行 "4. 完整測試" 測試各種時間點

## 📊 預設營業時間設定

| 星期 | 營業時間    | 狀態 |
| ---- | ----------- | ---- |
| 週一 | 11:00-22:00 | 營業 |
| 週二 | 11:00-22:00 | 營業 |
| 週三 | 11:00-22:00 | 營業 |
| 週四 | 11:00-22:00 | 營業 |
| 週五 | 11:00-22:00 | 營業 |
| 週六 | 10:00-23:00 | 營業 |
| 週日 | 10:00-23:00 | 營業 |

## ⚠️ 注意事項

1. **資料庫狀態**: 確保餐廳 ID 1 存在
2. **API 權限**: 確保有權限設定營業時間
3. **後端運行**: 確保 Spring Boot 應用程式正在運行
4. **端口設定**: 確認使用正確的端口 (8080)

## 🐛 故障排除

### 常見錯誤

1. **404 錯誤**: 檢查餐廳 ID 是否存在
2. **500 錯誤**: 檢查資料庫連接
3. **連接錯誤**: 確認後端服務正在運行

### 調試命令

```bash
# 檢查後端狀態
curl "http://localhost:8080/actuator/health"

# 檢查餐廳是否存在
curl "http://localhost:8080/api/stores/1"

# 檢查資料庫連接
curl "http://localhost:8080/api/stores"
```

## ✅ 驗證清單

- [ ] 後端服務正在運行
- [ ] 餐廳 ID 1 存在於資料庫
- [ ] 成功設定預設營業時間
- [ ] 營業時間查詢和檢查結果一致
- [ ] 各種時間點測試通過

## 📞 支援

如果問題仍然存在，請檢查：

1. 後端日誌中的錯誤訊息
2. 資料庫中的餐廳資料
3. API 權限設定
4. 網路連接狀態
