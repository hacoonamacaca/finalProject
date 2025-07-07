package tw.com.ispan.eeit.controller.store;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import tw.com.ispan.eeit.model.dto.StoreDto;
import tw.com.ispan.eeit.model.entity.store.StoreBean;
import tw.com.ispan.eeit.model.mapper.StoreMapper;
import tw.com.ispan.eeit.service.store.StoreService;

@RestController
@RequestMapping("/api/store")
public class StoreController {
    @Autowired
    private StoreService storeService;
    
    @GetMapping
    public List<StoreDto> getAllStores() {
        return storeService.getAllStores()
            .stream()
            .map(StoreMapper::toDto)
            .toList();
    }
    
    @GetMapping("/{id}")
    public StoreDto getStoreById(@PathVariable Integer id) {
        return StoreMapper.toDto(storeService.getStoreById(id));
    }

    @PostMapping("/registerInfo")
    public Map<String, Object> register(@RequestBody Map<String, Object> map) {
    	// 1. ownerId 必填
    	Object ownerObj = map.get("ownerId"); // ← 跟前端對好
    	System.out.println("ownerObj = " + ownerObj);
        if (ownerObj == null) {
            return Map.of("success", false, "message", "ownerId 不可為空");
        }
        Integer ownerId = null;
        try {
            ownerId = Integer.parseInt(ownerObj.toString());
        } catch (Exception e) {
            return Map.of("success", false, "message", "ownerId 格式錯誤");
        }
        // 2. 其他欄位必填
        String name = (String) map.get("name");
        String storeCategory = (String) map.get("storeCategory");
        String intro = (String) map.get("storeIntro");
        if (name == null || name.isBlank()) {
            return Map.of("success", false, "message", "請輸入店名");
        }
        if (storeCategory == null || storeCategory.isBlank()) {
            return Map.of("success", false, "message", "請選擇餐廳類型");
        }

        // 3. 呼叫 Service
        StoreBean store = storeService.registerStore(ownerId, name, storeCategory, intro);
        if (store == null || store.getId() == null) {
            return Map.of("success", false, "message", "註冊失敗");
        }
        return Map.of("success", true, "storeId", store.getId());
    }
    
    @PostMapping("/updateAddress")
    public Map<String, Object> updateAddress(@RequestBody Map<String, Object> map) {
        // 必填欄位檢查
        Object storeIdObj = map.get("storeId");
        if (storeIdObj == null) {
            return Map.of("success", false, "message", "storeId 不可為空");
        }
        Integer storeId;
        try {
            storeId = Integer.parseInt(storeIdObj.toString());
        } catch (Exception e) {
            return Map.of("success", false, "message", "storeId 格式錯誤");
        }
        
        String address = (String) map.get("address");
        
        // 轉 double 型態，null 安全
        Double lat = null;
        Double lng = null;
        try {
            if (map.get("lat") != null && !((String) map.get("lat")).isBlank())
                lat = Double.parseDouble((String) map.get("lat"));
            if (map.get("lon") != null && !((String) map.get("lon")).isBlank())
                lng = Double.parseDouble((String) map.get("lon"));
        } catch (Exception e) {
            return Map.of("success", false, "message", "經緯度格式錯誤");
        }

        boolean ok = storeService.updateAddress(storeId, address, lat, lng);
        return Map.of("success", ok);
    }
 }