package tw.com.ispan.eeit.repository.store;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tw.com.ispan.eeit.model.entity.store.OpenHourBean;
import tw.com.ispan.eeit.model.entity.store.StoreBean;

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
    List<OpenHourBean> findByStoreIdAndDay(Integer storeId, Integer day);

    /**
     * 根據餐廳和星期查詢營業時間
     */
    Optional<OpenHourBean> findByStoreAndDay(StoreBean store, Integer day);

    /**
     * 根據餐廳和星期查詢營業時間（使用 DayOfWeek 枚舉）
     */
    @Query("SELECT oh FROM OpenHourBean oh WHERE oh.store = :store AND oh.day = :day")
    Optional<OpenHourBean> findByStoreAndDay(@Param("store") StoreBean store, @Param("day") DayOfWeek day);

    /**
     * 根據餐廳ID和星期查詢營業時間（使用 DayOfWeek 枚舉）
     */
    @Query("SELECT oh FROM OpenHourBean oh WHERE oh.store.id = :storeId AND oh.day = :day")
    List<OpenHourBean> findByStoreIdAndDay(@Param("storeId") Integer storeId, @Param("day") DayOfWeek day);

    /**
     * 根據餐廳ID和星期查詢營業時間（使用 DayOfWeek 枚舉）- 別名方法
     */
    default List<OpenHourBean> findByStoreIdAndDayOfWeek(Integer storeId, DayOfWeek dayOfWeek) {
        return findByStoreIdAndDay(storeId, dayOfWeek);
    }
}
