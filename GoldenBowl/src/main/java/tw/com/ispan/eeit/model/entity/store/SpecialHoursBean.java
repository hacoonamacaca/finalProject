package tw.com.ispan.eeit.model.entity.store;

import java.time.LocalTime;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name="special_hours")
public class SpecialHoursBean {
	
	@Id
	@Column(name="id")
	private Integer id;
	@Column(name="store_id")
	private Integer storeId ;
	@Column(name="date")
	private Date date;
	@Column(name="open_time",columnDefinition = "Time(0)")
	private LocalTime openTime;
	@Column(name="close_time",columnDefinition = "Time(0)")
	private LocalTime closeTime;
	@Column(name="is_close")
	private Boolean isClose;
	
	
	
	
}
