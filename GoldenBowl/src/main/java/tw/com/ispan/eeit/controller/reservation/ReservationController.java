package tw.com.ispan.eeit.controller.reservation;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import tw.com.ispan.eeit.model.entity.reservation.ReservationBean;
import tw.com.ispan.eeit.model.enums.ReservationStatus;
import tw.com.ispan.eeit.repository.reservation.ReservationRepository;
import tw.com.ispan.eeit.service.reservation.ReservationService;
import tw.com.ispan.eeit.model.dto.reservation.ReservationDTO;
import tw.com.ispan.eeit.model.dto.reservation.ReservationSearchRequest;
import tw.com.ispan.eeit.service.reservation.ReservationQueryService;
import tw.com.ispan.eeit.model.dto.common.ApiResponse;

@RestController
@RequestMapping("/api/reservations")
@CrossOrigin(origins = "*")
public class ReservationController {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private ReservationQueryService reservationQueryService;

    /**
     * 創建新預約
     */
    @PostMapping
    public ResponseEntity<ApiResponse<ReservationBean>> createReservation(@RequestBody ReservationDTO request) {
        try {
            // 檢查是否為創建預約的請求（有 userId 和 storeId）
            if (request.userId() == null || request.storeId() == null) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("缺少必要參數：userId 或 storeId"));
            }

