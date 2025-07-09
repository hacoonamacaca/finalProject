package tw.com.ispan.eeit.model.dto.store;


import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor   // 無參數建構子
@AllArgsConstructor  // 有所有欄位的建構子
public class CategoryDTO {
    private Integer id;
    private String name;
    // 不包含	store屬性
}