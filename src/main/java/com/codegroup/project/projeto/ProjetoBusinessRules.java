package com.codegroup.project.projeto;

import com.codegroup.project.common.exception.BusinessException;
import com.codegroup.project.membro.entity.Membro;
import com.codegroup.project.projeto.entity.Projeto;
import com.codegroup.project.projeto.entity.ProjetoStatus;
import com.codegroup.project.projeto.entity.RiscoProjeto;
import java.math.BigDecimal;
import java.text.Normalizer;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

public final class ProjetoBusinessRules {

    private static final BigDecimal LIMITE_BAIXO = new BigDecimal("100000");
    private static final BigDecimal LIMITE_ALTO = new BigDecimal("500000");
    private static final Set<ProjetoStatus> STATUS_NAO_EXCLUIVEIS = EnumSet.of(
        ProjetoStatus.INICIADO,
        ProjetoStatus.EM_ANDAMENTO,
        ProjetoStatus.ENCERRADO
    );
    private static final Set<ProjetoStatus> STATUS_FINALIZADOS = EnumSet.of(
        ProjetoStatus.ENCERRADO,
        ProjetoStatus.CANCELADO
    );

    private ProjetoBusinessRules() {
    }

    public static void validarDatas(LocalDate dataInicio, LocalDate previsaoTermino) {
        if (previsaoTermino.isBefore(dataInicio)) {
            throw new BusinessException("Previsao de termino nao pode ser anterior a data de inicio");
        }
    }

    public static RiscoProjeto classificarRisco(BigDecimal orcamento, LocalDate dataInicio, LocalDate previsaoTermino) {
        long meses = calcularMesesComArredondamento(dataInicio, previsaoTermino);
        if (orcamento.compareTo(LIMITE_BAIXO) <= 0 && meses <= 3) {
            return RiscoProjeto.BAIXO;
        }
        if (orcamento.compareTo(LIMITE_ALTO) > 0 || meses > 6) {
            return RiscoProjeto.ALTO;
        }
        return RiscoProjeto.MEDIO;
    }

    public static void validarTransicaoStatus(ProjetoStatus atual, ProjetoStatus proximo) {
        if (atual == null || atual == proximo) {
            return;
        }
        if (proximo == ProjetoStatus.CANCELADO) {
            if (atual == ProjetoStatus.ENCERRADO) {
                throw new BusinessException("Projeto encerrado nao pode ser cancelado");
            }
            return;
        }
        if (proximo.ordinal() != atual.ordinal() + 1) {
            throw new BusinessException("Transicao de status invalida: nao e permitido pular etapas");
        }
    }

    public static void validarExclusao(ProjetoStatus status) {
        if (STATUS_NAO_EXCLUIVEIS.contains(status)) {
            throw new BusinessException("Projetos iniciados, em andamento ou encerrados nao podem ser excluidos");
        }
    }

    public static void validarAlocacaoMembros(
        Projeto projeto,
        List<Membro> membros,
        Function<Long, Long> contadorProjetosAtivos
    ) {
        if (membros.isEmpty() || membros.size() > 10) {
            throw new BusinessException("Cada projeto deve ter entre 1 e 10 membros");
        }

        for (Membro membro : membros) {
            if (!isFuncionario(membro.getAtribuicao())) {
                throw new BusinessException("Somente membros com atribuicao funcionario podem ser alocados");
            }
            long projetosAtivos = contadorProjetosAtivos.apply(membro.getId());
            boolean jaNoProjeto = projeto.getMembros().stream().anyMatch(item -> item.getId().equals(membro.getId()));
            boolean projetoContaComoAtivo = !STATUS_FINALIZADOS.contains(projeto.getStatus());
            long totalAtivosComProjetoAtual = projetosAtivos + ((!jaNoProjeto && projetoContaComoAtivo) ? 1 : 0);
            if (totalAtivosComProjetoAtual > 3) {
                throw new BusinessException("Um membro nao pode participar de mais de 3 projetos ativos");
            }
        }
    }

    public static long calcularDuracaoDias(LocalDate inicio, LocalDate fim) {
        return ChronoUnit.DAYS.between(inicio, fim);
    }

    private static long calcularMesesComArredondamento(LocalDate inicio, LocalDate fim) {
        long meses = ChronoUnit.MONTHS.between(inicio, fim);
        LocalDate base = inicio.plusMonths(meses);
        return base.isBefore(fim) ? meses + 1 : meses;
    }

    private static boolean isFuncionario(String atribuicao) {
        if (atribuicao == null) {
            return false;
        }
        String normalized = Normalizer.normalize(atribuicao, Normalizer.Form.NFD)
            .replaceAll("\\p{M}", "")
            .trim()
            .toLowerCase();
        return "funcionario".equals(normalized);
    }
}


