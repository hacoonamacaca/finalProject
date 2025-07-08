package tw.com.ispan.eeit.model.dto.reservation;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import lombok.Data;
import tw.com.ispan.eeit.model.entity.store.StoreBean;
import tw.com.ispan.eeit.model.entity.reservation.TableBean;

@Data
public class StoreReservationDTO {

    // 餐廳基本資訊
    private Integer storeId;
    private String storeName;
    private String storeAddress;
    private String storeIntro;
    private String storePhoto;
    private Boolean isOpen;
    private Float score;
    private List<String> categories;

    // 桌位資訊
    private List<TableInfo> tables;

    // 訂位資訊
    private LocalDate reservedDate;
    private LocalTime reservedTime;
    private Integer guests;
    private Integer duration;
    private String content;

    // 可用性資訊
    private Boolean isAvailable;
    private String availabilityMessage;

    @Data
    public static class TableInfo {
        private Integer tableId;
        private Integer seats;
        private Integer quantity;
        private Boolean isAvailable;
    }

    /**
     * 從 StoreBean 創建 DTO
     */
    public static StoreReservationDTO fromStore(StoreBean store) {
        StoreReservationDTO dto = new StoreReservationDTO();
        dto.setStoreId(store.getId());
        dto.setStoreName(store.getName());
        dto.setStoreAddress(store.getAddress());
        dto.setStoreIntro(store.getStoreIntro());
        dto.setStorePhoto(store.getPhoto());
        dto.setIsOpen(store.getIsOpen());
        dto.setScore(store.getScore());

        // 設置類別
        if (store.getCategories() != null) {
            dto.setCategories(store.getCategories().stream()
                    .map(category -> category.getName())
                    .toList());
        }

        return dto;
    }

    /**
     * 設置桌位資訊
     */
    public void setTableInfo(List<TableBean> tables) {
        this.tables = tables.stream()
                .map(table -> {
                    TableInfo info = new TableInfo();
                    info.setTableId(table.getId());
                    info.setSeats(table.getSeats());
                    info.setQuantity(table.getQuantity());
                    info.setIsAvailable(table.getStatus());
                    return info;
                })
                .toList();
    }
}