            ReservationBean reservation = reservationService.createReservation(
                    request.userId(),
                    request.storeId(),
                    request.reservedDate(),
                    request.reservedTime(),
                    request.guests(),
                    request.duration(),
                    request.content());

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(reservation));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("預約創建失敗: " + e.getMessage()));
        }
    }

    /**
     * 取得用戶的所有預約紀錄
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ReservationBean>> getUserReservations(@PathVariable Integer userId) {
        try {
            // 暫時硬編碼為用戶 ID 1，直到登入功能完成
            List<ReservationBean> reservations = reservationRepository.findByUserId(1);
            return ResponseEntity.ok(reservations);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 取得特定預約詳情
     */
    @GetMapping("/{id}")
    public ResponseEntity<ReservationBean> getReservation(@PathVariable Integer id) {
        try {
            Optional<ReservationBean> reservation = reservationRepository.findById(id);
            return reservation.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 修改預約資訊
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ReservationBean>> updateReservation(
            @PathVariable Integer id,
            @RequestBody ReservationUpdateRequest request) {
        try {
            Optional<ReservationBean> existingReservation = reservationRepository.findById(id);
            if (existingReservation.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("預約不存在"));
            }

            ReservationBean reservation = existingReservation.get();

            // 添加調試日誌
            System.out.println("=== 修改預約調試資訊 ===");
            System.out.println("預約ID: " + id);
            System.out.println("預約日期: " + reservation.getReservedDate());
            System.out.println("預約狀態: " + reservation.getStatus());
            System.out.println("今天日期: " + LocalDate.now());
            System.out.println("是否為今天或未來: " + (reservation.getReservedDate().isAfter(LocalDate.now())
                    || reservation.getReservedDate().isEqual(LocalDate.now())));
            System.out.println("狀態是否允許編輯: " + (ReservationStatus.PENDING.equals(reservation.getStatus())
                    || ReservationStatus.CONFIRMED.equals(reservation.getStatus())));
            System.out.println("canEditReservation 結果: " + canEditReservation(reservation));

            // 檢查是否可以修改（未過期且狀態為待確認或已確認）
            if (!canEditReservation(reservation)) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("此預約不可編輯"));
            }

            // 更新預約資訊
            if (request.getGuests() != null) {
                reservation.setGuests(request.getGuests());
            }
            if (request.getContent() != null) {
                reservation.setContent(request.getContent());
            }

            ReservationBean updatedReservation = reservationRepository.save(reservation);
            return ResponseEntity.ok(ApiResponse.success(updatedReservation));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("修改預約失敗: " + e.getMessage()));
        }
    }

    /**
     * 取消預約
     */
    @PutMapping("/{id}/cancel")
    public ResponseEntity<ReservationBean> cancelReservation(@PathVariable Integer id) {
        try {
            Optional<ReservationBean> existingReservation = reservationRepository.findById(id);
            if (existingReservation.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            ReservationBean reservation = existingReservation.get();

            // 檢查是否可以取消
            if (!canCancelReservation(reservation)) {
                return ResponseEntity.badRequest()
                        .body(null);
            }

            // 更新狀態為已取消
            reservation.setStatus(ReservationStatus.CANCELLED);
            ReservationBean cancelledReservation = reservationRepository.save(reservation);
            return ResponseEntity.ok(cancelledReservation);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 確認預約
     */
    @PutMapping("/{id}/confirm")
    public ResponseEntity<ReservationBean> confirmReservation(@PathVariable Integer id) {
        try {
            Optional<ReservationBean> existingReservation = reservationRepository.findById(id);
            if (existingReservation.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            ReservationBean reservation = existingReservation.get();

            // 檢查是否可以確認
            if (!canConfirmReservation(reservation)) {
                return ResponseEntity.badRequest()
                        .body(null);
            }

            // 更新狀態為已確認
            reservation.setStatus(ReservationStatus.CONFIRMED);
            ReservationBean confirmedReservation = reservationRepository.save(reservation);
            return ResponseEntity.ok(confirmedReservation);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 完成預約
     */
    @PutMapping("/{id}/complete")
    public ResponseEntity<ReservationBean> completeReservation(@PathVariable Integer id) {
        try {
            Optional<ReservationBean> existingReservation = reservationRepository.findById(id);
            if (existingReservation.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            ReservationBean reservation = existingReservation.get();

            // 檢查是否可以完成
            if (!canCompleteReservation(reservation)) {
                return ResponseEntity.badRequest()
                        .body(null);
            }

            // 更新狀態為已確認（因為枚舉中沒有 COMPLETED 狀態）
            reservation.setStatus(ReservationStatus.CONFIRMED);
            ReservationBean completedReservation = reservationRepository.save(reservation);
            return ResponseEntity.ok(completedReservation);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 更新預約狀態
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<ReservationBean> updateReservationStatus(
            @PathVariable Integer id,
            @RequestBody Map<String, String> request) {
        try {
            Optional<ReservationBean> existingReservation = reservationRepository.findById(id);
            if (existingReservation.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            ReservationBean reservation = existingReservation.get();
            String newStatus = request.get("status");

            if (newStatus == null) {
                return ResponseEntity.badRequest().body(null);
            }

            // 驗證狀態值
            try {
                ReservationStatus status = ReservationStatus.valueOf(newStatus.toUpperCase());
                reservation.setStatus(status);
                reservation.setUpdatedAt(java.time.LocalDateTime.now());

                ReservationBean updatedReservation = reservationRepository.save(reservation);
                return ResponseEntity.ok(updatedReservation);
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(null);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 通用搜尋 API
     */
    @GetMapping("/search")
    public ResponseEntity<List<ReservationDTO>> search(@ModelAttribute ReservationSearchRequest request) {
        return ResponseEntity.ok(reservationQueryService.searchReservations(request));
    }

    /**
     * 查詢用戶未來預約
     */
    @GetMapping("/user/{userId}/upcoming")
    public ResponseEntity<List<ReservationBean>> getUserUpcomingReservations(
            @PathVariable Integer userId) {
        return ResponseEntity.ok(reservationQueryService.findUpcomingReservationsByUserId(userId));
    }

    /**
     * 查詢用戶歷史預約
     */
    @GetMapping("/user/{userId}/historical")
    public ResponseEntity<List<ReservationBean>> getUserHistoricalReservations(
            @PathVariable Integer userId) {
        return ResponseEntity.ok(reservationQueryService.findHistoricalReservationsByUserId(userId));
    }

    /**
     * 查詢餐廳未來預約
     */
    @GetMapping("/store/{storeId}/upcoming")
    public ResponseEntity<List<ReservationBean>> getStoreUpcomingReservations(
            @PathVariable Integer storeId) {
        return ResponseEntity.ok(reservationQueryService.findUpcomingReservationsByStoreId(storeId));
    }

    /**
     * 查詢特定狀態的預約
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<ReservationDTO>> getReservationsByStatus(
            @PathVariable ReservationStatus status) {
        return ResponseEntity.ok(reservationQueryService.findReservationsByStatus(status));
    }

    /**
     * 查詢特定日期的預約
     */
    @GetMapping("/date/{date}")
    public ResponseEntity<List<ReservationDTO>> getReservationsByDate(
            @PathVariable LocalDate date) {
        return ResponseEntity.ok(reservationQueryService.findReservationsByDate(date));
    }

    /**
     * 檢查是否可以編輯預約
     */
    private boolean canEditReservation(ReservationBean reservation) {
        // 檢查是否為今天或未來日期，且狀態為待確認或已確認
        LocalDate today = LocalDate.now();
        LocalDate reservedDate = reservation.getReservedDate();

        return (reservedDate.isAfter(today) || reservedDate.isEqual(today)) &&
                (ReservationStatus.PENDING.equals(reservation.getStatus()) ||
                        ReservationStatus.CONFIRMED.equals(reservation.getStatus()));
    }

    /**
     * 檢查是否可以取消預約
     */
    private boolean canCancelReservation(ReservationBean reservation) {
        // 檢查是否為今天或未來日期，且狀態為待確認或已確認
        LocalDate today = LocalDate.now();
        LocalDate reservedDate = reservation.getReservedDate();

        return (reservedDate.isAfter(today) || reservedDate.isEqual(today)) &&
                (ReservationStatus.PENDING.equals(reservation.getStatus()) ||
                        ReservationStatus.CONFIRMED.equals(reservation.getStatus()));
    }

    /**
     * 檢查是否可以確認預約
     */
    private boolean canConfirmReservation(ReservationBean reservation) {
        // 只有待確認狀態的預約可以確認
        return ReservationStatus.PENDING.equals(reservation.getStatus());
    }

    /**
     * 檢查是否可以完成預約
     */
    private boolean canCompleteReservation(ReservationBean reservation) {
        // 已確認且日期已過的預約可以標記為完成
        LocalDate today = LocalDate.now();
        LocalDate reservedDate = reservation.getReservedDate();

        return ReservationStatus.CONFIRMED.equals(reservation.getStatus()) &&
                (reservedDate.isBefore(today) || reservedDate.isEqual(today));
    }

    /**
     * 修改預約資訊請求 DTO
     */
    public static class ReservationUpdateRequest {
        private Integer guests;
        private String content;

        // Getters and Setters
        public Integer getGuests() {
            return guests;
        }

        public void setGuests(Integer guests) {
            this.guests = guests;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }
}
