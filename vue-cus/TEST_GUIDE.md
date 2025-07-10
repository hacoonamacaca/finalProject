# Golden Bowl API 測試指南

## 前提條件

1. **確保後端服務正在運行**

   ```bash
   cd GoldenBowl
   ./mvnw spring-boot:run
   ```

   應該看到：`Started GoldenBowlApplication` 並且服務運行在 `http://localhost:8080`

2. **確保資料庫連接正常**
   - SQL Server 正在運行
   - 資料庫表結構正確
   - 有測試資料（store、user 等）

## 測試方法選擇

### 方法 1: Node.js 自動化測試 (推薦)

**優點：** 快速、自動化、彩色輸出、生成報告

```bash
# 1. 安裝依賴
npm install

# 2. 運行完整測試
node test_all_apis.js

# 3. 查看測試報告
# 會生成 api_test_report.json 檔案
```

### 方法 2: Postman Collection

**優點：** 視覺化界面、詳細請求/回應檢查

1. 打開 Postman
2. 導入 `GoldenBowl_API_Collection.postman_collection.json`
3. 設置環境變數：
   - `baseUrl`: `http://localhost:8080`
4. 逐一測試 API 或批量運行

### 方法 3: 手動 API 測試

**優點：** 精確控制、深入調試

```bash
# 測試服務器狀態
curl http://localhost:8080/api/test/status

# 測試預約 API
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

## 重點測試案例

### 1. 預約功能測試 (重要!)

這個是你之前遇到錯誤的 API：

```bash
POST /api/reservations
{
  "userId": 1,
  "storeId": 1,
  "reservedDate": "2025-07-11",
  "reservedTime": "18:00",
  "guests": 4,
  "duration": 120,
  "content": "測試預約"
}
```

**期望結果：**

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

**如果還是錯誤：**

```json
{
  "success": false,
  "message": "訂位失敗: 系統錯誤: JDBC exception..."
}
```

### 2. 可用性檢查測試

```bash
GET /api/booking/check?storeId=1&date=2025-07-11&time=18:00&guests=4

# 批量檢查
GET /api/booking/batch-check?storeId=1&date=2025-07-11&times=18:00,18:30,19:00&guests=4&duration=120
```

### 3. 時間段相關測試

```bash
# 查詢可用時間段
GET /api/time-slots?storeId=1&date=2025-07-11

# 生成時間段
POST /api/time-slots/generate?storeId=1&days=30
```

## 測試步驟

### 第一步：快速健康檢查

```bash
# 運行 Node.js 測試（僅基本功能）
node test_all_apis.js
```

檢查：

- [ ] 服務器回應正常
- [ ] 基本 API 可訪問
- [ ] 資料庫連接正常

### 第二步：預約功能測試

使用 Postman 或 curl 測試預約 API，重點關注：

- [ ] 無 SQL 類型錯誤
- [ ] 能成功創建預約
- [ ] 返回正確的回應格式

### 第三步：深入測試

使用完整的 Postman Collection 測試所有 52 個 API 端點。

## 故障排除

### 1. 連接失敗

**症狀：** `Connection refused` 或 `ECONNRESET`

**解決：**

```bash
# 檢查後端是否運行
netstat -an | findstr :8080

# 重新啟動後端
cd GoldenBowl
./mvnw spring-boot:run
```

### 2. SQL 類型錯誤仍然存在

**症狀：** `equal to 運算子中的資料類型 time 和 datetime 不相容`

**解決：**

1. 確認已重新啟動應用程式
2. 檢查日誌中的具體錯誤位置
3. 驗證 Repository 方法是否正確載入

### 3. 404 錯誤

**症狀：** API 返回 404 Not Found

**解決：**

1. 檢查 API 路徑是否正確
2. 確認 Controller 是否正確註冊
3. 檢查 Spring Boot 啟動日誌

### 4. 資料不存在錯誤

**症狀：** `用戶不存在` 或 `餐廳不存在`

**解決：**

1. 檢查資料庫中是否有測試資料
2. 確認 userId 和 storeId 是否正確
3. 可能需要先創建測試資料

## 測試資料準備

如果需要創建測試資料：

```sql
-- 創建測試餐廳
INSERT INTO stores (name, address, phone) VALUES ('測試餐廳', '測試地址', '0912345678');

-- 創建測試用戶
INSERT INTO users (username, email, password) VALUES ('testuser', 'test@test.com', 'password');

-- 創建測試桌位
INSERT INTO tables (store_id, seats, quantity, status) VALUES (1, 4, 5, 1);

-- 創建測試時間段
INSERT INTO time_slots (store_id, day, start_time, end_time, is_active)
VALUES (1, '2025-07-11', '18:00:00', '19:00:00', 1);
```

## 成功指標

修復成功的指標：

- [x] Node.js 測試腳本運行無錯誤
- [x] 預約 API 返回成功狀態
- [x] 無 SQL 類型不相容錯誤
- [x] 可用性檢查正常工作
- [x] 時間段查詢正常
