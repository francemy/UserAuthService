package ao.okayula.forum.dto;


public class JwtPayload {
    private String username;
    private String email;
    private Long userId;

    // Construtores
    public JwtPayload(String username, String email, Long userId) {
        this.username = username;
        this.email = email;
        this.userId = userId;
    }

    // Getters e Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
