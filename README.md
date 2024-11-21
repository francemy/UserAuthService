
# SecureUserManager - Sistema de Gerenciamento de Usuários com Autenticação Avançada

**SecureUserManager** é uma aplicação backend desenvolvida com **Spring Boot** que oferece um sistema seguro de gerenciamento de usuários, com autenticação avançada utilizando **JWT (JSON Web Tokens)**. O sistema possibilita o cadastro de novos usuários, autenticação com tokens JWT, gerenciamento de permissões e recuperação de senha.

## Funcionalidades

- **Cadastro de Usuários**: Permite o registro de novos usuários com validação de dados essenciais como nome, email e senha.
- **Autenticação JWT**: Implementação de login seguro com JWT para garantir a comunicação sem estado entre o cliente e o servidor.
- **Gerenciamento de Perfis e Permissões**: Controle sobre diferentes tipos de usuários e suas permissões de acesso.
- **Recuperação de Senha**: Funcionalidade para recuperação e redefinição de senha via e-mail.
- **Segurança Avançada**: Proteção das APIs com **Spring Security** para garantir a segurança do sistema.

## Tecnologias Utilizadas

- **Spring Boot**: Framework para desenvolvimento rápido de aplicativos Java.
- **Spring Security**: Framework para autenticação e controle de acesso.
- **JWT (JSON Web Tokens)**: Técnica de autenticação baseada em tokens para comunicação segura.
- **Spring Data JPA**: Simplifica o acesso a dados em bancos de dados relacionais.
- **Banco de Dados Relacional** (H2, PostgreSQL, MySQL, etc.): Utilizado para armazenar dados dos usuários.
- **Maven**: Gerenciador de dependências.

## Requisitos

Antes de rodar o projeto, tenha as seguintes ferramentas instaladas:

- **Java 22+** (preferencialmente)
- **Maven** (para gerenciar dependências e rodar o projeto)

## Configuração

### 1. Clone o repositório

Clone este repositório para o seu ambiente local:

```bash
git clone https://github.com/usuario/SecureUserManager.git
```

### 2. Configuração do Banco de Dados

No arquivo `src/main/resources/application.properties`, configure as informações do banco de dados:

```properties
# Configuração do Banco de Dados (exemplo com H2 para desenvolvimento)
spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=password
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect

# Para usar outro banco de dados, como PostgreSQL ou MySQL, basta ajustar a URL e credenciais.
```

### 3. Rodando o Projeto

Para rodar o projeto, execute o comando abaixo no terminal:

```bash
mvn spring-boot:run
```

