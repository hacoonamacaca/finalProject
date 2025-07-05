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
@Table(name = "notification")
public class NotificationBean {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private UserBean user;

	@ManyToOne
	@JoinColumn(name = "promotion_id")
	private PromotionBean promotion;

	@Column(name = "created_time")
	private LocalDateTime createdTime;

	@Column(name = "is_read")
	private Boolean isRead;

	@Column(name = "read_time")
	private LocalDateTime readTime;
}