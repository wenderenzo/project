package com.codegroup.project.projeto;

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
import com.codegroup.project.projeto.repository.ProjetoSpecifications;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProjetoService {

    private final ProjetoRepository projetoRepository;
    private final MembroService membroService;

    public ProjetoService(ProjetoRepository projetoRepository, MembroService membroService) {
        this.projetoRepository = projetoRepository;
        this.membroService = membroService;
    }

    @Transactional
    public ProjetoResponse criar(ProjetoRequest request) {
        Projeto projeto = new Projeto();
        preencherCampos(projeto, request, null);
        Projeto salvo = projetoRepository.save(projeto);
        return toResponse(salvo);
    }

    @Transactional(readOnly = true)
    public ProjetoResponse buscarPorId(Long id) {
        return toResponse(obterProjeto(id));
    }

    @Transactional(readOnly = true)
    public Page<ProjetoResponse> listar(String nome, ProjetoStatus status, Long gerenteId, Pageable pageable) {
        Specification<Projeto> specs = Specification.where(ProjetoSpecifications.comNome(nome))
            .and(ProjetoSpecifications.comStatus(status))
            .and(ProjetoSpecifications.comGerenteId(gerenteId));
        return projetoRepository.findAll(specs, pageable).map(this::toResponse);
    }

    @Transactional
    public ProjetoResponse atualizar(Long id, ProjetoRequest request) {
        Projeto projeto = obterProjeto(id);
        preencherCampos(projeto, request, projeto.getStatus());
        Projeto salvo = projetoRepository.save(projeto);
        return toResponse(salvo);
    }

    @Transactional
    public void excluir(Long id) {
        Projeto projeto = obterProjeto(id);
        ProjetoBusinessRules.validarExclusao(projeto.getStatus());
        projetoRepository.delete(projeto);
    }

    @Transactional
    public ProjetoResponse associarMembros(Long projetoId, List<Long> membrosIds) {
        Projeto projeto = obterProjeto(projetoId);
        List<Membro> membros = new ArrayList<>();
        for (Long membroId : new LinkedHashSet<>(membrosIds)) {
            membros.add(membroService.buscarEntidade(membroId));
        }

        ProjetoBusinessRules.validarAlocacaoMembros(
            projeto,
            membros,
            membroId -> projetoRepository.countProjetosAtivosDoMembroExcluindoProjeto(
                membroId,
                projeto.getId(),
                EnumSet.of(ProjetoStatus.ENCERRADO, ProjetoStatus.CANCELADO)
            )
        );

        projeto.setMembros(new LinkedHashSet<>(membros));
        return toResponse(projetoRepository.save(projeto));
    }

    @Transactional(readOnly = true)
    public RelatorioProjetosResponse relatorio() {
        List<Projeto> projetos = projetoRepository.findAll();
        List<StatusResumoResponse> resumo = new ArrayList<>();
        for (ProjetoStatus status : ProjetoStatus.values()) {
            long quantidade = projetos.stream().filter(p -> p.getStatus() == status).count();
            BigDecimal totalOrcado = projetos.stream()
                .filter(p -> p.getStatus() == status)
                .map(Projeto::getOrcamentoTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            resumo.add(new StatusResumoResponse(status, quantidade, totalOrcado));
        }

        List<Long> duracoes = projetos.stream()
            .filter(p -> p.getStatus() == ProjetoStatus.ENCERRADO)
            .filter(p -> p.getDataRealTermino() != null)
            .map(p -> ProjetoBusinessRules.calcularDuracaoDias(p.getDataInicio(), p.getDataRealTermino()))
            .toList();

        BigDecimal mediaDuracao = duracoes.isEmpty()
            ? BigDecimal.ZERO
            : BigDecimal.valueOf(duracoes.stream().mapToLong(Long::longValue).average().orElse(0));

        long membrosUnicos = projetos.stream()
            .flatMap(p -> p.getMembros().stream())
            .map(Membro::getId)
            .distinct()
            .count();

        return new RelatorioProjetosResponse(resumo, mediaDuracao, membrosUnicos);
    }

    private Projeto obterProjeto(Long id) {
        return projetoRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Projeto nao encontrado: " + id));
    }

    private void preencherCampos(Projeto projeto, ProjetoRequest request, ProjetoStatus statusAtual) {
        ProjetoBusinessRules.validarDatas(request.dataInicio(), request.previsaoTermino());
        ProjetoBusinessRules.validarTransicaoStatus(statusAtual, request.status());

        projeto.setNome(request.nome());
        projeto.setDataInicio(request.dataInicio());
        projeto.setPrevisaoTermino(request.previsaoTermino());
        projeto.setDataRealTermino(request.dataRealTermino());
        projeto.setOrcamentoTotal(request.orcamentoTotal());
        projeto.setDescricao(request.descricao());
        projeto.setStatus(request.status());
        projeto.setGerente(membroService.buscarEntidade(request.gerenteId()));
    }

    private ProjetoResponse toResponse(Projeto projeto) {
        Set<Long> membrosIds = projeto.getMembros().stream().map(Membro::getId).collect(java.util.stream.Collectors.toSet());
        RiscoProjeto risco = ProjetoBusinessRules.classificarRisco(
            projeto.getOrcamentoTotal(),
            projeto.getDataInicio(),
            projeto.getPrevisaoTermino()
        );
        return new ProjetoResponse(
            projeto.getId(),
            projeto.getNome(),
            projeto.getDataInicio(),
            projeto.getPrevisaoTermino(),
            projeto.getDataRealTermino(),
            projeto.getOrcamentoTotal(),
            projeto.getDescricao(),
            projeto.getStatus(),
            risco,
            projeto.getGerente().getId(),
            membrosIds
        );
    }
}


