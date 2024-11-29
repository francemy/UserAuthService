package ao.okayula.forum.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ao.okayula.forum.model.PerfilModel;
import ao.okayula.forum.repository.PerfilRepository;

@Service
public class PerfilService {

    @Autowired
    private PerfilRepository perfilRepository;

    // Buscar perfil por usuário
    public Optional<PerfilModel> buscarPorUsuario(Long usuarioId) {
        return perfilRepository.findByUsuarioId(usuarioId);
    }

    // Criar ou atualizar perfil
    public PerfilModel salvar(PerfilModel perfil) {
        return perfilRepository.save(perfil);
    }

    // Deletar perfil
    public void deletar(Long id) {
        perfilRepository.deleteById(id);
    }
}
