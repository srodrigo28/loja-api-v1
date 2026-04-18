# Loja99 API

<p align="center">
  <img src="./assets/banner-loja99.svg" alt="Banner Loja99 API" />
</p>

```
./mvnw.cmd spring-boot:run
./mvnw.cmd test
```

<p align="center">
  <img alt="Java 21" src="https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=openjdk" />
  <img alt="Spring Boot 4.0.5" src="https://img.shields.io/badge/Spring%20Boot-4.0.5-6DB33F?style=for-the-badge&logo=springboot" />
  <img alt="PostgreSQL 16.12" src="https://img.shields.io/badge/PostgreSQL-16.12-316192?style=for-the-badge&logo=postgresql" />
  <img alt="Flyway" src="https://img.shields.io/badge/Flyway-Migrations-CC0200?style=for-the-badge&logo=flyway" />
  <img alt="Maven" src="https://img.shields.io/badge/Maven-Build-C71A36?style=for-the-badge&logo=apachemaven" />
</p>

<p align="center">
  <strong>API REST para gerenciamento de usuarios e categorias</strong>
</p>

<p align="center">
  Projeto backend construído com Java, Spring Boot, PostgreSQL e Flyway, com foco em organizacao, persistencia robusta e base pronta para evolucao.
</p>

---

## ✨ Destaques do projeto

##  ✨✨ Rodar o projeto & Testes

### Rodar o projeto

```bash
./mvnw.cmd spring-boot:run
```

Esse comando inicia a API Spring Boot usando as configuracoes de `application.properties`.

### Rodar os testes

```bash
./mvnw.cmd test
```

Esse comando executa a suite automatizada com JUnit, Spring Boot Test, MockMvc e banco H2 em memoria.

### Lista de testes automatizados

- `Loja99ApplicationTests`: verifica se o contexto Spring Boot sobe corretamente.
- `AuthControllerTests`: testa login com email e senha, rejeicao de senha invalida, sessao por Bearer Token, sessao por cookie e logout.
- `LojaControllerTests`: testa criacao de loja, bloqueio de slug duplicado, listagem de lojas e atualizacao de status.
- `CategoriaControllerTests`: testa criacao de categoria com imagem, validacao de imagem obrigatoria, rejeicao de tipo invalido, filtro por loja, atualizacao e exclusao com limpeza do arquivo enviado.
- `ProdutoControllerTests`: testa criacao de produto, upload de imagens, troca de imagem principal, variantes de tamanho, remocao protegendo a ultima imagem, exclusao com limpeza de diretorio e listagem por loja.

- API REST com operacoes completas de CRUD.
- Persistencia com Spring Data JPA e Hibernate.
- Banco PostgreSQL com schema versionado por Flyway.
- Seed inicial para demonstracao rapida da aplicacao.
- Validacao de entrada com Bean Validation.
- Estrutura organizada em camadas: controller, service, repository, entity, dto e mapper.
- Banco H2 em memoria para testes automatizados.

## 🖼️ Visao do sistema

```text
Cliente / Frontend
        |
        v
   Controllers REST
        |
        v
  Services de negocio
        |
        v
Repositories Spring Data JPA
        |
        v
 PostgreSQL + Flyway
```

## 🚀 Visao geral

O projeto Loja99 API foi pensado como uma base moderna para cadastro e gerenciamento de usuarios e categorias. A aplicacao oferece uma estrutura clara para evolucao futura, permitindo adicionar autenticacao, novos modulos, documentacao OpenAPI e monitoramento com facilidade.

### 📌 Recursos principais

- Cadastro, listagem, busca, atualizacao e remocao de usuarios.
- Cadastro, listagem, busca, atualizacao e remocao de categorias.
- Relacionamento entre `usuarios` e `categorias`.
- Tratamento global de erros da API.
- Migrations SQL para criacao do schema e carga inicial.

## 🧰 Stack utilizada

- Java 21
- Spring Boot 4.0.5
- Maven
- PostgreSQL
- H2 para testes
- Flyway para versionamento do banco
- Lombok para reduzir codigo repetitivo

## 📦 Dependencias do projeto

As dependencias declaradas em [pom.xml](./pom.xml) possuem os seguintes papeis:

### Dependencias principais

- `spring-boot-starter-actuator`
  Resumo: adiciona endpoints tecnicos de observabilidade e monitoramento, como health checks.

- `spring-boot-starter-data-jpa`
  Resumo: fornece JPA, Hibernate e integracao com repositorios Spring Data para acesso ao banco.

- `spring-boot-starter-validation`
  Resumo: habilita validacoes em DTOs com anotacoes como `@NotBlank`, `@Email` e `@NotNull`.

- `spring-boot-starter-webmvc`
  Resumo: disponibiliza a estrutura web MVC para expor os endpoints REST da aplicacao.

- `spring-boot-devtools`
  Resumo: melhora a experiencia de desenvolvimento com restart automatico e ajustes para ambiente local.

- `postgresql`
  Resumo: driver JDBC usado para conectar a aplicacao ao banco PostgreSQL.

- `flyway-core`
  Resumo: motor principal do Flyway, responsavel por localizar e executar migrations versionadas.

- `flyway-database-postgresql`
  Resumo: modulo especifico do Flyway para suporte completo ao PostgreSQL.

- `lombok`
  Resumo: reduz boilerplate em entidades, DTOs e services com anotacoes como `@Getter`, `@Setter` e `@Builder`.

### Dependencias de teste

- `h2`
  Resumo: banco em memoria usado nos testes automatizados para execucao rapida e isolada.

