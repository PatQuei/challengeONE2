package com.alura.forumhub.api.controller;

import com.alura.forumhub.api.dto.topic.TopicCreateDTO;
import com.alura.forumhub.api.dto.topic.TopicDetailResponseDTO;
import com.alura.forumhub.api.dto.topic.TopicListResponseDTO;
import com.alura.forumhub.api.dto.topic.TopicUpdateDTO;
import com.alura.forumhub.api.service.TopicService;
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
 * Controlador REST para gerenciar tópicos do fórum
 * Implementa CRUD completo de tópicos
 */
@Tag(name = "Tópicos", description = "Endpoints para gerenciar tópicos do fórum")
@RestController
@RequestMapping("/topicos")
@RequiredArgsConstructor
public class TopicController {

    private final TopicService topicService;

    /**
     * Cria um novo tópico
     * Requer autenticação
     *
     * @param createDTO DTO com dados do novo tópico
     * @return TopicDetailResponseDTO do tópico criado
     */
    @Operation(
            summary = "Criar novo tópico",
            description = "Cria um novo tópico no fórum",
            security = @SecurityRequirement(name = "Bearer Token")
    )
    @PostMapping
    public ResponseEntity<TopicDetailResponseDTO> createTopic(@Valid @RequestBody TopicCreateDTO createDTO) {
        TopicDetailResponseDTO response = topicService.createTopic(createDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Lista todos os tópicos com paginação
     * Públicos (sem autenticação necessária)
     *
     * @param page número da página (padrão: 0)
     * @param size tamanho da página (padrão: 10)
     * @param sortBy campo para ordenação (padrão: createdAt)
     * @return Página de TopicListResponseDTO
     */
    @Operation(summary = "Listar tópicos", description = "Lista todos os tópicos do fórum com paginação")
    @GetMapping
    public ResponseEntity<Page<TopicListResponseDTO>> listTopics(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy) {

        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(sortBy).descending());
        Page<TopicListResponseDTO> response = topicService.listTopics(pageRequest);
        return ResponseEntity.ok(response);
    }

    /**
     * Obtém um tópico específico pelo ID
     * Públicos (sem autenticação necessária)
     *
     * @param id ID do tópico
     * @return TopicDetailResponseDTO com detalhes completos
     */
    @Operation(summary = "Obter detalhes do tópico", description = "Retorna os detalhes completos de um tópico")
    @GetMapping("/{id}")
    public ResponseEntity<TopicDetailResponseDTO> getTopicById(@PathVariable Long id) {
        TopicDetailResponseDTO response = topicService.getTopicById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Atualiza um tópico existente
     * Requer autenticação
     * Apenas o autor ou admin podem atualizar
     *
     * @param id ID do tópico
     * @param updateDTO DTO com dados atualizados
     * @return TopicDetailResponseDTO com tópico atualizado
     */
    @Operation(
            summary = "Atualizar tópico",
            description = "Atualiza um tópico existente (apenas autor ou admin)",
            security = @SecurityRequirement(name = "Bearer Token")
    )
    @PutMapping("/{id}")
    public ResponseEntity<TopicDetailResponseDTO> updateTopic(
            @PathVariable Long id,
            @Valid @RequestBody TopicUpdateDTO updateDTO) {

        TopicDetailResponseDTO response = topicService.updateTopic(id, updateDTO);
        return ResponseEntity.ok(response);
    }

    /**
     * Deleta um tópico
     * Requer autenticação
     * Apenas o autor ou admin podem deletar
     * Realiza exclusão lógica (soft delete)
     *
     * @param id ID do tópico
     * @return ResponseEntity sem conteúdo
     */
    @Operation(
            summary = "Deletar tópico",
            description = "Deleta um tópico (apenas autor ou admin). Realiza exclusão lógica",
            security = @SecurityRequirement(name = "Bearer Token")
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTopic(@PathVariable Long id) {
        topicService.deleteTopic(id);
        return ResponseEntity.noContent().build();
    }
}
