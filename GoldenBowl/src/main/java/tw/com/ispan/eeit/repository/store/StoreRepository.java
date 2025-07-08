package tw.com.ispan.eeit.repository.store;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import tw.com.ispan.eeit.model.entity.store.StoreBean;

// By 呂冠驄 因為我Reservation 需要用到StoreRepository 所以我先創了一個
@Repository
public interface StoreRepository extends JpaRepository<StoreBean, Integer> {

    Optional<StoreBean> findById(Integer storeId);

    List<StoreBean> findByCategoriesName(String category);

    // 根據名稱查詢餐廳
    List<StoreBean> findByNameContaining(String name);

    List<StoreBean> findByNameContainingIgnoreCase(String name);

    /**
     * 根據地址模糊查詢餐廳
     */

    // 根據地址查詢餐廳
    List<StoreBean> findByAddressContaining(String address);

    /**
     * 根據地址模糊查詢餐廳
     */
    List<StoreBean> findByAddressContainingIgnoreCase(String address);

    // 根據評分查詢餐廳
    List<StoreBean> findByScoreGreaterThanEqual(Float minScore);

    // 查詢開業中的餐廳
    List<StoreBean> findByIsOpenTrue();

    // 查詢活躍的餐廳
    List<StoreBean> findByIsActiveTrue();

    /**
     * 查詢指定店主的餐廳
     */
    List<StoreBean> findByOwnerId(Integer ownerId);

    // 根據名稱和地址查詢餐廳
    @Query("SELECT s FROM StoreBean s WHERE s.name LIKE %:name% OR s.address LIKE %:address%")
    List<StoreBean> findByNameOrAddressContaining(@Param("name") String name, @Param("address") String address);
}
