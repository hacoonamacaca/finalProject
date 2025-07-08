package tw.com.ispan.eeit.model.entity.order;

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
import tw.com.ispan.eeit.model.entity.comment.LikedFoodBean;
import tw.com.ispan.eeit.model.entity.food.FoodBean;

@Data
@Entity
@Table(name = "order_detail")
@NoArgsConstructor
public class OrderDetailBean {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_id")
	@JsonIgnore // 在 OrderDetailBean 中忽略 order 的反向引用，避免無限循環
	private OrderBean order;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "food_id")
	private FoodBean food; // 假設 Food Entity 存在，可以加載其信息

	@Column(length = 100)
	private String name; // 這個字段通常是 redundant 的，因為 food bean 已經有 name 了。但如果需要獨立於 food bean，可以保留。

	private Integer quantity;

	private Integer price; // 這個字段通常是 redundant 的，因為 food bean 已經有 price 了。同上。

	@Column(name = "sub_total")
	private Integer subTotal;

	private Integer total; // 在 OrderDetail 層面，通常是 subTotal

	@OneToMany(mappedBy = "orderDetail", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonIgnore // 假設 LikedFoodBean 有 orderDetail 字段，在此處忽略
	private List<LikedFoodBean> likedFoods;
}
