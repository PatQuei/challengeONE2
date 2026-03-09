package com.alura.forumhub.api.dto.topic;

import com.alura.forumhub.domain.topic.Topic;
import com.alura.forumhub.domain.topic.TopicStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO para resposta listando tópico (informações resumidas)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TopicListResponseDTO {

    private Long id;
    private String title;
    private String courseName;
    private String authorName;
    private TopicStatus status;
    private LocalDateTime createdAt;
    private Integer answerCount;

    public TopicListResponseDTO(Topic topic) {
        this.id = topic.getId();
        this.title = topic.getTitle();
        this.courseName = topic.getCourse().getName();
        this.authorName = topic.getAuthor().getName();
        this.status = topic.getStatus();
        this.createdAt = topic.getCreatedAt();
        this.answerCount = topic.getAnswers() != null ? topic.getAnswers().size() : 0;
    }
}
