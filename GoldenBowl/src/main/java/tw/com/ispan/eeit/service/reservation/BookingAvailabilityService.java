package tw.com.ispan.eeit.service.reservation;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tw.com.ispan.eeit.model.entity.reservation.ReservationBean;
import tw.com.ispan.eeit.model.entity.reservation.TableBean;
import tw.com.ispan.eeit.model.entity.reservation.TimeSlot;
import tw.com.ispan.eeit.model.entity.store.OpenHourBean;
import tw.com.ispan.eeit.model.entity.store.SpecialHoursBean;
import tw.com.ispan.eeit.model.entity.store.StoreBean;
import tw.com.ispan.eeit.model.enums.ReservationStatus;
import tw.com.ispan.eeit.repository.reservation.ReservationRepository;
import tw.com.ispan.eeit.repository.reservation.TableRepository;
import tw.com.ispan.eeit.repository.reservation.TimeSlotRepository;
import tw.com.ispan.eeit.repository.store.OpenHourRepository;
import tw.com.ispan.eeit.repository.store.SpecialHoursRepository;
import tw.com.ispan.eeit.repository.store.StoreRepository;

@Service
public class BookingAvailabilityService {

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private TimeSlotRepository timeSlotRepository;

    @Autowired
    private SpecialHoursRepository specialHoursRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private TableRepository tableRepository;

    @Autowired
    private OpenHourRepository openHourRepository;

    /**
     * 全面檢查指定時間是否可預約
     * 
     * @param storeId  餐廳ID
     * @param date     預約日期
     * @param time     預約時間
     * @param guests   客人數量
     * @param duration 用餐時長（分鐘）
     * @return 預約可用性結果
     */
    public BookingAvailabilityResult checkBookingAvailability(
            Integer storeId, LocalDate date, LocalTime time, Integer guests, Integer duration) {

        BookingAvailabilityResult result = new BookingAvailabilityResult();
        result.setAvailable(false);
        result.setStoreId(storeId);
        result.setDate(date);
        result.setTime(time);
        result.setGuests(guests);

        try {
            // 1. 檢查餐廳是否存在並營業
            StoreBean store = storeRepository.findById(storeId).orElse(null);
            if (store == null) {
                result.setReason("餐廳不存在");
                return result;
            }

            if (!store.getIsOpen()) {
                result.setReason("餐廳目前暫停營業");
                return result;
            }

            // 2. 檢查日期是否合法（不能預約過去的日期）
            if (date.isBefore(LocalDate.now())) {
                result.setReason("不能預約過去的日期");
                return result;
            }

            // 3. 檢查特殊營業時間覆蓋
            BookingAvailabilityResult specialHoursCheck = checkSpecialHours(storeId, date, time);
            if (!specialHoursCheck.isAvailable()) {
                return specialHoursCheck;
            }

            // 4. 檢查時段是否存在且啟用
            BookingAvailabilityResult timeSlotCheck = checkTimeSlotAvailability(store, date, time);
            if (!timeSlotCheck.isAvailable()) {
                return timeSlotCheck;
            }

            // 5. 檢查桌位容量
            BookingAvailabilityResult tableCapacityCheck = checkTableCapacity(storeId, date, time, guests, duration);
            if (!tableCapacityCheck.isAvailable()) {
                return tableCapacityCheck;
            }

            // 6. 檢查是否在正常營業時間內（如果沒有特殊時間覆蓋）
            if (specialHoursCheck.getReason() == null) { // 沒有特殊時間覆蓋
                BookingAvailabilityResult businessHoursCheck = checkBusinessHours(storeId, date, time);
                if (!businessHoursCheck.isAvailable()) {
                    return businessHoursCheck;
                }
            }

            // 所有檢查都通過
            result.setAvailable(true);
            result.setReason("可以預約");
            return result;

        } catch (Exception e) {
            result.setReason("系統錯誤: " + e.getMessage());
            return result;
        }
    }

