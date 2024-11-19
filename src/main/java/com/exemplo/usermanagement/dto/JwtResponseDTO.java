package com.exemplo.usermanagement.dto;
import jakarta.validation.constraints.NotEmpty;

public class JwtResponseDTO {
    @NotEmpty(message = "Token must not be empty")
    private String token;

    public JwtResponseDTO(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "JwtResponseDTO{token='" + token + "'}";
    }
}
