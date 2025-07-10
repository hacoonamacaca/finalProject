package tw.com.ispan.eeit.model.entity.reservation;

import java.util.HashSet;
import java.util.Set;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Entity
@Table(name = "tables")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TableBean {
    @ManyToMany(mappedBy = "tables")
    private Set<ReservationBean> reservations = new HashSet<>();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NonNull
    @Column(name = "FK_store_id")
    private Integer storeId;

    @Column(name = "quantity")
    private Integer quantity; // 桌位數量

    @Column(name = "seats")
    private Integer seats; // 座位數量

    @Column(name = "isActive")
    private Boolean status; // 桌位狀態
}
