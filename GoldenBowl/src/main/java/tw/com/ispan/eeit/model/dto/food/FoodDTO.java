package tw.com.ispan.eeit.model.dto.food;

import lombok.Data;
import lombok.NoArgsConstructor;

// DTO: Data Transfer Object
// 這個類別專門用來在 Controller 和前端之間傳遞 Food 的簡化資訊

@Data
@NoArgsConstructor 
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
    
 // 全參數建構子，給 JPQL 使用 (繞開store回傳錯誤測試用，可刪除)
//    public FoodDTO(Integer id, String name, Integer price, String description, Float score, Boolean isActive, Integer stock, String imgResource, Integer storeId, String storeName) {
//        this.id = id;
//        this.name = name;
//        this.price = price;
//        this.description = description;
//        this.score = score;
//        this.isActive = isActive;
//        this.stock = stock;
//        this.imgResource = imgResource;
//        this.storeId = storeId;
//        this.storeName = storeName;
//    }
}