Isso iniciará a aplicação no servidor embutido Tomcat, disponível em [http://localhost:8080](http://localhost:8080).

### 4. Testando a API

Você pode testar as APIs usando o Postman ou qualquer outra ferramenta para fazer requisições HTTP.

- **POST para** `/api/users` **para criar um novo usuário.**
- **GET para** `/api/users/{username}` **para buscar um usuário pelo nome de usuário.**
- **POST para** `/api/auth/login` **para autenticar e obter o token JWT.**

## Estrutura do Projeto

```plaintext
src
│
├── main
│   ├── java
│   │   └── com
│   │       └── exemplo
│   │           ├── controller       # Controladores de API
│   │           ├── model            # Entidades JPA
│   │           ├── repository       # Repositórios JPA
│   │           ├── service          # Serviços de negócio
│   │           └── SecureUserManagerApplication.java # Classe principal
│   │
│   └── resources
│       └── application.properties   # Configurações da aplicação
└── test
    └── java
        └── com
            └── exemplo
                ├── controller       # Testes de controladores
                └── service          # Testes de serviços
```

## Contribuições

Contribuições são bem-vindas! Se você tiver sugestões ou melhorias, fique à vontade para abrir um pull request ou issue.

## Licença

Este projeto está licenciado sob a MIT License - veja o arquivo LICENSE para mais detalhes.

---

## Como Usar o Makefile

Este projeto inclui um `Makefile` para automatizar várias tarefas no ciclo de desenvolvimento. O `Makefile` oferece suporte multi-plataforma e pode ser usado para compilar, executar, testar e limpar o projeto, além de integrar com Docker para fácil execução em contêineres.

### Comandos Disponíveis:

#### **Build e Execução**:
- `make build`: Compila o projeto usando o Maven.
- `make run`: Roda a aplicação Spring Boot.
- `make clean`: Limpa os arquivos gerados pelo build.

#### **Docker**:
- `make docker-build`: Cria a imagem Docker do projeto.
- `make docker-run`: Roda o contêiner Docker.
- `make docker-clean`: Limpa os recursos Docker não utilizados.

#### **Docker Compose**:
- `make docker-compose-up`: Sobe os serviços definidos no `docker-compose.yml`.
- `make docker-compose-down`: Remove os serviços definidos no `docker-compose.yml`.

#### **Gerenciamento de Arquivos/Diretórios**:
- `make create-target-dir`: Cria o diretório `target` no projeto.
- `make remove-dir`: Remove o diretório `target`.
- `make check-file-exists`: Verifica se o arquivo `example-file.txt` existe no diretório `target`.

#### **JAR**:
- `make jar`: Gera o arquivo JAR do projeto.
- `make run-jar`: Roda o arquivo JAR gerado.

### Exemplo de Uso do Makefile:

1. **Compilando o projeto**:
   ```bash
   make build
   ```

2. **Rodando a aplicação**:
   ```bash
   make run
   ```

3. **Criando a imagem Docker**:
   ```bash
   make docker-build
   ```

4. **Subindo os serviços com Docker Compose**:
   ```bash
   make docker-compose-up
   ```

### Suporte Multi-Plataforma

Este Makefile é configurado para funcionar em sistemas **Linux**, **macOS** e **Windows**. Ele detecta automaticamente o sistema operacional e ajusta os comandos de acordo (como `rm`, `mkdir`, `rmdir`, `touch`).

Aqui está um exemplo de `README.md` para o uso de **Dockerfile** e **docker-compose.yml** no seu projeto:

```markdown
# Dockerfile e Docker Compose para SecureUserManager

Este repositório fornece um **Dockerfile** e um arquivo **docker-compose.yml** para facilitar a criação e execução do projeto **SecureUserManager** em contêineres Docker.

## Introdução

Com o uso de **Docker** e **Docker Compose**, você pode facilmente configurar o ambiente de desenvolvimento e produção para o projeto **SecureUserManager**, sem se preocupar com a configuração manual de ambientes. Os contêineres garantem que o sistema tenha a mesma configuração em qualquer lugar.

### O que está incluído

- **Dockerfile**: Arquivo para construir a imagem Docker do projeto.
- **docker-compose.yml**: Arquivo para orquestrar contêineres, incluindo o banco de dados e o aplicativo backend.

## Como Usar

### Requisitos

Antes de usar o Docker e o Docker Compose, você precisa ter as seguintes ferramentas instaladas no seu sistema:

- **Docker**: Para criar, executar e gerenciar contêineres.
- **Docker Compose**: Para definir e gerenciar serviços multi-contêineres.

Você pode instalar o Docker e o Docker Compose seguindo as instruções oficiais:

- [Instalar o Docker](https://docs.docker.com/get-docker/)
- [Instalar o Docker Compose](https://docs.docker.com/compose/install/)

### Usando o Dockerfile

O **Dockerfile** está configurado para criar uma imagem Docker do **SecureUserManager**. Com ele, você pode criar a imagem e rodar o contêiner com o seguinte comando:

#### 1. **Construir a Imagem Docker**

Primeiro, construa a imagem Docker com o comando:

```bash
docker build -t secureusermanager .
```

Isso cria a imagem com o nome `secureusermanager` a partir do **Dockerfile**.

#### 2. **Rodar o Contêiner**

Para rodar o contêiner da imagem gerada, execute:

```bash
docker run -p 8080:8080 secureusermanager
```

Isso inicia a aplicação Spring Boot no contêiner e a disponibiliza em [http://localhost:8080](http://localhost:8080).

### Usando o Docker Compose

**Docker Compose** permite que você defina e gerencie múltiplos contêineres, facilitando a orquestração do seu ambiente de desenvolvimento ou produção. O arquivo **docker-compose.yml** inclui configurações para o serviço de aplicação Spring Boot e um banco de dados.

#### 1. **Subir os Contêineres com Docker Compose**

Execute o seguinte comando para subir os contêineres definidos no arquivo **docker-compose.yml**:

```bash
docker-compose up
```

Este comando vai:
- Construir as imagens (se necessário).
- Criar e iniciar os contêineres para o **SecureUserManager** e o banco de dados (definido no arquivo `docker-compose.yml`).

A aplicação estará disponível em [http://localhost:8080](http://localhost:8080).

#### 2. **Subir em Segundo Plano**

Se você quiser rodar os contêineres em segundo plano (modo detach), use o seguinte comando:

```bash
docker-compose up -d
```

#### 3. **Parar os Contêineres**

Para parar os contêineres em execução, execute:

```bash
docker-compose down
```

Este comando para e remove os contêineres, redes e volumes definidos no arquivo `docker-compose.yml`.

## Estrutura do Dockerfile

O **Dockerfile** é responsável por criar a imagem Docker da aplicação. Aqui está a explicação das principais instruções do Dockerfile:

```Dockerfile
# Usar a imagem oficial do OpenJDK como base
FROM openjdk:17-jdk-slim

# Definir o diretório de trabalho
WORKDIR /app

# Copiar o arquivo JAR da aplicação para o contêiner
COPY target/secureusermanager.jar /app/secureusermanager.jar

# Expor a porta 8080 (onde a aplicação estará disponível)
EXPOSE 8080

# Comando para rodar a aplicação no contêiner
ENTRYPOINT ["java", "-jar", "secureusermanager.jar"]
```

### Explicação:

1. **FROM**: Define a imagem base. Neste caso, estamos usando a imagem `openjdk:17-jdk-slim`, que é uma versão leve do OpenJDK 17.
2. **WORKDIR**: Define o diretório de trabalho dentro do contêiner.
3. **COPY**: Copia o arquivo JAR da aplicação gerado pelo Maven para dentro do contêiner.
4. **EXPOSE**: Expõe a porta 8080 para que a aplicação seja acessível.
5. **ENTRYPOINT**: Define o comando para rodar a aplicação Spring Boot no contêiner.

## Estrutura do docker-compose.yml

O **docker-compose.yml** orquestra os serviços necessários, como o serviço da aplicação e o banco de dados. Aqui está um exemplo de como o arquivo pode ser configurado:

```yaml
version: '3.8'

services:
  app:
    build: .
    container_name: secureusermanager-app
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=prod
    depends_on:
      - db

  db:
    image: postgres:13
    container_name: secureusermanager-db
    environment:
      POSTGRES_USER: user
      POSTGRES_PASSWORD: password
      POSTGRES_DB: secureusermanager
    ports:
      - "5432:5432"
```

```
# Etapa 1: Build do projeto
FROM eclipse-temurin:23-jdk AS builder
WORKDIR /app

# Instalação do Maven
RUN apt-get update && apt-get install -y maven && rm -rf /var/lib/apt/lists/*

# Copiar arquivos do projeto para o container
COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src

# Construir o projeto (gera o JAR na pasta target)
RUN mvn clean package -DskipTests

# Etapa 2: Runtime (executar a aplicação)
FROM eclipse-temurin:23-jre
WORKDIR /app

# Copiar o JAR gerado na etapa de build
COPY --from=builder /app/target/*.jar app.jar

# Definir a porta que a aplicação irá expor
EXPOSE 8080

# Comando para rodar a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]

# Nome da imagem
LABEL name="usermanagement"
```


### Explicação:

1. **services**: Define os serviços (contêineres) que serão orquestrados pelo Docker Compose.
2. **app**: Serviço que constrói a imagem a partir do **Dockerfile** e executa a aplicação Spring Boot.
   - `build: .` indica que a construção da imagem deve ser feita no diretório atual.
   - `ports` mapeia a porta 8080 do contêiner para a porta 8080 do host.
   - `depends_on` garante que o banco de dados esteja disponível antes de iniciar o aplicativo.
3. **db**: Serviço para o banco de dados PostgreSQL.
   - Usa a imagem oficial `postgres:13`.
   - Define variáveis de ambiente para configurar o usuário, senha e nome do banco de dados.

## Como Contribuir

Se você deseja contribuir para o projeto, pode enviar um **pull request** com suas alterações, ou abrir uma **issue** para relatar bugs ou sugerir melhorias.

## Licença

Este projeto está licenciado sob a MIT License - veja o arquivo LICENSE para mais detalhes.

```

### Explicação do `README.md`:

- **Dockerfile**: Instruções sobre como construir e rodar a aplicação usando o Docker.
- **docker-compose.yml**: Configuração para orquestrar múltiplos serviços, como a aplicação e o banco de dados.
- **Comandos Docker e Docker Compose**: Explicações sobre como construir a imagem, rodar os contêineres, e gerenciar os serviços.


Essa versão do `README.md` inclui todas as informações sobre como utilizar o **Makefile**, Docker, Docker Compose, além das instruções completas para configurar e rodar o projeto localmente.