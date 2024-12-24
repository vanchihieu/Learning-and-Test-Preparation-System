package com.backend.spring.security;

import com.backend.spring.security.jwt.AuthEntryPointJwt;
import com.backend.spring.security.jwt.AuthTokenFilter;

import com.backend.spring.security.service.UserDetailsServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig { // extends WebSecurityConfigurerAdapter {

    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @Autowired
    private AuthEntryPointJwt unauthorizedHandler;

//  Cấu hình xác thực và phân quyền cho các tài nguyên của ứng dụng

    //Tạo bộ lọc Auth
    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }

    //Tạo ra một đối tượng xử lý xác thực bằng CSDL
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    //Mã hóa mật khẩu người dùng
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    //Quy tắc phần quyền dựa trên URL của tài nguyên.
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())

                .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth ->
//                      Ràng buộc chung các URL có thể truy cập được (ràng buộc cụ thể bên Controller)
                        auth.requestMatchers("/api/auth/**").permitAll()
                                .requestMatchers("/api/gpt/**").permitAll()
                                .requestMatchers("/api/test/**").permitAll()
                                .requestMatchers("/api/topic/**").permitAll()
                                .requestMatchers("/api/vocabulary/**").permitAll()
                                .requestMatchers("/api/vocabulary-question/**").permitAll()
                                .requestMatchers("/api/grammar/**").permitAll()
                                .requestMatchers("/api/grammar-content/**").permitAll()
                                .requestMatchers("/api/grammar-question/**").permitAll()
                                .requestMatchers("/api/section/**").permitAll()
                                .requestMatchers("/api/lesson/**").permitAll()
                                .requestMatchers("/api/lesson-content/**").permitAll()
                                .requestMatchers("/api/question-group/**").permitAll()
                                .requestMatchers("/api/question/**").permitAll()
                                .requestMatchers("/api/exam/**").permitAll()
                                .requestMatchers("/api/exam-question/**").permitAll()
                                .requestMatchers("/api/user-exam/**").permitAll()
                                .requestMatchers("/api/user-goal/**").permitAll()
                                .requestMatchers("/api/user-vocabulary/**").permitAll()
                                .requestMatchers("/api/user-exam-questions/**").permitAll()
                                .requestMatchers("/api/feedback/**").permitAll()
                                .requestMatchers("/api/comments/**").permitAll()
                                .requestMatchers("/api/images/**").permitAll()
                                .requestMatchers("/api/free-material/**").permitAll()
                                .requestMatchers("/api/profile-image/**").permitAll()
                                .requestMatchers("/api/score-table/**").permitAll()
                                .requestMatchers("/api/note/**").permitAll()

                                .requestMatchers("/ws/**").permitAll()

//                               Cho phép truy cập vào file tĩnh
                                .requestMatchers("/images/**").permitAll()
                                .requestMatchers("/audios/**").permitAll()
                                .requestMatchers("/pdfs/**").permitAll()
                                .anyRequest().authenticated()
                );
        http.authenticationProvider(authenticationProvider());
        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

}
