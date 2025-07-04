package tw.com.ispan.eeit.model.entity.promotion;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import tw.com.ispan.eeit.model.entity.UserBean;

@Data
@NoArgsConstructor
@Entity
@Table(name = "sub_record")
public class SubRecordBean {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private UserBean user;

	@ManyToOne
	@JoinColumn(name = "plan_id")
	private PlanBean plan;

	@Column(name = "start_time")
	private LocalDateTime startTime;

	@Column(name = "end_time")
	private LocalDateTime endTime;

	private Integer amount;

	private Integer method;

	@Column(name = "paid_time")
	private LocalDateTime paidTime;
}