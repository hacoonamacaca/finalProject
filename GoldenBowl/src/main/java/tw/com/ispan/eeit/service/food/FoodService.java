package tw.com.ispan.eeit.service.food;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tw.com.ispan.eeit.model.dto.food.FoodDTO;
import tw.com.ispan.eeit.model.entity.food.FoodBean;
import tw.com.ispan.eeit.model.entity.food.TagBean;
import tw.com.ispan.eeit.repository.food.FoodRepository;

@Service
public class FoodService {

    @Autowired
    private FoodRepository foodRepository;

    // 修改返回類型為 List<FoodDTO>
    public List<FoodDTO> getAllFoodsDTO() {
        List<FoodBean> foods = foodRepository.findAll();
        // 將 FoodBean 列表轉換為 FoodDTO 列表
        return foods.stream()
                .map(this::convertToFoodDTO) // 使用輔助方法進行轉換
                .collect(Collectors.toList());
    }

    // 獲取單個食物的 DTO (可選，如果需要詳情頁)
    public Optional<FoodDTO> getFoodDTOById(Integer id) {
        return foodRepository.findById(id)
                .map(this::convertToFoodDTO);
    }

    public FoodBean createFood(FoodBean food) {
        food.setCreateTime(LocalDateTime.now());
        food.setUpdateTime(LocalDateTime.now());
        if (food.getIsActive() == null) {
            food.setIsActive(true);
        }
        if (food.getStock() == null) {
            food.setStock(0); // 預設庫存為 0
        }
        return foodRepository.save(food);
    }

    public FoodBean updateFood(Integer id, FoodBean foodDetails) {
        Optional<FoodBean> optionalFood = foodRepository.findById(id);
        if (optionalFood.isPresent()) {
            FoodBean existingFood = optionalFood.get();
            existingFood.setName(foodDetails.getName());
            existingFood.setPrice(foodDetails.getPrice());
            existingFood.setDescription(foodDetails.getDescription());
            existingFood.setUpdateTime(LocalDateTime.now());
            existingFood.setScore(foodDetails.getScore());
            existingFood.setIsActive(foodDetails.getIsActive());
            existingFood.setStock(foodDetails.getStock());
            existingFood.setImgResource(foodDetails.getImgResource());
            // 處理關聯實體 (store, tags, specGroups, foodClasses, favoritedByUsers) 需要額外的邏輯
            return foodRepository.save(existingFood);
        }
        return null;
    }

    public boolean deleteFood(Integer id) {
        if (foodRepository.existsById(id)) {
            foodRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // 輔助方法：將 FoodBean 轉換為 FoodDTO
    private FoodDTO convertToFoodDTO(FoodBean foodBean) {
        FoodDTO dto = new FoodDTO();
        dto.setId(foodBean.getId());
        dto.setName(foodBean.getName());
        dto.setPrice(foodBean.getPrice());
        dto.setDescription(foodBean.getDescription());
        dto.setImgResource(foodBean.getImgResource());
        dto.setScore(foodBean.getScore());

        // 處理 tags：將 TagBean 列表轉換為 Tag 名稱列表
        if (foodBean.getTags() != null) {
            dto.setTagNames(foodBean.getTags().stream()
                    .map(TagBean::getName) // 假設 TagBean 有 getName() 方法
                    .collect(Collectors.toList()));
        }

        // 如果需要 Store 名稱
        if (foodBean.getStore() != null) {
            dto.setStoreId(foodBean.getStore().getId());
            dto.setStoreName(foodBean.getStore().getName());
        }
        return dto;
    }
}