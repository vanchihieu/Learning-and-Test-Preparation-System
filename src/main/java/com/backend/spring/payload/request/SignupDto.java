package com.backend.spring.payload.request;

import java.util.Set;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignupDto {
    @NotBlank
    @Size(min = 3, max = 20)
    private String username;

    @NotBlank
    @Size(max = 50)
    @Email
    private String email;

    // Danh sách không trùng lặp
    private Set<String> role;

    @NotBlank
    @Size(min = 6, max = 40)
    private String password;

    private String address;

    private String phoneNumber;

    private Integer gender;

    @Size(min = 2, max = 50)
    private String name;

}
