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

## Cobertura de testes (foco em regras de negocio)
Este repositorio ja possui relatorio HTML de cobertura dentro do proprio projeto, em `htmlReport/index.html`.

Como usar no dia a dia:
1. Execute os testes para validar o estado atual:

```powershell
./mvnw.cmd test
```

2. Abra no navegador o arquivo do repositorio `htmlReport/index.html`.
3. Ordene por `Class` ou `Line` e confira primeiro:
   - `com.codegroup.project.projeto.ProjetoBusinessRules`
   - `com.codegroup.project.projeto.ProjetoService`
   - `com.codegroup.project.membro.MembroService`

Endereco dos resultados no repositorio:
- Cobertura das regras de negocio (visual): `htmlReport/index.html`
- Resultado da execucao dos testes (JUnit/Surefire): `target/surefire-reports/`
- Arquivos mais diretos das regras de negocio: `target/surefire-reports/TEST-com.codegroup.project.projeto.ProjetoBusinessRulesTest.xml` e `target/surefire-reports/TEST-com.codegroup.project.projeto.ProjetoServiceTest.xml`

Como interpretar resultado satisfatorio para as rules:
- Meta tecnica do desafio: pelo menos `70%` de cobertura nas regras de negocio.
- Priorize o percentual das classes de regra (nao apenas o total global do projeto).

Sugestao rapida: sempre que adicionar/alterar regra em service/business rules, atualize o teste correspondente e reabra `htmlReport/index.html` para confirmar impacto.

Swagger/OpenAPI:
- `http://localhost:8080/swagger-ui/index.html`

Autenticacao basica:
- usuario: `admin`
- senha: `123456`

