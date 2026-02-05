package ao.okayula.forum.faturacao.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class FaturaRequest {

    private String numero;

    @NotNull(message = "A data de emissão é obrigatória")
    private LocalDate dataEmissao;

    @NotBlank(message = "A moeda é obrigatória")
    private String moeda;

    @Valid
    @NotNull(message = "Os dados do cliente são obrigatórios")
    private ClienteRequest cliente;

    @Valid
    @NotNull(message = "Os itens da fatura são obrigatórios")
    private List<FaturaItemRequest> itens;

    @NotNull(message = "O subtotal é obrigatório")
    @Positive(message = "O subtotal deve ser positivo")
    private BigDecimal subtotal;

    @NotNull(message = "Os impostos são obrigatórios")
    @Positive(message = "Os impostos devem ser positivos")
    private BigDecimal impostos;

    @NotNull(message = "O desconto é obrigatório")
    private BigDecimal desconto;

    @NotNull(message = "O total é obrigatório")
    @Positive(message = "O total deve ser positivo")
    private BigDecimal total;

    @Valid
    @NotNull(message = "O pagamento é obrigatório")
    private PagamentoRequest pagamento;

    private String status;

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public LocalDate getDataEmissao() {
        return dataEmissao;
    }

    public void setDataEmissao(LocalDate dataEmissao) {
        this.dataEmissao = dataEmissao;
    }

    public String getMoeda() {
        return moeda;
    }

    public void setMoeda(String moeda) {
        this.moeda = moeda;
    }

    public ClienteRequest getCliente() {
        return cliente;
    }

    public void setCliente(ClienteRequest cliente) {
        this.cliente = cliente;
    }

    public List<FaturaItemRequest> getItens() {
        return itens;
    }

    public void setItens(List<FaturaItemRequest> itens) {
        this.itens = itens;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getImpostos() {
        return impostos;
    }

    public void setImpostos(BigDecimal impostos) {
        this.impostos = impostos;
    }

    public BigDecimal getDesconto() {
        return desconto;
    }

    public void setDesconto(BigDecimal desconto) {
        this.desconto = desconto;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public PagamentoRequest getPagamento() {
        return pagamento;
    }

    public void setPagamento(PagamentoRequest pagamento) {
        this.pagamento = pagamento;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
