# 立即修復 SQL 時間類型錯誤 - 緊急步驟

## 🚨 問題狀況

測試顯示仍有 **"equal to 運算子中的資料類型 time 和 datetime 不相容"** 錯誤

## ⚡ 立即修復步驟

### 第一步：強制重新啟動後端 (必須!)

```bash
# 1. 停止現有的 Spring Boot 應用程式
# 按 Ctrl+C 或關閉終端

# 2. 移動到後端目錄
cd GoldenBowl

# 3. 清理編譯
./mvnw clean

# 4. 重新編譯
./mvnw compile

# 5. 重新啟動應用程式
./mvnw spring-boot:run
```

### 第二步：確認修復生效

```bash
# 在 vue-cus 目錄下運行測試
cd ../vue-cus
node simple_test.js
```

### 第三步：如果問題仍然存在

**檢查是否有其他地方呼叫了錯誤的方法：**

1. **檢查 BookingAvailabilityService**

   - 確認是否使用正確的 Repository 方法
   - 確認沒有直接使用 Spring Data JPA 自動生成的方法

2. **檢查 TimeSlotService**

   - 確認所有方法都使用自定義的 @Query

3. **資料庫連接檢查**
   - 確認 SQL Server 正在運行
   - 確認時區設定正確

## 🔍 測試類型說明

### A. **Node.js 自動測試**

```bash
# 在 vue-cus 目錄
node simple_test.js
```

**優點：**

- 快速測試主要功能
- 彩色輸出，容易閱讀
- 重點測試預約功能 (問題核心)

### B. **使用 Postman** [[memory:2660220]]

1. 導入 `GoldenBowl_API_Collection.postman_collection.json`
2. 設置環境變數：`baseUrl = http://localhost:8080`
3. 逐一測試或批量運行

**優點：**

- 詳細的請求/回應檢查
- 可以保存測試結果
- 視覺化界面

### C. **手動 cURL 測試**

```bash
# 測試關鍵的預約 API
curl -X POST http://localhost:8080/api/reservations \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "storeId": 1,
    "reservedDate": "2025-07-11",
    "reservedTime": "18:00",
    "guests": 4,
    "duration": 120,
    "content": "測試預約"
  }'
```

## 🎯 關鍵測試 API

### 1. **預約創建** (最重要)

```
POST /api/reservations
```

這是出錯的主要 API，必須通過

### 2. **可用性檢查**

```
GET /api/booking/check
GET /api/booking/batch-check
```

這些也受到同樣的 SQL 錯誤影響

### 3. **餐廳資訊**

```
GET /api/stores/1
```

這個應該正常 (已通過測試)

## ✅ 修復成功指標

當看到以下結果時，表示修復成功：

```json
{
  "success": true,
  "message": "訂位成功",
  "data": {
    "reservationId": 123,
    "status": "PENDING"
  }
}
```

## ❌ 如果仍然失敗

1. **檢查後端日誌** - 尋找具體的錯誤訊息
2. **確認資料庫連接** - 檢查 SQL Server 狀態
3. **驗證時間段資料** - 確認 time_slots 表有資料
4. **檢查用戶和餐廳資料** - 確認 userId=1, storeId=1 存在

## 💡 測試技巧

- **先測試基本功能** - 餐廳查詢、用戶驗證
- **再測試核心功能** - 預約創建、可用性檢查
- **最後測試邊緣案例** - 特殊時間、滿員情況

記住：修復後 **必須重新啟動應用程式** 才能生效！
