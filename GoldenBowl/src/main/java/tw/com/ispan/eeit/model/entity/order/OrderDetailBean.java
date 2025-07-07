package tw.com.ispan.eeit.model.entity.order;

import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
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
import tw.com.ispan.eeit.model.entity.comment.LikedFoodBean;
import tw.com.ispan.eeit.model.entity.food.FoodBean;
import tw.com.ispan.eeit.model.entity.store.SpecBean;

@Data
@Entity
@Table(name = "order_detail")
@NoArgsConstructor
public class OrderDetailBean {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_id")//SQLServer的名稱
	@JsonBackReference
	private OrderBean order;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "food_id")
	@JsonBackReference
	private FoodBean food;



	private Integer quantity;

	private Integer price;

	@Column(name = "sub_total")
	private Integer subTotal;

	private Integer total;

	@OneToMany(mappedBy = "orderDetail",fetch = FetchType.LAZY)
	
	private Set<LikedFoodBean> likedFoods;
	
	@ManyToMany(fetch = FetchType.LAZY)
	@JsonIgnore
	@JoinTable(
			name="order_detail_spec",
			joinColumns=@JoinColumn(name="order_detail_id"),
			inverseJoinColumns =@JoinColumn(name="spec_id"))
	private List<SpecBean> specs;
}
