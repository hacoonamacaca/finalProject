package tw.com.ispan.eeit.model.entity;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

<<<<<<< HEAD
import com.fasterxml.jackson.annotation.JsonIgnore;
=======
import com.fasterxml.jackson.annotation.JsonManagedReference;
>>>>>>> origin/jamie

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import tw.com.ispan.eeit.model.entity.comment.CategorySearchedBean;
import tw.com.ispan.eeit.model.entity.comment.CommentBean;
import tw.com.ispan.eeit.model.entity.comment.LikedFoodBean;
import tw.com.ispan.eeit.model.entity.food.FoodBean;
import tw.com.ispan.eeit.model.entity.food.UserTagBean;
import tw.com.ispan.eeit.model.entity.order.OrderBean;
import tw.com.ispan.eeit.model.entity.reservation.ReservationBean;
import tw.com.ispan.eeit.model.entity.store.StoreBean;

@Data
@NoArgsConstructor
@Entity
@Table(name = "app_user")
public class UserBean {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 50)
    private String email;

    @Column(nullable = false, length = 100)
    private String password;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(length = 10)
    private String phone;

    @Column(name = "sigup_date")
    private LocalDateTime signupDate;

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "is_verify")
    private Boolean isVerify;

    @Column(name = "hide_until")
    private LocalDateTime hideUntil;

    @OneToMany(mappedBy = "user")
<<<<<<< HEAD
    @JsonIgnore
=======
    @JsonManagedReference("user-orders")
>>>>>>> origin/jamie
    private List<OrderBean> orders;

    @OneToMany(mappedBy = "user")
    @JsonManagedReference("user-userTags")
    private List<UserTagBean> userTags;

    @OneToMany(mappedBy = "user")
    @JsonManagedReference("user-likedFoods")
    private List<LikedFoodBean> likedFoods;

    @OneToMany(mappedBy = "user")
    @JsonManagedReference("user-comments")
    private List<CommentBean> comments;

    @OneToMany(mappedBy = "user")
    @JsonManagedReference("user-categorySearched")
    private List<CategorySearchedBean> categorySearched;

    // 新增：用戶的訂位記錄
    @OneToMany(mappedBy = "user")
    @JsonManagedReference("user-reservations")
    private List<ReservationBean> reservations;

    // 多對多關係：User 與 Store 通過 favorite_store 表格關聯
    @ManyToMany
    @JoinTable(name = "favorite_store", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "store_id"))
    private Set<StoreBean> favoriteStores = new HashSet<>();

    // 多對多關係：User 與 Food 通過 favorite_food 表格關聯
    @ManyToMany
    @JoinTable(name = "favorite_food", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "food_id"))
    private Set<FoodBean> favoriteFoods = new HashSet<>();
}