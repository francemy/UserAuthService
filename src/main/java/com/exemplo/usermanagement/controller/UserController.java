package com.exemplo.usermanagement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.exemplo.usermanagement.dto.UserDTO;
import com.exemplo.usermanagement.model.User;
import com.exemplo.usermanagement.security.JwtTokenUtil;
import com.exemplo.usermanagement.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    // Endpoint para registro de novos usuários
    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody UserDTO userDTO) {
        try {
            User newUser = userService.registerUser(userDTO);
            return new ResponseEntity<>(newUser, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
    // Endpoint para login de usuários (gera token JWT)
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserDTO userDTO) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(userDTO.getUsername(), userDTO.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtTokenUtil.generateToken(authentication.getName());

        return ResponseEntity.ok(jwt);
    }

    // Endpoint para obter informações do usuário (apenas para usuários autenticados)
    @GetMapping("/profile")
    public ResponseEntity<User> getProfile(HttpServletRequest request) {
        // Obtém o token do cabeçalho Authorization
        String token = jwtTokenUtil.getTokenFromRequest(request);
        if (token == null || !jwtTokenUtil.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String username = jwtTokenUtil.getUsernameFromToken(token);
        User user = userService.getUserByUsername(username);
        return ResponseEntity.ok(user);
    }

    // Endpoint para listar todos os usuários (somente administradores)
    @GetMapping
    public ResponseEntity<?> getAllUsers() {
        // Este endpoint pode ser restrito para administradores através do Spring Security
        return ResponseEntity.ok(userService.getAllUsers());
    }

    // Endpoint para atualizar um usuário
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable String id, @RequestBody UserDTO userDTO) {
        User updatedUser = userService.updateUser(id, userDTO);
        return ResponseEntity.ok(updatedUser);
    }

    // Endpoint para deletar um usuário
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
