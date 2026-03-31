package com.codegroup.project.projeto.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;

public record AssociarMembrosRequest(
    @NotEmpty(message = "Informe ao menos um membro")
    @Size(max = 10, message = "Um projeto pode ter no maximo 10 membros")
    List<Long> membroIds
) {
}

