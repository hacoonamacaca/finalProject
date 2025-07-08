package tw.com.ispan.eeit.service.food;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tw.com.ispan.eeit.exception.ResourceNotFoundException;
import tw.com.ispan.eeit.model.dto.food.FoodDTO;
import tw.com.ispan.eeit.model.dto.food.FoodRequest;
import tw.com.ispan.eeit.model.entity.food.FoodBean;
import tw.com.ispan.eeit.model.entity.food.FoodClassBean;
import tw.com.ispan.eeit.model.entity.store.StoreBean;
import tw.com.ispan.eeit.repository.food.FoodClassRepository;
import tw.com.ispan.eeit.repository.food.FoodRepository;
import tw.com.ispan.eeit.repository.store.StoreRepository;

@Service
public class FoodService {

    @Autowired
    private FoodRepository foodRepository;
    @Autowired 
    private StoreRepository storeRepository;
    @Autowired
    private FoodClassRepository foodClassRepository;

    
 // --- Create ---
    @Transactional
    public FoodDTO createFood(FoodRequest request) {
        StoreBean store = storeRepository.findById(request.getStoreId())
            .orElseThrow(() -> new ResourceNotFoundException("找不到店家，ID: " + request.getStoreId()));
        
        List<FoodClassBean> foodClasses = foodClassRepository.findAllById(request.getFoodClassIds());
        if (foodClasses.size() != request.getFoodClassIds().size()) {
            throw new ResourceNotFoundException("部分食物分類不存在，請確認 ID 是否正確。");
        }

        FoodBean newFood = new FoodBean();
        newFood.setStore(store);
        newFood.setName(request.getName());
        newFood.setPrice(request.getPrice());
        newFood.setDescription(request.getDescription());
        newFood.setStock(request.getStock());
        newFood.setImgResource(request.getImgResource());
        newFood.setFoodClasses(foodClasses); // 設定多對多關聯

        newFood.setCreateTime(LocalDateTime.now());
        newFood.setUpdateTime(LocalDateTime.now());
        newFood.setIsActive(true);
        newFood.setScore(0.0f);

        FoodBean savedFood = foodRepository.save(newFood);
        return convertToDTO(savedFood);
    }

    
 // --- Read (Single) ---
    public FoodDTO findFoodById(Integer id) {
        FoodBean foodBean = foodRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("找不到食物，ID: " + id));
        return convertToDTO(foodBean);
    }
 
    
 // --- Read (List by Store) ---
    public List<FoodDTO> findFoodsByStoreId(Integer storeId) {
        // 可以在這裡加一個檢查，確認店家是否存在
        if (!storeRepository.existsById(storeId)) {
            throw new ResourceNotFoundException("找不到店家，ID: " + storeId);
        }
        List<FoodBean> foodBeans = foodRepository.findByStoreId(storeId);
        return foodBeans.stream().map(this::convertToDTO).collect(Collectors.toList());
    }
    
    
 // --- Update ---
    @Transactional
    public FoodDTO updateFood(Integer id, FoodRequest request) {
        FoodBean existingFood = foodRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("找不到要更新的食物，ID: " + id));

        // 驗證店家 ID 是否匹配 (可選，但更安全)
        if (!existingFood.getStore().getId().equals(request.getStoreId())) {
            throw new IllegalArgumentException("無法變更食物所屬的店家。");
        }
        
        List<FoodClassBean> foodClasses = foodClassRepository.findAllById(request.getFoodClassIds());
        if (foodClasses.size() != request.getFoodClassIds().size()) {
            throw new ResourceNotFoundException("部分食物分類不存在，請確認 ID 是否正確。");
        }

        existingFood.setName(request.getName());
        existingFood.setPrice(request.getPrice());
        existingFood.setDescription(request.getDescription());
        existingFood.setStock(request.getStock());
        existingFood.setImgResource(request.getImgResource());
        existingFood.setFoodClasses(foodClasses);
        existingFood.setUpdateTime(LocalDateTime.now());

        FoodBean updatedFood = foodRepository.save(existingFood);
        return convertToDTO(updatedFood);
    }

    // --- Delete ---
    @Transactional
    public void deleteFood(Integer id) {
        if (!foodRepository.existsById(id)) {
            throw new ResourceNotFoundException("找不到要刪除的食物，ID: " + id);
        }
        foodRepository.deleteById(id);
    }
    
    // --- Helper Method ---
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
        
        if (foodBean.getStore() != null) {
            dto.setStoreId(foodBean.getStore().getId());
            dto.setStoreName(foodBean.getStore().getName());
        }
        
        if (foodBean.getFoodClasses() != null && !foodBean.getFoodClasses().isEmpty()) {
            // 為了簡化，DTO 只顯示第一個分類的資訊
            FoodClassBean primaryClass = foodBean.getFoodClasses().get(0);
            dto.setCategoryName(primaryClass.getName());
            dto.setCategoryId(primaryClass.getId());
        }
        
        return dto;
    }
}