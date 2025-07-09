package tw.com.ispan.eeit.model.entity.store;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "special_hours")
@NoArgsConstructor
public class SpecialHoursBean {

	@Id
	@Column(name = "id")
	private Integer id;
	@Column(name = "store_id")
	private Integer storeId;
	@Column(name = "date", columnDefinition = "datetime2(6)")
	private LocalDate date;
	@Column(name = "open_time", columnDefinition = "time(0)")
	private LocalTime openTime;
	@Column(name = "close_time", columnDefinition = "time(0)")
	private LocalTime closeTime;
	@Column(name = "is_close")
	private Boolean isClose;
}
