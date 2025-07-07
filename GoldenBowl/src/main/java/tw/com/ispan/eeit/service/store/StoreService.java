package tw.com.ispan.eeit.service.store;

import java.time.LocalDateTime;
import java.util.List;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tw.com.ispan.eeit.model.entity.OwnerBean;
import tw.com.ispan.eeit.model.entity.store.CategoryBean;
import tw.com.ispan.eeit.model.entity.store.StoreBean;
import tw.com.ispan.eeit.repository.OwnerRepository;
import tw.com.ispan.eeit.repository.store.CategoryRepository;
import tw.com.ispan.eeit.repository.store.StoreRepository;

@Service
public class StoreService {

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private OwnerRepository ownerRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private GeometryFactory geometryFactory = new GeometryFactory();

    public StoreBean registerStore(
            Integer ownerId,
            String name,
            String storeCategory,
            String storeIntro
    ) {
        StoreBean store = new StoreBean();
        OwnerBean owner = ownerRepository.findById(ownerId)
            .orElseThrow(() -> new RuntimeException("Owner not found: " + ownerId));
        store.setOwner(owner);
        store.setName(name);
        store.setStoreIntro(storeIntro);
        store.setCreatedTime(LocalDateTime.now());

        CategoryBean category = categoryRepository.findByName(storeCategory);
        if (category == null) {
            throw new RuntimeException("Category Not Found: " + storeCategory);
        }
        store.setCategories(List.of(category));

        StoreBean saved = storeRepository.save(store); 
        System.out.println("Store after save: " + saved.getId());
        return saved;
    }

    public boolean updateAddress(
            Integer storeId,
            String address,
            Double lat,
            Double lng
    ) {
        StoreBean store = storeRepository.findById(storeId)
                .orElse(null);
        if (store == null) return false;

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

        store.setUpdatedTime(LocalDateTime.now());
        storeRepository.save(store);

        return true;
    }
    public List<StoreBean> getAllStores() {
        return storeRepository.findAll();
    }
    
    public StoreBean getStoreById(Integer id) {
        return storeRepository.findById(id).orElse(null);
    }
}