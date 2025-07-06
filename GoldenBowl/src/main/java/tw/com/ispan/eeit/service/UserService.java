package tw.com.ispan.eeit.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tw.com.ispan.eeit.model.entity.UserBean;
import tw.com.ispan.eeit.repository.UserRepository;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public boolean checkEmailExists(String email) {
        System.out.println("查詢用 email: [" + email + "]");
        return userRepository.existsByEmail(email.trim());
    }

    public UserBean findByEmailAndPassword(String email, String password) {
        Optional<UserBean> optional = userRepository.findByEmail(email);
        if (optional.isPresent()) {
            UserBean user = optional.get();
            if (user.getPassword().equals(password)) {
                return user;
            }
        }
        return null;
    }
}
