package ao.okayula.forum.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import ao.okayula.forum.dto.ChangePasswordDTO;
import ao.okayula.forum.dto.LoginUserDTO;
import ao.okayula.forum.dto.UserDto;
import ao.okayula.forum.exception.UserNotFoundException;
import ao.okayula.forum.model.UserModel;
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

    // Atualizar um usuário
    public UserModel updateUser(String id, UserDto userDTO) {
        UserModel user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());

        if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        }

        user.setRole(userDTO.getRole());
        return userRepository.save(user);
    }

    // Deletar um usuário
    public void deleteUser(String id) {
        UserModel user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        userRepository.delete(user);
    }

    // Listar todos os usuários
    public List<UserModel> getAllUsers() {
        return userRepository.findAll();
    }
}
