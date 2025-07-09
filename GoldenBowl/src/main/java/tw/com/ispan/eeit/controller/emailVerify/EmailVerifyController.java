//package tw.com.ispan.eeit.controller.emailVerify;
//
//import java.util.Map;
//import java.util.Set;
//import java.util.UUID;
//import java.util.concurrent.ConcurrentHashMap;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.mail.SimpleMailMessage;
//import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequestMapping("/api")
//public class EmailVerifyController {
//
//    @Autowired
//    private JavaMailSender mailSender;
//
//    private Map<String, String> tokenToEmail = new ConcurrentHashMap<>();
//    private Set<String> verifiedEmails = ConcurrentHashMap.newKeySet();
//
//    @PostMapping("/send-verify-email")
//    public ResponseEntity<String> sendVerifyEmail(@RequestParam String email) {
//        System.out.println("收到前端 email 參數: " + email);
//        try {
//            System.out.println("準備寄信到: " + email);
//            String token = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
//            // 暫存token對應email
//            tokenToEmail.put(token, email);
//
//            String verifyUrl = "http://localhost:5173/profile?token=" + token;
//            SimpleMailMessage message = new SimpleMailMessage();
//            message.setFrom("eattiy1986@gmail.com");
//            message.setTo(email); // ← 這裡收件人從前端傳來
//            message.setSubject("測試驗證信");
//            message.setText("您好！請點擊以下連結完成驗證：\n" + verifyUrl);
//            System.out.println("send前");
//
//            mailSender.send(message);
//
//            System.out.println("send後，寄信完成");
//            return ResponseEntity.ok("驗證信已寄出到 " + email + "，請查收！");
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            return ResponseEntity.status(500).body("寄信失敗！錯誤：" + ex.getMessage());
//        }
//    }
//
//    @GetMapping("/verify-email")
//    public ResponseEntity<?> verifyEmail(@RequestParam String token) {
//        String email = tokenToEmail.get(token);
//        if (email != null) {
//            verifiedEmails.add(email);
//            tokenToEmail.remove(token);
//            return ResponseEntity.ok().build();
//        } else {
//            return ResponseEntity.status(400).body("驗證失敗或連結已過期");
//        }
//    }
//
//    public ResponseEntity<Boolean> checkEmailVerified(@RequestParam String email) {
//        return ResponseEntity.ok(verifiedEmails.contains(email));
//    }
//}