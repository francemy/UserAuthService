package ao.okayula.forum.faturacao.service;

import java.io.IOException;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class FaturaExemploService {

    private final ObjectMapper objectMapper;

    public FaturaExemploService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public JsonNode carregarExemploFatura() {
        return lerJson("faturacao/exemplo-fatura.json");
    }

    public JsonNode carregarExemplosFaturas() {
        return lerJson("faturacao/exemplo-faturas.json");
    }

    private JsonNode lerJson(String caminho) {
        Resource resource = new ClassPathResource(caminho);
        try {
            return objectMapper.readTree(resource.getInputStream());
        } catch (IOException ex) {
            throw new IllegalStateException("Não foi possível ler o arquivo JSON: " + caminho, ex);
        }
    }
}
