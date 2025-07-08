package tw.com.ispan.eeit.model.entity.order;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@NoArgsConstructor
public class OrderBean {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "user_id")
	@JsonBackReference("user-orders")
	private UserBean user;

	@ManyToOne
	@JoinColumn(name = "store_id")
	@JsonBackReference("store-orders")
	private StoreBean store;

	@ManyToOne
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

	@OneToMany(mappedBy = "order")
	private List<OrderDetailBean> orderDetails;

	@OneToMany(mappedBy = "order")
	private List<CommentBean> comments;
}
