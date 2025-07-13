package tw.com.ispan.eeit.controller.store;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import tw.com.ispan.eeit.model.dto.store.StoreDTO;
import tw.com.ispan.eeit.model.entity.OwnerBean;
import tw.com.ispan.eeit.model.entity.store.StoreBean;
import tw.com.ispan.eeit.service.store.StoreService;

@RestController
@RequestMapping("/api/stores")
public class StoreController {

    @Autowired
    private StoreService storeService;

    @GetMapping
    public ResponseEntity<List<StoreDTO>> getAllStores(@RequestParam(required = false) String search) {
        List<StoreBean> stores;
        if (search != null && !search.trim().isEmpty()) {
            // 如果提供了搜尋參數，則呼叫搜尋方法
            stores = storeService.searchStores(search.trim());
        } else {
            // 否則，獲取所有商店
            stores = storeService.getAllStores();
        }

        List<StoreDTO> storeDTOs = stores.stream()
                .map(StoreDTO::new) // 將 StoreBean 轉換為 StoreDTO
                .collect(Collectors.toList());
        return new ResponseEntity<>(storeDTOs, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StoreDTO> getStoreById(@PathVariable Integer id) {
        Optional<StoreBean> storeOptional = storeService.getStoreById(id);
        return storeOptional.map(storeBean -> {
            StoreDTO storeDTO = new StoreDTO(storeBean); // 將 StoreBean 轉換為 StoreDTO
            return new ResponseEntity<>(storeDTO, HttpStatus.OK);
        }).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<StoreDTO> createStore(@RequestBody StoreDTO storeDto) {
        // 將 StoreDTO 轉換回 StoreBean 以便 service 層處理
        StoreBean storeToCreate = new StoreBean();
        // 使用 BeanUtils.copyProperties 可以複製相同的屬性名稱
        // 但對於複雜的嵌套 DTO 或關聯，你需要手動轉換或使用 MapStruct 等映射工具
        storeToCreate.setName(storeDto.getName());
        storeToCreate.setPhoto(storeDto.getPhoto());
        storeToCreate.setScore(storeDto.getScore());
        storeToCreate.setIsOpen(storeDto.getIsOpen());
        // 對於 categoryNames, comments, foods，如果你在創建時需要設置這些關聯，
        // 你需要根據 DTO 中的資訊從資料庫查詢對應的 Bean，並設置到 storeToCreate 中。
        // 例如：
        // if (storeDto.getCategoryNames() != null &&
        // !storeDto.getCategoryNames().isEmpty()) {
        // List<CategoryBean> categories =
        // categoryRepository.findByNameIn(storeDto.getCategoryNames());
        // storeToCreate.setCategories(categories);
        // }
        // 否則，在創建或更新時，這些集合通常不會直接從 JSON 傳入。
        // 你需要根據業務邏輯決定如何處理這些關聯。
        // 暫時假設這些集合在創建時不會直接傳入，或者由 Service 層負責處理。

        StoreBean createdStoreBean = storeService.createStore(storeToCreate);

        // 將創建後的 StoreBean 轉換回 StoreDTO 返回給客戶端
        StoreDTO createdStoreDTO = new StoreDTO(createdStoreBean);
        return new ResponseEntity<>(createdStoreDTO, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<StoreDTO> updateStore(@PathVariable Integer id, @RequestBody StoreDTO storeDetailsDto) {
        // 將 StoreDTO 轉換回 StoreBean 以便 service 層處理更新
        StoreBean storeToUpdate = new StoreBean();
        storeToUpdate.setName(storeDetailsDto.getName());
        storeToUpdate.setPhoto(storeDetailsDto.getPhoto());
        storeToUpdate.setScore(storeDetailsDto.getScore());
        storeToUpdate.setIsOpen(storeDetailsDto.getIsOpen());
        storeToUpdate.setAddress(storeDetailsDto.getAddress());
        storeToUpdate.setStoreIntro(storeDetailsDto.getStoreIntro());
        
        OwnerBean owner = new OwnerBean();
        owner.setPhone(storeDetailsDto.getPhone());
        owner.setEmail(storeDetailsDto.getEmail());
        storeToUpdate.setOwner(owner);
        // 同樣，對於關聯集合的更新，你需要根據業務邏輯處理
        // 例如，如果前端傳入新的 categoryNames，你可能需要在 service 層更新 StoreBean 的 categories 集合
        // storeDetailsDto.getCategoryNames(); // <-- 這裡會包含前端傳來的類別名稱

        StoreBean updatedStoreBean = storeService.updateStore(id, storeToUpdate);

        if (updatedStoreBean != null) {
            // 將更新後的 StoreBean 轉換回 StoreDTO 返回
            StoreDTO updatedStoreDTO = new StoreDTO(updatedStoreBean);
            return new ResponseEntity<>(updatedStoreDTO, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteStore(@PathVariable Integer id) {
        if (storeService.deleteStore(id)) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    // 支援多張照片的註冊，照片存前端public/photos，SQL只存相對路徑
    @PostMapping(value = "/registerInfo", consumes = "multipart/form-data")
    public Map<String, Object> register(
            @RequestParam("ownerId") Integer ownerId,
            @RequestParam("name") String name,
            @RequestParam("storeCategory") String storeCategory,
            @RequestParam("storeIntro") String intro,
            @RequestPart(value = "photos", required = false) List<MultipartFile> photos) {
        // 驗證必填
        if (ownerId == null) {
            return Map.of("success", false, "message", "ownerId 不可為空");
        }
        if (name == null || name.isBlank()) {
            return Map.of("success", false, "message", "請輸入店名");
        }
        if (storeCategory == null || storeCategory.isBlank()) {
            return Map.of("success", false, "message", "請選擇餐廳類型");
        }

        // 儲存照片（多檔案路徑用 ; 連接）
        StringBuilder photoPaths = new StringBuilder();
        if (photos != null && !photos.isEmpty()) {
            for (MultipartFile photo : photos) {
                String path = savePhoto(photo); // 你可以自訂上傳位置與網址
                if (!path.isBlank()) {
                    photoPaths.append(path).append(";");
                }
            }
        }
        // 去掉最後一個分號
        String photoPathStr = photoPaths.length() > 0 ? photoPaths.substring(0, photoPaths.length() - 1) : "";

        // 呼叫 Service
        StoreBean store = storeService.registerStore(ownerId, name, storeCategory, intro, photoPathStr);
        if (store == null || store.getId() == null) {
            return Map.of("success", false, "message", "註冊失敗");
        }
        return Map.of("success", true, "storeId", store.getId());
    }
    
 // 照片儲存到前端 public/photos，回傳相對路徑（供 SQL 儲存、前端使用）
    private String savePhoto(MultipartFile file) {
        try {
            // 路徑視你的前端資料夾位置而定
            String folder = "../frontend/public/photos/"; // 調整成你自己的public照片資料夾絕對或相對路徑
            File dir = new File(folder);
            if (!dir.exists()) dir.mkdirs();

            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            File dest = new File(folder + fileName);
            file.transferTo(dest);

            // 存相對路徑即可，前端 <img src="/photos/xxx.jpg" />
            String relativePath = "/photos/" + fileName;
            return relativePath;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    @PostMapping("/updateAddress")
    public Map<String, Object> updateAddress(@RequestBody Map<String, Object> map) {
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

        Double lat = null, lon = null;
        try {
            if (map.get("lat") != null && !((String) map.get("lat")).isBlank())
                lat = Double.parseDouble((String) map.get("lat"));
            if (map.get("lon") != null && !((String) map.get("lon")).isBlank())
                lon = Double.parseDouble((String) map.get("lon"));
        } catch (Exception e) {
            return Map.of("success", false, "message", "經緯度格式錯誤");
        }

        boolean ok = storeService.updateAddress(storeId, address, lat, lon);
        return Map.of("success", ok);
    }
    
    @GetMapping("/profile")
    public ResponseEntity<StoreDTO> getMyStoreProfile(@RequestParam Integer ownerId) {
        // ownerId 可以從 JWT 或 session 取得，不要讓前端直接傳（安全性問題）
        // 這邊為示範，暫時用 query 參數帶
        if (ownerId == null) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        Optional<StoreBean> storeOptional = storeService.getStoreByOwnerId(ownerId);
        if (storeOptional.isEmpty()) return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        StoreDTO storeDTO = new StoreDTO(storeOptional.get());
        return new ResponseEntity<>(storeDTO, HttpStatus.OK);
    }
}