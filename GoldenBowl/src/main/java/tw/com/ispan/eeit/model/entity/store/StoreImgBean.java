package tw.com.ispan.eeit.model.entity.store;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "store_img")
@Data
@NoArgsConstructor
public class StoreImgBean {
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "store_id") // SQL名稱store_img表單的名稱
	private StoreBean store;

	@Column(name = "resource")
	private String resource;

	@Column(name = "display_sort")
	private Integer sort;

	@Column(name = "alt_text")
	private String altText;
}
