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
import org.springframework.web.bind.annotation.RestController;

import tw.com.ispan.eeit.model.entity.UserBean;
import tw.com.ispan.eeit.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<List<UserBean>> getAllUsers() {
        List<UserBean> users = userService.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserBean> getUserById(@PathVariable Integer id) {
        Optional<UserBean> user = userService.getUserById(id);
        return user.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<UserBean> createUser(@RequestBody UserBean user) {
        UserBean createdUser = userService.createUser(user);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserBean> updateUser(@PathVariable Integer id, @RequestBody UserBean userDetails) {
        UserBean updatedUser = userService.updateUser(id, userDetails);
        if (updatedUser != null) {
            return new ResponseEntity<>(updatedUser, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteUser(@PathVariable Integer id) {
        if (userService.deleteUser(id)) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

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
                    "userEmail", user.getEmail(),
                    "userId", user.getId() // <--- 新增這一行
            );
        } else {
            return Map.of("success", false, "message", "帳號或密碼錯誤");
        }
    }
}
