package com.codegroup.project.projeto;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.codegroup.project.common.exception.BusinessException;
import com.codegroup.project.membro.entity.Membro;
import com.codegroup.project.projeto.entity.Projeto;
import com.codegroup.project.projeto.entity.ProjetoStatus;
import com.codegroup.project.projeto.entity.RiscoProjeto;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;

class ProjetoBusinessRulesTest {

    @Test
    void deveClassificarRiscoBaixo() {
        RiscoProjeto risco = ProjetoBusinessRules.classificarRisco(
            new BigDecimal("100000"),
            LocalDate.of(2026, 1, 1),
            LocalDate.of(2026, 3, 31)
        );

        assertEquals(RiscoProjeto.BAIXO, risco);
    }

    @Test
    void deveClassificarRiscoAltoPorOrcamento() {
        RiscoProjeto risco = ProjetoBusinessRules.classificarRisco(
            new BigDecimal("500001"),
            LocalDate.of(2026, 1, 1),
            LocalDate.of(2026, 2, 1)
        );

        assertEquals(RiscoProjeto.ALTO, risco);
    }

    @Test
    void deveClassificarRiscoMedio() {
        RiscoProjeto risco = ProjetoBusinessRules.classificarRisco(
            new BigDecimal("150000"),
            LocalDate.of(2026, 1, 1),
            LocalDate.of(2026, 4, 30)
        );

        assertEquals(RiscoProjeto.MEDIO, risco);
    }

    @Test
    void naoDevePermitirPularStatus() {
        assertThrows(
            BusinessException.class,
            () -> ProjetoBusinessRules.validarTransicaoStatus(ProjetoStatus.EM_ANALISE, ProjetoStatus.PLANEJADO)
        );
    }

    @Test
    void devePermitirCancelarAntesDeEncerrar() {
        assertDoesNotThrow(
            () -> ProjetoBusinessRules.validarTransicaoStatus(ProjetoStatus.PLANEJADO, ProjetoStatus.CANCELADO)
        );
    }

    @Test
    void naoDevePermitirExcluirProjetoEmAndamento() {
        assertThrows(BusinessException.class, () -> ProjetoBusinessRules.validarExclusao(ProjetoStatus.EM_ANDAMENTO));
    }

    @Test
    void deveBloquearMembroNaoFuncionario() {
        Projeto projeto = novoProjeto(ProjetoStatus.PLANEJADO);
        Membro membro = novoMembro(10L, "terceiro");

        assertThrows(
            BusinessException.class,
            () -> ProjetoBusinessRules.validarAlocacaoMembros(projeto, List.of(membro), id -> 0L)
        );
    }

    @Test
    void deveBloquearMembroComMaisDeTresProjetosAtivos() {
        Projeto projeto = novoProjeto(ProjetoStatus.PLANEJADO);
        Membro membro = novoMembro(10L, "funcionario");

        assertThrows(
            BusinessException.class,
            () -> ProjetoBusinessRules.validarAlocacaoMembros(projeto, List.of(membro), id -> 3L)
        );
    }

    private static Projeto novoProjeto(ProjetoStatus status) {
        Projeto projeto = new Projeto();
        projeto.setId(1L);
        projeto.setStatus(status);
        return projeto;
    }

    private static Membro novoMembro(Long id, String atribuicao) {
        Membro membro = new Membro();
        membro.setId(id);
        membro.setNome("Membro " + id);
        membro.setAtribuicao(atribuicao);
        return membro;
    }
}

