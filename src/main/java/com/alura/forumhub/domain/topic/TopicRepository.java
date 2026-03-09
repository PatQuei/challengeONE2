package com.alura.forumhub.domain.topic;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositório para operações com entidade Topic
 */
@Repository
public interface TopicRepository extends JpaRepository<Topic, Long> {

    /**
     * Pagina através de todos os tópicos ativos (não deletados)
     * @param pageable paginação
     * @return página de tópicos
     */
    @Query("SELECT t FROM Topic t WHERE t.status != 'DELETED' ORDER BY t.createdAt DESC")
    Page<Topic> findAllActive(Pageable pageable);

    /**
     * Busca tópicos por curso
     * @param courseId ID do curso
     * @param pageable paginação
     * @return página de tópicos do curso
     */
    @Query("SELECT t FROM Topic t WHERE t.course.id = :courseId AND t.status != 'DELETED'")
    Page<Topic> findByCourseId(@Param("courseId") Long courseId, Pageable pageable);

    /**
     * Busca tópicos por status
     * @param status status do tópico
     * @param pageable paginação
     * @return página de tópicos com esse status
     */
    @Query("SELECT t FROM Topic t WHERE t.status = :status AND t.status != 'DELETED'")
    Page<Topic> findByStatus(@Param("status") TopicStatus status, Pageable pageable);

    /**
     * Verifica se um tópico com o mesmo título já existe para um curso
     * @param title título do tópico
     * @param courseId ID do curso
     * @return true se existe, false caso contrário
     */
    @Query("SELECT CASE WHEN COUNT(t) > 0 THEN true ELSE false END FROM Topic t WHERE t.title = :title AND t.course.id = :courseId AND t.status != 'DELETED'")
    boolean existsByTitleAndCourseId(@Param("title") String title, @Param("courseId") Long courseId);

    /**
     * Busca um tópico pelo ID, excluindo os deletados
     * @param id ID do tópico
     * @return Optional contendo o tópico se encontrado e não deletado
     */
    @Query("SELECT t FROM Topic t WHERE t.id = :id AND t.status != 'DELETED'")
    Optional<Topic> findByIdAndNotDeleted(@Param("id") Long id);
}
