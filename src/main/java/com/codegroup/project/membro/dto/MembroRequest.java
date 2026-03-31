package com.codegroup.project.membro.dto;

import jakarta.validation.constraints.NotBlank;

public record MembroRequest(
    @NotBlank(message = "Nome do membro e obrigatorio")
    String nome,
    @NotBlank(message = "Atribuicao e obrigatoria")
    String atribuicao
) {
}

