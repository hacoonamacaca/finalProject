package tw.com.ispan.eeit.model.entity.food;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "tag")
public class TagBean {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 50)
    private String name;

    @ManyToMany(mappedBy = "tags",fetch = FetchType.LAZY)
    private List<FoodBean> foods;

    @OneToMany(mappedBy = "tag",fetch = FetchType.LAZY)
    private List<UserTagBean> userTags;
}