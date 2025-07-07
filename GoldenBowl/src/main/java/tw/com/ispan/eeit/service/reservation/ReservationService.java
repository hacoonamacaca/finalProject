package tw.com.ispan.eeit.service.reservation;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tw.com.ispan.eeit.exception.ResourceNotFoundException;
import tw.com.ispan.eeit.model.entity.UserBean;
import tw.com.ispan.eeit.model.entity.reservation.ReservationBean;
import tw.com.ispan.eeit.model.entity.reservation.TableBean;
import tw.com.ispan.eeit.model.entity.reservation.TimeSlot;
import tw.com.ispan.eeit.model.entity.store.StoreBean;
import tw.com.ispan.eeit.model.entity.store.OpenHourBean;
import tw.com.ispan.eeit.model.enums.ReservationStatus;
import tw.com.ispan.eeit.repository.UserRepository;
import tw.com.ispan.eeit.repository.reservation.ReservationRepository;
import tw.com.ispan.eeit.repository.reservation.TableRepository;
import tw.com.ispan.eeit.repository.reservation.TimeSlotRepository;
import tw.com.ispan.eeit.repository.store.StoreRepository;
import tw.com.ispan.eeit.repository.store.OpenHourRepository;

@Service
@Transactional
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepo;

    @Autowired
    private TableRepository tableRepo;

    @Autowired
    private TimeSlotRepository timeSlotRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private StoreRepository storeRepo;

    @Autowired
    private OpenHourRepository openHourRepo;

    /**
     * 建立訂位
     */
    public ReservationBean createReservation(ReservationBean reservation, List<Integer> tableIds) {
        // 驗證用戶和餐廳是否存在
        UserBean user = userRepo.findById(reservation.getUser().getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        StoreBean store = storeRepo.findById(reservation.getStore().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Store not found"));

        // 設置關聯
        reservation.setUser(user);
        reservation.setStore(store);

        // 設置桌位
        if (tableIds != null && !tableIds.isEmpty()) {
            List<TableBean> tables = tableRepo.findAllById(tableIds);
            reservation.setTables(new HashSet<>(tables));
        }

        // 設置預設值
        reservation.setStatus(ReservationStatus.PENDING);
        reservation.setCreatedAt(LocalDateTime.now());
        reservation.setUpdatedAt(LocalDateTime.now());

        return reservationRepo.save(reservation);
    }

    /**
     * 查詢用戶的訂位記錄
     */
    public List<ReservationBean> getUserReservations(Integer userId) {
        UserBean user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return reservationRepo.findByUser(user);
    }

    /**
     * 查詢用戶的未來訂位
     */
    public List<ReservationBean> getUserUpcomingReservations(Integer userId) {
        UserBean user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return reservationRepo.findUpcomingReservationsByUser(user, LocalDate.now());
    }

    /**
     * 查詢餐廳的訂位記錄
     */
    public List<ReservationBean> getStoreReservations(Integer storeId) {
        StoreBean store = storeRepo.findById(storeId)
                .orElseThrow(() -> new ResourceNotFoundException("Store not found"));
        return reservationRepo.findByStore(store);
    }

    /**
     * 查詢餐廳的未來訂位
     */
    public List<ReservationBean> getStoreUpcomingReservations(Integer storeId) {
        StoreBean store = storeRepo.findById(storeId)
                .orElseThrow(() -> new ResourceNotFoundException("Store not found"));
        return reservationRepo.findUpcomingReservationsByStore(store, LocalDate.now());
    }

    /**
     * 查詢特定日期的訂位
     */
    public List<ReservationBean> getReservationsByDate(Integer storeId, LocalDate date) {
        StoreBean store = storeRepo.findById(storeId)
                .orElseThrow(() -> new ResourceNotFoundException("Store not found"));
        return reservationRepo.findByStoreAndReservedDate(store, date);
    }

    /**
     * 查詢可用的桌位
     */
    public List<TableBean> findAvailableTables(Integer storeId, LocalDateTime startTime, int duration, int minSeats) {
        StoreBean store = storeRepo.findById(storeId)
                .orElseThrow(() -> new ResourceNotFoundException("Store not found"));

        // 計算結束時間
        LocalDateTime endTime = startTime.plusMinutes(duration);

        // 查詢符合座位數要求的可用桌位
        List<TableBean> availableTables = tableRepo.findAvailableTablesByStoreAndMinSeats(store, minSeats);

        // 過濾掉在指定時段已被預約的桌位
        List<TableBean> filteredTables = new ArrayList<>();
        for (TableBean table : availableTables) {
            if (!isTableReservedInTimeRange(table, startTime, endTime)) {
                filteredTables.add(table);
            }
        }

        return filteredTables;
    }

    /**
     * 檢查桌位在指定時段是否已被預約
     */
    private boolean isTableReservedInTimeRange(TableBean table, LocalDateTime startTime, LocalDateTime endTime) {
        LocalDate date = startTime.toLocalDate();

        // 查詢該桌位在指定日期的所有訂位
        List<ReservationBean> reservations = reservationRepo.findByStoreAndReservedDate(table.getStore(), date);

        for (ReservationBean reservation : reservations) {
            if (reservation.getTables().contains(table) &&
                    reservation.getStatus() != ReservationStatus.CANCELLED &&
                    reservation.getStatus() != ReservationStatus.COMPLETED) {

                LocalDateTime reservationStart = reservation.getReservedTime();
                LocalDateTime reservationEnd = reservationStart.plusMinutes(reservation.getDuration());

                // 檢查時間重疊
                if (!(endTime.isBefore(reservationStart) || startTime.isAfter(reservationEnd))) {
                    return true; // 有重疊，桌位不可用
                }
            }
        }

        return false; // 沒有重疊，桌位可用
    }

    /**
     * 更新訂位狀態
     */
    public ReservationBean updateReservationStatus(Integer reservationId, ReservationStatus status) {
        ReservationBean reservation = reservationRepo.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found"));

        reservation.setStatus(status);
        reservation.setUpdatedAt(LocalDateTime.now());

        return reservationRepo.save(reservation);
    }

    /**
     * 取消訂位
     */
    public ReservationBean cancelReservation(Integer reservationId) {
        return updateReservationStatus(reservationId, ReservationStatus.CANCELLED);
    }

    /**
     * 完成訂位
     */
    public ReservationBean completeReservation(Integer reservationId) {
        return updateReservationStatus(reservationId, ReservationStatus.COMPLETED);
    }

    /**
     * 查詢餐廳的可用時段
     */
    public List<TimeSlot> getAvailableTimeSlots(Integer storeId) {
        StoreBean store = storeRepo.findById(storeId)
                .orElseThrow(() -> new ResourceNotFoundException("Store not found"));
        return timeSlotRepo.findAvailableTimeSlotsByStore(store);
    }

    /**
     * 查詢餐廳在特定日期的可用時段
     */
    public List<TimeSlot> getAvailableTimeSlotsByDate(Integer storeId, LocalDate date) {
        StoreBean store = storeRepo.findById(storeId)
                .orElseThrow(() -> new ResourceNotFoundException("Store not found"));
        return timeSlotRepo.findByStoreAndDayAndIsActive(store, date, true);
    }

    /**
     * 根據ID查詢訂位
     */
    public ReservationBean getReservationById(Integer reservationId) {
        return reservationRepo.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found"));
    }

    /**
     * 為餐廳生成未來30天的時段資料
     */
    public void generateTimeSlotsForRestaurant(Integer storeId, int daysAhead) {
        StoreBean store = storeRepo.findById(storeId)
                .orElseThrow(() -> new ResourceNotFoundException("Store not found"));

        LocalDate today = LocalDate.now();

        for (LocalDate date = today; date.isBefore(today.plusDays(daysAhead)); date = date.plusDays(1)) {
            // 在生成前檢查是否已存在
            if (!timeSlotRepo.findByStoreAndDay(store, date).isEmpty()) {
                continue; // 跳過已存在的日期
            }

            // 檢查是否為營業日
            if (isBusinessDay(store, date)) {
                // 取得營業時間
                LocalTime startTime = getOpenTime(store, date.getDayOfWeek());
                LocalTime endTime = getCloseTime(store, date.getDayOfWeek());
                int interval = getTimeInterval(store); // 預設30分鐘

                // 生成時段
                for (LocalTime time = startTime; time.isBefore(endTime); time = time.plusMinutes(interval)) {
                    TimeSlot timeSlot = new TimeSlot();
                    timeSlot.setStore(store);
                    timeSlot.setDay(date);
                    timeSlot.setStartTime(time);
                    timeSlot.setEndTime(time.plusMinutes(interval));
                    timeSlot.setIsActive(true);

                    timeSlotRepo.save(timeSlot);
                }
            }
        }
    }

    /**
     * 檢查是否為營業日
     */
    private boolean isBusinessDay(StoreBean store, LocalDate date) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();

        // 查詢該餐廳在該星期的營業時間設定
        Optional<OpenHourBean> openHour = openHourRepo.findByStoreAndDay(store, dayOfWeek);

        // 如果沒有設定，預設為營業
        if (openHour.isEmpty()) {
            return true;
        }

        // 根據設定判斷是否營業
        return openHour.get().getIsOpen();
    }

    /**
     * 取得營業開始時間
     */
    private LocalTime getOpenTime(StoreBean store, DayOfWeek dayOfWeek) {
        // 查詢該餐廳在該星期的營業時間設定
        Optional<OpenHourBean> openHour = openHourRepo.findByStoreAndDay(store, dayOfWeek);

        // 如果有設定，使用設定的時間
        if (openHour.isPresent() && openHour.get().getIsOpen()) {
            return openHour.get().getOpenTime();
        }

        // 預設營業時間
        switch (dayOfWeek) {
            case MONDAY:
            case TUESDAY:
            case WEDNESDAY:
            case THURSDAY:
            case FRIDAY:
                return LocalTime.of(11, 0); // 週一到週五 11:00
            case SATURDAY:
            case SUNDAY:
                return LocalTime.of(10, 0); // 週六週日 10:00
            default:
                return LocalTime.of(11, 0);
        }
    }

    /**
     * 取得營業結束時間
     */
    private LocalTime getCloseTime(StoreBean store, DayOfWeek dayOfWeek) {
        // 查詢該餐廳在該星期的營業時間設定
        Optional<OpenHourBean> openHour = openHourRepo.findByStoreAndDay(store, dayOfWeek);

        // 如果有設定，使用設定的時間
        if (openHour.isPresent() && openHour.get().getIsOpen()) {
            return openHour.get().getCloseTime();
        }

        // 預設營業時間
        switch (dayOfWeek) {
            case MONDAY:
            case TUESDAY:
            case WEDNESDAY:
            case THURSDAY:
            case FRIDAY:
                return LocalTime.of(22, 0); // 週一到週五 22:00
            case SATURDAY:
            case SUNDAY:
                return LocalTime.of(23, 0); // 週六週日 23:00
            default:
                return LocalTime.of(22, 0);
        }
    }

    /**
     * 取得時間間隔
     */
    private int getTimeInterval(StoreBean store) {
        // 查詢該餐廳的營業時間設定
        List<OpenHourBean> openHours = openHourRepo.findByStore(store);

        // 如果有設定，使用第一個設定的間隔
        if (!openHours.isEmpty()) {
            return openHours.get(0).getTimeIntervalMinutes();
        }

        // 預設30分鐘
        return 30;
    }

    /**
     * 為所有餐廳生成時段資料
     */
    public void generateTimeSlotsForAllRestaurants(int daysAhead) {
        List<StoreBean> allStores = storeRepo.findAll();

        for (StoreBean store : allStores) {
            if (store.getIsActive()) { // 只為營業中的餐廳生成
                generateTimeSlotsForRestaurant(store.getId(), daysAhead);
            }
        }
    }
}
