package com.alura.forumhub.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositório para operações com entidade User
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Busca um usuário pelo email
     * @param email email do usuário
     * @return Optional contendo o usuário se encontrado
     */
    Optional<User> findByEmail(String email);

    /**
     * Busca um UserDetails pelo email (para Spring Security)
     * @param email email do usuário
     * @return UserDetails do usuário se encontrado
     */
    UserDetails findByEmailForSecurity(String email);

    /**
     * Verifica se um email já está registrado
     * @param email email a verificar
     * @return true se email existe, false caso contrário
     */
    boolean existsByEmail(String email);
}
