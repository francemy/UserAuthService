package ao.okayula.forum.service;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service  // Registra a classe como um Bean no Spring
public class CustomUserDetailsService implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Aqui você pode implementar a lógica para buscar o usuário no banco de dados.
        // Para fins de exemplo, vamos retornar um usuário fixo:
        if ("testuser".equals(username)) {
            return User.builder()
                    .username("testuser")
                    .password("$2a$10$TqZpPspPpQOLWZ.gNwMzEu5j1W3XcP/pzxofCmWnL4zNjPqNpFjQy") // Exemplo de senha criptografada
                    .roles("USER")
                    .build();
        } else {
            throw new UsernameNotFoundException("Usuário não encontrado");
        }
    }
}