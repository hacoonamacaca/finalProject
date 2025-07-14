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
import tw.com.ispan.eeit.model.entity.store.StoreBean;
import tw.com.ispan.eeit.service.store.StoreService;

@RestController
@RequestMapping("/api/stores")
public class StoreController {

    @Autowired
    private StoreService storeService;

    @GetMapping
    public ResponseEntity<List<StoreDTO>> getAllStores(
            @RequestParam(value = "userId", required = false) Integer userId, // 新增 userId 參數
            @RequestParam(value = "search", required = false) String search) {
        
        List<StoreDTO> storeDTOs;
        if (search != null && !search.trim().isEmpty()) {
            // 調用帶有 userId 參數的 searchStores 方法
            storeDTOs = storeService.searchStores(userId, search.trim());
        } else {
            // 調用帶有 userId 參數的 getAllStores 方法
            storeDTOs = storeService.getAllStores(userId);
        }
        return new ResponseEntity<>(storeDTOs, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StoreDTO> getStoreById(
            @PathVariable Integer id,
            @RequestParam(value = "userId", required = false) Integer userId) { // 新增 userId 參數
        StoreDTO storeDTO = storeService.getStoreById(id, userId); // 傳遞 userId
        if (storeDTO != null) {
            return new ResponseEntity<>(storeDTO, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
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

    // 支援多張照片的註冊
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

        Double lat = null, lng = null;
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

    // 照片存檔範例
    private String savePhoto(MultipartFile file) {
        try {
            String folder = "uploads/photos";
            File dir = new File(folder);
            if (!dir.exists())
                dir.mkdirs();

            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            File dest = new File(folder + fileName);
            file.transferTo(dest);

            // 回傳完整網址（這裡假設 domain 你自己決定）
            String domain = "https://localhost:8080";
            return domain + "/photos/" + fileName;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    // 新增：收藏餐廳
    @PostMapping("/{storeId}/favorite/{userId}")
    public ResponseEntity<Map<String, Boolean>> addFavoriteStore(
            @PathVariable Integer storeId,
            @PathVariable Integer userId) {
        boolean success = storeService.addStoreToFavorites(userId, storeId);
        return new ResponseEntity<>(Map.of("success", success), success ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    // 新增：取消收藏餐廳
    @DeleteMapping("/{storeId}/favorite/{userId}")
    public ResponseEntity<Map<String, Boolean>> removeFavoriteStore(
            @PathVariable Integer storeId,
            @PathVariable Integer userId) {
        boolean success = storeService.removeStoreFromFavorites(userId, storeId);
        return new ResponseEntity<>(Map.of("success", success), success ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }
}