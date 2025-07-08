# 訂位系統 API 文檔

## 概述

本系統提供完整的餐廳訂位管理功能，包括訂位建立、查詢、修改、取消，以及桌位和時段管理。

## 基礎 URL

```
http://localhost:8080/api
```

## 1. 訂位管理 API

### 1.1 建立訂位

**POST** `/reservations`

**請求範例：**

```json
{
  "userId": 1,
  "storeId": 1,
  "reservedDate": "2024-01-15",
  "reservedTime": "18:30",
  "guests": 4,
  "duration": 120,
  "content": "生日聚餐",
  "tableIds": [1, 2],
  "contactName": "張三",
  "contactPhone": "0912345678",
  "contactEmail": "zhang@example.com",
  "specialRequests": "需要生日蛋糕",
  "source": "web",
  "type": "dinner"
}
```

**回應：**

```json
{
  "id": 1,
  "user": { "id": 1, "name": "張三" },
  "store": { "id": 1, "name": "黃金碗餐廳" },
  "reservedDate": "2024-01-15",
  "reservedTime": "18:30:00",
  "guests": 4,
  "duration": 120,
  "content": "生日聚餐",
  "status": "PENDING",
  "createdAt": "2024-01-10T10:30:00",
  "updatedAt": "2024-01-10T10:30:00"
}
```

### 1.2 查詢用戶訂位記錄

**GET** `/reservations/user/{userId}`

**回應：**

```json
[
  {
    "id": 1,
    "store": { "id": 1, "name": "黃金碗餐廳" },
    "reservedDate": "2024-01-15",
    "reservedTime": "18:30:00",
    "guests": 4,
    "status": "CONFIRMED"
  }
]
```

### 1.3 查詢用戶未來訂位

**GET** `/reservations/user/{userId}/upcoming`

### 1.4 查詢用戶歷史訂位

**GET** `/reservations/user/{userId}/history`

### 1.5 查詢餐廳訂位記錄

**GET** `/reservations/store/{storeId}`

### 1.6 查詢餐廳今日訂位

**GET** `/reservations/store/{storeId}/today`

### 1.7 查詢特定日期訂位

**GET** `/reservations/store/{storeId}/date?date=2024-01-15`

### 1.8 更新訂位狀態

**PUT** `/reservations/{reservationId}/status?status=CONFIRMED`

**狀態選項：**

- `PENDING` - 待確認
- `CONFIRMED` - 已確認
- `CANCELLED` - 已取消
- `COMPLETED` - 已完成

### 1.9 取消訂位

**PUT** `/reservations/{reservationId}/cancel`

### 1.10 完成訂位

**PUT** `/reservations/{reservationId}/complete`

### 1.11 刪除訂位

**DELETE** `/reservations/{reservationId}`

### 1.12 查詢訂位統計

**GET** `/reservations/store/{storeId}/statistics?startDate=2024-01-01&endDate=2024-01-31`

**回應：**

```json
{
  "totalReservations": 150,
  "confirmedReservations": 120,
  "cancelledReservations": 20,
  "completedReservations": 10
}
```

### 1.13 批量取消訂位

**PUT** `/reservations/store/{storeId}/batch-cancel?date=2024-01-15&status=PENDING`

## 2. 桌位管理 API

### 2.1 查詢餐廳桌位

**GET** `/stores/{storeId}/tables`

### 2.2 查詢可用桌位

**GET** `/stores/{storeId}/tables/available?startTime=2024-01-15T18:30:00&duration=120&minSeats=4`

### 2.3 新增桌位

**POST** `/stores/{storeId}/tables?seats=4&quantity=2&status=true`

### 2.4 更新桌位

**PUT** `/stores/{storeId}/tables/{tableId}?seats=6&quantity=1&status=true`

### 2.5 刪除桌位

**DELETE** `/stores/{storeId}/tables/{tableId}`

### 2.6 查詢桌位詳情

**GET** `/stores/{storeId}/tables/{tableId}`

### 2.7 檢查桌位可用性

