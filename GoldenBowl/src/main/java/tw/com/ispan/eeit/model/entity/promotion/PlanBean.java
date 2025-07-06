package tw.com.ispan.eeit.model.entity.promotion;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "plan")
public class PlanBean {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(length = 50)
	private String name;

	private Integer price;

	@Column(name = "valid_months")
	private Integer validMonths;

	@Column(name = "created_time")
	private LocalDateTime createdTime;

	@Column(name = "update_time")
	private LocalDateTime updateTime;

	@OneToMany(mappedBy = "plan")
	private List<PromotionBean> promotions;

	@OneToMany(mappedBy = "plan")
	private List<SubRecordBean> subRecords;
}
