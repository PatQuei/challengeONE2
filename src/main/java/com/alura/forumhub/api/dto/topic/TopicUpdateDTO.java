package com.alura.forumhub.api.dto.topic;

import com.alura.forumhub.domain.topic.TopicStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para atualização de um tópico existente
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TopicUpdateDTO {

    @NotBlank(message = "Título é obrigatório")
    private String title;

    @NotBlank(message = "Mensagem é obrigatória")
    private String message;

    private TopicStatus status;
}
