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
import ao.okayula.forum.model.TopicoModel;
import ao.okayula.forum.service.TopicoService;

@RestController
@RequestMapping("/api/topicos")
public class TopicoController {

    @Autowired
    private TopicoService topicoService;

    /**
     * Listar todos os tópicos
     * @return Lista de tópicos
     */
    @GetMapping
    public ResponseEntity<ResponseDTO> listarTopicos() {
        List<TopicoModel> topicos = topicoService.listarTodos();
        return ResponseEntity.ok(new ResponseDTO(topicos.toString(), "Tópicos listados com sucesso"));
    }

    /**
     * Buscar tópicos por ID do curso
     * @param cursoId ID do curso
     * @return Lista de tópicos do curso
     */
    @GetMapping("/curso/{cursoId}")
    public ResponseEntity<ResponseDTO> buscarTopicosPorCurso(@PathVariable Long cursoId) {
        List<TopicoModel> topicos = topicoService.buscarPorCurso(cursoId);
        if (topicos.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseDTO("", "Nenhum tópico encontrado para o curso"));
        }
        return ResponseEntity.ok(new ResponseDTO(topicos.toString(), "Tópicos encontrados para o curso"));
    }

    /**
     * Criar um novo tópico
     * @param topico Dados do tópico a ser criado
     * @return Tópico criado com sucesso
     */
    @PostMapping
    public ResponseEntity<ResponseDTO> criarTopico(@RequestBody TopicoModel topico) {
        try {
            TopicoModel novoTopico = topicoService.salvar(topico);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ResponseDTO(novoTopico.toString(), "Tópico criado com sucesso"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseDTO("", "Erro ao criar o tópico: " + e.getMessage()));
        }
    }

    /**
     * Deletar tópico por ID
     * @param id ID do tópico a ser deletado
     * @return Status de sucesso ou erro
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDTO> deletarTopico(@PathVariable Long id) {
        try {
            topicoService.deletar(id);
            return ResponseEntity.noContent()
                    .build();  // Retorna status HTTP 204 (No Content)
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseDTO("", "Tópico não encontrado para exclusão"));
        }
    }
}
