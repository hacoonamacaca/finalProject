# 餐廳訂位系統整合 API 文檔

## 概述

本系統整合了餐廳管理功能和訂位系統，提供完整的餐廳查詢、桌位管理和訂位服務。

## 主要功能模組

### 1. 餐廳管理 (Store Management)

- 餐廳註冊和資訊管理
- 餐廳類別管理
- 餐廳地址和座標管理
- 餐廳營業狀態管理

### 2. 訂位系統 (Reservation System)

- 訂位創建和管理
- 桌位可用性檢查
- 訂位狀態管理
- 訂位記錄查詢

### 3. 整合服務 (Integration Service)

- 餐廳資訊與訂位功能整合
- 可用性檢查
- 統計資訊查詢

## API 端點

### 餐廳查詢 API

#### 1. 獲取餐廳完整資訊

```
GET /api/integration/store/{storeId}
```

**回應範例：**

```json
{
  "success": true,
  "store": {
    "storeId": 1,
    "storeName": "黃金餐廳",
    "storeAddress": "台北市信義區...",
    "storeIntro": "精緻美食...",
    "isOpen": true,
    "score": 4.5,
    "categories": ["中式", "海鮮"],
    "tables": [
      {
        "tableId": 1,
        "seats": 4,
        "quantity": 2,
        "isAvailable": true
      }
    ]
  }
}
```

#### 2. 檢查餐廳可用性

```
GET /api/integration/store/{storeId}/availability?date=2024-01-15&time=18:00&guests=4
```

**回應範例：**

```json
{
  "success": true,
  "available": true,
  "message": "有可用桌位",
  "storeName": "黃金餐廳"
}
```

#### 3. 搜尋餐廳

```
GET /api/integration/search?keyword=黃金&category=中式&isOpen=true
```

### 訂位管理 API

#### 1. 創建訂位

```
POST /api/integration/reservation/create
```

**請求範例：**

```json
{
  "userId": 1,
  "storeId": 1,
  "reservedDate": "2024-01-15",
  "reservedTime": "18:00",
  "guests": 4,
  "duration": 120,
  "content": "生日聚餐"
}
```

**回應範例：**

```json
{
  "success": true,
  "reservationId": 123,
  "storeName": "黃金餐廳",
  "message": "訂位成功"
}
```

#### 2. 查詢用戶訂位記錄

```
GET /api/reservation/user/{userId}
```

#### 3. 查詢餐廳訂位記錄

```
GET /api/reservation/store/{storeId}
```

#### 4. 更新訂位狀態

```
PUT /api/reservation/{reservationId}/status
```

**請求範例：**

```json
{
  "status": "CONFIRMED"
}
```

#### 5. 取消訂位

```
DELETE /api/reservation/{reservationId}?userId=1
```

### 餐廳管理 API

#### 1. 餐廳註冊

```
POST /api/store/registerInfo
```

**請求範例：**

```json
{
  "ownerId": 1,
  "name": "黃金餐廳",
  "storeCategory": "中式",
  "storeIntro": "精緻中式料理"
}
```

#### 2. 更新餐廳地址

```
POST /api/store/updateAddress
```

**請求範例：**

```json
{
  "storeId": 1,
  "address": "台北市信義區...",
  "lat": "25.0330",
  "lon": "121.5654"
}
```

### 店主管理 API

#### 1. 店主註冊

```
POST /api/owner/register
```

**請求範例：**

```json
{
  "email": "owner@example.com",
  "password": "password123",
  "name": "張老闆",
  "phone": "0912345678"
}
```

#### 2. 店主登入

```
POST /api/owner/login
```

**請求範例：**

```json
{
  "email": "owner@example.com",
  "password": "password123"
}
```

## 資料庫關係

### 主要實體關係

1. **OwnerBean** (店主) → **StoreBean** (餐廳) - 一對多
2. **StoreBean** (餐廳) → **TableBean** (桌位) - 一對多
3. **StoreBean** (餐廳) → **ReservationBean** (訂位) - 一對多
4. **UserBean** (用戶) → **ReservationBean** (訂位) - 一對多
5. **ReservationBean** (訂位) ↔ **TableBean** (桌位) - 多對多

### 循環參考處理

使用 Jackson 註解處理 JSON 序列化時的循環參考：

- `@JsonManagedReference` - 用於父端
- `@JsonBackReference` - 用於子端

## 使用流程

### 1. 餐廳註冊流程

1. 店主註冊/登入
2. 創建餐廳基本資訊
3. 設置餐廳地址和座標
4. 添加桌位資訊

### 2. 訂位流程

1. 用戶查詢餐廳資訊
2. 檢查指定時間的可用性
3. 創建訂位
4. 系統自動分配桌位

### 3. 訂位管理流程

1. 餐廳查看訂位記錄
2. 更新訂位狀態
3. 用戶查看自己的訂位記錄
4. 用戶取消訂位

## 錯誤處理

### 常見錯誤回應

```json
{
  "success": false,
  "message": "錯誤描述"
}
```

### 錯誤類型

- 參數錯誤：缺少必要參數或格式錯誤
- 業務邏輯錯誤：餐廳不營業、無可用桌位等
- 權限錯誤：無權限執行操作
- 系統錯誤：資料庫連接失敗等

## 測試建議

### 1. 使用 Postman 測試

- 創建測試集合
- 設置環境變數
- 按流程順序測試

### 2. 測試場景

- 正常訂位流程
- 餐廳不營業情況
- 無可用桌位情況
- 重複訂位檢查
- 訂位取消流程

### 3. 資料驗證

- 檢查資料庫中的關聯關係
- 驗證 JSON 序列化是否正確
- 確認循環參考已正確處理
