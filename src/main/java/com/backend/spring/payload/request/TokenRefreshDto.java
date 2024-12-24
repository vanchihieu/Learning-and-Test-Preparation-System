package com.backend.spring.payload.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TokenRefreshDto {
    @NotBlank
    private String refreshToken;
}
