package tw.com.ispan.eeit.model.entity.order;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;




@Entity
@Table(name ="[order]")
@Getter
@Setter
public class OrderBean {
	@Id
	@Column(name="id")
	private Integer id;
	@Column(name="user_id")
	private Integer userId;
	@Column(name="store_id")
	private Integer storeId;
	@Column(name="total")
	private Integer total;
	@Column(name="status",columnDefinition = "varchar")
	private String status;
	@Column(name="create_time",columnDefinition = "datetime")
	private Date createTime;
	@Column(name="content",columnDefinition = "nvarchar(50)")
	private String content;
	@Column(name="pickup_time",columnDefinition = "datetime")
	private Date pickupTime;
	@Column(name="promotion_id")
	private Integer promotionId;
	
	
	
	
	
	
	
}
