package com.codegroup.project.projeto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.codegroup.project.projeto.entity.ProjetoStatus;
import org.junit.jupiter.api.Test;

class ProjetoStatusTest {

    @Test
    void deveLerStatusComAcento() {
        assertEquals(ProjetoStatus.EM_ANALISE, ProjetoStatus.fromValue("em analise"));
        assertEquals(ProjetoStatus.ANALISE_REALIZADA, ProjetoStatus.fromValue("análise realizada"));
        assertEquals(ProjetoStatus.EM_ANDAMENTO, ProjetoStatus.fromValue("EM_ANDAMENTO"));
    }
}

