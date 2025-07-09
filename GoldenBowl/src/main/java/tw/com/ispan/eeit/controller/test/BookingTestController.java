package tw.com.ispan.eeit.controller.test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import tw.com.ispan.eeit.model.entity.reservation.TimeSlot;
import tw.com.ispan.eeit.model.entity.store.SpecialHoursBean;
import tw.com.ispan.eeit.service.reservation.BookingAvailabilityService;
import tw.com.ispan.eeit.service.reservation.BookingAvailabilityService.BookingAvailabilityResult;
import tw.com.ispan.eeit.service.reservation.ReservationService;
import tw.com.ispan.eeit.service.store.SpecialHoursService;

@RestController
@RequestMapping("/api/test/booking")
public class BookingTestController {

    @Autowired
    private BookingAvailabilityService bookingAvailabilityService;

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private SpecialHoursService specialHoursService;

    /**
     * 測試時段生成
     * POST /api/test/booking/generate-time-slots?storeId=3&days=7
     */
    @PostMapping("/generate-time-slots")
    public ResponseEntity<Map<String, Object>> generateTimeSlots(
            @RequestParam Integer storeId,
            @RequestParam(defaultValue = "30") Integer days) {

        Map<String, Object> result = new HashMap<>();

        try {
            reservationService.generateTimeSlotsForRestaurant(storeId, days);
            result.put("success", true);
            result.put("message", "成功為餐廳 " + storeId + " 生成未來 " + days + " 天的時段");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "生成時段失敗: " + e.getMessage());
        }

