package tw.com.ispan.eeit.repository.reservation;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import tw.com.ispan.eeit.model.entity.reservation.TimeSlot;
import tw.com.ispan.eeit.model.entity.store.StoreBean;

@Repository
public interface TimeSlotRepository extends JpaRepository<TimeSlot, Integer> {

    // 根據餐廳查詢時段
    List<TimeSlot> findByStore(StoreBean store);

    // 根據餐廳和日期查詢時段
    List<TimeSlot> findByStoreAndDay(StoreBean store, LocalDate day);

    // 根據餐廳、日期和狀態查詢時段
    List<TimeSlot> findByStoreAndDayAndIsActive(StoreBean store, LocalDate day, Boolean isActive);

    // 根據餐廳查詢可用的時段
    @Query("SELECT ts FROM TimeSlot ts WHERE ts.store = :store AND ts.isActive = true ORDER BY ts.day, ts.startTime")
    List<TimeSlot> findAvailableTimeSlotsByStore(@Param("store") StoreBean store);

    // 根據餐廳和日期範圍查詢時段（只查詢啟用的）
    @Query("SELECT ts FROM TimeSlot ts WHERE ts.store = :store AND ts.day BETWEEN :startDate AND :endDate AND ts.isActive = true ORDER BY ts.day, ts.startTime")
    List<TimeSlot> findActiveTimeSlotsByStoreAndDateRange(
            @Param("store") StoreBean store,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    // 根據餐廳、日期和時間範圍查詢時段
    @Query("SELECT ts FROM TimeSlot ts WHERE ts.store = :store AND ts.day = :day AND ts.startTime >= :startTime AND ts.endTime <= :endTime AND ts.isActive = true")
    List<TimeSlot> findTimeSlotsByStoreAndDayAndTimeRange(
            @Param("store") StoreBean store,
            @Param("day") LocalDate day,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime);

    // 根據餐廳和日期範圍查詢時段（包含所有狀態）
    @Query("SELECT ts FROM TimeSlot ts WHERE ts.store = :store AND ts.day BETWEEN :startDate AND :endDate ORDER BY ts.day, ts.startTime")
    List<TimeSlot> findTimeSlotsByStoreAndDateRange(
            @Param("store") StoreBean store,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    // 根據餐廳、日期範圍和狀態查詢時段
    @Query("SELECT ts FROM TimeSlot ts WHERE ts.store = :store AND ts.day BETWEEN :startDate AND :endDate AND ts.isActive = :isActive ORDER BY ts.day, ts.startTime")
    List<TimeSlot> findTimeSlotsByStoreAndDateRangeAndStatus(
            @Param("store") StoreBean store,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("isActive") Boolean isActive);

    // 查詢餐廳的可用時段（未來日期且啟用）
    @Query("SELECT ts FROM TimeSlot ts WHERE ts.store = :store AND ts.day >= :today AND ts.isActive = true ORDER BY ts.day, ts.startTime")
    List<TimeSlot> findAvailableTimeSlotsByStore(
            @Param("store") StoreBean store,
            @Param("today") LocalDate today);

    // 統計餐廳在日期範圍內的時段數量
    @Query("SELECT COUNT(ts) FROM TimeSlot ts WHERE ts.store = :store AND ts.day BETWEEN :startDate AND :endDate")
    Long countByStoreAndDateRange(
            @Param("store") StoreBean store,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    // 檢查特定日期和時間是否已有時段
    @Query("SELECT COUNT(ts) > 0 FROM TimeSlot ts WHERE ts.store = :store AND ts.day = :day AND ts.startTime = :startTime")
    Boolean existsByStoreAndDayAndStartTime(
            @Param("store") StoreBean store,
            @Param("day") LocalDate day,
            @Param("startTime") LocalTime startTime);
}