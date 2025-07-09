package tw.com.ispan.eeit.repository.order;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import tw.com.ispan.eeit.model.entity.order.OrderBean;

public interface OrderRepository extends JpaRepository<OrderBean, Integer> {

    // 根據用戶 ID 查找所有訂單
    List<OrderBean> findByUser_Id(Integer userId);
}
