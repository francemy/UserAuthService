package ao.okayula.forum.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import ao.okayula.forum.model.UserModel;
import ao.okayula.forum.repository.UserRepository;

@Service  // Registra a classe como um Bean no Spring
public class CustomUserDetailsService implements UserDetailsService {

     @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Aqui você pode implementar a lógica para buscar o usuário no banco de dados.
        // Para fins de exemplo, vamos retornar um usuário fixo:
          Optional<UserModel> user = userRepository.findByUsername(username);
        
        if (!user.isPresent()) {
            throw new UsernameNotFoundException("Usuário não encontrado: " + username);
        }
        UserModel userdto = user.get();
        // Retorna o usuário com as informações necessárias para autenticação
        return User.builder()
                .username(userdto.getUsername())
                .password(userdto.getPassword())  // Senha já criptografada no banco de dados
                .roles(userdto.getRole())  // Ou roles do usuário
                .build();
     
    }
}