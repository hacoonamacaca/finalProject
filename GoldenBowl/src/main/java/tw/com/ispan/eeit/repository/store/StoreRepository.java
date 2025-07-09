package tw.com.ispan.eeit.repository.store;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import tw.com.ispan.eeit.model.entity.store.StoreBean;

@Repository
public interface StoreRepository extends JpaRepository<StoreBean, Integer> {

    @EntityGraph(attributePaths = { "categories", "comments", "foods" }) // 加載 Store 的直接關聯
    List<StoreBean> findAll();

    @EntityGraph(attributePaths = { "categories", "comments", "foods" })
    Optional<StoreBean> findById(Integer id);
}
