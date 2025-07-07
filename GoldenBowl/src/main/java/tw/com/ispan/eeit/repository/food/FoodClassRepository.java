package tw.com.ispan.eeit.repository.food;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import tw.com.ispan.eeit.model.entity.food.FoodClassBean;

@Repository
public interface FoodClassRepository extends JpaRepository<FoodClassBean, Integer>{

}
