package ao.okayula.forum.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import ao.okayula.forum.dto.ChangePasswordDTO;
import ao.okayula.forum.dto.LoginUserDTO;
import ao.okayula.forum.dto.UserDto;
import ao.okayula.forum.dto.UserFormDTO;
import ao.okayula.forum.exception.ResourceNotFoundException;
import ao.okayula.forum.exception.UserNotFoundException;
import ao.okayula.forum.model.PerfilModel;
import ao.okayula.forum.model.UserModel;
import ao.okayula.forum.repository.PerfilRepository;
import ao.okayula.forum.repository.UserRepository;
import ao.okayula.forum.security.JwtTokenUtil;
import jakarta.annotation.PostConstruct;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public UserService(BCryptPasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Autowired
    private PerfilRepository perfilRepository;

    public UserModel createUserWithProfile(UserFormDTO userFormDTO) {
        // Criar o usuário
        UserModel user = new UserModel();
        user.setUsername(userFormDTO.getUsername());
        user.setEmail(userFormDTO.getEmail());
        user.setPassword(passwordEncoder.encode(userFormDTO.getPassword()));
        user.setRole(userFormDTO.getRole());

        // Salvar o usuário
        userRepository.save(user);

        // Criar o perfil e associar ao usuário
        PerfilModel perfil = new PerfilModel();
        perfil.setDescricao(userFormDTO.getPerfil().getDescricao());
        perfil.setFotoUrl(userFormDTO.getPerfil().getFotoUrl());
        perfil.setUsuario(user); // Associar o perfil ao usuário

        // Salvar o perfil
        perfilRepository.save(perfil);

        return user;
    }

    // Registrar um novo usuário
    public UserModel registerUser(UserDto userDTO) {
        if (userRepository.existsByUsername(userDTO.getUsername())) {
            throw new RuntimeException("Username is already taken");
        }
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new RuntimeException("Email is already taken");
        }

        UserModel newUser = new UserModel();
        newUser.setUsername(userDTO.getUsername());
        newUser.setEmail(userDTO.getEmail());
        newUser.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        newUser.setRole(userDTO.getRole());
        return userRepository.save(newUser);
    }

    @PostConstruct
    public void initDatabase() {
        if (!userRepository.existsByEmail("admin@example.com")) {
            UserModel user = new UserModel();
            // user.setId((long)1 );
            user.setUsername("admin");
            user.setEmail("admin@example.com");
            user.setPassword(passwordEncoder.encode("password123"));
            user.setRole("ADMIN");
            userRepository.save(user);
        }
    }

    // Login de um usuário - gera um token JWT
    public String loginUser(LoginUserDTO userDTO) {
        UserModel user = userRepository.findByUsername(userDTO.getUsername())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!passwordEncoder.matches(userDTO.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        return jwtTokenUtil.generateToken(user.getUsername());
    }

    public UserModel changePassword(ChangePasswordDTO userDTO) {
        // Recupera o usuário pelo nome de usuário
        UserModel user = userRepository.findByUsername(userDTO.getUserName())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        // Verifica se a senha antiga está correta
        if (!passwordEncoder.matches(userDTO.getOldPassword(), user.getPassword())) {
            throw new RuntimeException("Old password is incorrect");
        }

        // Verifica se a nova senha é igual à confirmação
        if (!userDTO.isNewPasswordMatching()) {
            throw new RuntimeException("New password and confirmation do not match");
        }

        // Codifica a nova senha e atualiza no usuário
        user.setPassword(passwordEncoder.encode(userDTO.getNewPassword()));

        // Salva o usuário com a nova senha
        return userRepository.save(user);

    }

    // Recuperar um usuário pelo nome de usuário
    public UserModel getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    /* // Atualizar um usuário
    public UserModel updateUser(Long id, UserFormDTO userFormDTO) {

        Optional<UserModel> userR = userRepository.findById(id);
        if (userR.isPresent()) {
            UserModel user = userR.get();
            user.setUsername(userFormDTO.getUsername());
            
            user.setEmail(userFormDTO.getEmail());
            user.setPassword(passwordEncoder.encode(userFormDTO.getPassword()));
            user.setRole(userFormDTO.getRole());

            // Salvar o usuário
            userRepository.save(user);

            // Criar o perfil e associar ao usuário
            Optional<PerfilModel> perfilR =  perfilRepository.findByUsuarioId(id);
            PerfilModel perfil;
            if(perfilR.isPresent())
                perfil = perfilR.get();
            else
                perfil = new PerfilModel();
            perfil.setDescricao(userFormDTO.getPerfil().getDescricao());
            perfil.setFotoUrl(userFormDTO.getPerfil().getFotoUrl());
            perfil.setUsuario(user); // Associar o perfil ao usuário

            // Salvar o perfil
            perfilRepository.save(perfil);
        }
        return userR.get();
    } */

    public UserModel updateUser(Long id, UserFormDTO userFormDTO) {
        // Buscar o usuário pelo ID
        UserModel user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com o ID: " + id));
    
        // Atualizar os dados do usuário
        user.setUsername(userFormDTO.getUsername());
        user.setEmail(userFormDTO.getEmail());
        user.setPassword(passwordEncoder.encode(userFormDTO.getPassword()));
        user.setRole(userFormDTO.getRole());
    
        // Salvar as atualizações do usuário
        userRepository.save(user);
    
        // Buscar ou criar o perfil associado ao usuário
        PerfilModel perfil = perfilRepository.findByUsuarioId(id)
                .orElseGet(PerfilModel::new);
    
        perfil.setDescricao(userFormDTO.getPerfil().getDescricao());
        perfil.setFotoUrl(userFormDTO.getPerfil().getFotoUrl());
        perfil.setUsuario(user); // Associar o perfil ao usuário
    
        // Salvar o perfil atualizado
        perfilRepository.save(perfil);
    
        return user;
    }
    

    public void deleteUser(Long id) {
        // Buscar o usuário pelo ID
        UserModel user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        Optional<PerfilModel> perfil = perfilRepository.findByUsuarioId(id);
        // Verificar se o usuário tem um perfil associado
        if (perfil.isPresent()) {
            // Se tiver, excluir o perfil
            perfilRepository.delete(perfil.get());
        }

        // Excluir o usuário
        userRepository.delete(user);
    }

    // Listar todos os usuários
    public List<UserModel> getAllUsers() {
        return userRepository.findAll();
    }
}
