package com.exemplo.usermanagement.repository;

import com.exemplo.usermanagement.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    // Método para buscar usuário pelo nome de usuário (username)
    Optional<User> findByUsername(String username);

    // Método para buscar usuário pelo e-mail
    Optional<User> findByEmail(String email);

    // Verificar se o usuário existe pelo nome de usuário
    boolean existsByUsername(String username);

    // Verificar se o usuário existe pelo e-mail
    boolean existsByEmail(String email);
}
