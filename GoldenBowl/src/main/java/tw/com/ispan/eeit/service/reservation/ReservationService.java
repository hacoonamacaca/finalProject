package tw.com.ispan.eeit.service.reservation;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import tw.com.ispan.eeit.model.entity.reservation.ReservationBean;
import tw.com.ispan.eeit.model.entity.reservation.TableBean;
import tw.com.ispan.eeit.repository.reservation.ReservationRepository;
import tw.com.ispan.eeit.repository.reservation.TableRepository;

//@Service
public class ReservationService {

//    @Autowired
    private ReservationRepository reservationRepo;

//    @Autowired
    private TableRepository tableRepo;

    public ReservationBean createReservation(ReservationBean reservation, List<Integer> tableIds) {
        List<TableBean> tables = tableRepo.findAllById(tableIds);
        reservation.setTables(new HashSet<>(tables));
        reservation.setCreatedAt(LocalDateTime.now());
        return reservationRepo.save(reservation);
    }

    public List<TableBean> findAvailableTables(LocalDateTime start, int duration, int storeId) {
        // TODO: 查詢該時段未被預約的桌位
        return new ArrayList<>();
    }
}
