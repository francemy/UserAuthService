package ao.okayula.forum.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ao.okayula.forum.model.RespostaModel;

@Repository
public interface RespostaRepository extends JpaRepository<RespostaModel, Long> {

    // Buscar respostas por tópico
    List<RespostaModel> findByTopicoId(Long topicoId);

    // Buscar respostas por usuário
    List<RespostaModel> findByUsuarioId(Long usuarioId);

    // Outros métodos de consulta podem ser adicionados conforme necessário
}
