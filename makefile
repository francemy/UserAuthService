# Definir variáveis do projeto
PROJECT_NAME=usermanagement
TARGET_DIR=target
MAVEN_CMD=mvn
# Git commands
BRANCH=$(shell git branch --show-current) # Branch atual
MESSAGE="Commit automático" # Mensagem padrão de commit
IMAGE_NAME="usermanagement" # Nome da imagem Docker
CONTAINER_NAME="usermanagement-container"

# Define as variáveis para o build e o comando de execução do Spring Boot
BUILD_CMD=$(MAVEN_CMD) clean install -DskipTests
RUN_CMD=$(MAVEN_CMD) spring-boot:run
# Detecta o sistema operacional
all: print-message
# Exibe o sistema operacional detectado
print-os:
	@echo "Sistema operacional detectado : $(OS)"

# Define comandos específicos para cada sistema operacional
ifeq ($(OS),Windows_NT)
    IS_WINDOWS := true
    RM = del /F /Q
    MKDIR = mkdir
    RMDIR = rmdir /S /Q
    TOUCH = type nul >
    SHELL := powershell.exe
    CLEAR_CMD = @powershell -Command "Clear-Host"
    SLEEP := @timeout /t
    STOP_CMD := for /f "tokens=*" %%i in ('docker ps -a -q') do docker stop %%i
    REMOVE_CMD = for /f "tokens=*" %%i in ('docker ps -a -q') do docker rm -v %%i
    VOLUME_PRUNE = docker volume prune -f
    CHECK_GIT_DIFF := @git diff --quiet > nul 2>&1 || (echo "Alterações detectadas." && exit /b 1)
else
    IS_WINDOWS := false
    RM = rm -f
    MKDIR = mkdir -p
    RMDIR = rm -rf
    TOUCH = touch
    CLEAR_CMD = @clear # No Linux, usar clear
    SLEEP := @sleep
    STOP_CMD := docker ps -a -q | xargs docker stop
    REMOVE_CMD = docker ps -a -q | xargs docker rm -v
    VOLUME_PRUNE = docker volume prune -f
    CHECK_GIT_DIFF :=  @if git diff --quiet; then echo "Nenhuma alteração detectada, sem necessidade de commit e push."; exit 0; fi
endif

# Regra para construir o projeto (compilação e teste)
build:
	@echo "Iniciando o processo de build..."
	$(BUILD_CMD)
	@echo "Build concluído com sucesso!"

# Regra para rodar o aplicativo Spring Boot
run:
	@echo "Iniciando a aplicação Spring Boot..."
	$(RUN_CMD)
	@echo "Aplicação finalizada."

# Regra para limpar o projeto (limpar arquivos temporários e artefatos)
clean:
	@echo "Limpando o projeto e removendo artefatos gerados..."
	$(MAVEN_CMD) clean
	@echo "Limpeza concluída!"

# Regra para gerar o arquivo JAR
package:
	@echo "Gerando o arquivo JAR..."
	$(MAVEN_CMD) package
	@echo "Arquivo JAR gerado com sucesso em $(TARGET_DIR)!"

# Docker e Docker Compose
docker-build:
	@echo "Construindo a imagem Docker..."
	docker build -t $(PROJECT_NAME) .
	@echo "Imagem Docker construída com sucesso!"

docker-run:
	@echo "Iniciando o contêiner Docker..."
	docker run -p 8080:8080 $(PROJECT_NAME)
	@echo "Contêiner Docker encerrado."

docker-clean:
	@echo "Limpando recursos Docker não utilizados..."
	docker system prune -f
	@echo "Recursos Docker limpos!"

docker-compose-up:
	@echo "Subindo os serviços definidos no docker-compose.yml..."
	docker-compose up -d
	@echo "Serviços iniciados com sucesso!"

docker-compose-down:
	@echo "Encerrando os serviços definidos no docker-compose.yml..."
	docker-compose down
	@echo "Serviços encerrados."

docker-clean-all:
	@echo "Parando e removendo todos os contêineres e volumes..."
	@$(STOP_CMD)
	@$(REMOVE_CMD)
	@$(VOLUME_PRUNE)
	@echo "Contêineres e volumes removidos com sucesso!"

docker-stop:
	@echo "Encerrando o contêiner Docker..."
	docker stop $(PROJECT_NAME)
	docker rm $(PROJECT_NAME)

docker-app-health-check:
	@echo "Verificando a saúde da aplicação no contêiner..."
	@if $(IS_WINDOWS); then \
		curl --silent --fail http://localhost:8080; \
		if ($?) { \
			echo "Aplicação acessível na porta 8080!"; \
		} else { \
			echo "Aplicação não está acessível!" && exit 1; \
		} \
	else \
		curl --silent --fail http://localhost:8080 || (echo "Aplicação não está acessível!" && exit 1); \
	fi

