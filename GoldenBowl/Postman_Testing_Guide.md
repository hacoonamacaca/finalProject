# GoldenBowl 餐廳預約系統 - Postman 測試指南

## 環境設定

### 1. 基礎 URL 設定

在 Postman 中設定環境變數：

- 變數名稱：`baseUrl`
- 變數值：`http://localhost:8080`

### 2. 匯入測試集合

1. 下載 `Postman_Test_Collection.json` 檔案
2. 在 Postman 中點擊 "Import"
3. 選擇該 JSON 檔案
4. 確認匯入成功

## API 端點說明

### 測試 API (用於開發測試)

#### 1. 查詢所有用戶

- **方法**: GET
- **URL**: `{{baseUrl}}/api/test/users`
- **說明**: 查詢資料庫中所有用戶資料

#### 2. 查詢所有餐廳

- **方法**: GET
- **URL**: `{{baseUrl}}/api/test/stores`
- **說明**: 查詢資料庫中所有餐廳資料

#### 3. 查詢餐廳桌位

- **方法**: GET
- **URL**: `{{baseUrl}}/api/test/stores/1/tables?storeId=1`
- **說明**: 查詢指定餐廳的所有桌位

#### 4. 查詢餐廳時段

- **方法**: GET
- **URL**: `{{baseUrl}}/api/test/stores/1/time-slots?storeId=1`
- **說明**: 查詢指定餐廳的可用時段

#### 5. 測試建立訂位

- **方法**: POST
- **URL**: `{{baseUrl}}/api/test/create-reservation`
- **Content-Type**: `application/x-www-form-urlencoded`
- **參數**:
  - `userId`: 用戶 ID (例如: 1)
  - `storeId`: 餐廳 ID (例如: 1)
  - `date`: 預約日期 (例如: 2024-12-25)
  - `time`: 預約時間 (例如: 18:00)
  - `guests`: 人數 (例如: 4)

#### 6. 查詢用戶訂位

- **方法**: GET
- **URL**: `{{baseUrl}}/api/test/users/1/reservations?userId=1`
- **說明**: 查詢指定用戶的所有訂位記錄

#### 7. 查詢餐廳訂位

- **方法**: GET
- **URL**: `{{baseUrl}}/api/test/stores/1/reservations?storeId=1`
- **說明**: 查詢指定餐廳的所有訂位記錄

#### 8. 生成時段資料

- **方法**: POST
- **URL**: `{{baseUrl}}/api/test/generate-time-slots`
- **Content-Type**: `application/x-www-form-urlencoded`
- **參數**:
  - `storeId`: 餐廳 ID (例如: 1)

#### 9. 查詢可用桌位

- **方法**: GET
- **URL**: `{{baseUrl}}/api/test/available-tables?storeId=1&startTime=2024-12-25T18:00:00&duration=120&minSeats=4`
- **參數**:
  - `storeId`: 餐廳 ID
  - `startTime`: 開始時間 (ISO 格式)
  - `duration`: 預約時長 (分鐘)
  - `minSeats`: 最少座位數

### 正式預約 API

#### 1. 創建預約

- **方法**: POST
- **URL**: `{{baseUrl}}/api/reservation/create`
- **Content-Type**: `application/json`
- **請求體**:

```json
{
  "userId": 1,
  "storeId": 1,
  "reservedDate": "2024-12-25",
  "reservedTime": "18:00",
  "guests": 4,
  "duration": 120,
  "content": "姓名: 張三, 電話: 0912345678, 備註: 靠窗座位"
}
```

#### 2. 查詢用戶預約記錄

- **方法**: GET
- **URL**: `{{baseUrl}}/api/reservation/user/1`
- **說明**: 查詢指定用戶的所有預約記錄

#### 3. 查詢餐廳預約記錄

- **方法**: GET
- **URL**: `{{baseUrl}}/api/reservation/store/1`
- **說明**: 查詢指定餐廳的所有預約記錄

#### 4. 更新預約狀態

- **方法**: PUT
- **URL**: `{{baseUrl}}/api/reservation/1/status`
- **Content-Type**: `application/json`
- **請求體**:

```json
{
  "status": "CONFIRMED"
}
```

- **狀態值**: `PENDING`, `CONFIRMED`, `CANCELLED`, `COMPLETED`

#### 5. 取消預約

- **方法**: DELETE
- **URL**: `{{baseUrl}}/api/reservation/1?userId=1`
- **參數**:
  - `userId`: 用戶 ID (用於驗證)

#### 6. 查詢餐廳可用桌位

- **方法**: GET
- **URL**: `{{baseUrl}}/api/reservation/store/1/tables?minSeats=4`
- **參數**:
  - `minSeats`: 最少座位數

#### 7. 檢查可用性

- **方法**: GET
- **URL**: `{{baseUrl}}/api/reservation/store/1/availability?date=2024-12-25&time=18:00&guests=4`
- **參數**:
  - `date`: 預約日期 (YYYY-MM-DD)
  - `time`: 預約時間 (HH:MM)
  - `guests`: 人數

## 測試步驟建議

### 1. 基礎資料測試

1. 先執行「查詢所有用戶」確認資料庫連接
2. 執行「查詢所有餐廳」確認餐廳資料
3. 執行「查詢餐廳桌位」確認桌位資料

### 2. 預約功能測試

1. 執行「檢查可用性」確認時段可用
2. 執行「創建預約」建立新預約
3. 執行「查詢用戶預約記錄」確認預約建立成功
4. 執行「更新預約狀態」測試狀態管理
5. 執行「取消預約」測試取消功能

### 3. 管理功能測試

1. 執行「查詢餐廳預約記錄」查看餐廳端預約
2. 執行「查詢餐廳可用桌位」確認桌位管理
3. 執行「生成時段資料」建立時段資料

## 常見問題

### 1. 連接錯誤

- 確認 Spring Boot 應用程式已啟動
- 確認端口 8080 未被佔用
- 檢查防火牆設定

### 2. 資料不存在

- 確認資料庫已正確初始化
- 檢查是否有測試資料
- 確認 ID 參數正確

### 3. 日期時間格式錯誤

- 日期格式：`YYYY-MM-DD`
- 時間格式：`HH:MM`
- 完整時間格式：`YYYY-MM-DDTHH:MM:SS`

### 4. JSON 格式錯誤

- 確認 Content-Type 設定正確
- 檢查 JSON 語法
- 確認所有必要欄位都有提供

## 預期回應格式

### 成功回應

```json
{
    "success": true,
    "message": "操作成功",
    "data": {...}
}
```

### 錯誤回應

```json
{
  "success": false,
  "message": "錯誤訊息"
}
```

## 測試資料建議

### 測試用戶

- ID: 1, 姓名: 測試用戶

### 測試餐廳

- ID: 1, 名稱: 測試餐廳

### 測試日期

- 使用未來日期，例如：2024-12-25

### 測試時間

- 營業時間內，例如：18:00
