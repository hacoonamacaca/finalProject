package tw.com.ispan.eeit.model.dto.store;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import tw.com.ispan.eeit.model.entity.store.SpecBean; // 引入 SpecBean

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SpecDTO {
    private Integer id;
    private String name;
    private Integer price;
    private Integer sort;
    private Boolean isActive;
    // 注意：这里没有包含 SpecGroupBean 和 OrderDetailBean 的关联属性
    // 如果需要 SpecGroup 的部分信息（例如 SpecGroup 的 ID 或名称），
    // 可以在 DTO 中添加相应的字段，并在转换时手动设置。

    /**
     * 静态方法，将 SpecBean 转换为 SpecDTO。
     * @param specBean 要转换的 SpecBean 对象。
     * @return 转换后的 SpecDTO 对象。
     */
    public static SpecDTO fromSpecBean(SpecBean specBean) {
        if (specBean == null) {
            return null;
        }
        SpecDTO dto = new SpecDTO();
        dto.setId(specBean.getId());
        dto.setName(specBean.getName());
        dto.setPrice(specBean.getPrice());
        dto.setSort(specBean.getSort());
        dto.setIsActive(specBean.getIsActive());
        // 如果需要 SpecGroup 的 ID，可以这样添加：
        // if (specBean.getSpecGroup() != null) {
        //     dto.setSpecGroupId(specBean.getSpecGroup().getId());
        // }
        return dto;
    }
}