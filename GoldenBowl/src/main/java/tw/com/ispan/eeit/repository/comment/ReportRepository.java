package tw.com.ispan.eeit.repository.comment;

import org.springframework.data.jpa.repository.JpaRepository;

import tw.com.ispan.eeit.model.entity.comment.ReportBean;

public interface ReportRepository extends JpaRepository<ReportBean, Integer> {

}
