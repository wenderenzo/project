package com.codegroup.project.projeto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.codegroup.project.common.exception.BusinessException;
import com.codegroup.project.common.exception.NotFoundException;
import com.codegroup.project.membro.MembroService;
import com.codegroup.project.membro.entity.Membro;
import com.codegroup.project.projeto.dto.ProjetoRequest;
import com.codegroup.project.projeto.dto.ProjetoResponse;
import com.codegroup.project.projeto.dto.RelatorioProjetosResponse;
import com.codegroup.project.projeto.dto.StatusResumoResponse;
import com.codegroup.project.projeto.entity.Projeto;
import com.codegroup.project.projeto.entity.ProjetoStatus;
import com.codegroup.project.projeto.entity.RiscoProjeto;
import com.codegroup.project.projeto.repository.ProjetoRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.EnumSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProjetoServiceTest {

    @Mock
    private ProjetoRepository projetoRepository;

    @Mock
    private MembroService membroService;

    @InjectMocks
    private ProjetoService projetoService;

    private Membro gerente;

    @BeforeEach
    void setUp() {
        gerente = novoMembro(7L, "Gerente", "funcionario");
    }

    @Test
    void deveCriarProjetoComDadosDoRequest() {
        ProjetoRequest request = novoRequest(ProjetoStatus.EM_ANALISE, gerente.getId());

        when(membroService.buscarEntidade(gerente.getId())).thenReturn(gerente);
        when(projetoRepository.save(any(Projeto.class))).thenAnswer(invocation -> {
            Projeto projeto = invocation.getArgument(0);
            projeto.setId(100L);
            return projeto;
        });

        ProjetoResponse response = projetoService.criar(request);

        assertEquals(100L, response.id());
        assertEquals("Projeto Atlas", response.nome());
        assertEquals(ProjetoStatus.EM_ANALISE, response.status());
        assertEquals(RiscoProjeto.BAIXO, response.risco());
        assertEquals(gerente.getId(), response.gerenteId());
        verify(membroService).buscarEntidade(gerente.getId());
        verify(projetoRepository).save(any(Projeto.class));
    }

    @Test
    void deveBuscarProjetoPorId() {
        Projeto projeto = novoProjeto(1L, ProjetoStatus.PLANEJADO, gerente);

        when(projetoRepository.findById(1L)).thenReturn(Optional.of(projeto));

        ProjetoResponse response = projetoService.buscarPorId(1L);

        assertEquals(1L, response.id());
        assertEquals(ProjetoStatus.PLANEJADO, response.status());
    }

    @Test
    void deveLancarNotFoundQuandoProjetoNaoExiste() {
        when(projetoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> projetoService.buscarPorId(99L));
    }

    @Test
    void naoDeveExcluirProjetoEmAndamento() {
        Projeto projeto = novoProjeto(2L, ProjetoStatus.EM_ANDAMENTO, gerente);
        when(projetoRepository.findById(2L)).thenReturn(Optional.of(projeto));

        assertThrows(BusinessException.class, () -> projetoService.excluir(2L));

        verify(projetoRepository, never()).delete(any(Projeto.class));
    }

    @Test
    void deveExcluirProjetoPlanejado() {
        Projeto projeto = novoProjeto(3L, ProjetoStatus.PLANEJADO, gerente);
        when(projetoRepository.findById(3L)).thenReturn(Optional.of(projeto));

        projetoService.excluir(3L);

        verify(projetoRepository).delete(projeto);
    }

    @Test
    void deveAssociarMembrosRemovendoIdsDuplicados() {
        Projeto projeto = novoProjeto(10L, ProjetoStatus.PLANEJADO, gerente);
        Membro membro1 = novoMembro(1L, "Ana", "funcionario");
        Membro membro2 = novoMembro(2L, "Beto", "funcionario");

        when(projetoRepository.findById(10L)).thenReturn(Optional.of(projeto));
        when(membroService.buscarEntidade(1L)).thenReturn(membro1);
        when(membroService.buscarEntidade(2L)).thenReturn(membro2);
        when(projetoRepository.countProjetosAtivosDoMembroExcluindoProjeto(
            eq(1L),
            eq(10L),
            eq(EnumSet.of(ProjetoStatus.ENCERRADO, ProjetoStatus.CANCELADO))
        )).thenReturn(0L);
        when(projetoRepository.countProjetosAtivosDoMembroExcluindoProjeto(
            eq(2L),
            eq(10L),
            eq(EnumSet.of(ProjetoStatus.ENCERRADO, ProjetoStatus.CANCELADO))
        )).thenReturn(1L);
        when(projetoRepository.save(any(Projeto.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ProjetoResponse response = projetoService.associarMembros(10L, List.of(1L, 1L, 2L));

        assertEquals(Set.of(1L, 2L), response.membrosIds());
        verify(membroService, times(1)).buscarEntidade(1L);
        verify(membroService, times(1)).buscarEntidade(2L);
    }

    @Test
    void deveGerarRelatorioComMediaDuracaoESomatorios() {
        Membro membro1 = novoMembro(1L, "Ana", "funcionario");
        Membro membro2 = novoMembro(2L, "Beto", "funcionario");

        Projeto encerrado = novoProjeto(20L, ProjetoStatus.ENCERRADO, gerente);
        encerrado.setDataInicio(LocalDate.of(2026, 1, 1));
        encerrado.setDataRealTermino(LocalDate.of(2026, 1, 11));
        encerrado.setOrcamentoTotal(new BigDecimal("120000"));
        encerrado.setMembros(new LinkedHashSet<>(List.of(membro1, membro2)));

        Projeto emAnalise = novoProjeto(21L, ProjetoStatus.EM_ANALISE, gerente);
        emAnalise.setOrcamentoTotal(new BigDecimal("90000"));
        emAnalise.setMembros(new LinkedHashSet<>(List.of(membro2)));

        when(projetoRepository.findAll()).thenReturn(List.of(encerrado, emAnalise));

        RelatorioProjetosResponse response = projetoService.relatorio();

        StatusResumoResponse resumoEncerrado = response.resumoPorStatus().stream()
            .filter(item -> item.status() == ProjetoStatus.ENCERRADO)
            .findFirst()
            .orElseThrow();
        StatusResumoResponse resumoEmAnalise = response.resumoPorStatus().stream()
            .filter(item -> item.status() == ProjetoStatus.EM_ANALISE)
            .findFirst()
            .orElseThrow();

        assertEquals(ProjetoStatus.values().length, response.resumoPorStatus().size());
        assertEquals(1L, resumoEncerrado.quantidade());
        assertEquals(new BigDecimal("120000"), resumoEncerrado.totalOrcado());
        assertEquals(1L, resumoEmAnalise.quantidade());
        assertEquals(new BigDecimal("90000"), resumoEmAnalise.totalOrcado());
        assertEquals(new BigDecimal("10.0"), response.mediaDuracaoEncerradosDias());
        assertEquals(2L, response.totalMembrosUnicos());
    }

    @Test
    void deveRetornarMediaZeroQuandoNaoHouverEncerradosComDataReal() {
        Projeto iniciado = novoProjeto(30L, ProjetoStatus.INICIADO, gerente);
        iniciado.setMembros(new LinkedHashSet<>());
        when(projetoRepository.findAll()).thenReturn(List.of(iniciado));

        RelatorioProjetosResponse response = projetoService.relatorio();

        assertEquals(BigDecimal.ZERO, response.mediaDuracaoEncerradosDias());
        assertTrue(response.resumoPorStatus().stream().anyMatch(item -> item.status() == ProjetoStatus.INICIADO));
    }

    private static ProjetoRequest novoRequest(ProjetoStatus status, Long gerenteId) {
        return new ProjetoRequest(
            "Projeto Atlas",
            LocalDate.of(2026, 1, 1),
            LocalDate.of(2026, 3, 31),
            null,
            new BigDecimal("100000"),
            "Projeto para validar regras",
            status,
            gerenteId
        );
    }

    private static Projeto novoProjeto(Long id, ProjetoStatus status, Membro gerente) {
        Projeto projeto = new Projeto();
        projeto.setId(id);
        projeto.setNome("Projeto Atlas");
        projeto.setDataInicio(LocalDate.of(2026, 1, 1));
        projeto.setPrevisaoTermino(LocalDate.of(2026, 3, 31));
        projeto.setDataRealTermino(null);
        projeto.setOrcamentoTotal(new BigDecimal("100000"));
        projeto.setDescricao("Projeto para validar regras");
        projeto.setStatus(status);
        projeto.setGerente(gerente);
        projeto.setMembros(new LinkedHashSet<>());
        return projeto;
    }

    private static Membro novoMembro(Long id, String nome, String atribuicao) {
        Membro membro = new Membro();
        membro.setId(id);
        membro.setNome(nome);
        membro.setAtribuicao(atribuicao);
        return membro;
    }
}

