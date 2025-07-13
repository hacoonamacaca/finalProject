package tw.com.ispan.eeit.service.reservation;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tw.com.ispan.eeit.model.entity.reservation.ReservationBean;
import tw.com.ispan.eeit.model.entity.reservation.TableBean;
import tw.com.ispan.eeit.model.entity.reservation.TimeSlot;
import tw.com.ispan.eeit.model.entity.store.OpenHourBean;
import tw.com.ispan.eeit.model.entity.store.SpecialHoursBean;
import tw.com.ispan.eeit.model.entity.store.StoreBean;
import tw.com.ispan.eeit.repository.reservation.ReservationRepository;
import tw.com.ispan.eeit.repository.reservation.TableRepository;
import tw.com.ispan.eeit.repository.reservation.TimeSlotRepository;
import tw.com.ispan.eeit.repository.store.OpenHourRepository;
import tw.com.ispan.eeit.repository.store.SpecialHoursRepository;
import tw.com.ispan.eeit.repository.store.StoreRepository;

/**
 * 預約可用性檢查服務 - 包含修復的 SQL 時間類型處理
 */
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
     * 檢查預約可用性 - 主要入口點
     * 
     * @param storeId  餐廳ID
     * @param date     預約日期
     * @param time     預約時間
     * @param guests   客人數量
     * @param duration 用餐時長(分鐘)
     * @return 可用性檢查結果
     */
    public BookingAvailabilityResult checkBookingAvailability(
            Integer storeId, LocalDate date, LocalTime time, Integer guests, Integer duration) {

        BookingAvailabilityResult result = new BookingAvailabilityResult();
        result.setStoreId(storeId);
        result.setDate(date);
        result.setTime(time);
        result.setGuests(guests);

        try {
            // 1. 檢查餐廳是否存在和營業狀態
            StoreBean store = storeRepository.findById(storeId).orElse(null);
            if (store == null) {
                result.setAvailable(false);
                result.setReason("餐廳不存在");
                return result;
            }

            // 2. 檢查日期是否有效（不能是過去日期）
            if (date.isBefore(LocalDate.now())) {
                result.setAvailable(false);
                result.setReason("不能預約過去的日期");
                return result;
            }

            // 3. 檢查特殊營業時間 (節假日等)
            BookingAvailabilityResult specialHoursResult = checkSpecialHours(storeId, date, time);
            if (!specialHoursResult.isAvailable()) {
                return specialHoursResult;
            }

            // 4. 檢查時段是否開放 - 使用修復的方法
            BookingAvailabilityResult timeSlotResult = checkTimeSlotAvailability(store, date, time);
            if (!timeSlotResult.isAvailable()) {
                return timeSlotResult;
            }

            // 5. 檢查桌位容量
            BookingAvailabilityResult capacityResult = checkTableCapacity(storeId, date, time, guests, duration);
            if (!capacityResult.isAvailable()) {
                return capacityResult;
            }

            // 6. 檢查正常營業時間
            BookingAvailabilityResult businessHoursResult = checkBusinessHours(storeId, date, time);
            if (!businessHoursResult.isAvailable()) {
                return businessHoursResult;
            }

            // 全部檢查通過
            result.setAvailable(true);
            result.setReason("預約時段可用");
            result.setAvailableSeats(capacityResult.getAvailableSeats());

        } catch (Exception e) {
            result.setAvailable(false);
            result.setReason("系統錯誤: " + e.getMessage());
        }

        return result;
    }

    /**
     * 檢查特殊營業時間
     */
    private BookingAvailabilityResult checkSpecialHours(Integer storeId, LocalDate date, LocalTime time) {
        BookingAvailabilityResult result = new BookingAvailabilityResult();
        result.setAvailable(true);

        // 查詢該日期是否有特殊營業時間設定
        Optional<SpecialHoursBean> specialHoursOpt = specialHoursRepository.findByStoreIdAndDate(storeId, date);

        if (specialHoursOpt.isPresent()) {
            SpecialHoursBean specialHours = specialHoursOpt.get();

            // 如果該日期店家關閉
            if (specialHours.getIsClose()) {
                result.setAvailable(false);
                result.setReason("該日期餐廳不營業");
                return result;
            }

            // 如果有設定特殊營業時間，檢查是否在範圍內
            if (specialHours.getOpenTime() != null && specialHours.getCloseTime() != null
                    && (time.isBefore(specialHours.getOpenTime())
                            || time.isAfter(specialHours.getCloseTime()))) {
                result.setAvailable(false);
                result.setReason("不在特殊營業時間內");
                return result;
            }
        }

        return result;
    }

    /**
     * 檢查時段是否存在且啟用 - 使用 CONVERT 函數修復 SQL 類型錯誤
     */
    private BookingAvailabilityResult checkTimeSlotAvailability(StoreBean store, LocalDate date, LocalTime time) {
        BookingAvailabilityResult result = new BookingAvailabilityResult();
        result.setAvailable(false);

        // 使用修復後的方法，使用 CONVERT 函數避免時間類型錯誤
        List<TimeSlot> timeSlots = timeSlotRepository.findByStoreIdAndDayAndStartTimeAndIsActive(
                store.getId(), date, time, true);

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
     * 檢查桌位容量是否足夠 - 使用 CONVERT 函數優化
     */
    private BookingAvailabilityResult checkTableCapacity(
            Integer storeId, LocalDate date, LocalTime time, Integer guests, Integer duration) {

        BookingAvailabilityResult result = new BookingAvailabilityResult();
        result.setAvailable(false);

        // 計算用餐時間範圍
        LocalTime endTime = time.plusMinutes(duration != null ? duration : 120);

        // 使用 CONVERT 函數查詢該時間段內已確認的預約
        List<ReservationBean> existingReservations = reservationRepository
                .findConflictingReservationsInTimeRange(storeId, date, time, endTime);

        // 計算已被預約的座位數
        int reservedSeats = existingReservations.stream()
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
                .findAvailableTablesByStoreIdAndMinSeats(storeId, guests);

        if (suitableTables.isEmpty()) {
            result.setReason("沒有適合 " + guests + " 人的桌位組合");
            return result;
        }

        result.setAvailable(true);
        result.setAvailableSeats(availableSeats);
        return result;
    }

    /**
     * 檢查正常營業時間 - 使用 CONVERT 函數優化
     */
    private BookingAvailabilityResult checkBusinessHours(Integer storeId, LocalDate date, LocalTime time) {
        BookingAvailabilityResult result = new BookingAvailabilityResult();
        result.setAvailable(false);

        // 取得該日期對應的星期幾
        java.time.DayOfWeek dayOfWeek = date.getDayOfWeek();
        // 修正：將 DayOfWeek 轉換為資料庫格式（0=SUNDAY, 1=MONDAY, ..., 6=SATURDAY）
        int dayValue = dayOfWeek.getValue() == 7 ? 0 : dayOfWeek.getValue();

        // 使用 CONVERT 函數檢查是否在營業時間內
        Integer isOpen = openHourRepository.isStoreOpenAtTime(storeId, dayValue, time);

        if (isOpen == null || isOpen == 0) {
            result.setReason("不在營業時間內");
            return result;
        }

        result.setAvailable(true);
        return result;
    }

    /**
     * 取得餐廳在指定日期的所有時段（不進行可用性檢查）
     */
    public List<TimeSlot> getTimeSlotsForDate(Integer storeId, LocalDate date) {
        StoreBean store = storeRepository.findById(storeId).orElse(null);
        if (store == null) {
            return List.of();
        }

        // 直接從資料庫查詢該餐廳在指定日期的所有啟用時段
        return timeSlotRepository.findByStoreIdAndDayAndIsActive(storeId, date, true);
    }

    /**
     * 取得餐廳在指定日期的已預訂預約
     */
    public List<ReservationBean> getBookedReservations(Integer storeId, LocalDate date) {
        return reservationRepository.findByStoreIdAndReservedDate(storeId, date);
    }

    /**
     * 取得餐廳在指定日期的可用時段列表
     */
    public List<TimeSlot> getAvailableTimeSlotsForDate(Integer storeId, LocalDate date, Integer guests) {
        StoreBean store = storeRepository.findById(storeId).orElse(null);
        if (store == null) {
            return List.of();
        }

        // 取得基本時段列表 - 使用新的方法直接根據storeId查詢
        List<TimeSlot> allTimeSlots = timeSlotRepository.findByStoreIdAndDayAndIsActive(storeId, date, true);

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
     * 取得餐廳的日曆元數據 - 用於前端日期選擇器控制
     * 
     * @param storeId 餐廳ID
     * @return 包含最大日期和禁用日期列表的元數據
     */
    public Map<String, Object> getCalendarMetadata(Integer storeId) {
        Map<String, Object> metadata = new java.util.HashMap<>();

        try {
            // 1. 獲取時段資料最新日期
            LocalDate maxSlotDate = timeSlotRepository.findMaxDateByStoreId(storeId);
            if (maxSlotDate != null) {
                metadata.put("maxDate", maxSlotDate.toString());
            } else {
                // 如果沒有時段資料，使用今天作為最大日期
                metadata.put("maxDate", LocalDate.now().toString());
            }

            // 2. 獲取禁用日期列表（包含週期性公休日和特殊休假日）
            List<String> disabledDates = getClosedDates(storeId, 30)
                    .stream()
                    .map(LocalDate::toString)
                    .collect(java.util.stream.Collectors.toList());
            metadata.put("disabledDates", disabledDates);

            return metadata;
        } catch (Exception e) {
            // 發生錯誤時返回基本數據
            metadata.put("maxDate", LocalDate.now().toString());
            metadata.put("disabledDates", List.of());
            return metadata;
        }
    }

    /**
     * 取得餐廳的禁用日期列表
     * 
     * @param storeId   餐廳ID
     * @param daysAhead 往前查詢的天數
     * @return 禁用日期列表
     */
    public List<LocalDate> getClosedDates(Integer storeId, int daysAhead) {
        LocalDate start = LocalDate.now();
        LocalDate end = start.plusDays(daysAhead);

        // 獲取週期性公休日（例如：週一、週三休息）
        Set<Integer> weeklyClosedDays = new java.util.HashSet<>(openHourRepository.findClosedDaysByStore(storeId));

        // 獲取特殊休假日（例如：2025-01-01 元旦）
        List<LocalDate> specialClosedDates = specialHoursRepository.findClosedDates(storeId);

        List<LocalDate> results = new java.util.ArrayList<>();

        // 遍歷從今天開始的指定天數範圍
        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
            // 計算星期幾（MON=1, SUN=7，轉換為資料庫格式：SUN=0, MON=1, ..., SAT=6）
            int dayOfWeek = date.getDayOfWeek().getValue() % 7;

            // 檢查是否為週期性公休日或特殊休假日
            if (weeklyClosedDays.contains(dayOfWeek) || specialClosedDates.contains(date)) {
                results.add(date);
            }
        }

        return results;
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