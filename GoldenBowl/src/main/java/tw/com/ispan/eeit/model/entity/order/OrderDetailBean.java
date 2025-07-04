package tw.com.ispan.eeit.model.entity.order;

import java.util.List;

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

	@ManyToOne
	@JoinColumn(name = "order_id")
	private OrderBean order;

	@ManyToOne
	@JoinColumn(name = "food_id")
	private FoodBean food;

	@Column(length = 100)
	private String name;

	private Integer quantity;

	private Integer price;

	@Column(name = "sub_total")
	private Integer subTotal;

	private Integer total;

	@OneToMany(mappedBy = "orderDetail")
	private List<LikedFoodBean> likedFoods;
}
