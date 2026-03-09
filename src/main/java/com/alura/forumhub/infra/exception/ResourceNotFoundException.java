package com.alura.forumhub.infra.exception;

/**
 * Exceção lançada quando um recurso não é encontrado
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Factory method para criar exceção com mensagem formatada
     */
    public static ResourceNotFoundException of(String resourceType, Long id) {
        return new ResourceNotFoundException(resourceType + " com ID " + id + " não encontrado");
    }
}
