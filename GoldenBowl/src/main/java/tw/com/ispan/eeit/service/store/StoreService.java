package tw.com.ispan.eeit.service.store;

import java.time.LocalDateTime;
import java.util.List;

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

        return storeRepository.save(store);
    }
}
