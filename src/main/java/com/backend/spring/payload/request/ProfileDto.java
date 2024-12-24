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
public class ProfileDto {
    @NotBlank
    @Size(max = 100) // Adjust the size as needed
    private String address;

    @NotBlank
    @Size(max = 15) // Adjust the size as needed
    private String phoneNumber;

    private Integer gender;
    @Size(max = 50) // Adjust the size as needed
    private String name;
}
