package tw.com.ispan.eeit.service.food;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tw.com.ispan.eeit.model.entity.food.FoodBean;
import tw.com.ispan.eeit.repository.food.FoodRepository;

@Service
public class FoodService {

    @Autowired
    private FoodRepository foodRepository;

    public List<FoodBean> getAllFoods() {
        return foodRepository.findAll();
    }

    public Optional<FoodBean> getFoodById(Integer id) {
        return foodRepository.findById(id);
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
}