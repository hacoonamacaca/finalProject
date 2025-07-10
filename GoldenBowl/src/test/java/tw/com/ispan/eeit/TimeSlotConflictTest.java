package tw.com.ispan.eeit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import tw.com.ispan.eeit.model.entity.reservation.TimeSlot;
import tw.com.ispan.eeit.repository.reservation.TimeSlotRepository;
import tw.com.ispan.eeit.service.reservation.BookingAvailabilityService;
import tw.com.ispan.eeit.service.reservation.BookingAvailabilityService.BookingAvailabilityResult;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class TimeSlotConflictTest {

    @Autowired
    private TimeSlotRepository timeSlotRepository;

    @Autowired
    private BookingAvailabilityService bookingAvailabilityService;

    @Test
    public void testTimeSlotQueryWithConvertFunction() {
        // 測試使用 CONVERT 函數的查詢方法
        Integer storeId = 1;
        LocalDate date = LocalDate.of(2025, 7, 11);
        LocalTime time = LocalTime.of(18, 0);

        try {
            // 測試 findByStoreIdAndDayAndStartTimeAndIsActive 方法
            List<TimeSlot> slots = timeSlotRepository
                    .findByStoreIdAndDayAndStartTimeAndIsActive(storeId, date, time, true);

            assertNotNull(slots, "查詢結果不應為 null");
            System.out.println("✅ findByStoreIdAndDayAndStartTimeAndIsActive 測試通過");

        } catch (Exception e) {
            fail("TimeSlot 查詢失敗: " + e.getMessage());
        }
    }

    @Test
    public void testExistsByStoreIdAndDayAndStartTime() {
        // 測試 existsByStoreIdAndDayAndStartTime 方法
        Integer storeId = 1;
        LocalDate date = LocalDate.of(2025, 7, 11);
        LocalTime time = LocalTime.of(18, 0);

        try {
            Integer exists = timeSlotRepository
                    .existsByStoreIdAndDayAndStartTime(storeId, date, time);

            assertNotNull(exists, "查詢結果不應為 null");
            System.out.println("✅ existsByStoreIdAndDayAndStartTime 測試通過，結果: " + exists);

        } catch (Exception e) {
            fail("TimeSlot 存在性檢查失敗: " + e.getMessage());
        }
    }

    @Test
    public void testBatchAvailabilityCheck() {
        // 測試批量可用性檢查
        Integer storeId = 1;
        LocalDate date = LocalDate.of(2025, 7, 11);
        List<String> times = Arrays.asList("18:00", "18:30", "19:00");
        Integer guests = 4;
        Integer duration = 120;

        try {
            List<BookingAvailabilityResult> results = times.stream()
                    .map(timeStr -> {
                        try {
                            LocalTime time = LocalTime.parse(timeStr);
                            return bookingAvailabilityService
                                    .checkBookingAvailability(storeId, date, time, guests, duration);
                        } catch (Exception e) {
                            BookingAvailabilityResult errorResult = new BookingAvailabilityResult();
                            errorResult.setAvailable(false);
                            errorResult.setReason("時間格式錯誤: " + timeStr);
                            return errorResult;
                        }
                    })
                    .toList();

            assertNotNull(results, "批量檢查結果不應為 null");
            assertEquals(3, results.size(), "應該有 3 個檢查結果");

            for (int i = 0; i < results.size(); i++) {
                BookingAvailabilityResult result = results.get(i);
                assertNotNull(result, "結果 " + i + " 不應為 null");
                System.out.println("✅ 時間 " + times.get(i) + " 檢查結果: " +
                        (result.isAvailable() ? "可用" : "不可用 - " + result.getReason()));
            }

        } catch (Exception e) {
            fail("批量可用性檢查失敗: " + e.getMessage());
        }
    }

    @Test
    public void testTimeSlotWithDifferentTimeFormats() {
        // 測試不同時間格式的處理
        Integer storeId = 1;
        LocalDate date = LocalDate.of(2025, 7, 11);

        // 測試不同的時間格式
        List<LocalTime> testTimes = Arrays.asList(
                LocalTime.of(18, 0), // 18:00
                LocalTime.of(18, 30), // 18:30
                LocalTime.of(19, 0, 0), // 19:00:00
                LocalTime.of(19, 30, 0) // 19:30:00
        );

        for (LocalTime time : testTimes) {
            try {
                List<TimeSlot> slots = timeSlotRepository
                        .findByStoreIdAndDayAndStartTimeAndIsActive(storeId, date, time, true);

                assertNotNull(slots, "時間 " + time + " 的查詢結果不應為 null");
                System.out.println("✅ 時間 " + time + " 測試通過，找到 " + slots.size() + " 個時段");

            } catch (Exception e) {
                fail("時間 " + time + " 的查詢失敗: " + e.getMessage());
            }
        }
    }

    @Test
    public void testOverlappingTimeSlots() {
        // 測試重疊時段檢查
        Integer storeId = 1;
        LocalDate date = LocalDate.of(2025, 7, 11);
        LocalTime startTime = LocalTime.of(18, 0);
        LocalTime endTime = LocalTime.of(20, 0);

        try {
            Integer hasOverlap = timeSlotRepository
                    .hasOverlappingTimeSlots(storeId, date, startTime, endTime);

            assertNotNull(hasOverlap, "重疊檢查結果不應為 null");
            System.out.println("✅ 重疊時段檢查測試通過，結果: " + hasOverlap);

        } catch (Exception e) {
            fail("重疊時段檢查失敗: " + e.getMessage());
        }
    }

    @Test
    public void testAvailableTimeSlotsAfterTime() {
        // 測試查詢指定時間之後的可用時段
        Integer storeId = 1;
        LocalDate date = LocalDate.of(2025, 7, 11);
        LocalTime afterTime = LocalTime.of(17, 0);

        try {
            List<TimeSlot> slots = timeSlotRepository
                    .findAvailableTimeSlotsAfterTime(storeId, date, afterTime);

            assertNotNull(slots, "查詢結果不應為 null");
            System.out.println("✅ 查詢 " + afterTime + " 之後的時段測試通過，找到 " + slots.size() + " 個時段");

        } catch (Exception e) {
            fail("查詢指定時間之後的時段失敗: " + e.getMessage());
        }
    }

    @Test
    public void testTimeSlotWithLocalDateTime() {
        // 測試使用 LocalDateTime 參數的方法
        Integer storeId = 1;
        LocalDate date = LocalDate.of(2025, 7, 11);
        java.time.LocalDateTime dateTime = java.time.LocalDateTime.of(date, LocalTime.of(18, 0));

        try {
            List<TimeSlot> slots = timeSlotRepository
                    .findByStoreIdAndDayAndStartTimeDateTime(storeId, date, dateTime);

            assertNotNull(slots, "查詢結果不應為 null");
            System.out.println("✅ LocalDateTime 參數測試通過，找到 " + slots.size() + " 個時段");

        } catch (Exception e) {
            fail("使用 LocalDateTime 參數的查詢失敗: " + e.getMessage());
        }
    }
}