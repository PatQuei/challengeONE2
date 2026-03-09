# 📚 Fórum Hub - Backend API

Sistema backend de um **fórum de discussões** desenvolvido com **Java 17** e **Spring Boot 3.3**, seguindo as especificações do Challenge Backend da Alura.

## 🎯 Visão Geral do Projeto

**Fórum Hub** é uma API REST que permite a criação, listagem, atualização e exclusão de tópicos de discussão em um fórum, similar ao Fórum da Alura. O sistema implementa autenticação segura com **JWT**, autorização baseada em roles, validação de dados e relacionamentos complexos entre entidades.

### Características Principais

- ✅ **CRUD completo de tópicos** com validação e regras de negócio
- ✅ **Sistema de respostas** a tópicos com marcação de solução
- ✅ **Autenticação com JWT** segura e stateless
- ✅ **Autorização baseada em roles** (ADMIN, MODERATOR, USER)
- ✅ **Paginação e ordenação** de resultados
- ✅ **Migrações de banco de dados** com Flyway
- ✅ **Documentação automática** com Swagger/OpenAPI
- ✅ **Testes de integração** com JUnit e Spring Test
- ✅ **Exception handling** centralizado
- ✅ **Dados iniciais** (seed) para desenvolvimento

---

## 🏗️ Arquitetura e Estrutura do Projeto

```
forumhub/
├── src/main/java/com/alura/forumhub/
│   ├── ForumHubApplication.java          # Classe principal
│   ├── api/
│   │   ├── controller/                   # Controladores REST
│   │   │   ├── AuthController.java       # Login e registro
│   │   │   ├── TopicController.java      # CRUD de tópicos
│   │   │   └── AnswerController.java     # CRUD de respostas
│   │   ├── dto/                          # DTOs para requisição/resposta
│   │   │   ├── auth/
│   │   │   ├── user/
│   │   │   ├── topic/
│   │   │   ├── answer/
│   │   │   └── error/
│   │   └── service/                      # Lógica de negócio
│   │       ├── AuthService.java
│   │       ├── TopicService.java
│   │       └── AnswerService.java
│   ├── domain/                           # Entidades de domínio
│   │   ├── user/
│   │   │   ├── User.java
│   │   │   ├── UserRole.java
│   │   │   └── UserRepository.java
│   │   ├── course/
│   │   │   ├── Course.java
│   │   │   └── CourseRepository.java
│   │   ├── topic/
│   │   │   ├── Topic.java
│   │   │   ├── TopicStatus.java
│   │   │   └── TopicRepository.java
│   │   └── answer/
│   │       ├── Answer.java
│   │       └── AnswerRepository.java
│   ├── security/                         # Configuração de segurança
│   │   ├── SecurityConfiguration.java
│   │   └── jwt/
│   │       ├── JwtService.java
│   │       └── JwtAuthenticationFilter.java
│   └── infra/
│       ├── exception/
│       │   ├── GlobalExceptionHandler.java
│       │   └── ResourceNotFoundException.java
│       └── config/
│           └── SwaggerConfiguration.java
├── src/main/resources/
│   ├── application.properties             # Config produção
│   ├── application-test.properties        # Config testes
│   └── db/migration/
│       ├── V1__initial_schema.sql        # Schema inicial
│       └── V2__insert_initial_data.sql   # Dados de seed
├── src/test/java/...                     # Testes de integração
└── pom.xml                               # Dependências Maven
```

---

## 🗄️ Diagrama de Banco de Dados

### Entidades e Relacionamentos

