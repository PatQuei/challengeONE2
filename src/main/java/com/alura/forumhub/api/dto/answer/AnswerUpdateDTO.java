package com.alura.forumhub.api.dto.answer;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para atualização de uma resposta existente
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnswerUpdateDTO {

    @NotBlank(message = "Mensagem é obrigatória")
    private String message;
}
