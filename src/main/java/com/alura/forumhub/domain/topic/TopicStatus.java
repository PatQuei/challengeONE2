package com.alura.forumhub.domain.topic;

/**
 * Enum que define os possíveis status de um tópico
 */
public enum TopicStatus {
    /**
     * Tópico aberto para discussão
     */
    OPEN,

    /**
     * Tópico foi resolvido
     */
    CLOSED,

    /**
     * Tópico em processo de ser resolvido
     */
    PENDING,

    /**
     * Tópico foi excluído logicamente
     */
    DELETED
}