```
┌─────────────────┐
│     USERS       │
├─────────────────┤
│ id (PK)         │
│ name            │
│ email (UNIQUE)  │
│ password        │  ┌──────────────────┐
│ role (ENUM)     │──┤   TOPICS         │
│ created_at      │  │ ├─────────────┤  │
│ updated_at      │  │ id (PK)       │  │
│ is_active       │  │ title        │  │
└─────────────────┘  │ message      │  │
                     │ status       │  │────┐
┌─────────────────┐  │ author_id(FK)│  │    │
│     COURSES     │  │ course_id(FK)│  │    │
├─────────────────┤──┤ created_at   │  │    │
│ id (PK)         │  │ updated_at   │  │    │
│ name (UNIQUE)   │  └──────────────┘  │    │
│ category        │         ▲           │    │
│ created_at      │         │           │    │ 1:N
│ updated_at      │         │           │    │
│ is_active       │         │           │    │
└─────────────────┘  ┌──────────────────┐  │
                     │      ANSWERS      │  │
                     ├──────────────────┤  │
                     │ id (PK)          │  │
                     │ message          │  │
                     │ author_id (FK)───┘  │
                     │ topic_id (FK)───────┘
                     │ is_solution      │
                     │ created_at       │
                     │ updated_at       │
                     └──────────────────┘
```

### Tabelas

**USERS**: Armazena informações dos usuários do fórum
- Chave primária: `id`
- Índices: `email` (único)

**COURSES**: Catálogo de cursos disponíveis
- Chave primária: `id`
- Índices: `name` (único)

**TOPICS**: Tópicos de discussão criados pelos usuários
- Chave primária: `id`
- Chaves estrangeiras: `author_id` → USERS, `course_id` → COURSES
- Índices: `author_id`, `course_id`, `status`
- Restrição: Título único por curso

**ANSWERS**: Respostas aos tópicos
- Chave primária: `id`
- Chaves estrangeiras: `author_id` → USERS, `topic_id` → TOPICS
- Índices: `author_id`, `topic_id`

---

## 🚀 Tecnologias Utilizadas

| Tecnologia | Versão | Propósito |
|---|---|---|
| Java | 17 | Linguagem principal |
| Spring Boot | 3.3.0 | Framework web |
| Spring Data JPA | 3.3.0 | ORM e persistência |
| Spring Security | 3.3.0 | Autenticação e autorização |
| PostgreSQL | 42.7+ | Banco de dados relacional |
| Flyway | 10.x | Migrações de banco de dados |
| JJWT | 0.12.3 | Geração e validação de JWT |
| Springdoc OpenAPI | 2.2.0 | Documentação Swagger/OpenAPI |
| JUnit 5 | 5.x | Testes unitários |
| Spring Test | 3.3.0 | Testes de integração |
| Lombok | 1.18.x | Redução de boilerplate |
| Maven | 3.8+ | Gerenciador de dependências |

---

## 📋 Pré-requisitos

Antes de começar, certifique-se de que você possui:

