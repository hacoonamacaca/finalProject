package tw.com.ispan.eeit.service.reservation;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tw.com.ispan.eeit.model.entity.UserBean;
import tw.com.ispan.eeit.model.entity.reservation.ReservationBean;
import tw.com.ispan.eeit.model.entity.reservation.TableBean;
import tw.com.ispan.eeit.model.entity.reservation.TimeSettingBean;
import tw.com.ispan.eeit.model.entity.reservation.TimeSlot;
import tw.com.ispan.eeit.model.entity.store.OpenHourBean;
import tw.com.ispan.eeit.model.entity.store.StoreBean;
import tw.com.ispan.eeit.model.enums.ReservationStatus;
import tw.com.ispan.eeit.repository.UserRepository;
import tw.com.ispan.eeit.repository.reservation.ReservationRepository;
import tw.com.ispan.eeit.repository.reservation.TableRepository;
import tw.com.ispan.eeit.repository.reservation.TimeSettingRepository;
import tw.com.ispan.eeit.repository.reservation.TimeSlotRepository;
import tw.com.ispan.eeit.repository.store.OpenHourRepository;
import tw.com.ispan.eeit.repository.store.StoreRepository;
import tw.com.ispan.eeit.model.entity.store.SpecialHoursBean;
import tw.com.ispan.eeit.repository.store.SpecialHoursRepository;
import tw.com.ispan.eeit.service.reservation.BookingAvailabilityService.BookingAvailabilityResult;

