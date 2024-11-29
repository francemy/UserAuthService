package ao.okayula.forum.controller;

import java.util.Optional;

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

import ao.okayula.forum.model.PerfilModel;
import ao.okayula.forum.service.PerfilService;

@RestController
@RequestMapping("/api/perfis")
public class PerfilController {

    @Autowired
    private PerfilService perfilService;

    @GetMapping("/usuario/{usuarioId}")
    public Optional<PerfilModel> buscarPerfilPorUsuario(@PathVariable Long usuarioId) {
        return perfilService.buscarPorUsuario(usuarioId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PerfilModel criarPerfil(@RequestBody PerfilModel perfil) {
        return perfilService.salvar(perfil);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletarPerfil(@PathVariable Long id) {
        perfilService.deletar(id);
    }
}
