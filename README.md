# Autofinance - Sistema de Controle Financeiro Pessoal

## Sobre o projeto

O Autofinance é um sistema web desenvolvido como meu primeiro projeto completo no back-end com Spring Boot. Surgiu da necessidade de organizar minhas próprias finanças de forma bastante visual, talvez até com certa gamificação e com uma interface moderna e intuitiva.

Antes de começar, eu usava planilhas, mas queria algo que me desse uma visão melhor dos gastos ao longo do tempo, que me permitisse visualizar de forma clara os gastos por categoria e que armazenasse os dados de forma persistente, podendo acessar de qualquer lugar e definir gastos que caem todo mês de forma automática.

O projeto está disponível gratuitamente em: [https://autofinance-tjkc.onrender.com](https://autofinance-tjkc.onrender.com)

---

## Funcionalidades principais

- **Autenticação de usuários**: cadastro, login e logout com senhas criptografadas (BCrypt). Bem como o sistema de recuperação de senha em caso de esquecimento, através do email.
- **Ciclo financeiro personalizado**: o usuário define um dia de fechamento (ex: dia 5). O dashboard mostra as transações do período entre esse dia e o dia anterior do mês seguinte.
- **Dashboard interativo**: cards com renda mensal, receitas do período, gastos totais e economia. Gráfico de bolhas para distribuição das despesas.
- **Lançamentos**: criação, listagem e exclusão de transações (receitas e despesas), com suporte a transações recorrentes (dia fixo do mês).
- **Metas financeiras**: cadastro de metas (nome, valor alvo, valor se houver) e acompanhamento de progresso.
- **Relatórios por período**: seleção de qualquer período já registrado, com cards, categorias mais gastas, gráfico de gastos por dia do mês e tabela de transações.
- **Calendário financeiro**: visualização mensal com marcação dos dias que possuem transações. Ao clicar, exibe os detalhes do dia. Também destaca o dia de fechamento do mês.
- **Perfil do usuário**: edição de nome, email, objetivo financeiro, renda mensal, avatar (com recorte de imagem), alteração de senha.
- **Tema claro/escuro**: persistente (salvo no perfil) e sincronizado entre dispositivos.
- **Responsividade**: layout adaptável para desktop e dispositivos móveis (Tailwind CSS).

---

## Tecnologias utilizadas

### Back-end
- **Java 21**
- **Spring Boot 3.3.0** (Web, Data JPA, Security, Validation)
- **PostgreSQL** (banco de dados de produção, via Neon)
- **H2** (banco de testes e desenvolvimento local)
- **BCrypt** (codificação de senhas)
- **Maven** (gerenciamento de dependências)

### Front-end
- **Thymeleaf** (templates HTML com integração com Spring)
- **Tailwind CSS** (estilização, com suporte a dark mode via classe `dark`)
- **Lucide** (ícones SVG)
- **Chart.js** (gráfico de barras nos relatórios)
- **Cropper.js** (recorte de imagem para avatar)
- **JavaScript puro** (fetch API para comunicação com o back-end)

### Infraestrutura / Deploy
- **Git / GitHub** (controle de versão)
- **Render** (hospedagem da aplicação e do banco PostgreSQL)
- **Neon** (PostgreSQL gratuito utilizado em produção)

---

## O que aprendi com este projeto

Como iniciante em back-end, enfrentei vários desafios que me ensinaram muito:

1. **Entender o fluxo do Spring Security**: configurar login, CSRF, permissões de rotas e codificação de senhas foi complicado no começo.
2. **Trabalhar com JPA e relacionamentos**: as entidades `User`, `Transaction` e `Goal` têm relacionamentos (OneToMany, ManyToOne) e precisei evitar loops com `@JsonIgnore`.
3. **Ciclo financeiro personalizado**: a lógica de calcular o período atual com base num dia de fechamento (que pode ser 1 a 28) e lidar com meses de tamanhos diferentes foi um dos pontos interessantes.
4. **Transações recorrentes**: implementar um scheduler que verifica diariamente se uma transação recorrente precisa ser gerada (e evitar duplicação) me mostrou a importância de testes.
5. **Deploy no Render**: configurar variáveis de ambiente, entender a diferença entre `application.properties` e `application-prod.properties`, resolver erros de conexão com banco e de falta de colunas (`theme`, `financial_month_day`) foi uma jornada, mas aprendi a lidar com logs.
6. **Dark mode com persistência**: salvar a preferência no banco de dados e sincronizar com localStorage + classe `dark` no HTML foi um desafio curioso de front-end.
7. **Gerenciamento de estado no front**: guardar transações, ciclos, metas em variáveis globais, atualizar o DOM depois de cada operação.

---

## Como executar localmente

### Pré-requisitos
- Java 21
- Maven
- PostgreSQL (ou pode usar H2 para teste)

### Passos
1. Clone o repositório:
   ```bash
   git clone https://github.com/rickegss/autofinance_springboot.git
   cd autofinance_springboot
   ```

2. Configure o banco de dados:
   - Copie o arquivo `src/main/resources/application.properties` e ajuste a URL do banco (ou use o H2 diretamente).
   - Crie um banco PostgreSQL chamado `autofinance` (ou modifique o nome).

3. Defina a variável de ambiente `DB_PASSWORD` com a senha do seu PostgreSQL.

4. Execute a aplicação:
   ```bash
   ./mvnw spring-boot:run
   ```

5. Acesse `http://localhost:8080`

> Se preferir usar H2 (sem instalar PostgreSQL), altere o `application.properties` para as configurações padrão.

---

## Estrutura de pastas (principais)

```
src/main/java/com/rickegss/autofinance/
├── config/               # SecurityConfig, GlobalExceptionHandler
├── controller/           # Controllers para páginas e API
├── dto/                  # Records para transferência de dados
├── entity/               # JPA entities (User, Transaction, Goal)
├── repository/           # Spring Data JPA repositories
├── scheduler/            # RecurringTransactionScheduler
└── service/              # Lógica de negócio (UserService, TransactionService, CycleService)
src/main/resources/
├── static/               # CSS, JS, imagens
├── templates/            # HTML Thymeleaf
└── application*.properties
```
---

## Segurança

O projeto foi desenvolvido pensando em boas práticas de segurança desde o início, mesmo sendo um estudo. Entre as medidas implementadas:

- **Proteção de senhas**: todas as senhas são codificadas com BCrypt (strength 10) antes de serem armazenadas no banco de dados.
- **Proteção CSRF**: o Spring Security mantém a proteção ativa (padrão), e o front-end envia o token CSRF em todas as requisições que modificam dados.
- **Headers de segurança**: o Spring Security adiciona automaticamente cabeçalhos como `X-Content-Type-Options: nosniff`, `X-Frame-Options: DENY` e `X-XSS-Protection: 1; mode=block`.
- **Conexão com banco**: o PostgreSQL utiliza SSL (via `?sslmode=require` na URL da conexão), garantindo que os dados trafeguem criptografados entre a aplicação e o banco.
- **Prevenção a SQL Injection**: o uso de JPA/Hibernate com consultas parametrizadas (JPQL e derived queries) elimina a concatenação de strings, protegendo contra injeção.
- **Escapamento de HTML**: os dados dinâmicos são escapados no front-end (função `escapeHtml`) para evitar ataques XSS.
- **Autorização por recurso**: os controllers verificam se o usuário logado é proprietário das transações ou metas antes de permitir exclusão ou alteração (controle no service layer).
- **Variáveis de ambiente**: dados sensíveis (como senha do banco) não estão fixados no código; são injetados via variáveis de ambiente no deploy (Render/Neon).
- **Tratamento de exceções**: um `@RestControllerAdvice` captura exceções comuns e retorna respostas padronizadas, evitando vazamento de informações internas.

---

## Melhorias futuras (ideias)

- Adicionar gráficos de evolução de patrimônio.
- Permitir edição de transações (hoje só exclui).
- Exportar relatórios em PDF/CSV.
- Suporte a múltiplas moedas ou categorias dinâmicas (hoje as categorias são fixas no front).

---

## Licença

Este projeto foi desenvolvido para fins de estudo e está disponível sob a licença MIT.

---

**Desenvolvido por Ricardo Gomes** – [GitHub](https://github.com/rickegss)
