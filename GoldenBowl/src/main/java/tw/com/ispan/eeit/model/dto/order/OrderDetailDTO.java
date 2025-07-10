package tw.com.ispan.eeit.model.dto.order;
//2. OrderDetailDto.java (可以定義為 OrderDto 的靜態內部類，或獨立類)

import java.util.List;
import java.util.stream.Collectors;

import org.hibernate.Hibernate;

import lombok.Data;
import lombok.NoArgsConstructor;
import tw.com.ispan.eeit.model.dto.comment.LikedFoodDTO;
import tw.com.ispan.eeit.model.dto.food.FoodDTO;
import tw.com.ispan.eeit.model.dto.store.SpecDTO;
import tw.com.ispan.eeit.model.entity.order.OrderDetailBean;

@Data
@NoArgsConstructor
public class OrderDetailDTO {
	private Integer id;
	private Integer quantity;
	private Integer price;
	private Integer subTotal;
	private Integer total;
	// 外部資料的Food
	private FoodDTO food; // 包含食物資料
	private List<SpecDTO> specs;
	private LikedFoodDTO likeFood;

	public static OrderDetailDTO fromEntity(OrderDetailBean orderDetailBean) {
		OrderDetailDTO orderDetail = new OrderDetailDTO();
		orderDetail.setId(orderDetailBean.getId());
		orderDetail.setQuantity(orderDetailBean.getQuantity());
		orderDetail.setPrice(orderDetailBean.getPrice());
		orderDetail.setSubTotal(orderDetailBean.getSubTotal());
		orderDetail.setTotal(orderDetailBean.getTotal());

		if (orderDetailBean.getFood() != null &&
				Hibernate.isInitialized(orderDetailBean.getFood())) {
			orderDetail.setFood(FoodDTO.fromEntity(orderDetailBean.getFood()));
		}
		if (orderDetailBean.getSpecs() != null && Hibernate.isInitialized(orderDetailBean.getSpecs())) {
			List<SpecDTO> specDtos = orderDetailBean.getSpecs().stream()
					.map(SpecDTO::fromSpecBean) // 假设 SpecDTO 的转换方法名为 fromSpecBean
					.collect(Collectors.toList());
			orderDetail.setSpecs(specDtos);
		}
		if (orderDetailBean.getLikedFood() != null && Hibernate.isInitialized(orderDetailBean.getLikedFood())) {
			// 假設 LikedFoodDTO 的轉換方法為 fromLikedFoodBean
			orderDetail.setLikeFood(LikedFoodDTO.fromLikedFoodBean(orderDetailBean.getLikedFood()));
		}
		return orderDetail;
	}

}
