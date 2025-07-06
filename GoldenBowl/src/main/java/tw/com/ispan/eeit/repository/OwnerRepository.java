package tw.com.ispan.eeit.repository;

import tw.com.ispan.eeit.model.entity.OwnerBean;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OwnerRepository extends JpaRepository<OwnerBean, Integer>{
	boolean existsByEmail(String email);
	OwnerBean findByEmailAndPassword(String email, String password);
}
