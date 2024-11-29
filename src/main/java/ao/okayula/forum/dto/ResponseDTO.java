package ao.okayula.forum.dto;

import jakarta.validation.constraints.NotEmpty;

public class ResponseDTO {
    @NotEmpty(message = "jwt vazio")  // Corrigido aqui
    private String data;
    private String message;

    // Construtor
    public ResponseDTO(String data, String message) {
        this.data = data;
        this.message = message;
    }

    // Getters and setters
    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
