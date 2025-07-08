package tw.com.ispan.eeit.controller.emailVerify;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class EmailVerifyController {

    // 暫時註解掉郵件發送器，避免啟動錯誤
    // @Autowired
    // private JavaMailSender mailSender;

    @PostMapping("/send-verify-email")
    public ResponseEntity<String> sendVerifyEmail(@RequestParam String email) {
        System.out.println("收到前端 email 參數: " + email);
        try {
            System.out.println("準備寄信到: " + email);
            String token = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
            String verifyUrl = "http://localhost:5173/verify?token=" + token;

            // 暫時禁用實際郵件發送，改為模擬回應
            System.out.println("模擬寄信完成，驗證連結: " + verifyUrl);

            // 實際郵件發送代碼（暫時註解）
            /*
             * SimpleMailMessage message = new SimpleMailMessage();
             * message.setFrom("eattiy1986@gmail.com");
             * message.setTo(email);
             * message.setSubject("測試驗證信");
             * message.setText("您好！請點擊以下連結完成驗證：\n" + verifyUrl);
             * mailSender.send(message);
             */

            return ResponseEntity.ok("驗證信已寄出到 " + email + "，請查收！(模擬模式)");
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(500).body("寄信失敗！錯誤：" + ex.getMessage());
        }
    }
}