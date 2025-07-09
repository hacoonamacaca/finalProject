package tw.com.ispan.eeit.model.entity.promotion;

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
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import tw.com.ispan.eeit.model.entity.food.TagBean;
import tw.com.ispan.eeit.model.entity.order.OrderBean;
import tw.com.ispan.eeit.model.entity.store.StoreBean;

@Data
@NoArgsConstructor
@Entity
@Table(name = "promotion")
public class PromotionBean {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column
	private String title;

	@Column
	private String description;

	@Column(name = "discount_type")
	private String discountType;

	@Column(name = "discount_value")
	private String discountValue;

	@Column(name = "min_spend")
	private Integer minSpend;

	@Column(name = "start_time")
	private LocalDateTime startTime;

	@Column(name = "end_time")
	private LocalDateTime endTime;

	@Column(name = "created_time")
	private LocalDateTime createdTime;

	@Column(name = "updated_time")
	private LocalDateTime updatedTime;

	@Column
	private String code;

	@Column(name = "max_usage")
	private Integer maxUsage;

	@Column(name = "user_usage_limit")
	private Integer userUsageLimit;

	@Column(length = 20)
	private String status;
	
	@ManyToOne
	@JoinColumn(name = "plan_id")
	private PlanBean plan;



//------------comment資料夾-----------------------------------
	
//------------food   資料夾-----------------------------------
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "tag_id")
	@JsonBackReference
	private TagBean tag;
	
//------------order  資料夾-----------------------------------
	@OneToMany(mappedBy = "promotion")
	@JsonManagedReference
	private List<OrderBean> orders;
	
//------------promotion資料夾---------------------------------
	@OneToMany(mappedBy = "promotion",fetch = FetchType.LAZY)
	@JsonManagedReference
	private List<NotificationBean> notifications;
	
//------------store  資料夾-----------------------------------
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "store_id")
	@JsonBackReference
	private StoreBean store;
	
//------------多對多關聯表------------------------------------

	
}