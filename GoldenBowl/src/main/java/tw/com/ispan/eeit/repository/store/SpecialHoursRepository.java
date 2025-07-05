package tw.com.ispan.eeit.repository.store;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import tw.com.ispan.eeit.model.entity.store.SpecialHoursBean;


@Repository
public interface SpecialHoursRepository extends JpaRepository<SpecialHoursBean, Integer> {

}
