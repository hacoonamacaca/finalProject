package tw.com.ispan.eeit.repository.food;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import tw.com.ispan.eeit.model.entity.food.FoodBean;

@Repository
public interface FoodRepository extends JpaRepository<FoodBean, Integer> {
   
} 