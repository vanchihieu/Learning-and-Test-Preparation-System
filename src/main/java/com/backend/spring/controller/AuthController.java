package com.backend.spring.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

import com.backend.spring.entity.*;
import com.backend.spring.payload.request.*;
import com.backend.spring.service.EmailService;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.backend.spring.exception.TokenRefreshException;
import com.backend.spring.payload.response.JwtResponse;
import com.backend.spring.payload.response.MessageResponse;
import com.backend.spring.payload.response.TokenRefreshResponse;
import com.backend.spring.repository.RoleRepository;
import com.backend.spring.repository.UserRepository;
import com.backend.spring.security.jwt.JwtUtils;
import com.backend.spring.security.service.RefreshTokenService;
import com.backend.spring.security.service.UserDetailsImpl;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    RefreshTokenService refreshTokenService;

    @Autowired
    private EmailService emailService;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginDto loginDto) {
        try {
            Authentication authentication = authenticate(loginDto);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

            if (!isAccountActive(userDetails)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponse("Tài khoản chưa được kích hoạt hoặc đã bị khóa."));
            }

            return createAuthResponse(userDetails);
        } catch (BadCredentialsException e) {
            // Xử lý khi tên đăng nhập hoặc mật khẩu không đúng
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new MessageResponse("Tên đăng nhập hoặc mật khẩu không đúng."));
        }
    }

    private Authentication authenticate(LoginDto loginDto) {
        return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword()));
    }

    private boolean isAccountActive(UserDetailsImpl userDetails) {
        return userDetails.getIsActive() == 1 && userDetails.getStatus() == 1;
    }

    private ResponseEntity<?> createAuthResponse(UserDetailsImpl userDetails) {
        String jwt = jwtUtils.generateJwtToken(userDetails);
        String refreshToken = refreshTokenService.createRefreshToken(userDetails.getId()).getToken();
        List<String> roles = userDetails.getAuthorities().stream().map(item -> item.getAuthority()).collect(Collectors.toList());

        return ResponseEntity.ok(new JwtResponse(
                jwt, refreshToken, userDetails.getId(), userDetails.getUsername(), userDetails.getEmail(),
                userDetails.getAddress(), userDetails.getPhoneNumber(), userDetails.getGender(),
                userDetails.getStatus(), userDetails.getIsActive(), userDetails.getVerificationCode(),
                userDetails.getName(), roles, jwtUtils.getJwtExpirationMs(), refreshTokenService.getRefreshTokenDurationMs()
        ));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupDto signUpDto) {
        if (userRepository.existsByUsername(signUpDto.getUsername())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Username đã tồn tại!"));
        }
        if (userRepository.existsByEmail(signUpDto.getEmail())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Email đã được sử dụng!"));
        }
        if (userRepository.existsByPhoneNumber(signUpDto.getPhoneNumber())) {
            return ResponseEntity.badRequest().body(new MessageResponse("SĐT đã được sử dụng!"));
        }
        // Create new user's account
        User user = new User(
                signUpDto.getName(),
                signUpDto.getUsername(),
                signUpDto.getEmail(),
                encoder.encode(signUpDto.getPassword()),
                signUpDto.getAddress(),
                signUpDto.getPhoneNumber(),
                signUpDto.getGender()
        );
        // Tạo mã xác thực
        String verificationCode = UUID.randomUUID().toString();
        user.setVerificationCode(verificationCode);

        Set<String> strRoles = signUpDto.getRole();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {
            Role userRole = roleRepository.findByName(ERole.ROLE_LEARNER)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy vai trò"));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Không tìm thấy vai trò"));
                        roles.add(adminRole);

                        break;
                    default:
                        Role userRole = roleRepository.findByName(ERole.ROLE_LEARNER)
                                .orElseThrow(() -> new RuntimeException("Không tìm thấy vai trò"));
                        roles.add(userRole);
                }
            });
        }
        user.setRoles(roles);
        userRepository.save(user);

        // Gửi email xác thực
        String subject = "Xác thực tài khoản";
        // Đọc nội dung của file template
        String templateContent = loadVerificationEmailTemplate(verificationCode);
        // Gửi email sử dụng template
        emailService.sendEmail(signUpDto.getEmail(), subject, templateContent);

        return ResponseEntity.ok(new MessageResponse("Đăng kí người dùng dành công"));
    }

    private String loadVerificationEmailTemplate(String verificationCode) {
        try {
            Resource resource = new ClassPathResource("templates/verification.html");
            InputStream inputStream = resource.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }
            // Link xác thực
            return content.toString().replace("${url}", "http://localhost:9004/api/auth/activate-account?verificationCode=" + verificationCode);
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    @GetMapping("/activate-account")
    public ResponseEntity<?> activateAccount(@RequestParam("verificationCode") String verificationCode) {
        User user = userRepository.findByVerificationCode(verificationCode);
        if (user == null) {
            return ResponseEntity.badRequest().body(new MessageResponse("Không tìm thấy người dùng với mã là: " + verificationCode));
        }
        if (user.getIsActive() == 1) {
            return ResponseEntity.badRequest().body(new MessageResponse("Tài khoản này đã được kích hoạt!!!"));
        }
        user.setIsActive(1);
        userRepository.save(user);
        // Chuyển hướng tới trang frontend và truyền mã xác thực trong URL
        String frontendRedirectUrl = "http://localhost:3002/verification?verificationCode=" + verificationCode;
        return ResponseEntity.status(HttpStatus.FOUND)
                .header("Location", frontendRedirectUrl)
                .body(new MessageResponse("Tài khoản đã được kích hoạt thành công. Redirecting..."));
    }

    @PostMapping("/refreshtoken")
    public ResponseEntity<?> refreshtoken(@Valid @RequestBody TokenRefreshDto request) {
        String requestRefreshToken = request.getRefreshToken();

        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String token = jwtUtils.generateTokenFromUsername(user.getUsername(), user.getId());
                    return ResponseEntity.ok(new TokenRefreshResponse(token, requestRefreshToken));
                })
                .orElseThrow(() -> new TokenRefreshException(requestRefreshToken,
                        "Refresh token không có trong CSDL!"));
    }


    @PostMapping("/signout")
    public ResponseEntity<?> logoutUser() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = userDetails.getId();
        refreshTokenService.deleteByUserId(userId);
        return ResponseEntity.ok(new MessageResponse("Đăng xuất thành công!"));
    }


    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/learners")
    public ResponseEntity<List<User>> getLearners() {
        List<User> learners = userRepository.findByRoles_Name(ERole.ROLE_LEARNER);
        return ResponseEntity.ok(learners);
    }

    @GetMapping("/getUserIdByUsername/{username}")
    public ResponseEntity<?> getUserIdByUsername(@PathVariable String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Lỗi: Không tìm thấy người dùng!"));
        Long userId = user.getId();
        return ResponseEntity.ok(userId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/learners/count")
    public ResponseEntity<Long> countLearners() {
        long learnerCount = userRepository.countByRoles_Name(ERole.ROLE_LEARNER);
        return ResponseEntity.ok(learnerCount);
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<?> getUserById(@PathVariable Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Error: User not found!"));

        return ResponseEntity.ok(user);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<MessageResponse> updateProfile(@PathVariable Long userId, @RequestBody ProfileDto profileDto) {
        try {
            Optional<User> userOptional = userRepository.findById(userId);
            if (userOptional.isPresent()) {
                User existingUser = userOptional.get();
                existingUser.setName(profileDto.getName());
                existingUser.setAddress(profileDto.getAddress());
                existingUser.setPhoneNumber(profileDto.getPhoneNumber());
                existingUser.setGender(profileDto.getGender());
                userRepository.save(existingUser);
                return ResponseEntity.ok(new MessageResponse("Cập nhật thông tin cá nhân thành công!"));
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            // Xử lý các ngoại lệ khác nếu cần
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/change-password/{userId}")
    public ResponseEntity<MessageResponse> changePassword(@PathVariable Long userId, @Valid @RequestBody ChangePasswordDto changePasswordDto) {
        try {
            System.out.println(changePasswordDto);
            Optional<User> userOptional = userRepository.findById(userId);
            if (userOptional.isPresent()) {
                User existingUser = userOptional.get();

                // Kiểm tra mật khẩu cũ
                if (encoder.matches(changePasswordDto.getOldPassWord(), existingUser.getPassword())) {
                    // Mật khẩu cũ đúng, tiến hành thay đổi mật khẩu
                    existingUser.setPassword(encoder.encode(changePasswordDto.getNewPassWord()));
                    userRepository.save(existingUser);
                    return ResponseEntity.ok(new MessageResponse("Đổi mật khẩu thành công!"));
                } else {
                    // Mật khẩu cũ không đúng
                    return ResponseEntity.badRequest().body(new MessageResponse("Mật khẩu cũ không chính xác!"));
                }
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            // Xử lý các ngoại lệ khác nếu cần
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{userId}/status")
    public ResponseEntity<MessageResponse> updateUserStatus(@PathVariable Long userId, @RequestBody int newStatus) {
        try {
            System.out.println(newStatus);
            Optional<User> userOptional = userRepository.findById(userId);
            if (userOptional.isPresent()) {
                User existingUser = userOptional.get();
                existingUser.setStatus(newStatus);
                userRepository.save(existingUser);
                return ResponseEntity.ok(new MessageResponse("Update user status successfully!"));
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(new MessageResponse("Error updating user status: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    //  Gửi email lại
    @PostMapping("/send-email")
    public ResponseEntity<String> sendEmailBack(@RequestBody EmailDto emailDto) {
        try {
            System.out.println(emailDto.getTo());
            // Tìm kiếm người dùng bằng email
            User user = userRepository.findByEmail(emailDto.getTo());
            if (user == null) {
                System.err.println("User not found with email: " + emailDto.getTo());
                return ResponseEntity.badRequest().body("User not found with the provided email.");
            }
            // Tạo mã xác thực mới
            String newVerificationCode = UUID.randomUUID().toString();
            user.setVerificationCode(newVerificationCode);
            userRepository.save(user);

            // Gửi email xác thực mới
            String subject = "Xác thực tài khoản";
            String body = "Nhấn vào liên kết sau để xác thực tài khoản:\n";
            body += "<a href='http://localhost:9004/api/auth/activate-account?verificationCode=" + newVerificationCode + "'>Xác thực tài khoản</a>";

            emailService.sendEmail(emailDto.getTo(), subject, body);

            return ResponseEntity.ok("Email sent successfully");
        } catch (Exception e) {
            System.err.println("Failed to send email: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to send email");
        }
    }

    @GetMapping("/check-email-exists")
    public ResponseEntity<?> checkEmailExists(@RequestParam String email) {
        try {
            boolean emailExists = userRepository.existsByEmail(email);
            return ResponseEntity.ok(emailExists);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Failed to check email existence"));
        }
    }


}
