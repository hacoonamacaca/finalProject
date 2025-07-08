# 訂位系統啟動指南

## 1. 環境準備

### 1.1 確認資料庫

- SQL Server 正在運行
- 資料庫 `back` 已建立
- 用戶 `sa` 有足夠權限

### 1.2 確認 Java 環境

- Java 17 或以上
- Maven 已安裝

## 2. 啟動應用程式

### 2.1 編譯專案

```bash
cd GoldenBowl
mvn clean compile
```

### 2.2 啟動應用程式

```bash
mvn spring-boot:run
```

### 2.3 確認啟動成功

查看控制台輸出，應該看到：

```
開始初始化測試資料...
測試資料初始化完成！
測試用戶 ID: 1
測試餐廳 ID: 1
```

## 3. 測試 API

### 3.1 基礎功能測試

#### 查詢測試資料

```bash
# 查詢用戶
curl http://localhost:8080/api/test/users

# 查詢餐廳
curl http://localhost:8080/api/test/stores

# 查詢桌位
curl http://localhost:8080/api/test/stores/1/tables

# 查詢時段
curl http://localhost:8080/api/test/stores/1/time-slots
```

#### 建立測試訂位

```bash
curl -X POST "http://localhost:8080/api/test/create-reservation?userId=1&storeId=1&date=2024-01-15&time=18:30&guests=4"
```

#### 查詢訂位

```bash
# 查詢用戶訂位
curl http://localhost:8080/api/test/users/1/reservations

# 查詢餐廳訂位
curl http://localhost:8080/api/test/stores/1/reservations
```

### 3.2 正式 API 測試

#### 訂位管理

```bash
# 建立訂位
curl -X POST http://localhost:8080/api/reservations \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "storeId": 1,
    "reservedDate": "2024-01-15",
    "reservedTime": "18:30",
    "guests": 4,
    "duration": 120,
    "content": "測試訂位",
    "tableIds": [1]
  }'

# 查詢可用桌位
curl "http://localhost:8080/api/reservations/store/1/available-tables?startTime=2024-01-15T18:30:00&duration=120&minSeats=4"

# 查詢可用時段
curl http://localhost:8080/api/reservations/store/1/time-slots/date?date=2024-01-15
```

#### 桌位管理

```bash
# 查詢餐廳桌位
curl http://localhost:8080/api/stores/1/tables

# 新增桌位
curl -X POST "http://localhost:8080/api/stores/1/tables?seats=4&quantity=2&status=true"
```

#### 時段管理

```bash
# 查詢時段
curl http://localhost:8080/api/stores/1/time-slots

# 生成時段
curl -X POST "http://localhost:8080/api/stores/1/time-slots/generate?daysAhead=30"
```

## 4. 前端整合測試

### 4.1 確認 CORS 設定

如果前端需要存取 API，確認 CORS 設定正確。

### 4.2 測試前端連線

在前端應用程式中測試 API 連線：

```javascript
// 查詢餐廳
fetch("http://localhost:8080/api/test/stores")
  .then((response) => response.json())
  .then((data) => console.log(data));

// 建立訂位
fetch("http://localhost:8080/api/reservations", {
  method: "POST",
  headers: {
    "Content-Type": "application/json",
  },
  body: JSON.stringify({
    userId: 1,
    storeId: 1,
    reservedDate: "2024-01-15",
    reservedTime: "18:30",
    guests: 4,
    duration: 120,
    content: "前端測試",
    tableIds: [1],
  }),
})
  .then((response) => response.json())
  .then((data) => console.log(data));
```

## 5. 常見問題排除

### 5.1 資料庫連線失敗

```
錯誤：無法連線到資料庫
解決方案：
1. 確認 SQL Server 正在運行
2. 確認資料庫名稱正確
3. 確認用戶名密碼正確
4. 確認防火牆設定
```

### 5.2 資料表建立失敗

```
錯誤：無法建立資料表
解決方案：
1. 確認資料庫權限
2. 檢查 Entity 類別註解
3. 確認 JPA 設定
```

### 5.3 API 回應錯誤

```
錯誤：API 回應 500 錯誤
解決方案：
1. 檢查應用程式日誌
2. 確認資料庫資料完整性
3. 檢查 API 參數格式
```

## 6. 效能測試

### 6.1 負載測試

```bash
# 使用 Apache Bench 測試
ab -n 100 -c 10 http://localhost:8080/api/test/stores

# 測試訂位建立
ab -n 50 -c 5 -p reservation.json -T application/json http://localhost:8080/api/reservations
```

### 6.2 資料庫效能

```sql
-- 檢查查詢效能
SET STATISTICS IO ON;
SELECT * FROM reservation WHERE store_id = 1;
SET STATISTICS IO OFF;
```

## 7. 監控和日誌

### 7.1 應用程式日誌

查看 `application.yml` 中的日誌設定：

```yaml
logging:
  level:
    tw.com.ispan.eeit: DEBUG
    org.springframework.web: DEBUG
```

### 7.2 資料庫監控

```sql
-- 檢查連線數
SELECT * FROM sys.dm_exec_connections;

-- 檢查查詢效能
SELECT * FROM sys.dm_exec_query_stats;
```

## 8. 部署檢查清單

- [ ] 資料庫連線正常
- [ ] 測試資料初始化成功
- [ ] 基礎 API 測試通過
- [ ] 訂位功能正常
- [ ] 前端整合測試通過
- [ ] 效能測試達標
- [ ] 日誌監控正常
- [ ] 錯誤處理完善

## 9. 下一步

1. 整合前端應用程式
2. 實作用戶認證
3. 加入更多業務邏輯
4. 優化效能
5. 加入監控和警報
6. 準備生產環境部署
