package com.alura.forumhub.api.dto.topic;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para criação de novo tópico
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TopicCreateDTO {

    @NotBlank(message = "Título é obrigatório")
    private String title;

    @NotBlank(message = "Mensagem é obrigatória")
    private String message;

    @NotNull(message = "ID do curso é obrigatório")
    private Long courseId;
}
