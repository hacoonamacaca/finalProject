package tw.com.ispan.eeit.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tw.com.ispan.eeit.model.entity.UserBean;
import tw.com.ispan.eeit.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public List<UserBean> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<UserBean> getUserById(Integer id) {
        return userRepository.findById(id);
    }

    public UserBean createUser(UserBean user) {
        // 設定創建日期和最後登入日期
        user.setSignupDate(LocalDateTime.now());
        user.setLastLogin(LocalDateTime.now());
        // 預設 active 和 verify 狀態
        if (user.getIsActive() == null) {
            user.setIsActive(true);
        }
        if (user.getIsVerify() == null) {
            user.setIsVerify(false);
        }
        return userRepository.save(user);
    }

    public UserBean updateUser(Integer id, UserBean userDetails) {
        Optional<UserBean> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            UserBean existingUser = optionalUser.get();
            existingUser.setEmail(userDetails.getEmail());
            existingUser.setPassword(userDetails.getPassword());
            existingUser.setName(userDetails.getName());
            existingUser.setPhone(userDetails.getPhone());
            // signupDate, lastLogin, isActive, isVerify, hideUntil 這些欄位可能根據業務需求進行更新
            // 例如，lastLogin 通常在登入時更新，而不是在 CRUD update 時更新
            // existingUser.setLastLogin(LocalDateTime.now());
            existingUser.setIsActive(userDetails.getIsActive());
            existingUser.setIsVerify(userDetails.getIsVerify());
            existingUser.setHideUntil(userDetails.getHideUntil());
            // 處理多對多關係，請注意這需要更複雜的邏輯來更新
            // existingUser.setFavoriteStores(userDetails.getFavoriteStores());
            // existingUser.setFavoriteFoods(userDetails.getFavoriteFoods());
            return userRepository.save(existingUser);
        }
        return null; // 或者拋出一個自定義異常
    }

    public boolean deleteUser(Integer id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }
    
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