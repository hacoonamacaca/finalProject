package tw.com.ispan.eeit.repository.store;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import tw.com.ispan.eeit.model.entity.store.SpecialHoursBean;

@Repository
public interface SpecialHoursRepository extends JpaRepository<SpecialHoursBean, Integer> {

    /**
     * 根據餐廳ID和日期查詢特殊營業時間
     */
    @Query("SELECT sh FROM SpecialHoursBean sh WHERE sh.storeId = :storeId AND sh.date = :date")
    Optional<SpecialHoursBean> findByStoreIdAndDate(@Param("storeId") Integer storeId, @Param("date") LocalDate date);

    /**
     * 根據餐廳ID查詢所有特殊營業時間
     */
    List<SpecialHoursBean> findByStoreId(Integer storeId);

    /**
     * 根據餐廳ID和日期範圍查詢特殊營業時間
     */
    @Query("SELECT sh FROM SpecialHoursBean sh WHERE sh.storeId = :storeId AND sh.date BETWEEN :startDate AND :endDate ORDER BY sh.date")
    List<SpecialHoursBean> findByStoreIdAndDateBetween(
            @Param("storeId") Integer storeId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
}
