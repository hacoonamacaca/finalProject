package tw.com.ispan.eeit.repository.food;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import tw.com.ispan.eeit.model.entity.food.FoodBean;

//import java.util.List;
//import org.springframework.data.jpa.repository.Query; (測試用可刪)
//import tw.com.ispan.eeit.model.dto.food.FoodDTO; (測試用可刪)

@Repository
public interface FoodRepository extends JpaRepository<FoodBean, Integer> {
  // (繞開store回傳錯誤測試用，可刪除)
//	@Query("SELECT new tw.com.ispan.eeit.model.dto.food.FoodDTO(f.id, f.name, f.price, f.description, f.score, f.isActive, f.stock, f.imgResource, s.id, s.name) FROM FoodBean f JOIN f.store s")
//    List<FoodDTO> findAllAsDTO();
} 