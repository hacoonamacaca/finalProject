package tw.com.ispan.eeit.model.dto.reservation;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TimeSlotSimpleDTO {
    private Integer id;
    private Integer storeId;
    private String storeName;
    private LocalDate day;
    private LocalTime startTime;
    private LocalTime endTime;
    private Boolean isActive;
}