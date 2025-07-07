package tw.com.ispan.eeit.service.food;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tw.com.ispan.eeit.model.dto.food.FoodDTO;
import tw.com.ispan.eeit.model.entity.food.FoodBean;
import tw.com.ispan.eeit.repository.food.FoodRepository;

@Service
public class FoodService {

    @Autowired
    private FoodRepository foodRepository;

    // 查詢所有食物，並轉換成 DTO 列表
    public List<FoodDTO> findAllFoods() {
        List<FoodBean> foodBeans = foodRepository.findAll();
        
//    	return foodRepository.findAllAsDTO(); (繞開store回傳錯誤測試用，可刪除)
        
//         使用 Java Stream API 將 List<FoodBean> 轉換為 List<FoodDTO>
        return foodBeans.stream()
                        .map(this::convertToDTO) // 對每個 FoodBean 執行 convertToDTO 方法
                        .collect(Collectors.toList()); // 將結果收集成一個新的 List
    }

//     這是一個輔助方法，負責將單一 FoodBean 轉換成 FoodDTO
    
    private FoodDTO convertToDTO(FoodBean foodBean) {
        FoodDTO dto = new FoodDTO();
        dto.setId(foodBean.getId());
        dto.setName(foodBean.getName());
        dto.setPrice(foodBean.getPrice());
        dto.setDescription(foodBean.getDescription());
        dto.setScore(foodBean.getScore());
        dto.setIsActive(foodBean.getIsActive());
        dto.setStock(foodBean.getStock());
        dto.setImgResource(foodBean.getImgResource());
        
//         安全地取得關聯物件的屬性
        if (foodBean.getStore() != null) {
            dto.setStoreId(foodBean.getStore().getId());
            dto.setStoreName(foodBean.getStore().getName()); // 假設 StoreBean 有 getName() 方法
        }
        
        return dto;
    }
}