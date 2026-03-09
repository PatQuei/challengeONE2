package com.alura.forumhub.domain.answer;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repositório para operações com entidade Answer
 */
@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {

    /**
     * Busca todas as respostas para um tópico
     * @param topicId ID do tópico
     * @param pageable paginação
     * @return página de respostas
     */
    @Query("SELECT a FROM Answer a WHERE a.topic.id = :topicId ORDER BY a.isSolution DESC, a.createdAt ASC")
    Page<Answer> findByTopicId(@Param("topicId") Long topicId, Pageable pageable);

    /**
     * Busca a resposta marcada como solução para um tópico
     * @param topicId ID do tópico
     * @return resposta marcada como solução, se houver
     */
    @Query("SELECT a FROM Answer a WHERE a.topic.id = :topicId AND a.isSolution = true")
    Answer findSolutionByTopicId(@Param("topicId") Long topicId);
}