docker-logs:
	@echo "Exibindo os logs do contêiner Docker..."
	docker logs $(CONTAINER_NAME)

# Gerenciamento de arquivos e diretórios
mdir:
	@echo "Criando o diretório $(TARGET_DIR)..."
	$(MKDIR) $(TARGET_DIR)
	@echo "Diretório $(TARGET_DIR) criado com sucesso!"

rmd:
	@echo "Removendo o diretório $(TARGET_DIR)..."
	$(RMDIR) $(TARGET_DIR)
	@echo "Diretório $(TARGET_DIR) removido."

limpar:
	@echo "Limpando o terminal... $(CLEAR_CMD)"
	$(CLEAR_CMD)

check-file-exists:
	@echo "Verificando se o arquivo example-file.txt existe em $(TARGET_DIR)..."
	@if [ -f "$(TARGET_DIR)/example-file.txt" ]; then \
		echo "O arquivo example-file.txt existe."; \
	else \
		echo "O arquivo example-file.txt não existe."; \
	fi

check-dir-exists:
	@echo "Verificando se o diretório $(TARGET_DIR) existe..."
	@if [ -d "$(TARGET_DIR)" ]; then \
		echo "O diretório $(TARGET_DIR) existe."; \
	else \
		echo "O diretório $(TARGET_DIR) não existe."; \
	fi

jar:
	@echo "Criando o arquivo JAR do projeto..."
	$(MAVEN_CMD) clean package
	@echo "Arquivo JAR gerado com sucesso em $(TARGET_DIR)!"

run-jar:
	@echo "Executando o arquivo JAR do projeto..."
	java -jar $(TARGET_DIR)/$(PROJECT_NAME).jar
	@echo "Aplicação encerrada."

update-deps:
	@echo "Atualizando dependências do projeto..."
	$(MAVEN_CMD) clean install
	@echo "Dependências atualizadas com sucesso!"

# Verificar status do repositório
status:
	@echo "Verificando status do repositório..."
	git status

# Adicionar todas as alterações ao stage
add:
	@echo "Adicionando todas as alterações ao stage..."
	git add .

# Commit com mensagem padrão ou personalizada
commit:
	@if [ -z "$(message)" ]; then \
		echo "Realizando commit com a mensagem padrão: $(MESSAGE)..."; \
		git commit -m "$(MESSAGE)"; \
	else \
		echo "Realizando commit com a mensagem personalizada: $(message)..."; \
		git commit -m "$(message)"; \
	fi

# Enviar alterações para a branch atual
push:
	@echo "Enviando alterações para a branch $(BRANCH)..."
	git push origin $(BRANCH)

# Verificar alterações antes do commit
CHECK-GIT-DIFF:
	@echo "Verificando alterações..."
	ifeq ($(OS),Windows_NT)
		git diff --quiet || (echo "Alterações detectadas." && exit 1)
	else
		git diff --quiet || (echo "Alterações detectadas." && exit 1)
	endif


# Atualizar a branch atual com alterações remotas
pull:
	@echo "Atualizando a branch $(BRANCH) com alterações remotas..."
	git pull origin $(BRANCH)

# Criar uma nova branch
new-branch:
	@echo "Criando a nova branch: $(BRANCH_NAME)..."
	git checkout -b $(BRANCH_NAME)

# Trocar para uma branch existente
switch-branch:
	@echo "Trocando para a branch existente: $(BRANCH_NAME)..."
	git checkout $(BRANCH_NAME)

# Excluir uma branch local
delete-branch:
	@echo "Excluindo a branch local: $(BRANCH_NAME)..."
	git branch -d $(BRANCH_NAME)

# Listar todas as branches locais
branches:
	@echo "Listando todas as branches locais..."
	git branch

# Excluir uma branch remota
delete-remote-branch:
	@echo "Excluindo a branch remota: $(BRANCH_NAME)..."
	git push origin --delete $(BRANCH_NAME)

# Verificar logs do Git
log:
	@echo "Exibindo logs do Git..."
	git log --oneline --graph --decorate --all

# Comandos informativos e de ajuda
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

# Testar se o Docker está em execução corretamente no sistema
docker-health-check:
	@echo "Verificando se o Docker está em execução no sistema..."
	@if $(IS_WINDOWS); then \
		docker info >/dev/null || (echo "Docker não está em execução!" && exit 1); \
	else \
		docker info >/dev/null || (echo "Docker não está em execução!" && exit 1); \
	fi
	@echo "Docker está em execução corretamente!"

# Definir meta-alvos padrão
.DEFAULT_GOAL := help