        return ResponseEntity.ok(result);
    }

    /**
     * 測試設定特殊休假日
     * POST /api/test/booking/set-holiday?storeId=3&date=2024-12-25
     */
    @PostMapping("/set-holiday")
    public ResponseEntity<Map<String, Object>> setSpecialHoliday(
            @RequestParam Integer storeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        Map<String, Object> result = new HashMap<>();

        try {
            SpecialHoursBean holiday = specialHoursService.setSpecialHoliday(storeId, date, "測試休假日");
            result.put("success", true);
            result.put("message", "成功設定 " + date + " 為特殊休假日");
            result.put("data", holiday);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "設定休假日失敗: " + e.getMessage());
        }

        return ResponseEntity.ok(result);
    }

    /**
     * 測試設定特殊營業時間
     * POST
     * /api/test/booking/set-special-hours?storeId=3&date=2024-12-31&openTime=22:00&closeTime=02:00
     */
    @PostMapping("/set-special-hours")
    public ResponseEntity<Map<String, Object>> setSpecialBusinessHours(
            @RequestParam Integer storeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime openTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime closeTime) {

        Map<String, Object> result = new HashMap<>();

        try {
            SpecialHoursBean specialHours = specialHoursService.setSpecialBusinessHours(
                    storeId, date, openTime, closeTime, "跨年特殊營業");
            result.put("success", true);
            result.put("message", "成功設定 " + date + " 特殊營業時間 " + openTime + "-" + closeTime);
            result.put("data", specialHours);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "設定特殊營業時間失敗: " + e.getMessage());
        }

        return ResponseEntity.ok(result);
    }

    /**
     * 測試預約可用性檢查
     * GET
     * /api/test/booking/check-availability?storeId=3&date=2024-12-25&time=18:30&guests=4
     */
    @GetMapping("/check-availability")
    public ResponseEntity<Map<String, Object>> testAvailabilityCheck(
            @RequestParam Integer storeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime time,
            @RequestParam Integer guests,
            @RequestParam(defaultValue = "120") Integer duration) {

        Map<String, Object> result = new HashMap<>();

        BookingAvailabilityResult availabilityResult = bookingAvailabilityService
                .checkBookingAvailability(storeId, date, time, guests, duration);

        result.put("storeId", storeId);
        result.put("date", date);
        result.put("time", time);
        result.put("guests", guests);
        result.put("duration", duration);
        result.put("available", availabilityResult.isAvailable());
        result.put("reason", availabilityResult.getReason());
        result.put("availableSeats", availabilityResult.getAvailableSeats());

        return ResponseEntity.ok(result);
    }

    /**
     * 測試批量檢查時段可用性
     * GET /api/test/booking/batch-check?storeId=3&date=2024-12-31&guests=2
     */
    @GetMapping("/batch-check")
    public ResponseEntity<Map<String, Object>> testBatchAvailabilityCheck(
            @RequestParam Integer storeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam Integer guests) {

        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> timeChecks = new ArrayList<>();

        // 測試幾個不同的時間點
        LocalTime[] testTimes = {
                LocalTime.of(11, 30),
                LocalTime.of(12, 30),
                LocalTime.of(18, 0),
                LocalTime.of(19, 30),
                LocalTime.of(21, 0),
                LocalTime.of(23, 30) // 測試跨日時間
        };

        for (LocalTime time : testTimes) {
            BookingAvailabilityResult checkResult = bookingAvailabilityService
                    .checkBookingAvailability(storeId, date, time, guests, 120);

            Map<String, Object> timeResult = new HashMap<>();
            timeResult.put("time", time);
            timeResult.put("available", checkResult.isAvailable());
            timeResult.put("reason", checkResult.getReason());
            timeChecks.add(timeResult);
        }

        result.put("storeId", storeId);
        result.put("date", date);
        result.put("guests", guests);
        result.put("timeChecks", timeChecks);

        return ResponseEntity.ok(result);
    }

    /**
     * 測試取得可用時段
     * GET /api/test/booking/available-slots?storeId=3&date=2024-12-31&guests=2
     */
    @GetMapping("/available-slots")
    public ResponseEntity<Map<String, Object>> testGetAvailableSlots(
            @RequestParam Integer storeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam Integer guests) {

        Map<String, Object> result = new HashMap<>();

        List<TimeSlot> availableSlots = bookingAvailabilityService
                .getAvailableTimeSlotsForDate(storeId, date, guests);

        result.put("storeId", storeId);
        result.put("date", date);
        result.put("guests", guests);
        result.put("availableSlotCount", availableSlots.size());
        result.put("availableSlots", availableSlots);

        return ResponseEntity.ok(result);
    }

    /**
     * 測試完整的預約流程
     * POST /api/test/booking/full-test?storeId=3
     */
    @PostMapping("/full-test")
    public ResponseEntity<Map<String, Object>> fullTest(@RequestParam Integer storeId) {
        Map<String, Object> result = new HashMap<>();
        List<String> steps = new ArrayList<>();

        try {
            LocalDate testDate = LocalDate.now().plusDays(7);

            // 步驟 1: 生成時段
            steps.add("1. 生成時段");
            reservationService.generateTimeSlotsForRestaurant(storeId, 10);

            // 步驟 2: 設定特殊休假日
            steps.add("2. 設定特殊休假日");
            LocalDate holidayDate = testDate.plusDays(1);
            specialHoursService.setSpecialHoliday(storeId, holidayDate, "測試休假日");

            // 步驟 3: 設定特殊營業時間
            steps.add("3. 設定特殊營業時間");
            LocalDate specialDate = testDate.plusDays(2);
            specialHoursService.setSpecialBusinessHours(
                    storeId, specialDate, LocalTime.of(22, 0), LocalTime.of(2, 0), "跨日測試");

            // 步驟 4: 測試正常日期預約
            steps.add("4. 測試正常日期預約");
            BookingAvailabilityResult normalCheck = bookingAvailabilityService
                    .checkBookingAvailability(storeId, testDate, LocalTime.of(18, 30), 4, 120);

            // 步驟 5: 測試休假日預約
            steps.add("5. 測試休假日預約");
            BookingAvailabilityResult holidayCheck = bookingAvailabilityService
                    .checkBookingAvailability(storeId, holidayDate, LocalTime.of(18, 30), 4, 120);

            // 步驟 6: 測試特殊營業時間預約
            steps.add("6. 測試特殊營業時間預約");
            BookingAvailabilityResult specialCheck = bookingAvailabilityService
                    .checkBookingAvailability(storeId, specialDate, LocalTime.of(23, 30), 2, 120);

            result.put("success", true);
            result.put("message", "完整測試完成");
            result.put("steps", steps);
            result.put("results", Map.of(
                    "normalDate", Map.of(
                            "date", testDate,
                            "available", normalCheck.isAvailable(),
                            "reason", normalCheck.getReason()),
                    "holidayDate", Map.of(
                            "date", holidayDate,
                            "available", holidayCheck.isAvailable(),
                            "reason", holidayCheck.getReason()),
                    "specialDate", Map.of(
                            "date", specialDate,
                            "available", specialCheck.isAvailable(),
                            "reason", specialCheck.getReason())));

        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "測試失敗: " + e.getMessage());
            result.put("completedSteps", steps);
        }

        return ResponseEntity.ok(result);
    }
}