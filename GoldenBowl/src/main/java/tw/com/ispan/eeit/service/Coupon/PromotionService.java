package tw.com.ispan.eeit.service.Coupon;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tw.com.ispan.eeit.model.entity.promotion.PromotionBean;
import tw.com.ispan.eeit.repository.Coupon.PromotionRepository;

@Service
public class PromotionService {
	@Autowired
	//注入PromotionRepository，讓Service可以使用
	private PromotionRepository promotionRepository;
	
	//查詢全部優惠券
	public List<PromotionBean> findAll(){
		return promotionRepository.findAll();
	}
	
	//根據方案 ID 查優惠券
	public List<PromotionBean> findByPlanId(Integer planId){
		return promotionRepository.findByPlanId(planId);
	}
	
	//根據商店 ID 查優惠券
	public List<PromotionBean> findByStoreId(Integer storeId){
		return promotionRepository.findByStoreId(storeId);
	}
	
	//查詢單筆
	public PromotionBean findById(Integer id) {
		Optional<PromotionBean> optional = promotionRepository.findById(id);
		return optional.orElse(null);
	}
    //新增優惠券，建立時間和更新時間，用save()存進資料庫
	public PromotionBean create(PromotionBean promotion) {
		promotion.setCreatedTime(LocalDateTime.now());
		promotion.setUpdatedTime(LocalDateTime.now());
		return promotionRepository.save(promotion);
	}
	
	//準備修改一筆優惠券，先查是否有這筆資料存在
    public PromotionBean update(Integer id, PromotionBean newData) {
        Optional<PromotionBean> optional = promotionRepository.findById(id);

	//如果有找到，就把資料取出來準備更新
    if (optional.isPresent()) {
        PromotionBean p = optional.get();

	//將前端送進來的 newData 欄位值一個一個設進原本的物件 p 裡，然後更新時間
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

	//儲存修改後的優惠券（會自動變成 UPDATE 語法）
        return promotionRepository.save(p);

	//找不到要修改的資料，則回傳 null
    }
    return null;
}
    //根據 id 刪除一筆優惠券資料
    public void deleteById(Integer id) {
        promotionRepository.deleteById(id);
    }
}
