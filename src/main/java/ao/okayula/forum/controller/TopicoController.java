package ao.okayula.forum.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import ao.okayula.forum.model.TopicoModel;
import ao.okayula.forum.service.TopicoService;

@RestController
@RequestMapping("/api/topicos")
public class TopicoController {

    @Autowired
    private TopicoService topicoService;

    @GetMapping
    public List<TopicoModel> listarTopicos() {
        return topicoService.listarTodos();
    }

    @GetMapping("/curso/{cursoId}")
    public List<TopicoModel> buscarTopicosPorCurso(@PathVariable Long cursoId) {
        return topicoService.buscarPorCurso(cursoId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TopicoModel criarTopico(@RequestBody TopicoModel topico) {
        return topicoService.salvar(topico);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletarTopico(@PathVariable Long id) {
        topicoService.deletar(id);
    }
}
