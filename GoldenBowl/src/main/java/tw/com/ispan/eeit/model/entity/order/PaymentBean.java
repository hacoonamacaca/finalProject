package tw.com.ispan.eeit.model.entity.order;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="payment")
@Getter
@Setter
public class PaymentBean {

	@Id
	@Column(name="id")
	private Integer id;
	@Column(name="order_id")
	private Integer orderId;
	@Column(name="method",columnDefinition = "nvarchar(50)")
	private String method;
	@Column(name="transaction_id",columnDefinition = "varchar(100)")
	private String transactionId;
	@Column(name="total")
	private Integer total;
	@Column(name="is_paid")
	private Boolean isPaid;
	@Column(name="paid_time")
	private Date paidTime;
	
	
	
}
