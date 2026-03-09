package com.alura.forumhub.api.dto.answer;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para criação de nova resposta
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnswerCreateDTO {

    @NotBlank(message = "Mensagem é obrigatória")
    private String message;

    @NotNull(message = "ID do tópico é obrigatório")
    private Long topicId;
}
