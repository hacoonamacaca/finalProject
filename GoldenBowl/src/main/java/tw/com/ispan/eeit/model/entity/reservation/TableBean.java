package tw.com.ispan.eeit.model.entity.reservation;

import java.util.HashSet;
import java.util.Set;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import tw.com.ispan.eeit.model.entity.store.StoreBean;
import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "tables")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TableBean {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // 關聯到 Store
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    @JsonBackReference("store-tables")
    private StoreBean store;

    @Column(name = "quantity")
    private Integer quantity; // 桌位數量

    @Column(name = "seats")
    private Integer seats; // 座位數量

    @Column(name = "isActive")
    private Boolean status; // 桌位狀態

    @ManyToMany(mappedBy = "tables")
    @JsonBackReference("reservation-tables")
    private Set<ReservationBean> reservations = new HashSet<>();
}
