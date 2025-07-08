package tw.com.ispan.eeit.model.dto.store;

import jakarta.persistence.Column;
import lombok.Data;


@Data
public class SpecDto {

	private Integer id;
    private String name;
    private Integer price;
    private Integer sort;
}
