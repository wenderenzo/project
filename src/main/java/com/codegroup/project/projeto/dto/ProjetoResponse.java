package com.codegroup.project.projeto.dto;

import com.codegroup.project.projeto.entity.ProjetoStatus;
import com.codegroup.project.projeto.entity.RiscoProjeto;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

public record ProjetoResponse(
    Long id,
    String nome,
    LocalDate dataInicio,
    LocalDate previsaoTermino,
    LocalDate dataRealTermino,
    BigDecimal orcamentoTotal,
    String descricao,
    ProjetoStatus status,
    RiscoProjeto risco,
    Long gerenteId,
    Set<Long> membrosIds
) {
}

