# 時間格式驗證

## 🔧 問題解決方案

### 後端時間格式

後端使用 Jackson 序列化 `LocalTime` 對象，返回格式為：

```json
{
  "startTime": "14:30:00",
  "endTime": "22:00:00"
}
```

### 前端需要的格式

前端需要 `HH:mm` 格式：

```javascript
"14:30";
"22:00";
```

## 📋 修改內容

### 1. 時間格式轉換函數

```javascript
export function formatTimeToString(time) {
  if (!time) return "";

  // 如果已經是字符串格式，處理 ISO 格式 (HH:mm:ss)
  if (typeof time === "string") {
    // 如果是 ISO 格式 (14:30:00)，只取前5個字符 (14:30)
    if (time.includes(":") && time.length >= 5) {
      return time.substring(0, 5);
    }
    return time;
  }

  // 如果是 LocalTime 對象（包含 hour, minute 屬性）
  if (time && typeof time === "object") {
    try {
      const hour = String(time.hour || 0).padStart(2, "0");
      const minute = String(time.minute || 0).padStart(2, "0");
      return `${hour}:${minute}`;
    } catch (error) {
      console.error("格式化時間時發生錯誤:", error, time);
      return "";
    }
  }

  console.error("無效的時間格式:", time);
  return "";
}
```

### 2. 應用的地方

- `groupTimeSlotsByPeriod()` - 時間段分組
- `disabledTimeSlots` 計算屬性 - 已預訂時間段
- `isTimeSlotBooked()` - 時間段檢查

## 🧪 測試案例

### 測試案例 1: ISO 格式字符串

```javascript
// 輸入
formatTimeToString("14:30:00");
// 期望輸出
("14:30");
```

### 測試案例 2: 已格式化字符串

```javascript
// 輸入
formatTimeToString("14:30");
// 期望輸出
("14:30");
```

### 測試案例 3: LocalTime 對象

```javascript
// 輸入
formatTimeToString({ hour: 14, minute: 30 });
// 期望輸出
("14:30");
```

### 測試案例 4: 無效輸入

```javascript
// 輸入
formatTimeToString(null);
// 期望輸出
("");
```

## 🔍 驗證步驟

### 1. 檢查 API 響應

```bash
# 獲取時間段數據
curl "http://localhost:8080/api/booking/all-slots/1?date=2024-01-15"
```

預期響應：

```json
[
  {
    "id": 1,
    "storeId": 1,
    "day": "2024-01-15",
    "startTime": "11:00:00",
    "endTime": "12:00:00",
    "isActive": true
  }
]
```

### 2. 檢查前端顯示

在瀏覽器控制台測試：

```javascript
// 測試時間格式轉換
formatTimeToString("14:30:00"); // 應該返回 "14:30"
formatTimeToString("22:00:00"); // 應該返回 "22:00"
```

### 3. 檢查時間段分組

確認時間段正確分組為：

- 午餐時段：11:00, 12:00, 13:00
- 下午時段：15:00, 16:00
- 晚餐時段：17:00, 18:00, 19:00, 20:00

## ⚠️ 注意事項

1. **向後兼容性**: 函數同時支持 ISO 格式和已格式化字符串
2. **錯誤處理**: 無效輸入會返回空字符串
3. **性能考慮**: 字符串截取操作輕量級

## 🐛 故障排除

### 常見問題

1. **時間顯示為完整格式**: 確認使用了 `formatTimeToString` 函數
2. **時間段不正確**: 檢查後端返回的時間格式
3. **分組錯誤**: 確認時間比較邏輯正確

### 調試技巧

1. 在瀏覽器控制台檢查 API 響應
2. 使用 `console.log` 檢查轉換結果
3. 確認所有相關函數都使用了新的格式轉換

## 📊 預期結果

### 修復前

```javascript
// 後端返回
{
  startTime: "14:30:00";
}
// 前端顯示
("14:30:00"); // 或 [object Object]
```

### 修復後

```javascript
// 後端返回
{
  startTime: "14:30:00";
}
// 前端顯示
("14:30");
```
