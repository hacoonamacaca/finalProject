package tw.com.ispan.eeit.repository.Coupon;

import javax.management.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface NotificationRepository extends JpaRepository<Notification, Integer>{

}
