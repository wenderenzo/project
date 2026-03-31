package com.codegroup.project.projeto.dto;

import com.codegroup.project.projeto.entity.ProjetoStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public record ProjetoRequest(
    @NotBlank(message = "Nome do projeto e obrigatorio")
    String nome,
    @NotNull(message = "Data de inicio e obrigatoria")
    LocalDate dataInicio,
    @NotNull(message = "Previsao de termino e obrigatoria")
    LocalDate previsaoTermino,
    LocalDate dataRealTermino,
    @NotNull(message = "Orcamento total e obrigatorio")
    @DecimalMin(value = "0.01", message = "Orcamento deve ser maior que zero")
    BigDecimal orcamentoTotal,
    @NotBlank(message = "Descricao e obrigatoria")
    String descricao,
    @NotNull(message = "Status e obrigatorio")
    ProjetoStatus status,
    @NotNull(message = "Gerente e obrigatorio")
    Long gerenteId
) {
}

