# Plano Inicial - API Loja99

## Objetivo
Construir a API inicial do projeto Loja99 em Java com Spring Boot, usando arquitetura MVC, PostgreSQL e boas praticas de validacao, organizacao de camadas e resposta HTTP.

## Stack base
- Java 21
- Spring Boot
- Spring Web MVC
- Spring Data JPA
- PostgreSQL
- Lombok
- Bean Validation

## Recursos desta primeira entrega

### 1. Configuracao da aplicacao
- Configurar conexao com PostgreSQL.
- Habilitar JPA com criacao e atualizacao automatica das tabelas.
- Organizar o projeto em pacotes: `controller`, `service`, `repository`, `entity`, `dto`, `mapper` e `exception`.

### 2. Cadastro de usuarios
- Criar CRUD completo de usuarios.
- Campos obrigatorios:
  - nome
  - email
  - telefone
  - endereco
- Estrutura de endereco:
  - cep
  - logradouro
  - numero
  - complemento
  - bairro
  - cidade
  - estado
- Regras iniciais:
  - email unico
  - validacao de email
  - validacao basica de telefone
  - validacao de CEP

### 3. Cadastro de categorias
- Criar CRUD completo de categorias.
- Campos:
  - nome
  - descricao
  - usuarioId
- Regras iniciais:
  - toda categoria deve pertencer a um usuario
  - um usuario pode ter uma ou muitas categorias
  - ID inteiro com auto incremento

### 4. Tratamento de erros
- Padronizar respostas para:
  - validacao de campos
  - recurso nao encontrado
  - regras de negocio

## Endpoints previstos

### Usuarios
- `POST /api/usuarios`
- `GET /api/usuarios`
- `GET /api/usuarios/{id}`
- `PUT /api/usuarios/{id}`
- `DELETE /api/usuarios/{id}`

### Categorias
- `POST /api/categorias`
- `GET /api/categorias`
- `GET /api/categorias/{id}`
- `PUT /api/categorias/{id}`
- `DELETE /api/categorias/{id}`

## Proximos recursos sugeridos
- Consulta automatica de endereco por CEP via integracao externa.
- Paginacao e filtros nas listagens.
- Auditoria com data de criacao e atualizacao.
- Documentacao da API com Swagger/OpenAPI.
- Autenticacao e autorizacao.
- Testes unitarios e de integracao para services e controllers.
- Soft delete para registros sensiveis.
