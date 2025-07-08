//package tw.com.ispan.eeit.service.order;
//
//import java.util.List;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import tw.com.ispan.eeit.model.entity.order.OrderBean;
//import tw.com.ispan.eeit.repository.order.OrderRepository;
//
//// 定义一个OrderService类，用于处理订单相关的业务逻辑
//@Service
//// 开启事务管理
//@Transactional
//public class OrderService {
//	@Autowired
//	OrderRepository orderRepository;
//
//	public OrderBean create(OrderBean order) {
//		orderRepository.save(order);
//
//		return null;
//	}
//
//	public void update(OrderBean order) {
//		orderRepository.save(order);
//	}
//
//	public void delete(Integer id) {
//		orderRepository.deleteById(id);
//	}
//
//	public OrderBean findById(Integer id) {
//		return orderRepository.findById(id).get();
//		// Option 要使用.get() 取出內容
//	}
//
//	public List<OrderBean> findAllByUserId(int userId) {
//		return orderRepository.findAllByUserId(userId);
//	}
//
//}
