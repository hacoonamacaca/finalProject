package tw.com.ispan.eeit.model.dto.store;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import tw.com.ispan.eeit.model.entity.store.CategoryBean;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDTO {
    private Integer id;
    private String name;
    // 不包含 stores 屬性
    
    public static CategoryDTO fromCategoryBean(CategoryBean categoryBean) {
        if (categoryBean == null) {
            return null;
        }
        CategoryDTO dto = new CategoryDTO();
        dto.setId(categoryBean.getId());
        dto.setName(categoryBean.getName());
        // 注意：这里我们明确不包含 stores 属性，与 CategoryDTO 的设计一致
        return dto;
    }
}