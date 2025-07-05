package tw.com.ispan.eeit.service.order;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tw.com.ispan.eeit.repository.order.OrderDetailSpecRepository;

@Service
@Transactional
public class OrderSpecService {

	@Autowired
	OrderDetailSpecRepository orderDetailSpecRepository;
}
