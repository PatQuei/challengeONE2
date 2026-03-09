package com.alura.forumhub.domain.user;

/**
 * Enum que define os papéis (roles) dos usuários no Fórum Hub
 */
public enum UserRole {
    /**
     * Usuário comum com permissão de criar e comentar tópicos
     */
    USER,

    /**
     * Moderador com permissão de gerenciar conteúdo
     */
    MODERATOR,

    /**
     * Administrador com permissão total
     */
    ADMIN
}
