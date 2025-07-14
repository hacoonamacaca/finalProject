package tw.com.ispan.eeit.repository.order;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import tw.com.ispan.eeit.model.entity.order.OrderBean;

public interface OrderRepository extends JpaRepository<OrderBean, Integer> {


    
	@Query("SELECT o FROM OrderBean o " +
            "LEFT JOIN FETCH o.user u " +
            "LEFT JOIN FETCH o.orderDetails od " +
            "LEFT JOIN FETCH od.food f " +
            "LEFT JOIN FETCH o.comment c " +
            "LEFT JOIN FETCH f.store fs " +
            "LEFT JOIN FETCH od.likedFood lf " + // 使用 LEFT JOIN FETCH 以防沒有評論
            // 如果需要 likedFoods: "LEFT JOIN FETCH od.likedFoods lf " + // 注意：不能同時 JOIN FETCH 多個 OneToMany 集合
            "WHERE o.id = :orderId ")
	List<OrderBean> findOrderById(@Param("orderId") Integer orderId);
	
	
	
	
	@Query("SELECT o FROM OrderBean o " +
            "LEFT JOIN FETCH o.user u " +
            "LEFT JOIN FETCH o.orderDetails od " +
            "LEFT JOIN FETCH od.food f " +
            "LEFT JOIN FETCH o.comment c " +
            "LEFT JOIN FETCH f.store fs " +
            "LEFT JOIN FETCH od.likedFood lf " + // 使用 LEFT JOIN FETCH 以防沒有評論
            // 如果需要 likedFoods: "LEFT JOIN FETCH od.likedFoods lf " + // 注意：不能同時 JOIN FETCH 多個 OneToMany 集合
            "WHERE u.id = :userId ")
	List<OrderBean> findByUser_Id(@Param("userId") Integer userId);
    
    
    
    
    
    
    @Query("SELECT o FROM OrderBean o " +
            "LEFT JOIN FETCH o.user u " +
            "LEFT JOIN FETCH o.orderDetails od " +
            "LEFT JOIN FETCH od.food f " +
            "LEFT JOIN FETCH o.comment c " +
            "LEFT JOIN FETCH o.store s " +
            "LEFT JOIN FETCH od.likedFood lf " + // 使用 LEFT JOIN FETCH 以防沒有評論
           
            "WHERE u.id = :userId "+ 
            "AND o.status = 'COMPLETED' OR  o.status = 'CANCELLED'")    
    List<OrderBean> findHistoryByUserId(@Param("userId") Integer userId);
    //用來找歷史訂單的訂單
    
    
    @Query("SELECT o FROM OrderBean o " +
            "LEFT JOIN FETCH o.user u " +
            "LEFT JOIN FETCH o.orderDetails od " +
            "LEFT JOIN FETCH od.food f " +
            "LEFT JOIN FETCH o.comment c " +
            "LEFT JOIN FETCH o.store s " +
            "LEFT JOIN FETCH od.likedFood lf " + // 使用 LEFT JOIN FETCH 以防沒有評論
            // 如果需要 likedFoods: "LEFT JOIN FETCH od.likedFoods lf " + // 注意：不能同時 JOIN FETCH 多個 OneToMany 集合
            "WHERE u.id = :userId "+ 
            "AND o.status != 'COMPLETED' AND  o.status != 'CANCELLED'")
    List<OrderBean> findUncompleteByUserId(@Param("userId")Integer userId);
  //用來找正在線上的訂單
    
    
    @Query("SELECT o FROM OrderBean o " +
            "LEFT JOIN FETCH o.user u " +
            "LEFT JOIN FETCH o.orderDetails od " +
            "LEFT JOIN FETCH od.food f " +
            "LEFT JOIN FETCH o.promotion p " +
            "LEFT JOIN FETCH o.comment c " +
            "LEFT JOIN FETCH o.store s " +
            "LEFT JOIN FETCH od.likedFood lf " +
            "WHERE s.id = :storeId "
            + "ORDER By o.pickupTime DESC")
    List<OrderBean> findByStore_Id(@Param("storeId")Integer storeId);
//  尋找店家的所有訂單
    
    
}