**GET** `/stores/{storeId}/tables/{tableId}/availability?startTime=2024-01-15T18:30:00&duration=120`

## 3. 時段管理 API

### 3.1 查詢餐廳時段

**GET** `/stores/{storeId}/time-slots`

### 3.2 查詢特定日期時段

**GET** `/stores/{storeId}/time-slots/date?date=2024-01-15`

### 3.3 查詢日期範圍時段

**GET** `/stores/{storeId}/time-slots/date-range?startDate=2024-01-01&endDate=2024-01-31`

### 3.4 新增時段

**POST** `/stores/{storeId}/time-slots?day=2024-01-15&startTime=18:00&endTime=20:00&isActive=true`

### 3.5 更新時段

**PUT** `/stores/{storeId}/time-slots/{timeSlotId}?startTime=18:30&endTime=20:30&isActive=true`

### 3.6 刪除時段

**DELETE** `/stores/{storeId}/time-slots/{timeSlotId}`

### 3.7 查詢時段詳情

**GET** `/stores/{storeId}/time-slots/{timeSlotId}`

### 3.8 生成時段資料

**POST** `/stores/{storeId}/time-slots/generate?daysAhead=30`

### 3.9 停用特定日期時段

**PUT** `/stores/{storeId}/time-slots/disable-date?date=2024-01-15`

### 3.10 啟用特定日期時段

**PUT** `/stores/{storeId}/time-slots/enable-date?date=2024-01-15`

## 4. 營業時間管理 API

### 4.1 查詢餐廳營業時間

**GET** `/stores/{storeId}/open-hours`

### 4.2 設定營業時間

**POST** `/stores/{storeId}/open-hours`

```json
{
  "dayOfWeek": "MONDAY",
  "openTime": "11:00",
  "closeTime": "22:00",
  "isOpen": true,
  "timeIntervalMinutes": 30
}
```

### 4.3 更新營業時間

**PUT** `/stores/{storeId}/open-hours/{openHourId}`

```json
{
  "openTime": "12:00",
  "closeTime": "23:00",
  "isOpen": true
}
```

### 4.4 刪除營業時間

**DELETE** `/stores/{storeId}/open-hours/{openHourId}`

### 4.5 設定預設營業時間

**POST** `/stores/{storeId}/open-hours/default`

## 5. 錯誤處理

### 5.1 錯誤回應格式

```json
{
  "timestamp": "2024-01-10T10:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "Reservation not found",
  "path": "/api/reservations/999"
}
```

### 5.2 常見錯誤碼

- `400` - 請求參數錯誤
- `404` - 資源不存在
- `409` - 時間衝突
- `422` - 業務邏輯錯誤
- `500` - 伺服器內部錯誤

## 6. 使用範例

### 6.1 完整訂位流程

1. 查詢可用時段：`GET /stores/1/time-slots/date?date=2024-01-15`
2. 查詢可用桌位：`GET /stores/1/tables/available?startTime=2024-01-15T18:30:00&duration=120&minSeats=4`
3. 建立訂位：`POST /reservations`
4. 確認訂位：`PUT /reservations/1/status?status=CONFIRMED`

### 6.2 餐廳管理流程

1. 設定營業時間：`POST /stores/1/open-hours`
2. 生成時段：`POST /stores/1/time-slots/generate?daysAhead=30`
3. 新增桌位：`POST /stores/1/tables?seats=4&quantity=2`
4. 查詢今日訂位：`GET /reservations/store/1/today`

## 7. 注意事項

1. **時間格式**：所有時間參數使用 ISO 8601 格式
2. **日期格式**：使用 `YYYY-MM-DD` 格式
3. **時區**：所有時間均為本地時間
4. **權限**：某些 API 需要適當的權限驗證
5. **限制**：單次查詢最多返回 100 筆記錄
6. **分頁**：大量資料查詢建議使用分頁參數

## 8. 版本資訊

- **版本**：1.0.0
- **更新日期**：2024-01-10
- **維護者**：GoldenBowl 開發團隊
