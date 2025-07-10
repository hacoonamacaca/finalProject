package tw.com.ispan.eeit.model.dto.order;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import lombok.Data;
import tw.com.ispan.eeit.model.dto.comment.CommentResponseDTO;
import tw.com.ispan.eeit.model.dto.store.StoreDTO;
import tw.com.ispan.eeit.model.entity.order.OrderBean;

@Data
public class OrderDTO {
    private Integer id;
    private Integer promotionId; // 只返回促銷ID，或者可以是一個 PromotionDto
    private Integer total;
    private String status;
    private LocalDateTime createTime;
    private String content;
    private LocalDateTime pickupTime;
    // 外部連結的部分

    private OrderUserDto User; // 只返回用戶ID，或者可以是一個 UserDto
    private CommentResponseDTO comment;
    private List<OrderDetailDTO> orderDetails; // 包含訂單明細列表
    private List<StoreDTO> stores;
    private OrderStoreDto store;

    // 如果需要更詳細的User/Store/Promotion資料，可以嵌套對應的DTO
    @Data
    public static class OrderUserDto {
        private Integer id;
        private String name;
        // ... 其他用戶資訊
    }

    @Data
    public static class OrderStoreDto {
        private Integer id;
        private String name;
        private String photo;
        private Boolean isOpen;
        private Boolean isActive;
        // ... 其他用戶資訊
    }

    public static OrderDTO fromEntity(OrderBean orderBean) {
        // 透過靜態法將Bean匯入到DTO中階產生物件
        OrderDTO orderDto = new OrderDTO();
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
            OrderDTO.OrderUserDto userDto = new OrderDTO.OrderUserDto();
            userDto.setId(orderBean.getUser().getId());
            userDto.setName(orderBean.getUser().getName());
            orderDto.setUser(userDto);
        }

        if (orderBean.getStore() != null &&
                org.hibernate.Hibernate.isInitialized(orderBean.getStore())) {
            // 如果 OrderDto 包含嵌套的 UserDto，可以這樣做：
            OrderDTO.OrderStoreDto storeDto = new OrderDTO.OrderStoreDto();
            storeDto.setId(orderBean.getStore().getId());
            storeDto.setName(orderBean.getStore().getName());
            storeDto.setPhoto(orderBean.getStore().getPhoto());
            storeDto.setIsActive(orderBean.getStore().getIsActive());
            storeDto.setIsOpen(orderBean.getStore().getIsOpen());
            orderDto.setStore(storeDto);
        }

        // 複製 Comment 資訊
        // 檢查關聯是否已初始化且不為 null
        if (orderBean.getComment() != null &&
                org.hibernate.Hibernate.isInitialized(orderBean.getComment())) {
            orderDto.setComment(CommentResponseDTO
                    .fromCommentBean(orderBean.getComment()));
        }

        // 複製 OrderDetails 及其子關聯 (Food, LikedFoods)
        // 檢查集合是否已初始化且不為 null
        // isInitialized在觸發初始化之前，安全地檢查一個物件或集合是否已經初始化，從而避免 LazyInitializationException
        if (orderBean.getOrderDetails() != null && org.hibernate.Hibernate.isInitialized(orderBean.getOrderDetails())) {
            List<OrderDetailDTO> orderDetailDtos = orderBean.getOrderDetails().stream()
                    .map(OrderDetailDTO::fromEntity)
                    // .map(orderDetail -> OrderDetailDTO.fromEntity(orderDetail))
                    .collect(Collectors.toList());
            orderDto.setOrderDetails(orderDetailDtos);
        }

        return orderDto;
    }

}
