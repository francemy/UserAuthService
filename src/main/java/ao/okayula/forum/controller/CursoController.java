package ao.okayula.forum.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import ao.okayula.forum.model.CursoModel;
import ao.okayula.forum.service.CursoService;

@RestController
@RequestMapping("/api/cursos")
public class CursoController {

    @Autowired
    private CursoService cursoService;

    @GetMapping
    public List<CursoModel> listarCursos() {
        return cursoService.listarTodos();
    }

    @GetMapping("/{id}")
    public Optional<CursoModel> buscarCurso(@PathVariable Long id) {
        return cursoService.buscarPorId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CursoModel criarCurso(@RequestBody CursoModel curso) {
        return cursoService.salvar(curso);
    }

    @PutMapping("/{id}")
    public CursoModel atualizarCurso(@PathVariable Long id, @RequestBody CursoModel curso) {
        curso.setId(id);
        return cursoService.salvar(curso);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletarCurso(@PathVariable Long id) {
        cursoService.deletar(id);
    }
}
