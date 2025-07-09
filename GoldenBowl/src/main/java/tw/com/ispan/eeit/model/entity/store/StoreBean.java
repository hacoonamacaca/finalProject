package tw.com.ispan.eeit.model.entity.store;
//07/09核對過 增加specialHours,specGroup
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.annotations.BatchSize;
import org.locationtech.jts.geom.Point;

import com.fasterxml.jackson.annotation.JsonBackReference;
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
public class StoreBean {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 50)
    private String name;

    @Column(length = 50)
    private String address;

    @Convert(converter = tw.com.ispan.eeit.model.converter.PointToGeographyConverter.class)
    @Column(name = "store_coords", columnDefinition = "GEOGRAPHY")
    private Point storeCoords;
    
    private Double lng;

    private Double lat;

    @Column(name="sotre_intro",columnDefinition = "varchar(max)")
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
    
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    @JsonBackReference
    private OwnerBean owner;
//------------comment資料夾-----------------------------------
    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL)
    @BatchSize(size = 25)
    @JsonManagedReference
    private Set<CommentBean> comments;
    
    @OneToMany(mappedBy = "store",fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<CategorySearchedBean> categorySearched;
    
//------------food   資料夾-----------------------------------
    @OneToMany(mappedBy = "store",fetch = FetchType.LAZY)
    @JsonManagedReference
    private Set<FoodBean> foods;

    @OneToMany(mappedBy = "store",fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<FoodClassBean> foodClasses; // 如果這裡不是 Set，要特別注意
    
//------------order  資料夾-----------------------------------
    @OneToMany(mappedBy = "store",fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<OrderBean> orders;
    
//------------store  資料夾-----------------------------------

    @ManyToMany(mappedBy = "stores",fetch = FetchType.LAZY)
    @JsonManagedReference
    private Set<CategoryBean> categories;
    // 多對多關係：Store 與 User 通過 favorite_store 表格關聯
    
    @OneToMany(mappedBy = "store",fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<OpenHourBean> OpenHours;
    
    @OneToMany(mappedBy = "store",fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<SpecGroupBean> specGroups;

    @OneToMany(mappedBy = "store",fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<SpecialHoursBean> specialHours;
    
    
    //------------多對多關聯表--------------------------------------
    @ManyToMany(mappedBy = "favoriteStores",fetch = FetchType.LAZY)
    @JsonBackReference
    private Set<UserBean> favoritedByUsers = new HashSet<>();
}