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

