package com.alura.forumhub.domain.answer;

import com.alura.forumhub.domain.topic.Topic;
import com.alura.forumhub.domain.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entidade de Resposta
 * Representa uma resposta a um tópico do fórum
 */
@Entity
@Table(name = "answers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Answer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(columnDefinition = "TEXT")
    private String message;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "topic_id", nullable = false)
    private Topic topic;

    /**
     * Indica se esta resposta foi marcada como solução pelo autor do tópico
     */
    @Column(name = "is_solution")
    private Boolean isSolution = false;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * Marca esta resposta como solução
     */
    public void markAsSolution() {
        this.isSolution = true;
    }

    /**
     * Desmarcar essa resposta como solução
     */
    public void unmarkAsSolution() {
        this.isSolution = false;
    }
}
