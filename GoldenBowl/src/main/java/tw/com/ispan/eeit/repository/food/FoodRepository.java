package tw.com.ispan.eeit.repository.food;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import tw.com.ispan.eeit.model.entity.food.FoodBean;

@Repository
public interface FoodRepository extends JpaRepository<FoodBean, Integer> {
    // 根據店家 ID 查詢所有食物
     List<FoodBean> findByStoreId(Integer storeId);
}
