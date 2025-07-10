package tw.com.ispan.eeit.controller.reservation;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import tw.com.ispan.eeit.model.entity.reservation.TimeSlot;
import tw.com.ispan.eeit.service.reservation.BookingAvailabilityService;
import tw.com.ispan.eeit.service.reservation.BookingAvailabilityService.BookingAvailabilityResult;

@RestController
@RequestMapping("/api/booking")
public class BookingAvailabilityController {

    @Autowired
    private BookingAvailabilityService bookingAvailabilityService;

    /**
     * 檢查特定時間是否可預約
     */
    @GetMapping("/check")
    public ResponseEntity<BookingAvailabilityResult> checkAvailability(
            @RequestParam Integer storeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime time,
            @RequestParam Integer guests,
            @RequestParam(defaultValue = "120") Integer duration) {

        BookingAvailabilityResult result = bookingAvailabilityService
                .checkBookingAvailability(storeId, date, time, guests, duration);

        return ResponseEntity.ok(result);
    }

    /**
     * 批量檢查多個時間的可用性
     */
    @GetMapping("/batch-check")
    public ResponseEntity<List<BookingAvailabilityResult>> batchCheckAvailability(
            @RequestParam Integer storeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam List<String> times,
            @RequestParam Integer guests,
            @RequestParam(defaultValue = "120") Integer duration) {

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
                        errorResult.setStoreId(storeId);
                        errorResult.setDate(date);
                        errorResult.setGuests(guests);
                        return errorResult;
                    }
                })
                .toList();

        return ResponseEntity.ok(results);
    }

    /**
     * 取得餐廳在指定日期的可用時段 - 返回簡化的時段資料
     */
    @GetMapping("/slots/{storeId}")
    public ResponseEntity<List<Map<String, Object>>> getAvailableTimeSlots(
            @PathVariable Integer storeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam Integer guests) {

        List<TimeSlot> availableSlots = bookingAvailabilityService
                .getAvailableTimeSlotsForDate(storeId, date, guests);

        // 轉換為簡化的DTO，只包含必要的時段資訊
        List<Map<String, Object>> simplifiedSlots = availableSlots.stream()
                .map(slot -> {
                    Map<String, Object> slotMap = new java.util.HashMap<>();
                    slotMap.put("id", slot.getId());
                    slotMap.put("storeId", storeId);
                    slotMap.put("day", slot.getDay());
                    slotMap.put("startTime", slot.getStartTime());
                    slotMap.put("endTime", slot.getEndTime());
                    slotMap.put("isActive", slot.getIsActive());
                    return slotMap;
                })
                .toList();

        return ResponseEntity.ok(simplifiedSlots);
    }

    /**
     * 取得餐廳在指定日期的所有時段 - 返回簡化的時段資料
     */
    @GetMapping("/all-slots/{storeId}")
    public ResponseEntity<List<Map<String, Object>>> getAllTimeSlots(
            @PathVariable Integer storeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        List<TimeSlot> timeSlots = bookingAvailabilityService
                .getAvailableTimeSlotsForDate(storeId, date, 1); // 使用最小人數來獲取所有基本時段

        // 轉換為簡化的DTO，只包含必要的時段資訊
        List<Map<String, Object>> simplifiedSlots = timeSlots.stream()
                .map(slot -> {
                    Map<String, Object> slotMap = new java.util.HashMap<>();
                    slotMap.put("id", slot.getId());
                    slotMap.put("storeId", storeId);
                    slotMap.put("day", slot.getDay());
                    slotMap.put("startTime", slot.getStartTime());
                    slotMap.put("endTime", slot.getEndTime());
                    slotMap.put("isActive", slot.getIsActive());
                    return slotMap;
                })
                .toList();

        return ResponseEntity.ok(simplifiedSlots);
    }
}