- `spring-boot-starter-test`
  Resumo: conjunto de bibliotecas para testes com Spring Boot, JUnit, Mockito e utilitarios de suporte.

## 🗄️ Banco de dados

### Banco principal

A aplicacao esta preparada para usar PostgreSQL como banco principal.

#### Configuracao padrao atual

- Host: `99dev.pro`
- Porta: `5432`
- Banco: `pgtest`
- Usuario padrao: `postgres`
- Driver: `org.postgresql.Driver`

#### Versao validada durante a revisao

- PostgreSQL `16.12`

#### Arquivo de configuracao principal

- [src/main/resources/application.properties](./src/main/resources/application.properties)

#### Variaveis suportadas

- `DB_URL`
- `DB_HOST`
- `DB_PORT`
- `DB_NAME`
- `DB_USER`
- `DB_PASSWORD`
- `DB_CONNECTION_TIMEOUT_MS`
- `DB_VALIDATION_TIMEOUT_MS`
- `DB_INIT_FAIL_TIMEOUT_MS`
- `DB_MAX_POOL_SIZE`

### Banco de testes

Nos testes automatizados, o projeto usa H2 em memoria.

#### Configuracao de teste

- URL: `jdbc:h2:mem:loja99-test;MODE=PostgreSQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE`
- Usuario: `sa`
- Driver: `org.h2.Driver`

#### Versao observada nos testes

- H2 `2.4.240`

#### Arquivo de configuracao de teste

- [src/test/resources/application.properties](./src/test/resources/application.properties)

## 🛫 Versionamento do banco com Flyway

O schema do banco e controlado por migrations SQL.

### Arquivos atuais

- [V1__create_tables.sql](./src/main/resources/db/migration/V1__create_tables.sql)
  Cria as tabelas `usuarios` e `categorias`.

- [V2__seed_initial_data.sql](./src/main/resources/db/migration/V2__seed_initial_data.sql)
  Insere a carga inicial de dados com 3 usuarios e 3 categorias.

### Configuracao complementar

- [src/main/java/com/loja99/config/FlywayConfiguration.java](./src/main/java/com/loja99/config/FlywayConfiguration.java)

## 🌱 Seed inicial

O projeto ja possui carga inicial com 3 registros em cada tabela, ideal para testes manuais e demonstracao.

### Usuarios inseridos

- Ana Souza
- Bruno Lima
- Carla Mendes

### Categorias inseridas

- Eletronicos
- Moda
- Casa

## 🔌 Estrutura basica da API

### Endpoints principais

| Recurso | Metodo | Endpoint | Descricao |
|---|---|---|---|
| Usuarios | `POST` | `/api/usuarios` | Cria um novo usuario |
| Usuarios | `GET` | `/api/usuarios` | Lista todos os usuarios |
| Usuarios | `GET` | `/api/usuarios/{id}` | Busca um usuario por id |
| Usuarios | `PUT` | `/api/usuarios/{id}` | Atualiza um usuario existente |
| Usuarios | `DELETE` | `/api/usuarios/{id}` | Remove um usuario |
| Categorias | `POST` | `/api/categorias` | Cria uma nova categoria |
| Categorias | `GET` | `/api/categorias` | Lista todas as categorias |
| Categorias | `GET` | `/api/categorias/{id}` | Busca uma categoria por id |
| Categorias | `PUT` | `/api/categorias/{id}` | Atualiza uma categoria existente |
| Categorias | `DELETE` | `/api/categorias/{id}` | Remove uma categoria |

## 🧪 Exemplo de payload

### Criar usuario

```json
{
  "nome": "Maria Oliveira",
  "email": "maria@loja99.com",
  "telefone": "(11) 97777-8888",
  "endereco": {
    "cep": "01310-100",
    "logradouro": "Avenida Paulista",
    "numero": "1000",
    "complemento": "Conjunto 12",
    "bairro": "Bela Vista",
    "cidade": "Sao Paulo",
    "estado": "SP"
  }
}
```

### Criar categoria

```json
{
  "nome": "Informatica",
  "descricao": "Produtos de tecnologia e perifericos.",
  "usuarioId": 1
}
```

## ▶️ Como executar o projeto

### Requisitos

- Java 21 instalado
- Maven Wrapper disponivel no projeto
- Acesso ao PostgreSQL, caso queira rodar com banco real

### Executar em desenvolvimento

```powershell
./mvnw.cmd spring-boot:run
```

### Executar os testes

```powershell
./mvnw.cmd test
```

## 📁 Estrutura do projeto

```text
src/main/java/com/loja99
|-- controller
|-- dto
|-- entity
|-- exception
|-- mapper
|-- repository
|-- service
|-- config

src/main/resources
|-- application.properties
|-- db/migration

assets
|-- banner-loja99.svg
```

## 🛣️ Roadmap sugerido

- Adicionar documentacao com Swagger ou OpenAPI.
- Criar autenticacao com Spring Security e JWT.
- Incluir paginacao e filtros nas listagens.
- Adicionar testes de integracao para controllers e services.
- Incluir pipeline CI para build e testes automaticos.

## 📝 Observacoes tecnicas

- O projeto usa `spring.jpa.hibernate.ddl-auto=validate` no ambiente principal para validar o schema sem alterar o banco automaticamente.
- O Flyway e responsavel por criar e evoluir o banco em ambiente real.
- Nos testes, o H2 usa `create-drop` para manter execucao simples e isolada.
- O projeto ja esta pronto para crescer com autenticacao, documentacao automatica e novos modulos de dominio.
