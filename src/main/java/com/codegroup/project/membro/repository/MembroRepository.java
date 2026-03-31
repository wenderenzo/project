package com.codegroup.project.membro.repository;

import com.codegroup.project.membro.entity.Membro;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MembroRepository extends JpaRepository<Membro, Long> {
}

