package ao.okayula.forum.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ao.okayula.forum.model.TopicoModel;

@Repository
public interface TopicoRepository extends JpaRepository<TopicoModel, Long> {

    // Buscar tópicos por curso
    List<TopicoModel> findByCursoId(Long cursoId);

    // Buscar tópicos por usuário (autor do tópico)
    List<TopicoModel> findByUsuarioId(Long usuarioId);

    // Outros métodos de consulta podem ser adicionados conforme necessário
}
