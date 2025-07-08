package tw.com.ispan.eeit.model.entity.store;

import java.time.DayOfWeek;
import java.time.LocalTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "open_hour")
@Data
@NoArgsConstructor
public class OpenHourBean {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;
	@Column(name = "store_id")
	private Integer storeId;
	@Column(name = "day")
	private Integer day;
	@Column(name = "open_time", columnDefinition = "TIME(0)")
	private LocalTime openTime;

	@Column(name = "close_time", columnDefinition = "TIME(0)")
	private LocalTime closeTime;

	@Column(name = "is_open", nullable = false)
	private Boolean isOpen = true;

	@Column(name = "time_interval_minutes", nullable = false)
	private Integer timeIntervalMinutes = 30; // 預設30分鐘一個時段
}
