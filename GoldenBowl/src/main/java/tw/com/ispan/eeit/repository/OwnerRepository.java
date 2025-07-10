package tw.com.ispan.eeit.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import tw.com.ispan.eeit.model.entity.OwnerBean;

public interface OwnerRepository extends JpaRepository<OwnerBean, Integer>{
	boolean existsByEmail(String email);
	OwnerBean findByEmailAndPassword(String email, String password);
}

