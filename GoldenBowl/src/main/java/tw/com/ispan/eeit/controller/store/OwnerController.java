package tw.com.ispan.eeit.controller.store;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import tw.com.ispan.eeit.model.entity.OwnerBean;
import tw.com.ispan.eeit.service.store.OwnerService;

@RestController
@RequestMapping("/api/owner")
public class OwnerController {
    @Autowired
    private OwnerService ownerService;

    @PostMapping("/check-email")
    public Map<String, Object> checkEmail(@RequestBody Map<String, String> map) {
        String email = map.get("email");
        boolean exists = ownerService.checkEmailExists(email);
        return Map.of("exists", exists);
    }

    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody Map<String, String> map) {
        String email = map.get("email");
        String pwd = map.get("password");
        String name = map.get("name");
        String phone = map.get("phone");
        OwnerBean owner = ownerService.register(email, pwd, name, phone);
        if (owner == null) {
            return Map.of("success", false, "message", "Email 已註冊");
        }
        return Map.of("success", true, "ownerId", owner.getId());
    }

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> map) {
        String email = map.get("email");
        String pwd = map.get("password");
        OwnerBean owner = ownerService.findByEmailAndPassword(email, pwd);
        if (owner == null) {
            return Map.of("success", false, "message", "帳號或密碼錯誤");
        }
        // 把所需欄位全部回傳
        return Map.of(
                "success", true,
                "ownerId", owner.getId(),
                "name", owner.getName(),
                "email", owner.getEmail(),
                "phone", owner.getPhone());
    }
}
