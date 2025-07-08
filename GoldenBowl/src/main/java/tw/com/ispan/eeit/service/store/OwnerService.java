package tw.com.ispan.eeit.service.store;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tw.com.ispan.eeit.model.entity.OwnerBean;
import tw.com.ispan.eeit.repository.store.OwnerRepository;

@Service
public class OwnerService {
	@Autowired
	private OwnerRepository ownerRepository;

	public boolean checkEmailExists(String email) {
		return ownerRepository.existsByEmail(email);
	}

	public OwnerBean findByEmailAndPassword(String email, String password) {
		OwnerBean owner = ownerRepository.findByEmailAndPassword(email, password);
		if (owner != null) {
			owner.setLastLogin(LocalDateTime.now());
			ownerRepository.save(owner);
		}
		return owner;
	}

	public OwnerBean register(String email, String password, String name, String phone) {
		if (ownerRepository.existsByEmail(email))
			return null;
		OwnerBean owner = new OwnerBean();
		owner.setEmail(email);
		owner.setPassword(password);
		owner.setName(name);
		owner.setPhone(phone);
		owner.setSignupDate(LocalDateTime.now());
		owner.setIsActive(true);
		owner.setIsVerify(false);
		return ownerRepository.save(owner);
	}
}
