package tw.com.ispan.eeit.model.entity.comment;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
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
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private UserBean user;

    @Column(name = "is_liked")
    private Boolean isLiked;

    @Column(name = "updated_time")
    private LocalDateTime updatedTime;
//------------comment資料夾-----------------------------------
//------------food   資料夾-----------------------------------
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "food_id")
    @JsonBackReference
    private FoodBean food;
//------------order  資料夾-----------------------------------
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_detail_id")
    @JsonBackReference
    private OrderDetailBean orderDetail;

//------------promotion資料夾---------------------------------
//------------store  資料夾-----------------------------------
//------------多對多關聯表--------------------------------------






}
