//package tw.com.ispan.eeit.service.order;
//
//import java.util.List;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import tw.com.ispan.eeit.model.entity.order.OrderDetailBean;
//import tw.com.ispan.eeit.repository.order.OrderDetailRepository;
//
//@Service
//@Transactional
//public class OrderDetailService {
//	@Autowired
//	OrderDetailRepository orderDetailRepository;
//
//	public OrderDetailBean create(OrderDetailBean orderDetail) {
//		orderDetailRepository.save(orderDetail);
//		return null;
//	}
//
//	public void update(OrderDetailBean orderDetail) {
//		orderDetailRepository.save(orderDetail);
//	}
//
//	public void delete(OrderDetailBean orderDetail) {
//		orderDetailRepository.delete(orderDetail);
//	}
//
//	public List<OrderDetailBean> findByOrderId(Integer orderId) {
//		return orderDetailRepository.findByOrderId(orderId);
//		// 根據訂單編號查詢訂單明細
//	}
//
//}
