package tw.com.ispan.eeit.model.entity.store;

import java.time.LocalTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

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

@Entity
@Table(name = "open_hour")
@Data
@NoArgsConstructor
public class OpenHourBean {
	@Id
	@Column(name = "id")
	private Integer id;

	@Column(name = "day")
	private Integer day;
	@Column(name = "open_time", columnDefinition = "TIME(0)")
	private LocalTime openTime;
	@Column(name = "close_time", columnDefinition = "TIME(0)")
	private LocalTime close_time;

	@ManyToOne
	@JoinColumn(name = "store_id")
	@JsonBackReference
	private StoreBean store;

}