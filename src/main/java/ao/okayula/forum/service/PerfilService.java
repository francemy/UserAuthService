package ao.okayula.forum.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ao.okayula.forum.model.PerfilModel;
import ao.okayula.forum.model.UserModel;
import ao.okayula.forum.repository.PerfilRepository;
import ao.okayula.forum.repository.UserRepository;

@Service
public class PerfilService {

    @Autowired
    private PerfilRepository perfilRepository;

    @Autowired
    private UserRepository userRepository;



    // Buscar perfil por usuário
    public PerfilModel buscarPorUsuario(Long usuarioId) {

        Optional<PerfilModel> perfil = perfilRepository.findByUsuarioId(usuarioId);
        if (perfil.isPresent()) {
            return perfil.get();
        }
        return null;
    }

    // Buscar perfil pelo nome do usuário
    public PerfilModel buscarPorNomeUsuario(String nomeUsuario) {
        System.out.println("nome do user:"+nomeUsuario);
        Optional<UserModel> user = userRepository.findByUsername(nomeUsuario);
        Optional<PerfilModel> perfil = perfilRepository.findByUsuarioId(user.get().getId());
        System.out.println(perfil);
        if (perfil.isPresent()) {

            return perfil.get();
        }
        return null;
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
