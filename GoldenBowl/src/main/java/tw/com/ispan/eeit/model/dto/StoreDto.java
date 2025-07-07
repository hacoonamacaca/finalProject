package tw.com.ispan.eeit.model.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class StoreDto {
    private Integer id;
    private Integer ownerId;
    private String name;
    private String address;
    private Double lng;
    private Double lat;
    private String storeIntro;
    private String photo;
    private Boolean isOpen;
    private Float score;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
    private Boolean isActive;

    // 若你要給前端顯示空間點座標，可以自行決定怎麼包
    private String storeCoords; // 通常轉 WKT 字串或 GeoJSON 字串。要怎麼顯示決定於你的前端設計
}