- **Java 17+** instalado: [Download JDK](https://www.oracle.com/java/technologies/downloads/)
- **Maven 3.8+** instalado: [Download Maven](https://maven.apache.org/download.cgi)
- **PostgreSQL 12+** instalado e rodando: [Download PostgreSQL](https://www.postgresql.org/download/)
- **Git** para clonar o repositório

### Verificar instalação

```bash
java -version
mvn -version
psql --version
```

---

## ⚙️ Configuração e Instalação

### 1️⃣ Clonar o Repositório

```bash
git clone https://github.com/PatQuei/challengeONE2.git
cd forumhub
```

### 2️⃣ Configurar Banco de Dados PostgreSQL

```bash
# Abrir PostgreSQL
psql -U postgres

# Criar banco de dados
CREATE DATABASE forumhub;

# (Opcional) Criar usuário específico
CREATE USER forumhub_user WITH PASSWORD 'forumhub_password';
ALTER ROLE forumhub_user WITH CREATEDB;
GRANT ALL PRIVILEGES ON DATABASE forumhub TO forumhub_user;

# Sair
\q
```

### 3️⃣ Configurar Variáveis de Ambiente

Editar `src/main/resources/application.properties`:

```properties
# Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/forumhub
spring.datasource.username=postgres
spring.datasource.password=postgres

# JWT Configuration
jwt.secret=seu-secret-key-super-seguro-aqui-mudar-em-producao
jwt.expiration=24
```

> ⚠️ **IMPORTANTE**: Em produção, altere a `jwt.secret` para uma string forte e aleatória.

### 4️⃣ Compilar o Projeto

```bash
mvn clean install
```

### 5️⃣ Executar Migrações de Banco de Dados

As migrações são executadas automaticamente pelo Flyway ao iniciar a aplicação:

```bash
mvn flyway:migrate
```

### 6️⃣ Iniciar a Aplicação

```bash
mvn spring-boot:run
```

A aplicação estará disponível em: **http://localhost:8080**

---

## 📖 Endpoints da API

### 🔐 Autenticação (Públicos)

#### Login
```http
POST /auth/login
Content-Type: application/json

{
  "email": "admin@forumhub.com",
  "password": "senha_aqui"
}

Response:
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "type": "Bearer",
  "expiresIn": 86400000,
  "email": "admin@forumhub.com"
}
```

#### Registrar
```http
POST /auth/register
Content-Type: application/json

{
  "name": "Novo Usuário",
  "email": "novo@example.com",
  "password": "senha123",
  "role": "USER"
}

Response (201 Created):
{
  "id": 5,
  "name": "Novo Usuário",
  "email": "novo@example.com",
  "role": "USER",
  "createdAt": "2024-03-09T10:30:00",
  "updatedAt": "2024-03-09T10:30:00"
}
```

### 📝 Tópicos

#### Criar Tópico (Autenticado)
```http
POST /topicos
Authorization: Bearer {token}
Content-Type: application/json

{
  "title": "Qual é a melhor forma de usar JPA?",
  "message": "Estou começando a usar JPA e gostaria de saber as melhores práticas...",
  "courseId": 1
}

Response (201 Created):
{
  "id": 1,
  "title": "Qual é a melhor forma de usar JPA?",
  "message": "Estou começando a usar JPA...",
  "courseName": "Spring Boot Avançado",
  "authorName": "João Silva",
  "status": "OPEN",
  "createdAt": "2024-03-09T10:30:00",
  "updatedAt": "2024-03-09T10:30:00",
  "answers": []
}
```

#### Listar Tópicos (Público)
```http
GET /topicos?page=0&size=10&sortBy=createdAt

Response (200 OK):
{
  "content": [
    {
      "id": 1,
      "title": "Qual é a melhor forma de usar JPA?",
      "courseName": "Spring Boot Avançado",
      "authorName": "João Silva",
      "status": "OPEN",
      "createdAt": "2024-03-09T10:30:00",
      "answerCount": 3
    }
  ],
  "pageable": {...},
  "totalPages": 1,
  "totalElements": 1
}
```

#### Obter Detalhes do Tópico (Público)
```http
GET /topicos/{id}

Response (200 OK):
{
  "id": 1,
  "title": "Qual é a melhor forma de usar JPA?",
  "message": "Estou começando a usar JPA...",
  "courseName": "Spring Boot Avançado",
  "authorName": "João Silva",
  "status": "OPEN",
  "createdAt": "2024-03-09T10:30:00",
  "updatedAt": "2024-03-09T10:30:00",
  "answers": [
    {
      "id": 1,
      "message": "Use as melhores práticas...",
      "authorName": "Maria Santos",
      "isSolution": true,
      "createdAt": "2024-03-09T11:00:00",
      "updatedAt": "2024-03-09T11:00:00"
    }
  ]
}
```

#### Atualizar Tópico (Autenticado - Autor ou Admin)
```http
PUT /topicos/{id}
Authorization: Bearer {token}
Content-Type: application/json

{
  "title": "Qual é a melhor forma de usar JPA? [ATUALIZADO]",
  "message": "Atualizando com mais informações...",
  "status": "CLOSED"
}

Response (200 OK): [mesmo formato anterior]
```

#### Deletar Tópico (Autenticado - Autor ou Admin)
```http
DELETE /topicos/{id}
Authorization: Bearer {token}

Response (204 No Content)
```

### 💬 Respostas

#### Criar Resposta (Autenticado)
```http
POST /answers
Authorization: Bearer {token}
Content-Type: application/json

{
  "message": "Esta é uma excelente pergunta! Aqui estão as melhores práticas...",
  "topicId": 1
}

Response (201 Created):
{
  "id": 1,
  "message": "Esta é uma excelente pergunta!...",
  "authorName": "Maria Santos",
  "isSolution": false,
  "createdAt": "2024-03-09T11:00:00",
  "updatedAt": "2024-03-09T11:00:00"
}
```

#### Listar Respostas de um Tópico (Público)
```http
GET /answers/topic/{topicId}?page=0&size=10

Response (200 OK): [formato similar a tópicos]
```

#### Marcar como Solução (Autenticado - Autor do Tópico)
```http
POST /answers/{answerId}/solution
Authorization: Bearer {token}

Response (200 OK):
{
  "id": 1,
  "message": "Esta é uma excelente pergunta!...",
  "authorName": "Maria Santos",
  "isSolution": true,
  "createdAt": "2024-03-09T11:00:00",
  "updatedAt": "2024-03-09T11:00:00"
}
```

---

## 🧪 Testes

### Executar todos os testes

```bash
mvn test
```

### Executar teste específico

```bash
mvn test -Dtest=AuthControllerTest
mvn test -Dtest=TopicControllerTest
```

### Teste com cobertura

```bash
mvn test jacoco:report
# Relatório em: target/site/jacoco/index.html
```

### Testes Implementados

- ✅ **AuthControllerTest**: Registro, login, validações
- ✅ **TopicControllerTest**: CRUD de tópicos, autenticação, paginação

---

## 📚 Documentação Swagger

A documentação interativa da API está disponível em:

```
http://localhost:8080/swagger-ui.html
```

Você pode testar os endpoints diretamente do Swagger UI.

**Arquivo OpenAPI JSON**:
```
http://localhost:8080/v3/api-docs
```

---

## 🔐 Segurança e Autenticação

### Sistema de Roles

| Role | Permissões |
|---|---|
| **USER** | Criar/atualizar seus próprios tópicos e respostas |
| **MODERATOR** | Gerenciar qualquer conteúdo do fórum |
| **ADMIN** | Acesso total (futuro: gerenciar usuários, cursos, etc.) |

### JWT (JSON Web Token)

O JWT é gerado após login bem-sucedido e deve ser incluído no header de requisições protegidas:

```http
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbkBmb3J1bWh1Yi5jb20iLCJpYXQiOjE2NzA3NzU1MDB9...
```

**Token Contém**:
- Subject: Email do usuário
- Issued At: Timestamp de emissão
- Expiration: Timestamp de expiração (configurável em `application.properties`)

### Endpoints Públicos

- `POST /auth/login` - Login
- `POST /auth/register` - Registro
- `GET /topicos` - Listar tópicos
- `GET /topicos/{id}` - Detalhes do tópico
- `GET /answers/topic/{topicId}` - Listar respostas
- `GET /swagger-ui.html` - Documentação

### Endpoints Protegidos

- `POST /topicos` - Criar tópico
- `PUT /topicos/{id}` - Atualizar tópico
- `DELETE /topicos/{id}` - Deletar tópico
- `POST /answers` - Criar resposta
- `PUT /answers/{id}` - Atualizar resposta
- `DELETE /answers/{id}` - Deletar resposta
- `POST /answers/{id}/solution` - Marcar como solução

---

## 🛠️ Configurações Adicionais

### Ambiente de Desenvolvimento

O arquivo `application.properties` já contém configurações para desenvolvimento local.

### Logs

Para ver logs detalhados, edite `application.properties`:

```properties
logging.level.com.alura.forumhub=DEBUG
logging.level.org.springframework.security=DEBUG
```

### Pool de Conexões

Configurar tamanho do pool de conexões (em `application.properties`):

```properties
spring.jpa.properties.hibernate.jdbc.batch_size=15
spring.jpa.properties.hibernate.order_inserts=true
spring.jpa.properties.hibernate.order_updates=true
```

---

## 📊 Decisões de Projeto

### 1. Exclusão Lógica vs Física

**Decisão**: Implementamos **exclusão lógica** (soft delete) para tópicos.

**Justificativa**: 
- Mantém histórico de discussões
- Permite auditoria
- Evita orphaned records de respostas

**Implementação**: Campo `status` com valor `DELETED` ao deletar.

### 2. Autorização de Edição

**Decisão**: Apenas autores ou admins podem editar/deletar.

**Justificativa**: 
- Protege direitos autorais
- Valida consenso do fórum
- Permite moderação

### 3. Paginação Padrão

**Padrão**: 10 itens por página, ordenação por data descendente.

**Configurável**: O cliente pode alterar via query parameters.

### 4. JWT em vez de Sessões

**Decisão**: Utilizamos JWT (stateless).

**Vantagens**:
- Escalável horizontalmente
- Sem necessidade de sessão no servidor
- Ideal para APIs REST
- Suporta múltiplos clientes

### 5. Validação em Camadas

Validação implementada em:
- **DTOs**: Bean Validation (`@NotNull`, `@Email`, etc.)
- **Service**: Regras de negócio
- **GlobalExceptionHandler**: Centralizado para consistent error responses

---

## 🚨 Tratamento de Erros

Todos os erros retornam um JSON padronizado:

```json
{
  "status": 400,
  "message": "Erro de validação",
  "path": "/topicos",
  "timestamp": "2024-03-09T10:30:00",
  "errors": [
    {
      "field": "title",
      "message": "Título é obrigatório"
    }
  ]
}
```

---

## 🔄 Fluxo de Autenticação

```
1. Usuário chama POST /auth/login com email e senha
2. AuthController → AuthService.login()
3. AuthenticationManager valida credenciais
4. JwtService gera token JWT com expiração
5. Server retorna token ao cliente
6. Cliente inclui no header: "Authorization: Bearer {token}"
7. JwtAuthenticationFilter valida token
8. SecurityContext é preenchido com Usuario autenticado
9. Requisição é processada com autorização
```

---

## 📈 Escalabilidade

### Melhorias Futuras

- [ ] Cache com Redis
- [ ] Busca full-text com Elasticsearch
- [ ] Notificações em tempo real com WebSocket
- [ ] Rate limiting com spring-cloud-gateway
- [ ] Métricas com Micrometer e Prometheus
- [ ] API Versioning
- [ ] GraphQL como alternativa a REST

---

## 🐛 Troubleshooting

### Erro: Banco de dados não encontrado
```bash
# Certifique-se que PostgreSQL está rodando
psql -U postgres -l

# Crie o banco se não existir
createdb -U postgres forumhub
```

### Erro: Porta 8080 já em uso
```bash
# Altere em application.properties
server.port=8081
```

### Erro: JWT Token Expirado
O token expira de acordo com `jwt.expiration`. Re-faça login para obter novo token.

### Erro: Acesso Negado (403)
Verifique se o usuário autenticado é o autor do recurso ou admin.

---

## 📄 Licença

Este projeto está licenciado sob a **MIT License** - veja o arquivo LICENSE para detalhes.

---

## 👤 Autores

- **PatQuei** - Desenvolvedor Principal
- **Alura** - Especificações e desafio

---

## 🤝 Contribuindo

1. Fork o projeto
2. Crie uma branch para sua feature (`git checkout -b feature/AmazingFeature`)
3. Commit suas mudanças (`git commit -m 'Add some AmazingFeature'`)
4. Push para a branch (`git push origin feature/AmazingFeature`)
5. Abra um Pull Request

---

## 📞 Suporte

Para dúvidas ou problemas, abra uma issue no [GitHub](https://github.com/PatQuei/challengeONE2/issues).

---

## 🎓 Recursos de Aprendizado

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Security Reference](https://spring.io/projects/spring-security)
- [JWT Introduction](https://jwt.io/introduction)
- [JPA/Hibernate Best Practices](https://hibernate.org/orm/)
- [RESTful API Best Practices](https://restfulapi.net/)

---

**Desenvolvido com ❤️ para o Archive Challenge - Alura**

Última atualização: Março 2024
