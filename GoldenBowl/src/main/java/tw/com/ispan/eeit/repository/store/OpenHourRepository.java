package tw.com.ispan.eeit.repository.store;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import tw.com.ispan.eeit.model.entity.store.OpenHourBean;
import tw.com.ispan.eeit.model.entity.store.StoreBean;

public interface OpenHourRepository extends JpaRepository<OpenHourBean, Integer> {

    // 根據餐廳查詢營業時間
    List<OpenHourBean> findByStore(StoreBean store);

    // 根據餐廳和星期查詢營業時間
    Optional<OpenHourBean> findByStoreAndDay(StoreBean store, DayOfWeek day);

    // 根據餐廳查詢營業中的時段
    @Query("SELECT oh FROM OpenHourBean oh WHERE oh.store = :store AND oh.isOpen = true ORDER BY oh.day")
    List<OpenHourBean> findOpenHoursByStore(@Param("store") StoreBean store);

    // 根據餐廳ID查詢營業時間
    List<OpenHourBean> findByStoreId(Integer storeId);

    // 根據餐廳ID和星期查詢營業時間
    Optional<OpenHourBean> findByStoreIdAndDay(Integer storeId, DayOfWeek day);
}
