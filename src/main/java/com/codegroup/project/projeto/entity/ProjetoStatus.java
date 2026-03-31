package com.codegroup.project.projeto.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.text.Normalizer;
import java.util.Arrays;

public enum ProjetoStatus {
    EM_ANALISE("em analise"),
    ANALISE_REALIZADA("analise realizada"),
    ANALISE_APROVADA("analise aprovada"),
    PLANEJADO("planejado"),
    INICIADO("iniciado"),
    EM_ANDAMENTO("em andamento"),
    ENCERRADO("encerrado"),
    CANCELADO("cancelado");

    private final String label;

    ProjetoStatus(String label) {
        this.label = label;
    }

    @JsonValue
    public String getLabel() {
        return label;
    }

    @JsonCreator
    public static ProjetoStatus fromValue(String value) {
        if (value == null) {
            return null;
        }
        String normalized = normalize(value);
        return Arrays.stream(values())
            .filter(status -> normalize(status.label).equals(normalized) || status.name().equalsIgnoreCase(normalized))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Status invalido: " + value));
    }

    private static String normalize(String value) {
        String withoutAccents = Normalizer.normalize(value, Normalizer.Form.NFD)
            .replaceAll("\\p{M}", "");
        return withoutAccents.trim().replace('_', ' ').toLowerCase();
    }
}

