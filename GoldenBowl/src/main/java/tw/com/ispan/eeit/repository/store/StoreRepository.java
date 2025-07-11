package tw.com.ispan.eeit.repository.store;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import tw.com.ispan.eeit.model.entity.store.StoreBean;

@Repository
public interface StoreRepository extends JpaRepository<StoreBean, Integer> {

        @EntityGraph(attributePaths = { "categories", "comments", "foods" })
        Optional<StoreBean> findById(Integer id);

        @EntityGraph(attributePaths = { "categories", "foods", "foods.tags" })
        List<StoreBean> findAll();

        // 複雜查詢示例：模糊搜尋商店名稱、地址、分類名稱、食物名稱、食物標籤名稱
        // 注意：實際查詢會根據你的資料模型和關聯關係來調整。
        // 使用 JOIN FETCH 確保一次性載入所有相關聯的集合，避免 N+1 問題和 LazyInitializationException。
        @Query("SELECT s FROM StoreBean s " + // 移除 DISTINCT
                        "LEFT JOIN FETCH s.categories c " +
                        "LEFT JOIN FETCH s.foods f " +
                        "LEFT JOIN FETCH f.tags t " +
                        "WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
                        "OR LOWER(s.address) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
                        "OR LOWER(c.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
                        "OR LOWER(f.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
                        "OR LOWER(t.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
        List<StoreBean> findStoresBySearchTerm(@Param("searchTerm") String searchTerm);

        // 如果你的 getAllStores() 也要載入所有細節，也可以定義類似的 @Query
        @Query("SELECT DISTINCT s FROM StoreBean s " +
                        "LEFT JOIN FETCH s.categories c " +
                        "LEFT JOIN FETCH s.foods f " +
                        "LEFT JOIN FETCH f.tags t")
        List<StoreBean> findAllWithDetails(); // 建議你這樣修改 getAllStores，以確保資料一次載入

        @Query("SELECT s FROM StoreBean s LEFT JOIN FETCH s.comments WHERE s.id = :id")
        Optional<StoreBean> findByIdWithComments(@Param("id") Integer id);

}
