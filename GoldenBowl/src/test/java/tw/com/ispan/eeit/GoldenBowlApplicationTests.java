package tw.com.ispan.eeit;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import tw.com.ispan.eeit.model.entity.UserBean;
import tw.com.ispan.eeit.model.entity.order.OrderBean;
import tw.com.ispan.eeit.repository.order.OrderRepository;
import tw.com.ispan.eeit.service.order.OrderService;

@SpringBootTest
class GoldenBowlApplicationTests {
	@Autowired
	OrderService orderService;
	@Autowired
	OrderRepository orderRepository;

	@Test
	void contextLoads() {
		// 创建一个OrderBean对象
		OrderBean order = new OrderBean();
		// 创建一个UserBean对象
		UserBean user = new UserBean();
		// 设置UserBean对象的id为1
		user.setId(1);
		System.out.println(user.toString());
		// 设置OrderBean对象的content为"test3"
//		order.setContent("test3");
//
//		// 设置OrderBean对象的total为95
//		order.setTotal(95);
//		// 设置OrderBean对象的user为user
//		order.setUser(user);

		
//		// 调用orderService的create方法，创建order对象
//		orderService.create(order);
		// 调用orderService的findAllByUserId方法，获取用户id为1的所有订单
//		List<OrderBean> orderList = orderRepository.findByUser_Id(1);
		List<OrderBean> orderList = orderRepository.findByUser_IdAndStatus(1,"PENDING");
////		// 遍历orderList，打印每个订单的content
		for (OrderBean orderBean : orderList) {
			System.out.println(orderBean.toString());
		} 
		System.out.println("----------------------------------------");
		orderList = orderRepository.findByUser_IdAndStatusNot(1,null);
////	// 遍历orderList，打印每个订单的content
		for (OrderBean orderBean : orderList) {
			System.out.println(orderBean.toString());
		}
		
		// 打印"success"
		System.out.println("success");
	}

}
