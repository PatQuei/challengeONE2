package com.alura.forumhub.api.dto.topic;

import com.alura.forumhub.api.dto.answer.AnswerResponseDTO;
import com.alura.forumhub.domain.topic.Topic;
import com.alura.forumhub.domain.topic.TopicStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * DTO para resposta detalhada de um tópico (inclui respostas)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TopicDetailResponseDTO {

    private Long id;
    private String title;
    private String message;
    private String courseName;
    private String authorName;
    private TopicStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<AnswerResponseDTO> answers;

    public TopicDetailResponseDTO(Topic topic) {
        this.id = topic.getId();
        this.title = topic.getTitle();
        this.message = topic.getMessage();
        this.courseName = topic.getCourse().getName();
        this.authorName = topic.getAuthor().getName();
        this.status = topic.getStatus();
        this.createdAt = topic.getCreatedAt();
        this.updatedAt = topic.getUpdatedAt();
        this.answers = topic.getAnswers() != null
                ? topic.getAnswers().stream()
                .map(AnswerResponseDTO::new)
                .collect(Collectors.toList())
                : List.of();
    }
}
