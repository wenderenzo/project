# Project API

API Spring Boot para gestao de projetos e membros com regras de negocio, seguranca basica.

## O que foi implementado
- CRUD de `projetos` com paginacao e filtros (`nome`, `status`, `gerenteId`).
- API de membros mockada em `membros-externos` (criar e consultar).
- Associacao de membros ao projeto com validacoes de alocacao.
- Relatorio em `GET /projetos/relatorio`.
- Seguranca HTTP Basic em memoria (`admin` / `123456`).

## Regras de negocio cobertas
- Risco dinamico por prazo e orcamento (`BAIXO`, `MEDIO`, `ALTO`).
- Transicao de status sem pular etapas.
- Bloqueio de exclusao para status `iniciado`, `em andamento`, `encerrado`.
- Apenas `funcionario` pode ser associado.
- Projeto com 1 a 10 membros.
- Membro em no maximo 3 projetos ativos (nao encerrado/cancelado).

## Estrutura principal
- `src/main/java/com/codegroup/project/projeto/...`
- `src/main/java/com/codegroup/project/membro/...`
- `src/main/java/com/codegroup/project/config/SecurityConfig.java`
- `src/main/java/com/codegroup/project/common/...`
- `src/test/java/com/codegroup/project/projeto/...`

## Diagramas

### Arquitetura
#### Visao de camadas MVC
<img src="docs/diagrams/Diagrama de Classes Principais Visao de camadas.svg" alt="Visao de camadas MVC" />

#### Modelo de dominio
<img src="docs/diagrams/Diagrama de Classes Principais Modelo de dominio.svg" alt="Modelo de dominio" />

### Regras de negocio e estados
#### Fluxo de status do projeto
<img src="docs/diagrams/Diagrama de Classes Principais Fluxo de status do projeto.svg" alt="Fluxo de status do projeto" />

#### Regras de classificacao de risco
<img src="docs/diagrams/Diagrama de Classes Principais Regras de classificacao de risco.svg" alt="Regras de classificacao de risco" />

#### Regras de alocacao de membros
<img src="docs/diagrams/Diagrama de Classes Principais Regras de alocacao de membros.svg" alt="Regras de alocacao de membros" />

#### Regra de exclusao
<img src="docs/diagrams/Diagrama de Classes Principais Regra de exclusao.svg" alt="Regra de exclusao" />

### Fluxos de API
#### Criacao de projeto
<img src="docs/diagrams/Diagrama de Classes Principais Criacao de projeto.svg" alt="Criacao de projeto" />

#### Associacao de membros ao projeto
<img src="docs/diagrams/Diagrama de Classes Principais Associacao de membros ao projeto.svg" alt="Associacao de membros ao projeto" />

#### Tratamento de erro de negocio
<img src="docs/diagrams/Diagrama de Classes Principais Tratamento de erro de negocio.svg" alt="Tratamento de erro de negocio" />

Referencias em Markdown:
- `docs/architecture.md`
- `docs/business-rules-state-flow.md`
- `docs/api-flow.md`

## Execucao
Use o Maven Wrapper na raiz:

```powershell
./mvnw.cmd test
./mvnw.cmd spring-boot:run
```

Swagger/OpenAPI:
- `http://localhost:8080/swagger-ui/index.html`

Autenticacao basica:
- usuario: `admin`
- senha: `123456`

