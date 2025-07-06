package tw.com.ispan.eeit;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import tw.com.ispan.eeit.model.entity.UserBean;
import tw.com.ispan.eeit.model.entity.order.OrderBean;
import tw.com.ispan.eeit.service.order.OrderService;

@SpringBootTest
class GoldenBowlApplicationTests {
	@Autowired
	OrderService orderService;

	@Test
	void contextLoads() {
		OrderBean order = new OrderBean();
		UserBean user = new UserBean();
		user.setId(1);
		order.setContent("test3");

		order.setTotal(95);
		order.setUser(user);

		orderService.create(order);
		List<OrderBean> orderList = orderService.findAllByUserId(1);
		for (OrderBean orderBean : orderList) {
			System.out.println(orderBean.toString());
		}

		System.out.println("success");
	}

}
