package tw.com.ispan.eeit.repository.reservation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tw.com.ispan.eeit.model.entity.reservation.TimeSettingBean;

import java.util.Optional;

@Repository
public interface TimeSettingRepository extends JpaRepository<TimeSettingBean, Integer> {

    /**
     * 根據餐廳ID查詢時間設定
     */
    Optional<TimeSettingBean> findByStoreId(Integer storeId);
}