package tw.com.ispan.eeit.controller.emailVerify;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
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

    // 1. 寄送驗證信（只要未註冊就可寄）
    @PostMapping("/send-verify-email")
    public ResponseEntity<String> sendVerifyEmail(@RequestParam String email) {
        System.out.println("[DEBUG] 收到前端 email 參數: " + email);

        // 檢查 email 是否已註冊
        if (userRepository.findByEmail(email.trim()).isPresent()) {
            System.out.println("[WARN] 此 email 已被註冊: " + email);
            return ResponseEntity.badRequest().body("此 email 已被註冊，請直接登入！");
        }

        try {
            String token = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
            tokenToEmail.put(token, email.trim());
            System.out.println("[DEBUG] 產生驗證 token: " + token + " 對應 email: " + email);

            String verifyUrl = "http://localhost:5173/?token=" + token + "&email=" + email.trim();
            System.out.println("[INFO] 驗證信連結: " + verifyUrl);

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("eattiy1986@gmail.com");
            message.setTo(email);
            message.setSubject("Email 註冊驗證信");
            message.setText("您好！請點擊以下連結完成驗證：\n" + verifyUrl);

            // debug 信件內容
            System.out.println("[DEBUG] 預備寄送信件: ");
            System.out.println("From: " + message.getFrom());
            System.out.println("To: " + Arrays.toString(message.getTo()));
            System.out.println("Subject: " + message.getSubject());
            System.out.println("Text: " + message.getText());

            mailSender.send(message);
            System.out.println("[INFO] 驗證信已寄出到: " + email);

            return ResponseEntity.ok("驗證信已寄出到 " + email + "，請查收！");
        } catch (Exception ex) {
            System.out.println("[ERROR] 寄送驗證信失敗: " + ex.getMessage());
            ex.printStackTrace();
            return ResponseEntity.status(500).body("寄信失敗！錯誤：" + ex.getMessage());
        }
    }

    // 2. 驗證連結 (前端收到驗證成功後再跳註冊)
    @GetMapping("/verify-email")
    public ResponseEntity<String> verifyEmail(@RequestParam String token, @RequestParam String email) {
        String tmp = tokenToEmail.get(token);
        if (tmp != null && tmp.equals(email.trim())) {
            verifiedEmailSet.add(email.trim());
            tokenToEmail.remove(token);
            System.out.println("[INFO] 驗證通過: " + email);
            return ResponseEntity.ok("驗證成功，請繼續註冊");
        } else {
            System.out.println("[WARN] 驗證失敗或連結已過期: token=" + token + ", email=" + email);
            return ResponseEntity.status(400).body("驗證失敗或連結已過期");
        }
    }

    // 3. 註冊 API (要二次檢查 email 是否已驗證)
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestParam String email, @RequestParam String password, @RequestParam String name) {
        if (!verifiedEmailSet.contains(email.trim())) {
            System.out.println("[WARN] 註冊未通過驗證: " + email);
            return ResponseEntity.badRequest().body("請先完成 Email 驗證再註冊！");
        }
        if (userRepository.findByEmail(email.trim()).isPresent()) {
            System.out.println("[WARN] 此 email 已被註冊: " + email);
            return ResponseEntity.badRequest().body("此 email 已被註冊！");
        }
        // 這裡補齊你的 UserBean 建立方式
        UserBean newUser = new UserBean();
        newUser.setEmail(email.trim());
        newUser.setPassword(password); // 記得加密
        newUser.setName(name);
        newUser.setIsVerify(true);
        userRepository.save(newUser);

        verifiedEmailSet.remove(email.trim());
        System.out.println("[INFO] 註冊完成: " + email);
        return ResponseEntity.ok("註冊成功！");
    }

    // 4. 查詢 email 是否已驗證（前端可用來控制註冊按鈕啟用）
    @GetMapping("/check-email-verified")
    public ResponseEntity<Boolean> checkEmailVerified(@RequestParam String email) {
        boolean verified = verifiedEmailSet.contains(email.trim());
        System.out.println("[DEBUG] 查詢 email 是否已驗證: " + email + " ? " + verified);
        return ResponseEntity.ok(verified);
    }
}