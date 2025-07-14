package tw.com.ispan.eeit.service.store;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.WKTWriter;
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
        if (store.getScore() != null) {
            store.setScore(roundTo1Decimal(store.getScore()));
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

            // name
            if (storeDetails.getName() != null)
                existingStore.setName(storeDetails.getName());

            // address
            if (storeDetails.getAddress() != null)
                existingStore.setAddress(storeDetails.getAddress());

            // storeIntro
            if (storeDetails.getStoreIntro() != null)
                existingStore.setStoreIntro(storeDetails.getStoreIntro());

            // photo
            if (storeDetails.getPhoto() != null)
                existingStore.setPhoto(storeDetails.getPhoto());

            // isOpen
            if (storeDetails.getIsOpen() != null)
                existingStore.setIsOpen(storeDetails.getIsOpen());

            // score
            if (storeDetails.getScore() != null)
                existingStore.setScore(roundTo1Decimal(storeDetails.getScore()));

            // isActive
            if (storeDetails.getIsActive() != null)
                existingStore.setIsActive(storeDetails.getIsActive());

            // lat/lon（僅在兩個都有值且有效範圍才覆蓋）
            System.out.println("[updateStore] 進入方法，id=" + id);
            System.out.println("[updateStore] storeDetails.getLat() = " + storeDetails.getLat());
            System.out.println("[updateStore] storeDetails.getLon() = " + storeDetails.getLon());
            if (storeDetails.getLat() != null && storeDetails.getLon() != null) {
                double lat = storeDetails.getLat();
                double lon = storeDetails.getLon();
                System.out.println("[updateStore] 收到 lat=" + lat + ", lon=" + lon);
                if(lat >= -90 && lat <= 90 && lon >= -180 && lon <= 180) {
                    existingStore.setLat(lat);
                    existingStore.setLon(lon);
                    Point point = geometryFactory.createPoint(new Coordinate(lon, lat));
                    point.setSRID(4326);
                    System.out.println("[updateStore] 建立 point: " + point);
                    System.out.println("[updateStore] WKT: " + new WKTWriter().write(point));
                    existingStore.setStoreCoords(point);
                } else {
                    System.out.println("[updateStore] 收到非法經緯度，lat/lon 不處理");
                }
            }
            // 🚫 不要讓前端直接改 storeCoords（保護 DB 不會被塞壞掉）

            // owner（如果有送就覆蓋 owner phone/email）
            if (storeDetails.getOwner() != null) {
                OwnerBean owner = existingStore.getOwner();
                if (owner != null) {
                    if (storeDetails.getOwner().getPhone() != null) {
                        owner.setPhone(storeDetails.getOwner().getPhone());
                    }
                    if (storeDetails.getOwner().getEmail() != null) {
                        owner.setEmail(storeDetails.getOwner().getEmail());
                    }
                    ownerRepository.save(owner);
                }
            }
            existingStore.setUpdatedTime(LocalDateTime.now());

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
            Double lon) {
        StoreBean store = storeRepository.findById(storeId)
                .orElse(null);
        if (store == null)
            return false;

        store.setAddress(address);
        store.setLat(lat);
        store.setLon(lon);

        System.out.println("lat=" + lat + ", lon=" + lon);
        try {
            if (lat != null && lon != null) {
                Point point = geometryFactory.createPoint(new Coordinate(lon, lat));
                point.setSRID(4326);
                System.out.println("set storeCoords: " + point.toText() + " SRID=" + point.getSRID());
                store.setStoreCoords(point);
            } else {
                store.setStoreCoords(null);
            }
         // 重要：需要保存變更！
            storeRepository.save(store);
            return true;
            
        } catch (Exception e) {
            System.err.println("錯誤：" + e.getMessage());
            e.printStackTrace();
            // 可以選擇 return false; 讓外面知道失敗
            return false;
        }
    }
    
    /**
     * 獲取Owner的所有Store
     */
    public List<StoreBean> getStoresByOwnerId(Integer ownerId) {
        return storeRepository.findByOwner_Id(ownerId);
    }

    /**
     * 獲取Owner的主要Store（最新建立的）
     */
    public Optional<StoreBean> getMainStoreByOwnerId(Integer ownerId) {
        List<StoreBean> stores = storeRepository.findByOwnerIdOrderByCreatedTimeDesc(ownerId);
        return stores.isEmpty() ? Optional.empty() : Optional.of(stores.get(0));
    }

    /**
     * 獲取Owner的第一個Store（最早建立的）
     */
    public Optional<StoreBean> getFirstStoreByOwnerId(Integer ownerId) {
        List<StoreBean> stores = storeRepository.findByOwnerIdOrderByCreatedTimeAsc(ownerId);
        return stores.isEmpty() ? Optional.empty() : Optional.of(stores.get(0));
    }

    /**
     * 檢查Owner是否擁有指定的Store
     */
    public boolean isStoreOwnedByOwner(Integer storeId, Integer ownerId) {
        return storeRepository.existsByIdAndOwnerId(storeId, ownerId);
    }

    /**
     * 獲取Owner的Store數量，更安全的 getStoreCountByOwnerId 實作
     */
    public long getStoreCountByOwnerId(Integer ownerId) {
    	// 避免載入所有資料再計算數量，改用直接查詢
        return storeRepository.countByOwner_Id(ownerId);
    }
    
    @Deprecated
    public Optional<StoreBean> getStoreByOwnerId(Integer ownerId) {
        return getMainStoreByOwnerId(ownerId); // 注意你的 repository 要有這個方法
    }
    
    private Float roundTo1Decimal(Float value) {
        if (value == null) return null;
        return Math.round(value * 10f) / 10f;
    }
}