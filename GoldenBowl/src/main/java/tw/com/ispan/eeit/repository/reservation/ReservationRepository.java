package tw.com.ispan.eeit.repository.reservation;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import tw.com.ispan.eeit.model.entity.reservation.ReservationBean;
import tw.com.ispan.eeit.model.enums.ReservationStatus;

public interface ReservationRepository extends JpaRepository<ReservationBean, Integer> {

        // 根據日期查詢訂位
        List<ReservationBean> findByReservedDate(LocalDate date);

        // 根據用戶ID查詢訂位
        List<ReservationBean> findByUserId(Integer userId);

        // 根據餐廳ID查詢訂位
        List<ReservationBean> findByStoreId(Integer storeId);

        // 根據用戶ID和日期查詢訂位
        List<ReservationBean> findByUserIdAndReservedDate(Integer userId, LocalDate date);

        // 根據餐廳ID和日期查詢訂位
        List<ReservationBean> findByStoreIdAndReservedDate(Integer storeId, LocalDate date);

        // 根據狀態查詢訂位
        List<ReservationBean> findByStatus(ReservationStatus status);

        // 根據用戶ID和狀態查詢訂位
        List<ReservationBean> findByUserIdAndStatus(Integer userId, ReservationStatus status);

        // 根據餐廳ID和狀態查詢訂位
        List<ReservationBean> findByStoreIdAndStatus(Integer storeId, ReservationStatus status);

        // 查詢特定時間範圍內的訂位
        @Query("SELECT r FROM ReservationBean r WHERE r.storeId = :storeId AND r.reservedDate = :date AND r.reservedTime BETWEEN :startTime AND :endTime")
        List<ReservationBean> findByStoreIdAndDateAndTimeRange(
                        @Param("storeId") Integer storeId,
                        @Param("date") LocalDate date,
                        @Param("startTime") java.time.LocalDateTime startTime,
                        @Param("endTime") java.time.LocalDateTime endTime);

        // 查詢用戶的未來訂位
        @Query("SELECT r FROM ReservationBean r WHERE r.userId = :userId AND r.reservedDate >= :today ORDER BY r.reservedDate, r.reservedTime")
        List<ReservationBean> findUpcomingReservationsByUserId(
                        @Param("userId") Integer userId,
                        @Param("today") LocalDate today);

        // 查詢餐廳的未來訂位
        @Query("SELECT r FROM ReservationBean r WHERE r.storeId = :storeId AND r.reservedDate >= :today ORDER BY r.reservedDate, r.reservedTime")
        List<ReservationBean> findUpcomingReservationsByStoreId(
                        @Param("storeId") Integer storeId,
                        @Param("today") LocalDate today);

        // 查詢用戶的歷史訂位
        @Query("SELECT r FROM ReservationBean r WHERE r.userId = :userId AND r.reservedDate < :today ORDER BY r.reservedDate DESC, r.reservedTime DESC")
        List<ReservationBean> findByUserIdAndReservedDateBefore(
                        @Param("userId") Integer userId,
                        @Param("today") LocalDate today);

        // 統計餐廳在日期範圍內的訂位數
        @Query("SELECT COUNT(r) FROM ReservationBean r WHERE r.storeId = :storeId AND r.reservedDate BETWEEN :startDate AND :endDate")
        Long countByStoreIdAndReservedDateBetween(
                        @Param("storeId") Integer storeId,
                        @Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate);

        // 統計餐廳在日期範圍內特定狀態的訂位數
        @Query("SELECT COUNT(r) FROM ReservationBean r WHERE r.storeId = :storeId AND r.status = :status AND r.reservedDate BETWEEN :startDate AND :endDate")
        Long countByStoreIdAndStatusAndReservedDateBetween(
                        @Param("storeId") Integer storeId,
                        @Param("status") ReservationStatus status,
                        @Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate);

        // 查詢餐廳特定日期和狀態的訂位
        @Query("SELECT r FROM ReservationBean r WHERE r.storeId = :storeId AND r.status = :status AND r.reservedDate = :date")
        List<ReservationBean> findByStoreIdAndStatusAndReservedDate(
                        @Param("storeId") Integer storeId,
                        @Param("status") ReservationStatus status,
                        @Param("date") LocalDate date);

