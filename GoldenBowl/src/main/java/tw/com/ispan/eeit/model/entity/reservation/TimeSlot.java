package tw.com.ispan.eeit.model.entity.reservation;

import jakarta.persistence.*;
import lombok.*;
import tw.com.ispan.eeit.model.entity.store.StoreBean;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "time_slots")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class TimeSlot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "store_id", nullable = false)
    @JsonIgnoreProperties({ "owner", "stores", "timeSlots", "tables", "reservations" })
    private StoreBean store;

    @Column(name = "day", columnDefinition = "date")
    private LocalDate day;

    @Column(name = "start_time", columnDefinition = "time(0)")
    private LocalTime startTime;

    @Column(name = "end_time", columnDefinition = "time(0)")
    private LocalTime endTime;

    @Column(name = "is_active")
    private Boolean isActive;
}