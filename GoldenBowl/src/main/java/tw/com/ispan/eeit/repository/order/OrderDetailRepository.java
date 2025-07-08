package tw.com.ispan.eeit.repository.order;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import tw.com.ispan.eeit.model.entity.order.OrderDetailBean;

public interface OrderDetailRepository extends JpaRepository<OrderDetailBean, Integer> {
    // 根據訂單ID查找所有訂單明細
    List<OrderDetailBean> findByOrder_Id(Integer orderId);

}
