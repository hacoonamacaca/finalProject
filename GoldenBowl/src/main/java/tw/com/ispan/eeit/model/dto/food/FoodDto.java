package tw.com.ispan.eeit.model.dto.food;

//3. FoodDto.java (可以定義為 OrderDetailDto 的靜態內部類，或獨立類)


import lombok.Data;
import tw.com.ispan.eeit.model.entity.food.FoodBean;

@Data
public class FoodDto {
 private Integer id;
 private String name;
 private Integer price;
 private Float score;
 private String imgResource;
 private String description;
 // ... 其他食物屬性
 	public static FoodDto fromEntity(FoodBean foodBean) {
 		FoodDto foodDto = new FoodDto();
 		foodDto.setId(foodBean.getId());
 		foodDto.setName(foodBean.getName());
 		foodDto.setPrice(foodBean.getPrice());
 		foodDto.setScore(foodBean.getScore());
 		foodDto.setImgResource(foodBean.getImgResource());
 		foodDto.setDescription(foodBean.getDescription());
 		return foodDto;
 	}
 
 
 
}