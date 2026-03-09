package com.alura.forumhub.api.service;

import com.alura.forumhub.api.dto.auth.LoginRequestDTO;
import com.alura.forumhub.api.dto.auth.TokenResponseDTO;
import com.alura.forumhub.api.dto.user.UserRegisterDTO;
import com.alura.forumhub.api.dto.user.UserResponseDTO;
import com.alura.forumhub.domain.user.User;
import com.alura.forumhub.domain.user.UserRepository;
import com.alura.forumhub.security.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Serviço de autenticação e autorização
 * Gerencia login, registro e geração de tokens JWT
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    /**
     * Realiza a autenticação de um usuário e retorna um token JWT
     *
     * @param loginRequest DTO contendo email e senha
     * @return TokenResponseDTO com o JWT gerado
     * @throws BadCredentialsException se as credenciais forem inválidas
     */
    public TokenResponseDTO login(LoginRequestDTO loginRequest) {
        log.info("Tentativa de login para email: {}", loginRequest.getEmail());

        try {
            // Autentica o usuário usando AuthenticationManager
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );

            // Extrai o usuário autenticado
            User user = (User) authentication.getPrincipal();

            // Gera o token JWT
            String token = jwtService.generateToken(user);
            long expirationMs = jwtService.getExpirationTimeMs();

            log.info("Login bem-sucedido para usuário: {}", loginRequest.getEmail());

            return new TokenResponseDTO(token, expirationMs, user.getEmail());

        } catch (Exception e) {
            log.warn("Falha na autenticação para email: {}", loginRequest.getEmail());
            throw new BadCredentialsException("Email ou senha inválidos");
        }
    }

    /**
     * Registra um novo usuário na plataforma
     *
     * @param registerRequest DTO contendo dados de registro
     * @return UserResponseDTO do usuário criado
     * @throws IllegalArgumentException se o email já estiver registrado
     */
    public UserResponseDTO register(UserRegisterDTO registerRequest) {
        // Verifica se o email já existe
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            log.warn("Tentativa de registro com email duplicado: {}", registerRequest.getEmail());
            throw new IllegalArgumentException("Email já registrado");
        }

        // Cria novo usuário
        User user = new User();
        user.setName(registerRequest.getName());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setRole(registerRequest.getRole());
        user.setIsActive(true);

        // Salva no banco
        User savedUser = userRepository.save(user);
        log.info("Novo usuário registrado: {}", savedUser.getEmail());

        return new UserResponseDTO(savedUser);
    }
}
