package tw.com.ispan.eeit.controller.reservation;

import java.time.LocalDate;
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

@RestController
@RequestMapping("/api/reservations")
@CrossOrigin(origins = "*")
public class ReservationController {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private ReservationService reservationService;

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
    public ResponseEntity<ReservationBean> updateReservation(
            @PathVariable Integer id,
            @RequestBody ReservationUpdateRequest request) {
        try {
            Optional<ReservationBean> existingReservation = reservationRepository.findById(id);
            if (existingReservation.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            ReservationBean reservation = existingReservation.get();

            // 檢查是否可以修改（未過期且狀態為已確認）
            if (!canEditReservation(reservation)) {
                return ResponseEntity.badRequest()
                        .body(null); // 可以返回錯誤訊息
            }

            // 更新預約資訊
            if (request.getGuests() != null) {
                reservation.setGuests(request.getGuests());
            }
            if (request.getContent() != null) {
                reservation.setContent(request.getContent());
            }

            ReservationBean updatedReservation = reservationRepository.save(reservation);
            return ResponseEntity.ok(updatedReservation);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
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
     * 檢查是否可以編輯預約
     */
    private boolean canEditReservation(ReservationBean reservation) {
        // 檢查是否未過期且狀態為已確認
        LocalDate today = LocalDate.now();
        LocalDate reservedDate = reservation.getReservedDate();

        return reservedDate.isAfter(today) && "CONFIRMED".equals(reservation.getStatus());
    }

    /**
     * 檢查是否可以取消預約
     */
    private boolean canCancelReservation(ReservationBean reservation) {
        // 檢查是否未過期且狀態為待確認或已確認
        LocalDate today = LocalDate.now();
        LocalDate reservedDate = reservation.getReservedDate();

        return reservedDate.isAfter(today) &&
                ("PENDING".equals(reservation.getStatus()) || "CONFIRMED".equals(reservation.getStatus()));
    }

    /**
     * 檢查是否可以確認預約
     */
    private boolean canConfirmReservation(ReservationBean reservation) {
        // 只有待確認狀態的預約可以確認
        return "PENDING".equals(reservation.getStatus());
    }

    /**
     * 檢查是否可以完成預約
     */
    private boolean canCompleteReservation(ReservationBean reservation) {
        // 已確認且日期已過的預約可以標記為完成
        LocalDate today = LocalDate.now();
        LocalDate reservedDate = reservation.getReservedDate();

        return "CONFIRMED".equals(reservation.getStatus()) &&
                (reservedDate.isBefore(today) || reservedDate.isEqual(today));
    }

    /**
     * 預約更新請求 DTO
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
