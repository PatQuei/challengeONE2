package com.alura.forumhub.api.controller;

import com.alura.forumhub.api.dto.auth.LoginRequestDTO;
import com.alura.forumhub.api.dto.auth.TokenResponseDTO;
import com.alura.forumhub.api.dto.user.UserRegisterDTO;
import com.alura.forumhub.api.dto.user.UserResponseDTO;
import com.alura.forumhub.api.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para autenticação e registro
 * Endpoints públicos para login e registro de usuários
 */
@Tag(name = "Autenticação", description = "Endpoints para autenticação, login e registro de usuários")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * Realiza login e retorna um token JWT
     *
     * @param loginRequest DTO com email e senha
     * @return TokenResponseDTO com o token JWT
     */
    @Operation(summary = "Acessar a plataforma", description = "Realiza a autenticação do usuário e retorna um token JWT")
    @PostMapping("/login")
    public ResponseEntity<TokenResponseDTO> login(@Valid @RequestBody LoginRequestDTO loginRequest) {
        TokenResponseDTO response = authService.login(loginRequest);
        return ResponseEntity.ok(response);
    }

    /**
     * Registra um novo usuário na plataforma
     *
     * @param registerRequest DTO com dados de registro
     * @return UserResponseDTO do usuário criado
     */
    @Operation(summary = "Registrar novo usuário", description = "Cria um novo usuário na plataforma")
    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> register(@Valid @RequestBody UserRegisterDTO registerRequest) {
        UserResponseDTO response = authService.register(registerRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
