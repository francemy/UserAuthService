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
