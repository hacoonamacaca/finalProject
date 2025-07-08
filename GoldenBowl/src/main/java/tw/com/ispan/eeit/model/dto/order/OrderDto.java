package tw.com.ispan.eeit.model.dto.order;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import lombok.Data;
import tw.com.ispan.eeit.model.dto.food.FoodDto;
import tw.com.ispan.eeit.model.dto.store.StoreDTO;
import tw.com.ispan.eeit.model.entity.order.OrderBean;

@Data
public class OrderDto {
    private Integer id;
    private Integer promotionId; // 只返回促銷ID，或者可以是一個 PromotionDto
    private Integer total;
    private String status;
    private LocalDateTime createTime;
    private String content;
    private LocalDateTime pickupTime;
//  外部連結的部分
    private OrderCommentDto comment; // 如果需要評論資料
    private OrderUserDto User; // 只返回用戶ID，或者可以是一個 UserDto
    private List<OrderDetailDto> orderDetails; // 包含訂單明細列表
    private List<StoreDTO> stores;

    // 如果需要更詳細的User/Store/Promotion資料，可以嵌套對應的DTO
    @Data
    public static class OrderUserDto {
        private Integer id;
        private String name;
        // ... 其他用戶資訊
    }

    @Data
    public static class OrderCommentDto {
        private Integer id;
        private String content;
        private Integer score;
    }
    
    
    public static OrderDto fromEntity(OrderBean orderBean) {
   	 	//透過靜態法將Bean匯入到DTO中階產生物件
        OrderDto orderDto = new OrderDto();
        orderDto.setId(orderBean.getId());
        orderDto.setTotal(orderBean.getTotal());
        orderDto.setStatus(orderBean.getStatus());
        orderDto.setCreateTime(orderBean.getCreateTime());
        orderDto.setContent(orderBean.getContent());
        orderDto.setPickupTime(orderBean.getPickupTime());

        // 複製 User 資訊
        // 檢查關聯是否已初始化且不為 null，避免 LazyInitializationException
        if (orderBean.getUser() != null && 
        		org.hibernate.Hibernate.isInitialized(orderBean.getUser())) {   
            // 如果 OrderDto 包含嵌套的 UserDto，可以這樣做：
             OrderDto.OrderUserDto userDto = new OrderDto.OrderUserDto();
             userDto.setId(orderBean.getUser().getId());
             userDto.setName(orderBean.getUser().getName());
             orderDto.setUser(userDto);
        }

        // 複製 Comment 資訊
        // 檢查關聯是否已初始化且不為 null
        if (orderBean.getComment() != null && 
        		org.hibernate.Hibernate.isInitialized(orderBean.getComment())) {
            OrderDto.OrderCommentDto commentDto = new OrderDto.OrderCommentDto();
//          建立OrderDto中的靜態類別
            commentDto.setId(orderBean.getComment().getId());
            commentDto.setContent(orderBean.getComment().getContent());
            commentDto.setScore(orderBean.getComment().getScore());
            orderDto.setComment(commentDto);
        }

        // 複製 OrderDetails 及其子關聯 (Food, LikedFoods)
        // 檢查集合是否已初始化且不為 null
//     	isInitialized在觸發初始化之前，安全地檢查一個物件或集合是否已經初始化，從而避免 LazyInitializationException
        if (orderBean.getOrderDetails() != null && org.hibernate.Hibernate.isInitialized(orderBean.getOrderDetails())) {
            List<OrderDetailDto> orderDetailDtos = orderBean.getOrderDetails().stream()
                .map(orderDetail -> {
                    OrderDetailDto odDto = new OrderDetailDto();
                    odDto.setId(orderDetail.getId());
                    odDto.setQuantity(orderDetail.getQuantity());
                    odDto.setPrice(orderDetail.getPrice());
                    odDto.setSubTotal(orderDetail.getSubTotal());

                    // 複製 Food 資訊
                    if (orderDetail.getFood() != null && org.hibernate.Hibernate.isInitialized(orderDetail.getFood())) {
                    	FoodDto foodDto = new FoodDto();
                        foodDto.setId(orderDetail.getFood().getId());
                        foodDto.setName(orderDetail.getFood().getName());
                        foodDto.setPrice(orderDetail.getFood().getPrice());
                        foodDto.setScore(orderDetail.getFood().getScore());
                        // ... 複製其他食物屬性
                        odDto.setFood(foodDto);
                    }
                    return odDto;
                }).collect(Collectors.toList());
            orderDto.setOrderDetails(orderDetailDtos);
        }

        return orderDto;
    }
    
    
    
    
}
