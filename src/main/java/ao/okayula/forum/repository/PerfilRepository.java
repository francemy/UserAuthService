package ao.okayula.forum.repository;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ao.okayula.forum.model.PerfilModel;

@Repository
public interface PerfilRepository extends JpaRepository<PerfilModel, Long> {

    // Buscar perfil pelo usuário
    Optional<PerfilModel> findByUsuarioId(Long usuarioId);

    Optional<PerfilModel> findByUsuarioUsername(String nomeUsuario);

    // Outros métodos de consulta podem ser adicionados conforme necessário
}
