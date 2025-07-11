package tw.com.ispan.eeit.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import tw.com.ispan.eeit.model.dto.UserDTO;
import tw.com.ispan.eeit.model.entity.UserBean;
import tw.com.ispan.eeit.repository.emailVerify.UserTokenRepository;
import tw.com.ispan.eeit.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private UserTokenRepository userTokenRepository;
    
    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserBean> users = userService.getAllUsers();
        List<UserDTO> dtos = users.stream()
                                  .map(UserService::toDTO)
                                  .toList();
        return ResponseEntity.ok(dtos);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Integer id) {
        Optional<UserBean> user = userService.getUserById(id);
        return user.map(u -> ResponseEntity.ok(UserService.toDTO(u)))
                   .orElseGet(() -> ResponseEntity.notFound().build());
    }
    
    @GetMapping("/profile")
    public ResponseEntity<UserDTO> getProfileByEmail(@RequestParam String email) {
        Optional<UserBean> user = userService.getUserByEmail(email);
        return user.map(u -> ResponseEntity.ok(UserService.toDTO(u)))
                   .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody UserDTO dto) {
        if(dto.getEmail() == null || dto.getEmail().isBlank()){
            return ResponseEntity.badRequest().body("Email必填");
        }

        // 檢查這個 email 是否通過 email 驗證
        boolean verified = userTokenRepository.findTopByEmailAndUsedTrueOrderByIdDesc(dto.getEmail().trim()).isPresent();
        if (!verified) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("請先完成 Email 驗證再註冊！");
        }

        try {
            UserBean entity = UserService.toEntity(dto);
            UserBean createdUser = userService.createUser(entity);
            return ResponseEntity.status(HttpStatus.CREATED).body(UserService.toDTO(createdUser));
        } catch (IllegalStateException e) {
            // email 重複
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email 已註冊");
        } catch (IllegalArgumentException e) {
            // email 為空
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("新增會員失敗");
        }
    }

    
    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Integer id, @RequestBody UserDTO dto) {
        UserDTO updatedUser = userService.updateUser(id, dto);
        if (updatedUser != null) {
            return ResponseEntity.ok(updatedUser);
        }
        return ResponseEntity.notFound().build();
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteUser(@PathVariable Integer id) {
        if (userService.deleteUser(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
    
    @PostMapping("/check-email-exists")
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
                "userId", user.getId(),
                "userFullName", user.getName(),
                "userEmail", user.getEmail()
            );
        } else {
            return Map.of("success", false, "message", "帳號或密碼錯誤");
        }
    }
}
