package tw.com.ispan.eeit.model.entity.promotion;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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
@Table(name = "plans")
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
//------------promotion資料夾---------------------------------
	@OneToMany(mappedBy = "plan",fetch = FetchType.LAZY)
	@JsonManagedReference
	private List<PromotionBean> promotions;

	@OneToMany(mappedBy = "plan",fetch = FetchType.LAZY)
	@JsonManagedReference
	private List<SubRecordBean> subRecords;



}
