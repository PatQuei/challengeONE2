package com.alura.forumhub.api.controller;

import com.alura.forumhub.api.dto.auth.LoginRequestDTO;
import com.alura.forumhub.api.dto.user.UserRegisterDTO;
import com.alura.forumhub.domain.user.UserRole;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Testes de integração para o AuthController
 * Testa endpoints de autenticação e registro
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("AuthController - Testes de Integração")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Deve registrar novo usuário com sucesso")
    void testRegisterSuccess() throws Exception {
        UserRegisterDTO registerRequest = new UserRegisterDTO(
                "Test User",
                "test@example.com",
                "password123",
                UserRole.USER
        );

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("Test User"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    @DisplayName("Deve falhar ao registrar com email duplicado")
    void testRegisterDuplicateEmail() throws Exception {
        UserRegisterDTO registerRequest1 = new UserRegisterDTO(
                "User 1",
                "duplicate@example.com",
                "password123",
                UserRole.USER
        );

        // Primeiro registro
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest1)))
                .andExpect(status().isCreated());

        // Segundo registro com mesmo email
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest1)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve fazer login e retornar token JWT")
    void testLoginSuccess() throws Exception {
        // Primeiro registra um usuário
        UserRegisterDTO registerRequest = new UserRegisterDTO(
                "Login Test",
                "login@example.com",
                "password123",
                UserRole.USER
        );

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated());

        // Tenta fazer login
        LoginRequestDTO loginRequest = new LoginRequestDTO(
                "login@example.com",
                "password123"
        );

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isString())
                .andExpect(jsonPath("$.type").value("Bearer"))
                .andExpect(jsonPath("$.expiresIn").isNumber());
    }

    @Test
    @DisplayName("Deve falhar ao fazer login com senha incorreta")
    void testLoginInvalidPassword() throws Exception {
        LoginRequestDTO loginRequest = new LoginRequestDTO(
                "admin@forumhub.com",
                "wrongpassword"
        );

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Deve falhar ao fazer login com email não registrado")
    void testLoginInvalidEmail() throws Exception {
        LoginRequestDTO loginRequest = new LoginRequestDTO(
                "nonexistent@example.com",
                "password123"
        );

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Deve validar campos obrigatórios no registro")
    void testRegisterValidation() throws Exception {
        UserRegisterDTO invalidRequest = new UserRegisterDTO(
                "",  // nome em branco
                "invalid-email",  // email inválido
                "",  // senha em branco
                null
        );

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }
}
