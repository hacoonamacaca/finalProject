package tw.com.ispan.eeit.controller.reservation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import tw.com.ispan.eeit.model.dto.reservation.ReservationRequest;
import tw.com.ispan.eeit.model.entity.reservation.ReservationBean;
import tw.com.ispan.eeit.model.enums.ReservationStatus;
import tw.com.ispan.eeit.service.reservation.ReservationService;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    @PostMapping
    public ResponseEntity<ReservationBean> create(@RequestBody ReservationRequest request) {
        ReservationBean reservation = new ReservationBean();
        reservation.setUserId(request.getUserId());
        reservation.setStoreId(request.getStoreId());
        reservation.setReservedDate(request.getReservedDate());
        reservation.setReservedTime(request.getReservedTime());
        reservation.setGuests(request.getGuests());
        reservation.setDuration(request.getDuration());
        reservation.setStatus(ReservationStatus.PENDING);
        return ResponseEntity.ok(reservationService.createReservation(reservation, request.getTableIds()));
    }
}
