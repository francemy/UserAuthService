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

import ao.okayula.forum.model.RespostaModel;
import ao.okayula.forum.service.RespostaService;

@RestController
@RequestMapping("/api/respostas")
public class RespostaController {

    @Autowired
    private RespostaService respostaService;

    @GetMapping
    public List<RespostaModel> listarRespostas() {
        return respostaService.listarTodos();
    }

    @GetMapping("/topico/{topicoId}")
    public List<RespostaModel> buscarRespostasPorTopico(@PathVariable Long topicoId) {
        return respostaService.buscarPorTopico(topicoId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RespostaModel criarResposta(@RequestBody RespostaModel resposta) {
        return respostaService.salvar(resposta);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletarResposta(@PathVariable Long id) {
        respostaService.deletar(id);
    }
}
