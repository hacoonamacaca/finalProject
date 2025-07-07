package tw.com.ispan.eeit.repository.store;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import tw.com.ispan.eeit.model.entity.store.CategoryBean;

public interface CategoryRepository extends JpaRepository<CategoryBean, Integer> {
	List<CategoryBean> findByName(String name);
}