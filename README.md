# API de Fórum

Este projeto é uma API RESTful para gerenciar um fórum de cursos, tópicos e respostas. Os usuários podem criar cursos, tópicos, responder a tópicos e gerenciar seus perfis. A API é construída utilizando **Spring Boot** e utiliza um banco de dados relacional (como PostgresQL) para persistir os dados.

## Funcionalidades

- **Cursos**: Criar e listar cursos.
- **Tópicos**: Criar tópicos dentro de cursos, listar tópicos de um curso e excluir tópicos.
- **Respostas**: Criar respostas para tópicos e listar respostas de um tópico.
- **Perfis de Usuários**: Criar, ler e atualizar o perfil de usuários.
- **Autenticação**: Sistema de autenticação JWT para proteger rotas e permitir operações autenticadas.

## Tecnologias Utilizadas

- **Spring Boot**: Framework principal para a criação da API.
- **JPA/Hibernate**: Para persistência de dados no banco de dados.
- **JWT (JSON Web Tokens)**: Para autenticação e autorização de usuários.
- **MySQL/H2**: Banco de dados relacional para persistência dos dados (configurável).
- **Maven**: Gerenciador de dependências e build do projeto.

## Endpoints

### **Cursos**
- `GET /api/cursos` - Lista todos os cursos.
- `POST /api/cursos` - Cria um novo curso.
- `DELETE /api/cursos/{id}` - Deleta um curso.

### **Tópicos**
- `GET /api/topicos` - Lista todos os tópicos.
- `GET /api/topicos/curso/{cursoId}` - Lista todos os tópicos de um curso.
- `POST /api/topicos` - Cria um novo tópico.
- `DELETE /api/topicos/{id}` - Deleta um tópico.

### **Respostas**
- `GET /api/respostas/topico/{topicoId}` - Lista todas as respostas de um tópico.
- `POST /api/respostas` - Cria uma nova resposta para um tópico.
- `DELETE /api/respostas/{id}` - Deleta uma resposta.

### **Perfis**
- `GET /api/perfis/usuario/{usuarioId}` - Consulta o perfil de um usuário.
- `POST /api/perfis` - Cria ou atualiza o perfil de um usuário.

### **Autenticação**
- `/api/v1/users/login` Realiza login de um usuário e retorna um token JWT para autenticação.

### **criar Perfil**
- `POST /api/v1/users/create` - Realiza login de um usuário e retorna um token JWT para autenticação.

## Como Rodar o Projeto

### Pré-requisitos

Antes de rodar o projeto, verifique se você possui os seguintes softwares instalados:

- JDK 17 ou superior
- Maven
- MySQL ou H2 (Banco de dados configurável)

### Passos

1. Clone o repositório:

```bash
git clone https://github.com/usuario/forum-api.git
cd forum-api
```

2. Configure o arquivo `application.properties` com suas credenciais de banco de dados.

Para usar o **Postgres**:

```properties
spring.datasource.url=jdbc:postgresql://xxxxxx:5432/xxxxx
spring.datasource.username=xxxx
spring.datasource.password=xxxxx
spring.jpa.hibernate.ddl-auto=update
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
```

Ou use o **H2** (para desenvolvimento local):

```properties
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=password
spring.jpa.hibernate.ddl-auto=update
spring.h2.console.enabled=true
```

3. Instale as dependências e execute o projeto:

```bash
mvn spring-boot:run
```

4. Acesse a API em `http://localhost:8080`.

## Testando a API

Você pode testar a API utilizando ferramentas como [Postman](https://www.postman.com) ou [Insomnia](https://insomnia.rest), ou utilizando **curl** no terminal.

### Exemplos de Requisições

- **Criar um novo curso:**
```bash
POST /api/cursos
{
  "nome": "Java Básico",
  "descricao": "Curso de introdução à linguagem Java"
}
```

- **Listar todos os tópicos de um curso:**
```bash
GET /api/topicos/curso/{cursoId}
```

- **Criar uma resposta:**
```bash
POST /api/respostas
{
  "conteudo": "Ótima explicação, muito claro!",
  "topicoId": 1,
  "usuarioId": 1
}
```

- **Autenticar um usuário (Login):**
```bash
POST /api/v1/users/login
{
  "username": "admin",
  "password": "password123"
}

```

- **criar um usuário :**
```bash
POST /api/v1/users/create
{
  "username": "john_doe",
  "email": "john@example.com",
  "password": "password123",
  "role": "USER",
  "perfil": {
    "descricao": "Software Developer",
    "fotoUrl": "https://example.com/photo.jpg"
  }
}


```

A resposta será um token JWT que deve ser enviado nas requisições subsequentes no cabeçalho `Authorization`.

## Contribuindo

Sinta-se à vontade para abrir **issues** ou enviar **pull requests** para melhorar a API. Toda contribuição é bem-vinda!

1. Faça o fork do repositório.
2. Crie uma nova branch para a sua feature (`git checkout -b minha-feature`).
3. Faça as modificações e commit (`git commit -am 'Adicionando nova feature'`).
4. Envie para o repositório (`git push origin minha-feature`).
5. Abra um pull request.

## Licença

Este projeto está licenciado sob a MIT License - veja o arquivo [LICENSE](LICENSE) para mais detalhes.
```

### Explicação do `README.md`

1. **Introdução**: Apresenta o projeto, suas funcionalidades e a arquitetura.
2. **Tecnologias Utilizadas**: Descreve as principais tecnologias usadas, como Spring Boot, JPA/Hibernate, JWT, e banco de dados.
3. **Endpoints**: Detalha todos os endpoints da API, com explicações sobre cada um e os métodos HTTP utilizados.
4. **Como Rodar o Projeto**: Instruções sobre como configurar e rodar a aplicação localmente.
5. **Testando a API**: Exemplos de como fazer requisições usando ferramentas como Postman ou curl.
6. **Contribuindo**: Orientações sobre como contribuir para o projeto.
7. **Licença**: Especifica a licença sob a qual o projeto está distribuído.