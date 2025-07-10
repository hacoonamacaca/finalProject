package tw.com.ispan.eeit.controller.emailVerify;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import tw.com.ispan.eeit.model.entity.UserBean;
import tw.com.ispan.eeit.repository.UserRepository;

@RestController
@RequestMapping("/api")
public class EmailVerifyController {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private UserRepository userRepository;

    // 暫存 token-email 對應
    private Map<String, String> tokenToEmail = new ConcurrentHashMap<>();
    // 已驗證通過 email (簡單處理，正式應用建議用 Redis 或資料表且設過期)
    private Set<String> verifiedEmailSet = Collections.newSetFromMap(new ConcurrentHashMap<>());

    // 1. 寄送驗證信
    @PostMapping("/send-verify-email")
    public ResponseEntity<String> sendVerifyEmail(@RequestParam String email) {
        if (userRepository.findByEmail(email.trim()).isPresent()) {
            return ResponseEntity.badRequest().body("此 email 已被註冊，請直接登入！");
        }
        String token = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        tokenToEmail.put(token, email.trim());

        String verifyUrl = "http://localhost:5173/?token=" + token + "&email=" + email.trim();

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("eattiy1986@gmail.com");
        message.setTo(email);
        message.setSubject("Email 註冊驗證信");
        message.setText("您好！請點擊以下連結完成驗證：\n" + verifyUrl);

        mailSender.send(message);

        return ResponseEntity.ok("驗證信已寄出到 " + email + "，請查收！");
    }

    // 2. 驗證連結 (前端收到驗證成功後再跳註冊)
    @GetMapping("/verify-email")
    public ResponseEntity<String> verifyEmail(@RequestParam String token, @RequestParam String email) {
        // 假設 token 已經有對應 user id（你也可查 email）
        Optional<UserBean> userOpt = userRepository.findByEmail(email.trim());
        if (userOpt.isPresent()) {
            UserBean user = userOpt.get();
            user.setIsVerify(true);
            userRepository.save(user);
            return ResponseEntity.ok("驗證成功！");
        } else {
            return ResponseEntity.badRequest().body("查無使用者，請先註冊！");
        }
    }

 // 3. 註冊 API
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestParam String email, @RequestParam String password, @RequestParam String name) {
        if (!verifiedEmailSet.contains(email.trim())) {
            return ResponseEntity.badRequest().body("請先完成 Email 驗證再註冊！");
        }
        if (userRepository.findByEmail(email.trim()).isPresent()) {
            return ResponseEntity.badRequest().body("此 email 已被註冊！");
        }
        UserBean newUser = new UserBean();
        newUser.setEmail(email.trim());
        newUser.setPassword(password); // 記得加密
        newUser.setName(name);
        newUser.setIsVerify(true);
        userRepository.save(newUser);

        verifiedEmailSet.remove(email.trim());
        return ResponseEntity.ok("註冊成功！");
    }

    // 4. 查詢 email 是否已驗證（前端可用來控制註冊按鈕啟用）
    @GetMapping("/check-email-verified")
    public ResponseEntity<Boolean> checkEmailVerified(@RequestParam String email) {
        boolean verified = verifiedEmailSet.contains(email.trim());
        return ResponseEntity.ok(verified);
    }
}