package tw.com.ispan.eeit.controller.store;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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

    // 支援多張照片的註冊
    @PostMapping(value = "/registerInfo", consumes = "multipart/form-data")
    public Map<String, Object> register(
        @RequestParam("ownerId") Integer ownerId,
        @RequestParam("name") String name,
        @RequestParam("storeCategory") String storeCategory,
        @RequestParam("storeIntro") String intro,
        @RequestPart(value = "photos", required = false) List<MultipartFile> photos
    ) {
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
        String photoPathStr = photoPaths.length() > 0 ? photoPaths.substring(0, photoPaths.length()-1) : "";

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
            if (!dir.exists()) dir.mkdirs();

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
}