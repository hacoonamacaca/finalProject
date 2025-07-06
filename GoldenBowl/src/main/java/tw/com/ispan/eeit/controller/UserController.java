package tw.com.ispan.eeit.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import tw.com.ispan.eeit.model.entity.UserBean;
import tw.com.ispan.eeit.service.UserService;

@RestController
@RequestMapping("/api")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/check-email-exists") // 這樣路徑才是 /api/check-email-exists
    public Map<String, Boolean> checkEmailExists(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        boolean exists = userService.checkEmailExists(email);
        return Map.of("exists", exists);
    }
    
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String password = body.get("password");
        UserBean user = userService.findByEmailAndPassword(email, password);
        if (user != null) {
            return Map.of(
                "success", true,
                "userFullName", user.getName(),
                "userEmail", user.getEmail()
            );
        } else {
            return Map.of("success", false, "message", "帳號或密碼錯誤");
        }
    }
}

