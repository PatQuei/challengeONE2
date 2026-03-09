package com.alura.forumhub.security;

import com.alura.forumhub.domain.user.UserRepository;
import com.alura.forumhub.security.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configuração de Segurança do Spring Security
 * Define as regras de acesso, autenticação e autorização
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final UserRepository userRepository;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * Define a cadeia de filtros de segurança (SecurityFilterChain)
     * Configura quais endpoints são públicos e quais requerem autenticação
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Desabilita CSRF (não necessário para API com tokens JWT)
                .csrf(csrf -> csrf.disable())

                // Define criação de sessão como STATELESS (sem sessão, baseado em tokens)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Configuração de autorização de endpoints
                .authorizeHttpRequests(authz -> authz
                        // Endpoints públicos (sem autenticação)
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/v3/api-docs/**").permitAll()
                        .requestMatchers("/swagger-ui/**").permitAll()
                        .requestMatchers("/swagger-ui.html").permitAll()

                        // GET de tópicos pode ser público (listagem e detalhes)
                        .requestMatchers(HttpMethod.GET, "/topicos/**").permitAll()

                        // GET de courses pode ser público
                        .requestMatchers(HttpMethod.GET, "/courses/**").permitAll()

                        // Endpoints protegidos (requerem autenticação)
                        .requestMatchers(HttpMethod.POST, "/topicos/**").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/topicos/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/topicos/**").authenticated()

                        .requestMatchers(HttpMethod.POST, "/answers/**").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/answers/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/answers/**").authenticated()

                        // Qualquer outra requisição requer autenticação
                        .anyRequest().authenticated()
                )

                // Adiciona o filtro JWT antes do UsernamePasswordAuthenticationFilter
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Configura o AuthenticationManager
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Configura o provedor de autenticação (DaoAuthenticationProvider)
     * Usa o UserDetailsService para carregar usuários do banco de dados
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * Bean de UserDetailsService que carrega usuários do banco de dados
     */
    @Bean
    public UserDetailsService userDetailsService() {
        return email -> userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + email));
    }

    /**
     * Configura o BCryptPasswordEncoder para hash de senhas
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
