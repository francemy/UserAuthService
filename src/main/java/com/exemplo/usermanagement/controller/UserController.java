package com.exemplo.usermanagement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import com.exemplo.usermanagement.dto.UserDTO;
import com.exemplo.usermanagement.dto.loginUserDTO;
import com.exemplo.usermanagement.model.User;
import com.exemplo.usermanagement.security.JwtTokenUtil;
import com.exemplo.usermanagement.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

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
    public ResponseEntity<User> registerUser(@Valid @RequestBody UserDTO userDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            // Se houver erros de validação, retorne uma resposta 400 com os erros
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        try {
            User newUser = userService.registerUser(userDTO);
            return new ResponseEntity<>(newUser, HttpStatus.CREATED);
        } catch (Exception e) {
            // Exibir erro no log
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Endpoint para login de usuários (gera token JWT)
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid loginUserDTO userDTO, BindingResult bindingResult) {
        // Validação de dados
        if (bindingResult.hasErrors()) {
            System.out.println("Erro na validação do JSON ou campos ausentes.");
            return ResponseEntity.badRequest().body("Erro: JSON inválido ou campos obrigatórios ausentes.");
        }

        try {
            System.out.println("Tentando autenticar usuário: " + userDTO.getUsername());

            String jwt = userService.loginUser(userDTO);
            System.out.println("Autenticação bem-sucedida, gerando token JWT.");
            return ResponseEntity.ok(new JwtResponse(jwt));

        } catch (Exception e) {
            System.out.println("Falha na autenticação: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login falhou: Credenciais inválidas.");
        }
    }

    // Endpoint para obter informações do usuário (apenas para usuários
    // autenticados)
    @GetMapping("/profile")
    public ResponseEntity<User> getProfile(HttpServletRequest request) {
        String token = jwtTokenUtil.getTokenFromRequest(request);
        if (token == null || !jwtTokenUtil.validateToken(token)) {
            System.out.println("Token inválido ou ausente");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String username = jwtTokenUtil.getUsernameFromToken(token);
        System.out.println("Usuário autenticado: " + username);
        User user = userService.getUserByUsername(username);
        return ResponseEntity.ok(user);
    }

    // Endpoint para listar todos os usuários (somente administradores)
    @GetMapping
    public ResponseEntity<?> getAllUsers() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // Somente administradores
        }
        return ResponseEntity.ok(userService.getAllUsers());
    }

    // Endpoint para atualizar um usuário
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody UserDTO userDTO) {
        User updatedUser = userService.updateUser(id, userDTO);
        return ResponseEntity.ok(updatedUser);
    }

    // Endpoint para deletar um usuário
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    // Classe para encapsular a resposta do token JWT
    public static class JwtResponse {
        private String token;

        public JwtResponse(String token) {
            this.token = token;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }
    }

}
