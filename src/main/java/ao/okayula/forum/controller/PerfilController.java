package ao.okayula.forum.controller;

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
import ao.okayula.forum.model.PerfilModel;
import ao.okayula.forum.service.PerfilService;

@RestController
@RequestMapping("/api/perfis")
public class PerfilController {

    @Autowired
    private PerfilService perfilService;

    /**
     * Buscar o perfil de um usuário pelo ID do usuário
     * @param usuarioId ID do usuário
     * @return ResponseDTO com status adequado
     */
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<ResponseDTO> buscarPerfilPorUsuario(@PathVariable Long usuarioId) {
        PerfilModel perfil = perfilService.buscarPorUsuario(usuarioId);
        
        if (perfil != null) {
            return ResponseEntity.ok(new ResponseDTO(perfil.toString(), "Perfil encontrado com sucesso"));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseDTO("", "Perfil não encontrado"));
        }
    }

    /**
     * Criar um novo perfil para um usuário
     * @param perfil Dados do perfil a ser criado
     * @return ResponseDTO com dados do perfil criado e status HTTP 201 (Criado)
     */
    @PostMapping
    public ResponseEntity<ResponseDTO> criarPerfil(@RequestBody PerfilModel perfil) {
        try {
            PerfilModel novoPerfil = perfilService.salvar(perfil);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ResponseDTO(novoPerfil.toString(), "Perfil criado com sucesso"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseDTO("", "Erro ao criar o perfil: " + e.getMessage()));
        }
    }

    /**
     * Deletar o perfil por ID
     * @param id ID do perfil a ser deletado
     * @return ResponseDTO com mensagem e status HTTP 204 (No Content)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDTO> deletarPerfil(@PathVariable Long id) {
        try {
            perfilService.deletar(id);
            return ResponseEntity.noContent()
                    .build();  // Resposta sem conteúdo, apenas com status 204 (No Content)
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseDTO("", "Perfil não encontrado para exclusão"));
        }
    }
}
