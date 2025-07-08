# 訂位系統資料庫整合指南

## 概述

本指南說明如何將訂位系統與現有的 SQL Server 資料庫進行整合。

## 1. 資料庫配置

### 1.1 現有配置

```yaml
spring:
  datasource:
    driver-class-name: com.microsoft.sqlserver.jdbc.SQLServerDriver
    url: jdbc:sqlserver://localhost:1433;database=back;trustServerCertificate=true
    username: sa
    password: P@ssw0rd
  jpa:
    show-sql: true
    database-platform: org.hibernate.dialect.SQLServerDialect
    hibernate:
      ddl-auto: update
```

### 1.2 資料表結構

系統會自動建立以下資料表：

- `reservation` - 訂位記錄
- `table` - 桌位資訊
- `time_slot` - 時段設定
- `open_hour` - 營業時間
- `reservation_tables` - 訂位與桌位的關聯表

## 2. 初始化流程

### 2.1 自動初始化

系統啟動時會自動執行 `DatabaseInitializer`，建立測試資料：

- 測試用戶
- 測試餐廳
- 桌位配置
- 營業時間設定
- 時段資料生成

### 2.2 手動初始化

如果需要手動初始化，可以呼叫以下 API：

```bash
# 生成時段資料
POST /api/test/generate-time-slots?storeId=1

# 為所有餐廳生成時段
POST /api/reservations/generate-all-time-slots?daysAhead=30
```

## 3. 測試 API

### 3.1 基礎查詢測試

```bash
# 查詢所有用戶
GET /api/test/users

# 查詢所有餐廳
GET /api/test/stores

# 查詢餐廳桌位
GET /api/test/stores/1/tables

# 查詢餐廳時段
GET /api/test/stores/1/time-slots
```

### 3.2 訂位功能測試

```bash
# 建立測試訂位
POST /api/test/create-reservation?userId=1&storeId=1&date=2024-01-15&time=18:30&guests=4

# 查詢用戶訂位
GET /api/test/users/1/reservations

# 查詢餐廳訂位
GET /api/test/stores/1/reservations

# 查詢可用桌位
GET /api/test/available-tables?storeId=1&startTime=2024-01-15T18:30:00&duration=120&minSeats=4
```

## 4. 資料庫操作

### 4.1 訂位相關

```sql
-- 查詢所有訂位
SELECT * FROM reservation;

-- 查詢特定用戶的訂位
SELECT * FROM reservation WHERE user_id = 1;

-- 查詢特定餐廳的訂位
SELECT * FROM reservation WHERE store_id = 1;

-- 查詢今日訂位
SELECT * FROM reservation WHERE reserved_date = CAST(GETDATE() AS DATE);
```

### 4.2 桌位相關

```sql
-- 查詢所有桌位
SELECT * FROM table;

-- 查詢特定餐廳的桌位
SELECT * FROM table WHERE store_id = 1;

-- 查詢可用桌位
SELECT * FROM table WHERE store_id = 1 AND status = 1;
```

### 4.3 時段相關

```sql
-- 查詢所有時段
SELECT * FROM time_slot;

-- 查詢特定餐廳的時段
SELECT * FROM time_slot WHERE store_id = 1;

-- 查詢特定日期的時段
SELECT * FROM time_slot WHERE store_id = 1 AND day = '2024-01-15';
```

### 4.4 營業時間相關

```sql
-- 查詢營業時間設定
SELECT * FROM open_hour WHERE store_id = 1;

-- 查詢特定星期的營業時間
SELECT * FROM open_hour WHERE store_id = 1 AND day = 'MONDAY';
```

## 5. 常見問題

### 5.1 資料庫連線問題

- 確認 SQL Server 服務正在運行
- 確認資料庫名稱、用戶名、密碼正確
- 確認防火牆設定允許連線

### 5.2 資料表建立問題

- 確認 JPA 設定正確
- 確認 Entity 類別有正確的註解
- 檢查資料庫權限

### 5.3 時段生成問題

- 確認營業時間設定正確
- 確認餐廳狀態為啟用
- 檢查時段間隔設定

## 6. 效能優化

### 6.1 索引建議

```sql
-- 為常用查詢建立索引
CREATE INDEX idx_reservation_user_date ON reservation(user_id, reserved_date);
CREATE INDEX idx_reservation_store_date ON reservation(store_id, reserved_date);
CREATE INDEX idx_table_store_status ON table(store_id, status);
CREATE INDEX idx_time_slot_store_day ON time_slot(store_id, day);
```

### 6.2 查詢優化

- 使用分頁查詢大量資料
- 避免 N+1 查詢問題
- 使用適當的 JOIN 查詢

## 7. 監控和維護

### 7.1 資料庫監控

```sql
-- 檢查資料表大小
SELECT
    t.NAME AS TableName,
    s.Name AS SchemaName,
    p.rows AS RowCounts,
    SUM(a.total_pages) * 8 AS TotalSpaceKB
FROM sys.tables t
INNER JOIN sys.indexes i ON t.OBJECT_ID = i.object_id
INNER JOIN sys.partitions p ON i.object_id = p.OBJECT_ID AND i.index_id = p.index_id
INNER JOIN sys.allocation_units a ON p.partition_id = a.container_id
INNER JOIN sys.schemas s ON t.schema_id = s.schema_id
WHERE t.NAME IN ('reservation', 'table', 'time_slot', 'open_hour')
GROUP BY t.NAME, s.Name, p.Rows
ORDER BY t.NAME;
```

### 7.2 定期維護

- 定期清理過期的時段資料
- 備份重要資料
- 監控資料庫效能

## 8. 部署注意事項

### 8.1 生產環境

- 修改資料庫連線設定
- 關閉 SQL 語法顯示
- 設定適當的連接池大小
- 啟用資料庫連線加密

### 8.2 資料遷移

- 備份現有資料
- 測試資料遷移腳本
- 驗證資料完整性
- 設定回滾計畫

## 9. 聯絡支援

如有問題，請檢查：

1. 應用程式日誌
2. 資料庫錯誤日誌
3. 網路連線狀態
4. 資料庫權限設定
