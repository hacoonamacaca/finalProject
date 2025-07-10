package tw.com.ispan.eeit.repository.reservation;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import tw.com.ispan.eeit.model.entity.reservation.ReservationBean;

public interface ReservationRepository extends JpaRepository<ReservationBean, Integer> {
    List<ReservationBean> findByReservedDate(LocalDate date);
}
