package tw.com.ispan.eeit.repository.order;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import tw.com.ispan.eeit.model.entity.order.OrderBean;

public interface OrderRepository extends JpaRepository<OrderBean, Integer> {

//    // 根據用戶 ID 查找所有訂單
//	@Query("SELECT o FROM OrderBean o " +
//	           "LEFT JOIN FETCH o.user u " +          // 載入 UserBean
//	           "LEFT JOIN FETCH o.store s " +         // 載入 StoreBean
//	           "LEFT JOIN FETCH o.promotion p " +     // 載入 PromotionBean
//	           "LEFT JOIN FETCH o.orderDetails od " + // 載入 OrderDetailBean 集合
//	           "LEFT JOIN FETCH od.food f " +         // 載入 OrderDetailBean 中的 FoodBean
//	           "LEFT JOIN FETCH o.comments c " +      // 載入 CommentBean 集合
//	           "WHERE o.user.id = :userId")
//    List<OrderBean> findByUser_Id(@Param("userId") Integer userId);
////    都是java類別名稱
	@Query("SELECT o FROM OrderBean o " +
		       "LEFT JOIN FETCH o.user u " +
		       "LEFT JOIN FETCH o.store s " +
		       "LEFT JOIN FETCH o.promotion p " +
		       "LEFT JOIN FETCH p.plan pp " +
		       "LEFT JOIN FETCH p.tag pt " +
		       "LEFT JOIN FETCH o.orderDetails od " +
		       "LEFT JOIN FETCH od.food f " +
		       "LEFT JOIN FETCH f.store fs " +
		       "LEFT JOIN FETCH od.likedFoods lf " + // 現在可以載入 likedFoods 了 (因為它是 Set)
		       "LEFT JOIN FETCH o.comments c " +
		       "WHERE u.id = :userId")
     List<OrderBean> findByUser_Id(@Param("userId") Integer userId);
	
	
    
    List<OrderBean> findByUser_IdAndStatus(Integer userId,String status);
    List<OrderBean> findByUser_IdAndStatusNot(Integer userId,String status);
    
}