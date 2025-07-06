package tw.com.ispan.eeit.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

//測試postman-開放 API（不用登入）

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // ✅ 關掉 CSRF
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll() // ✅ 所有 API 放行
            );
        return http.build();
    }
}

