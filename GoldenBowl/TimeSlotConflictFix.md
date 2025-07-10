# 測試批量檢查 API

## 問題描述

SQL 錯誤："equal to 運算子中的資料類型 time 和 datetime 不相容"

## 問題原因

1. **資料庫欄位類型**：`time_slots` 表的 `start_time`, `end_time` 是 `time(0)` 類型
2. **查詢參數類型**：某些查詢可能傳入 `datetime` 類型與 `time` 類型比較
3. **Spring Data JPA 自動生成查詢**：可能生成了不正確的 SQL

## 解決方案

### 1. 確認資料庫結構

```sql
-- 檢查 time_slots 表結構
SELECT
    COLUMN_NAME,
    DATA_TYPE,
    CHARACTER_MAXIMUM_LENGTH,
    IS_NULLABLE,
    COLUMN_DEFAULT
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_NAME = 'time_slots'
ORDER BY ORDINAL_POSITION;

-- 預期結果：
-- start_time: time(0)
-- end_time: time(0)
-- day: date
```

### 2. 驗證實體類定義

```java
@Entity
@Table(name = "time_slots")
public class TimeSlot {
    @Column(name = "start_time", columnDefinition = "time(0)")
    private LocalTime startTime;  // ✅ 正確：使用 LocalTime

    @Column(name = "end_time", columnDefinition = "time(0)")
    private LocalTime endTime;    // ✅ 正確：使用 LocalTime

    @Column(name = "day", columnDefinition = "date")
    private LocalDate day;        // ✅ 正確：使用 LocalDate
}
```

### 3. Repository 方法檢查清單

#### ✅ 已修復的方法（使用 CONVERT 函數）

- `findByStoreIdAndDayAndStartTimeAndIsActive()`
- `findByStoreIdAndDayAndStartTime()`
- `findByStoreIdAndDayAndStartTimeDateTime()`
- `findByStoreIdAndDayAndStartTimeDateTimeAndIsActive()`
- `existsByStoreIdAndDayAndStartTime()`
- `findAvailableTimeSlotsAfterTime()`
- `findAvailableTimeSlotsBeforeTime()`
- `hasOverlappingTimeSlots()`

#### ✅ 安全的方法（使用 StoreBean 參數 + JPQL）

- `findByStoreAndDayAndIsActive()`
- `findTimeSlotsByStoreAndDayAndExactTime()`
- `findByStoreAndDay()`
- `findByStore()`

### 4. 常見錯誤模式及解決方案

#### 錯誤模式 1：直接時間比較

```sql
-- ❌ 錯誤：直接比較 time 和 datetime
WHERE start_time = ?  -- 如果 ? 是 datetime 類型

-- ✅ 正確：使用 CONVERT 函數
WHERE CONVERT(time, start_time) = CONVERT(time, ?)
```

#### 錯誤模式 2：Spring Data JPA 自動生成

```java
// ❌ 可能導致問題：如果沒有明確定義，Spring 會自動生成
// 且可能傳入錯誤的參數類型
List<TimeSlot> findByStoreIdAndDayAndStartTimeAndIsActive(
    Integer storeId, LocalDate day, LocalDateTime startTime, Boolean isActive);

// ✅ 正確：明確定義使用 CONVERT 函數
@Query(value = """
    SELECT ts.* FROM time_slots ts
    WHERE ts.store_id = :storeId
    AND ts.day = :day
    AND CONVERT(time, ts.start_time) = CONVERT(time, :startTime)
    AND ts.is_active = :isActive
    """, nativeQuery = true)
List<TimeSlot> findByStoreIdAndDayAndStartTimeAndIsActive(
    @Param("storeId") Integer storeId,
    @Param("day") LocalDate day,
    @Param("startTime") LocalTime startTime,
    @Param("isActive") Boolean isActive);
```

### 5. 測試方法

#### 測試 1：基本時段查詢

```java
@Test
public void testTimeSlotQuery() {
    Integer storeId = 1;
    LocalDate date = LocalDate.of(2025, 7, 11);
    LocalTime time = LocalTime.of(18, 0);

    // 測試使用 CONVERT 函數的方法
    List<TimeSlot> slots = timeSlotRepository
        .findByStoreIdAndDayAndStartTimeAndIsActive(storeId, date, time, true);

    assertNotNull(slots);
    // 如果沒有拋出 SQL 異常，表示修復成功
}
```

#### 測試 2：批量時間檢查

```java
@Test
public void testBatchTimeCheck() {
    Integer storeId = 1;
    LocalDate date = LocalDate.of(2025, 7, 11);
    List<String> times = Arrays.asList("18:00", "18:30", "19:00");

    // 測試批量檢查 API
    List<BookingAvailabilityResult> results = bookingAvailabilityService
        .batchCheckAvailability(storeId, date, times, 4, 120);

    assertEquals(3, results.size());
    // 檢查是否所有結果都有正確的回應
}
```

### 6. 故障排除步驟

#### 步驟 1：檢查日誌

```bash
# 查看 Hibernate SQL 日誌
# 在 application.properties 中設置：
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE
```

#### 步驟 2：手動執行 SQL

```sql
-- 測試 CONVERT 函數是否正常工作
SELECT
    id,
    store_id,
    day,
    CONVERT(time, start_time) as start_time_converted,
    start_time,
    is_active
FROM time_slots
WHERE store_id = 1
AND day = '2025-07-11'
AND CONVERT(time, start_time) = CONVERT(time, '18:00:00');
```

#### 步驟 3：清除應用程式緩存

```bash
# 重新編譯並重啟應用程式
mvn clean compile
mvn spring-boot:run
```

### 7. 預防措施

#### 程式碼審查檢查清單

- [ ] 所有 TimeSlot 相關查詢都使用 LocalTime 參數
- [ ] 涉及時間比較的原生 SQL 都使用 CONVERT 函數
- [ ] 沒有直接的 datetime 與 time 類型比較
- [ ] Spring Data JPA 方法都有明確的 @Query 定義

#### 單元測試覆蓋

- [ ] 所有 TimeSlot Repository 方法都有單元測試
- [ ] 測試涵蓋各種時間格式和邊界情況
- [ ] 測試包含錯誤情況的處理

### 8. 監控和警報

#### 應用程式監控

```java
// 在 Service 層添加錯誤監控
@Service
public class TimeSlotService {

    @Autowired
    private TimeSlotRepository timeSlotRepository;

    public List<TimeSlot> getTimeSlots(Integer storeId, LocalDate date, LocalTime time) {
        try {
            return timeSlotRepository.findByStoreIdAndDayAndStartTimeAndIsActive(
                storeId, date, time, true);
        } catch (Exception e) {
            // 記錄錯誤並發送警報
            log.error("TimeSlot query failed: storeId={}, date={}, time={}",
                storeId, date, time, e);
            throw new TimeSlotQueryException("時段查詢失敗", e);
        }
    }
}
```

## 總結

通過以上步驟，TimeSlot 與資料庫的衝突問題應該能夠完全解決。關鍵是：

1. **使用 CONVERT 函數**處理時間類型轉換
2. **明確定義 Repository 方法**避免 Spring Data JPA 自動生成錯誤查詢
3. **確保參數類型一致**：LocalTime 對應 time 類型
4. **添加適當的測試**確保修復有效

如果問題持續存在，請檢查具體的錯誤日誌和調用堆疊，以確定問題的確切來源。
