package tw.com.ispan.eeit.repository.store;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import tw.com.ispan.eeit.model.entity.reservation.TableBean;
import tw.com.ispan.eeit.model.entity.store.StoreBean;

// By 呂冠驄 因為我Reservation 需要用到StoreRepository 所以我先創了一個
public interface StoreRepository extends JpaRepository<StoreBean, Integer> {

    Optional<StoreBean> findById(Integer storeId);

    // 根據餐廳查詢桌位
    List<TableBean> findByStore(StoreBean store);

    // 根據餐廳和狀態查詢桌位
    List<TableBean> findByStoreAndStatus(StoreBean store, Boolean status);

    // 根據餐廳和座位數查詢桌位
    List<TableBean> findByStoreAndSeats(StoreBean store, Integer seats);
}
