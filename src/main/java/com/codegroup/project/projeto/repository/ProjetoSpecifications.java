package com.codegroup.project.projeto.repository;

import com.codegroup.project.projeto.entity.Projeto;
import com.codegroup.project.projeto.entity.ProjetoStatus;
import org.springframework.data.jpa.domain.Specification;

public final class ProjetoSpecifications {

    private ProjetoSpecifications() {
    }

    public static Specification<Projeto> comStatus(ProjetoStatus status) {
        return (root, query, cb) -> status == null ? cb.conjunction() : cb.equal(root.get("status"), status);
    }

    public static Specification<Projeto> comNome(String nome) {
        return (root, query, cb) -> {
            if (nome == null || nome.isBlank()) {
                return cb.conjunction();
            }
            return cb.like(cb.lower(root.get("nome")), "%" + nome.toLowerCase() + "%");
        };
    }

    public static Specification<Projeto> comGerenteId(Long gerenteId) {
        return (root, query, cb) -> gerenteId == null
            ? cb.conjunction()
            : cb.equal(root.get("gerente").get("id"), gerenteId);
    }
}

