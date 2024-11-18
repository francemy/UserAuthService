package com.exemplo.usermanagement.service;

import com.exemplo.usermanagement.dto.UserDTO;
import com.exemplo.usermanagement.model.User;
import com.exemplo.usermanagement.repository.UserRepository;
import com.exemplo.usermanagement.security.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    // Registrar um novo usuário
    public User registerUser(UserDTO userDTO) {
        // Verifica se o nome de usuário ou e-mail já existem no banco de dados
        if (userRepository.existsByUsername(userDTO.getUsername())) {
            throw new RuntimeException("Username is already taken");
        }
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new RuntimeException("Email is already taken");
        }

        // Criação de um novo usuário
        User newUser = new User();
        newUser.setUsername(userDTO.getUsername());
        newUser.setEmail(userDTO.getEmail());
        newUser.setPassword(passwordEncoder.encode(userDTO.getPassword())); // Senha criptografada
        newUser.setRole(userDTO.getRole());
        return userRepository.save(newUser);
    }

    // Login de um usuário - gera um token JWT
    public String loginUser(UserDTO userDTO) {
        User user = userRepository.findByUsername(userDTO.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Verifica se a senha fornecida é válida
        if (!passwordEncoder.matches(userDTO.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        // Gerar o token JWT
        return jwtTokenUtil.generateToken(userDTO.getUsername());
    }

    // Recuperar um usuário pelo nome de usuário
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // Atualizar um usuário
    public User updateUser(String id, UserDTO userDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword())); // Senha criptografada
        user.setRole(userDTO.getRole());
        return userRepository.save(user);
    }

    // Deletar um usuário
    public void deleteUser(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        userRepository.delete(user);
    }

    // Listar todos os usuários (admin)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
