package tw.com.ispan.eeit.repository.Plan;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tw.com.ispan.eeit.model.entity.promotion.SubRecordBean;

@Repository
public interface SubRecordRepository extends JpaRepository<SubRecordBean, Integer>{

}
