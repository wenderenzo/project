package com.codegroup.project.projeto.dto;

import com.codegroup.project.projeto.entity.ProjetoStatus;
import java.math.BigDecimal;

public record StatusResumoResponse(
    ProjetoStatus status,
    long quantidade,
    BigDecimal totalOrcado
) {
}

