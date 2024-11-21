# Definir variáveis do projeto
PROJECT_NAME=usermanagement
TARGET_DIR=target
MAVEN_CMD=mvn

IMAGE_NAME="usermanagement" # Nome da imagem Docker
CONTAINER_NAME="usermanagement-container"

# Define as variáveis para o build e o comando de execução do Spring Boot
BUILD_CMD=$(MAVEN_CMD) clean install -DskipTests
RUN_CMD=$(MAVEN_CMD) spring-boot:run

# Verifica se o sistema é Windows
ifeq ($(OS),Windows_NT)
    RM = del /F /Q
    MKDIR = mkdir
    RMDIR = rmdir /S /Q
    TOUCH = type nul >
    CLEAR_CMD = cls
else
    CLEAR_CMD = clear
    RM = rm -f
    MKDIR = mkdir -p
    RMDIR = rm -rf
    TOUCH = touch
endif

# Regra padrão (quando você apenas digita "make")
all: build

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

# Regra para rodar os testes
test:
	@echo "Executando os testes do projeto..."
	$(MAVEN_CMD) test
	@echo "Testes finalizados."

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
	# Verifica se o sistema é Windows
ifeq ($(OS),Windows_NT)
    STOP_CMD = for /f "tokens=*" %%i in ('docker ps -a -q') do docker stop %%i
    REMOVE_CMD = for /f "tokens=*" %%i in ('docker ps -a -q') do docker rm -v %%i
    VOLUME_PRUNE = docker volume prune -f
else
    STOP_CMD = docker ps -a -q | xargs docker stop
    REMOVE_CMD = docker ps -a -q | xargs docker rm -v
    VOLUME_PRUNE = docker volume prune -f
endif

# Comando para parar e remover todos os contêineres e volumes
docker-clean-all:
	@echo "Parando e removendo todos os contêineres e volumes..."
	@$(STOP_CMD)
	@$(REMOVE_CMD)
	@$(VOLUME_PRUNE)
	@echo "Contêineres e volumes removidos com sucesso!"

docker-stop:
	@echo "Encerrando os serviços definidos no docker e limpando o projeto..."
	docker stop $(PROJECT_NAME)
	docker rm $(PROJECT_NAME)

docker-logs:
	@echo "Exibindo os logs do container Docker..."
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
	@echo "Limpando o terminal..."
	@$(CLEAR_CMD)

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

# Git commands
BRANCH=$(shell git branch --show-current) # Branch atual
MESSAGE="Commit automático" # Mensagem padrão de commit

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

git-config:
	@echo "Configurando Git com nome de usuário e e-mail..."
	@if echo $$OS | grep -i "Windows_NT" > /dev/null; then 
		# Para Windows, usa set /p para ler as variáveis
		echo "Digite seu nome de usuário do Git:"; 
		set /p GIT_USER_NAME=""; 
		echo "Digite seu e-mail do Git:"; 
		set /p GIT_USER_EMAIL=""; 
		git config --global user.name "$$GIT_USER_NAME"; 
		git config --global user.email "$$GIT_USER_EMAIL"; 
		echo "Git configurado com sucesso!"; 
	else 
		# Para Linux/macOS, usa read
		read -p "Digite seu nome de usuário do Git: " GIT_USER_NAME; 
		read -p "Digite seu e-mail do Git: " GIT_USER_EMAIL; 
		git config --global user.name "$$GIT_USER_NAME"; 
		git config --global user.email "$$GIT_USER_EMAIL"; 
		echo "Git configurado com sucesso!"; 
	fi


push-speed:
	@echo "Verificando se há alterações no repositório..."
	@git diff --quiet || ( 
		echo "Alterações detectadas. Adicionando mudanças..."; 
		git add .; 
		# Verificar o sistema operacional para capturar a mensagem de commit
		@if echo $$OS | grep -i "Windows_NT" > /dev/null; then 
			# Para Windows, usa set /p para ler a mensagem do commit
			echo "Digite a mensagem para o commit:"; 
			set /p MESSAGE=""; 
		else 
			# Para Linux/macOS, usa read
			read -p "Digite a mensagem para o commit: " MESSAGE; 
		fi; 
		# Se nenhuma mensagem for fornecida, atribui uma mensagem padrão
		if [ -z "$$MESSAGE" ]; then 
			MESSAGE="Commit automático"; 
		fi; 
		echo "Realizando commit com a mensagem: $$MESSAGE..."; 
		git commit -m "$$MESSAGE"; 
		echo "Enviando alterações para o repositório..."; 
		git push origin $(BRANCH) 
	) || echo "Nenhuma alteração detectada, sem necessidade de commit e push."


