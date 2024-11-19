package com.exemplo.usermanagement.service;

import com.exemplo.usermanagement.dto.JwtPayload;
import com.exemplo.usermanagement.dto.UserDTO;
import com.exemplo.usermanagement.dto.loginUserDTO;
import com.exemplo.usermanagement.model.User;
import com.exemplo.usermanagement.model.UserSession;
import com.exemplo.usermanagement.repository.UserRepository;
import com.exemplo.usermanagement.repository.UserSessionRepository;
import com.exemplo.usermanagement.security.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserSessionService userSessionService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    // Registrar um novo usuário
    public User registerUser(UserDTO userDTO) {
        System.out.println("Recebendo solicitação de registro do usuário: " + userDTO);
        //
        if (userRepository.existsByUsername(userDTO.getUsername())) {
            throw new RuntimeException("Username is already taken");
        }
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new RuntimeException("Email is already taken");
        }

        User newUser = new User();
        newUser.setUsername(userDTO.getUsername());
        newUser.setEmail(userDTO.getEmail());
        newUser.setPassword(passwordEncoder.encode(userDTO.getPassword())); // Senha criptografada
        newUser.setRole(userDTO.getRole());
        return userRepository.save(newUser);
    }

    // Login de um usuário - gera um token JWT
    public String loginUser(loginUserDTO userDTO) {
        System.out.println("Entrou no método loginUser com o username: " + userDTO.getUsername());

        User user = userRepository.findByUsername(userDTO.getUsername())
                .orElseThrow(() -> {
                    System.out.println("Usuário não encontrado: " + userDTO.getUsername());
                    return new RuntimeException("User not found");
                });

        if (!passwordEncoder.matches(userDTO.getPassword(), user.getPassword())) {
            System.out.println("Senha inválida para o username: " + userDTO.getUsername());
            throw new RuntimeException("Invalid password");
        }
        JwtPayload payload = new JwtPayload(user.getUsername(), user.getEmail(), user.getId());

        String token = jwtTokenUtil.generateToken(payload);
        userSessionService.saveUserSession(payload.getUserId(), token);

        System.out.println("Token gerado com sucesso para o username: " + userDTO.getUsername());
        return token;
    }

    // Recuperar um usuário pelo nome de usuário
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // Atualizar um usuário
    public User updateUser(Long id, UserDTO userDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword())); // Senha criptografada
        user.setRole(userDTO.getRole());
        return userRepository.save(user);
    }

    // Deletar um usuário
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        userRepository.delete(user);
    }

    // Listar todos os usuários (admin)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
