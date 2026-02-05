package ao.okayula.forum.faturacao.service;

import java.time.LocalDate;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Service;

import ao.okayula.forum.faturacao.dto.ClienteRequest;
import ao.okayula.forum.faturacao.dto.EnderecoRequest;
import ao.okayula.forum.faturacao.dto.FaturaItemRequest;
import ao.okayula.forum.faturacao.dto.FaturaRequest;
import ao.okayula.forum.faturacao.dto.PagamentoRequest;
import ao.okayula.forum.faturacao.model.Cliente;
import ao.okayula.forum.faturacao.model.Endereco;
import ao.okayula.forum.faturacao.model.Fatura;
import ao.okayula.forum.faturacao.model.FaturaItem;
import ao.okayula.forum.faturacao.model.Pagamento;

@Service
public class FaturaService {

    private final Map<String, Fatura> faturas = new ConcurrentHashMap<>();
    private final AtomicLong sequencia = new AtomicLong(1);

    public Fatura emitirFatura(FaturaRequest request) {
        Fatura fatura = new Fatura();
        String numero = request.getNumero();
        if (numero == null || numero.isBlank()) {
            numero = gerarNumero(LocalDate.now());
        }

        fatura.setNumero(numero);
        fatura.setDataEmissao(request.getDataEmissao());
        fatura.setMoeda(request.getMoeda());
        fatura.setCliente(mapCliente(request.getCliente()));
        fatura.setItens(mapItens(request.getItens()));
        fatura.setSubtotal(request.getSubtotal());
        fatura.setImpostos(request.getImpostos());
        fatura.setDesconto(request.getDesconto());
        fatura.setTotal(request.getTotal());
        fatura.setPagamento(mapPagamento(request.getPagamento()));
        fatura.setStatus(request.getStatus() == null ? "EMITIDA" : request.getStatus());

        faturas.put(numero, fatura);
        return fatura;
    }

    public List<Fatura> listarFaturas() {
        return new ArrayList<>(faturas.values());
    }

    public Optional<Fatura> buscarFatura(String numero) {
        return Optional.ofNullable(faturas.get(numero));
    }

    public Optional<Fatura> cancelarFatura(String numero) {
        Fatura fatura = faturas.get(numero);
        if (fatura == null) {
            return Optional.empty();
        }
        fatura.setStatus("CANCELADA");
        return Optional.of(fatura);
    }

    private String gerarNumero(LocalDate dataEmissao) {
        long atual = sequencia.getAndIncrement();
        return String.format("FT-%s-%04d", Year.from(dataEmissao), atual);
    }

    private Cliente mapCliente(ClienteRequest request) {
        Cliente cliente = new Cliente();
        cliente.setNome(request.getNome());
        cliente.setNif(request.getNif());
        cliente.setEmail(request.getEmail());
        cliente.setTelefone(request.getTelefone());
        cliente.setEndereco(mapEndereco(request.getEndereco()));
        return cliente;
    }

    private Endereco mapEndereco(EnderecoRequest request) {
        if (request == null) {
            return null;
        }
        Endereco endereco = new Endereco();
        endereco.setRua(request.getRua());
        endereco.setCidade(request.getCidade());
        endereco.setProvincia(request.getProvincia());
        endereco.setPais(request.getPais());
        endereco.setCodigoPostal(request.getCodigoPostal());
        return endereco;
    }

    private List<FaturaItem> mapItens(List<FaturaItemRequest> itens) {
        List<FaturaItem> resultado = new ArrayList<>();
        for (FaturaItemRequest itemRequest : itens) {
            FaturaItem item = new FaturaItem();
            item.setDescricao(itemRequest.getDescricao());
            item.setQuantidade(itemRequest.getQuantidade());
            item.setPrecoUnitario(itemRequest.getPrecoUnitario());
            item.setTotal(itemRequest.getTotal());
            resultado.add(item);
        }
        return resultado;
    }

    private Pagamento mapPagamento(PagamentoRequest request) {
        Pagamento pagamento = new Pagamento();
        pagamento.setMetodo(request.getMetodo());
        pagamento.setReferencia(request.getReferencia());
        pagamento.setDataPagamento(request.getDataPagamento());
        pagamento.setValorPago(request.getValorPago());
        return pagamento;
    }
}
