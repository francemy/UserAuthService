package ao.okayula.forum.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ao.okayula.forum.model.CursoModel;

@Repository
public interface CursoRepository extends JpaRepository<CursoModel, Long> {

    // Buscar um curso por nome
    Optional<CursoModel> findByNome(String nome);

    // Outros métodos de consulta podem ser adicionados conforme necessário
}
