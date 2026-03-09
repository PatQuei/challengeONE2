package com.alura.forumhub.api.service;

import com.alura.forumhub.api.dto.topic.TopicCreateDTO;
import com.alura.forumhub.api.dto.topic.TopicDetailResponseDTO;
import com.alura.forumhub.api.dto.topic.TopicListResponseDTO;
import com.alura.forumhub.api.dto.topic.TopicUpdateDTO;
import com.alura.forumhub.domain.course.Course;
import com.alura.forumhub.domain.course.CourseRepository;
import com.alura.forumhub.domain.topic.Topic;
import com.alura.forumhub.domain.topic.TopicRepository;
import com.alura.forumhub.domain.user.User;
import com.alura.forumhub.infra.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

/**
 * Serviço de negócio para tópicos
 * Gerencia CRUD de tópicos e validações de regra de negócio
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TopicService {

    private final TopicRepository topicRepository;
    private final CourseRepository courseRepository;

    /**
     * Cria um novo tópico
     *
     * @param createDTO DTO com dados do novo tópico
     * @return TopicListResponseDTO do tópico criado
     */
    @Transactional
    public TopicDetailResponseDTO createTopic(TopicCreateDTO createDTO) {
        // Obtém o usuário autenticado
        User author = getAuthenticatedUser();

        // Busca o curso
        Course course = courseRepository.findById(createDTO.getCourseId())
                .orElseThrow(() -> ResourceNotFoundException.of("Curso", createDTO.getCourseId()));

        // Verifica se já existe um tópico com o mesmo título para o mesmo curso
        if (topicRepository.existsByTitleAndCourseId(createDTO.getTitle(), course.getId())) {
            log.warn("Tentativa de criar tópico duplicado - Título: {}, Curso: {}", 
                    createDTO.getTitle(), course.getName());
            throw new IllegalArgumentException("Este tópico já existe neste curso");
        }

        // Cria o novo tópico
        Topic topic = new Topic();
        topic.setTitle(createDTO.getTitle());
        topic.setMessage(createDTO.getMessage());
        topic.setAuthor(author);
        topic.setCourse(course);

        Topic savedTopic = topicRepository.save(topic);
        log.info("Novo tópico criado - ID: {}, Título: {}", savedTopic.getId(), savedTopic.getTitle());

        return new TopicDetailResponseDTO(savedTopic);
    }

    /**
     * Lista todos os tópicos com paginação
     *
     * @param pageable paginação
     * @return Página de TopicListResponseDTO
     */
    @Transactional(readOnly = true)
    public Page<TopicListResponseDTO> listTopics(Pageable pageable) {
        Page<Topic> topicsPage = topicRepository.findAllActive(pageable);
        return topicsPage.map(TopicListResponseDTO::new);
    }

    /**
     * Busca um tópico específico pelo ID
     *
     * @param id ID do tópico
     * @return TopicDetailResponseDTO com detalhes completos
     */
    @Transactional(readOnly = true)
    public TopicDetailResponseDTO getTopicById(Long id) {
        Topic topic = topicRepository.findByIdAndNotDeleted(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Tópico", id));

        return new TopicDetailResponseDTO(topic);
    }

    /**
     * Atualiza um tópico existente
     * Apenas o autor ou admin podem atualizar
     *
     * @param id ID do tópico
     * @param updateDTO DTO com dados atualizados
     * @return TopicDetailResponseDTO com tópico atualizado
     */
    @Transactional
    public TopicDetailResponseDTO updateTopic(Long id, TopicUpdateDTO updateDTO) {
        User authenticatedUser = getAuthenticatedUser();
        Topic topic = topicRepository.findByIdAndNotDeleted(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Tópico", id));

        // Verifica se o usuário é o autor do tópico ou admin
        if (!topic.getAuthor().getId().equals(authenticatedUser.getId()) && 
            !authenticatedUser.getRole().name().equals("ADMIN")) {
            log.warn("Usuário {} tentou atualizar tópico de outro usuário", authenticatedUser.getEmail());
            throw new IllegalArgumentException("Apenas o autor do tópico ou um admin pode atualizá-lo");
        }

        // Atualiza os campos
        if (updateDTO.getTitle() != null) {
            topic.setTitle(updateDTO.getTitle());
        }
        if (updateDTO.getMessage() != null) {
            topic.setMessage(updateDTO.getMessage());
        }
        if (updateDTO.getStatus() != null) {
            topic.setStatus(updateDTO.getStatus());
        }

        Topic updatedTopic = topicRepository.save(topic);
        log.info("Tópico atualizado - ID: {}", updatedTopic.getId());

        return new TopicDetailResponseDTO(updatedTopic);
    }

    /**
     * Deleta um tópico (exclusão lógica)
     * Apenas o autor ou admin podem deletar
     *
     * @param id ID do tópico a deletar
     */
    @Transactional
    public void deleteTopic(Long id) {
        User authenticatedUser = getAuthenticatedUser();
        Topic topic = topicRepository.findByIdAndNotDeleted(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Tópico", id));

        // Verifica se o usuário é o autor do tópico ou admin
        if (!topic.getAuthor().getId().equals(authenticatedUser.getId()) && 
            !authenticatedUser.getRole().name().equals("ADMIN")) {
            log.warn("Usuário {} tentou deletar tópico de outro usuário", authenticatedUser.getEmail());
            throw new IllegalArgumentException("Apenas o autor do tópico ou um admin podem deletá-lo");
        }

        // Realiza soft delete
        topic.softDelete();
        topicRepository.save(topic);
        log.info("Tópico deletado - ID: {}", id);
    }

    /**
     * Obtém o usuário autenticado do contexto de segurança
     *
     * @return User autenticado
     */
    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalArgumentException("Usuário não autenticado");
        }
        return (User) authentication.getPrincipal();
    }
}
