package tw.com.ispan.eeit.repository.reservation;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import tw.com.ispan.eeit.model.entity.reservation.TableBean;

public interface TableRepository extends JpaRepository<TableBean, Integer> {
    List<TableBean> findByStoreId(Integer storeId);
}
