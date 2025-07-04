package tw.com.ispan.eeit.model.entity.food;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import tw.com.ispan.eeit.model.entity.store.StoreBean;

@Data
@Entity
@Table(name = "food_class")
@NoArgsConstructor
public class FoodClassBean {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "store_id")
    private StoreBean store;

    @Column(length = 100, nullable = false)
    private String name;

    @Column(length = 100)
    private String description;

    private Integer sort;

    @ManyToMany(mappedBy = "foodClasses")
    private List<FoodBean> foods;
}