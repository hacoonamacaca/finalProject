package tw.com.ispan.eeit.controller.reservation;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import tw.com.ispan.eeit.exception.ResourceNotFoundException;
import tw.com.ispan.eeit.model.dto.reservation.ReservationRequest;
import tw.com.ispan.eeit.model.entity.UserBean;
import tw.com.ispan.eeit.model.entity.reservation.ReservationBean;
import tw.com.ispan.eeit.model.entity.reservation.TableBean;
import tw.com.ispan.eeit.model.entity.reservation.TimeSlot;
import tw.com.ispan.eeit.model.entity.store.StoreBean;
import tw.com.ispan.eeit.model.enums.ReservationStatus;
import tw.com.ispan.eeit.service.reservation.ReservationService;
import org.springframework.scheduling.annotation.Scheduled;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    /**
     * 建立訂位
     */
    @PostMapping
    public ResponseEntity<ReservationBean> createReservation(@RequestBody ReservationRequest request) {
        try {
            // 創建 ReservationBean 並設置關聯
            ReservationBean reservation = new ReservationBean();

            // 設置用戶和餐廳關聯（需要先創建 UserBean 和 StoreBean 的實例）
            UserBean user = new UserBean();
            user.setId(request.getUserId());
            reservation.setUser(user);

            StoreBean store = new StoreBean();
            store.setId(request.getStoreId());
            reservation.setStore(store);

            reservation.setReservedDate(request.getReservedDate());
            reservation.setReservedTime(request.getReservedTime());
            reservation.setGuests(request.getGuests());
            reservation.setDuration(request.getDuration());
            reservation.setContent(request.getContent());

            ReservationBean savedReservation = reservationService.createReservation(reservation, request.getTableIds());
            return ResponseEntity.status(HttpStatus.CREATED).body(savedReservation);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * 查詢用戶的訂位記錄
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ReservationBean>> getUserReservations(@PathVariable Integer userId) {
        try {
            List<ReservationBean> reservations = reservationService.getUserReservations(userId);
            return ResponseEntity.ok(reservations);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 查詢用戶的未來訂位
     */
    @GetMapping("/user/{userId}/upcoming")
    public ResponseEntity<List<ReservationBean>> getUserUpcomingReservations(@PathVariable Integer userId) {
        try {
            List<ReservationBean> reservations = reservationService.getUserUpcomingReservations(userId);
            return ResponseEntity.ok(reservations);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 查詢餐廳的訂位記錄
     */
    @GetMapping("/store/{storeId}")
    public ResponseEntity<List<ReservationBean>> getStoreReservations(@PathVariable Integer storeId) {
        try {
            List<ReservationBean> reservations = reservationService.getStoreReservations(storeId);
            return ResponseEntity.ok(reservations);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 查詢餐廳的未來訂位
     */
    @GetMapping("/store/{storeId}/upcoming")
    public ResponseEntity<List<ReservationBean>> getStoreUpcomingReservations(@PathVariable Integer storeId) {
        try {
            List<ReservationBean> reservations = reservationService.getStoreUpcomingReservations(storeId);
            return ResponseEntity.ok(reservations);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 查詢特定日期的訂位
     */
    @GetMapping("/store/{storeId}/date")
    public ResponseEntity<List<ReservationBean>> getReservationsByDate(
            @PathVariable Integer storeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            List<ReservationBean> reservations = reservationService.getReservationsByDate(storeId, date);
            return ResponseEntity.ok(reservations);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 查詢可用的桌位
     */
    @GetMapping("/store/{storeId}/available-tables")
    public ResponseEntity<List<TableBean>> getAvailableTables(
            @PathVariable Integer storeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam(defaultValue = "120") int duration,
            @RequestParam(defaultValue = "1") int minSeats) {
        try {
            List<TableBean> tables = reservationService.findAvailableTables(storeId, startTime, duration, minSeats);
            return ResponseEntity.ok(tables);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 查詢餐廳的可用時段
     */
    @GetMapping("/store/{storeId}/time-slots")
    public ResponseEntity<List<TimeSlot>> getAvailableTimeSlots(@PathVariable Integer storeId) {
        try {
            List<TimeSlot> timeSlots = reservationService.getAvailableTimeSlots(storeId);
            return ResponseEntity.ok(timeSlots);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 查詢餐廳在特定日期的可用時段
     */
    @GetMapping("/store/{storeId}/time-slots/date")
    public ResponseEntity<List<TimeSlot>> getAvailableTimeSlotsByDate(
            @PathVariable Integer storeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            List<TimeSlot> timeSlots = reservationService.getAvailableTimeSlotsByDate(storeId, date);
            return ResponseEntity.ok(timeSlots);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 根據ID查詢訂位
     */
    @GetMapping("/{reservationId}")
    public ResponseEntity<ReservationBean> getReservationById(@PathVariable Integer reservationId) {
        try {
            ReservationBean reservation = reservationService.getReservationById(reservationId);
            return ResponseEntity.ok(reservation);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 更新訂位狀態
     */
    @PutMapping("/{reservationId}/status")
    public ResponseEntity<ReservationBean> updateReservationStatus(
            @PathVariable Integer reservationId,
            @RequestParam ReservationStatus status) {
        try {
            ReservationBean reservation = reservationService.updateReservationStatus(reservationId, status);
            return ResponseEntity.ok(reservation);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 取消訂位
     */
    @PutMapping("/{reservationId}/cancel")
    public ResponseEntity<ReservationBean> cancelReservation(@PathVariable Integer reservationId) {
        try {
            ReservationBean reservation = reservationService.cancelReservation(reservationId);
            return ResponseEntity.ok(reservation);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 完成訂位
     */
    @PutMapping("/{reservationId}/complete")
    public ResponseEntity<ReservationBean> completeReservation(@PathVariable Integer reservationId) {
        try {
            ReservationBean reservation = reservationService.completeReservation(reservationId);
            return ResponseEntity.ok(reservation);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 刪除訂位
     */
    @DeleteMapping("/{reservationId}")
    public ResponseEntity<Void> deleteReservation(@PathVariable Integer reservationId) {
        try {
            // 這裡可以添加刪除邏輯，或者只是標記為已刪除
            reservationService.updateReservationStatus(reservationId, ReservationStatus.CANCELLED);
            return ResponseEntity.noContent().build();
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 為餐廳生成時段資料
     */
    @PostMapping("/store/{storeId}/generate-time-slots")
    public ResponseEntity<String> generateTimeSlots(
            @PathVariable Integer storeId,
            @RequestParam(defaultValue = "30") int daysAhead) {
        try {
            reservationService.generateTimeSlotsForRestaurant(storeId, daysAhead);
            return ResponseEntity.ok("成功為餐廳 " + storeId + " 生成未來 " + daysAhead + " 天的時段資料");
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("生成時段資料時發生錯誤: " + e.getMessage());
        }
    }

    /**
     * 為所有餐廳生成時段資料
     */
    @PostMapping("/generate-all-time-slots")
    public ResponseEntity<String> generateAllTimeSlots(
            @RequestParam(defaultValue = "30") int daysAhead) {
        try {
            reservationService.generateTimeSlotsForAllRestaurants(daysAhead);
            return ResponseEntity.ok("成功為所有餐廳生成未來 " + daysAhead + " 天的時段資料");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("生成時段資料時發生錯誤: " + e.getMessage());
        }
    }

    @Scheduled(cron = "0 0 1 * * ?") // 每天凌晨1點執行
    public void generateDailyTimeSlots() {
        reservationService.generateTimeSlotsForAllRestaurants(30);
    }
}
