package tw.com.ispan.eeit.model.entity.store;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.locationtech.jts.geom.Point;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
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
import tw.com.ispan.eeit.model.entity.reservation.ReservationBean;
import tw.com.ispan.eeit.model.entity.reservation.TableBean;

@Data
@Entity
@Table(name = "store")
@NoArgsConstructor
public class StoreBean {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    @JsonBackReference("owner-stores")
    private OwnerBean owner;

    @Column(length = 50)
    private String name;

    @Column(length = 50)
    private String address;
    @Convert(converter = tw.com.ispan.eeit.model.converter.PointToGeographyConverter.class)
    @Column(name = "store_coords", columnDefinition = "GEOGRAPHY")
    private Point storeCoords; // 假設 geography 欄位用 String，實際需依 SQL Server 空間資料類型調整

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

    @OneToMany(mappedBy = "store")
    @JsonManagedReference("store-foods")
    private List<FoodBean> foods;

    @OneToMany(mappedBy = "store")
    @JsonManagedReference("store-orders")
    private List<OrderBean> orders;

    @OneToMany(mappedBy = "store")
    @JsonManagedReference("store-reservations")
    private List<ReservationBean> reservations;

    @OneToMany(mappedBy = "store")
    @JsonManagedReference("store-tables")
    private List<TableBean> tables;

    @OneToMany(mappedBy = "store")
    @JsonManagedReference("store-openHours")
    private List<OpenHourBean> openHours;

    @ManyToMany
    @JoinTable(name = "store_category", joinColumns = @JoinColumn(name = "store_id"), inverseJoinColumns = @JoinColumn(name = "category_id"))
    private List<CategoryBean> categories;

    @OneToMany(mappedBy = "store")
    @JsonManagedReference("store-comments")
    private List<CommentBean> comments;

    @OneToMany(mappedBy = "store")
    @JsonManagedReference("store-foodClasses")
    private List<FoodClassBean> foodClasses;

    @OneToMany(mappedBy = "store")
    @JsonManagedReference("store-categorySearched")
    private List<CategorySearchedBean> categorySearched;

    // 多對多關係：Store 與 User 通過 favorite_store 表格關聯
    @ManyToMany(mappedBy = "favoriteStores")
    private Set<UserBean> favoritedByUsers = new HashSet<>();
}