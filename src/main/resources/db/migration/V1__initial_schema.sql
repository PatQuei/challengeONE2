-- V1__initial_schema.sql
-- Script de migração inicial para criar as tabelas do Fórum Hub

-- Tabela de Usuários
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT true
);

-- Tabela de Cursos
CREATE TABLE courses (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    category VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT true
);

-- Tabela de Tópicos
CREATE TABLE topics (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'OPEN',
    author_id BIGINT NOT NULL,
    course_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_topics_author FOREIGN KEY (author_id) REFERENCES users(id),
    CONSTRAINT fk_topics_course FOREIGN KEY (course_id) REFERENCES courses(id),
    CONSTRAINT unique_topic_per_course UNIQUE (title, course_id)
);

-- Tabela de Respostas
CREATE TABLE answers (
    id BIGSERIAL PRIMARY KEY,
    message TEXT NOT NULL,
    author_id BIGINT NOT NULL,
    topic_id BIGINT NOT NULL,
    is_solution BOOLEAN DEFAULT false,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_answers_author FOREIGN KEY (author_id) REFERENCES users(id),
    CONSTRAINT fk_answers_topic FOREIGN KEY (topic_id) REFERENCES topics(id) ON DELETE CASCADE
);

-- Índices para melhorar performance
CREATE INDEX idx_topics_author ON topics(author_id);
CREATE INDEX idx_topics_course ON topics(course_id);
CREATE INDEX idx_topics_status ON topics(status);
CREATE INDEX idx_answers_author ON answers(author_id);
CREATE INDEX idx_answers_topic ON answers(topic_id);
CREATE INDEX idx_users_email ON users(email);
