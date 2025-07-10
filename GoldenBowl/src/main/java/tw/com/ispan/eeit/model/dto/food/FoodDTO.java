package tw.com.ispan.eeit.model.dto.food;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;
import tw.com.ispan.eeit.model.entity.food.FoodBean;

// DTO: Data Transfer Object
// 這個類別專門用來在 Controller 和前端之間傳遞 Food 的簡化資訊

@Data
@NoArgsConstructor
public class FoodDTO {
	// 我們只挑選前端列表頁面需要的欄位
	private Integer id;
	private String name;
	private Integer price;
	private Float score;
	private String imgResource;
	private String description;
	private String storeName;
	private Integer storeId;

	private List<String> tagNames;

	//新增food資料需要的映射 by kinan
	private Boolean isActive;
    private Integer stock;
	private String categoryName;
	private Integer categoryId;

	// ... 其他食物屬性 (已不需要)
	public static FoodDTO fromEntity(FoodBean foodBean) {
		FoodDTO foodDto = new FoodDTO();
		foodDto.setId(foodBean.getId());
		foodDto.setName(foodBean.getName());
		foodDto.setPrice(foodBean.getPrice());
		foodDto.setScore(foodBean.getScore());
		foodDto.setImgResource(foodBean.getImgResource());
		foodDto.setDescription(foodBean.getDescription());
		return foodDto;
	}

}