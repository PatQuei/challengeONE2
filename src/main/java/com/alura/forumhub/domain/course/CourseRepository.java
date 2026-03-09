package com.alura.forumhub.domain.course;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositório para operações com entidade Course
 */
@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    /**
     * Busca um curso pelo nome
     * @param name nome do curso
     * @return Optional contendo o curso se encontrado
     */
    Optional<Course> findByName(String name);

    /**
     * Verifica se um curso com o nome especificado existe
     * @param name nome do curso
     * @return true se existe, false caso contrário
     */
    boolean existsByName(String name);
}
