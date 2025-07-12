package tw.com.ispan.eeit.repository.order;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import tw.com.ispan.eeit.model.entity.order.OrderBean;

public interface OrderRepository extends JpaRepository<OrderBean, Integer> {

    //// 都是java類別名稱
    // @Query("SELECT o FROM OrderBean o " +
    // "LEFT JOIN FETCH o.user u " +
    // "LEFT JOIN FETCH o.store s " +
    // "LEFT JOIN FETCH o.promotion p " +
    // "LEFT JOIN FETCH p.plan pp " +
    // "LEFT JOIN FETCH p.tag pt " +
    // "LEFT JOIN FETCH o.orderDetails od " +
    // "LEFT JOIN FETCH od.food f " +
    // "LEFT JOIN FETCH f.store fs " +
    // "LEFT JOIN FETCH od.likedFoods lf " + // 現在可以載入 likedFoods 了 (因為它是 Set)
    // "LEFT JOIN FETCH o.comment c " +
    // "WHERE u.id = :userId")
    // List<OrderBean> findByUser_Id(@Param("userId") Integer userId);
    @Query("SELECT o FROM OrderBean o " +
            "LEFT JOIN FETCH o.user u " +
            "LEFT JOIN FETCH o.orderDetails od " +
            "LEFT JOIN FETCH od.food f " +
            "LEFT JOIN FETCH o.comment c " +
            "LEFT JOIN FETCH f.store fs " +
            "LEFT JOIN FETCH od.likedFood lf " + // 使用 LEFT JOIN FETCH 以防沒有評論
            // 如果需要 likedFoods: "LEFT JOIN FETCH od.likedFoods lf " + // 注意：不能同時 JOIN FETCH
            // 多個 OneToMany 集合
            "WHERE u.id = :userId ")
    List<OrderBean> findByUser_Id(@Param("userId") Integer userId);

    List<OrderBean> findByUser_IdAndStatus(Integer userId, String status);

    List<OrderBean> findByUser_IdAndStatusNot(Integer userId, String status);

    // 範例：根據用戶 ID 查詢訂單，並一次性載入 orderDetails 及其中的 food 和 likedFood-jimmy
    @Query("SELECT o FROM OrderBean o " +
            "LEFT JOIN FETCH o.orderDetails od " +
            "LEFT JOIN FETCH od.food f " + // 假設 OrderDetailBean 有 food 關聯
            "LEFT JOIN FETCH od.likedFood lf " + // 假設 OrderDetailBean 有 likedFood 關聯
            "WHERE o.user.id = :userId") // 假設 OrderBean 有 user 關聯
    List<OrderBean> findByUserIdWithDetailsAndLikedFood(@Param("userId") Integer userId);

    // 範例：根據 ID 查詢單一訂單，並一次性載入所有相關資料-jimmy
    @Query("SELECT o FROM OrderBean o " +
            "LEFT JOIN FETCH o.orderDetails od " +
            "LEFT JOIN FETCH od.food f " +
            "LEFT JOIN FETCH od.likedFood lf " +
            "LEFT JOIN FETCH o.user u " + // 載入 User
            "LEFT JOIN FETCH o.store s " + // 載入 Store
            "LEFT JOIN FETCH o.comment c " + // 載入 Comment
            "WHERE o.id = :orderId")
    Optional<OrderBean> findByIdWithAllDetails(@Param("orderId") Integer orderId);

}