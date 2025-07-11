package tw.com.ispan.eeit.service.promotion;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tw.com.ispan.eeit.model.dto.plan.PlanDTO;
import tw.com.ispan.eeit.model.dto.promotion.PromotionDTO;
import tw.com.ispan.eeit.model.entity.promotion.PromotionBean;
import tw.com.ispan.eeit.repository.promotion.PromotionRepository;

@Service
public class PromotionService {

    @Autowired
    private PromotionRepository promotionRepository;

    // 查詢全部優惠券（後台管理）
    public List<PromotionBean> findAll() {
        return promotionRepository.findAll();
    }

    // 根據方案 ID 查優惠券
    public List<PromotionBean> findByPlanId(Integer planId) {
        return promotionRepository.findByPlanId(planId);
    }

    // 根據商店 ID 查優惠券
    public List<PromotionBean> findByStoreId(Integer storeId) {
        return promotionRepository.findByStoreId(storeId);
    }

    // 查詢單筆優惠券（轉 DTO）
    public PromotionDTO findById(Integer id) {
        Optional<PromotionBean> optional = promotionRepository.findById(id);
//        return optional.orElse(null);
        return  toDTO(optional.get());
    }

    // 新增優惠券
    public PromotionBean create(PromotionBean promotion) {
        promotion.setCreatedTime(LocalDateTime.now());
        promotion.setUpdatedTime(LocalDateTime.now());
        return promotionRepository.save(promotion);
    }

    // 修改優惠券
    public PromotionBean update(Integer id, PromotionBean newData) {
        Optional<PromotionBean> optional = promotionRepository.findById(id);

        if (optional.isPresent()) {
            PromotionBean p = optional.get();
            p.setTitle(newData.getTitle());
            p.setDescription(newData.getDescription());
            p.setDiscountType(newData.getDiscountType());
            p.setDiscountValue(newData.getDiscountValue());
            p.setMinSpend(newData.getMinSpend());
            p.setStartTime(newData.getStartTime());
            p.setEndTime(newData.getEndTime());
            p.setPlan(newData.getPlan());
            p.setStore(newData.getStore());
            p.setTag(newData.getTag());
            p.setCode(newData.getCode());
            p.setMaxUsage(newData.getMaxUsage());
            p.setUserUsageLimit(newData.getUserUsageLimit());
            p.setStatus(newData.getStatus());
            p.setUpdatedTime(LocalDateTime.now());
            p.setCreatedTime(null); // 防止意外改到原本建立時間

            return promotionRepository.save(p);
        }

        return null;
    }

    // 刪除優惠券
    public void deleteById(Integer id) {
        promotionRepository.deleteById(id);
    }

//    // 新增：查詢可用優惠券 + 回傳 DTO（分類含 type）
//    public List<PromotionBean> getAvailablePromotions(Integer userId, Integer storeId, Integer amount) {
//        List<PromotionBean> result = promotionRepository.findAvailablePromotions(userId, storeId, amount);
//        return result;
//        
//        //        return result.stream().map(this::toDTO).toList();
//    }
    
    // ✅ 優惠券清單：查詢目前有效的優惠券
    public List<PromotionDTO> findAllAvailable() {
        List<PromotionBean> all = promotionRepository.findAll();
        LocalDateTime now = LocalDateTime.now();

        System.out.println("🔍 現在時間：" + now);
        System.out.println("📦 資料庫優惠券筆數：" + all.size());

        List<PromotionDTO> result = all.stream()
            .peek(p -> {
                System.out.println("➡️ 優惠券：" + p.getTitle());
                System.out.println("   狀態：" + p.getStatus());
                System.out.println("   時間區間：" + p.getStartTime() + " ~ " + p.getEndTime());
            })
            .filter(p -> "ACTIVE".equalsIgnoreCase(p.getStatus()))
            .filter(p -> 
                (p.getStartTime() == null || !now.isBefore(p.getStartTime())) &&
                (p.getEndTime() == null || !now.isAfter(p.getEndTime()))
            )
            .map(this::toDTO)
            .toList();

        System.out.println("✅ 篩選後筆數：" + result.size());

        return result;
    }

    
    // ✅ 結帳時使用：查詢符合條件的優惠券（userId、storeId、金額）
    public List<PromotionDTO> getAvailablePromotions(Integer userId, Integer storeId, Integer amount) {
        List<PromotionBean> result = promotionRepository.findAvailablePromotions(userId, storeId, amount);
        return result.stream().map(this::toDTO).toList(); // ✅ 把每筆轉成 DTO
    }

    
    // ✅✨ 新增：分類用（由後端過濾分類 type）
    public List<PromotionDTO> findByType(String type) {
        List<PromotionBean> all = promotionRepository.findAll();
        return all.stream()
            .filter(p -> getType(p).equals(type)) // 後端分類條件
            .map(this::toDTO)
            .toList();
    }

    // ✅✨ 新增：取得分類（配合上方 findByType 使用）
    private String getType(PromotionBean p) {
        if (p.getStore() != null) return "restaurant";
        if (p.getTag() != null) return "food";
        if (p.getPlan() != null) return "member";
        return "global";
    }

    

    // Entity 轉 DTO（包含分類 type 判斷）
    public PromotionDTO toDTO(PromotionBean p) {
        String type = getType(p);

        return new PromotionDTO(
            p.getId(),
            p.getTitle(),
            p.getDescription(),
            p.getDiscountType(),
            p.getDiscountValue(),
            p.getMinSpend(),
            p.getStartTime(),
            p.getEndTime(),
            p.getStatus(),
            type,
            p.getTag() != null ? p.getTag().getName() : null,
            p.getStore() != null ? p.getStore().getId() : null,
            p.getStore() != null ? p.getStore().getName() : null,
            p.getPlan() != null ? p.getPlan().getId() : null,
            p.getPlan() != null ? p.getPlan().getName() : null,
            p.getPlan() != null ? p.getPlan().getPrice() : null,
            p.getPlan() != null ? p.getPlan().getValidMonths() : null
        );
    }
}
