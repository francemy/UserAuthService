package ao.okayula.forum.faturacao.dto;

import jakarta.validation.constraints.NotBlank;

public class EnderecoRequest {

    @NotBlank(message = "A rua é obrigatória")
    private String rua;

    @NotBlank(message = "A cidade é obrigatória")
    private String cidade;

    @NotBlank(message = "A província é obrigatória")
    private String provincia;

    @NotBlank(message = "O país é obrigatório")
    private String pais;

    private String codigoPostal;

    public String getRua() {
        return rua;
    }

    public void setRua(String rua) {
        this.rua = rua;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getProvincia() {
        return provincia;
    }

    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public String getCodigoPostal() {
        return codigoPostal;
    }

    public void setCodigoPostal(String codigoPostal) {
        this.codigoPostal = codigoPostal;
    }
}
