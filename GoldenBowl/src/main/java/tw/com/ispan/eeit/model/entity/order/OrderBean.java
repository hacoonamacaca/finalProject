package tw.com.ispan.eeit.model.entity.order;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
@Table(name = "customer_order")
@NoArgsConstructor
public class OrderBean {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@ManyToOne(fetch = FetchType.LAZY) // 使用 LAZY 加載，避免不必要的數據加載和循環引用
	@JoinColumn(name = "user_id")
	@JsonIgnore
	private UserBean user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "store_id")
	@JsonIgnore
	private StoreBean store;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "promotion_id")
	@JsonIgnore
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

	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true) // 訂單刪除時，明細也刪除
	@JsonIgnore // 在 OrderBean 中忽略 orderDetails 的反向引用，避免無限循環
	private List<OrderDetailBean> orderDetails;

	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
	@JsonIgnore // 在 OrderBean 中忽略 comments 的反向引用，避免無限循環
	private List<CommentBean> comments;
}
