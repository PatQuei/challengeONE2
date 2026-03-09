package com.alura.forumhub.api.controller;

import com.alura.forumhub.api.dto.topic.TopicCreateDTO;
import com.alura.forumhub.api.dto.user.UserRegisterDTO;
import com.alura.forumhub.domain.course.Course;
import com.alura.forumhub.domain.course.CourseRepository;
import com.alura.forumhub.domain.user.UserRole;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Testes de integração para o TopicController
 * Testa endpoints de CRUD de tópicos
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("TopicController - Testes de Integração")
class TopicControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CourseRepository courseRepository;

    private String token;
    private Long courseId;

    @BeforeEach
    void setUp() throws Exception {
        // Registra usuário
        UserRegisterDTO registerRequest = new UserRegisterDTO(
                "Topic Test User",
                "topictest@example.com",
                "password123",
                UserRole.USER
        );

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated());

        // Faz login para obter token
        LoginRequestDTOWrapper loginRequest = new LoginRequestDTOWrapper(
                "topictest@example.com",
                "password123"
        );

        var result = mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        var responseBody = result.getResponse().getContentAsString();
        token = objectMapper.readTree(responseBody).get("token").asText();

        // Cria curso para testes
        Course course = new Course();
        course.setName("Test Course for Topics");
        course.setCategory("Backend");
        course.setIsActive(true);
        Course savedCourse = courseRepository.save(course);
        courseId = savedCourse.getId();
    }

    @Test
    @DisplayName("Deve listar tópicos sem autenticação")
    void testListTopicsPublic() throws Exception {
        mockMvc.perform(get("/topicos")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @DisplayName("Deve criar novo tópico com autenticação")
    void testCreateTopicSuccess() throws Exception {
        TopicCreateDTO createDTO = new TopicCreateDTO(
                "Test Topic Title",
                "Test Topic Message",
                courseId
        );

        mockMvc.perform(post("/topicos")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.title").value("Test Topic Title"))
                .andExpect(jsonPath("$.status").value("OPEN"));
    }

    @Test
    @DisplayName("Deve falhar ao criar tópico sem autenticação")
    void testCreateTopicWithoutAuth() throws Exception {
        TopicCreateDTO createDTO = new TopicCreateDTO(
                "Test Topic",
                "Test Message",
                courseId
        );

        mockMvc.perform(post("/topicos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Deve falhar ao criar tópico duplicado")
    void testCreateDuplicateTopic() throws Exception {
        TopicCreateDTO createDTO = new TopicCreateDTO(
                "Duplicate Topic",
                "Message",
                courseId
        );

        // Criar primeiro tópico
        mockMvc.perform(post("/topicos")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isCreated());

        // Tentar criar segundo com mesmo título e curso
        mockMvc.perform(post("/topicos")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve obter um tópico específico")
    void testGetTopicById() throws Exception {
        // Cria tópico
        TopicCreateDTO createDTO = new TopicCreateDTO(
                "Detail Topic",
                "Detail Message",
                courseId
        );

        var createResult = mockMvc.perform(post("/topicos")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isCreated())
                .andReturn();

        var responseBody = createResult.getResponse().getContentAsString();
        Long topicId = objectMapper.readTree(responseBody).get("id").asLong();

        // Busca tópico
        mockMvc.perform(get("/topicos/" + topicId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(topicId))
                .andExpect(jsonPath("$.title").value("Detail Topic"))
                .andExpect(jsonPath("$.answers").isArray());
    }

    // DTO wrapper para login (auxiliar)
    static class LoginRequestDTOWrapper {
        public String email;
        public String password;

        LoginRequestDTOWrapper(String email, String password) {
            this.email = email;
            this.password = password;
        }
    }
}
