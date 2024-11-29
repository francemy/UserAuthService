package ao.okayula.forum.dto;

import jakarta.validation.constraints.NotBlank;

public class LoginUserDTO {

    @NotBlank(message = "O campo username é obrigatório.")
    private final String username;

    @NotBlank(message = "O campo password é obrigatório.")
    private final String password;

    public LoginUserDTO(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
