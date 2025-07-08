package tw.com.ispan.eeit.model.dto.order;
//2. OrderDetailDto.java (可以定義為 OrderDto 的靜態內部類，或獨立類)


import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;
import tw.com.ispan.eeit.model.dto.food.FoodDto;
import tw.com.ispan.eeit.model.dto.store.SpecDto;
import tw.com.ispan.eeit.model.entity.order.OrderDetailBean;


@Data
@NoArgsConstructor
public class OrderDetailDto {
 private Integer id;
 private Integer quantity;
 private Integer price;
 private Integer subTotal;
 private Integer total;
// 外部資料的Food
 private FoodDto food; // 包含食物資料
 private List<SpecDto> specs;
 
 
	 public static OrderDetailDto fromEntity(OrderDetailBean orderDetailBean) {
		 OrderDetailDto orderDetail = new OrderDetailDto();
		 orderDetail.setId(orderDetailBean.getId());
		 orderDetail.setQuantity(orderDetailBean.getQuantity());
		 orderDetail.setPrice(orderDetailBean.getPrice());
		 orderDetail.setSubTotal(orderDetailBean.getSubTotal());
		 orderDetail.setTotal(orderDetailBean.getTotal());
		 
		 
		 
		 
		 
		 
		 return null;
	 }
 
 
 
 
}
