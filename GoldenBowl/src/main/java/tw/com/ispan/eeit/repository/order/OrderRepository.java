package tw.com.ispan.eeit.repository.order;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import tw.com.ispan.eeit.model.entity.order.OrderBean;

@Repository
public interface OrderRepository extends JpaRepository<OrderBean, Integer> {
  public List<OrderBean> findAllByUserId(Integer userId);
}
