package tw.com.ispan.eeit.repository.reservation;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import tw.com.ispan.eeit.model.entity.reservation.TableBean;
import tw.com.ispan.eeit.model.entity.store.StoreBean;

public interface TableRepository extends JpaRepository<TableBean, Integer> {

    // 根據餐廳查詢桌位
    List<TableBean> findByStore(StoreBean store);

    // 根據餐廳和狀態查詢桌位
    List<TableBean> findByStoreAndStatus(StoreBean store, Boolean status);

    // 根據餐廳和座位數查詢桌位
    List<TableBean> findByStoreAndSeats(StoreBean store, Integer seats);

    // 根據餐廳查詢可用的桌位
    @Query("SELECT t FROM TableBean t WHERE t.store = :store AND t.status = true")
    List<TableBean> findAvailableTablesByStore(@Param("store") StoreBean store);

    // 根據餐廳和座位數查詢可用的桌位
    @Query("SELECT t FROM TableBean t WHERE t.store = :store AND t.seats >= :minSeats AND t.status = true")
    List<TableBean> findAvailableTablesByStoreAndMinSeats(
            @Param("store") StoreBean store,
            @Param("minSeats") Integer minSeats);
}
