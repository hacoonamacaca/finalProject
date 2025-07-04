package tw.com.ispan.eeit.service.store;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tw.com.ispan.eeit.repository.store.OpenHourRepository;

@Service
@Transactional
public class OpenHourService {
	@Autowired
	OpenHourRepository openHourRepository;
}
