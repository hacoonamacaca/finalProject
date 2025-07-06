package tw.com.ispan.eeit.model.dto.food;

import lombok.Data;
import java.time.LocalDateTime;

// DTO: Data Transfer Object
// 這個類別專門用來在 Controller 和前端之間傳遞 Food 的簡化資訊

@Data
public class FoodDTO {
    // 我們只挑選前端列表頁面需要的欄位
    private Integer id;
    private String name;
    private Integer price;
    private String description;
    private Float score;
    private Boolean isActive;
    private Integer stock;
    private String imgResource;
    
    // 對於關聯物件，我們通常只回傳它的 ID
    private Integer storeId; 
    private String storeName; // 也可以考慮順便回傳店家名稱，增加方便性
}