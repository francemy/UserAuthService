package ao.okayula.forum.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ao.okayula.forum.model.UserModel;

@Repository
public interface UserRepository extends JpaRepository<UserModel, Long> {

    // Método para buscar usuário pelo nome de usuário (username)
    Optional<UserModel> findByUsername(String username);

    // Método para buscar usuário pelo e-mail
    Optional<UserModel> findByEmail(String email);

    // Verificar se o usuário existe pelo nome de usuário
    boolean existsByUsername(String username);

    // Verificar se o usuário existe pelo e-mail
    boolean existsByEmail(String email);
}
