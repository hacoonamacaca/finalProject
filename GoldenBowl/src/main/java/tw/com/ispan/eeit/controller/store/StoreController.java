package tw.com.ispan.eeit.controller.store;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import tw.com.ispan.eeit.model.entity.store.StoreBean;
import tw.com.ispan.eeit.repository.store.StoreRepository;
import tw.com.ispan.eeit.service.store.StoreService;

@RestController
@RequestMapping("/api/stores")
public class StoreController {

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private StoreService storeService;

    /**
     * 查詢所有餐廳（簡化版本，避免循環引用）
     */
    @GetMapping
    public ResponseEntity<List<Object>> getAllStores() {
        List<StoreBean> stores = storeRepository.findAll();
        List<Object> simplifiedStores = stores.stream()
                .map(store -> {
                    return new Object() {
                        public final Integer id = store.getId();
                        public final String name = store.getName();
                        public final String address = store.getAddress();
                        public final Boolean isOpen = store.getIsOpen();
                        public final Boolean isActive = store.getIsActive();
                        public final Float score = store.getScore();
                    };
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(simplifiedStores);
    }

    /**
     * 根據 ID 查詢餐廳
     */
    @GetMapping("/{id}")
    public ResponseEntity<StoreBean> getStoreById(@PathVariable Integer id) {
        return storeRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 根據名稱查詢餐廳
     */
    @GetMapping("/search")
    public ResponseEntity<List<StoreBean>> searchStoresByName(@RequestParam String name) {
        List<StoreBean> stores = storeRepository.findByNameContaining(name);
        return ResponseEntity.ok(stores);
    }

    /**
     * 查詢開業中的餐廳
     */
    @GetMapping("/open")
    public ResponseEntity<List<StoreBean>> getOpenStores() {
        List<StoreBean> stores = storeRepository.findByIsOpenTrue();
        return ResponseEntity.ok(stores);
    }

    /**
     * 查詢活躍的餐廳
     */
    @GetMapping("/active")
    public ResponseEntity<List<StoreBean>> getActiveStores() {
        List<StoreBean> stores = storeRepository.findByIsActiveTrue();
        return ResponseEntity.ok(stores);
    }

    /**
     * 根據評分查詢餐廳
     */
    @GetMapping("/rating")
    public ResponseEntity<List<StoreBean>> getStoresByRating(@RequestParam Float minScore) {
        List<StoreBean> stores = storeRepository.findByScoreGreaterThanEqual(minScore);
        return ResponseEntity.ok(stores);
    }

    /**
     * 餐廳註冊（從 ivy 合併）
     */
    @PostMapping("/register")
    public Map<String, Object> registerStore(@RequestBody Map<String, Object> map) {
        // 1. ownerId 必填
        Object ownerObj = map.get("ownerId");
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

    /**
     * 更新餐廳地址（從 ivy 合併）
     */
    @PostMapping("/{storeId}/address")
    public Map<String, Object> updateStoreAddress(
            @PathVariable Integer storeId,
            @RequestBody Map<String, Object> map) {

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