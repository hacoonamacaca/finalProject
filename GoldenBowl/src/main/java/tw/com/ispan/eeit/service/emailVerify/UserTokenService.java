package tw.com.ispan.eeit.service.emailVerify;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tw.com.ispan.eeit.model.entity.emailVerify.UserTokenBean;
import tw.com.ispan.eeit.repository.emailVerify.UserTokenRepository;

@Service
public class UserTokenService {

    @Autowired
    private UserTokenRepository userTokenRepository;

    public UserTokenBean createToken(Integer userid, String email) {
        String verifyCode = UUID.randomUUID().toString();
        LocalDateTime expiration = LocalDateTime.now().plusMinutes(30);

        UserTokenBean token = new UserTokenBean();
        token.setUserId(userid);
        token.setEmail(email);
        token.setVerifyCode(verifyCode);
        token.setExpiration(expiration);
        token.setUsed(false);
        
        return userTokenRepository.save(token);

    }

    // 查詢未用過且未過期的token
    public Optional<UserTokenBean> findValidToken(String token) {
        return userTokenRepository.findByVerifyCodeAndUsedFalse(token)
                .filter(t -> t.getExpiration().isAfter(LocalDateTime.now()));
    }

    public void setTokenUsed(UserTokenBean token) {
        token.setUsed(true);
        userTokenRepository.save(token);
    }
    
}
