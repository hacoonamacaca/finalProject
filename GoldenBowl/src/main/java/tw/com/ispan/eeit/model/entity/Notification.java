package tw.com.ispan.eeit.model.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(name = "notification")
public class Notification {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column(name = "user_id")
	private Integer userId;
	
	@Column(name = "promotion_id")
	private Integer promotiomId;
	
	@Column(name = "created_time")
	private LocalDateTime createdTime;
	
	@Column(name = "is_read")
	private Boolean isRead;
	
	@Column(name = "read_time")
	private LocalDateTime readTime;
	
	//建構子
	public Notification() {}

	// getter & setter
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Integer getPromotiomId() {
		return promotiomId;
	}

	public void setPromotiomId(Integer promotiomId) {
		this.promotiomId = promotiomId;
	}

	public LocalDateTime getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(LocalDateTime createdTime) {
		this.createdTime = createdTime;
	}

	public Boolean getIsRead() {
		return isRead;
	}

	public void setIsRead(Boolean isRead) {
		this.isRead = isRead;
	}

	public LocalDateTime getReadTime() {
		return readTime;
	}

	public void setReadTime(LocalDateTime readTime) {
		this.readTime = readTime;
	}	
}
