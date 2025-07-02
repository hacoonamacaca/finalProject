package tw.com.ispan.eeit.model.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "promotion")

public class Promotion {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column (name = "plan_id")
	private Integer planId;
	
	@Column (name = "store_id")
	private Integer storeId;
	
	@Column (name = "tag_id")
	private Integer tagId;
	
	@Column (name = "title")
	private String title;
	
	@Column (name = "description")
	private String description;
	
	@Column (name = "discount_type")
	private String discountType;
	
	@Column (name = "discount_value")
	private String discountValue;
	
	@Column (name = "min_spend")
	private Integer minSpend;
	
	@Column (name = "start_time")
	private LocalDateTime startTime;
	
	@Column (name = "end_time")
	private LocalDateTime endTime;
	
	@Column (name = "created_time")
	private LocalDateTime createdTime;
	
	@Column (name = "updated_time")
	private LocalDateTime updatedTime;
	
	@Column (name = "code")
	private String code;
	
	@Column (name = "max_usage")
	private Integer maxUsage;
	
	@Column (name = "user_usage_limit")
	private Integer userUsageLimit;

	//建構子
	public Promotion() {}
	
	//getter & setter
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getPlanId() {
		return planId;
	}
	
	public void setPlanId(Integer planId) {
		this.planId = planId;
	}

	public Integer getStoreId() {
		return storeId;
	}
	public void setStoreId(Integer storeId) {
		this.storeId = storeId;
	}

	public Integer getTagId() {
		return tagId;
	}
	
	public void setTagId(Integer tagId) {
		this.tagId = tagId;
	}

	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}

	public String getDiscountType() {
		return discountType;
	}
	
	public void setDiscountType(String discountType) {
		this.discountType = discountType;
	}
	
	public String getDiscountValue() {
		return discountValue;
	}
	
	public void setDiscountValue(String discountValue) {
		this.discountValue = discountValue;
	}

	public Integer getMinSpend() {
		return minSpend;
	}
	
	public void setMinSpend(Integer minSpend) {
		this.minSpend = minSpend;
	}

	public LocalDateTime getStartTime() {
		return startTime;
	}
	
	public void setStartTime(LocalDateTime startTime) {
		this.startTime = startTime;
	}

	public LocalDateTime getEndTime() {
		return endTime;
	}
	
	public void setEndTime(LocalDateTime endTime) {
		this.endTime = endTime;
	}
	
	public LocalDateTime getCreatedTime() {
		return createdTime;
	}
	
	public void setCreatedTime(LocalDateTime createdTime) {
		this.createdTime = createdTime;
	}

	public LocalDateTime getUpdatedTime() {
		return updatedTime;
	}
	
	public void setUpdatedTime(LocalDateTime updatedTime) {
		this.updatedTime = updatedTime;
	}

	public String getCode() {
		return code;
	}
	
	public void setCode(String code) {
		this.code = code;
	}

	public Integer getMaxUsage() {
		return maxUsage;
	}
	
	public void setMaxUsage(Integer maxUsage) {
		this.maxUsage = maxUsage;
	}

	public Integer getUserUsageLimit() {
		return userUsageLimit;
	}
	
	public void setUserUsageLimit(Integer userUsageLimit) {
		this.userUsageLimit = userUsageLimit;
	}
}