    /**
     * 檢查特殊營業時間
     */
    private BookingAvailabilityResult checkSpecialHours(Integer storeId, LocalDate date, LocalTime time) {
        BookingAvailabilityResult result = new BookingAvailabilityResult();
        result.setAvailable(true);

        Optional<SpecialHoursBean> specialHoursOpt = specialHoursRepository.findByStoreIdAndDate(storeId, date);

        if (specialHoursOpt.isPresent()) {
            SpecialHoursBean specialHours = specialHoursOpt.get();

            // 檢查是否為特殊休假日
            if (specialHours.getIsClose()) {
                result.setAvailable(false);
                result.setReason("當日為特殊休假日");
                return result;
            }

            // 檢查特殊營業時間
            if (specialHours.getOpenTime() != null && specialHours.getCloseTime() != null) {
                LocalTime openTime = specialHours.getOpenTime();
                LocalTime closeTime = specialHours.getCloseTime();

                // 處理跨日營業（如23:00-02:00）
                if (closeTime.isBefore(openTime)) {
                    // 跨日營業：時間必須 >= openTime 或 <= closeTime
                    if (time.isBefore(openTime) && time.isAfter(closeTime)) {
                        result.setAvailable(false);
                        result.setReason("不在特殊營業時間內 (" + openTime + "-" + closeTime + ")");
                        return result;
                    }
                } else {
                    // 同日營業：時間必須在 openTime 和 closeTime 之間
                    if (time.isBefore(openTime) || time.isAfter(closeTime.minusMinutes(1))) {
                        result.setAvailable(false);
                        result.setReason("不在特殊營業時間內 (" + openTime + "-" + closeTime + ")");
                        return result;
                    }
                }

                result.setReason("特殊營業日");
            }
        }

        return result;
    }

    /**
     * 檢查時段是否存在且啟用
     */
    private BookingAvailabilityResult checkTimeSlotAvailability(StoreBean store, LocalDate date, LocalTime time) {
        BookingAvailabilityResult result = new BookingAvailabilityResult();
        result.setAvailable(false);

        // 查找對應的時段（允許一些時間誤差）
        List<TimeSlot> timeSlots = timeSlotRepository.findTimeSlotsByStoreAndDayAndTimeRange(
                store, date, time, time.plusMinutes(1));

        if (timeSlots.isEmpty()) {
            result.setReason("該時間沒有開放預約時段");
            return result;
        }

        TimeSlot timeSlot = timeSlots.get(0);
        if (!timeSlot.getIsActive()) {
            result.setReason("該時段已被停用");
            return result;
        }

        result.setAvailable(true);
        return result;
    }

    /**
     * 檢查桌位容量是否足夠
     */
    private BookingAvailabilityResult checkTableCapacity(
            Integer storeId, LocalDate date, LocalTime time, Integer guests, Integer duration) {

        BookingAvailabilityResult result = new BookingAvailabilityResult();
        result.setAvailable(false);

        // 計算用餐時間段
        LocalDateTime startTime = LocalDateTime.of(date, time);
        LocalDateTime endTime = startTime.plusMinutes(duration != null ? duration : 120); // 預設2小時

        // 查詢該時間段內已確認的預約
        List<ReservationBean> existingReservations = reservationRepository
                .findConflictingReservations(storeId, date, startTime, endTime);

        // 計算已被預約的座位數
        int reservedSeats = existingReservations.stream()
                .filter(r -> r.getStatus() == ReservationStatus.CONFIRMED ||
                        r.getStatus() == ReservationStatus.PENDING)
                .mapToInt(ReservationBean::getGuests)
                .sum();

        // 查詢餐廳總座位數
        List<TableBean> allTables = tableRepository.findByStoreId(storeId);
        int totalSeats = allTables.stream()
                .filter(table -> table.getStatus()) // 只計算啟用的桌位
                .mapToInt(TableBean::getSeats)
                .sum();

        int availableSeats = totalSeats - reservedSeats;

        if (availableSeats < guests) {
            result.setReason("座位不足，該時段僅剩 " + availableSeats + " 個座位");
            return result;
        }

        // 檢查是否有合適大小的桌位組合
        List<TableBean> suitableTables = tableRepository
                .findAvailableTablesByStoreAndMinSeats(storeId, guests);

        if (suitableTables.isEmpty()) {
            result.setReason("沒有適合 " + guests + " 人的桌位組合");
            return result;
        }

        result.setAvailable(true);
        result.setAvailableSeats(availableSeats);
        return result;
    }

