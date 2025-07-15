package tw.com.ispan.eeit.model.dto.store;

import java.time.DayOfWeek;
import java.time.LocalTime;

import lombok.Data;
import lombok.NoArgsConstructor;
import tw.com.ispan.eeit.model.entity.store.OpenHourBean;
import tw.com.ispan.eeit.model.entity.store.StoreBean;
import tw.com.ispan.eeit.util.DatetimeConvert;

@Data
@NoArgsConstructor
public class OpenHourDTO {
	
	 // 時間格式定義，與資料庫的 TIME(0) 匹配
    private static final String TIME_FORMAT = "HH:mm";

    private Integer id;
    private Integer storeId;
    private DayOfWeek dayOfWeek;
    private LocalTime openTime;
    private LocalTime closeTime;

    // 簡化的營業時間字串格式
    private String openTimeStr;
    private String closeTimeStr;

    // 是否營業的標示
    private Boolean isOpen;

    // 星期中文名稱
    private String dayName;

    public OpenHourDTO(Integer id, Integer storeId, DayOfWeek dayOfWeek, LocalTime openTime, LocalTime closeTime) {
        this.id = id;
        this.storeId = storeId;
        this.dayOfWeek = dayOfWeek;
        this.openTime = openTime;
        this.closeTime = closeTime;
        this.isOpen = (openTime != null && closeTime != null);

        // 設定時間字串
        if (openTime != null) {
            this.openTimeStr = openTime.toString();
        }
        if (closeTime != null) {
            this.closeTimeStr = closeTime.toString();
        }

        // 設定星期中文名稱
        this.dayName = getDayName(dayOfWeek);
    }

    private String getDayName(DayOfWeek dayOfWeek) {
        switch (dayOfWeek) {
            case MONDAY:
                return "星期一";
            case TUESDAY:
                return "星期二";
            case WEDNESDAY:
                return "星期三";
            case THURSDAY:
                return "星期四";
            case FRIDAY:
                return "星期五";
            case SATURDAY:
                return "星期六";
            case SUNDAY:
                return "星期日";
            default:
                return dayOfWeek.toString();
        }
    }
    
    
    /**
     * 將 OpenHourBean 轉換為 OpenHourDTO。
     * @param bean OpenHourBean 物件
     * @return 轉換後的 OpenHourDTO 物件
     */
    public static OpenHourDTO toDto(OpenHourBean bean) {
        if (bean == null) {
            return null;
        }

        // 使用 DTO 提供的完整建構子來初始化屬性，可以利用內部邏輯設定 openTimeStr, closeTimeStr, isOpen, dayName
        OpenHourDTO dto = new OpenHourDTO(
            bean.getId(),
            bean.getStore() != null ? bean.getStore().getId() : null, // 獲取 storeId
            bean.getDayOfWeek(), // 獲取 DayOfWeek 枚舉
            bean.getOpenTime(),
            bean.getCloseTime()
        );
        
        // 如果Bean中的openTime和closeTime都為null，表示休息，則isOpen為false，openTimeStr和closeTimeStr為空
        // 否則，isOpen為true，時間字串已在建構子中設定
        if (bean.getOpenTime() == null && bean.getCloseTime() == null) {
            dto.setIsOpen(false);
            dto.setOpenTimeStr("");
            dto.setCloseTimeStr("");
        } else {
            dto.setIsOpen(true);
            dto.setOpenTimeStr(DatetimeConvert.toString(bean.getOpenTime(), TIME_FORMAT));
            dto.setCloseTimeStr(DatetimeConvert.toString(bean.getCloseTime(), TIME_FORMAT));
        }

        return dto;
    }

    /**
     * 將 OpenHourDTO 轉換為 OpenHourBean。
     * 注意：StoreBean 的設置通常應在 Service 層完成資料庫查詢，此處僅設置 Store ID。
     * @param dto OpenHourDTO 物件
     * @return 轉換後的 OpenHourBean 物件
     */
    public static OpenHourBean toBean(OpenHourDTO dto) {
        if (dto == null) {
            return null;
        }

        OpenHourBean bean = new OpenHourBean();
        bean.setId(dto.getId());

        // 設定 DayOfWeek (Bean 中的 day 屬性會透過 setDayOfWeek 自動轉換)
        bean.setDayOfWeek(dto.getDayOfWeek());

        // 使用 DatetimeConvert 將字串時間轉換為 LocalTime
        // 這裡需要根據 openTimeStr 或 openTime 哪個有值來判斷
        // 優先使用 LocalTime openTime 屬性，如果為 null 再嘗試解析 openTimeStr
        if (dto.getOpenTime() != null) {
            bean.setOpenTime(dto.getOpenTime());
        } else if (dto.getOpenTimeStr() != null && !dto.getOpenTimeStr().isEmpty()) {
            bean.setOpenTime(DatetimeConvert.parseLocalTime(dto.getOpenTimeStr(), TIME_FORMAT));
        } else {
            bean.setOpenTime(null);
        }

        if (dto.getCloseTime() != null) {
            bean.setCloseTime(dto.getCloseTime());
        } else if (dto.getCloseTimeStr() != null && !dto.getCloseTimeStr().isEmpty()) {
            bean.setCloseTime(DatetimeConvert.parseLocalTime(dto.getCloseTimeStr(), TIME_FORMAT));
        } else {
            bean.setCloseTime(null);
        }
        
        // 處理 StoreBean 的設置：
        // 在 DTO 層，我們只能根據 storeId 創建一個 "空殼" 的 StoreBean。
        // 真正的 StoreBean 實例（例如從資料庫查詢獲得的持久化實例）
        // 應在 Service 層處理，以避免 DTO 層與資料存取邏輯耦合。
        if (dto.getStoreId() != null) {
            StoreBean store = new StoreBean();
            store.setId(dto.getStoreId());
            bean.setStore(store);
        }

        return bean;
    }
}