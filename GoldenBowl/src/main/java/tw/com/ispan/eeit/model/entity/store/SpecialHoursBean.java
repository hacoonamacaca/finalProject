package tw.com.ispan.eeit.model.entity.store;

//0709修改store
import java.time.LocalTime;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "store_id")
	@JsonBackReference
	private StoreBean store;

	// @Column(name = "store_id")
	// private Integer storeId;

	@Column(name = "date")
	private Date date;
	@Column(name = "open_time", columnDefinition = "Time(0)")
	private LocalTime openTime;
	@Column(name = "close_time", columnDefinition = "Time(0)")
	private LocalTime closeTime;
	@Column(name = "is_close")
	private Boolean isClose;

}
