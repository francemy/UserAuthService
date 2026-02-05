package ao.okayula.forum.faturacao.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.databind.JsonNode;

import ao.okayula.forum.faturacao.dto.FaturaRequest;
import ao.okayula.forum.faturacao.model.Fatura;
import ao.okayula.forum.faturacao.service.FaturaExemploService;
import ao.okayula.forum.faturacao.service.FaturaService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/faturacao")
@Validated
public class FaturacaoController {

    private final FaturaService faturaService;
    private final FaturaExemploService exemploService;

    public FaturacaoController(FaturaService faturaService, FaturaExemploService exemploService) {
        this.faturaService = faturaService;
        this.exemploService = exemploService;
    }

    @GetMapping("/exemplo")
    public JsonNode obterExemploFatura() {
        return exemploService.carregarExemploFatura();
    }

    @GetMapping("/exemplos")
    public JsonNode obterExemplosFaturas() {
        return exemploService.carregarExemplosFaturas();
    }

    @PostMapping("/faturas")
    public ResponseEntity<Fatura> emitirFatura(@Valid @RequestBody FaturaRequest request) {
        Fatura fatura = faturaService.emitirFatura(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(fatura);
    }

    @GetMapping("/faturas")
    public List<Fatura> listarFaturas() {
        return faturaService.listarFaturas();
    }

    @GetMapping("/faturas/{numero}")
    public ResponseEntity<Fatura> buscarFatura(@PathVariable String numero) {
        return faturaService.buscarFatura(numero)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Fatura não encontrada"));
    }

    @PostMapping("/faturas/{numero}/cancelar")
    public ResponseEntity<Fatura> cancelarFatura(@PathVariable String numero) {
        return faturaService.cancelarFatura(numero)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Fatura não encontrada"));
    }
}
