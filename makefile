# Definir variáveis do projeto
PROJECT_NAME=usermanagement
TARGET_DIR=target
MAVEN_CMD=mvn

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
    RM = del /F /Q
    MKDIR = mkdir
    RMDIR = rmdir /S /Q
    TOUCH = type nul >
    CLEAR_CMD = cls
	SLEEP := @timeout /t 
	STOP_CMD := for /f "tokens=*" %%i in ('docker ps -a -q') do docker stop %%i
    REMOVE_CMD = for /f "tokens=*" %%i in ('docker ps -a -q') do docker rm -v %%i
    VOLUME_PRUNE = docker volume prune -f
	CHECK_GIT_DIFF := @git diff --quiet > nul 2>&1 || (echo "Alterações detectadas." && exit /b 1) 
else
    RM = rm -f
    MKDIR = mkdir -p
    RMDIR = rm -rf
    TOUCH = touch
    CLEAR_CMD = clear
	SLEEP := @sleep
	STOP_CMD := docker ps -a -q | xargs docker stop
    REMOVE_CMD = docker ps -a -q | xargs docker rm -v
    VOLUME_PRUNE = docker volume prune -f
	CHECK_GIT_DIFF :=  @if git diff --quiet; then echo "Nenhuma alteração detectada, sem necessidade de commit e push."; exit 0; fi 
endif


# Regra padrão (quando você apenas digita "make")


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
	ifeq ($(OS), Windows_NT)
		@echo "Digite seu nome de usuário do Git (digite '\c' para cancelar):"
		@set /p GIT_USER_NAME=
		@if [ "$(GIT_USER_NAME)" = "\c" ]; then echo "Operação cancelada."; exit 0; fi
		@echo "Digite seu e-mail do Git (digite '\c' para cancelar):"
		@set /p GIT_USER_EMAIL=
		@if [ "$(GIT_USER_EMAIL)" = "\c" ]; then echo "Operação cancelada."; exit 0; fi
		@git config --global user.name "$(GIT_USER_NAME)"
		@git config --global user.email "$(GIT_USER_EMAIL)"
		@echo "Git configurado com sucesso!"
	else
		@read -p "Digite seu nome de usuário do Git (digite '\c' para cancelar): " GIT_USER_NAME
		@if [ "$$GIT_USER_NAME" = "\c" ]; then echo "Operação cancelada."; exit 0; fi
		@read -p "Digite seu e-mail do Git (digite '\c' para cancelar): " GIT_USER_EMAIL
		@if [ "$$GIT_USER_EMAIL" = "\c" ]; then echo "Operação cancelada."; exit 0; fi
		@git config --global user.name "$$GIT_USER_NAME"
		@git config --global user.email "$$GIT_USER_EMAIL"
		@echo "Git configurado com sucesso!"
	endif

push-speed:
	@echo "Verificando se há alterações no repositório..."
	@$(CHECK-GIT-DIFF)
	@echo "Alterações detectadas. Adicionando mudanças..."
	@git add .
	@echo "Digite a mensagem para o commit (digite '\c' para cancelar):"

	# Verificar o sistema operacional
	

# Verifica se há alterações usando git diff
CHECK-GIT-DIFF:
	@if [ "$(OS)" = "Windows_NT" ]; then \
		@echo "Verificando alterações..."; \
		git status --porcelain > nul 2>&1 || (echo "Alterações detectadas." && exit 1); \
	else \
		@echo "Verificando alterações..."; \
		if git status --porcelain | grep -q .; then \
			echo "Alterações detectadas."; \
		else \
			echo "Nenhuma alteração detectada."; \
			exit 0; \
		fi; \
	fi


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
	@echo " - make print-message: introdução ao Makefile"
	@echo " - make push-speed: push rapido com todas operações antes"
	@echo "Observação: Para comandos do Git que requerem parâmetros como BRANCH_NAME, use a sintaxe 'make comando BRANCH_NAME=nome-da-branch'."


