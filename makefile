# Variáveis do projeto
PROJECT_NAME=usermanagement
TARGET_DIR=target
MAVEN_CMD=mvn
IMAGE_NAME=usermanagement
CONTAINER_NAME=usermanagement-container
CONTAINER_REDIS=redis
CONTAINER_NGINX=nginx_proxy
MESSAGE="Commit automático" # Mensagem padrão de commit

# Comando para build e execução do Spring Boot
BUILD_CMD=$(MAVEN_CMD) clean install -DskipTests
RUN_CMD=$(MAVEN_CMD) spring-boot:run

# Detectar sistema operacional
ifeq ($(OS),Windows_NT)
    IS_WINDOWS := true
    RM = del /F /Q
    MKDIR = mkdir
    RMDIR = rmdir /S /Q
    TOUCH = type nul >
    SHELL := powershell.exe
    CLEAR_CMD = @powershell -Command "Clear-Host"
    SLEEP = timeout /t
    READ = powershell -Command "Read-Host 'Digite os caminhos:'"
	DOCKER_STOP_CMD := powershell -Command docker ps -q | % { docker stop $_ }
	DOCKER_RM_CMD := powershell -Command "docker ps -a -q | ForEach-Object { docker rm $_ }"
else
    IS_WINDOWS := false
    RM = rm -f
    MKDIR = mkdir -p
    RMDIR = rm -rf
    TOUCH = touch
    CLEAR_CMD = clear
    SLEEP = sleep
    READ = read -p "Digite os caminhos: "
	DOCKER_STOP_CMD := @docker stop $(docker ps -q)
	DOCKER_RM_CMD := docker ps -a -q | xargs -r docker rm
endif

# Regras principais
all: help

# Build do projeto
build:
	@echo "Iniciando o processo de build..."
	$(BUILD_CMD)
	@echo "Build concluído com sucesso!"

run:
	@echo "Iniciando a aplicação Spring Boot..."
	$(RUN_CMD)
	@echo "Aplicação encerrada."

clean:
	@echo "Limpando o projeto e removendo artefatos gerados..."
	$(MAVEN_CMD) clean
	@echo "Limpeza concluída!"

package:
	@echo "Gerando o arquivo JAR..."
	$(MAVEN_CMD) package
	@echo "Arquivo JAR gerado com sucesso em $(TARGET_DIR)!"

# Docker
docker-build:
	@echo "Construindo a imagem Docker..."
	docker build -t $(IMAGE_NAME) .
	@echo "Imagem Docker construída com sucesso!"

docker-logs:
	@$(READ) CONTAINER
	docker logs $(CONTAINER)

docker-run:
	@echo "Iniciando o contêiner Docker..."
	docker run -p 8080:8080 $(IMAGE_NAME)
	@echo "Contêiner encerrado."

docker-stop:
	@echo "Parando todos os containers Docker..."
	$(DOCKER_STOP_CMD)
	@echo "Encerrando serviços do Docker Compose..."
	@docker-compose down

docker-clean-all:
	@echo "Parando todos os containers Docker..."
	$(DOCKER_STOP_CMD)
	@echo "Removendo containers parados..."
	$(DOCKER_RM_CMD)
	@echo "Removendo volumes não utilizados..."
	@docker volume prune -f
	@echo "Removendo imagens não utilizadas..."
	@docker image prune -a -f
	@echo "Removendo redes não utilizadas..."
	@docker network prune -f
	@echo "Limpeza concluída!"

docker-clean:
	@echo "Removendo containers parados..."
	$(DOCKER_RM_CMD)
	@echo "Removendo volumes não utilizados..."
	@docker volume prune -f
	@echo "Removendo imagens não utilizadas..."
	@docker image prune -a -f
	@echo "Removendo redes não utilizadas..."
	@docker network prune -f
	@echo "Limpeza concluída!"

docker-compose-up:
	@echo "Subindo os serviços..."
	docker-compose up -d
	@echo "Serviços iniciados com sucesso!"

docker-compose-down:
	@echo "Encerrando os serviços..."
	docker-compose down
	@echo "Serviços encerrados."

docker-health-check:
	@echo "Verificando saúde da aplicação no contêiner..."
	curl --silent --fail http://localhost:8080 || (echo "Aplicação não está acessível!" && exit 1)

# Gerenciamento de arquivos
mkdir:
	@$(READ) FILES
	@echo "Criando arquivos/diretórios..."
	@for path in $(FILES); do \
		if [ ! -e $$path ]; then \
			if [[ $$path == */ ]]; then \
				$(MKDIR) $$path; \
			else \
				$(TOUCH) $$path; \
			fi; \
		else \
			echo "$$path já existe."; \
		fi; \
	done

help:
	@echo "Comandos disponíveis no Makefile:"
	@echo ""
	@echo " - make build: Compilar o projeto, gerar artefatos (JAR) e executar o build do Maven."
	@echo " - make run: Iniciar a aplicação Spring Boot."
	@echo " - make clean: Limpar os artefatos do projeto (diretórios de build e dependências)."
	@echo " - make package: Gerar o arquivo JAR do projeto."
	@echo " - make test: Executar os testes do projeto com o Maven."
	@echo ""
	@echo "Docker:"
	@echo " - make docker-build: Construir a imagem Docker."
	@echo " - make docker-run: Executar o contêiner Docker."
	@echo " - make docker-clean: Limpar volumes e contêineres Docker."
	@echo " - make docker-compose-up: Subir os serviços definidos no docker-compose."
	@echo " - make docker-compose-down: Parar os serviços definidos no docker-compose."
	@echo " - make docker-clean-all: Parar e remover todos os contêineres e volumes Docker."
	@echo ""
	@echo "Git:"
	@echo " - make commit: Realizar um commit com uma mensagem."
	@echo " - make push: Enviar alterações para o repositório remoto."
	@echo " - make pull: Atualizar o repositório local com alterações remotas."
	@echo " - make status: Verificar o status do repositório Git."
	@echo " - make branches: Listar as branches locais."
	@echo ""
	@echo "Gerenciamento de Arquivos e Diretórios:"
	@echo " - make mdir: Criar o diretório $(TARGET_DIR)."
	@echo " - make rmd: Remover o diretório $(TARGET_DIR)."
	@echo " - make check-file-exists: Verificar se o arquivo example-file.txt existe."
	@echo " - make check-dir-exists: Verificar se o diretório $(TARGET_DIR) existe."
