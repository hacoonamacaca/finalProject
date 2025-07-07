package tw.com.ispan.eeit.model.dto.order;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderDto {
    private Integer id;
    private Integer userId; // 只返回用戶ID，或者可以是一個 UserDto
    private Integer storeId; // 只返回商店ID，或者可以是一個 StoreDto
    private Integer promotionId; // 只返回促銷ID，或者可以是一個 PromotionDto
    private Integer total;
    private String status;
    private LocalDateTime createTime;
    private String content;
    private LocalDateTime pickupTime;
    private OrderCommentDto comment; // 如果需要評論資料
    private List<OrderDetailDto> orderDetails; // 包含訂單明細列表

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
        // ... 其他評論資訊
    }
}