print-message:
	@echo "Introdução ao Makefile"
	@echo "Este Makefile foi configurado para automatizar várias tarefas no desenvolvimento de um projeto de gestão de usuários com Spring Boot e Docker. O objetivo principal de um Makefile é simplificar a execução de comandos repetitivos, definindo regras e dependências para uma sequência de ações a serem realizadas com apenas um comando."
	@echo "A seguir, vamos passar por todas as partes deste arquivo:"
	$(SLEEP) 4
	@echo "Estrutura e Definições de Variáveis"
	@echo "No início do Makefile, você encontra várias variáveis definidas para facilitar o uso e configuração do projeto:"
	@echo "PROJECT_NAME: Nome do projeto, usado em diversas partes, como na construção da imagem Docker."
	@echo "TARGET_DIR: O diretório de destino onde os artefatos compilados serão armazenados."
	@echo "MAVEN_CMD: O comando para invocar o Maven, responsável pela construção do projeto."
	@echo "IMAGE_NAME e CONTAINER_NAME: Usados para manipulação de imagens e contêineres Docker."
	@echo "BUILD_CMD e RUN_CMD: Comandos específicos para compilar e executar a aplicação Spring Boot."
	@echo "Além disso, detectamos automaticamente o sistema operacional em uso (Windows, Unix/Linux, ou macOS) para ajustar os comandos conforme necessário."
	$(SLEEP) 4
	@echo "Comandos do Makefile"
	@echo "O Makefile possui várias regras que definem o comportamento de cada tarefa. Vou explicar cada uma delas:"
	@echo "1. Comando all:"
	@echo "A regra padrão (all) apenas chama a tarefa build, ou seja, se você rodar make sem especificar um comando, o Make executará o processo de construção."
	@echo "2. Comandos de Build e Execução:"
	@echo "build: Executa o comando de build do Maven, compilando o projeto e gerando os artefatos necessários."
	@echo "run: Inicia a aplicação Spring Boot."
	@echo "clean: Limpa os artefatos gerados pelo Maven, como arquivos temporários de build."
	@echo "package: Gera o arquivo JAR do projeto, pronto para ser distribuído ou executado."
	@echo "3. Docker e Docker Compose:"
	@echo "docker-build: Construa a imagem Docker."
	@echo "docker-run: Executa o contêiner Docker a partir da imagem."
	@echo "docker-clean: Limpa recursos não utilizados do Docker."
	@echo "docker-compose-up: Inicializa os serviços definidos no docker-compose.yml."
	@echo "docker-compose-down: Encerra os serviços."
	@echo "docker-clean-all: Para e remove todos os contêineres e volumes Docker."
	@echo "4. Comandos para Gerenciamento de Arquivos:"
	@echo "mdir e rmd: Criação e remoção de diretórios."
	@echo "limpar: Limpa o terminal."
	@echo "check-file-exists: Verifica a existência de um arquivo específico."
	@echo "check-dir-exists: Verifica a existência de um diretório específico."
	@echo "5. Comandos do Maven e Spring Boot:"
	@echo "jar: Gera o arquivo JAR do projeto."
	@echo "run-jar: Executa o arquivo JAR gerado."
	@echo "6. Git Commands:"
	@echo "status: Verifica o status do repositório."
	@echo "add: Adiciona as mudanças ao staging."
	@echo "commit: Realiza um commit no repositório com uma mensagem padrão ou personalizada."
	@echo "push: Envia as alterações para o repositório remoto."
	@echo "pull: Atualiza a branch local com alterações remotas."
	@echo "new-branch, switch-branch, delete-branch, delete-remote-branch: Comandos para gerenciamento de branches Git."
	@echo "log: Exibe o histórico de commits."
	$(SLEEP) 4
	@echo "Personalizações e Considerações Finais"
	@echo "Este Makefile é altamente flexível, com a capacidade de adicionar novos comandos para atender às necessidades específicas do seu fluxo de trabalho de desenvolvimento. Há várias oportunidades para personalizar e expandir o Makefile:"
	@echo "Adicionar comandos de integração contínua: Como comandos para testes de integração ou deploy automático para ambientes de staging/produção."
	@echo "Melhorias no Docker: Implementação de mais comandos relacionados ao Docker, como a configuração de volumes ou redes personalizadas."
	@echo "Verificação de dependências do Maven: Comandos para garantir que as dependências estejam sempre atualizadas ou até mesmo configurar um repositório privado de dependências."
	@echo "Automação do Versionamento: Criar uma regra para versionamento do código e atualizar tags no Git."
	$(SLEEP) 4
	@echo "Exemplo de Expansões:"
	@echo "Adição de teste de performance: Pode-se adicionar um comando que execute testes de performance antes ou após a construção do projeto."
	@echo "Scripts de Build Avançados: Como scripts que geram o build para diferentes ambientes (ex: produção, staging, desenvolvimento)."
	$(SLEEP) 4
	@echo "Agora, uma breve pausa de 4 segundos e vou continuar com algumas sugestões adicionais!"
	$(SLEEP) 4
	@echo "(Segundos de pausa...)"
	@echo "Agora, algumas ideias adicionais que podem ser úteis para adicionar ao seu Makefile:"
	@echo "Comandos de Deploy Automático: Se o projeto envolver múltiplos ambientes (desenvolvimento, staging, produção), é possível automatizar o deploy para esses ambientes usando comandos específicos para cada servidor."
	@echo "Execução de Scripts Pós-Deploy: Caso o projeto dependa de ações pós-deploy, como atualizações de banco de dados ou validações de configuração, esses comandos podem ser incluídos no Makefile para garantir que tudo ocorra automaticamente após a implantação."
	@echo "Verificação de Qualidade de Código: Usar ferramentas como SonarQube ou Checkstyle para rodar verificações de qualidade de código automaticamente após o build."
	@echo "Agendamentos de Tarefas: Se você tiver tarefas recorrentes (como geração de relatórios ou backup de dados), pode agendar esses comandos dentro do Makefile usando ferramentas de automação como cron (em sistemas Unix/Linux)."
	@echo "Com essas adições, seu Makefile não só automatiza o ciclo de vida de desenvolvimento, mas também melhora a integração com processos de qualidade, deploy e manutenção do projeto."