package tw.com.ispan.eeit.model.entity.store;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.annotations.BatchSize;
import org.locationtech.jts.geom.Point;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
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
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import tw.com.ispan.eeit.model.entity.OwnerBean;
import tw.com.ispan.eeit.model.entity.UserBean;
import tw.com.ispan.eeit.model.entity.comment.CategorySearchedBean;
import tw.com.ispan.eeit.model.entity.comment.CommentBean;
import tw.com.ispan.eeit.model.entity.food.FoodBean;
import tw.com.ispan.eeit.model.entity.food.FoodClassBean;
import tw.com.ispan.eeit.model.entity.order.OrderBean;

@Data
@Entity
@Table(name = "store")
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class StoreBean {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY) // 建議對 ManyToOne 關聯設置 LAZY
    @JoinColumn(name = "owner_id")
    @JsonIgnore
    private OwnerBean owner;

    @Column(length = 50)
    private String name;

    @Column(length = 50)
    private String address;

    @Convert(converter = tw.com.ispan.eeit.model.converter.PointToGeographyConverter.class)
    @Column(name = "store_coords", columnDefinition = "GEOGRAPHY")
    private Point storeCoords;

    private Double lng;

    private Double lat;

    @Column(name = "store_intro", columnDefinition = "varchar(max)")
    private String storeIntro;

    @Column(columnDefinition = "varchar(max)")
    private String photo;

    @Column(name = "is_open")
    private Boolean isOpen;

    private Float score;

    @Column(name = "created_time")
    private LocalDateTime createdTime;

    @Column(name = "updated_time")
    private LocalDateTime updatedTime;

    @Column(name = "is_active")
    private Boolean isActive;

    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL, orphanRemoval = true) // 如果食物刪除是級聯刪除，加上 cascade 和
                                                                                    // orphanRemoval
    private Set<FoodBean> foods; // 您已經改為 Set，這是好的

    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL) // 訂單通常不應該因為 Store 刪除而刪除，請根據業務邏輯調整 cascade
    @JsonIgnore
    private List<OrderBean> orders; // 如果這裡不是 Set，要特別注意，但 StackOverflow 是 equals/hashCode 引起

    @ManyToMany
    @JoinTable(name = "store_category", joinColumns = @JoinColumn(name = "store_id"), inverseJoinColumns = @JoinColumn(name = "category_id"))
    @JsonManagedReference
    private Set<CategoryBean> categories; // 您已經改為 Set，這是好的

    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL) // 評論通常不應該因為 Store 刪除而刪除，請根據業務邏輯調整 cascade
    @BatchSize(size = 25)
    private Set<CommentBean> comments; // 您已經改為 Set，這是好的

    @OneToMany(mappedBy = "store")
    @JsonIgnore
    private List<FoodClassBean> foodClasses; // 如果這裡不是 Set，要特別注意

    @OneToMany(mappedBy = "store")
    @JsonIgnore
    private List<CategorySearchedBean> categorySearched; // 如果這裡不是 Set，要特別注意

    @ManyToMany(mappedBy = "favoriteStores")
    @JsonIgnore
    private Set<UserBean> favoritedByUsers = new HashSet<>();
}