package tw.com.ispan.eeit.service.reservation;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.ArrayList;
import java.util.HashSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tw.com.ispan.eeit.model.entity.UserBean;
import tw.com.ispan.eeit.model.entity.store.StoreBean;
import tw.com.ispan.eeit.model.entity.reservation.ReservationBean;
import tw.com.ispan.eeit.model.entity.reservation.TableBean;
import tw.com.ispan.eeit.model.enums.ReservationStatus;
import tw.com.ispan.eeit.repository.UserRepository;
import tw.com.ispan.eeit.repository.store.StoreRepository;
import tw.com.ispan.eeit.repository.reservation.ReservationRepository;
import tw.com.ispan.eeit.repository.reservation.TableRepository;
import tw.com.ispan.eeit.model.entity.reservation.TimeSlot;

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

    /**
     * 創建訂位
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

        // 檢查餐廳是否營業
        if (!store.getIsOpen()) {
            throw new RuntimeException("餐廳目前不營業");
        }

        // 組合日期和時間
        LocalDateTime reservedDateTime = LocalDateTime.of(reservedDate, reservedTime);

        // 檢查是否有足夠的桌位
        List<TableBean> availableTables = tableRepository.findAvailableTablesByStoreAndMinSeats(storeId, guests);
        if (availableTables.isEmpty()) {
            throw new RuntimeException("沒有足夠的桌位");
        }

        // 創建訂位
        ReservationBean reservation = new ReservationBean();
        reservation.setUser(user);
        reservation.setStore(store);
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
        if (!reservation.getUser().getId().equals(userId)) {
            throw new RuntimeException("無權限取消此訂位");
        }

        reservation.setStatus(ReservationStatus.CANCELLED);
        reservation.setUpdatedAt(LocalDateTime.now());
        reservationRepository.save(reservation);
        return true;
    }

    /**
     * 查詢餐廳可用桌位
     */
    public List<TableBean> getAvailableTables(Integer storeId, Integer minSeats) {
        return tableRepository.findAvailableTablesByStoreAndMinSeats(storeId, minSeats);
    }

    /**
     * 檢查指定時間是否有可用桌位
     */
    public boolean checkAvailability(Integer storeId, LocalDate date, LocalTime time, Integer guests) {
        // 檢查該時間段是否有衝突的訂位
        LocalDateTime startTime = LocalDateTime.of(date, time);
        LocalDateTime endTime = startTime.plusHours(2); // 假設用餐時間2小時

        List<ReservationBean> conflictingReservations = reservationRepository
                .findConflictingReservations(storeId, date, startTime, endTime);

        // 計算已使用的座位數
        int usedSeats = conflictingReservations.stream()
                .mapToInt(ReservationBean::getGuests)
                .sum();

        // 查詢餐廳總座位數
        List<TableBean> allTables = tableRepository.findByStoreId(storeId);
        int totalSeats = allTables.stream()
                .mapToInt(TableBean::getSeats)
                .sum();

        return (totalSeats - usedSeats) >= guests;
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
        // TODO: 實作時段查詢邏輯
        return new ArrayList<>();
    }

    // 新增方法：創建訂位（重載版本）
    public ReservationBean createReservation(ReservationBean reservation, List<Integer> tableIds) {
        // 驗證用戶和餐廳
        UserBean user = userRepository.findById(reservation.getUser().getId())
                .orElseThrow(() -> new RuntimeException("用戶不存在"));
        StoreBean store = storeRepository.findById(reservation.getStore().getId())
                .orElseThrow(() -> new RuntimeException("餐廳不存在"));

        reservation.setUser(user);
        reservation.setStore(store);
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

    // 新增方法：生成時段資料
    public void generateTimeSlotsForRestaurant(Integer storeId, int days) {
        // TODO: 實作時段生成邏輯
        System.out.println("生成餐廳 " + storeId + " 的 " + days + " 天時段資料");
    }

    // TimeSlot 相關方法
    public List<TimeSlot> getAvailableTimeSlotsByDate(Integer storeId, LocalDate date) {
        // TODO: 實作按日期查詢時段邏輯
        return new ArrayList<>();
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
        // TODO: 實作停用時段邏輯
        return 0;
    }

    public int enableTimeSlotsByDate(Integer storeId, LocalDate date) {
        // TODO: 實作啟用時段邏輯
        return 0;
    }
}