        // 根據用戶ID查詢訂位（按日期排序）
        @Query("SELECT r FROM ReservationBean r WHERE r.userId = :userId ORDER BY r.reservedDate DESC")
        List<ReservationBean> findByUserIdOrderByReservedDateDesc(@Param("userId") Integer userId);

        // 根據餐廳ID查詢訂位（按日期排序）
        @Query("SELECT r FROM ReservationBean r WHERE r.storeId = :storeId ORDER BY r.reservedDate DESC")
        List<ReservationBean> findByStoreIdOrderByReservedDateDesc(@Param("storeId") Integer storeId);

        // 查詢衝突的訂位 - 只查詢有效狀態的預約
        @Query("SELECT r FROM ReservationBean r WHERE r.storeId = :storeId AND r.reservedDate = :date AND r.reservedTime BETWEEN :startTime AND :endTime AND r.status IN ('CONFIRMED', 'PENDING')")
        List<ReservationBean> findConflictingReservations(
                        @Param("storeId") Integer storeId,
                        @Param("date") LocalDate date,
                        @Param("startTime") java.time.LocalDateTime startTime,
                        @Param("endTime") java.time.LocalDateTime endTime);

        // === 新增 CONVERT 函數優化方法 ===

        // 統計時間範圍內的預約數量（使用 CONVERT 函數）- 只計算有效預約
        @Query(value = """
                        SELECT COUNT(*) FROM reservation r
                        WHERE r.store_id = :storeId
                        AND r.reserved_date = :date
                        AND CONVERT(time, r.reserved_time) >= CONVERT(time, :startTime)
                        AND CONVERT(time, r.reserved_time) <= CONVERT(time, :endTime)
                        AND r.status IN ('CONFIRMED', 'PENDING')
                        """, nativeQuery = true)
        Integer countReservationsInTimeRange(
                        @Param("storeId") Integer storeId,
                        @Param("date") LocalDate date,
                        @Param("startTime") LocalTime startTime,
                        @Param("endTime") LocalTime endTime);

        // 統計時間範圍內的總客人數（使用 CONVERT 函數）- 只計算有效預約
        @Query(value = """
                        SELECT COALESCE(SUM(r.guests), 0) FROM reservation r
                        WHERE r.store_id = :storeId
                        AND r.reserved_date = :date
                        AND CONVERT(time, r.reserved_time) >= CONVERT(time, :startTime)
                        AND CONVERT(time, r.reserved_time) <= CONVERT(time, :endTime)
                        AND r.status IN ('CONFIRMED', 'PENDING')
                        """, nativeQuery = true)
        Integer sumGuestsInTimeRange(
                        @Param("storeId") Integer storeId,
                        @Param("date") LocalDate date,
                        @Param("startTime") LocalTime startTime,
                        @Param("endTime") LocalTime endTime);

        // 查詢時間範圍內的衝突預約（使用 CONVERT 函數）
        @Query(value = """
                        SELECT * FROM reservation r
                        WHERE r.store_id = :storeId
                        AND r.reserved_date = :date
                        AND CONVERT(time, r.reserved_time) >= CONVERT(time, :startTime)
                        AND CONVERT(time, r.reserved_time) <= CONVERT(time, :endTime)
                        AND r.status IN ('CONFIRMED', 'PENDING')
                        """, nativeQuery = true)
        List<ReservationBean> findConflictingReservationsInTimeRange(
                        @Param("storeId") Integer storeId,
                        @Param("date") LocalDate date,
                        @Param("startTime") LocalTime startTime,
                        @Param("endTime") LocalTime endTime);

        // 檢查特定時間是否有預約（使用 CONVERT 函數）- 只檢查有效預約
        @Query(value = """
                        SELECT COUNT(*) FROM reservation r
                        WHERE r.store_id = :storeId
                        AND r.reserved_date = :date
                        AND CONVERT(time, r.reserved_time) = :time
                        AND r.status IN ('CONFIRMED', 'PENDING')
                        """, nativeQuery = true)
        Integer hasReservationAtTime(
                        @Param("storeId") Integer storeId,
                        @Param("date") LocalDate date,
                        @Param("time") LocalTime time);
}
