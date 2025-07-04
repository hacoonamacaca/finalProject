package tw.com.ispan.eeit.model.entity.comment;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import tw.com.ispan.eeit.model.entity.UserBean;
import tw.com.ispan.eeit.model.entity.food.FoodBean;
import tw.com.ispan.eeit.model.entity.order.OrderDetailBean;

@Data
@Entity
@Table(name = "liked_food")
@NoArgsConstructor
public class LikedFoodBean {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserBean user;

    @ManyToOne
    @JoinColumn(name = "food_id")
    private FoodBean food;

    @ManyToOne
    @JoinColumn(name = "order_detail_id")
    private OrderDetailBean orderDetail;

    @Column(name = "is_liked")
    private Boolean isLiked;

    @Column(name = "updated_time")
    private LocalDateTime updatedTime;
}
