package tw.com.ispan.eeit.repository.reservation;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import tw.com.ispan.eeit.model.entity.UserBean;
import tw.com.ispan.eeit.model.entity.reservation.ReservationBean;
import tw.com.ispan.eeit.model.entity.store.StoreBean;
import tw.com.ispan.eeit.model.enums.ReservationStatus;

public interface ReservationRepository extends JpaRepository<ReservationBean, Integer> {

    // 根據日期查詢訂位
    List<ReservationBean> findByReservedDate(LocalDate date);

    // 根據用戶查詢訂位
    List<ReservationBean> findByUser(UserBean user);

    // 根據餐廳查詢訂位
    List<ReservationBean> findByStore(StoreBean store);

    // 根據用戶和日期查詢訂位
    List<ReservationBean> findByUserAndReservedDate(UserBean user, LocalDate date);

    // 根據餐廳和日期查詢訂位
    List<ReservationBean> findByStoreAndReservedDate(StoreBean store, LocalDate date);

    // 根據狀態查詢訂位
    List<ReservationBean> findByStatus(ReservationStatus status);

    // 根據用戶和狀態查詢訂位
    List<ReservationBean> findByUserAndStatus(UserBean user, ReservationStatus status);

    // 根據餐廳和狀態查詢訂位
    List<ReservationBean> findByStoreAndStatus(StoreBean store, ReservationStatus status);

    // 查詢特定時間範圍內的訂位
    @Query("SELECT r FROM ReservationBean r WHERE r.store = :store AND r.reservedDate = :date AND r.reservedTime BETWEEN :startTime AND :endTime")
    List<ReservationBean> findByStoreAndDateAndTimeRange(
            @Param("store") StoreBean store,
            @Param("date") LocalDate date,
            @Param("startTime") java.time.LocalDateTime startTime,
            @Param("endTime") java.time.LocalDateTime endTime);

    // 查詢用戶的未來訂位
    @Query("SELECT r FROM ReservationBean r WHERE r.user = :user AND r.reservedDate >= :today ORDER BY r.reservedDate, r.reservedTime")
    List<ReservationBean> findUpcomingReservationsByUser(
            @Param("user") UserBean user,
            @Param("today") LocalDate today);

    // 查詢餐廳的未來訂位
    @Query("SELECT r FROM ReservationBean r WHERE r.store = :store AND r.reservedDate >= :today ORDER BY r.reservedDate, r.reservedTime")
    List<ReservationBean> findUpcomingReservationsByStore(
            @Param("store") StoreBean store,
            @Param("today") LocalDate today);
}