# Pull da branch atual
pull:
	@echo "Atualizando a branch $(BRANCH) com alterações remotas..."
	git pull origin $(BRANCH)

# Criar uma nova branch (uso: make new-branch BRANCH_NAME=feature/minha-branch)
new-branch:
	@echo "Criando a nova branch: $(BRANCH_NAME)..."
	git checkout -b $(BRANCH_NAME)

# Trocar para uma branch existente (uso: make switch-branch BRANCH_NAME=develop)
switch-branch:
	@echo "Trocando para a branch existente: $(BRANCH_NAME)..."
	git checkout $(BRANCH_NAME)

# Excluir uma branch local (uso: make delete-branch BRANCH_NAME=feature/minha-branch)
delete-branch:
	@echo "Excluindo a branch local: $(BRANCH_NAME)..."
	git branch -d $(BRANCH_NAME)

# Listar todas as branches locais
branches:
	@echo "Listando todas as branches locais..."
	git branch

# Excluir uma branch remota (uso: make delete-remote-branch BRANCH_NAME=feature/minha-branch)
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
	@echo "Docker e Docker Compose:"
	@echo " - make docker-build: Construir a imagem Docker do projeto."
	@echo " - make docker-run: Iniciar um contêiner Docker baseado na imagem gerada."
	@echo " - make docker-clean: Limpar recursos não utilizados do Docker."
	@echo " - make docker-compose-up: Subir os serviços definidos no docker-compose.yml."
	@echo " - make docker-compose-down: Parar e remover os serviços definidos no docker-compose.yml."
	@echo " - make docker-clean-all: Parar e remover todos os contêineres e volumes Docker."
	@echo " - make docker-stop: Parar o contêiner Docker do projeto."
	@echo " - make docker-logs: Exibir os logs do contêiner Docker."
	@echo ""
	@echo "Gerenciamento de arquivos e diretórios:"
	@echo " - make mdir: Criar o diretório $(TARGET_DIR)."
	@echo " - make rmd: Remover o diretório $(TARGET_DIR)."
	@echo " - make limpar: Limpar a tela do terminal."
	@echo " - make check-file-exists: Verificar se o arquivo 'example-file.txt' existe no diretório $(TARGET_DIR)."
	@echo " - make check-dir-exists: Verificar se o diretório $(TARGET_DIR) existe."
	@echo " - make jar: Gerar o arquivo JAR do projeto."
	@echo " - make run-jar: Executar o arquivo JAR gerado pelo Maven."
	@echo " - make update-deps: Atualizar as dependências do projeto com Maven."
	@echo ""
	@echo "Comandos do Git:"
	@echo " - make status: Verificar o status do repositório Git."
	@echo " - make add: Adicionar todas as alterações ao stage do Git."
	@echo " - make commit \"mensagem\": Realizar um commit com a mensagem especificada (ou padrão se não fornecida)."
	@echo " - make push: Enviar alterações para a branch atual no repositório remoto."
	@echo " - make pull: Atualizar a branch atual com as alterações remotas."
	@echo " - make new-branch BRANCH_NAME=\"nome\": Criar uma nova branch com o nome especificado."
	@echo " - make switch-branch BRANCH_NAME=\"nome\": Alternar para uma branch existente."
	@echo " - make delete-branch BRANCH_NAME=\"nome\": Excluir uma branch local."
	@echo " - make delete-remote-branch BRANCH_NAME=\"nome\": Excluir uma branch remota."
	@echo " - make branches: Listar todas as branches locais."
	@echo " - make log: Exibir o log de commits do Git."
	@echo ""
	@echo "Observação: Para comandos do Git que requerem parâmetros como BRANCH_NAME, use a sintaxe 'make comando BRANCH_NAME=nome-da-branch'."
