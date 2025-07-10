package tw.com.ispan.eeit.model.dto.order;
//2. OrderDetailDto.java (可以定義為 OrderDto 的靜態內部類，或獨立類)


import java.util.List;
import java.util.stream.Collectors;

import org.hibernate.Hibernate;

import lombok.Data;
import lombok.NoArgsConstructor;
import tw.com.ispan.eeit.model.dto.store.SpecDTO;
import tw.com.ispan.eeit.model.entity.comment.LikedFoodBean;
import tw.com.ispan.eeit.model.entity.food.FoodBean;
import tw.com.ispan.eeit.model.entity.order.OrderDetailBean;


@Data
@NoArgsConstructor
public class OrderDetailDTO {
	 private Integer id;
	 private Integer quantity; //數量
	 private Integer price; //食物單價
	 private Integer subTotal; //spec 的價格
	 private Integer total;//總價
	// 外部資料的Food
	 private OrderDetailFoodDTO food; // OrderDetailDTO內的包含食物資料
	 private Boolean likeFood; //LikeFood表單中的選項

	 private List<SpecDTO> specs;
	 
	 @Data
	 public static class OrderDetailFoodDTO {
	     private Integer id;
	     private String name;
	     
	     
	     public FoodBean toFoodBean() {
	         FoodBean foodBean = new FoodBean();
	         foodBean.setId(this.id);
	         foodBean.setName(this.name);
	         // ... 其他 FoodBean 屬性
	         return foodBean;
	     }
	 }
	 
	 public static OrderDetailDTO fromEntity(OrderDetailBean orderDetailBean) {
		 OrderDetailDTO orderDetail = new OrderDetailDTO();
		 orderDetail.setId(orderDetailBean.getId());
		 orderDetail.setQuantity(orderDetailBean.getQuantity());
		 orderDetail.setPrice(orderDetailBean.getPrice());
		 orderDetail.setSubTotal(orderDetailBean.getSubTotal());
		 orderDetail.setTotal(orderDetailBean.getTotal());
		 
		 //使用內部資料
		 if (orderDetailBean.getFood() != null && 
				 Hibernate.isInitialized(orderDetailBean.getFood())) {
			 OrderDetailDTO.OrderDetailFoodDTO foodDTO = new OrderDetailDTO.OrderDetailFoodDTO();
			 foodDTO.setId(orderDetailBean.getFood().getId());
			 foodDTO.setName(orderDetailBean.getFood().getName());
		 }
		 
		 //
		 if (orderDetailBean.getSpecs() != null && Hibernate.isInitialized(orderDetailBean.getSpecs())) {
	            List<SpecDTO> specDtos = orderDetailBean.getSpecs().stream()
	                .map(SpecDTO::fromSpecBean) // 假设 SpecDTO 的转换方法名为 fromSpecBean
	                .collect(Collectors.toList());
	            orderDetail.setSpecs(specDtos);
	        }
		 if (orderDetailBean.getLikedFood() != null && Hibernate.isInitialized(orderDetailBean.getLikedFood())) {
	            // 假設 LikedFoodDTO 的轉換方法為 fromLikedFoodBean
	            orderDetail.setLikeFood(orderDetailBean.getLikedFood().getIsLiked());
	        }
		 return orderDetail;
	 }
	 
	 
	 /**
	  * 將 OrderDetailDTO 轉換為 OrderDetailBean 的方法
	  * @return 轉換後的 OrderDetailBean 物件
	  */
	 public OrderDetailBean toBean() {
	     OrderDetailBean orderDetailBean = new OrderDetailBean();
	     orderDetailBean.setId(this.id);
	     orderDetailBean.setQuantity(this.quantity);
	     orderDetailBean.setPrice(this.price);
	     orderDetailBean.setSubTotal(this.subTotal);
	     orderDetailBean.setTotal(this.total);

	     // 轉換 Food (如果存在)
	     if (this.food != null) {
	         orderDetailBean.setFood(this.food.toFoodBean());
	     }

	     // 轉換 Specs 列表 (如果存在)
	     if (this.specs != null && !this.specs.isEmpty()) {
	         orderDetailBean.setSpecs(this.specs.stream()
	             .map(SpecDTO::toSpecBean) // 假設 SpecDTO 有一個 toSpecBean() 方法
	             .collect(Collectors.toList()));
	     }

	     // 轉換 LikedFood (如果存在)
	     if (this.likeFood != null) {
	    	 LikedFoodBean likeFoodBean = new LikedFoodBean();
	    	 likeFoodBean.setIsLiked(this.likeFood);
	         orderDetailBean.setLikedFood(likeFoodBean); // 假設 LikedFoodDTO 有一個 toLikedFoodBean() 方法
	     }

	     return orderDetailBean;
	 }
 
 
 
}
