package tw.com.ispan.eeit.model.entity.food;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import tw.com.ispan.eeit.model.entity.UserBean;
import tw.com.ispan.eeit.model.entity.comment.LikedFoodBean;
import tw.com.ispan.eeit.model.entity.order.OrderDetailBean;
import tw.com.ispan.eeit.model.entity.store.SpecGroupBean;
import tw.com.ispan.eeit.model.entity.store.StoreBean;

@Data
@Entity
@Table(name = "food")
@NoArgsConstructor
public class FoodBean {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 100)
    private String name;

    private Integer price;

    @Column(length = 100)
    private String description;

    @Column(name = "create_time")
    private LocalDateTime createTime;

    @Column(name = "update_time")
    private LocalDateTime updateTime;

    private Float score;

    @Column(name = "is_active")
    private Boolean isActive;

    private Integer stock;

    @Column(name = "img_resource", length = 500)
    private String imgResource;
//------------comment資料夾-----------------------------------
    @OneToMany(mappedBy = "food",fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<LikedFoodBean> likedFoods;
    
//------------food   資料夾-----------------------------------
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "food_class_id"
    , joinColumns = @JoinColumn(name = "food_id")
    , inverseJoinColumns = @JoinColumn(name = "food_class_id"))
    @JsonBackReference
    private List<FoodClassBean> foodClasses;
    
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "food_tag"
    , joinColumns = @JoinColumn(name = "food_id")
    , inverseJoinColumns = @JoinColumn(name = "tag_id"))
    @JsonBackReference
    private List<TagBean> tags;
    
//------------order  資料夾-----------------------------------
    @OneToMany(mappedBy = "food",fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<OrderDetailBean> orderDetails;
    
//------------promotion資料夾---------------------------------
//------------store  資料夾-----------------------------------
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "food_spec_group"
    	, joinColumns = @JoinColumn(name = "food_id")
    	, inverseJoinColumns = @JoinColumn(name = "spec_group_id"))
    @JsonBackReference
    private List<SpecGroupBean> specGroups;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
    @JsonBackReference
    private StoreBean store;
    
//------------多對多關聯表------------------------------------

    // 多對多關係：Food 與 User 通過 favorite_food 表格關聯
    @ManyToMany(mappedBy = "favoriteFoods",fetch = FetchType.LAZY)
    @JsonManagedReference
    private Set<UserBean> favoritedByUsers = new HashSet<>();
}
