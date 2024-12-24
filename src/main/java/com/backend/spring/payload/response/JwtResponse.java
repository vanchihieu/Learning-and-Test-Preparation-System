package com.backend.spring.payload.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private String refreshToken;
    private Long id;
    private String username;
    private String email;
    private String address;
    private String phoneNumber;
    private Integer gender;
    private Integer status;
    private Integer isActive;
    private String verificationCode;
    private String name;

    private List<String> roles;
    private Long jwtExpirationTime; // Thêm thông tin thời gian hết hạn của Access Token
    private Long refreshTokenExpirationTime; // Thêm thông tin thời gian hết hạn của Refresh Token

    public JwtResponse(String accessToken, String refreshToken, Long id, String username, String email, String address, String phoneNumber, Integer gender,  Integer status, Integer isActive, String verificationCode, String name, List<String> roles, Long accessTokenExpirationDate, Long refreshTokenExpirationTime) {
        this.token = accessToken;
        this.refreshToken = refreshToken;
        this.id = id;
        this.username = username;
        this.email = email;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.gender = gender;
        this.status = status;
        this.isActive = isActive;
        this.verificationCode = verificationCode;
        this.name = name;
        this.roles = roles;
        this.jwtExpirationTime = accessTokenExpirationDate;
        this.refreshTokenExpirationTime = refreshTokenExpirationTime;

    }


}
