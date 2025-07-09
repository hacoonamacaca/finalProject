package tw.com.ispan.eeit.model.entity.promotion;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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
@Table(name = "notification")
public class NotificationBean {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	@JsonBackReference
	private UserBean user;

	@Column(name = "created_time")
	private LocalDateTime createdTime;

	@Column(name = "is_read")
	private Boolean isRead;

	@Column(name = "read_time")
	private LocalDateTime readTime;
//------------promotion資料夾---------------------------------
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "promotion_id")
	@JsonBackReference
	private PromotionBean promotion;

}