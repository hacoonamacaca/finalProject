package tw.com.ispan.eeit.service.order;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import tw.com.ispan.eeit.model.entity.order.OrderBean;
import tw.com.ispan.eeit.model.entity.order.OrderDetailBean;
import tw.com.ispan.eeit.repository.order.OrderDetailRepository;
import tw.com.ispan.eeit.repository.order.OrderRepository;

@Service
@Transactional
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    // 創建訂單 (包含其明細)
    public OrderBean createOrder(OrderBean order) {
        order.setCreateTime(LocalDateTime.now());
        if (order.getStatus() == null || order.getStatus().isEmpty()) {
            order.setStatus("PENDING");
        }

        OrderBean savedOrder = orderRepository.save(order);

        if (order.getOrderDetails() != null && !order.getOrderDetails().isEmpty()) {
            for (OrderDetailBean detail : order.getOrderDetails()) {
                detail.setOrder(savedOrder);
                orderDetailRepository.save(detail);
            }
            // 重新加載訂單以確保 orderDetails 列表被正確填充 (如果需要即時返回)
            // 注意: 這裡的 findByOrder_Id 是對 OrderDetailRepository 的調用，它是正確的
            savedOrder.setOrderDetails(orderDetailRepository.findByOrder_Id(savedOrder.getId()));
        }

        return savedOrder;
    }

    // 根據 ID 查找訂單
    public Optional<OrderBean> findOrderById(Integer id) {
        Optional<OrderBean> orderOptional = orderRepository.findById(id);
        // orderOptional.ifPresent(order -> {
        // order.setOrderDetails(orderDetailRepository.findByOrder_Id(order.getId()));
        // });
        return orderOptional;
    }

    // 查找所有訂單
    public List<OrderBean> findAllOrders() {
        return orderRepository.findAll();
    }

    // 更新訂單
    public Optional<OrderBean> updateOrder(Integer id, OrderBean updatedOrder) {
        return orderRepository.findById(id).map(order -> {
            order.setUser(updatedOrder.getUser());
            order.setStore(updatedOrder.getStore());
            order.setPromotion(updatedOrder.getPromotion());
            order.setTotal(updatedOrder.getTotal());
            order.setStatus(updatedOrder.getStatus());
            order.setContent(updatedOrder.getContent());
            order.setPickupTime(updatedOrder.getPickupTime());
            return orderRepository.save(order);
        });
    }

    // 刪除訂單
    public boolean deleteOrder(Integer id) {
        if (orderRepository.existsById(id)) {
            orderRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // 根據用戶 ID 查找訂單
    // 這個方法現在可以正常工作，因為 OrderRepository 中已添加 findByUser_Id
    public List<OrderBean> findOrdersByUserId(Integer userId) {
    	if(userId!=null) {
    		return orderRepository.findByUser_Id(userId);
    	}
    	return null;
    }
    
    // 根據用戶 ID 查找訂單
    // 這個方法現在可以正常工作，因為 OrderRepository 中已添加 findByUser_Id
    public List<OrderBean> findOrdersUser_IdAndStatus(Integer userId,String status) {
    	if(userId!=null&& !status.isEmpty() && status.length()>0) {

    		return orderRepository.findByUser_IdAndStatus(userId,status);
    	}
    	return null;
    }
    // 根據用戶 ID 查找訂單
    // 這個方法現在可以正常工作，因為 OrderRepository 中已添加 findByUser_Id
    public List<OrderBean> findOrdersByUser_IdAndStatusNot(Integer userId,String status) {
        if(userId!=null&& !status.isEmpty() && status.length()>0) {
        	return orderRepository.findByUser_IdAndStatusNot(userId,status);
        }
        return null;
    }
    
    
    
}