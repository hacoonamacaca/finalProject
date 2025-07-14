package tw.com.ispan.eeit.repository.comment;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import tw.com.ispan.eeit.model.entity.comment.ReportBean;

public interface ReportRepository extends JpaRepository<ReportBean, Integer> {
    @EntityGraph(attributePaths = { "reportType", "comment" })
    List<ReportBean> findAll();

    @EntityGraph(attributePaths = { "reportType", "comment" })
    Optional<ReportBean> findById(Integer id);

}
