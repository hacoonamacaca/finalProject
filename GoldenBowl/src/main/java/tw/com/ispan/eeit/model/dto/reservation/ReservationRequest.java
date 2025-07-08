package tw.com.ispan.eeit.model.dto.reservation;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class ReservationRequest {

    private Integer userId;
    private Integer storeId;
    private LocalDate reservedDate;
    private LocalTime reservedTime;
    private Integer guests;
    private Integer duration; // 分鐘
    private String content;
    private List<Integer> tableIds;

    // 可選的聯絡資訊
    private String contactName;
    private String contactPhone;
    private String contactEmail;

    // 可選的特殊需求
    private String specialRequests;

    // 可選的預訂來源
    private String source; // "web", "mobile", "phone", "walk-in"

    // 可選的預訂類型
    private String type; // "lunch", "dinner", "breakfast", "other"

    // 建構函數
    public ReservationRequest() {
    }

    public ReservationRequest(Integer userId, Integer storeId, LocalDate reservedDate,
            LocalTime reservedTime, Integer guests, Integer duration,
            String content, List<Integer> tableIds) {
        this.userId = userId;
        this.storeId = storeId;
        this.reservedDate = reservedDate;
        this.reservedTime = reservedTime;
        this.guests = guests;
        this.duration = duration;
        this.content = content;
        this.tableIds = tableIds;
    }

    // Getter 和 Setter 方法
    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getStoreId() {
        return storeId;
    }

    public void setStoreId(Integer storeId) {
        this.storeId = storeId;
    }

    public LocalDate getReservedDate() {
        return reservedDate;
    }

    public void setReservedDate(LocalDate reservedDate) {
        this.reservedDate = reservedDate;
    }

    public LocalTime getReservedTime() {
        return reservedTime;
    }

    public void setReservedTime(LocalTime reservedTime) {
        this.reservedTime = reservedTime;
    }

    public Integer getGuests() {
        return guests;
    }

    public void setGuests(Integer guests) {
        this.guests = guests;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<Integer> getTableIds() {
        return tableIds;
    }

    public void setTableIds(List<Integer> tableIds) {
        this.tableIds = tableIds;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getSpecialRequests() {
        return specialRequests;
    }

    public void setSpecialRequests(String specialRequests) {
        this.specialRequests = specialRequests;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "ReservationRequest{" +
                "userId=" + userId +
                ", storeId=" + storeId +
                ", reservedDate=" + reservedDate +
                ", reservedTime=" + reservedTime +
                ", guests=" + guests +
                ", duration=" + duration +
                ", content='" + content + '\'' +
                ", tableIds=" + tableIds +
                ", contactName='" + contactName + '\'' +
                ", contactPhone='" + contactPhone + '\'' +
                ", contactEmail='" + contactEmail + '\'' +
                ", specialRequests='" + specialRequests + '\'' +
                ", source='" + source + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}