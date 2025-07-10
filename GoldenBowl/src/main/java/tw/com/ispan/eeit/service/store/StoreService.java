package tw.com.ispan.eeit.service.store;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import tw.com.ispan.eeit.model.entity.OwnerBean;
import tw.com.ispan.eeit.model.entity.store.CategoryBean;
import tw.com.ispan.eeit.model.entity.store.StoreBean;
import tw.com.ispan.eeit.repository.OwnerRepository;
import tw.com.ispan.eeit.repository.store.CategoryRepository;
import tw.com.ispan.eeit.repository.store.StoreRepository;

@Service
@Transactional
public class StoreService {

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private OwnerRepository ownerRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private GeometryFactory geometryFactory = new GeometryFactory();

    public List<StoreBean> searchStores(String searchTerm) {
        // 實作模糊搜尋邏輯
        // 這裡需要調用 StoreRepository 中新的查詢方法
        // 這是最關鍵的部分，查詢需要涵蓋 store 的 name, address, categoryName, food 的 name, food 的
        // tagName
        return storeRepository.findStoresBySearchTerm(searchTerm);
    }

    public List<StoreBean> getAllStores() {
        return storeRepository.findAll();
    }

    public Optional<StoreBean> getStoreById(Integer id) {
        return storeRepository.findById(id);
    }

    public StoreBean createStore(StoreBean store) {
        store.setCreatedTime(LocalDateTime.now());
        store.setUpdatedTime(LocalDateTime.now());
        if (store.getIsOpen() == null) {
            store.setIsOpen(false); // 預設為關閉
        }
        if (store.getIsActive() == null) {
            store.setIsActive(true);
        }
        return storeRepository.save(store);
    }

    public StoreBean registerStore(
            Integer ownerId,
            String name,
            String storeCategory,
            String storeIntro,
            String photo) {
        StoreBean store = new StoreBean();
        store.setName(name);
        store.setStoreIntro(storeIntro);
        store.setPhoto(photo);

        // 設定 owner
        OwnerBean owner = ownerRepository.findById(ownerId)
                .orElseThrow(() -> new RuntimeException("Owner not found: " + ownerId));
        store.setOwner(owner);

        // 設定 category
        List<CategoryBean> categories = categoryRepository.findByName(storeCategory);
        if (categories == null || categories.isEmpty()) {
            throw new RuntimeException("Category Not Found: " + storeCategory);
        }
        store.setCategories(Set.of(categories.get(0)));

        // 其他欄位 createStore 會幫你補齊
        return createStore(store);
    }

    public StoreBean updateStore(Integer id, StoreBean storeDetails) {
        Optional<StoreBean> optionalStore = storeRepository.findById(id);
        if (optionalStore.isPresent()) {
            StoreBean existingStore = optionalStore.get();
            existingStore.setName(storeDetails.getName());
            existingStore.setAddress(storeDetails.getAddress());
            existingStore.setStoreCoords(storeDetails.getStoreCoords());
            existingStore.setLng(storeDetails.getLng());
            existingStore.setLat(storeDetails.getLat());
            existingStore.setStoreIntro(storeDetails.getStoreIntro());
            existingStore.setPhoto(storeDetails.getPhoto());
            existingStore.setIsOpen(storeDetails.getIsOpen());
            existingStore.setScore(storeDetails.getScore());
            existingStore.setUpdatedTime(LocalDateTime.now());
            existingStore.setIsActive(storeDetails.getIsActive());
            // 處理關聯實體 (owner, categories, favoritedByUsers) 需要額外的邏輯
            return storeRepository.save(existingStore);
        }
        return null;
    }

    public boolean deleteStore(Integer id) {
        if (storeRepository.existsById(id)) {
            storeRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public boolean updateAddress(
            Integer storeId,
            String address,
            Double lat,
            Double lng) {
        StoreBean store = storeRepository.findById(storeId)
                .orElse(null);
        if (store == null)
            return false;

        store.setAddress(address);
        store.setLat(lat);
        store.setLng(lng);

        System.out.println("lat=" + lat + ", lng=" + lng);
        try {
            if (lat != null && lng != null) {
                Point point = geometryFactory.createPoint(new Coordinate(lng, lat));
                point.setSRID(4326);
                System.out.println("set storeCoords: " + point.toText() + " SRID=" + point.getSRID());
                store.setStoreCoords(point);
            } else {
                store.setStoreCoords(null);
            }
        } catch (Exception e) {
            System.err.println("錯誤：" + e.getMessage());
            e.printStackTrace();
            // 可以選擇 return false; 讓外面知道失敗
            return false;
        }
        return true;
    }
}