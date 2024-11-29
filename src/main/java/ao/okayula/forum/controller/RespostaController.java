package ao.okayula.forum.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ao.okayula.forum.dto.ResponseDTO;
import ao.okayula.forum.model.RespostaModel;
import ao.okayula.forum.service.RespostaService;

@RestController
@RequestMapping("/api/respostas")
public class RespostaController {

    @Autowired
    private RespostaService respostaService;

    /**
     * Listar todas as respostas
     * @return Lista de respostas
     */
    @GetMapping
    public ResponseEntity<ResponseDTO> listarRespostas() {
        List<RespostaModel> respostas = respostaService.listarTodos();
        return ResponseEntity.ok(new ResponseDTO(respostas.toString(), "Respostas listadas com sucesso"));
    }

    /**
     * Buscar respostas por ID do tópico
     * @param topicoId ID do tópico
     * @return Lista de respostas do tópico
     */
    @GetMapping("/topico/{topicoId}")
    public ResponseEntity<ResponseDTO> buscarRespostasPorTopico(@PathVariable Long topicoId) {
        List<RespostaModel> respostas = respostaService.buscarPorTopico(topicoId);
        if (respostas.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseDTO("", "Nenhuma resposta encontrada para o tópico"));
        }
        return ResponseEntity.ok(new ResponseDTO(respostas.toString(), "Respostas do tópico encontradas com sucesso"));
    }

    /**
     * Criar uma nova resposta
     * @param resposta Dados da resposta a ser criada
     * @return Resposta criada com sucesso
     */
    @PostMapping
    public ResponseEntity<ResponseDTO> criarResposta(@RequestBody RespostaModel resposta) {
        try {
            RespostaModel novaResposta = respostaService.salvar(resposta);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ResponseDTO(novaResposta.toString(), "Resposta criada com sucesso"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseDTO("", "Erro ao criar a resposta: " + e.getMessage()));
        }
    }

    /**
     * Deletar resposta por ID
     * @param id ID da resposta a ser deletada
     * @return Status de sucesso ou erro
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDTO> deletarResposta(@PathVariable Long id) {
        try {
            respostaService.deletar(id);
            return ResponseEntity.noContent()
                    .build();  // Retorna status HTTP 204 (No Content)
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseDTO("", "Resposta não encontrada para exclusão"));
        }
    }
}
