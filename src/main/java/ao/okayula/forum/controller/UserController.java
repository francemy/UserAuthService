package ao.okayula.forum.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import ao.okayula.forum.dto.ChangePasswordDTO;
import ao.okayula.forum.dto.LoginUserDTO;
import ao.okayula.forum.dto.ResponseDTO;
import ao.okayula.forum.dto.UserDto;
import ao.okayula.forum.dto.UserFormDTO;
import ao.okayula.forum.model.PerfilModel;
import ao.okayula.forum.model.UserModel;
import ao.okayula.forum.security.JwtTokenUtil;
import ao.okayula.forum.service.PerfilService;
import ao.okayula.forum.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private PerfilService perfilService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    // Endpoint para registro de novos usuários
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserDto userDTO, BindingResult result) {
        if (result.hasErrors()) {
            StringBuilder errorMessage = new StringBuilder("Validation failed: ");
            for (FieldError error : result.getFieldErrors()) {
                errorMessage.append(error.getField())
                            .append(" ")
                            .append(error.getDefaultMessage())
                            .append("; ");
            }
            return ResponseEntity.badRequest().body(new ResponseDTO(null, errorMessage.toString()));
        }
        
        try {
            UserModel newUser = userService.registerUser(userDTO);
            return new ResponseEntity<>(newUser, HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ResponseDTO(null, "Error registering user: " + e.getMessage()));
        }
    }

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> createUser(@Valid @RequestBody UserFormDTO userFormDTO, BindingResult result) {
        if (result.hasErrors()) {
            StringBuilder errorMessage = new StringBuilder("Validation failed: ");
            for (FieldError error : result.getFieldErrors()) {
                errorMessage.append(error.getField())
                            .append(" ")
                            .append(error.getDefaultMessage())
                            .append("; ");
            }
            return ResponseEntity.badRequest().body(new ResponseDTO(null, errorMessage.toString()));
        }
        
        try {
            UserModel newUser = userService.createUserWithProfile(userFormDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ResponseDTO(null, "Error creating user with profile: " + e.getMessage()));
        }
    }

    // Endpoint para login de usuários (gera token JWT)
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginUserDTO userDTO) {
        try {
            String jwt = userService.loginUser(userDTO);
            ResponseDTO response = new ResponseDTO(jwt, "Login successful");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseDTO(null, "Invalid credentials"));
        }
    }

    @PostMapping("/changepassword")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordDTO userDTO) {
        try {
            UserModel updatedUser = userService.changePassword(userDTO);
            ResponseDTO response = new ResponseDTO(updatedUser.getEmail(), "Password change successful for " + updatedUser.getUsername());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ResponseDTO(null, "Password change failed: " + e.getMessage()));
        }
    }

    // Endpoint para obter informações do usuário (apenas para usuários autenticados)
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(HttpServletRequest request) {
        // Obtém o token do cabeçalho Authorization
        String token = jwtTokenUtil.getTokenFromRequest(request);
        if (token == null || !jwtTokenUtil.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseDTO(null, "Unauthorized access"));
        }

        String username = jwtTokenUtil.getUsernameFromToken(token);

        PerfilModel user = perfilService.buscarPorNomeUsuario(username);
        if(user == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseDTO(null, "user not found"));
        }
        return ResponseEntity.ok(new ResponseDTO(user.toString(), "authorized access"));
    }

    // Endpoint para listar todos os usuários (somente administradores)
    @GetMapping
    public ResponseEntity<?> getAllUsers() {
        // Este endpoint pode ser restrito para administradores através do Spring Security
        return ResponseEntity.ok(userService.getAllUsers());
    }

    // Endpoint para atualizar um usuário
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @Valid @RequestBody UserFormDTO userFormDTO) {
        try {
            UserModel updatedUser = userService.updateUser(id, userFormDTO);
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseDTO(null, "User not found or update failed"));
        }
    }

    // Endpoint para deletar um usuário
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseDTO(null, "User not found"));
        }
    }
}
