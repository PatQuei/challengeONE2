package com.alura.forumhub.api.dto.answer;

import com.alura.forumhub.domain.answer.Answer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO para resposta de dados da resposta (Answer)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnswerResponseDTO {

    private Long id;
    private String message;
    private String authorName;
    private Boolean isSolution;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public AnswerResponseDTO(Answer answer) {
        this.id = answer.getId();
        this.message = answer.getMessage();
        this.authorName = answer.getAuthor().getName();
        this.isSolution = answer.getIsSolution();
        this.createdAt = answer.getCreatedAt();
        this.updatedAt = answer.getUpdatedAt();
    }
}
