package tw.com.ispan.eeit.model.entity.store;

import java.time.LocalTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="open_hour")
@Getter
@Setter
public class OpenHourBean {
	@Id
	@Column(name="id")
	private Integer  id	;
	@Column(name="store_id")
	private Integer storeId; 	 
	@Column(name="day")
	private Integer day;
	@Column(name="open_time",columnDefinition = "TIME(0)")
	private LocalTime  openTime;
	@Column(name="close_time" ,columnDefinition = "TIME(0)")
	private LocalTime close_time;
	
	
	
	
}
