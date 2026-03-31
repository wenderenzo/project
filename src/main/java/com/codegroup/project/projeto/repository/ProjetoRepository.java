package com.codegroup.project.projeto.repository;

import com.codegroup.project.projeto.entity.Projeto;
import com.codegroup.project.projeto.entity.ProjetoStatus;
import java.util.Collection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProjetoRepository extends JpaRepository<Projeto, Long>, JpaSpecificationExecutor<Projeto> {

    @Query("""
        select count(p)
        from Projeto p
        join p.membros m
        where m.id = :membroId
          and p.id <> :projetoId
          and p.status not in :statusFinais
    """)
    long countProjetosAtivosDoMembroExcluindoProjeto(
        @Param("membroId") Long membroId,
        @Param("projetoId") Long projetoId,
        @Param("statusFinais") Collection<ProjetoStatus> statusFinais
    );
}

