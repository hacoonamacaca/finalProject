package tw.com.ispan.eeit.model.entity.order;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="order_detail")
public class OrderDetailBean {
	@Id
	@Column(name="id")
	private Integer id;
	@Column(name="order_id")
	private Integer orderId;
	@Column(name="food_id")
	private Integer foodId;
	@Column(name="name")
	private String name;
	@Column(name="quantity")
	private Integer quantity;
	@Column(name="price")
	private Integer price;
	@Column(name="sub_total")
	private Integer subTotal;
	@Column(name="total")
	private Integer total;
	
	
	
	
	
}
