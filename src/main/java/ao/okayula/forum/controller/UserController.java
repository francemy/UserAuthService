package ao.okayula.forum.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;

import ao.okayula.forum.dto.ChangePasswordDTO;
import ao.okayula.forum.security.JwtTokenUtil;
import ao.okayula.forum.service.UserService;
import ao.okayula.forum.dto.ResponseDTO;
import ao.okayula.forum.dto.LoginUserDTO;
import ao.okayula.forum.dto.UserDto;
import ao.okayula.forum.model.UserModel;
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
    public ResponseEntity<UserModel> registerUser(@RequestBody UserDto userDTO) {
        try {
            UserModel newUser = userService.registerUser(userDTO);
            return new ResponseEntity<UserModel>(newUser, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<UserModel>(HttpStatus.BAD_REQUEST);
        }
    }

    // Endpoint para login de usuários (gera token JWT)
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginUserDTO userDTO) {
        String jwt = userService.loginUser(userDTO);
        ResponseDTO response = new ResponseDTO(jwt, "Login successful");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/changepassword")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordDTO userDTO) {
        try {
            UserModel updatedUser = userService.changePassword(userDTO);
            ResponseDTO response = new ResponseDTO(updatedUser.getEmail(), "change password "+updatedUser.getUsername()+" successful");
            return ResponseEntity.ok(response); // Retorna o usuário atualizado
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage()); // Retorna erro se houver
        }
    }

    // Endpoint para obter informações do usuário (apenas para usuários autenticados)
    @GetMapping("/profile")
    public ResponseEntity<UserModel> getProfile(HttpServletRequest request) {
        // Obtém o token do cabeçalho Authorization
        String token = jwtTokenUtil.getTokenFromRequest(request);
        if (token == null || !jwtTokenUtil.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String username = jwtTokenUtil.getUsernameFromToken(token);
        UserModel user = userService.getUserByUsername(username);
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
    public ResponseEntity<UserModel> updateUser(@PathVariable String id, @RequestBody UserDto userDTO) {
        UserModel updatedUser = userService.updateUser(id, userDTO);
        return ResponseEntity.ok(updatedUser);
    }

    // Endpoint para deletar um usuário
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
