package tw.com.ispan.eeit.model.dto.food;

//3. FoodDto.java (可以定義為 OrderDetailDto 的靜態內部類，或獨立類)


import lombok.Data;
import java.math.BigDecimal; // 假設 price 是 BigDecimal

import jakarta.persistence.Column;

@Data
public class FoodDto {
 private Integer id;
 private String name;
 private Integer price;
 private Float score;
 private String imgResource;
 private String description;
 // ... 其他食物屬性
}