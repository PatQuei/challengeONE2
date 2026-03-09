package com.alura.forumhub.api.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para resposta de login com token JWT
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenResponseDTO {

    private String token;
    private String type = "Bearer";
    private Long expiresIn;
    private String email;

    public TokenResponseDTO(String token, Long expiresIn, String email) {
        this.token = token;
        this.expiresIn = expiresIn;
        this.email = email;
        this.type = "Bearer";
    }
}
