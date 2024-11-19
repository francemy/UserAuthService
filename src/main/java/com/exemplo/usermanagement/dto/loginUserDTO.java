package com.exemplo.usermanagement.dto;

import jakarta.validation.constraints.NotBlank;

public class loginUserDTO {
    @NotBlank(message = "O campo username é obrigatório.")
    private String username;

    @NotBlank(message = "O campo password é obrigatório.")
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
