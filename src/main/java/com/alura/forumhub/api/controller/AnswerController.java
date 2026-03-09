package com.alura.forumhub.api.controller;

import com.alura.forumhub.api.dto.answer.AnswerCreateDTO;
import com.alura.forumhub.api.dto.answer.AnswerResponseDTO;
import com.alura.forumhub.api.dto.answer.AnswerUpdateDTO;
import com.alura.forumhub.api.service.AnswerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para gerenciar respostas a tópicos
 * Implementa CRUD de respostas (answers)
 */
@Tag(name = "Respostas", description = "Endpoints para gerenciar respostas em tópicos")
@RestController
@RequestMapping("/answers")
@RequiredArgsConstructor
public class AnswerController {

    private final AnswerService answerService;

    /**
     * Cria uma nova resposta para um tópico
     * Requer autenticação
     *
     * @param createDTO DTO com dados da nova resposta
     * @return AnswerResponseDTO da resposta criada
     */
    @Operation(
            summary = "Criar nova resposta",
            description = "Cria uma nova resposta para um tópico",
            security = @SecurityRequirement(name = "Bearer Token")
    )
    @PostMapping
    public ResponseEntity<AnswerResponseDTO> createAnswer(@Valid @RequestBody AnswerCreateDTO createDTO) {
        AnswerResponseDTO response = answerService.createAnswer(createDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Lista todas as respostas de um tópico
     * Públicos (sem autenticação necessária)
     *
     * @param topicId ID do tópico
     * @param page número da página (padrão: 0)
     * @param size tamanho da página (padrão: 10)
     * @return Página de AnswerResponseDTO
     */
    @Operation(summary = "Listar respostas de um tópico", description = "Lista todas as respostas de um tópico específico")
    @GetMapping("/topic/{topicId}")
    public ResponseEntity<Page<AnswerResponseDTO>> listAnswersByTopic(
            @PathVariable Long topicId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("createdAt").ascending());
        Page<AnswerResponseDTO> response = answerService.listAnswersByTopic(topicId, pageRequest);
        return ResponseEntity.ok(response);
    }

    /**
     * Obtém uma resposta específica pelo ID
     * Públicos (sem autenticação necessária)
     *
     * @param id ID da resposta
     * @return AnswerResponseDTO
     */
    @Operation(summary = "Obter detalhes da resposta", description = "Retorna os detalhes de uma resposta específica")
    @GetMapping("/{id}")
    public ResponseEntity<AnswerResponseDTO> getAnswerById(@PathVariable Long id) {
        AnswerResponseDTO response = answerService.getAnswerById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Atualiza uma resposta existente
     * Requer autenticação
     * Apenas o autor ou admin podem atualizar
     *
     * @param id ID da resposta
     * @param updateDTO DTO com dados atualizados
     * @return AnswerResponseDTO atualizada
     */
    @Operation(
            summary = "Atualizar resposta",
            description = "Atualiza uma resposta existente (apenas autor ou admin)",
            security = @SecurityRequirement(name = "Bearer Token")
    )
    @PutMapping("/{id}")
    public ResponseEntity<AnswerResponseDTO> updateAnswer(
            @PathVariable Long id,
            @Valid @RequestBody AnswerUpdateDTO updateDTO) {

        AnswerResponseDTO response = answerService.updateAnswer(id, updateDTO);
        return ResponseEntity.ok(response);
    }

    /**
     * Deleta uma resposta
     * Requer autenticação
     * Apenas o autor ou admin podem deletar
     *
     * @param id ID da resposta
     * @return ResponseEntity sem conteúdo
     */
    @Operation(
            summary = "Deletar resposta",
            description = "Deleta uma resposta (apenas autor ou admin)",
            security = @SecurityRequirement(name = "Bearer Token")
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAnswer(@PathVariable Long id) {
        answerService.deleteAnswer(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Marca uma resposta como solução para o tópico
     * Requer autenticação
     * Apenas o autor do tópico pode marcar
     *
     * @param answerId ID da resposta
     * @return AnswerResponseDTO marcada como solução
     */
    @Operation(
            summary = "Marcar como solução",
            description = "Marca uma resposta como solução do tópico (apenas autor do tópico)",
            security = @SecurityRequirement(name = "Bearer Token")
    )
    @PostMapping("/{answerId}/solution")
    public ResponseEntity<AnswerResponseDTO> markAsSolution(@PathVariable Long answerId) {
        AnswerResponseDTO response = answerService.markAsSolution(answerId);
        return ResponseEntity.ok(response);
    }
}
