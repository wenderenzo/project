package com.codegroup.project.projeto.dto;

import java.math.BigDecimal;
import java.util.List;

public record RelatorioProjetosResponse(
    List<StatusResumoResponse> resumoPorStatus,
    BigDecimal mediaDuracaoEncerradosDias,
    long totalMembrosUnicos
) {
}

