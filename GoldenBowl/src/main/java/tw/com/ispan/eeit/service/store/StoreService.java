package tw.com.ispan.eeit.service.store;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import tw.com.ispan.eeit.model.dto.store.StoreDTO;
import tw.com.ispan.eeit.model.entity.OwnerBean;
import tw.com.ispan.eeit.model.entity.UserBean;
import tw.com.ispan.eeit.model.entity.store.CategoryBean;
import tw.com.ispan.eeit.model.entity.store.StoreBean;
import tw.com.ispan.eeit.repository.OwnerRepository;
import tw.com.ispan.eeit.repository.UserRepository;
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

    @Autowired
    private UserRepository userRepository;

    private GeometryFactory geometryFactory = new GeometryFactory();

    public List<StoreDTO> searchStores(Integer userId, String searchTerm) {
        List<StoreBean> stores;
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            stores = storeRepository.findStoresBySearchTerm(searchTerm.trim());
        } else {
            stores = storeRepository.findAll();
        }
        return convertToStoreDTOs(stores, userId);
    }

    public List<StoreDTO> getAllStores(Integer userId) {
        // 從資料庫獲取所有 StoreBean
        List<StoreBean> stores = storeRepository.findAll();
        return convertToStoreDTOs(stores, userId);
    }

    public StoreDTO getStoreById(Integer id, Integer userId) {
        Optional<StoreBean> storeOptional = storeRepository.findByIdWithComments(id); // 使用您的 findByIdWithComments 方法
        if (storeOptional.isEmpty()) {
            return null; // 或者拋出自定義異常
        }
        StoreBean store = storeOptional.get();

        boolean isFavorited = false;
        if (userId != null) {
            // 這裡可以複用 isStoreFavoritedByUser 方法，但要確保 UserBean 也是在同一個事務中載入的
            // 或者直接用更優化的 Repository 查詢
            Optional<UserBean> userOpt = userRepository.findById(userId);
            if (userOpt.isPresent()) {
                isFavorited = userOpt.get().getFavoriteStores().contains(store); // 再次觸發懶載入
            }
            // 更優化的方式是：在 UserRepository 中添加一個方法來判斷特定用戶是否收藏了特定商店ID
            // 例如：boolean existsFavoriteByUserIdAndStoreId(Integer userId, Integer storeId);
            // isFavorited = userRepository.existsFavoriteByUserIdAndStoreId(userId, id);
        }
        return new StoreDTO(store, isFavorited);
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

    // --- 以下是針對收藏功能的新增方法 ---

    // 檢查用戶是否收藏了某餐廳
    public boolean isStoreFavoritedByUser(Integer userId, Integer storeId) {
        Optional<UserBean> userOpt = userRepository.findById(userId);
        Optional<StoreBean> storeOpt = storeRepository.findById(storeId);

        if (userOpt.isPresent() && storeOpt.isPresent()) {
            UserBean user = userOpt.get();
            StoreBean store = storeOpt.get();
            // 由於 favoriteStores 是 @ManyToMany，Spring Data JPA 會自動處理關聯
            return user.getFavoriteStores().contains(store);
        }
        return false;
    }

    // 添加餐廳到用戶收藏
    @Transactional // 事務管理，確保操作的原子性
    public boolean addStoreToFavorites(Integer userId, Integer storeId) {
        Optional<UserBean> userOpt = userRepository.findById(userId);
        Optional<StoreBean> storeOpt = storeRepository.findById(storeId);

        if (userOpt.isPresent() && storeOpt.isPresent()) {
            UserBean user = userOpt.get();
            StoreBean store = storeOpt.get();
            if (!user.getFavoriteStores().contains(store)) {
                user.getFavoriteStores().add(store);
                userRepository.save(user); // 保存用戶以更新關聯
                return true;
            }
        }
        return false; // 收藏失敗或已收藏
    }

    // 從用戶收藏中移除餐廳
    @Transactional // 事務管理
    public boolean removeStoreFromFavorites(Integer userId, Integer storeId) {
        Optional<UserBean> userOpt = userRepository.findById(userId);
        Optional<StoreBean> storeOpt = storeRepository.findById(storeId);

        if (userOpt.isPresent() && storeOpt.isPresent()) {
            UserBean user = userOpt.get();
            StoreBean store = storeOpt.get();
            if (user.getFavoriteStores().contains(store)) {
                user.getFavoriteStores().remove(store);
                userRepository.save(user); // 保存用戶以更新關聯
                return true;
            }
        }
        return false; // 取消收藏失敗或未收藏
    }

    private List<StoreDTO> convertToStoreDTOs(List<StoreBean> stores, Integer userId) {
        // 如果沒有提供 userId，則所有 isFavorited 都是 false
        if (userId == null) {
            return stores.stream()
                    .map(store -> new StoreDTO(store, false))
                    .collect(Collectors.toList());
        }

        // 如果提供了 userId，則需要查詢該用戶的所有收藏
        Optional<UserBean> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            // 如果用戶不存在，則所有 isFavorited 都是 false
            return stores.stream()
                    .map(store -> new StoreDTO(store, false))
                    .collect(Collectors.toList());
        }

        UserBean user = userOpt.get();
        // 由於 favoriteStores 是 LAZY，這裡需要在同一個事務中觸發其載入
        // 確保 user.getFavoriteStores() 被初始化。
        // 或者，更好的做法是使用 JPQL/HQL 的 JOIN FETCH 在查詢 user 時就載入 favoriteStores
        // 但如果 userRepository.findById(userId) 本身就在事務中，直接存取通常可以觸發懶載入。
        Set<StoreBean> userFavoriteStores = user.getFavoriteStores(); // 觸發懶載入

        return stores.stream().map(store -> {
            boolean isFavorited = userFavoriteStores.contains(store);
            return new StoreDTO(store, isFavorited);
        }).collect(Collectors.toList());
    }
}