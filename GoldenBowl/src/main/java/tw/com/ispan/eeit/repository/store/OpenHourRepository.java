package tw.com.ispan.eeit.repository.store;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tw.com.ispan.eeit.model.entity.store.OpenHourBean;
import tw.com.ispan.eeit.model.entity.store.StoreBean;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;

@Repository
public interface OpenHourRepository extends JpaRepository<OpenHourBean, Integer> {

    /**
     * 根據餐廳ID查詢營業時間
     */
    List<OpenHourBean> findByStoreId(Integer storeId);

    /**
     * 根據餐廳查詢營業時間
     */
    List<OpenHourBean> findByStore(StoreBean store);

    /**
     * 根據餐廳ID和星期查詢營業時間
     */
    List<OpenHourBean> findByStoreIdAndDay(Integer storeId, DayOfWeek day);

    /**
     * 根據餐廳和星期查詢營業時間
     */
    Optional<OpenHourBean> findByStoreAndDay(StoreBean store, DayOfWeek day);
}
