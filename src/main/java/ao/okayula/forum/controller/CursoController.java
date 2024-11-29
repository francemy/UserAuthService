package ao.okayula.forum.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import ao.okayula.forum.dto.ResponseDTO;
import ao.okayula.forum.model.CursoModel;
import ao.okayula.forum.service.CursoService;

@RestController
@RequestMapping("/api/cursos")
public class CursoController {

    @Autowired
    private CursoService cursoService;

    // Listar todos os cursos
    @GetMapping
    public ResponseEntity<ResponseDTO> listarCursos() {
        List<CursoModel> cursos = cursoService.listarTodos();
        if (cursos.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(new ResponseDTO("[]", "Nenhum curso encontrado"));  // Retorna 204 com uma mensagem
        }
        return ResponseEntity.ok(new ResponseDTO(cursos.toString(), "Cursos encontrados com sucesso"));
    }

    // Buscar curso por ID
    @GetMapping("/{id}")
    public ResponseEntity<ResponseDTO> buscarCurso(@PathVariable Long id) {
        Optional<CursoModel> curso = cursoService.buscarPorId(id);
        if (curso.isPresent()) {
            return ResponseEntity.ok(new ResponseDTO(curso.get().toString(), "Curso encontrado com sucesso"));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ResponseDTO("", "Curso não encontrado"));
        }
    }

    // Criar novo curso
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ResponseDTO> criarCurso(@RequestBody @Validated CursoModel curso) {
        try {
            CursoModel novoCurso = cursoService.salvar(curso);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResponseDTO(novoCurso.toString(), "Curso criado com sucesso"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ResponseDTO("", "Erro ao criar o curso: " + e.getMessage()));
        }
    }

    // Atualizar curso existente
    @PutMapping("/{id}")
    public ResponseEntity<ResponseDTO> atualizarCurso(@PathVariable Long id, @RequestBody @Validated CursoModel curso) {
        Optional<CursoModel> cursoExistente = cursoService.buscarPorId(id);
        if (cursoExistente.isPresent()) {
            curso.setId(id);
            CursoModel cursoAtualizado = cursoService.salvar(curso);
            return ResponseEntity.ok(new ResponseDTO(cursoAtualizado.toString(), "Curso atualizado com sucesso"));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ResponseDTO("", "Curso não encontrado para atualização"));
        }
    }

    // Deletar curso
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDTO> deletarCurso(@PathVariable Long id) {
        Optional<CursoModel> cursoExistente = cursoService.buscarPorId(id);
        if (cursoExistente.isPresent()) {
            cursoService.deletar(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(new ResponseDTO("", "Curso deletado com sucesso"));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ResponseDTO("", "Curso não encontrado para deleção"));
        }
    }

    // Método de exceção genérica (por exemplo, para erros inesperados)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseDTO> handleGenericException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(new ResponseDTO("", "Erro interno do servidor: " + ex.getMessage()));
    }
}