@Service
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private TableRepository tableRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TimeSlotRepository timeSlotRepository;

    @Autowired
    private TimeSettingRepository timeSettingRepository;

    @Autowired
    private OpenHourRepository openHourRepository;

    @Autowired
    private SpecialHoursRepository specialHoursRepository;

    @Autowired
    private BookingAvailabilityService bookingAvailabilityService;

    /**
     * 創建訂位 - 使用新的可用性檢查邏輯
     */
    public ReservationBean createReservation(
            Integer userId,
            Integer storeId,
            LocalDate reservedDate,
            LocalTime reservedTime,
            Integer guests,
            Integer duration,
            String content) {

        // 驗證用戶和餐廳是否存在
        UserBean user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用戶不存在: " + userId));

        StoreBean store = storeRepository.findById(storeId)
                .orElseThrow(() -> new RuntimeException("餐廳不存在: " + storeId));

        // 使用新的預約可用性檢查服務
        BookingAvailabilityResult availabilityResult = bookingAvailabilityService
                .checkBookingAvailability(storeId, reservedDate, reservedTime, guests, duration);

        if (!availabilityResult.isAvailable()) {
            throw new RuntimeException("預約失敗: " + availabilityResult.getReason());
        }

        // 組合日期和時間
        LocalDateTime reservedDateTime = LocalDateTime.of(reservedDate, reservedTime);

        // 查找可用桌位
        List<TableBean> availableTables = tableRepository.findAvailableTablesByStoreAndMinSeats(storeId, guests);
        if (availableTables.isEmpty()) {
            throw new RuntimeException("沒有足夠的桌位");
        }

        // 創建訂位
        ReservationBean reservation = new ReservationBean();
        reservation.setUserId(userId);
        reservation.setStoreId(storeId);
        reservation.setReservedDate(reservedDate);
        reservation.setReservedTime(reservedDateTime);
        reservation.setGuests(guests);
        reservation.setDuration(duration);
        reservation.setContent(content);
        reservation.setStatus(ReservationStatus.PENDING);
        reservation.setCreatedAt(LocalDateTime.now());
        reservation.setUpdatedAt(LocalDateTime.now());

        // 分配桌位
        Set<TableBean> tables = reservation.getTables();
        tables.add(availableTables.get(0)); // 簡單分配第一個可用桌位
        reservation.setTables(tables);

        return reservationRepository.save(reservation);
    }

    /**
     * 查詢用戶的訂位記錄
     */
    public List<ReservationBean> getUserReservations(Integer userId) {
        return reservationRepository.findByUserIdOrderByReservedDateDesc(userId);
    }

    /**
     * 查詢餐廳的訂位記錄
     */
    public List<ReservationBean> getStoreReservations(Integer storeId) {
        return reservationRepository.findByStoreIdOrderByReservedDateDesc(storeId);
    }

    /**
     * 更新訂位狀態
     */
    public ReservationBean updateReservationStatus(Integer reservationId, ReservationStatus status) {
        ReservationBean reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("訂位不存在: " + reservationId));

        reservation.setStatus(status);
        reservation.setUpdatedAt(LocalDateTime.now());
        return reservationRepository.save(reservation);
    }

    /**
     * 取消訂位
     */
    public boolean cancelReservation(Integer reservationId, Integer userId) {
        ReservationBean reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("訂位不存在: " + reservationId));

        // 檢查是否為訂位者本人
        if (!reservation.getUserId().equals(userId)) {
            throw new RuntimeException("無權限取消此訂位");
        }

        reservation.setStatus(ReservationStatus.CANCELLED);
        reservation.setUpdatedAt(LocalDateTime.now());
        reservationRepository.save(reservation);
        return true;
    }

    /**
     * 檢查指定時間是否有可用桌位 - 優化版本
     */
    public boolean checkAvailability(Integer storeId, LocalDate date, LocalTime time, Integer guests) {
        return checkAvailabilityWithDetails(storeId, date, time, guests, null).isAvailable();
    }

    /**
     * 檢查指定時間是否有可用桌位 - 返回詳細結果
     */
    public BookingAvailabilityResult checkAvailabilityWithDetails(
            Integer storeId, LocalDate date, LocalTime time, Integer guests, Integer duration) {
        return bookingAvailabilityService.checkBookingAvailability(storeId, date, time, guests, duration);
    }

    public List<TableBean> getAvailableTables(Integer storeId, Integer minSeats) {
        return tableRepository.findAvailableTablesByStoreAndMinSeats(storeId, minSeats);
    }

    public List<TableBean> getStoreTables(Integer storeId) {
        return tableRepository.findByStoreId(storeId);
    }

    public List<TableBean> findAvailableTables(Integer storeId, LocalDateTime startTime, int duration, int minSeats) {
        return tableRepository.findAvailableTablesByStoreAndMinSeats(storeId, minSeats);
    }

    public TableBean addTable(Integer storeId, Integer seats, Integer quantity, Boolean status) {
        StoreBean store = storeRepository.findById(storeId)
                .orElseThrow(() -> new RuntimeException("餐廳不存在: " + storeId));

        TableBean table = new TableBean();
        table.setStore(store);
        table.setSeats(seats);
        table.setQuantity(quantity);
        table.setStatus(status);

        return tableRepository.save(table);
    }

    public TableBean updateTable(Integer tableId, Integer seats, Integer quantity, Boolean status) {
        TableBean table = tableRepository.findById(tableId)
                .orElseThrow(() -> new RuntimeException("桌位不存在: " + tableId));

        if (seats != null)
            table.setSeats(seats);
        if (quantity != null)
            table.setQuantity(quantity);
        if (status != null)
            table.setStatus(status);

        return tableRepository.save(table);
    }

    public void deleteTable(Integer tableId) {
        if (!tableRepository.existsById(tableId)) {
            throw new RuntimeException("桌位不存在: " + tableId);
        }
        tableRepository.deleteById(tableId);
    }

    public TableBean getTableById(Integer tableId) {
        return tableRepository.findById(tableId)
                .orElseThrow(() -> new RuntimeException("桌位不存在: " + tableId));
    }

    public boolean isTableAvailable(Integer tableId, LocalDateTime startTime, int duration) {
        TableBean table = getTableById(tableId);
        if (!table.getStatus())
            return false;

        LocalDateTime endTime = startTime.plusMinutes(duration);
        List<ReservationBean> conflictingReservations = reservationRepository
                .findConflictingReservations(table.getStore().getId(), startTime.toLocalDate(), startTime, endTime);

        return conflictingReservations.isEmpty();
    }

    // 新增方法：取得可用時段
    public List<TimeSlot> getAvailableTimeSlots(Integer storeId) {
        StoreBean store = storeRepository.findById(storeId).orElse(null);
        if (store == null) {
            return new ArrayList<>();
        }

        // 查詢餐廳所有啟用的時段
        return timeSlotRepository.findAvailableTimeSlotsByStore(store);
    }

    // 新增方法：取得簡化的時段資料
    public List<Map<String, Object>> getAvailableTimeSlotsSimple(Integer storeId) {
        StoreBean store = storeRepository.findById(storeId).orElse(null);
        if (store == null) {
            return new ArrayList<>();
        }

        List<TimeSlot> timeSlots = timeSlotRepository.findAvailableTimeSlotsByStore(store);

        return timeSlots.stream()
                .map(slot -> {
                    Map<String, Object> simpleSlot = new HashMap<>();
                    simpleSlot.put("id", slot.getId());
                    simpleSlot.put("storeId", store.getId());
                    simpleSlot.put("storeName", store.getName());
                    simpleSlot.put("day", slot.getDay());
                    simpleSlot.put("startTime", slot.getStartTime());
                    simpleSlot.put("endTime", slot.getEndTime());
                    simpleSlot.put("isActive", slot.getIsActive());
                    return simpleSlot;
                })
                .toList();
    }

    // 新增方法：創建訂位（重載版本）
    public ReservationBean createReservation(ReservationBean reservation, List<Integer> tableIds) {
        // 驗證用戶和餐廳
        UserBean user = userRepository.findById(reservation.getUserId())
                .orElseThrow(() -> new RuntimeException("用戶不存在"));
        StoreBean store = storeRepository.findById(reservation.getStoreId())
                .orElseThrow(() -> new RuntimeException("餐廳不存在"));

        reservation.setUserId(user.getId());
        reservation.setStoreId(store.getId());
        reservation.setStatus(ReservationStatus.PENDING);
        reservation.setCreatedAt(LocalDateTime.now());
        reservation.setUpdatedAt(LocalDateTime.now());

        // 設置桌位
        Set<TableBean> tables = new HashSet<>();
        for (Integer tableId : tableIds) {
            TableBean table = tableRepository.findById(tableId)
                    .orElseThrow(() -> new RuntimeException("桌位不存在: " + tableId));
            tables.add(table);
        }
        reservation.setTables(tables);

        return reservationRepository.save(reservation);
    }

    // 新增方法：生成時段資料（使用 SQL 儲存程序）
    public void generateTimeSlotsForRestaurant(Integer storeId, int days) {
        // 驗證餐廳存在
        StoreBean store = storeRepository.findById(storeId)
                .orElseThrow(() -> new RuntimeException("餐廳不存在: " + storeId));

        try {
            // 使用 SQL 儲存程序大宗生成時段
            Integer generatedCount = timeSlotRepository.generateTimeSlotsUsingSP(storeId, days);

            System.out.println("成功透過儲存程序生成餐廳 " + storeId + " 的 " + generatedCount + " 個時段");

            // Java 後端負責處理業務邏輯：特殊營業日覆蓋
            handleSpecialBusinessHours(storeId, days);

        } catch (Exception e) {
            System.err.println("生成時段失敗: " + e.getMessage());
            // 如果儲存程序失敗，回退到原本的 Java 邏輯
            generateTimeSlotsForRestaurantFallback(storeId, days);
        }
    }

    // 回退方法：如果儲存程序失敗時使用的 Java 邏輯
    private void generateTimeSlotsForRestaurantFallback(Integer storeId, int days) {
        StoreBean store = storeRepository.findById(storeId)
                .orElseThrow(() -> new RuntimeException("餐廳不存在: " + storeId));

        TimeSettingBean setting = timeSettingRepository.findByStoreId(storeId)
                .orElseThrow(() -> new RuntimeException("未設定餐廳營業參數: " + storeId));

        Map<DayOfWeek, List<OpenHourBean>> businessHours = getBusinessHours(storeId);
        int intervalMinutes = setting.getInterval();

        LocalDate today = LocalDate.now();

        for (int i = 0; i < days; i++) {
            LocalDate date = today.plusDays(i);
            DayOfWeek dayOfWeek = date.getDayOfWeek();

            List<OpenHourBean> ranges = businessHours.get(dayOfWeek);
            if (ranges == null || ranges.isEmpty())
                continue;

            for (OpenHourBean range : ranges) {
                generateTimeSlotsForRange(store, date, range.getOpenTime(), range.getCloseTime(), intervalMinutes);
            }
        }

        System.out.println("使用回退邏輯成功生成餐廳 " + storeId + " 的 " + days + " 天時段資料");
    }

    // 處理特殊營業日覆蓋邏輯
    private void handleSpecialBusinessHours(Integer storeId, int days) {
        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusDays(days - 1);

        // 查詢指定日期範圍內的特殊營業時間
        List<SpecialHoursBean> specialHours = specialHoursRepository
                .findByStoreIdAndDateBetween(storeId, today, endDate);

        for (SpecialHoursBean special : specialHours) {
            LocalDate specialDate = special.getDate();

            if (special.getIsClose()) {
                // 特殊休假日：停用所有時段
                disableTimeSlotsByDate(storeId, specialDate);
                System.out.println("停用特殊休假日 " + specialDate + " 的所有時段");
            } else if (special.getOpenTime() != null && special.getCloseTime() != null) {
                // 特殊營業時間：先停用所有時段，再生成特殊時段
                disableTimeSlotsByDate(storeId, specialDate);
                generateSpecialTimeSlots(storeId, specialDate, special.getOpenTime(), special.getCloseTime());
                System.out.println("生成特殊營業日 " + specialDate + " 的時段");
            }
        }
    }

    // 生成特殊營業日的時段
    private void generateSpecialTimeSlots(Integer storeId, LocalDate date, LocalTime openTime, LocalTime closeTime) {
        StoreBean store = storeRepository.findById(storeId)
                .orElseThrow(() -> new RuntimeException("餐廳不存在: " + storeId));

        TimeSettingBean setting = timeSettingRepository.findByStoreId(storeId)
                .orElse(null);

        int intervalMinutes = (setting != null) ? setting.getInterval() : 30; // 預設 30 分鐘

        generateTimeSlotsForRange(store, date, openTime, closeTime, intervalMinutes);
    }

    // 判斷時段是否可預約（考慮特殊營業時間）
    public boolean isTimeSlotBookable(Integer storeId, LocalDate date, LocalTime time) {
        // 檢查時段是否存在且啟用
        List<TimeSlot> timeSlots = timeSlotRepository.findTimeSlotsByStoreAndDayAndTimeRange(
                storeRepository.findById(storeId).orElse(null),
                date, time, time.plusMinutes(1));

        if (timeSlots.isEmpty()) {
            return false; // 時段不存在
        }

        TimeSlot timeSlot = timeSlots.get(0);
        if (!timeSlot.getIsActive()) {
            return false; // 時段已停用
        }

        // 檢查是否有特殊營業時間覆蓋
        SpecialHoursBean specialHours = specialHoursRepository
                .findByStoreIdAndDate(storeId, date).orElse(null);

        if (specialHours != null) {
            if (specialHours.getIsClose()) {
                return false; // 特殊休假日
            }

            // 檢查是否在特殊營業時間內
            if (specialHours.getOpenTime() != null && specialHours.getCloseTime() != null) {
                return !time.isBefore(specialHours.getOpenTime()) &&
                        !time.isAfter(specialHours.getCloseTime().minusMinutes(1));
            }
        }

        return true; // 可預約
    }

    private void generateTimeSlotsForRange(StoreBean store, LocalDate date, LocalTime start, LocalTime end,
            int intervalMinutes) {
        for (LocalTime current = start; current.isBefore(end); current = current.plusMinutes(intervalMinutes)) {
            LocalTime slotEnd = current.plusMinutes(intervalMinutes);
            if (slotEnd.isAfter(end))
                break;
            String timeStr = current.format(DateTimeFormatter.ofPattern("HH:mm"));
            boolean exists = timeSlotRepository
                    .existsByStoreAndDayAndStartTime(store.getId(), date, timeStr) > 0;
            if (!exists) {
                TimeSlot slot = new TimeSlot();
                slot.setStore(store);
                slot.setDay(date);
                slot.setStartTime(current);
                slot.setEndTime(slotEnd);
                slot.setIsActive(true);
                timeSlotRepository.save(slot);
            }
        }
    }

    /**
     * 取得餐廳營業時段設定（從資料庫讀取）
     */
    private Map<DayOfWeek, List<OpenHourBean>> getBusinessHours(Integer storeId) {
        Map<DayOfWeek, List<OpenHourBean>> map = new HashMap<>();

        // 從資料庫讀取餐廳的營業時間設定
        List<OpenHourBean> openHours = openHourRepository.findByStoreId(storeId);

        // 如果沒有設定，使用預設值
        if (openHours.isEmpty()) {
            // 預設設定：週一休息，其它日開兩個時段
            // 注意：這裡應該在資料庫中建立預設資料，而不是在程式碼中建立
            System.out.println("警告：餐廳 " + storeId + " 沒有設定營業時間，請在資料庫中設定 open_hour 資料");
            return map;
        }

        // 將資料庫資料按星期分組
        for (OpenHourBean openHour : openHours) {
            if (openHour.getOpenTime() != null && openHour.getCloseTime() != null) {
                DayOfWeek day = openHour.getDayOfWeek();
                map.computeIfAbsent(day, k -> new ArrayList<>()).add(openHour);
            }
        }

        return map;
    }

    // TimeSlot 相關方法
    public List<TimeSlot> getAvailableTimeSlotsByDate(Integer storeId, LocalDate date, Integer guests) {
        return bookingAvailabilityService.getAvailableTimeSlotsForDate(storeId, date, guests);
    }

    public List<TimeSlot> getAvailableTimeSlotsByDate(Integer storeId, LocalDate date) {
        StoreBean store = storeRepository.findById(storeId).orElse(null);
        if (store == null) {
            return new ArrayList<>();
        }
        return timeSlotRepository.findByStoreAndDayAndIsActive(store, date, true);
    }

    public List<TimeSlot> getTimeSlotsByDateRange(Integer storeId, LocalDate startDate, LocalDate endDate) {
        // TODO: 實作按日期範圍查詢時段邏輯
        return new ArrayList<>();
    }

    public TimeSlot addTimeSlot(Integer storeId, LocalDate day, String startTime, String endTime, Boolean isActive) {
        // TODO: 實作新增時段邏輯
        TimeSlot timeSlot = new TimeSlot();
        // 設置基本屬性
        return timeSlot;
    }

    public TimeSlot updateTimeSlot(Integer timeSlotId, String startTime, String endTime, Boolean isActive) {
        // TODO: 實作更新時段邏輯
        TimeSlot timeSlot = new TimeSlot();
        // 更新屬性
        return timeSlot;
    }

    public void deleteTimeSlot(Integer timeSlotId) {
        // TODO: 實作刪除時段邏輯
        System.out.println("刪除時段: " + timeSlotId);
    }

    public TimeSlot getTimeSlotById(Integer timeSlotId) {
        // TODO: 實作查詢時段詳情邏輯
        TimeSlot timeSlot = new TimeSlot();
        return timeSlot;
    }

    public int disableTimeSlotsByDate(Integer storeId, LocalDate date) {
        // 查詢該日期的所有時段
        StoreBean store = storeRepository.findById(storeId).orElse(null);
        if (store == null) {
            return 0;
        }

        List<TimeSlot> timeSlots = timeSlotRepository.findByStoreAndDay(store, date);
        int disabledCount = 0;

        for (TimeSlot slot : timeSlots) {
            if (slot.getIsActive()) {
                slot.setIsActive(false);
                timeSlotRepository.save(slot);
                disabledCount++;
            }
        }

        return disabledCount;
    }

    public int enableTimeSlotsByDate(Integer storeId, LocalDate date) {
        // 查詢該日期的所有時段
        StoreBean store = storeRepository.findById(storeId).orElse(null);
        if (store == null) {
            return 0;
        }

        List<TimeSlot> timeSlots = timeSlotRepository.findByStoreAndDay(store, date);
        int enabledCount = 0;

        for (TimeSlot slot : timeSlots) {
            if (!slot.getIsActive()) {
                slot.setIsActive(true);
                timeSlotRepository.save(slot);
                enabledCount++;
            }
        }

        return enabledCount;
    }
}
