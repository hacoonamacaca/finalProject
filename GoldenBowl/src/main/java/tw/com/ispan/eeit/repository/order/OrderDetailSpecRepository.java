package tw.com.ispan.eeit.repository.order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import tw.com.ispan.eeit.model.entity.order.OrderDetailSpecBean;

@Repository
public interface OrderDetailSpecRepository extends JpaRepository<OrderDetailSpecBean, Integer> {

}
