package com.exemplo.usermanagement.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.exemplo.usermanagement.dto.JwtResponseDTO;
import com.exemplo.usermanagement.dto.UserDTO;
import com.exemplo.usermanagement.dto.loginUserDTO;
import com.exemplo.usermanagement.exception.InvalidPasswordException;
import com.exemplo.usermanagement.exception.UserNotFoundException;
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
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body("Erro: JSON inválido ou campos obrigatórios ausentes.");
        }

        try {
            String jwt = userService.loginUser(userDTO);
            return ResponseEntity.ok(new JwtResponseDTO(jwt));
        } catch (InvalidPasswordException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Senha inválida.");
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário não encontrado.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao tentar autenticar o usuário.");
        }
    }

    // Endpoint para obter informações do usuário (apenas para usuários autenticados)
    @GetMapping("/profile")
    public ResponseEntity<User> getProfile(HttpServletRequest request) {
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
    public ResponseEntity<?> getAllUsers(@RequestHeader(value = "Authorization") String authorizationHeader,
                                         Principal principal) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token JWT ausente ou malformado.");
        }

        String token = authorizationHeader.substring(7); // Remover "Bearer " do início do token
        if (!jwtTokenUtil.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Token inválido.");
        }

        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuário não autenticado.");
        }

        Authentication authentication = (Authentication) principal;
        if (!authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Acesso negado. Somente administradores podem acessar.");
        }

        try {
            var users = userService.getAllUsers();
            if (users == null || users.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Nenhum usuário encontrado.");
            }
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao tentar buscar os usuários: " + e.getMessage());
        }
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
}
