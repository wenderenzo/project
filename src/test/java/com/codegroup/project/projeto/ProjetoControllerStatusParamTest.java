package com.codegroup.project.projeto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.codegroup.project.config.StringToProjetoStatusConverter;
import com.codegroup.project.projeto.entity.ProjetoStatus;
import org.junit.jupiter.api.Test;

class ProjetoControllerStatusParamTest {

    private final StringToProjetoStatusConverter converter = new StringToProjetoStatusConverter();

    @Test
    void deveConverterStatusHumanoParaEnum() {
        assertEquals(ProjetoStatus.EM_ANALISE, converter.convert("em analise"));
        assertEquals(ProjetoStatus.EM_ANDAMENTO, converter.convert("em andamento"));
        assertEquals(ProjetoStatus.ANALISE_REALIZADA, converter.convert("análise realizada"));
    }

    @Test
    void deveRetornarNuloQuandoParametroStatusVierVazio() {
        assertNull(converter.convert(" "));
        assertNull(converter.convert(null));
    }

    @Test
    void deveFalharQuandoStatusForInvalido() {
        assertThrows(IllegalArgumentException.class, () -> converter.convert("status inexistente"));
    }
}
