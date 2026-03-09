-- V2__insert_initial_data.sql
-- Script de seed com dados iniciais

-- Inserir usuários de exemplo (senhas são hash bcrypt simuladas)
INSERT INTO users (name, email, password, role, is_active) VALUES
('Admin User', 'admin@forumhub.com', '$2a$10$Y9VplB6KFdqPOmkLEG47JeoxyJPfEhLMGLGSX7I5xzBm64MJbQqKO', 'ADMIN', true),
('Moderator User', 'moderator@forumhub.com', '$2a$10$Y9VplB6KFdqPOmkLEG47JeoxyJPfEhLMGLGSX7I5xzBm64MJbQqKO', 'MODERATOR', true),
('João Silva', 'joao@example.com', '$2a$10$Y9VplB6KFdqPOmkLEG47JeoxyJPfEhLMGLGSX7I5xzBm64MJbQqKO', 'USER', true),
('Maria Santos', 'maria@example.com', '$2a$10$Y9VplB6KFdqPOmkLEG47JeoxyJPfEhLMGLGSX7I5xzBm64MJbQqKO', 'USER', true);

-- Inserir cursos de exemplo
INSERT INTO courses (name, category, is_active) VALUES
('Spring Boot Avançado', 'Backend', true),
('Spring Framework', 'Backend', true),
('Java 17 Fundamentals', 'Backend', true),
('REST APIs com Spring', 'Backend', true);
