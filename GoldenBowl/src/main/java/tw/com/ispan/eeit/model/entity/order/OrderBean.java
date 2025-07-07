package tw.com.ispan.eeit.model.entity.order;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import tw.com.ispan.eeit.model.entity.UserBean;
import tw.com.ispan.eeit.model.entity.comment.CommentBean;
import tw.com.ispan.eeit.model.entity.promotion.PromotionBean;
import tw.com.ispan.eeit.model.entity.store.StoreBean;

@Data
@Entity
@Table(name = "customer_order")
@NoArgsConstructor
public class OrderBean {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	@JsonBackReference
	private UserBean user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JsonBackReference
	@JoinColumn(name = "store_id")//customer_order表中的欄位
	private StoreBean store;

	@ManyToOne(fetch = FetchType.LAZY)
	@JsonBackReference
	@JoinColumn(name = "promotion_id")
	private PromotionBean promotion; // 假設 Promotion Entity 存在

	private Integer total;

	@Column(length = 10)
	private String status;

	@Column(name = "create_time")
	private LocalDateTime createTime;

	@Column(length = 50)
	private String content;

	@Column(name = "pickup_time")
	private LocalDateTime pickupTime;

	@OneToMany(mappedBy = "order",fetch = FetchType.LAZY)
	@JsonManagedReference
	private List<OrderDetailBean> orderDetails;

	@OneToOne(mappedBy = "order",fetch = FetchType.LAZY)
	@JsonManagedReference
	private CommentBean comments;
//	mappedBy="order" 是comments的屬性order
//    @Override
//    public String toString() {
//        // 安全地獲取 ID，而不觸發懶加載
//        Integer userId = (user != null) ? user.getId() : null;
//        Integer storeId = (store != null) ? store.getId() : null;
//        Integer promotionId = (promotion != null) ? promotion.getId() : null;
//
//        // 檢查集合是否已初始化，以避免 LazyInitializationException
//        String orderDetailsInfo = (orderDetails != null && Hibernate.isInitialized(orderDetails)) ?
//                                   "orderDetails.size=" + orderDetails.size() : "orderDetails=[Not Loaded]";
//        String commentsInfo = (comments != null && Hibernate.isInitialized(comments)) ?
//                              "comments.size=" + comments.size() : "comments=[Not Loaded]";
//
//        return "OrderBean ["
//                + "id=" + id
//                + ", userId=" + userId
//                + ", storeId=" + storeId
//                + ", promotionId=" + promotionId
//                + ", total=" + total
//                + ", status='" + status + "'"
//                + ", createTime=" + createTime
//                + ", content='" + content + "'"
//                + ", pickupTime=" + pickupTime
//                + ", " + orderDetailsInfo
//                + ", " + commentsInfo
//                + "]";
//    }
	
	
	
	
	
}
