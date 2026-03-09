package com.alura.forumhub.api.service;

import com.alura.forumhub.api.dto.answer.AnswerCreateDTO;
import com.alura.forumhub.api.dto.answer.AnswerResponseDTO;
import com.alura.forumhub.api.dto.answer.AnswerUpdateDTO;
import com.alura.forumhub.domain.answer.Answer;
import com.alura.forumhub.domain.answer.AnswerRepository;
import com.alura.forumhub.domain.topic.Topic;
import com.alura.forumhub.domain.topic.TopicRepository;
import com.alura.forumhub.domain.user.User;
import com.alura.forumhub.infra.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Serviço de negócio para respostas
 * Gerencia CRUD de respostas em tópicos
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AnswerService {

    private final AnswerRepository answerRepository;
    private final TopicRepository topicRepository;

    /**
     * Cria uma nova resposta para um tópico
     *
     * @param createDTO DTO com dados da nova resposta
     * @return AnswerResponseDTO da resposta criada
     */
    @Transactional
    public AnswerResponseDTO createAnswer(AnswerCreateDTO createDTO) {
        User author = getAuthenticatedUser();

        // Busca o tópico
        Topic topic = topicRepository.findByIdAndNotDeleted(createDTO.getTopicId())
                .orElseThrow(() -> ResourceNotFoundException.of("Tópico", createDTO.getTopicId()));

        // Cria a nova resposta
        Answer answer = new Answer();
        answer.setMessage(createDTO.getMessage());
        answer.setAuthor(author);
        answer.setTopic(topic);
        answer.setIsSolution(false);

        Answer savedAnswer = answerRepository.save(answer);
        log.info("Nova resposta criada - ID: {}, Tópico: {}", savedAnswer.getId(), topic.getId());

        return new AnswerResponseDTO(savedAnswer);
    }

    /**
     * Lista todas as respostas de um tópico com paginação
     *
     * @param topicId ID do tópico
     * @param pageable paginação
     * @return Página de AnswerResponseDTO
     */
    @Transactional(readOnly = true)
    public Page<AnswerResponseDTO> listAnswersByTopic(Long topicId, Pageable pageable) {
        // Verifica se o tópico existe
        if (!topicRepository.existsById(topicId)) {
            throw ResourceNotFoundException.of("Tópico", topicId);
        }

        Page<Answer> answersPage = answerRepository.findByTopicId(topicId, pageable);
        return answersPage.map(AnswerResponseDTO::new);
    }

    /**
     * Obtém uma resposta específica pelo ID
     *
     * @param id ID da resposta
     * @return AnswerResponseDTO
     */
    @Transactional(readOnly = true)
    public AnswerResponseDTO getAnswerById(Long id) {
        Answer answer = answerRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Resposta", id));

        return new AnswerResponseDTO(answer);
    }

    /**
     * Atualiza uma resposta existente
     * Apenas o autor ou admin podem atualizar
     *
     * @param id ID da resposta
     * @param updateDTO DTO com dados atualizados
     * @return AnswerResponseDTO atualizada
     */
    @Transactional
    public AnswerResponseDTO updateAnswer(Long id, AnswerUpdateDTO updateDTO) {
        User authenticatedUser = getAuthenticatedUser();
        Answer answer = answerRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Resposta", id));

        // Verifica se o usuário é o autor da resposta ou admin
        if (!answer.getAuthor().getId().equals(authenticatedUser.getId()) && 
            !authenticatedUser.getRole().name().equals("ADMIN")) {
            log.warn("Usuário {} tentou atualizar resposta de outro usuário", authenticatedUser.getEmail());
            throw new IllegalArgumentException("Apenas o autor da resposta ou um admin podem atualizá-la");
        }

        answer.setMessage(updateDTO.getMessage());
        Answer updatedAnswer = answerRepository.save(answer);
        log.info("Resposta atualizada - ID: {}", updatedAnswer.getId());

        return new AnswerResponseDTO(updatedAnswer);
    }

    /**
     * Deleta uma resposta
     * Apenas o autor ou admin podem deletar
     *
     * @param id ID da resposta a deletar
     */
    @Transactional
    public void deleteAnswer(Long id) {
        User authenticatedUser = getAuthenticatedUser();
        Answer answer = answerRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Resposta", id));

        // Verifica se o usuário é o autor da resposta ou admin
        if (!answer.getAuthor().getId().equals(authenticatedUser.getId()) && 
            !authenticatedUser.getRole().name().equals("ADMIN")) {
            log.warn("Usuário {} tentou deletar resposta de outro usuário", authenticatedUser.getEmail());
            throw new IllegalArgumentException("Apenas o autor da resposta ou um admin podem deletá-la");
        }

        answerRepository.delete(answer);
        log.info("Resposta deletada - ID: {}", id);
    }

    /**
     * Marca uma resposta como solução para o tópico
     * Apenas o autor do tópico pode marcar uma resposta como solução
     *
     * @param answerId ID da resposta a marcar como solução
     */
    @Transactional
    public AnswerResponseDTO markAsSolution(Long answerId) {
        User authenticatedUser = getAuthenticatedUser();
        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> ResourceNotFoundException.of("Resposta", answerId));

        Topic topic = answer.getTopic();

        // Verifica se o usuário é o autor do tópico
        if (!topic.getAuthor().getId().equals(authenticatedUser.getId())) {
            throw new IllegalArgumentException("Apenas o autor do tópico pode marcar uma resposta como solução");
        }

        // Desmarca a resposta anterior como solução (se houver)
        Answer currentSolution = answerRepository.findSolutionByTopicId(topic.getId());
        if (currentSolution != null && !currentSolution.getId().equals(answerId)) {
            currentSolution.unmarkAsSolution();
            answerRepository.save(currentSolution);
        }

        // Marca a nova resposta como solução
        answer.markAsSolution();
        Answer updatedAnswer = answerRepository.save(answer);
        log.info("Resposta marcada como solução - ID: {}", answerId);

        return new AnswerResponseDTO(updatedAnswer);
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