    /**
     * 檢查正常營業時間
     */
    private BookingAvailabilityResult checkBusinessHours(Integer storeId, LocalDate date, LocalTime time) {
        BookingAvailabilityResult result = new BookingAvailabilityResult();
        result.setAvailable(false);

        // 取得該日期對應的星期幾
        java.time.DayOfWeek dayOfWeek = date.getDayOfWeek();

        // 查詢該餐廳該星期的營業時間
        List<OpenHourBean> openHours = openHourRepository
                .findByStoreIdAndDayOfWeek(storeId, dayOfWeek);

        if (openHours.isEmpty()) {
            result.setReason("該日餐廳不營業");
            return result;
        }

        // 檢查時間是否在任何一個營業時段內
        for (OpenHourBean openHour : openHours) {
            LocalTime openTime = openHour.getOpenTime();
            LocalTime closeTime = openHour.getCloseTime();

            if (openTime != null && closeTime != null) {
                // 處理跨日營業
                if (closeTime.isBefore(openTime)) {
                    if (time.compareTo(openTime) >= 0 || time.compareTo(closeTime) <= 0) {
                        result.setAvailable(true);
                        return result;
                    }
                } else {
                    if (time.compareTo(openTime) >= 0 && time.compareTo(closeTime.minusMinutes(1)) <= 0) {
                        result.setAvailable(true);
                        return result;
                    }
                }
            }
        }

        result.setReason("不在營業時間內");
        return result;
    }

    /**
     * 取得餐廳在指定日期的可用時段列表
     */
    public List<TimeSlot> getAvailableTimeSlotsForDate(Integer storeId, LocalDate date, Integer guests) {
        StoreBean store = storeRepository.findById(storeId).orElse(null);
        if (store == null) {
            return List.of();
        }

        // 取得基本時段列表
        List<TimeSlot> allTimeSlots = timeSlotRepository.findByStoreAndDayAndIsActive(store, date, true);

        // 過濾出真正可用的時段
        return allTimeSlots.stream()
                .filter(slot -> {
                    BookingAvailabilityResult result = checkBookingAvailability(
                            storeId, date, slot.getStartTime(), guests, 120);
                    return result.isAvailable();
                })
                .toList();
    }

    /**
     * 預約可用性結果類
     */
    public static class BookingAvailabilityResult {
        private boolean available;
        private String reason;
        private Integer storeId;
        private LocalDate date;
        private LocalTime time;
        private Integer guests;
        private Integer availableSeats;

        // Getters and Setters
        public boolean isAvailable() {
            return available;
        }

        public void setAvailable(boolean available) {
            this.available = available;
        }

        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }

        public Integer getStoreId() {
            return storeId;
        }

        public void setStoreId(Integer storeId) {
            this.storeId = storeId;
        }

        public LocalDate getDate() {
            return date;
        }

        public void setDate(LocalDate date) {
            this.date = date;
        }

        public LocalTime getTime() {
            return time;
        }

        public void setTime(LocalTime time) {
            this.time = time;
        }

        public Integer getGuests() {
            return guests;
        }

        public void setGuests(Integer guests) {
            this.guests = guests;
        }

        public Integer getAvailableSeats() {
            return availableSeats;
        }

        public void setAvailableSeats(Integer availableSeats) {
            this.availableSeats = availableSeats;
        }
    }
}