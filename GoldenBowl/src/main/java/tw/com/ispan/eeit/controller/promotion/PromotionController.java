package tw.com.ispan.eeit.controller.promotion;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import tw.com.ispan.eeit.model.dto.promotion.PromotionDTO;
import tw.com.ispan.eeit.model.entity.promotion.PromotionBean;
import tw.com.ispan.eeit.service.promotion.PromotionService;

@RestController
@RequestMapping("/promotions")
@CrossOrigin
public class PromotionController {

    @Autowired
    private PromotionService promotionService;

    // 查全部
    @GetMapping
    public List<PromotionBean> findAll() {
        return promotionService.findAll();
    }
    
    // 查單筆
    @GetMapping("/{id}")
    public PromotionDTO findById(@PathVariable Integer id) {
        return promotionService.findById(id);
    }

    // 查某方案的優惠券
    @GetMapping("/plan/{planId}")
    public List<PromotionBean> findByPlanId(@PathVariable Integer planId) {
        return promotionService.findByPlanId(planId);
    }

    // 查某商店的優惠券
    @GetMapping("/store/{storeId}")
    public List<PromotionBean> findByStoreId(@PathVariable Integer storeId) {
        return promotionService.findByStoreId(storeId);
    }

    // 新增優惠券
    @PostMapping
    public PromotionBean create(@RequestBody PromotionBean promotion) {
        return promotionService.create(promotion);
    }

    // 修改優惠券
    @PutMapping("/{id}")
    public PromotionBean update(@PathVariable Integer id, @RequestBody PromotionBean newData) {
        return promotionService.update(id, newData);
    }

    // 刪除優惠券
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        promotionService.deleteById(id);
    }
    
 // 查詢符合條件的可用優惠券（回傳 DTO）
    @GetMapping("/available")
    public List<PromotionBean> getAvailablePromotions(
        @RequestParam Integer userId,
        @RequestParam Integer storeId,
        @RequestParam Integer amount
    ) {
        return promotionService.getAvailablePromotions(userId, storeId, amount);
    }
}

