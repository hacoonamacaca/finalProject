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
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import tw.com.ispan.eeit.model.entity.UserBean;
import tw.com.ispan.eeit.model.entity.comment.CommentBean;
import tw.com.ispan.eeit.model.entity.promotion.PromotionBean;
import tw.com.ispan.eeit.model.entity.store.StoreBean;

@Data
@Entity
@Table(name = "customer_order") // 您可能需要將此表名改為非保留字，例如 "customer_orders"
@NoArgsConstructor
@EqualsAndHashCode(of = "id") // <--- 在這裡加上這一行
public class OrderBean {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@ManyToOne(fetch = FetchType.LAZY)
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
	private PromotionBean promotion;

	private Integer total;

	@Column(length = 10)
	private String status;

	@Column(name = "create_time")
	private LocalDateTime createTime;

	@Column(length = 50)
	private String content;

	@Column(name = "pickup_time")
	private LocalDateTime pickupTime;

	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonIgnore
	private List<OrderDetailBean> orderDetails;

	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL) // 評論通常不應該因為訂單刪除而刪除，請根據業務邏輯調整 cascade
	@JsonIgnore
	private List<CommentBean> comments; // 這裡還是 List，如果 `CommentBean` 也需要正確的 `equals`/`hashCode`，並且這個 List 被轉換為 Set，則需注意
}
