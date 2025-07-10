package tw.com.ispan.eeit.controller.reservation;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import tw.com.ispan.eeit.model.entity.reservation.ReservationBean;
import tw.com.ispan.eeit.model.entity.reservation.TableBean;
import tw.com.ispan.eeit.model.enums.ReservationStatus;
import tw.com.ispan.eeit.service.reservation.ReservationService;

@RestController
@RequestMapping("/api/reservations")
@CrossOrigin(origins = "*")
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    /**
     * 創建訂位
     */
    @PostMapping
    public Map<String, Object> createReservation(@RequestBody Map<String, Object> request) {
        try {
            Integer userId = Integer.parseInt(request.get("userId").toString());
            Integer storeId = Integer.parseInt(request.get("storeId").toString());
            LocalDate reservedDate = LocalDate.parse(request.get("reservedDate").toString());
            LocalTime reservedTime = LocalTime.parse(request.get("reservedTime").toString());
            Integer guests = Integer.parseInt(request.get("guests").toString());
            Integer duration = Integer.parseInt(request.get("duration").toString());
            String content = (String) request.get("content");

            ReservationBean reservation = reservationService.createReservation(
                    userId, storeId, reservedDate, reservedTime, guests, duration, content);

            return Map.of(
                    "success", true,
                    "reservationId", reservation.getId(),
                    "message", "訂位成功");
        } catch (Exception e) {
            return Map.of(
                    "success", false,
                    "message", "訂位失敗: " + e.getMessage());
        }
    }

    /**
     * 取消訂位
     */
    @DeleteMapping("/{reservationId}")
    public Map<String, Object> cancelReservation(
            @PathVariable Integer reservationId,
            @RequestParam Integer userId) {
        try {
            boolean success = reservationService.cancelReservation(reservationId, userId);
            return Map.of(
                    "success", success,
                    "message", success ? "取消成功" : "取消失敗");
        } catch (Exception e) {
            return Map.of(
                    "success", false,
                    "message", "取消失敗: " + e.getMessage());
        }
    }

    /**
     * 更新訂位狀態
     */
    @PutMapping("/{reservationId}/status")
    public Map<String, Object> updateStatus(
            @PathVariable Integer reservationId,
            @RequestBody Map<String, String> request) {
        try {
            ReservationStatus status = ReservationStatus.valueOf(request.get("status"));
            ReservationBean reservation = reservationService.updateReservationStatus(reservationId, status);
            return Map.of(
                    "success", true,
                    "message", "狀態更新成功");
        } catch (Exception e) {
            return Map.of(
                    "success", false,
                    "message", "更新失敗: " + e.getMessage());
        }
    }

    /**
     * 查詢用戶訂位記錄
     */
    @GetMapping("/user/{userId}")
    public Map<String, Object> getUserReservations(@PathVariable Integer userId) {
        try {
            List<ReservationBean> reservations = reservationService.getUserReservations(userId);
            return Map.of(
                    "success", true,
                    "reservations", reservations);
        } catch (Exception e) {
            return Map.of(
                    "success", false,
                    "message", "查詢失敗: " + e.getMessage());
        }
    }

    /**
     * 查詢餐廳訂位記錄
     */
    @GetMapping("/store/{storeId}")
    public Map<String, Object> getStoreReservations(@PathVariable Integer storeId) {
        try {
            List<ReservationBean> reservations = reservationService.getStoreReservations(storeId);
            return Map.of(
                    "success", true,
                    "reservations", reservations);
        } catch (Exception e) {
            return Map.of(
                    "success", false,
                    "message", "查詢失敗: " + e.getMessage());
        }
    }

    /**
     * 檢查可用性
     */
    @GetMapping("/availability")
    public Map<String, Object> checkAvailability(
            @RequestParam Integer storeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime time,
            @RequestParam Integer guests) {
        try {
            boolean available = reservationService.checkAvailability(storeId, date, time, guests);
            return Map.of(
                    "success", true,
                    "available", available,
                    "message", available ? "有可用桌位" : "無可用桌位");
        } catch (Exception e) {
            return Map.of(
                    "success", false,
                    "message", "查詢失敗: " + e.getMessage());
        }
    }

    /**
     * 查詢餐廳可用桌位
     */
    @GetMapping("/tables")
    public Map<String, Object> getAvailableTables(
            @RequestParam Integer storeId,
            @RequestParam Integer minSeats) {
        try {
            List<TableBean> tables = reservationService.getAvailableTables(storeId, minSeats);
            return Map.of(
                    "success", true,
                    "tables", tables);
        } catch (Exception e) {
            return Map.of(
                    "success", false,
                    "message", "查詢失敗: " + e.getMessage());
        }
    }
}
