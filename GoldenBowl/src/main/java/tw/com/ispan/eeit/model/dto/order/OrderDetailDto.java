package tw.com.ispan.eeit.model.dto.order;
//2. OrderDetailDto.java (可以定義為 OrderDto 的靜態內部類，或獨立類)


import java.util.List;

import lombok.Data;
import tw.com.ispan.eeit.model.dto.food.FoodDto;
import tw.com.ispan.eeit.model.dto.store.SpecDto;


@Data
public class OrderDetailDto {
 private Integer id;
 private Integer quantity;
 private Integer price;
 private Integer subTotal;
 private Integer total;
 private FoodDto food; // 包含食物資料
 private List<SpecDto> specs;

}
