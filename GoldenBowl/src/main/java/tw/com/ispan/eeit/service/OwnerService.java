package tw.com.ispan.eeit.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tw.com.ispan.eeit.model.entity.OwnerBean;
import tw.com.ispan.eeit.repository.OwnerRepository;

@Service
public class OwnerService {
	@Autowired
	private OwnerRepository ownerRepository;
	
	public boolean checkEmailExists(String email) {
		return ownerRepository.existsByEmail(email);
	}
	
	public OwnerBean findByEmailAndPassword(String email, String password) {
		return ownerRepository.findByEmailAndPassword(email, password);
	}
	
	public OwnerBean register(String email, String password, String name, String phone) {
        if (ownerRepository.existsByEmail(email)) return null;
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
